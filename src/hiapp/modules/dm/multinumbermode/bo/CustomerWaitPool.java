package hiapp.modules.dm.multinumbermode.bo;
import hiapp.modules.dm.Constants;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerWaitPool {

    int bizId = 0;

    public CustomerWaitPool(int bizId) {
        this.bizId = bizId;
    }

    public void add(String userId, MultiNumberCustomer customerItem) {
        Map<String, MultiNumberCustomer> mapWaitResultPool = mapWaitResultCustomerPool.get(userId);
        if (null == mapWaitResultPool) {
            mapWaitResultPool = new HashMap<String, MultiNumberCustomer>();
            mapWaitResultCustomerPool.put(userId, mapWaitResultPool);
        }
        mapWaitResultPool.put(customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        Long timeSlot = customerItem.getExtractTime().getTime()/Constants.timeSlotSpan;
        Map<String, MultiNumberCustomer> mapWaitTimeOutPool = mapWaitTimeOutCustomerPool.get(timeSlot);
        if (null == mapWaitTimeOutPool) {
            mapWaitTimeOutPool = new HashMap<String, MultiNumberCustomer>();
            mapWaitTimeOutCustomerPool.put(timeSlot, mapWaitTimeOutPool);
        }
        mapWaitTimeOutPool.put(customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);

        Map<String, MultiNumberCustomer> mapWaitStopPool = mapWaitStopCustomerPool.get(bizId + customerItem.getShareBatchId());
        if (null == mapWaitStopPool) {
            mapWaitStopPool = new HashMap<String, MultiNumberCustomer>();
            mapWaitStopCustomerPool.put(bizId + customerItem.getShareBatchId(), mapWaitStopPool);
        }
        mapWaitStopPool.put(customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);
    }

    public MultiNumberCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {

        MultiNumberCustomer customerItem = removeWaitResultCustome(userId, bizId, importBatchId, customerId);
        if (null == customerItem)
            return customerItem;

        Long timeSlot = customerItem.getExtractTime().getTime()/ Constants.timeSlotSpan;
        removeWaitTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);

        removeWaitStopCustomer(bizId, customerItem.getShareBatchId(), importBatchId, customerId);

        return customerItem;
    }

    public MultiNumberCustomer getWaitCustome(String userId, int bizId, String importBatchId, String customerId) {
        Map<String, MultiNumberCustomer> mapWaitResultPool = mapWaitResultCustomerPool.get(userId);
        if (null == mapWaitResultPool)
            return null;

        MultiNumberCustomer customerItem = mapWaitResultPool.get(importBatchId + customerId);
        return customerItem;
    }

    /**
     * 仅标注已经停止共享，不从等待池中移除。需要等待已拨打的结果。
     * @param shareBatchIds
     */
    public void markShareBatchStopFromCustomerWaitPool(List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            Map<String, MultiNumberCustomer> mapWaitStopPool;
            mapWaitStopPool = mapWaitStopCustomerPool.get(shareBatchId);
            for (MultiNumberCustomer item : mapWaitStopPool.values()) {
                item.setInvalid(true);
            }
        }
    }

    private MultiNumberCustomer removeWaitResultCustome(String userId, int bizId, String importBatchId, String customerId) {
        MultiNumberCustomer customerItem = null;

        Map<String, MultiNumberCustomer> mapWaitResultPool = mapWaitResultCustomerPool.get(userId);
        if (null != mapWaitResultPool) {
            customerItem = mapWaitResultPool.remove(importBatchId + customerId);
            if (mapWaitResultPool.isEmpty())
                mapWaitResultCustomerPool.remove(userId);
        }
        return customerItem;
    }

    private void removeWaitTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        Map<String, MultiNumberCustomer> mapWaitTimeOutPool = mapWaitTimeOutCustomerPool.get(timeSlot);
        if (null != mapWaitTimeOutPool) {
            mapWaitTimeOutPool.remove(bizId + importBatchId + customerId);
            if (mapWaitTimeOutPool.isEmpty())
                mapWaitTimeOutCustomerPool.remove(timeSlot);
        }
    }

    private void removeWaitStopCustomer(int bizId, String shareBatchId, String importBatchId, String customerId) {
        Map<String, MultiNumberCustomer> mapWaitStopPool = mapWaitStopCustomerPool.get(bizId + shareBatchId);
        if (null != mapWaitStopPool) {
            mapWaitStopPool.remove(importBatchId + customerId);
            if (mapWaitStopPool.isEmpty())
                mapWaitStopCustomerPool.remove(bizId + shareBatchId);
        }
    }



    // 等待拨打结果的客户池，坐席人员维度
    // UserID <==> {ImportID + CustomerID <==> MultiNumberCustomer}
    Map<String, Map<String, MultiNumberCustomer>> mapWaitResultCustomerPool;

    // 等待拨打超时的客户池，抽取时间的分钟SLOT维度
    // 分钟Slot <==> {ImportId + CustomerId <==> MultiNumberCustomer}
    Map<Long, Map<String, MultiNumberCustomer>> mapWaitTimeOutCustomerPool;

    // 等待共享停止的客户池，共享批次维度，用于标注已经停止共享的客户
    // ShareBatchId <==> {ImportId + CustomerId <==> MultiNumberCustomer}
    Map<String, Map<String, MultiNumberCustomer>> mapWaitStopCustomerPool;


}
