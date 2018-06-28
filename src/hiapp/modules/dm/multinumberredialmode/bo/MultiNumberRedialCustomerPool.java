package hiapp.modules.dm.multinumberredialmode.bo;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.bo.PhoneTypeDialSequence;
import hiapp.modules.dm.redismanager.JedisUtils;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import java.util.*;

@Component
public class MultiNumberRedialCustomerPool {

    @Autowired
    PhoneTypeDialSequence phoneTypeDialSequence;

    @Autowired
    MultiNumberRedialCustomerWaitPool customerWaitPool;

    @Autowired
    private DMBizMangeShare dmBizMangeShare;

    @Autowired
    private JedisUtils jedisUtils;

    private Jedis redisMultiNumberRedial;

    public void initialize() {
        multiNumberRedialCustomerSharePool = new HashMap<Integer, Map<Integer, OnePhoneTypeCustomerPool>>();
        //customerWaitPool = new CustomerWaitPool();

        /*
        for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.size(); dialIndex++) {
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = new OnePhoneTypeCustomerPool(bizId, dialIndex);
            multiNumberRedialCustomerSharePool.put(dialIndex, onePhoneTypeCustomerPool);

            //bizCustomerSharePool;
        }*/

    }

    // bizId <==> {号码类型 <==> 号码类型对应的客户池}
    Map<Integer, Map<Integer, OnePhoneTypeCustomerPool>> multiNumberRedialCustomerSharePool;

