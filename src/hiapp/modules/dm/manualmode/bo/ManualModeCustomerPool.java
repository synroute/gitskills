package hiapp.modules.dm.manualmode.bo;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import hiapp.modules.dm.util.RedisComparator;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
    private JedisPool jedisPool;
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
        if (mapCustomerSharePoolRedis != null){
            mapCustomerSharePoolRedis.close();
        }
    }
    //,,
    public void addCustomer(ManualModeCustomer customer) {

        System.out.println("M1 add customer: bizId[" + customer.getBizId()
                + "] shareId[" + customer.getSourceId() + "] IID[" + customer.getImportBatchId()
                + "] CID[" + customer.getCustomerId() + "] ");

        Map<byte[], byte[]> oneBizCustomerSharePoolRedis = mapCustomerSharePoolRedis.hgetAll(GenericitySerializeUtil.serialize(customer.getBizId()));
        if (0 != oneBizCustomerSharePoolRedis.size()) {
            //保存key值到set集合
            mapCustomerSharePoolKeys.add(GenericitySerializeUtil.serialize(customer.getBizId()));
            //保存到redis中
            //mapCustomerSharePoolRedis.hmset(SerializeUtil.serialize(customer.getBizId()),oneBizCustomerSharePoolRedis);
        }

        PriorityBlockingQueue<ManualModeCustomer> oneShareBatchCustomerSharePool = GenericitySerializeUtil.unserialize(oneBizCustomerSharePoolRedis.get(customer.getSourceId()));
        if (null == oneShareBatchCustomerSharePool || oneShareBatchCustomerSharePool.isEmpty()) {
            oneShareBatchCustomerSharePool = new PriorityBlockingQueue<ManualModeCustomer>(1, shareBatchBeginTimeComparator);
        }
        oneShareBatchCustomerSharePool.put(customer);
        oneBizCustomerSharePoolRedis.put(GenericitySerializeUtil.serialize(customer.getSourceId()), GenericitySerializeUtil.serialize(oneShareBatchCustomerSharePool));
        mapCustomerSharePoolRedis.hmset(GenericitySerializeUtil.serialize(customer.getBizId()), oneBizCustomerSharePoolRedis);
        mapWaitCustomerCancellation.put(customer.getCustomerToken(), customer);

    }
    //已改,,
    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        if (mapCustomerSharePoolRedis.hgetAll(GenericitySerializeUtil.serialize(bizId)).isEmpty()){
            return;
        }
        for (String shareBatchId : shareBatchIds) {
            mapCustomerSharePoolRedis.hdel(GenericitySerializeUtil.serialize(bizId), GenericitySerializeUtil.serialize(shareBatchId));
        }
    }
    //已改,,,
    public ManualModeCustomer extractCustomer(String userId, int bizId) {

        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);
        byte[] mapSerialize = GenericitySerializeUtil.serialize(bizId);
        Map<byte[], byte[]> oneBizCustomerSharePoolRedis = mapCustomerSharePoolRedis.hgetAll(mapSerialize);
        if (0 == oneBizCustomerSharePoolRedis.size())
            return null;

        for (String shareBatchId : shareBatchIdList) {
            byte[] fieldSerialize = GenericitySerializeUtil.serialize(shareBatchId);
            PriorityBlockingQueue<ManualModeCustomer> oneShareBatchCustomerPoolRedis = GenericitySerializeUtil.unserialize(oneBizCustomerSharePoolRedis.get(fieldSerialize));
            if (null == oneShareBatchCustomerPoolRedis || oneShareBatchCustomerPoolRedis.isEmpty())
                continue;

            ManualModeCustomer customerRedis = oneShareBatchCustomerPoolRedis.poll();
            mapCustomerSharePoolRedis.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(oneShareBatchCustomerPoolRedis));
            if (null == customerRedis) {
                oneBizCustomerSharePoolRedis.remove(fieldSerialize);
                mapCustomerSharePoolRedis.hdel(mapSerialize, fieldSerialize);
                continue;
            }

            if (customerRedis.getInvalid())
                continue;

            mapWaitCustomerCancellation.remove( customerRedis.getCustomerToken() );
            if (!oneShareBatchCustomerPoolRedis.isEmpty()){
                oneBizCustomerSharePoolRedis.put(GenericitySerializeUtil.serialize(shareBatchId), GenericitySerializeUtil.serialize(oneShareBatchCustomerPoolRedis));
                mapCustomerSharePoolRedis.hmset(GenericitySerializeUtil.serialize(bizId), oneBizCustomerSharePoolRedis);
            }
            return customerRedis;

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
}
