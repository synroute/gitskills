package hiapp.modules.dm.hidialermode.bo;

import hiapp.modules.dm.Constants;
import hiapp.modules.dm.hidialermode.HidialerOutboundDataManage;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Component
public class HidialerModeCustomerWaitPool {

    @Autowired
    HidialerOutboundDataManage hidialerOutboundDataManage;

    @Autowired
    JedisPool jedisPool;

   /* // 等待拨打结果的客户池，坐席人员维度
    // UserID <==> {BizID + ImportID + CustomerID <==> HidialerModeCustomer}
    Map<String, Map<String, HidialerModeCustomer>> mapOutboundResultWaitSubmitCustomerPool;

    // 等待共享停止的客户池，共享批次维度，用于标注已经停止共享的客户
    // ShareBatchId <==> {BizId + ImportId + CustomerId <==> HidialerModeCustomer}
    Map<String, Map<String, HidialerModeCustomer>> mapShareBatchWaitStopCustomerPool;

    // 等待hidialer呼通超时的客户池，抽取时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> HidialerModeCustomer}
    Map<Long, Map<String, HidialerModeCustomer>> mapTimeOutWaitPhoneConnectCustomerPool;

    // 等待坐席弹屏超时的客户池，hidialer呼通时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> HidialerModeCustomer}
    Map<Long, Map<String, HidialerModeCustomer>> mapTimeOutWaitScreenPopUpCustomerPool;

    // 等待坐席拨打结果超时的客户池，坐席弹屏时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> HidialerModeCustomer}
    Map<Long, Map<String, HidialerModeCustomer>> mapTimeOutWaitOutboundResultCustomerPool;*/

    Long earliestPhoneConnectTimeSlot;
    Long earliestScreenPopUpTimeSlot;
    Long earliestResultTimeSlot;
    Jedis hidialerWaitPool;
    public void initialize() {
        hidialerWaitPool = jedisPool.getResource();
    }
    public void clear() {
        if (hidialerWaitPool != null){
            hidialerWaitPool.close();
        }
    }
    public HidialerModeCustomerWaitPool() {
        //hidialerWaitPool = jedisPool.getResource();
       /* mapOutboundResultWaitSubmitCustomerPool = new HashMap<String, Map<String, HidialerModeCustomer>>();
        mapShareBatchWaitStopCustomerPool = new HashMap<String, Map<String, HidialerModeCustomer>>();

        mapTimeOutWaitPhoneConnectCustomerPool = new HashMap<Long, Map<String, HidialerModeCustomer>>();
        mapTimeOutWaitScreenPopUpCustomerPool = new HashMap<Long, Map<String, HidialerModeCustomer>>();
        mapTimeOutWaitOutboundResultCustomerPool = new HashMap<Long, Map<String, HidialerModeCustomer>>();*/

        Date now =  new Date();
        earliestPhoneConnectTimeSlot = now.getTime()/ Constants.timeSlotSpan;
        earliestScreenPopUpTimeSlot = now.getTime()/ Constants.timeSlotSpan;
        earliestResultTimeSlot = now.getTime()/ Constants.timeSlotSpan;

    }

