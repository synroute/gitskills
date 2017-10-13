package hiapp.modules.dm.multinumbermode.bo;
import hiapp.modules.dm.Constants;

import java.util.HashMap;
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
