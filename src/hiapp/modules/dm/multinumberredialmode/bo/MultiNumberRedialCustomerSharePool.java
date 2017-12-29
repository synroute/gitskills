package hiapp.modules.dm.multinumberredialmode.bo;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * M4 多号码重拨外呼
 * 客户信息需要按照共享批次分类，由于存在访问权限问题
 */

public class MultiNumberRedialCustomerSharePool {

    // 客户共享池
    //BizId + ShareBatchID <==> PriorityBlockingQueue<MultiNumberRedialCustomer>
    Map<String, PriorityBlockingQueue<MultiNumberRedialCustomer>> mapPreseCustomerSharePool;
    Map<String, PriorityBlockingQueue<MultiNumberRedialCustomer>> mapCustomerSharePool;

    // 用于处理共享停止/取消的客户池，共享批次维度，用于标注已经停止/取消共享的客户
    // BizId + ShareBatchId <==> {BizId + ImportId + CustomerId <==> MultiNumberRedialCustomer}
    Map<String, Map<String, MultiNumberRedialCustomer>> mapShareBatchWaitStopCustomerPool;


    int bizId = 0;

    public MultiNumberRedialCustomerSharePool(int bizId) {

        this.bizId = bizId;

        mapPreseCustomerSharePool = new HashMap<String, PriorityBlockingQueue<MultiNumberRedialCustomer>>();
        mapCustomerSharePool = new HashMap<String, PriorityBlockingQueue<MultiNumberRedialCustomer>>();

        //mapPreseCustomerSharePool = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, nextDialTimeComparator);
        //mapCustomerSharePool = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, shareBatchBeginTimeComparator);

        mapShareBatchWaitStopCustomerPool = new HashMap<String, Map<String, MultiNumberRedialCustomer>>();
    }

    public MultiNumberRedialCustomer extractCustomer(String userId, List<String> shareBatchIdList) {
        Date now = new Date();
        MultiNumberRedialCustomer shareDataItem = null;

        shareDataItem = retrievePresetCustomer(shareBatchIdList, mapPreseCustomerSharePool);
        if (null == shareDataItem)
            shareDataItem = retrieveGeneralCustomer(shareBatchIdList, mapCustomerSharePool);


        if (null != shareDataItem)
            removeFromShareBatchStopWaitPool(shareDataItem);

        return shareDataItem;
    }


