package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

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
    // ShareBatchId <==> {ImportId + CustomerId <==> MultiNumberCustomer}
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

    /*
    public MultiNumberCustomer extractCustomer(String userId) {

        MultiNumberCustomer shareDataItem = null;

        Date now = new Date();

        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);
        PriorityBlockingQueue<MultiNumberCustomer> customerQueue = null;

        // TODO 目前取得就走了，其实可以PEEK遍后比较拨打时间，确定先取那个客户
        for (String shareBatchId : shareBatchIdList) {
            customerQueue = mapPreseCustomerSharePool.get(shareBatchId);
            if (null == customerQueue)
                continue;

            shareDataItem = customerQueue.peek();
            if (null == shareDataItem) {
                mapPreseCustomerSharePool.remove(shareBatchId);
                continue;
            }

            if (shareDataItem.getCurPresetDialTime().before(now)) {
                shareDataItem = customerQueue.poll();
                break;
            }
        }

        if (null == shareDataItem) {
            for (String shareBatchId : shareBatchIdList) {
                customerQueue = mapCustomerSharePool.get(shareBatchId);
                if (null == customerQueue)
                    continue;

                shareDataItem = customerQueue.poll();
                if (null != shareDataItem)
                    break;

                mapCustomerSharePool.remove(shareBatchId);
            }
        }

        return shareDataItem;
    }
    */

    public MultiNumberCustomer extractCustomer(String userId) {
        Date now = new Date();
        MultiNumberCustomer shareDataItem = null;

        while (true) {
            shareDataItem = mapPreseCustomerSharePool.peek();
            if (null == shareDataItem)
                break;

            if (shareDataItem.getInvalid()) {
                mapPreseCustomerSharePool.poll();  // 扔掉已经停止共享批次的客户
                removeFromShareBatchStopWaitPool(shareDataItem);
                continue;
            }

            if (null != shareDataItem && shareDataItem.getCurPresetDialTime().before(now)) {
                shareDataItem = mapPreseCustomerSharePool.poll();
                removeFromShareBatchStopWaitPool(shareDataItem);
                return shareDataItem;
            }

            break;   //NOTE: 取不到合适的，就跳出
        }

        while (true) {
            shareDataItem = mapCustomerSharePool.poll();
            if (null == shareDataItem)
                break;

            removeFromShareBatchStopWaitPool(shareDataItem);

            if (shareDataItem.getInvalid())
                continue;

            return shareDataItem;
        }

        return shareDataItem;
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

        Map<String, MultiNumberCustomer> mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(customer.getShareBatchId());
        if (null == mapWaitStopPool) {
            mapWaitStopPool = new HashMap<String, MultiNumberCustomer>();
            mapShareBatchWaitStopCustomerPool.put(customer.getShareBatchId(), mapWaitStopPool);
        }
        mapWaitStopPool.put(customer.getImportBatchId() + customer.getCustomerId(), customer);
    }

    /**
     * 仅标注已经停止共享，由于没办法直接从PriorityBlockingQueue里面移除。
     * @param shareBatchIds
     */
    public void stopShareBatch(List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            Map<String, MultiNumberCustomer> mapWaitStopPool;
            mapWaitStopPool = mapShareBatchWaitStopCustomerPool.remove(shareBatchId);
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

    //////////////////////////////////////////////////////////

    private void removeFromShareBatchStopWaitPool(MultiNumberCustomer customer) {
        Map<String, MultiNumberCustomer> oneShareBatchPool = mapShareBatchWaitStopCustomerPool.get(customer.getShareBatchId());
        oneShareBatchPool.remove(customer.getImportBatchId() + customer.getCustomerId());
        if (oneShareBatchPool.isEmpty())
            mapShareBatchWaitStopCustomerPool.remove(customer.getShareBatchId());
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