    //已改
    public void add(String userId, HidialerModeCustomer customerItem) {
        //一次性存进去
        hidialerWaitPool.hset(GenericitySerializeUtil.serialize("mapOutboundResultWaitSubmitCustomerPool" + userId),
                GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                GenericitySerializeUtil.serialize(customerItem));

        hidialerWaitPool.hset(GenericitySerializeUtil.serialize("mapShareBatchWaitStopCustomerPool" + customerItem.getShareBatchId()),
                GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                GenericitySerializeUtil.serialize(customerItem));

        if (HidialerModeCustomerStateEnum.EXTRACTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            hidialerWaitPool.hset(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                    GenericitySerializeUtil.serialize(customerItem));

        } else if (HidialerModeCustomerStateEnum.PHONECONNECTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            hidialerWaitPool.hset(GenericitySerializeUtil.serialize("mapTimeOutWaitScreenPopUpCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                    GenericitySerializeUtil.serialize(customerItem));

        } else if (HidialerModeCustomerStateEnum.SCREENPOPUP.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            hidialerWaitPool.hset(GenericitySerializeUtil.serialize("mapTimeOutWaitOutboundResultCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                    GenericitySerializeUtil.serialize(customerItem));
        }

    }
    //已改
    public void hidialerPhoneConnect(HidialerModeCustomer customer, Date originModifyTime) {
        Long timeSlot = originModifyTime.getTime()/Constants.timeSlotSpan;
        //必须先取出来，不然如果为空，会报空指针异常
        if (!hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool" + timeSlot)).isEmpty()) {
            hidialerWaitPool.hdel(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId()));
            if (hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool" + timeSlot)).isEmpty())
                hidialerWaitPool.hdel(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool" + timeSlot));
        }

        Long timeSlot2 = customer.getModifyTime().getTime()/Constants.timeSlotSpan;

        hidialerWaitPool.hset(GenericitySerializeUtil.serialize("mapTimeOutWaitScreenPopUpCustomerPool" + timeSlot2),
                GenericitySerializeUtil.serialize(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId()),
                GenericitySerializeUtil.serialize(customer));
    }
    //已改
    public void agentScreenPopUp(HidialerModeCustomer customer, Date originModifyTime) {
        Long timeSlot = originModifyTime.getTime()/Constants.timeSlotSpan;
        //必须先取出来，不然如果为空，会报空指针异常
        if (!hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapTimeOutWaitScreenPopUpCustomerPool" + timeSlot)).isEmpty()) {
            hidialerWaitPool.hdel(GenericitySerializeUtil.serialize("mapTimeOutWaitScreenPopUpCustomerPool" + timeSlot),
                    GenericitySerializeUtil.serialize(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId()));
            if (hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapTimeOutWaitScreenPopUpCustomerPool" + timeSlot)).isEmpty())
                hidialerWaitPool.hdel(GenericitySerializeUtil.serialize("mapTimeOutWaitScreenPopUpCustomerPool" + timeSlot));
        }

        Long timeSlot2 = customer.getModifyTime().getTime()/Constants.timeSlotSpan;
        hidialerWaitPool.hset(GenericitySerializeUtil.serialize("mapTimeOutWaitOutboundResultCustomerPool" + timeSlot2),
                GenericitySerializeUtil.serialize(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId()),
                GenericitySerializeUtil.serialize(customer));
    }
    //已改
    public HidialerModeCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {

        HidialerModeCustomer customerItem = removeWaitResultCustome(userId, bizId, importBatchId, customerId);
        if (null == customerItem)
            return customerItem;

        removeWaitStopCustomer(bizId, customerItem.getShareBatchId(), importBatchId, customerId);

        if (HidialerModeCustomerStateEnum.EXTRACTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
            removeWaitPhoneConnectTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);
        } else if (HidialerModeCustomerStateEnum.PHONECONNECTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
            removeWaitScreenPopUpTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);
        } else if (HidialerModeCustomerStateEnum.SCREENPOPUP.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
            //
            removeWaitResultTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);
        }

        return customerItem;
    }
    //已改
    public HidialerModeCustomer getWaitCustome(String userId, int bizId, String importBatchId, String customerId) {

        HidialerModeCustomer customerItem = null;
        Map<byte[], byte[]> mapWaitResultPool = hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapOutboundResultWaitSubmitCustomerPool" + userId));
        if (!mapWaitResultPool.isEmpty()) {
            customerItem = GenericitySerializeUtil.unserialize(mapWaitResultPool.get(GenericitySerializeUtil.serialize(bizId + importBatchId + customerId)));

        }
        return customerItem;
    }

    /**
     * 仅标注已经停止共享，不从等待池中移除。需要等待已拨打的结果。
     * @param shareBatchIds
     */
    //已改
    public void markShareBatchStopFromCustomerWaitPool(int bizId, List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            Map<byte[], byte[]> mapWaitStopPool = hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapShareBatchWaitStopCustomerPool" + shareBatchId));
            if (mapWaitStopPool.isEmpty())
                continue;
            Set<Map.Entry<byte[], byte[]>> entrySet = mapWaitStopPool.entrySet();
            for (Map.Entry<byte[], byte[]> entry : entrySet) {
                HidialerModeCustomer hidialerModeCustomer = GenericitySerializeUtil.unserialize(entry.getValue());
                hidialerModeCustomer.setInvalid(true);
                mapWaitStopPool.put(entry.getKey(), GenericitySerializeUtil.serialize(hidialerModeCustomer));
            }
            hidialerWaitPool.hmset(GenericitySerializeUtil.serialize("mapShareBatchWaitStopCustomerPool" + shareBatchId), mapWaitStopPool);
        }
    }
    //已改
    public HidialerModeCustomer getWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {
        if (hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapShareBatchWaitStopCustomerPool" + userId)).isEmpty())
            return null;

        HidialerModeCustomer customerItem = GenericitySerializeUtil.unserialize(hidialerWaitPool.
                hget(GenericitySerializeUtil.serialize("mapShareBatchWaitStopCustomerPool" + userId),
                GenericitySerializeUtil.serialize(bizId + importBatchId + customerId)));
        return customerItem;
    }

    public void onLogin(String userId) {

        // TODO ??? 需要处理用户登录通知吗？

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
        Date now =  new Date();
        Long curTimeSlot = now.getTime()/ Constants.timeSlotSpan;

        // HiDialer 呼通超时处理
        Long phoneConnectTimeoutTimeSlot = curTimeSlot - Constants.PhoneConnectTimeoutThreshold2/Constants.timeSlotSpan;
        while (earliestPhoneConnectTimeSlot < phoneConnectTimeoutTimeSlot) {
            Map<byte[], byte[]> mapTimeSlotWaitTimeOutPool = hidialerWaitPool.hgetAll(GenericitySerializeUtil.
                    serialize("mapTimeOutWaitPhoneConnectCustomerPool" + earliestPhoneConnectTimeSlot++));
            if (mapTimeSlotWaitTimeOutPool.isEmpty())
                continue;
            for (Map.Entry<byte[], byte[]> entry : mapTimeSlotWaitTimeOutPool.entrySet()) {
                HidialerModeCustomer customerItem = GenericitySerializeUtil.unserialize(entry.getValue());
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    hidialerOutboundDataManage.lostProc(customerItem, HidialerModeCustomerStateEnum.HIDIALER_LOSS_WAIT_REDIAL);
                }

                removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }

        // 坐席弹屏 超时处理 ==> 呼损处理
        Long screenPopupTimeoutTimeSlot = curTimeSlot - Constants.ScreenPopUpTimeoutThreshold3/Constants.timeSlotSpan;
        while (earliestScreenPopUpTimeSlot < screenPopupTimeoutTimeSlot) {
            Map<byte[], byte[]> mapTimeSlotWaitTimeOutPool = hidialerWaitPool.hgetAll(GenericitySerializeUtil.
                    serialize("mapTimeOutWaitScreenPopUpCustomerPool" + earliestScreenPopUpTimeSlot++));
            if (mapTimeSlotWaitTimeOutPool.isEmpty())
                continue;

            for (Map.Entry<byte[], byte[]> entry : mapTimeSlotWaitTimeOutPool.entrySet()) {
                HidialerModeCustomer customerItem = GenericitySerializeUtil.unserialize(entry.getValue());
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    hidialerOutboundDataManage.lostProc(customerItem, HidialerModeCustomerStateEnum.HIDIALER_LOSS_WAIT_REDIAL);  // 呼损处理
                }

                removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }

        // 坐席递交结果 超时处理
        Long resultTimeoutTimeSlot = curTimeSlot - Constants.ResultTimeoutThreshold4/Constants.timeSlotSpan;
        while (earliestResultTimeSlot < resultTimeoutTimeSlot) {
            Map<byte[], byte[]> mapTimeSlotWaitTimeOutPool = hidialerWaitPool.hgetAll(GenericitySerializeUtil.
                    serialize("mapTimeOutWaitOutboundResultCustomerPool" + earliestResultTimeSlot++));
            if (mapTimeSlotWaitTimeOutPool.isEmpty())
                continue;

            for (Map.Entry<byte[], byte[]> entry : mapTimeSlotWaitTimeOutPool.entrySet()) {
                HidialerModeCustomer customerItem = GenericitySerializeUtil.unserialize(entry.getValue());
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    hidialerOutboundDataManage.lostProc(customerItem, HidialerModeCustomerStateEnum.LOSS_WAIT_REDIAL);
                }

                removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }
    }
    //已改
    public void postProcess() {
        Set<byte[]> phoneConnectTimeSlotSet = hidialerWaitPool.keys(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool*"));
        for (byte[] key : phoneConnectTimeSlotSet) {
            String s = GenericitySerializeUtil.unserialize(key);
            s = s.substring(s.lastIndexOf('l') + 1);
            Long timeSlot = Long.valueOf(s);
            if (timeSlot < earliestPhoneConnectTimeSlot)
                earliestPhoneConnectTimeSlot = timeSlot;
        }

        Set<byte[]> screenPopupTimeSlotSet = hidialerWaitPool.keys(GenericitySerializeUtil.serialize("mapTimeOutWaitScreenPopUpCustomerPool*"));
        for (byte[] key : screenPopupTimeSlotSet) {
            String s = GenericitySerializeUtil.unserialize(key);
            s = s.substring(s.lastIndexOf('l') + 1);
            Long timeSlot = Long.valueOf(s);
            if (timeSlot < earliestScreenPopUpTimeSlot)
                earliestScreenPopUpTimeSlot = timeSlot;
        }

        Set<byte[]> resultTimeSlotSet = hidialerWaitPool.keys(GenericitySerializeUtil.serialize("mapTimeOutWaitOutboundResultCustomerPool*"));
        for (byte[] key : resultTimeSlotSet) {
            String s = GenericitySerializeUtil.unserialize(key);
            s = s.substring(s.lastIndexOf('l') + 1);
            Long timeSlot = Long.valueOf(s);
            if (timeSlot < earliestResultTimeSlot)
                earliestResultTimeSlot = timeSlot;
        }
    }
    //已改
    private HidialerModeCustomer removeWaitResultCustome(String userId, int bizId, String importBatchId, String customerId) {
        HidialerModeCustomer customerItem = null;

        Map<byte[], byte[]> mapWaitResultPool = hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapOutboundResultWaitSubmitCustomerPool" + userId));
        if (!mapWaitResultPool.isEmpty()) {
            customerItem = GenericitySerializeUtil.unserialize(mapWaitResultPool.remove(GenericitySerializeUtil.serialize(bizId + importBatchId + customerId)));
            if (mapWaitResultPool.isEmpty())
                hidialerWaitPool.hdel(GenericitySerializeUtil.serialize("mapOutboundResultWaitSubmitCustomerPool" + userId));
        }else {
            hidialerWaitPool.hmset(GenericitySerializeUtil.serialize("mapOutboundResultWaitSubmitCustomerPool" + userId),mapWaitResultPool);
        }
        return customerItem;
    }
    //已改
    private void removeWaitStopCustomer(int bizId, String shareBatchId, String importBatchId, String customerId) {
        Map<byte[], byte[]> mapWaitStopPool = hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapShareBatchWaitStopCustomerPool" + shareBatchId));
        if (!mapWaitStopPool.isEmpty()) {
            mapWaitStopPool.remove(GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
            if (mapWaitStopPool.isEmpty())
                hidialerWaitPool.hdel(GenericitySerializeUtil.serialize("mapShareBatchWaitStopCustomerPool" + shareBatchId));
        }else {
            hidialerWaitPool.hmset(GenericitySerializeUtil.serialize("mapShareBatchWaitStopCustomerPool" + shareBatchId),mapWaitStopPool);
        }
    }
    //已改
    private void removeWaitPhoneConnectTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {

        Map<byte[], byte[]> mapWaitTimeOutPool = hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool" + timeSlot));
        if (!mapWaitTimeOutPool.isEmpty()) {
            mapWaitTimeOutPool.remove(GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
            if (mapWaitTimeOutPool.isEmpty())
                hidialerWaitPool.hdel(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool" + timeSlot));
        }else {
            hidialerWaitPool.hmset(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool" + timeSlot),mapWaitTimeOutPool);
        }
    }
    //已改
    private void removeWaitScreenPopUpTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<byte[], byte[]> mapWaitTimeOutPool = hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapTimeOutWaitScreenPopUpCustomerPool" + timeSlot));
        if (!mapWaitTimeOutPool.isEmpty()) {
            mapWaitTimeOutPool.remove(GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
            if (mapWaitTimeOutPool.isEmpty())
                hidialerWaitPool.hdel(GenericitySerializeUtil.serialize("mapTimeOutWaitScreenPopUpCustomerPool" + timeSlot));
        }else {
            hidialerWaitPool.hmset(GenericitySerializeUtil.serialize("mapTimeOutWaitScreenPopUpCustomerPool" + timeSlot),mapWaitTimeOutPool);
        }
    }
    //已改
    private void removeWaitResultTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<byte[], byte[]> mapWaitTimeOutPool = hidialerWaitPool.hgetAll(GenericitySerializeUtil.serialize("mapTimeOutWaitOutboundResultCustomerPool" + timeSlot));
        if (!mapWaitTimeOutPool.isEmpty()) {
            mapWaitTimeOutPool.remove(GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
            if (mapWaitTimeOutPool.isEmpty())
                hidialerWaitPool.hdel(GenericitySerializeUtil.serialize("mapTimeOutWaitOutboundResultCustomerPool" + timeSlot));
        }else {
            hidialerWaitPool.hmset(GenericitySerializeUtil.serialize("mapTimeOutWaitOutboundResultCustomerPool" + timeSlot),mapWaitTimeOutPool);
        }
    }

}

