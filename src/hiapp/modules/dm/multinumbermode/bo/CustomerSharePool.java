package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class CustomerSharePool {

    // 客户共享池
    // BizID <==> {ShareBatchID <==> PriorityBlockingQueue<SingleNumberModeShareCustomerItem>}
    Map<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>> mapPreseCustomerSharePool;
    Map<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>> mapCustomerSharePool;

    

}
