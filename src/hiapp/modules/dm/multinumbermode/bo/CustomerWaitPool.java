package hiapp.modules.dm.multinumbermode.bo;
import hiapp.modules.dm.Constants;
import hiapp.modules.dm.multinumbermode.MultiNumberOutboundDataManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomerWaitPool {

    @Autowired
    MultiNumberOutboundDataManage multiNumberOutboundDataManage;


    // 等待拨打结果的客户池，坐席人员维度
    // UserID <==> {BizID + ImportID + CustomerID <==> MultiNumberCustomer}
    Map<String, Map<String, MultiNumberCustomer>> mapOutboundResultWaitSubmitCustomerPool;

    // 等待共享停止的客户池，共享批次维度，用于标注已经停止共享的客户
    // ShareBatchId <==> {BizId + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<String, Map<String, MultiNumberCustomer>> mapShareBatchWaitStopCustomerPool;

    // 等待hidialer呼通超时的客户池，抽取时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<Long, Map<String, MultiNumberCustomer>> mapTimeOutWaitPhoneConnectCustomerPool;

    // 等待坐席弹屏超时的客户池，hidialer呼通时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<Long, Map<String, MultiNumberCustomer>> mapTimeOutWaitScreenPopUpCustomerPool;

    // 等待坐席拨打结果超时的客户池，坐席弹屏时间的分钟SLOT维度
    // 分钟Slot <==> {BizID + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<Long, Map<String, MultiNumberCustomer>> mapTimeOutWaitOutboundResultCustomerPool;

    Long earliestPhoneConnectTimeSlot;
    Long earliestScreenPopUpTimeSlot;
    Long earliestResultTimeSlot;

    public CustomerWaitPool() {
        Date now =  new Date();
        earliestPhoneConnectTimeSlot = now.getTime()/ Constants.timeSlotSpan;
        earliestScreenPopUpTimeSlot = now.getTime()/ Constants.timeSlotSpan;
        earliestResultTimeSlot = now.getTime()/ Constants.timeSlotSpan;

        mapOutboundResultWaitSubmitCustomerPool = new HashMap<String, Map<String, MultiNumberCustomer>>();
        mapShareBatchWaitStopCustomerPool = new HashMap<String, Map<String, MultiNumberCustomer>>();

        mapTimeOutWaitPhoneConnectCustomerPool = new HashMap<Long, Map<String, MultiNumberCustomer>>();
        mapTimeOutWaitScreenPopUpCustomerPool = new HashMap<Long, Map<String, MultiNumberCustomer>>();
        mapTimeOutWaitOutboundResultCustomerPool = new HashMap<Long, Map<String, MultiNumberCustomer>>();
    }


    public void add(String userId, MultiNumberCustomer customerItem) {
        Map<String, MultiNumberCustomer> mapWaitResultPool = mapOutboundResultWaitSubmitCustomerPool.get(userId);
        if (null == mapWaitResultPool) {
            mapWaitResultPool = new HashMap<String, MultiNumberCustomer>();
            mapOutboundResultWaitSubmitCustomerPool.put(userId, mapWaitResultPool);
        }
        mapWaitResultPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        Map<String, MultiNumberCustomer> mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(customerItem.getShareBatchId());
        if (null == mapWaitStopPool) {
            mapWaitStopPool = new HashMap<String, MultiNumberCustomer>();
            mapShareBatchWaitStopCustomerPool.put(customerItem.getShareBatchId(), mapWaitStopPool);
        }
        mapWaitStopPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        if (MultiNumberPredictStateEnum.EXTRACTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            Map<String, MultiNumberCustomer> mapWaitTimeOutPool = mapTimeOutWaitPhoneConnectCustomerPool.get(timeSlot);
            if (null == mapWaitTimeOutPool) {
                mapWaitTimeOutPool = new HashMap<String, MultiNumberCustomer>();
                mapTimeOutWaitPhoneConnectCustomerPool.put(timeSlot, mapWaitTimeOutPool);
            }
            mapWaitTimeOutPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        } else if (MultiNumberPredictStateEnum.PHONECONNECTED.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            Map<String, MultiNumberCustomer> mapWaitTimeOutPool = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot);
            if (null == mapWaitTimeOutPool) {
                mapWaitTimeOutPool = new HashMap<String, MultiNumberCustomer>();
                mapTimeOutWaitScreenPopUpCustomerPool.put(timeSlot, mapWaitTimeOutPool);
            }
            mapWaitTimeOutPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(),
                    customerItem);

        } else if (MultiNumberPredictStateEnum.SCREENPOPUP.equals(customerItem.getState())) {
            Long timeSlot = customerItem.getModifyTime().getTime()/Constants.timeSlotSpan;
            Map<String, MultiNumberCustomer> mapWaitTimeOutPool = mapTimeOutWaitOutboundResultCustomerPool.get(timeSlot);
            if (null == mapWaitTimeOutPool) {
                mapWaitTimeOutPool = new HashMap<String, MultiNumberCustomer>();
                mapTimeOutWaitOutboundResultCustomerPool.put(timeSlot, mapWaitTimeOutPool);
            }
            mapWaitTimeOutPool.put(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId(),
                    customerItem);
        }

    }

    public void hidialerPhoneConnect(MultiNumberCustomer customer, Date originModifyTime) {
        Long timeSlot = originModifyTime.getTime()/Constants.timeSlotSpan;
        Map<String, MultiNumberCustomer> mapWaitTimeOutPool = mapTimeOutWaitPhoneConnectCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId());
            if (mapWaitTimeOutPool.isEmpty())
                mapTimeOutWaitPhoneConnectCustomerPool.remove(timeSlot);
        }

        Long timeSlot2 = customer.getModifyTime().getTime()/Constants.timeSlotSpan;
        Map<String, MultiNumberCustomer> mapWaitTimeOutPool2 = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot2);
        if (null == mapWaitTimeOutPool2) {
            mapWaitTimeOutPool2 = new HashMap<String, MultiNumberCustomer>();
            mapTimeOutWaitScreenPopUpCustomerPool.put(timeSlot2, mapWaitTimeOutPool2);
        }
        mapWaitTimeOutPool2.put(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId(), customer);
    }

    public void agentScreenPopUp(MultiNumberCustomer customer, Date originModifyTime) {
        Long timeSlot = originModifyTime.getTime()/Constants.timeSlotSpan;
        Map<String, MultiNumberCustomer> mapWaitTimeOutPool = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId());
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitScreenPopUpCustomerPool.remove(timeSlot);
            }
        }

        Long timeSlot2 = customer.getModifyTime().getTime()/Constants.timeSlotSpan;
        Map<String, MultiNumberCustomer> mapWaitTimeOutPool2 = mapTimeOutWaitOutboundResultCustomerPool.get(timeSlot2);
        if (null == mapWaitTimeOutPool2) {
            mapWaitTimeOutPool2 = new HashMap<String, MultiNumberCustomer>();
            mapTimeOutWaitOutboundResultCustomerPool.put(timeSlot2, mapWaitTimeOutPool2);
        }
        mapWaitTimeOutPool2.put(customer.getBizId() + customer.getImportBatchId() + customer.getCustomerId(), customer);
    }

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

    public MultiNumberCustomer getWaitCustome(String userId, int bizId, String importBatchId, String customerId) {
        Map<String, MultiNumberCustomer> mapWaitResultPool = mapOutboundResultWaitSubmitCustomerPool.get(userId);
        if (null == mapWaitResultPool)
            return null;

        MultiNumberCustomer customerItem = mapWaitResultPool.get(bizId + importBatchId + customerId);
        return customerItem;
    }

    /**
     * 仅标注已经停止共享，不从等待池中移除。需要等待已拨打的结果。
     * @param shareBatchIds
     */
    public void markShareBatchStopFromCustomerWaitPool(int bizId, List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            Map<String, MultiNumberCustomer> mapWaitStopPool;
            mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(shareBatchId);
            for (MultiNumberCustomer item : mapWaitStopPool.values()) {
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
            Map<String, MultiNumberCustomer> mapTimeSlotWaitTimeOutPool;
            mapTimeSlotWaitTimeOutPool =  mapTimeOutWaitPhoneConnectCustomerPool.get(earliestPhoneConnectTimeSlot++);
            if (null == mapTimeSlotWaitTimeOutPool)
                continue;

            for (MultiNumberCustomer customerItem : mapTimeSlotWaitTimeOutPool.values()) {
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    multiNumberOutboundDataManage.addCustomerToSharePool(customerItem);
                }

                removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }

        // 坐席弹屏 超时处理 ==> 呼损处理
        while (earliestScreenPopUpTimeSlot < phoneConnectTimeoutTimeSlot) {
            Map<String, MultiNumberCustomer> mapTimeSlotWaitTimeOutPool;
            mapTimeSlotWaitTimeOutPool =  mapTimeOutWaitScreenPopUpCustomerPool.get(earliestScreenPopUpTimeSlot++);
            if (null == mapTimeSlotWaitTimeOutPool)
                continue;

            for (MultiNumberCustomer customerItem : mapTimeSlotWaitTimeOutPool.values()) {
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    multiNumberOutboundDataManage.lostProc(customerItem);  // 呼损处理
                    multiNumberOutboundDataManage.addCustomerToSharePool(customerItem);
                }

                removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }

        // 坐席递交结果 超时处理
        while (earliestResultTimeSlot < phoneConnectTimeoutTimeSlot) {
            Map<String, MultiNumberCustomer> mapTimeSlotWaitTimeOutPool;
            mapTimeSlotWaitTimeOutPool =  mapTimeOutWaitOutboundResultCustomerPool.get(earliestResultTimeSlot++);
            if (null == mapTimeSlotWaitTimeOutPool)
                continue;

            for (MultiNumberCustomer customerItem : mapTimeSlotWaitTimeOutPool.values()) {
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    multiNumberOutboundDataManage.addCustomerToSharePool(customerItem);
                }

                removeWaitResultCustome(customerItem.getModifyUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }
    }

    private MultiNumberCustomer removeWaitResultCustome(String userId, int bizId, String importBatchId, String customerId) {
        MultiNumberCustomer customerItem = null;

        Map<String, MultiNumberCustomer> mapWaitResultPool = mapOutboundResultWaitSubmitCustomerPool.get(userId);
        if (null != mapWaitResultPool) {
            customerItem = mapWaitResultPool.remove(bizId + importBatchId + customerId);
            if (mapWaitResultPool.isEmpty())
                mapOutboundResultWaitSubmitCustomerPool.remove(userId);
        }
        return customerItem;
    }

    private void removeWaitStopCustomer(int bizId, String shareBatchId, String importBatchId, String customerId) {
        Map<String, MultiNumberCustomer> mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(shareBatchId);
        if (null != mapWaitStopPool) {
            mapWaitStopPool.remove(bizId + importBatchId + customerId);
            if (mapWaitStopPool.isEmpty())
                mapShareBatchWaitStopCustomerPool.remove(shareBatchId);
        }
    }

    private void removeWaitPhoneConnectTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<String, MultiNumberCustomer> mapWaitTimeOutPool = mapTimeOutWaitPhoneConnectCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(bizId + importBatchId + customerId);
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitPhoneConnectCustomerPool.remove(timeSlot);
            }
        }
    }

    private void removeWaitScreenPopUpTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<String, MultiNumberCustomer> mapWaitTimeOutPool = mapTimeOutWaitScreenPopUpCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(bizId + importBatchId + customerId);
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitScreenPopUpCustomerPool.remove(timeSlot);
            }
        }
    }

    private void removeWaitResultTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<String, MultiNumberCustomer> mapWaitTimeOutPool = mapTimeOutWaitOutboundResultCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(bizId + importBatchId + customerId);
            if (mapWaitTimeOutPool.isEmpty()) {
                mapTimeOutWaitOutboundResultCustomerPool.remove(timeSlot);
            }
        }
    }
}

