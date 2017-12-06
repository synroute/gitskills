package hiapp.modules.dm.multinumberredialmode.bo;
import hiapp.modules.dm.Constants;
import hiapp.modules.dm.multinumbermode.MultiNumberOutboundDataManage;
import hiapp.modules.dm.multinumberredialmode.MultiNumberRedialDataManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MultiNumberRedialCustomerWaitPool {

    @Autowired
    MultiNumberRedialDataManage multiNumberOutboundDataManage;


    // 等待拨打结果的客户池，坐席人员维度
    // UserID <==> {BizID + ImportID + CustomerID <==> MultiNumberRedialCustomer}
    Map<String, Map<String, MultiNumberRedialCustomer>> mapOutboundResultWaitSubmitCustomerPool;

    // 等待共享停止的客户池，共享批次维度，用于标注已经停止共享的客户
    // ShareBatchId <==> {BizId + ImportId + CustomerId <==> MultiNumberRedialCustomer}
    Map<String, Map<String, MultiNumberRedialCustomer>> mapShareBatchWaitStopCustomerPool;

    // 等待hidialer呼通超时的客户池，抽取时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> MultiNumberRedialCustomer}
    Map<Long, Map<String, MultiNumberRedialCustomer>> mapTimeOutWaitPhoneConnectCustomerPool;

    // 等待坐席弹屏超时的客户池，hidialer呼通时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> MultiNumberRedialCustomer}
    Map<Long, Map<String, MultiNumberRedialCustomer>> mapTimeOutWaitScreenPopUpCustomerPool;

    // 等待坐席拨打结果超时的客户池，坐席弹屏时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> MultiNumberRedialCustomer}
    Map<Long, Map<String, MultiNumberRedialCustomer>> mapTimeOutWaitOutboundResultCustomerPool;

    Long earliestPhoneConnectTimeSlot;
    Long earliestScreenPopUpTimeSlot;
    Long earliestResultTimeSlot;

    public MultiNumberRedialCustomerWaitPool() {
        mapOutboundResultWaitSubmitCustomerPool = new HashMap<String, Map<String, MultiNumberRedialCustomer>>();
        mapShareBatchWaitStopCustomerPool = new HashMap<String, Map<String, MultiNumberRedialCustomer>>();

        mapTimeOutWaitPhoneConnectCustomerPool = new HashMap<Long, Map<String, MultiNumberRedialCustomer>>();
        mapTimeOutWaitScreenPopUpCustomerPool = new HashMap<Long, Map<String, MultiNumberRedialCustomer>>();
        mapTimeOutWaitOutboundResultCustomerPool = new HashMap<Long, Map<String, MultiNumberRedialCustomer>>();

        Date now =  new Date();
        earliestPhoneConnectTimeSlot = now.getTime()/ Constants.timeSlotSpan;
        earliestScreenPopUpTimeSlot = now.getTime()/ Constants.timeSlotSpan;
        earliestResultTimeSlot = now.getTime()/ Constants.timeSlotSpan;
    }

    public void add(String userId, MultiNumberRedialCustomer customerItem) {
        Map<String, MultiNumberRedialCustomer> mapWaitResultPool = mapOutboundResultWaitSubmitCustomerPool.get(userId);
        if (null == mapWaitResultPool) {
            mapWaitResultPool = new HashMap<String, MultiNumberRedialCustomer>();
            mapOutboundResultWaitSubmitCustomerPool.put(userId, mapWaitResultPool);
        }
        mapWaitResultPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        Map<String, MultiNumberRedialCustomer> mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(customerItem.getShareBatchId());
        if (null == mapWaitStopPool) {
            mapWaitStopPool = new HashMap<String, MultiNumberRedialCustomer>();
            mapShareBatchWaitStopCustomerPool.put(customerItem.getShareBatchId(), mapWaitStopPool);
        }
        mapWaitStopPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        if (MultiNumberRedialStateEnum.EXTRACTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            Map<String, MultiNumberRedialCustomer> mapWaitTimeOutPool = mapTimeOutWaitPhoneConnectCustomerPool.get(timeSlot);
            if (null == mapWaitTimeOutPool) {
                mapWaitTimeOutPool = new HashMap<String, MultiNumberRedialCustomer>();
                mapTimeOutWaitPhoneConnectCustomerPool.put(timeSlot, mapWaitTimeOutPool);
            }
            mapWaitTimeOutPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        } else if (MultiNumberRedialStateEnum.PHONECONNECTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            Map<String, MultiNumberRedialCustomer> mapWaitTimeOutPool = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot);
            if (null == mapWaitTimeOutPool) {
                mapWaitTimeOutPool = new HashMap<String, MultiNumberRedialCustomer>();
                mapTimeOutWaitScreenPopUpCustomerPool.put(timeSlot, mapWaitTimeOutPool);
            }
            mapWaitTimeOutPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(),
                    customerItem);

        } else if (MultiNumberRedialStateEnum.SCREENPOPUP.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            Map<String, MultiNumberRedialCustomer> mapWaitTimeOutPool = mapTimeOutWaitOutboundResultCustomerPool.get(timeSlot);
            if (null == mapWaitTimeOutPool) {
                mapWaitTimeOutPool = new HashMap<String, MultiNumberRedialCustomer>();
                mapTimeOutWaitOutboundResultCustomerPool.put(timeSlot, mapWaitTimeOutPool);
            }
            mapWaitTimeOutPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(),
                    customerItem);
        }

    }

    public void hidialerPhoneConnect(MultiNumberRedialCustomer customer, Date originModifyTime) {
        Long timeSlot = originModifyTime.getTime()/Constants.timeSlotSpan;
        Map<String, MultiNumberRedialCustomer> mapWaitTimeOutPool = mapTimeOutWaitPhoneConnectCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId());
            if (mapWaitTimeOutPool.isEmpty())
                mapTimeOutWaitPhoneConnectCustomerPool.remove(timeSlot);
        }

        Long timeSlot2 = customer.getModifyTime().getTime()/Constants.timeSlotSpan;
        Map<String, MultiNumberRedialCustomer> mapWaitTimeOutPool2 = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot2);
        if (null == mapWaitTimeOutPool2) {
            mapWaitTimeOutPool2 = new HashMap<String, MultiNumberRedialCustomer>();
            mapTimeOutWaitScreenPopUpCustomerPool.put(timeSlot2, mapWaitTimeOutPool2);
        }
        mapWaitTimeOutPool2.put(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId(), customer);
    }

    public void agentScreenPopUp(MultiNumberRedialCustomer customer, Date originModifyTime) {
        Long timeSlot = originModifyTime.getTime()/Constants.timeSlotSpan;
        Map<String, MultiNumberRedialCustomer> mapWaitTimeOutPool = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId());
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitScreenPopUpCustomerPool.remove(timeSlot);
            }
        }

        Long timeSlot2 = customer.getModifyTime().getTime()/Constants.timeSlotSpan;
        Map<String, MultiNumberRedialCustomer> mapWaitTimeOutPool2 = mapTimeOutWaitOutboundResultCustomerPool.get(timeSlot2);
        if (null == mapWaitTimeOutPool2) {
            mapWaitTimeOutPool2 = new HashMap<String, MultiNumberRedialCustomer>();
            mapTimeOutWaitOutboundResultCustomerPool.put(timeSlot2, mapWaitTimeOutPool2);
        }
        mapWaitTimeOutPool2.put(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId(), customer);
    }

    public MultiNumberRedialCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {

        MultiNumberRedialCustomer customerItem = removeWaitResultCustome(userId, bizId, importBatchId, customerId);
        if (null == customerItem)
            return customerItem;


        removeWaitStopCustomer(bizId, customerItem.getShareBatchId(), importBatchId, customerId);

        if (MultiNumberRedialStateEnum.EXTRACTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
            removeWaitPhoneConnectTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);
        } else if (MultiNumberRedialStateEnum.PHONECONNECTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
            removeWaitScreenPopUpTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);
        } else if (MultiNumberRedialStateEnum.SCREENPOPUP.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime() / Constants.timeSlotSpan;
            removeWaitResultTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);
        }

        return customerItem;
    }

    public MultiNumberRedialCustomer getWaitCustome(String userId, int bizId, String importBatchId, String customerId) {
        Map<String, MultiNumberRedialCustomer> mapWaitResultPool = mapOutboundResultWaitSubmitCustomerPool.get(userId);
        if (null == mapWaitResultPool)
            return null;

        MultiNumberRedialCustomer customerItem = mapWaitResultPool.get(bizId + importBatchId + customerId);
        return customerItem;
    }

    /**
     * 仅标注已经停止共享，不从等待池中移除。需要等待已拨打的结果。
     * @param shareBatchIds
     */
    public void markShareBatchStopFromCustomerWaitPool(int bizId, List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            Map<String, MultiNumberRedialCustomer> mapWaitStopPool;
            mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(shareBatchId);
            for (MultiNumberRedialCustomer item : mapWaitStopPool.values()) {
                item.setInvalid(true);
            }
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

    public void timeoutProc() {
        Date now =  new Date();
        Long curTimeSlot = now.getTime()/ Constants.timeSlotSpan;
        System.out.println("cur time slot : " + curTimeSlot);

        // HiDialer 呼通超时处理
        Long phoneConnectTimeoutTimeSlot = curTimeSlot - Constants.PhoneConnectTimeoutThreshold2/Constants.timeSlotSpan;
        while (earliestPhoneConnectTimeSlot < phoneConnectTimeoutTimeSlot) {
            Map<String, MultiNumberRedialCustomer> mapTimeSlotWaitTimeOutPool;
            mapTimeSlotWaitTimeOutPool =  mapTimeOutWaitPhoneConnectCustomerPool.remove(earliestPhoneConnectTimeSlot++);
            if (null == mapTimeSlotWaitTimeOutPool)
                continue;

            for (MultiNumberRedialCustomer customerItem : mapTimeSlotWaitTimeOutPool.values()) {
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    multiNumberOutboundDataManage.lostProc(customerItem, MultiNumberRedialStateEnum.HIDIALER_LOSS_WAIT_REDIAL);  // 呼损处理
                }

                removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }

        // 坐席弹屏 超时处理 ==> 呼损处理
        Long screenPopupTimeoutTimeSlot = curTimeSlot - Constants.ScreenPopUpTimeoutThreshold3/Constants.timeSlotSpan;
        while (earliestScreenPopUpTimeSlot < screenPopupTimeoutTimeSlot) {
            Map<String, MultiNumberRedialCustomer> mapTimeSlotWaitTimeOutPool;
            mapTimeSlotWaitTimeOutPool =  mapTimeOutWaitScreenPopUpCustomerPool.remove(earliestScreenPopUpTimeSlot++);
            if (null == mapTimeSlotWaitTimeOutPool)
                continue;

            for (MultiNumberRedialCustomer customerItem : mapTimeSlotWaitTimeOutPool.values()) {
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    multiNumberOutboundDataManage.lostProc(customerItem, MultiNumberRedialStateEnum.HIDIALER_LOSS_WAIT_REDIAL);  // 呼损处理
                }

                removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }

        // 坐席递交结果 超时处理
        Long resultTimeoutTimeSlot = curTimeSlot - Constants.ResultTimeoutThreshold4/Constants.timeSlotSpan;
        while (earliestResultTimeSlot < resultTimeoutTimeSlot) {
            Map<String, MultiNumberRedialCustomer> mapTimeSlotWaitTimeOutPool;
            mapTimeSlotWaitTimeOutPool =  mapTimeOutWaitOutboundResultCustomerPool.remove(earliestResultTimeSlot++);
            if (null == mapTimeSlotWaitTimeOutPool)
                continue;

            for (MultiNumberRedialCustomer customerItem : mapTimeSlotWaitTimeOutPool.values()) {
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    multiNumberOutboundDataManage.lostProc(customerItem, MultiNumberRedialStateEnum.LOSS_WAIT_REDIAL);  // 呼损处理
                }

                removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }
    }

    public void postProcess() {
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

    private MultiNumberRedialCustomer removeWaitResultCustome(String userId, int bizId, String importBatchId, String customerId) {
        MultiNumberRedialCustomer customerItem = null;

        Map<String, MultiNumberRedialCustomer> mapWaitResultPool = mapOutboundResultWaitSubmitCustomerPool.get(userId);
        if (null != mapWaitResultPool) {
            customerItem = mapWaitResultPool.remove(bizId + importBatchId + customerId);
            if (mapWaitResultPool.isEmpty())
                mapOutboundResultWaitSubmitCustomerPool.remove(userId);
        }
        return customerItem;
    }

    private void removeWaitStopCustomer(int bizId, String shareBatchId, String importBatchId, String customerId) {
        Map<String, MultiNumberRedialCustomer> mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(shareBatchId);
        if (null != mapWaitStopPool) {
            mapWaitStopPool.remove(bizId + importBatchId + customerId);
            if (mapWaitStopPool.isEmpty())
                mapShareBatchWaitStopCustomerPool.remove(shareBatchId);
        }
    }

    private void removeWaitPhoneConnectTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<String, MultiNumberRedialCustomer> mapWaitTimeOutPool = mapTimeOutWaitPhoneConnectCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(bizId + importBatchId + customerId);
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitPhoneConnectCustomerPool.remove(timeSlot);
            }
        }
    }

    private void removeWaitScreenPopUpTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<String, MultiNumberRedialCustomer> mapWaitTimeOutPool = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(bizId + importBatchId + customerId);
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitScreenPopUpCustomerPool.remove(timeSlot);
            }
        }
    }

    private void removeWaitResultTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<String, MultiNumberRedialCustomer> mapWaitTimeOutPool = mapTimeOutWaitOutboundResultCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(bizId + importBatchId + customerId);
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitOutboundResultCustomerPool.remove(timeSlot);
            }
        }
    }
}

