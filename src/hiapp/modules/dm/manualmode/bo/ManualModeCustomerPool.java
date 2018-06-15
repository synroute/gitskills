package hiapp.modules.dm.manualmode.bo;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import hiapp.modules.dm.manualmode.timeoutpro.ManualModeTimeoutPro;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import hiapp.modules.dm.util.RedisComparator;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class ManualModeCustomerPool {

    @Autowired
    DMDAO dmDAO;

    @Autowired
    ManualModeDAO manualModeDAO;

    @Autowired
    private DMBizMangeShare dmBizMangeShare;

    @Autowired
    private JedisSentinelPool jedisPool;

    @Autowired
    private ManualModeTimeoutPro manualModeTimeoutPro;
    // BizId <==> {shareBatchId <==> PriorityBlockingQueue<ManualModeCustomer>}

    // BizId + IID + CID <==> ManualModeCustomer
    Map<String, ManualModeCustomer> mapWaitCustomerCancellation;

    Jedis mapCustomerSharePoolRedis;
    //set集合用于存储mapCustomerSharePool所有的的key,便于清除操作
    Set<byte[]> mapCustomerSharePoolKeys;

    public void initialize() {
        mapCustomerSharePoolRedis = jedisPool.getResource();
        mapCustomerSharePoolKeys = new HashSet<>();

        mapWaitCustomerCancellation = new HashMap<String, ManualModeCustomer>();
    }

    public void clear() {
        //先删除redis中的共享数据
        for (byte[] mapCustomerSharePoolKey : mapCustomerSharePoolKeys) {
            mapCustomerSharePoolRedis.del(mapCustomerSharePoolKey);
        }
        //再清空set集合中的数据
        mapCustomerSharePoolKeys.clear();

        mapWaitCustomerCancellation.clear();
        //redis释放连接
        if (mapCustomerSharePoolRedis != null) {
            mapCustomerSharePoolRedis.close();
        }
    }

    //,,
    public void addCustomer(ManualModeCustomer customer) {
        //给用户添加初始化时间，用于超时判断
        customer.setInitTimeout(new Date());
        System.out.println("M1 add customer: bizId[" + customer.getBizId()
                + "] shareId[" + customer.getSourceId() + "] IID[" + customer.getImportBatchId()
                + "] CID[" + customer.getCustomerId() + "] ");

        byte[] mapSerialize = GenericitySerializeUtil.serialize("manualMode" + customer.getBizId());
        byte[] fieldSerialize = GenericitySerializeUtil.serialize(customer.getSourceId());
        PriorityBlockingQueue<ManualModeCustomer> oneShareBatchCustomerSharePool = GenericitySerializeUtil.unserialize(mapCustomerSharePoolRedis.hget(mapSerialize, fieldSerialize));
        if (null == oneShareBatchCustomerSharePool || oneShareBatchCustomerSharePool.isEmpty()) {
            oneShareBatchCustomerSharePool = new PriorityBlockingQueue<ManualModeCustomer>(1, shareBatchBeginTimeComparator);
        }
        oneShareBatchCustomerSharePool.put(customer);
        mapCustomerSharePoolRedis.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(oneShareBatchCustomerSharePool));
        mapWaitCustomerCancellation.put(customer.getCustomerToken(), customer);
        //保存key值到set集合
        mapCustomerSharePoolKeys.add(mapSerialize);
        //更新手动分配的配置文件
        //manualModeTimeoutPro.updateTimeOutConfig(customer.getBizId());

    }

    //已改,,
    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        byte[] mapSerialize = GenericitySerializeUtil.serialize("manualMode" + bizId);
        if (mapCustomerSharePoolRedis.hgetAll(mapSerialize).isEmpty()) {
            return;
        }
        for (String shareBatchId : shareBatchIds) {
            mapCustomerSharePoolRedis.hdel(mapSerialize, GenericitySerializeUtil.serialize(shareBatchId));
        }
    }

    //已改,,,
    public ManualModeCustomer extractCustomer(String userId, int bizId) {

        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);
        byte[] mapSerialize = GenericitySerializeUtil.serialize("manualMode" + bizId);
        for (String shareBatchId : shareBatchIdList) {
            byte[] fieldSerialize = GenericitySerializeUtil.serialize(shareBatchId);
            PriorityBlockingQueue<ManualModeCustomer> oneShareBatchCustomerPoolRedis = GenericitySerializeUtil.unserialize(mapCustomerSharePoolRedis.hget(mapSerialize, fieldSerialize));
            if (null == oneShareBatchCustomerPoolRedis || oneShareBatchCustomerPoolRedis.isEmpty())
                continue;
            //获取但不能移除
            Iterator<ManualModeCustomer> iterator = oneShareBatchCustomerPoolRedis.iterator();
            while (iterator.hasNext()) {
                ManualModeCustomer customerRedis = iterator.next();
                if (!customerRedis.isExtracted) {
                    customerRedis.setExtracted(true);
                    mapCustomerSharePoolRedis.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(oneShareBatchCustomerPoolRedis));
                    if (null == customerRedis) {
                        mapCustomerSharePoolRedis.hdel(mapSerialize, fieldSerialize);
                        continue;
                    }

                    if (customerRedis.getInvalid())
                        continue;

                    mapWaitCustomerCancellation.remove(customerRedis.getCustomerToken());
                    return customerRedis;
                }
            }
        }
        return null;
    }

    public List<ManualModeCustomer> cancelShare(int bizId, List<CustomerBasic> customerBasicList) {
        List<ManualModeCustomer> customerList = new ArrayList<ManualModeCustomer>();
        for (CustomerBasic customerBasic : customerBasicList) {
            ManualModeCustomer customer = mapWaitCustomerCancellation.remove(customerBasic.getCustomerToken());
            if (null == customer)
                continue;

            customer.setInvalid(true);

            customerList.add(customer);
        }

        return customerList;
    }

    //匿名Comparator实现
    private static RedisComparator shareBatchBeginTimeComparator = new RedisComparator<ManualModeCustomer>() {
        @Override
        public int compare(ManualModeCustomer c1, ManualModeCustomer c2) {
            if (c1 != null && c2 != null && c1.getShareBatchStartTime() != null && c2.getShareBatchStartTime() != null)
                return (c1.getShareBatchStartTime().before(c2.getShareBatchStartTime())) ? 1 : -1;
            return 0;
        }
    };

    //被抽取后删除redis中的方法
    public void deleteRedisCustomer(int bizId, String userId) {
        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);
        byte[] mapSerialize = GenericitySerializeUtil.serialize("manualMode" + bizId);
        for (String shareBatchId : shareBatchIdList) {
            byte[] fieldSerialize = GenericitySerializeUtil.serialize(shareBatchId);
            PriorityBlockingQueue<ManualModeCustomer> oneShareBatchCustomerPoolRedis = GenericitySerializeUtil.unserialize(mapCustomerSharePoolRedis.hget(mapSerialize, fieldSerialize));
            if (null == oneShareBatchCustomerPoolRedis || oneShareBatchCustomerPoolRedis.isEmpty())
                continue;
            //移除
            Iterator<ManualModeCustomer> iterator = oneShareBatchCustomerPoolRedis.iterator();
            while (iterator.hasNext()) {
                ManualModeCustomer customerRedis = iterator.next();
                if (customerRedis.isExtracted) {
                    iterator.remove();
                    mapCustomerSharePoolRedis.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(oneShareBatchCustomerPoolRedis));

                    if (null == customerRedis) {
                        mapCustomerSharePoolRedis.hdel(mapSerialize, fieldSerialize);
                        continue;
                    }

                    if (customerRedis.getInvalid())
                        continue;
                    if (mapWaitCustomerCancellation.get(customerRedis.getCustomerToken()) != null) {
                        mapWaitCustomerCancellation.remove(customerRedis.getCustomerToken());
                    }
                }
            }
        }
    }
}
