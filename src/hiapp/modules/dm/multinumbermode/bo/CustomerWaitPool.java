package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dm.Constants;
import hiapp.modules.dm.multinumbermode.MultiNumberOutboundDataManage;
import hiapp.modules.dm.redismanager.JedisUtils;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Component
public class CustomerWaitPool {

    @Autowired
    MultiNumberOutboundDataManage multiNumberOutboundDataManage;

    @Autowired
    private JedisUtils jedisUtils;

    // 等待拨打结果的客户池，坐席人员维度
    // UserID <==> {BizID + ImportID + CustomerID <==> MultiNumberCustomer}
    /*Map<String, Map<String, MultiNumberCustomer>> multiNumberWaitMapOutboundResultWaitSubmitCustomerPool;

    // 等待共享停止的客户池，共享批次维度，用于标注已经停止共享的客户
    // ShareBatchId <==> {BizId + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<String, Map<String, MultiNumberCustomer>> multiNumberWaitMapShareBatchWaitStopCustomerPool;

    // 等待hidialer呼通超时的客户池，抽取时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<Long, Map<String, MultiNumberCustomer>> multiNumberWaitMapTimeOutWaitPhoneConnectCustomerPool;

    // 等待坐席弹屏超时的客户池，hidialer呼通时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<Long, Map<String, MultiNumberCustomer>> multiNumberWaitMapTimeOutWaitScreenPopUpCustomerPool;

    // 等待坐席拨打结果超时的客户池，坐席弹屏时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<Long, Map<String, MultiNumberCustomer>> multiNumberWaitMapTimeOutWaitOutboundResultCustomerPool;*/

    private Jedis redisCustomerWaitPool;

    Long earliestPhoneConnectTimeSlot;
    Long earliestScreenPopUpTimeSlot;
    Long earliestResultTimeSlot;

    public CustomerWaitPool() {
        Date now = new Date();
        earliestPhoneConnectTimeSlot = now.getTime() / Constants.timeSlotSpan;
        earliestScreenPopUpTimeSlot = now.getTime() / Constants.timeSlotSpan;
        earliestResultTimeSlot = now.getTime() / Constants.timeSlotSpan;
    }

    //已改
    public void add(String userId, MultiNumberCustomer customerItem) {
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            //直接存进去
            redisCustomerWaitPool.hset(GenericitySerializeUtil.serialize("multiNumberWaitMapOutboundResultWaitSubmitCustomerPool" + userId),
                    GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                    GenericitySerializeUtil.serialize(customerItem)
            );
            //直接存进去
            redisCustomerWaitPool.hset(GenericitySerializeUtil.serialize("multiNumberWaitMapShareBatchWaitStopCustomerPool" + userId),
                    GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                    GenericitySerializeUtil.serialize(customerItem)
            );
            if (MultiNumberPredictStateEnum.EXTRACTED.equals(customerItem.getState())) {
                Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
                //直接存进去
                redisCustomerWaitPool.hset(GenericitySerializeUtil.serialize("multiNumberWaitMapTimeOutWaitPhoneConnectCustomerPool" + timeSlot),
                        GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                        GenericitySerializeUtil.serialize(customerItem)
                );
            } else if (MultiNumberPredictStateEnum.PHONECONNECTED.equals(customerItem.getState())) {
                Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
                redisCustomerWaitPool.hset(GenericitySerializeUtil.serialize("multiNumberWaitMapTimeOutWaitScreenPopUpCustomerPool" + timeSlot),
                        GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                        GenericitySerializeUtil.serialize(customerItem)
                );

            } else if (MultiNumberPredictStateEnum.SCREENPOPUP.equals(customerItem.getState())) {
                Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
                redisCustomerWaitPool.hset(GenericitySerializeUtil.serialize("multiNumberWaitMapTimeOutWaitOutboundResultCustomerPool" + timeSlot),
                        GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                        GenericitySerializeUtil.serialize(customerItem)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }

    }

    //已改
    public void hidialerPhoneConnect(MultiNumberCustomer customer, Date originModifyTime) {
        Long timeSlot = originModifyTime.getTime() / Constants.timeSlotSpan;
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            //直接删除
            redisCustomerWaitPool.hdel(GenericitySerializeUtil.serialize("multiNumberWaitMapTimeOutWaitPhoneConnectCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId())
            );

            Long timeSlot2 = customer.getModifyTime().getTime() / Constants.timeSlotSpan;

            redisCustomerWaitPool.hset(GenericitySerializeUtil.serialize("multiNumberWaitMapTimeOutWaitScreenPopUpCustomerPool" + timeSlot2),
                    GenericitySerializeUtil.serialize(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId()),
                    GenericitySerializeUtil.serialize(customer)
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }
    }

    //已改
    public void agentScreenPopUp(MultiNumberCustomer customer, Date originModifyTime) {
        Long timeSlot = originModifyTime.getTime() / Constants.timeSlotSpan;
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            //直接删除
            redisCustomerWaitPool.hdel(GenericitySerializeUtil.serialize("multiNumberWaitMapTimeOutWaitScreenPopUpCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId())
            );
            Long timeSlot2 = customer.getModifyTime().getTime() / Constants.timeSlotSpan;

            redisCustomerWaitPool.hset(GenericitySerializeUtil.serialize("multiNumberWaitMapTimeOutWaitOutboundResultCustomerPool" + timeSlot2),
                    GenericitySerializeUtil.serialize(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId()),
                    GenericitySerializeUtil.serialize(customer)
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }
    }

    //已改
    public MultiNumberCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {

        MultiNumberCustomer customerItem = removeWaitResultCustome(userId, bizId, importBatchId, customerId);
        if (null == customerItem)
            return customerItem;


        removeWaitStopCustomer(bizId, customerItem.getShareBatchId(), importBatchId, customerId);

        if (MultiNumberPredictStateEnum.EXTRACTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
            removeWaitPhoneConnectTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);
        } else if (MultiNumberPredictStateEnum.PHONECONNECTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
            removeWaitScreenPopUpTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);
        } else if (MultiNumberPredictStateEnum.SCREENPOPUP.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
            removeWaitResultTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);
        }

        return customerItem;
    }

