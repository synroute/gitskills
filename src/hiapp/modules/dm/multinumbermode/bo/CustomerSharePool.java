package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.multinumberredialmode.bo.MultiNumberRedialCustomer;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * M6 多号码预测外呼
 * Hidialer 抽取数据，客户信息不需要按照共享批次分类，由于不存在访问权限问题
 */

public class CustomerSharePool {

    @Autowired
    private DMBizMangeShare dmBizMangeShare;

    // 客户共享池
    // ShareBatchID <==> PriorityBlockingQueue<MultiNumberCustomer>
    //Map<String, PriorityBlockingQueue<MultiNumberCustomer>> mapPreseCustomerSharePool;
    //Map<String, PriorityBlockingQueue<MultiNumberCustomer>> mapCustomerSharePool;

    PriorityBlockingQueue<MultiNumberCustomer> mapPreseCustomerSharePool;
    PriorityBlockingQueue<MultiNumberCustomer> mapCustomerSharePool;

    // 用于处理共享停止的客户池，共享批次维度，用于标注已经停止共享的客户
    // BizId + ShareBatchId <==> {BizId + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<String, Map<String, MultiNumberCustomer>> mapShareBatchWaitStopCustomerPool;



    int bizId = 0;

    public CustomerSharePool(int bizId) {

        this.bizId = bizId;

        //mapPreseCustomerSharePool = new HashMap<String, PriorityBlockingQueue<MultiNumberCustomer>>();
        //mapCustomerSharePool = new HashMap<String, PriorityBlockingQueue<MultiNumberCustomer>>();

        mapPreseCustomerSharePool = new PriorityBlockingQueue<MultiNumberCustomer>(1, nextDialTimeComparator);
        mapCustomerSharePool = new PriorityBlockingQueue<MultiNumberCustomer>(1, shareBatchBeginTimeComparator);

        mapShareBatchWaitStopCustomerPool = new HashMap<String, Map<String, MultiNumberCustomer>>();
    }

    public MultiNumberCustomer extractCustomer(String userId) {
        Date now = new Date();
        MultiNumberCustomer shareDataItem = peekNextValidCustomer(mapPreseCustomerSharePool);
        if (null != shareDataItem && shareDataItem.getCurPresetDialTime().before(now)) {
            shareDataItem = mapPreseCustomerSharePool.poll();
            return shareDataItem;
        }

        return retrieveNextValideCustomer(mapCustomerSharePool);
    }

    /*
    public void add(MultiNumberCustomer customer) {
        PriorityBlockingQueue<MultiNumberCustomer> queue;
        if (MultiNumberPredictStateEnum.PRESET_DIAL.equals(customer.getState())
            || MultiNumberPredictStateEnum.WAIT_REDIAL.equals(customer.getState()) ) {
            queue = mapPreseCustomerSharePool.get(customer.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberCustomer>(1, nextDialTimeComparator);
                mapPreseCustomerSharePool.put(customer.getShareBatchId(), queue);
            }

        } else {
            queue = mapCustomerSharePool.get(customer.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberCustomer>(1, shareBatchBeginTimeComparator);
                mapCustomerSharePool.put(customer.getShareBatchId(), queue);
            }
        }

        queue.put(customer);
    }
    */

    public void add(MultiNumberCustomer customer) {
        PriorityBlockingQueue<MultiNumberCustomer> queue;
        if (MultiNumberPredictStateEnum.PRESET_DIAL.equals(customer.getState())
            || MultiNumberPredictStateEnum.WAIT_REDIAL.equals(customer.getState()) ) {
            mapPreseCustomerSharePool.put(customer);
        } else {
            mapCustomerSharePool.put(customer);
        }

        Map<String, MultiNumberCustomer> mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(customer.getShareToken());
        if (null == mapWaitStopPool) {
            mapWaitStopPool = new HashMap<String, MultiNumberCustomer>();
            mapShareBatchWaitStopCustomerPool.put(customer.getShareToken(), mapWaitStopPool);
        }
        mapWaitStopPool.put(customer.getCustomerToken(), customer);
    }