    //已改
    public MultiNumberRedialCustomer extractCustomer(String userId, int bizId) {
        MultiNumberRedialCustomer customer;

        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);

        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.getPhoneTypeNum(bizId); dialIndex++) {
                int phoneType = phoneTypeDialSequence.getPhoneTypeByPhoneDialSequence(bizId, dialIndex);

                OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = GenericitySerializeUtil.unserialize
                        (redisMultiNumberRedial.hget(GenericitySerializeUtil.serialize("multiNumberRedialCustomerSharePool" + bizId),
                                GenericitySerializeUtil.serialize(phoneType)));

                if (null == onePhoneTypeCustomerPool)
                    continue;

                customer = onePhoneTypeCustomerPool.extractCustomer(userId, shareBatchIdList);
                if (null != customer) {
                    // 放入 客户等待池
                    customerWaitPool.add(userId, customer);
                    return customer;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }

        return null;
    }

    /**
     * @param customer
     * @param theDayDialStrategy phoneType <==> dialNum
     * @return
     */
    //已改
    public Boolean add(MultiNumberRedialCustomer customer, Map<Integer, Integer> theDayDialStrategy) {
        Integer nextDialPhoneType = initNextDialPhoneType(customer, theDayDialStrategy);
        if (null == nextDialPhoneType)
            return false;
        byte[] mapSerialize = GenericitySerializeUtil.serialize(
                "multiNumberRedialCustomerSharePool" + customer.getBizId());
        byte[] fieldSerialize = GenericitySerializeUtil.serialize(nextDialPhoneType);
        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = GenericitySerializeUtil.unserialize
                    (redisMultiNumberRedial.hget(mapSerialize, fieldSerialize));
            if (null == onePhoneTypeCustomerPool) {
                onePhoneTypeCustomerPool = new OnePhoneTypeCustomerPool(customer.getBizId(), nextDialPhoneType);
            }

            onePhoneTypeCustomerPool.add(customer);
            redisMultiNumberRedial.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(onePhoneTypeCustomerPool));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }

        return true;
    }

    //已改
    public void clear() {
        Set<byte[]> keys = null;
        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            keys = redisMultiNumberRedial.keys("*multiNumberRedialCustomerSharePool*".getBytes());
            for (byte[] key : keys) {
                String byteString = null;
                try {
                    byteString = GenericitySerializeUtil.unserialize(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //字符串不包含
                if (byteString != null && byteString.contains("multiNumberRedialCustomerSharePool")) {
                    redisMultiNumberRedial.del(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }
        keys.clear();
    }

    //,,,
    public MultiNumberRedialCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId, int phoneType) {
        return customerWaitPool.removeWaitCustomer(userId, bizId, importBatchId, customerId);
    }

    //,,,
    public MultiNumberRedialCustomer getWaitCustomer(String userId, int bizId, String importBatchId, String customerId, int phoneType) {
        return customerWaitPool.getWaitCustome(userId, bizId, importBatchId, customerId);
    }


    //,,,
    public void markShareBatchStopFromCustomerWaitPool(int bizId, List<String> shareBatchIds) {
        customerWaitPool.markShareBatchStopFromCustomerWaitPool(bizId, shareBatchIds);
    }

    //已改
    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        Map<Integer, OnePhoneTypeCustomerPool> oneBizCustomerSharePool = multiNumberRedialCustomerSharePool.get(bizId);
        if (null == oneBizCustomerSharePool)
            return;

        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            // 号码类型遍历
            for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.getPhoneTypeNum(bizId); dialIndex++) {
                int phoneType = phoneTypeDialSequence.getPhoneTypeByPhoneDialSequence(bizId, dialIndex);
                byte[] mapSerialize = GenericitySerializeUtil.serialize("multiNumberRedialCustomerSharePool" + bizId);
                byte[] fieldSerialize = GenericitySerializeUtil.serialize(phoneType);
                OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = GenericitySerializeUtil.unserialize(redisMultiNumberRedial.
                        hget(mapSerialize,
                                fieldSerialize));

                if (null == onePhoneTypeCustomerPool)
                    continue;

                onePhoneTypeCustomerPool.stopShareBatch(shareBatchIds);
                redisMultiNumberRedial.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(onePhoneTypeCustomerPool));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }
    }

    //,,,
    public void timeoutProc() {
        customerWaitPool.timeoutProc();
    }

    // 用户登录通知
    //,,,
    public void onLogin(String userId) {
        customerWaitPool.onLogin(userId);
    }


    //,,,
    public Integer calcNextDialPhoneType(MultiNumberRedialCustomer customer) {
        Integer curPhoneType = customer.getCurDialPhoneType();
        if (null == curPhoneType || 0 == curPhoneType)
            return null;

        for (int dialSeq = 1; dialSeq <= 10; dialSeq++) {
            Integer nextPhoneType = phoneTypeDialSequence.getNextDialPhoneType(customer.getBizId(), curPhoneType);
            if (null == nextPhoneType)
                return null;

            PhoneDialInfo nextPhoneDialInfo = customer.getDialInfoByPhoneType(nextPhoneType);
            if (null != nextPhoneDialInfo.getPhoneNumber() && !nextPhoneDialInfo.getPhoneNumber().isEmpty()) {
                return nextPhoneType;
            }

            curPhoneType = nextPhoneType;
        }

        return null;
    }

    /**
     * @param customer
     * @param theDayDialStrategy phoneType <==> dialNum
     * @return
     */
    //,,,
    public Integer initNextDialPhoneType(MultiNumberRedialCustomer customer, Map<Integer, Integer> theDayDialStrategy) {
        Integer nextPhoneType = customer.getNextDialPhoneType();
        if (null != nextPhoneType && 0 != nextPhoneType)
            return nextPhoneType;

        for (int dialSeq = 1; dialSeq <= phoneTypeDialSequence.getPhoneTypeNum(customer.getBizId()); dialSeq++) {
            nextPhoneType = phoneTypeDialSequence.getPhoneTypeByPhoneDialSequence(customer.getBizId(), dialSeq);

            String phoneTypeNumber = customer.getDialInfoByPhoneType(nextPhoneType).getPhoneNumber();
            Integer phoneTypeDialNum = theDayDialStrategy.get(nextPhoneType);
            if (null != phoneTypeNumber && !phoneTypeNumber.isEmpty() && null != phoneTypeDialNum && phoneTypeDialNum != 0)
                break;

            nextPhoneType = null;  // NOTE: 需要清除
        }

        customer.setNextDialPhoneType(nextPhoneType);
        return nextPhoneType;
    }

    //已改
    public List<MultiNumberRedialCustomer> cancelShare(int bizId, List<CustomerBasic> customerBasicList) {
        byte[] mapSerialize = GenericitySerializeUtil.serialize("multiNumberRedialCustomerSharePool" + bizId);
        List<MultiNumberRedialCustomer> customerList = null;
        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            Map<byte[], byte[]> oneBizCustomerSharePool = redisMultiNumberRedial.hgetAll(mapSerialize);
            if (oneBizCustomerSharePool.isEmpty())
                return null;

            customerList = new ArrayList<MultiNumberRedialCustomer>();

            // 号码类型遍历
            for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.getPhoneTypeNum(bizId); dialIndex++) {
                int phoneType = phoneTypeDialSequence.getPhoneTypeByPhoneDialSequence(bizId, dialIndex);
                OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = GenericitySerializeUtil.unserialize(
                        redisMultiNumberRedial.hget(mapSerialize, GenericitySerializeUtil.serialize(phoneType))
                );
                if (null == onePhoneTypeCustomerPool)
                    continue;

                customerList.addAll(onePhoneTypeCustomerPool.cancelShare(customerBasicList));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }
        return customerList;
    }

}
