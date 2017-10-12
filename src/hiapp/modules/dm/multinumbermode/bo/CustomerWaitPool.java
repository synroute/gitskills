package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;

import java.util.Map;

public class CustomerWaitPool {

    int bizId = 0;

    public CustomerWaitPool(int bizId) {
        this.bizId = bizId;
    }

    public void add(String userId, MultiNumberCustomer customer) {

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