    /**
     * 注意：MultiNumberRedialStateEnum.WAIT_NEXT_DAY_DIAL 放入非预约队列
     *
     */
    public void add(MultiNumberRedialCustomer customer) {
        PriorityBlockingQueue<MultiNumberRedialCustomer> queue;
        if (MultiNumberRedialStateEnum.PRESET_DIAL.equals(customer.getState())
            || MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL.equals(customer.getState())) {
            queue = mapPreseCustomerSharePool.get(customer.getShareToken());
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, nextDialTimeComparator);
                mapPreseCustomerSharePool.put(customer.getShareToken(), queue);
            }

        } else {
            queue = mapCustomerSharePool.get(customer.getShareToken());
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, shareBatchBeginTimeComparator);
                mapCustomerSharePool.put(customer.getShareToken(), queue);
            }
        }

        queue.put(customer);

        // step 2 : 放入 等停止/取消的客户池
        Map<String, MultiNumberRedialCustomer> mapWaitStopPool = mapShareBatchWaitStopCustomerPool.get(customer.getShareToken());
        if (null == mapWaitStopPool) {
            mapWaitStopPool = new HashMap<String, MultiNumberRedialCustomer>();
            mapShareBatchWaitStopCustomerPool.put(customer.getShareToken(), mapWaitStopPool);
        }
        mapWaitStopPool.put(customer.getCustomerToken(), customer);

    }

    /**
     * 仅标注已经停止共享，由于没办法直接从PriorityBlockingQueue里面移除。
     * @param shareBatchIds
     */
    /*public void stopShareBatch(List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            Map<String, MultiNumberRedialCustomer> mapWaitStopPool;
            mapWaitStopPool = mapShareBatchWaitStopCustomerPool.remove(shareBatchId);
            for (MultiNumberRedialCustomer item : mapWaitStopPool.values()) {
                item.setInvalid(true);
            }
        }
    }*/


    public void stopShareBatch(List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            mapPreseCustomerSharePool.remove(bizId + shareBatchId);
            mapCustomerSharePool.remove(bizId + shareBatchId);
            mapShareBatchWaitStopCustomerPool.remove(bizId + shareBatchId);
        }
    }

    public List<MultiNumberRedialCustomer> cancelShare(List<CustomerBasic> customerBasicList) {
        List<MultiNumberRedialCustomer> customerList = new ArrayList<MultiNumberRedialCustomer>();
        for (CustomerBasic customerBasic : customerBasicList) {
            Map<String, MultiNumberRedialCustomer> oneShareBatchCustomerPool = mapShareBatchWaitStopCustomerPool.get(customerBasic.getSourceToken());
            if (null == oneShareBatchCustomerPool || oneShareBatchCustomerPool.isEmpty()) {
                mapShareBatchWaitStopCustomerPool.remove(customerBasic.getSourceToken());
                continue;
            }

            MultiNumberRedialCustomer customer = oneShareBatchCustomerPool.remove(customerBasic.getCustomerToken());
            if (null == customer)
                continue;

            customer.setInvalid(true);
            customerList.add(customer);
        }

        return customerList;
    }

    ///////////////////////////////////////////////////////////////////

    private MultiNumberRedialCustomer retrievePresetCustomer(List<String> shareBatchIdList,
                          Map<String, PriorityBlockingQueue<MultiNumberRedialCustomer>> preseCustomerSharePool) {

        Date now = new Date();

        MultiNumberRedialCustomer shareDataItem = null;
        for (String shareBatchId : shareBatchIdList) {
            PriorityBlockingQueue<MultiNumberRedialCustomer> presetCustomerPool = preseCustomerSharePool.get(bizId + shareBatchId);
            if (null == presetCustomerPool || presetCustomerPool.isEmpty()) {
                preseCustomerSharePool.remove(bizId + shareBatchId);
                continue;
            }

            shareDataItem = presetCustomerPool.peek();

            if (shareDataItem.getInvalid()) {
                presetCustomerPool.poll();  // 丢弃 作废的客户
                continue;
            }

            if (shareDataItem.getCurPresetDialTime().before(now)) {
                shareDataItem = presetCustomerPool.poll();
                return shareDataItem;
            }
        }

        return null;
    }

    private MultiNumberRedialCustomer retrieveGeneralCustomer(List<String> shareBatchIdList,
                      Map<String, PriorityBlockingQueue<MultiNumberRedialCustomer>> customerSharePool) {
        MultiNumberRedialCustomer shareDataItem = null;
        for (String shareBatchId : shareBatchIdList) {
            PriorityBlockingQueue<MultiNumberRedialCustomer> customerPool = customerSharePool.get(bizId + shareBatchId);
            if (null == customerPool || customerPool.isEmpty()) {
                customerSharePool.remove(bizId + shareBatchId);
                continue;
            }

            shareDataItem = customerPool.poll();

            if (shareDataItem.getInvalid()) {
                continue;   // 丢弃 作废的客户
            }

            return shareDataItem;
        }

        return null;
    }

    private void removeFromShareBatchStopWaitPool(MultiNumberRedialCustomer customer) {
        Map<String, MultiNumberRedialCustomer> oneShareBatchPool = mapShareBatchWaitStopCustomerPool.get(customer.getShareToken());
        oneShareBatchPool.remove(customer.getCustomerToken());
        if (oneShareBatchPool.isEmpty())
            mapShareBatchWaitStopCustomerPool.remove(customer.getShareToken());
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