    /**
     * 仅标注已经停止共享，由于没办法直接从PriorityBlockingQueue里面移除。
     * @param shareBatchIds
     */
    public void stopShareBatch(List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            Map<String, MultiNumberCustomer> mapWaitStopPool;
            mapWaitStopPool = mapShareBatchWaitStopCustomerPool.remove(bizId + shareBatchId);
            for (MultiNumberCustomer item : mapWaitStopPool.values()) {
                item.setInvalid(true);
            }
        }
    }

    /*
    public void stopShareBatch(List<String> shareBatchIds) {
        removeFromCustomerSharePool(shareBatchIds, mapPreseCustomerSharePool);
        removeFromCustomerSharePool(shareBatchIds, mapCustomerSharePool);
    }

    private void removeFromCustomerSharePool(List<String> shareBatchIds,
                                             Map<String, PriorityBlockingQueue<MultiNumberCustomer>> shareBatchIdVsCustomerMap)  {
        PriorityBlockingQueue<MultiNumberCustomer> queue;
        if (null != shareBatchIdVsCustomerMap) {
            for (String shareBatchId : shareBatchIds) {
                queue = shareBatchIdVsCustomerMap.remove(shareBatchId);
            }
        }
    }*/

    public List<MultiNumberCustomer> cancelShare(List<CustomerBasic> customerBasicList) {
        List<MultiNumberCustomer> customerList = new ArrayList<MultiNumberCustomer>();
        for (CustomerBasic customerBasic : customerBasicList) {
            Map<String, MultiNumberCustomer> oneShareBatchCustomerPool = mapShareBatchWaitStopCustomerPool.get(customerBasic.getSourceToken());
            if (null == oneShareBatchCustomerPool || oneShareBatchCustomerPool.isEmpty()) {
                mapShareBatchWaitStopCustomerPool.remove(customerBasic.getSourceToken());
                continue;
            }

            MultiNumberCustomer customer = oneShareBatchCustomerPool.remove(customerBasic.getCustomerToken());
            if (null == customer)
                continue;

            customer.setInvalid(true);
            customerList.add(customer);
        }

        return customerList;
    }


    //////////////////////////////////////////////////////////

    private MultiNumberCustomer peekNextValidCustomer(PriorityBlockingQueue<MultiNumberCustomer> customerSharePool) {
        while (!customerSharePool.isEmpty()) {
            MultiNumberCustomer shareDataItem = customerSharePool.peek();
            if (shareDataItem.getInvalid()) {
                customerSharePool.poll();  // 丢弃 已经停止共享的客户
                continue;
            }

            return shareDataItem;
            //break;   // 第一个有效客户还未到拨打时间，就跳出
        }

        return null;
    }

    private MultiNumberCustomer retrieveNextValideCustomer(PriorityBlockingQueue<MultiNumberCustomer> customerSharePool) {
        while (!customerSharePool.isEmpty()) {
            MultiNumberCustomer shareDataItem = customerSharePool.poll();
            if (!shareDataItem.getInvalid())
                return shareDataItem;
        }

        return null;
    }

    //匿名Comparator实现
    private static Comparator<MultiNumberCustomer> nextDialTimeComparator = new Comparator<MultiNumberCustomer>() {

        @Override
        public int compare(MultiNumberCustomer c1, MultiNumberCustomer c2) {
            return (c1.getCurPresetDialTime().before(c2.getCurPresetDialTime())) ? 1 : -1;
        }
    };

    //匿名Comparator实现
    private static Comparator<MultiNumberCustomer> shareBatchBeginTimeComparator = new Comparator<MultiNumberCustomer>() {

        @Override
        public int compare(MultiNumberCustomer c1, MultiNumberCustomer c2) {
            return (c1.getShareBatchStartTime().before(c2.getShareBatchStartTime())) ? 1 : -1;
        }
    };

}
