package hiapp.modules.dm.manualmode;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomerPool;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberPredictStateEnum;
import hiapp.modules.dmmanager.DataPoolRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ManualOutboundDataManage {

    @Autowired
    ManualModeDAO manualModeDAO;

    ManualModeCustomerPool customerPool;

    public void initialize() {
        customerPool.initialize();
    }

    public synchronized DataPoolRecord extractNextOutboundCustomer(String userId, int bizId) {
        return null;
    }

    public String submitOutboundResult(String userId, int bizId, String importBatchId, String customerId,
                                       String resultCodeType, String resultCode, Date presetTime, String resultData,
                                       String customerInfo) {
        return "";
    }

    /*
     * 重新初始化处理不适合，举例说明：系统运行中，进行启用共享批次操作，会导致获取外呼客户的功能暂停一段时间。
     *
     */
    public String startShareBatch(int bizId, List<String> shareBatchIds) {
        return "";
    }

    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
    }

    public Boolean appendCustomersToShareBatch(int bizId, List<String> shareBatchIds) {
        return true;
    }

    // 用户登录通知
    public void onLogin(String userId) {
    }


    ////////////////////////////////////////////////////////////
    public void dailyProc(List<ShareBatchItem> shareBatchItems) {
        //mapPresetDialCustomerSharePool.clear();
        //mapStageDialCustomerSharePool.clear();
        //mapDialCustomerSharePool.clear();

        loadCustomersDaily(shareBatchItems);

    }

    public void timeoutProc() {
        System.out.println("ManualOutboundDataManage TimeOut ...");

    }

    private void loadCustomersDaily(List<ShareBatchItem> shareBatchItems) {

        Date now = new Date();

        // 根据BizId, 归类共享客户数据
        Map<Integer, List<ShareBatchItem>> mapBizIdVsShareBatchItem = new HashMap<Integer, List<ShareBatchItem>>();
        for (ShareBatchItem shareBatchItem : shareBatchItems) {
            List<ShareBatchItem> shareBatchItemList = mapBizIdVsShareBatchItem.get(shareBatchItem.getBizId());
            if (null == shareBatchItemList) {
                shareBatchItemList = new ArrayList<ShareBatchItem>();
                mapBizIdVsShareBatchItem.put(shareBatchItem.getBizId(), shareBatchItemList);
            }
            shareBatchItemList.add(shareBatchItem);
        }

        // 初始化共享池
        List<DataPoolRecord> shareCustomerItems = new ArrayList<DataPoolRecord>();

        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {
            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            manualModeDAO.getGivenBizShareCustomers(bizId, givenBizShareBatchItems, shareCustomerItems);
        }

        for (DataPoolRecord customer : shareCustomerItems) {
            customerPool.addCustomer(customer);
        }
    }
}
