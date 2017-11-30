package hiapp.modules.dm.hidialermode.bo;
import hiapp.modules.dm.Constants;
import hiapp.modules.dm.hidialermode.HidialerOutboundDataManage;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HidialerModeCustomerWaitPool {

    @Autowired
    HidialerOutboundDataManage hidialerOutboundDataManage;


    // 等待拨打结果的客户池，坐席人员维度
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
    Map<Long, Map<String, HidialerModeCustomer>> mapTimeOutWaitOutboundResultCustomerPool;

    Long earliestPhoneConnectTimeSlot;
    Long earliestScreenPopUpTimeSlot;
    Long earliestResultTimeSlot;

    public HidialerModeCustomerWaitPool() {
        mapOutboundResultWaitSubmitCustomerPool = new HashMap<String, Map<String, HidialerModeCustomer>>();
        mapShareBatchWaitStopCustomerPool = new HashMap<String, Map<String, HidialerModeCustomer>>();

        mapTimeOutWaitPhoneConnectCustomerPool = new HashMap<Long, Map<String, HidialerModeCustomer>>();
        mapTimeOutWaitScreenPopUpCustomerPool = new HashMap<Long, Map<String, HidialerModeCustomer>>();
        mapTimeOutWaitOutboundResultCustomerPool = new HashMap<Long, Map<String, HidialerModeCustomer>>();
    }


    public void add(String userId, HidialerModeCustomer customerItem) {
        Map<String, HidialerModeCustomer> mapWaitResultPool = mapOutboundResultWaitSubmitCustomerPool.get(userId);
        if (null == mapWaitResultPool) {
            mapWaitResultPool = new HashMap<String, HidialerModeCustomer>();
            mapOutboundResultWaitSubmitCustomerPool.put(userId, mapWaitResultPool);
        }
        mapWaitResultPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        Map<String, HidialerModeCustomer> mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(customerItem.getShareBatchId());
        if (null == mapWaitStopPool) {
            mapWaitStopPool = new HashMap<String, HidialerModeCustomer>();
            mapShareBatchWaitStopCustomerPool.put(customerItem.getShareBatchId(), mapWaitStopPool);
        }
        mapWaitStopPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        if (HidialerModeCustomerStateEnum.EXTRACTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            Map<String, HidialerModeCustomer> mapWaitTimeOutPool = mapTimeOutWaitPhoneConnectCustomerPool.get(timeSlot);
            if (null == mapWaitTimeOutPool) {
                mapWaitTimeOutPool = new HashMap<String, HidialerModeCustomer>();
                mapTimeOutWaitPhoneConnectCustomerPool.put(timeSlot, mapWaitTimeOutPool);
            }
            mapWaitTimeOutPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        } else if (HidialerModeCustomerStateEnum.PHONECONNECTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            Map<String, HidialerModeCustomer> mapWaitTimeOutPool = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot);
            if (null == mapWaitTimeOutPool) {
                mapWaitTimeOutPool = new HashMap<String, HidialerModeCustomer>();
                mapTimeOutWaitScreenPopUpCustomerPool.put(timeSlot, mapWaitTimeOutPool);
            }
            mapWaitTimeOutPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(),
                    customerItem);

        } else if (HidialerModeCustomerStateEnum.SCREENPOPUP.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            Map<String, HidialerModeCustomer> mapWaitTimeOutPool = mapTimeOutWaitOutboundResultCustomerPool.get(timeSlot);
            if (null == mapWaitTimeOutPool) {
                mapWaitTimeOutPool = new HashMap<String, HidialerModeCustomer>();
                mapTimeOutWaitOutboundResultCustomerPool.put(timeSlot, mapWaitTimeOutPool);
            }
            mapWaitTimeOutPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(),
                    customerItem);
        }

    }

    public void hidialerPhoneConnect(HidialerModeCustomer customer, Date originModifyTime) {
        Long timeSlot = originModifyTime.getTime()/Constants.timeSlotSpan;
        Map<String, HidialerModeCustomer> mapWaitTimeOutPool = mapTimeOutWaitPhoneConnectCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId());
            if (mapWaitTimeOutPool.isEmpty())
                mapTimeOutWaitPhoneConnectCustomerPool.remove(timeSlot);
        }

        Long timeSlot2 = customer.getModifyTime().getTime()/Constants.timeSlotSpan;
        Map<String, HidialerModeCustomer> mapWaitTimeOutPool2 = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot2);
        if (null == mapWaitTimeOutPool2) {
            mapWaitTimeOutPool2 = new HashMap<String, HidialerModeCustomer>();
            mapTimeOutWaitScreenPopUpCustomerPool.put(timeSlot2, mapWaitTimeOutPool2);
        }
        mapWaitTimeOutPool2.put(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId(), customer);
    }

    public void agentScreenPopUp(HidialerModeCustomer customer, Date originModifyTime) {
        Long timeSlot = originModifyTime.getTime()/Constants.timeSlotSpan;
        Map<String, HidialerModeCustomer> mapWaitTimeOutPool = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId());
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitScreenPopUpCustomerPool.remove(timeSlot);
            }
        }

        Long timeSlot2 = customer.getModifyTime().getTime()/Constants.timeSlotSpan;
        Map<String, HidialerModeCustomer> mapWaitTimeOutPool2 = mapTimeOutWaitOutboundResultCustomerPool.get(timeSlot2);
        if (null == mapWaitTimeOutPool2) {
            mapWaitTimeOutPool2 = new HashMap<String, HidialerModeCustomer>();
            mapTimeOutWaitOutboundResultCustomerPool.put(timeSlot2, mapWaitTimeOutPool2);
        }
        mapWaitTimeOutPool2.put(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId(), customer);
    }

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
            removeWaitResultTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);
        }

        return customerItem;
    }

    public HidialerModeCustomer getWaitCustome(String userId, int bizId, String importBatchId, String customerId) {
        Map<String, HidialerModeCustomer> mapWaitResultPool = mapOutboundResultWaitSubmitCustomerPool.get(userId);
        if (null == mapWaitResultPool)
            return null;

        HidialerModeCustomer customerItem = mapWaitResultPool.get(bizId + importBatchId + customerId);
        return customerItem;
    }

    /**
     * 仅标注已经停止共享，不从等待池中移除。需要等待已拨打的结果。
     * @param shareBatchIds
     */
    public void markShareBatchStopFromCustomerWaitPool(int bizId, List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            Map<String, HidialerModeCustomer> mapWaitStopPool;
            mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(shareBatchId);
            if (null == mapWaitStopPool || mapWaitStopPool.isEmpty())
                continue;

            for (HidialerModeCustomer item : mapWaitStopPool.values()) {
                item.setInvalid(true);
            }
        }
    }

    public HidialerModeCustomer getWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {
        Map<String, HidialerModeCustomer> mapWaitResultPool = mapOutboundResultWaitSubmitCustomerPool.get(userId);
        if (null == mapWaitResultPool)
            return null;

        HidialerModeCustomer customerItem = mapWaitResultPool.get(bizId + importBatchId + customerId);
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

    public void timeoutProc() {
        Date now =  new Date();
        Long curTimeSlot = now.getTime()/ Constants.timeSlotSpan;
        System.out.println("m2 timeout proc: cur time slot " + curTimeSlot);

        // HiDialer 呼通超时处理
        Long phoneConnectTimeoutTimeSlot = curTimeSlot - Constants.PhoneConnectTimeoutThreshold2/Constants.timeSlotSpan;
        while (earliestPhoneConnectTimeSlot < phoneConnectTimeoutTimeSlot) {
            Map<String, HidialerModeCustomer> mapTimeSlotWaitTimeOutPool;
            mapTimeSlotWaitTimeOutPool =  mapTimeOutWaitPhoneConnectCustomerPool.remove(earliestPhoneConnectTimeSlot++);
            if (null == mapTimeSlotWaitTimeOutPool)
                continue;

            for (HidialerModeCustomer customerItem : mapTimeSlotWaitTimeOutPool.values()) {
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
            Map<String, HidialerModeCustomer> mapTimeSlotWaitTimeOutPool;
            mapTimeSlotWaitTimeOutPool =  mapTimeOutWaitScreenPopUpCustomerPool.remove(earliestScreenPopUpTimeSlot++);
            if (null == mapTimeSlotWaitTimeOutPool)
                continue;

            for (HidialerModeCustomer customerItem : mapTimeSlotWaitTimeOutPool.values()) {
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
            Map<String, HidialerModeCustomer> mapTimeSlotWaitTimeOutPool;
            mapTimeSlotWaitTimeOutPool =  mapTimeOutWaitOutboundResultCustomerPool.remove(earliestResultTimeSlot++);
            if (null == mapTimeSlotWaitTimeOutPool)
                continue;

            for (HidialerModeCustomer customerItem : mapTimeSlotWaitTimeOutPool.values()) {
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

    public void postProcess() {
        Date now =  new Date();
        earliestPhoneConnectTimeSlot = now.getTime()/ Constants.timeSlotSpan;
        earliestScreenPopUpTimeSlot = now.getTime()/ Constants.timeSlotSpan;
        earliestResultTimeSlot = now.getTime()/ Constants.timeSlotSpan;

        Set<Long> phoneConnectTimeSlotSet = mapTimeOutWaitPhoneConnectCustomerPool.keySet();
        for (Long timeSlot : phoneConnectTimeSlotSet) {
            if (timeSlot < earliestPhoneConnectTimeSlot)
                earliestPhoneConnectTimeSlot = timeSlot;
        }

        Set<Long> screenPopupTimeSlotSet = mapTimeOutWaitScreenPopUpCustomerPool.keySet();
        for (Long timeSlot : screenPopupTimeSlotSet) {
            if (timeSlot < earliestScreenPopUpTimeSlot)
                earliestScreenPopUpTimeSlot = timeSlot;
        }

        Set<Long> resultTimeSlotSet = mapTimeOutWaitOutboundResultCustomerPool.keySet();
        for (Long timeSlot : resultTimeSlotSet) {
            if (timeSlot < earliestResultTimeSlot)
                earliestResultTimeSlot = timeSlot;
        }
    }

    private HidialerModeCustomer removeWaitResultCustome(String userId, int bizId, String importBatchId, String customerId) {
        HidialerModeCustomer customerItem = null;

        Map<String, HidialerModeCustomer> mapWaitResultPool = mapOutboundResultWaitSubmitCustomerPool.get(userId);
        if (null != mapWaitResultPool) {
            customerItem = mapWaitResultPool.remove(bizId + importBatchId + customerId);
            if (mapWaitResultPool.isEmpty())
                mapOutboundResultWaitSubmitCustomerPool.remove(userId);
        }
        return customerItem;
    }

    private void removeWaitStopCustomer(int bizId, String shareBatchId, String importBatchId, String customerId) {
        Map<String, HidialerModeCustomer> mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(shareBatchId);
        if (null != mapWaitStopPool) {
            mapWaitStopPool.remove(bizId + importBatchId + customerId);
            if (mapWaitStopPool.isEmpty())
                mapShareBatchWaitStopCustomerPool.remove(shareBatchId);
        }
    }

    private void removeWaitPhoneConnectTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<String, HidialerModeCustomer> mapWaitTimeOutPool = mapTimeOutWaitPhoneConnectCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(bizId + importBatchId + customerId);
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitPhoneConnectCustomerPool.remove(timeSlot);
            }
        }
    }

    private void removeWaitScreenPopUpTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<String, HidialerModeCustomer> mapWaitTimeOutPool = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(bizId + importBatchId + customerId);
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitScreenPopUpCustomerPool.remove(timeSlot);
            }
        }
    }

    private void removeWaitResultTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<String, HidialerModeCustomer> mapWaitTimeOutPool = mapTimeOutWaitOutboundResultCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(bizId + importBatchId + customerId);
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitOutboundResultCustomerPool.remove(timeSlot);
            }
        }
    }
}

