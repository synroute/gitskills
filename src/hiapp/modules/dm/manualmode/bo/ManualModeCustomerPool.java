package hiapp.modules.dm.manualmode.bo;

import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;

import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class ManualModeCustomerPool {

    @Autowired
    DMDAO dmDAO;

    @Autowired
    ManualModeDAO manualModeDAO;

    @Autowired
    private DMBizMangeShare dmBizMangeShare;


    // BizId <==> {shareBatchId <==> PriorityBlockingQueue<ManualModeCustomer>}
    Map<Integer, Map<String, PriorityBlockingQueue<ManualModeCustomer>>> mapCustomerSharePool;

    public void initialize() {
        mapCustomerSharePool = new HashMap<Integer, Map<String, PriorityBlockingQueue<ManualModeCustomer>>>();
    }

    public void clear() {
        mapCustomerSharePool.clear();
    }

    public void addCustomer(ManualModeCustomer customer) {

        System.out.println("M1 add customer: bizId[" + customer.getBizId()
                + "] shareId[" + customer.getSourceId() + "] IID[" + customer.getImportBatchId()
                + "] CID[" + customer.getCustomerId() + "] ");

        Map<String, PriorityBlockingQueue<ManualModeCustomer>> oneBizCustomerSharePool = mapCustomerSharePool.get(customer.getBizId());
        if (null == oneBizCustomerSharePool) {
            oneBizCustomerSharePool = new HashMap<String, PriorityBlockingQueue<ManualModeCustomer>>();
            mapCustomerSharePool.put(customer.getBizId(), oneBizCustomerSharePool);
        }

        PriorityBlockingQueue<ManualModeCustomer> oneShareBatchCustomerSharePool = oneBizCustomerSharePool.get(customer.getSourceId());
        if (null == oneShareBatchCustomerSharePool) {
            oneShareBatchCustomerSharePool = new PriorityBlockingQueue<ManualModeCustomer>(1, shareBatchBeginTimeComparator);
            oneBizCustomerSharePool.put(customer.getSourceId(), oneShareBatchCustomerSharePool);
        }

        oneShareBatchCustomerSharePool.put(customer);
    }

    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        Map<String, PriorityBlockingQueue<ManualModeCustomer>> oneBizCustomerSharePool = mapCustomerSharePool.get(bizId);
        if (null == oneBizCustomerSharePool)
            return;

        for (String shareBatchId : shareBatchIds) {
            oneBizCustomerSharePool.remove(shareBatchId);
        }
    }

    public ManualModeCustomer extractCustomer(String userId, int bizId) {

        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);

        Map<String, PriorityBlockingQueue<ManualModeCustomer>> oneBizCustomerSharePool = mapCustomerSharePool.get(bizId);
        if (null == oneBizCustomerSharePool)
            return null;

        for (String shareBatchId : shareBatchIdList) {
            PriorityBlockingQueue<ManualModeCustomer> oneShareBatchCustomerPool = oneBizCustomerSharePool.get(shareBatchId);
            if (null == oneShareBatchCustomerPool)
                continue;

            ManualModeCustomer customer = oneShareBatchCustomerPool.poll();
            if (null == customer) {
                oneBizCustomerSharePool.remove(shareBatchId);
                continue;
            }

            return customer;

        }
        return null;
    }

    //匿名Comparator实现
    private static Comparator<ManualModeCustomer> shareBatchBeginTimeComparator = new Comparator<ManualModeCustomer>() {

        @Override
        public int compare(ManualModeCustomer c1, ManualModeCustomer c2) {
            return (c1.getShareBatchStartTime().before(c2.getShareBatchStartTime())) ? 1 : -1;
        }
    };



}
