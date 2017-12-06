package hiapp.modules.dm.multinumberredialmode.bo;

import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * M6 多号码预测外呼
 * Hidialer 抽取数据，客户信息不需要按照共享批次分类，由于不存在访问权限问题
 */

public class MultiNumberRedialCustomerSharePool {

    @Autowired
    private DMBizMangeShare dmBizMangeShare;

    // 客户共享池
    // ShareBatchID <==> PriorityBlockingQueue<MultiNumberRedialCustomer>
    //Map<String, PriorityBlockingQueue<MultiNumberRedialCustomer>> mapPreseCustomerSharePool;
    //Map<String, PriorityBlockingQueue<MultiNumberRedialCustomer>> mapCustomerSharePool;

    PriorityBlockingQueue<MultiNumberRedialCustomer> mapPreseCustomerSharePool;
    PriorityBlockingQueue<MultiNumberRedialCustomer> mapCustomerSharePool;

    // 用于处理共享停止的客户池，共享批次维度，用于标注已经停止共享的客户
    // ShareBatchId <==> {ImportId + CustomerId <==> MultiNumberRedialCustomer}
    Map<String, Map<String, MultiNumberRedialCustomer>> mapShareBatchWaitStopCustomerPool;



    int bizId = 0;

    public MultiNumberRedialCustomerSharePool(int bizId) {

        this.bizId = bizId;

        //mapPreseCustomerSharePool = new HashMap<String, PriorityBlockingQueue<MultiNumberRedialCustomer>>();
        //mapCustomerSharePool = new HashMap<String, PriorityBlockingQueue<MultiNumberRedialCustomer>>();

        mapPreseCustomerSharePool = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, nextDialTimeComparator);
        mapCustomerSharePool = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, shareBatchBeginTimeComparator);

        mapShareBatchWaitStopCustomerPool = new HashMap<String, Map<String, MultiNumberRedialCustomer>>();
    }

    /*
    public MultiNumberRedialCustomer extractCustomer(String userId) {

        MultiNumberRedialCustomer shareDataItem = null;

        Date now = new Date();

        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);
        PriorityBlockingQueue<MultiNumberRedialCustomer> customerQueue = null;

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

    public MultiNumberRedialCustomer extractCustomer(String userId) {
        Date now = new Date();
        MultiNumberRedialCustomer shareDataItem = null;

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
    public void add(MultiNumberRedialCustomer customer) {
        PriorityBlockingQueue<MultiNumberRedialCustomer> queue;
        if (MultiNumberRedialStateEnum.PRESET_DIAL.equals(customer.getState())
            || MultiNumberRedialStateEnum.WAIT_REDIAL.equals(customer.getState()) ) {
            queue = mapPreseCustomerSharePool.get(customer.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, nextDialTimeComparator);
                mapPreseCustomerSharePool.put(customer.getShareBatchId(), queue);
            }

        } else {
            queue = mapCustomerSharePool.get(customer.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, shareBatchBeginTimeComparator);
                mapCustomerSharePool.put(customer.getShareBatchId(), queue);
            }
        }

        queue.put(customer);
    }
    */

    public void add(MultiNumberRedialCustomer customer) {
        PriorityBlockingQueue<MultiNumberRedialCustomer> queue;
        if (MultiNumberRedialStateEnum.PRESET_DIAL.equals(customer.getState())
            || MultiNumberRedialStateEnum.WAIT_REDIAL.equals(customer.getState()) ) {
            mapPreseCustomerSharePool.put(customer);
        } else {
            mapCustomerSharePool.put(customer);
        }

        Map<String, MultiNumberRedialCustomer> mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(customer.getShareBatchId());
        if (null == mapWaitStopPool) {
            mapWaitStopPool = new HashMap<String, MultiNumberRedialCustomer>();
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
            Map<String, MultiNumberRedialCustomer> mapWaitStopPool;
            mapWaitStopPool = mapShareBatchWaitStopCustomerPool.remove(shareBatchId);
            for (MultiNumberRedialCustomer item : mapWaitStopPool.values()) {
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
                                             Map<String, PriorityBlockingQueue<MultiNumberRedialCustomer>> shareBatchIdVsCustomerMap)  {
        PriorityBlockingQueue<MultiNumberRedialCustomer> queue;
        if (null != shareBatchIdVsCustomerMap) {
            for (String shareBatchId : shareBatchIds) {
                queue = shareBatchIdVsCustomerMap.remove(shareBatchId);
            }
        }
    }*/

    //////////////////////////////////////////////////////////

    private void removeFromShareBatchStopWaitPool(MultiNumberRedialCustomer customer) {
        Map<String, MultiNumberRedialCustomer> oneShareBatchPool = mapShareBatchWaitStopCustomerPool.get(customer.getShareBatchId());
        oneShareBatchPool.remove(customer.getImportBatchId() + customer.getCustomerId());
        if (oneShareBatchPool.isEmpty())
            mapShareBatchWaitStopCustomerPool.remove(customer.getShareBatchId());
    }

    //匿名Comparator实现
    private static Comparator<MultiNumberRedialCustomer> nextDialTimeComparator = new Comparator<MultiNumberRedialCustomer>() {

        @Override
        public int compare(MultiNumberRedialCustomer c1, MultiNumberRedialCustomer c2) {
            return (c1.getCurPresetDialTime().before(c2.getCurPresetDialTime())) ? 1 : -1;
        }
    };

    //匿名Comparator实现
    private static Comparator<MultiNumberRedialCustomer> shareBatchBeginTimeComparator = new Comparator<MultiNumberRedialCustomer>() {

        @Override
        public int compare(MultiNumberRedialCustomer c1, MultiNumberRedialCustomer c2) {
            return (c1.getShareBatchStartTime().before(c2.getShareBatchStartTime())) ? 1 : -1;
        }
    };

}
