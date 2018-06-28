package hiapp.modules.dm.multinumberredialmode.bo;

import hiapp.modules.dm.Constants;
import hiapp.modules.dm.multinumberredialmode.MultiNumberRedialDataManage;
import hiapp.modules.dm.redismanager.JedisUtils;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class MultiNumberRedialCustomerWaitPool {

    @Autowired
    MultiNumberRedialDataManage multiNumberOutboundDataManage;

    @Autowired
    private JedisUtils jedisUtils;

    private Jedis redisMultiNumberRedial;
    // 等待拨打结果的客户池，坐席人员维度
    // UserID <==> {BizID + ImportID + CustomerID <==> MultiNumberRedialCustomer}
   /* Map<String, Map<String, MultiNumberRedialCustomer>> redialMultiNumberMapOutboundResultWaitSubmitCustomerPool;

    // 等待共享停止的客户池，共享批次维度，用于标注已经停止共享的客户
    // ShareBatchId <==> {BizId + ImportId + CustomerId <==> MultiNumberRedialCustomer}
    Map<String, Map<String, MultiNumberRedialCustomer>> redialMultiNumberMapShareBatchWaitStopCustomerPool;

    // 等待坐席拨打结果超时的客户池，坐席弹屏时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> MultiNumberRedialCustomer}
    Map<Long, Map<String, MultiNumberRedialCustomer>> redialMultiNumberMapTimeOutWaitOutboundResultCustomerPool;*/

    Long earliestPhoneConnectTimeSlot;
    Long earliestScreenPopUpTimeSlot;
    Long earliestResultTimeSlot;

    //已改
    public MultiNumberRedialCustomerWaitPool() {
        Date now = new Date();
        earliestPhoneConnectTimeSlot = now.getTime() / Constants.timeSlotSpan;
        earliestScreenPopUpTimeSlot = now.getTime() / Constants.timeSlotSpan;
        earliestResultTimeSlot = now.getTime() / Constants.timeSlotSpan;
    }

    //已改
    public void add(String userId, MultiNumberRedialCustomer customerItem) {
        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            //直接存进去
            redisMultiNumberRedial.hset(GenericitySerializeUtil.serialize("redialMultiNumberMapOutboundResultWaitSubmitCustomerPool" + userId),
                    GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                    GenericitySerializeUtil.serialize(customerItem));

            //直接存进去
            redisMultiNumberRedial.hset(GenericitySerializeUtil.serialize("redialMultiNumberMapShareBatchWaitStopCustomerPool" + customerItem.getShareBatchId()),
                    GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                    GenericitySerializeUtil.serialize(customerItem));

            Long timeSlot = customerItem.getExtractTime().getTime() / Constants.timeSlotSpan;

            //直接存进去
            redisMultiNumberRedial.hset(GenericitySerializeUtil.serialize("redialMultiNumberMapTimeOutWaitOutboundResultCustomerPool"
                            + timeSlot),
                    GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                    GenericitySerializeUtil.serialize(customerItem));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }
    }

    //已改
    public MultiNumberRedialCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {

        MultiNumberRedialCustomer customerItem = removeWaitResultCustome(userId, bizId, importBatchId, customerId);
        if (null == customerItem)
            return customerItem;


        removeWaitStopCustomer(bizId, customerItem.getShareBatchId(), importBatchId, customerId);

        Long timeSlot = customerItem.getExtractTime().getTime() / Constants.timeSlotSpan;
        removeWaitResultTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);

        return customerItem;
    }

    //已改
    public MultiNumberRedialCustomer getWaitCustome(String userId, int bizId, String importBatchId, String customerId) {
        MultiNumberRedialCustomer customerItem = null;
        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            customerItem = GenericitySerializeUtil.unserialize(redisMultiNumberRedial.hget(GenericitySerializeUtil.serialize(
                    "redialMultiNumberMapOutboundResultWaitSubmitCustomerPool" + userId),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }
        return customerItem;
    }

    /**
     * 仅标注已经停止共享，不从等待池中移除。需要等待已拨打的结果。
     *
     * @param shareBatchIds
     */
    //已改
    public void markShareBatchStopFromCustomerWaitPool(int bizId, List<String> shareBatchIds) {
        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            for (String shareBatchId : shareBatchIds) {
                byte[] mapSerialize = GenericitySerializeUtil.serialize("redialMultiNumberMapShareBatchWaitStopCustomerPool" + shareBatchId);
                Map<byte[], byte[]> mapWaitStopPool = redisMultiNumberRedial.hgetAll(mapSerialize);
                if (mapWaitStopPool.isEmpty())
                    continue;
                Set<Map.Entry<byte[], byte[]>> entries = mapWaitStopPool.entrySet();
                for (Map.Entry<byte[], byte[]> entry : entries) {
                    MultiNumberRedialCustomer item = GenericitySerializeUtil.unserialize(entry.getValue());
                    item.setInvalid(true);
                    redisMultiNumberRedial.hset(mapSerialize, entry.getKey(), GenericitySerializeUtil.serialize(item));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }
    }

    //待写
    public void onLogin(String userId) {
        // TODO 多号码重拨外呼需要处理用户登录通知

        /*Map<String, SingleNumberModeShareCustomerItem> mapUserWaitResultPool = mapWaitResultCustomerPool.remove(userId);
        if (null == mapUserWaitResultPool)
            return;

        for (SingleNumberModeShareCustomerItem customerItem : mapUserWaitResultPool.values()) {
            // 放回客户共享池
            if (!customerItem.getInvalid()) {
                addCustomerToSharePool(customerItem);
            }

            Long timeSlot = customerItem.getExtractTime().getTime()/Constants.timeSlotSpan;
            removeWaitTimeOutCustomer(customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId(), timeSlot);

            removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                    customerItem.getCustomerId());
        }*/

    }

    //已改
    public void timeoutProc() {
        Date now = new Date();
        Long curTimeSlot = now.getTime() / Constants.timeSlotSpan;

        // 坐席递交结果 超时处理
        Long resultTimeoutTimeSlot = curTimeSlot - Constants.ResultTimeoutThreshold4 / Constants.timeSlotSpan;
        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            while (earliestResultTimeSlot < resultTimeoutTimeSlot) {
                byte[] mapSerialize = GenericitySerializeUtil.serialize("redialMultiNumberMapTimeOutWaitOutboundResultCustomerPool" + earliestResultTimeSlot++);
                Map<byte[], byte[]> mapTimeSlotWaitTimeOutPool = redisMultiNumberRedial.hgetAll(mapSerialize);
                redisMultiNumberRedial.del(mapSerialize);
                if (mapTimeSlotWaitTimeOutPool.isEmpty())
                    continue;
                Set<Map.Entry<byte[], byte[]>> entries = mapTimeSlotWaitTimeOutPool.entrySet();
                for (Map.Entry<byte[], byte[]> entry : entries) {
                    MultiNumberRedialCustomer customerItem = GenericitySerializeUtil.unserialize(entry.getValue());
                    // 放回客户共享池
                    if (!customerItem.getInvalid()) {
                        multiNumberOutboundDataManage.lostProc(customerItem);  // 呼损处理
                    }

                    removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                    removeWaitStopCustomer(customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                            customerItem.getCustomerId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }
    }

    //已改
    private MultiNumberRedialCustomer removeWaitResultCustome(String userId, int bizId, String importBatchId, String customerId) {
        MultiNumberRedialCustomer customerItem = null;
        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            customerItem = GenericitySerializeUtil.unserialize(redisMultiNumberRedial.hget(GenericitySerializeUtil.serialize("redialMultiNumberMapOutboundResultWaitSubmitCustomerPool" + userId),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId)));
            redisMultiNumberRedial.hdel(GenericitySerializeUtil.serialize("redialMultiNumberMapOutboundResultWaitSubmitCustomerPool" + userId),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }
        return customerItem;
    }

    //已改
    private void removeWaitStopCustomer(int bizId, String shareBatchId, String importBatchId, String customerId) {
        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            redisMultiNumberRedial.hdel(GenericitySerializeUtil.serialize("redialMultiNumberMapShareBatchWaitStopCustomerPool" + shareBatchId),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }
    }

    //已改
    private void removeWaitResultTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        try {
            redisMultiNumberRedial = jedisUtils.getJedis();
            redisMultiNumberRedial.hdel(GenericitySerializeUtil.serialize("redialMultiNumberMapTimeOutWaitOutboundResultCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisMultiNumberRedial);
        }
    }
}

