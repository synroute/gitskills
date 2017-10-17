package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class CustomerSharePool {

    @Autowired
    private DMBizMangeShare dmBizMangeShare;

    // 客户共享池
    // ShareBatchID <==> PriorityBlockingQueue<MultiNumberCustomer>
    Map<String, PriorityBlockingQueue<MultiNumberCustomer>> mapPreseCustomerSharePool;
    Map<String, PriorityBlockingQueue<MultiNumberCustomer>> mapCustomerSharePool;


    int bizId = 0;

    public CustomerSharePool(int bizId) {
        this.bizId = bizId;
    }

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

    public void removeShareCustomer(List<String> shareBatchIds) {
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
    }

    //////////////////////////////////////////////////////////

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
