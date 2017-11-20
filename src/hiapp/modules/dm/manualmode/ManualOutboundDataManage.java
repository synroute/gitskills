package hiapp.modules.dm.manualmode;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomer;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomerPool;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ManualOutboundDataManage {

    @Autowired
    ManualModeDAO manualModeDAO;

    @Autowired
    ManualModeCustomerPool customerPool;

    @Autowired
    DMDAO dmDAO;


    public void initialize() {
        customerPool.initialize();
    }

    public synchronized ManualModeCustomer extractNextOutboundCustomer(String userId, int bizId) {
        ManualModeCustomer customer = customerPool.extractCustomer(userId, bizId);

        // TODO  更新 SA-》NONE


        return customer;
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

        // 设置共享批次状态
        dmDAO.updateShareBatchState(bizId, shareBatchIds, ShareBatchStateEnum.ENABLE.getName());

        List<ShareBatchItem> shareBatchItems = shareBatchIncrementalProc(bizId, shareBatchIds);

        loadCustomersIncremental(shareBatchItems);

        return "";
    }

    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        customerPool.stopShareBatch(bizId, shareBatchIds);
    }

    public Boolean appendCustomersToShareBatch(int bizId, List<String> shareBatchIds) {
        return true;
    }

    // 用户登录通知
    public void onLogin(String userId) {
    }


    ////////////////////////////////////////////////////////////
    public void dailyProc(List<ShareBatchItem> shareBatchItems) {
        customerPool.clear();

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
        List<ManualModeCustomer> shareCustomerItems = new ArrayList<ManualModeCustomer>();

        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {
            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            shareCustomerItems.clear();
            manualModeDAO.getGivenBizShareCustomers(bizId, givenBizShareBatchItems, shareCustomerItems);

            for (ManualModeCustomer customer : shareCustomerItems) {
                customerPool.addCustomer(customer);

            }
        }
    }

    // 用于共享批次的启用，根据共享批次，不会导致重复加载
    private void loadCustomersIncremental(List<ShareBatchItem> shareBatchItems) {

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
        List<ManualModeCustomer> shareCustomerItems = new ArrayList<ManualModeCustomer>();

        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {
            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            shareCustomerItems.clear();
            manualModeDAO.getGivenBizShareCustomers(bizId, givenBizShareBatchItems, shareCustomerItems);

            for (ManualModeCustomer customer : shareCustomerItems) {
                customerPool.addCustomer(customer);

            }
        }
    }

    /**
     * 过滤出当天需要激活的共享批次
     * @param bizId
     * @param shareBatchIds
     */
    private List<ShareBatchItem> shareBatchIncrementalProc(int bizId, /*IN,OUT*/List<String> shareBatchIds) {
        //List<String> expiredShareBatchIds = new ArrayList<String>();
        dmDAO.expireShareBatchsByEndTime(/*expiredShareBatchIds*/);

        List<ShareBatchItem> shareBatchItems = dmDAO.getCurDayNeedActiveShareBatchItems(bizId, shareBatchIds);

        dmDAO.activateShareBatchByStartTime(bizId, shareBatchItems);

        return shareBatchItems;
    }

}
