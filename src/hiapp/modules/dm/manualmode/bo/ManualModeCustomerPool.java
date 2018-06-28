package hiapp.modules.dm.manualmode.bo;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import hiapp.modules.dm.manualmode.timeoutpro.ManualModeTimeoutPro;
import hiapp.modules.dm.redismanager.JedisUtils;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import hiapp.modules.dm.util.RedisComparator;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.io.UnsupportedEncodingException;
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
    private JedisUtils jedisUtils;
    // BizId <==> {shareBatchId <==> PriorityBlockingQueue<ManualModeCustomer>}

    // BizId + IID + CID <==> ManualModeCustomer
    Map<String, ManualModeCustomer> mapWaitCustomerCancellation;

    private Jedis mapCustomerSharePoolRedis;


    public void initialize() {
        mapWaitCustomerCancellation = new HashMap<String, ManualModeCustomer>();
    }

    public void clear() {
        //先删除redis中的共享数据
        Set<byte[]> keys = null;
        try {
            mapCustomerSharePoolRedis = jedisUtils.getJedis();
            keys = mapCustomerSharePoolRedis.keys("*manualMode*".getBytes());
            for (byte[] key : keys) {
                String byteString = null;
                try {
                    //byteString = new String(key, "UTF-8");
                    byteString = GenericitySerializeUtil.unserialize(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //字符串不包含manualMode直接結束
                if (byteString != null && byteString.contains("manualMode")) {
                    mapCustomerSharePoolRedis.del(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(mapCustomerSharePoolRedis);
        }
        keys.clear();
        mapWaitCustomerCancellation.clear();

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
        try {
            mapCustomerSharePoolRedis = jedisUtils.getJedis();
            PriorityBlockingQueue<ManualModeCustomer> oneShareBatchCustomerSharePool = GenericitySerializeUtil.unserialize(mapCustomerSharePoolRedis.hget(mapSerialize, fieldSerialize));
            if (null == oneShareBatchCustomerSharePool || oneShareBatchCustomerSharePool.isEmpty()) {
                oneShareBatchCustomerSharePool = new PriorityBlockingQueue<ManualModeCustomer>(1, shareBatchBeginTimeComparator);
            }
            oneShareBatchCustomerSharePool.put(customer);
            mapCustomerSharePoolRedis.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(oneShareBatchCustomerSharePool));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(mapCustomerSharePoolRedis);
            System.out.println("reids-----------关闭");
        }
        mapWaitCustomerCancellation.put(customer.getCustomerToken(), customer);

    }

    //已改,,
    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        byte[] mapSerialize = GenericitySerializeUtil.serialize("manualMode" + bizId);
        try {
            mapCustomerSharePoolRedis = jedisUtils.getJedis();
            if (mapCustomerSharePoolRedis.hgetAll(mapSerialize).isEmpty()) {
                return;
            }
            for (String shareBatchId : shareBatchIds) {
                mapCustomerSharePoolRedis.hdel(mapSerialize, GenericitySerializeUtil.serialize(shareBatchId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(mapCustomerSharePoolRedis);
        }
    }

    //已改,,,
    public ManualModeCustomer extractCustomer(String userId, int bizId) {

        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);
        byte[] mapSerialize = GenericitySerializeUtil.serialize("manualMode" + bizId);
        try {
            mapCustomerSharePoolRedis = jedisUtils.getJedis();
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(mapCustomerSharePoolRedis);
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
    public void deleteRedisCustomer(int bizId, String shareBatchId) {
        // 根据userID，获取有权限访问的shareBatchIds
        byte[] mapSerialize = GenericitySerializeUtil.serialize("manualMode" + bizId);
        byte[] fieldSerialize = GenericitySerializeUtil.serialize(shareBatchId);
        try {
            mapCustomerSharePoolRedis = jedisUtils.getJedis();
            PriorityBlockingQueue<ManualModeCustomer> oneShareBatchCustomerPoolRedis = GenericitySerializeUtil.unserialize(mapCustomerSharePoolRedis.hget(mapSerialize, fieldSerialize));

            //移除
            Iterator<ManualModeCustomer> iterator = oneShareBatchCustomerPoolRedis.iterator();
            while (iterator.hasNext()) {
                ManualModeCustomer customerRedis = iterator.next();
                //根据dataPoolCur判断是共享的还是分配的
                if (customerRedis.getDataPoolIdCur() != null) {
                    if (customerRedis.isExtracted) {
                        iterator.remove();
                        mapCustomerSharePoolRedis.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(oneShareBatchCustomerPoolRedis));
                    }
                } else {
                    if (customerRedis.isExtracted) {
                        iterator.remove();
                        mapCustomerSharePoolRedis.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(oneShareBatchCustomerPoolRedis));
                        break;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(mapCustomerSharePoolRedis);
        }
    }
}
