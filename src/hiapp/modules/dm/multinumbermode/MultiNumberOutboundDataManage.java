package hiapp.modules.dm.multinumbermode;

import hiapp.modules.dm.Constants;
import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.multinumbermode.bo.EndCodeRedialStrategy;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.modules.dm.multinumbermode.bo.OneBizCustomerSharePool;
import hiapp.modules.dm.multinumbermode.dao.MultiNumberPredictModeDAO;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dm.Constants;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import hiapp.modules.dm.Constants;

import java.util.*;

@Service
public class MultiNumberOutboundDataManage {

    @Autowired
    MultiNumberPredictModeDAO multiNumberPredictModeDAO;

    @Autowired
    @Qualifier("multiNumberPredictMode-EndCodeRedialStrategy")
    EndCodeRedialStrategy endCodeRedialStrategy;

    Timer dailyTimer;
    TimerTask dailyTimerTask;

    Timer timeoutTimer;
    TimerTask timeoutTimerTask;


    // bizId <==> OneBizCustomerSharePool
    Map<Integer, OneBizCustomerSharePool> mapBizIdVsCustomerPool;


    public synchronized SingleNumberModeShareCustomerItem extractNextOutboundCustomer(String userId, int bizId) {
        OneBizCustomerSharePool bizCustomerSharePool = mapBizIdVsCustomerPool.get(bizId);
        bizCustomerSharePool.extractCustomer(userId);

        return null;
    }

    public String submitHiDialerOutboundResult(String userId, int bizId, String importBatchId, String customerId,
                                       String resultCodeType, String resultCode, Date presetTime, String resultData,
                                       String customerInfo) {
        return "";
    }

    public String submitOutboundResult(String userId, int bizId, String importBatchId, String customerId,
                                       String resultCodeType, String resultCode, Date presetTime, String resultData,
                                       String customerInfo) {

        //endCodeRedialStrategy;
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

    public String appendCustomersToShareBatch(int bizId, List<String> shareBatchIds) {
        return "";
    }

    // 用户登录通知
    public void onLogin(String userId) {
    }


    ////////////////////////////////////////////////////////////

    public void initialize() {

        mapBizIdVsCustomerPool = new HashMap<Integer, OneBizCustomerSharePool>();

        List<MultiNumberCustomer> customerList = multiNumberPredictModeDAO.getAllActiveCustomers();
        if (null == customerList)
            return;

        for (MultiNumberCustomer customer : customerList) {
            OneBizCustomerSharePool bizCustomerSharePool = mapBizIdVsCustomerPool.get(customer.getBizId());
            if (null == bizCustomerSharePool) {
                bizCustomerSharePool = new OneBizCustomerSharePool(customer.getBizId());
                bizCustomerSharePool.add(customer);
            }
        }

    }

    public void dailyProc(List<ShareBatchItem> shareBatchItems) {
//        mapPresetDialCustomerSharePool.clear();
//        mapStageDialCustomerSharePool.clear();
//        mapDialCustomerSharePool.clear();

        loadCustomersDaily(shareBatchItems);
    }

    //
    private void loadCustomersDaily(List<ShareBatchItem> shareBatchItems) {
        /*
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

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.CREATED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.APPENDED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.REVERT);

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList2 = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_STAGE_DIAL);
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);

        List<SingleNumberModeShareCustomerItem> shareDataItems = new ArrayList<SingleNumberModeShareCustomerItem>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            shareDataItems.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();


            // 根据未接通拨打日期，决定是否清零<当日未接通重拨次数>
            singleNumberModeDAO.clearPreviousDayLostCallCount(bizId);

            // 成批从DB取数据
            Boolean result = singleNumberModeDAO.getGivenBizShareDataItemsByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = singleNumberModeDAO.getGivenBizShareDataItemsByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareCustomerStateList2, shareDataItems);


            // 收集客户共享状态为 SingleNumberModeShareCustomerStateEnum.APPENDED 的客户信息
            // 后续需要更改状态为 SingleNumberModeShareCustomerStateEnum.CREATED
            List<String> appendedStateCustomerIdList = new ArrayList<String>();

            for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {
                if (needJoinCustomerPool(bizId, customerItem))
                    addCustomerToSharePool(customerItem);

                if (SingleNumberModeShareCustomerStateEnum.APPENDED.equals(customerItem.getState())) {
                    appendedStateCustomerIdList.add(customerItem.getShareBatchId());
                }
            }

            singleNumberModeDAO.updateCustomerShareState(bizId, appendedStateCustomerIdList,
                    SingleNumberModeShareCustomerStateEnum.CREATED.getName());
        }
        */
    }

    // 用于共享批次的启用，根据共享批次，不会导致重复加载
    private void loadCustomersIncremental(List<ShareBatchItem> shareBatchItems) {
        /*
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

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.CREATED);
        //shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.APPENDED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.REVERT);

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList2 = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_STAGE_DIAL);
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);

        List<SingleNumberModeShareCustomerItem> shareDataItems = new ArrayList<SingleNumberModeShareCustomerItem>();

        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = singleNumberModeDAO.getGivenBizShareDataItemsByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = singleNumberModeDAO.getGivenBizShareDataItemsByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareCustomerStateList2, shareDataItems);
        }

        for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {
            addCustomerToSharePool(customerItem);
        }
        */
    }

    public void timeoutProc() {
        /*Date now =  new Date();
        Long curTimeSlot = now.getTime()/Constants.timeSlotSpan;
        Long timeoutTimeSlot = curTimeSlot - Constants.timeoutThreshold/Constants.timeSlotSpan;

        while (earliestTimeSlot < timeoutTimeSlot) {
            Map<String, SingleNumberModeShareCustomerItem> mapTimeSlotWaitTimeOutPool;
            mapTimeSlotWaitTimeOutPool =  mapWaitTimeOutCustomerPool.get(earliestTimeSlot);

            for (SingleNumberModeShareCustomerItem customerItem : mapTimeSlotWaitTimeOutPool.values()) {
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    addCustomerToSharePool(customerItem);
                }

                removeWaitResultCustome(customerItem.getUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }*/
    }


}