    //已改
    public MultiNumberCustomer getWaitCustome(String userId, int bizId, String importBatchId, String customerId) {
        MultiNumberCustomer customerItem = null;
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            customerItem = GenericitySerializeUtil.unserialize(redisCustomerWaitPool.
                    hget(GenericitySerializeUtil.serialize("multiNumberWaitMapOutboundResultWaitSubmitCustomerPool" + userId),
                            GenericitySerializeUtil.serialize(bizId + importBatchId + customerId)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
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
            redisCustomerWaitPool = jedisUtils.getJedis();
            for (String shareBatchId : shareBatchIds) {
                byte[] mapSerialize = GenericitySerializeUtil.serialize("multiNumberWaitMapShareBatchWaitStopCustomerPool" + shareBatchId);
                Map<byte[], byte[]> mapWaitStopPool = redisCustomerWaitPool.hgetAll(mapSerialize);
                if (mapWaitStopPool.isEmpty())
                    continue;
                Set<Map.Entry<byte[], byte[]>> entries = mapWaitStopPool.entrySet();
                for (Map.Entry<byte[], byte[]> entry : entries) {
                    MultiNumberCustomer item = GenericitySerializeUtil.unserialize(entry.getValue());
                    item.setInvalid(true);
                    redisCustomerWaitPool.hset(mapSerialize, entry.getKey(), GenericitySerializeUtil.serialize(item));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }
    }

    public void onLogin(String userId) {

        // TODO ??? 多号码预测外呼需要处理用户登录通知吗？

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
        System.out.println("cur time slot : " + curTimeSlot);

        // HiDialer 呼通超时处理
        Long phoneConnectTimeoutTimeSlot = curTimeSlot - Constants.PhoneConnectTimeoutThreshold2 / Constants.timeSlotSpan;
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            while (earliestPhoneConnectTimeSlot < phoneConnectTimeoutTimeSlot) {
                //先取出来
                byte[] mapSerialize = GenericitySerializeUtil.serialize(
                        "multiNumberWaitMapTimeOutWaitPhoneConnectCustomerPool" + earliestPhoneConnectTimeSlot++);
                Map<byte[], byte[]> mapTimeSlotWaitTimeOutPool = redisCustomerWaitPool.hgetAll(mapSerialize);
                //再删除
                redisCustomerWaitPool.del(mapSerialize);
                if (mapTimeSlotWaitTimeOutPool.isEmpty())
                    continue;
                Set<Map.Entry<byte[], byte[]>> entries = mapTimeSlotWaitTimeOutPool.entrySet();
                for (Map.Entry<byte[], byte[]> entry : entries) {
                    MultiNumberCustomer customerItem = GenericitySerializeUtil.unserialize(entry.getValue());
                    // 放回客户共享池
                    if (!customerItem.getInvalid()) {
                        multiNumberOutboundDataManage.lostProc(customerItem, MultiNumberPredictStateEnum.HIDIALER_LOSS_WAIT_REDIAL);  // 呼损处理
                    }

                    removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                    removeWaitStopCustomer(customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                            customerItem.getCustomerId());
                }
            }

            // 坐席弹屏 超时处理 ==> 呼损处理
            Long screenPopupTimeoutTimeSlot = curTimeSlot - Constants.ScreenPopUpTimeoutThreshold3 / Constants.timeSlotSpan;
            while (earliestScreenPopUpTimeSlot < screenPopupTimeoutTimeSlot) {
                //先取出来
                byte[] mapSerialize = GenericitySerializeUtil.serialize(
                        "multiNumberWaitMapTimeOutWaitScreenPopUpCustomerPool" + earliestScreenPopUpTimeSlot++);
                Map<byte[], byte[]> mapTimeSlotWaitTimeOutPool = redisCustomerWaitPool.hgetAll(mapSerialize);
                //再删除
                redisCustomerWaitPool.del(mapSerialize);
                if (mapTimeSlotWaitTimeOutPool.isEmpty())
                    continue;
                Set<Map.Entry<byte[], byte[]>> entries = mapTimeSlotWaitTimeOutPool.entrySet();
                for (Map.Entry<byte[], byte[]> entry : entries) {
                    MultiNumberCustomer customerItem = GenericitySerializeUtil.unserialize(entry.getValue());
                    // 放回客户共享池
                    if (!customerItem.getInvalid()) {
                        multiNumberOutboundDataManage.lostProc(customerItem, MultiNumberPredictStateEnum.HIDIALER_LOSS_WAIT_REDIAL);  // 呼损处理
                    }

                    removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                    removeWaitStopCustomer(customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                            customerItem.getCustomerId());
                }
            }

            // 坐席递交结果 超时处理
            Long resultTimeoutTimeSlot = curTimeSlot - Constants.ResultTimeoutThreshold4 / Constants.timeSlotSpan;
            while (earliestResultTimeSlot < resultTimeoutTimeSlot) {
                //先取出来
                byte[] mapSerialize = GenericitySerializeUtil.serialize(
                        "multiNumberWaitMapTimeOutWaitOutboundResultCustomerPool" + resultTimeoutTimeSlot++);
                Map<byte[], byte[]> mapTimeSlotWaitTimeOutPool = redisCustomerWaitPool.hgetAll(mapSerialize);
                //再删除
                redisCustomerWaitPool.del(mapSerialize);
                if (mapTimeSlotWaitTimeOutPool.isEmpty())
                    continue;
                Set<Map.Entry<byte[], byte[]>> entries = mapTimeSlotWaitTimeOutPool.entrySet();
                for (Map.Entry<byte[], byte[]> entry : entries) {
                    MultiNumberCustomer customerItem = GenericitySerializeUtil.unserialize(entry.getValue());
                    // 放回客户共享池
                    if (!customerItem.getInvalid()) {
                        multiNumberOutboundDataManage.lostProc(customerItem, MultiNumberPredictStateEnum.LOSS_WAIT_REDIAL);  // 呼损处理
                    }

                    removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                    removeWaitStopCustomer(customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                            customerItem.getCustomerId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }
    }

    //已改.通配符不管用
    public void postProcess() {
        Set<byte[]> phoneConnectTimeSlotSet = null;
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            phoneConnectTimeSlotSet = redisCustomerWaitPool.keys("*multiNumberWaitMapTimeOutWait*".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }
        for (byte[] key : phoneConnectTimeSlotSet) {
            String s = null;
            try {
                s = GenericitySerializeUtil.unserialize(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (s == null) {
                continue;
            }
            s = s.substring(s.lastIndexOf('l') + 1);
            Long timeSlot = Long.valueOf(s);
            if (timeSlot == null) {
                continue;
            }
            if (s.contains("multiNumberWaitMapTimeOutWaitPhoneConnectCustomerPool")) {
                if (timeSlot < earliestPhoneConnectTimeSlot)
                    earliestPhoneConnectTimeSlot = timeSlot;
            } else if (s.contains("multiNumberWaitMapTimeOutWaitScreenPopUpCustomerPool")) {
                if (timeSlot < earliestScreenPopUpTimeSlot)
                    earliestScreenPopUpTimeSlot = timeSlot;
            } else if (s.contains("multiNumberWaitMapTimeOutWaitOutboundResultCustomerPool")) {
                if (timeSlot < earliestResultTimeSlot)
                    earliestResultTimeSlot = timeSlot;
            }
        }
    }

    //已改
    private MultiNumberCustomer removeWaitResultCustome(String userId, int bizId, String importBatchId, String customerId) {
        MultiNumberCustomer customerItem = null;
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            //先获取
            customerItem = GenericitySerializeUtil.unserialize(redisCustomerWaitPool.hget(
                    GenericitySerializeUtil.serialize("multiNumberWaitMapOutboundResultWaitSubmitCustomerPool" + userId),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId)));
            //再删除
            redisCustomerWaitPool.hdel(
                    GenericitySerializeUtil.serialize("multiNumberWaitMapOutboundResultWaitSubmitCustomerPool" + userId),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }
        return customerItem;
    }

    //已改
    private void removeWaitStopCustomer(int bizId, String shareBatchId, String importBatchId, String customerId) {
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            redisCustomerWaitPool.hdel(
                    GenericitySerializeUtil.serialize("multiNumberWaitMapShareBatchWaitStopCustomerPool" + shareBatchId),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }
    }

    //已改
    private void removeWaitPhoneConnectTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            redisCustomerWaitPool.hdel(
                    GenericitySerializeUtil.serialize("multiNumberWaitMapTimeOutWaitPhoneConnectCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }
    }

    //已改
    private void removeWaitScreenPopUpTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            redisCustomerWaitPool.hdel(
                    GenericitySerializeUtil.serialize("multiNumberWaitMapTimeOutWaitScreenPopUpCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }
    }

    //已改
    private void removeWaitResultTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        try {
            redisCustomerWaitPool = jedisUtils.getJedis();
            redisCustomerWaitPool.hdel(
                    GenericitySerializeUtil.serialize("multiNumberWaitMapTimeOutWaitOutboundResultCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(redisCustomerWaitPool);
        }
    }

}

