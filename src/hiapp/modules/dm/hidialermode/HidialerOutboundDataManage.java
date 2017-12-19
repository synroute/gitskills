package hiapp.modules.dm.hidialermode;

import hiapp.modules.dm.Constants;
import hiapp.modules.dm.hidialermode.bo.*;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomer;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberPredictStateEnum;
import hiapp.modules.dm.multinumbermode.bo.PhoneDialInfo;
import hiapp.modules.dmmanager.AreaTypeEnum;
import hiapp.modules.dmmanager.OperationNameEnum;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.hidialermode.dao.HidialerModeDAO;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmsetting.DMBizPresetItem;
import hiapp.modules.dmsetting.DMPresetStateEnum;
import hiapp.modules.dmsetting.data.DmBizRepository;

import java.util.*;

import static hiapp.modules.dm.Constants.HiDialerUserId;


@Service
public class HidialerOutboundDataManage {

    @Autowired
    HidialerModeDAO hidialerModeDAO;

    @Autowired
    DMDAO dmDAO;

    @Autowired
    HidialerModeCustomerSharePool customerPool;

    @Autowired
    HidialerModeCustomerWaitPool  customerWaitPool;

    @Autowired
    private DataImportJdbc dataImportJdbc;

    @Autowired
    EndCodeRedialStrategyM2 endCodeRedialStrategyM2;

    @Autowired
    DmBizRepository dmBizRepository;

    @Autowired
    DMBizMangeShare dmBizMangeShare;

    Timer dailyTimer;
    TimerTask dailyTimerTask;

    Timer timeoutTimer;
    TimerTask timeoutTimerTask;


    public synchronized List<HidialerModeCustomer> extractNextOutboundCustomer(String userId, int bizId, int count) {
        List<HidialerModeCustomer> customerList = new ArrayList<HidialerModeCustomer>();

        for (int i=0; i<count; i++ ) {
            HidialerModeCustomer customer = customerPool.extractCustomer(userId, bizId);
            if (null == customer)
                break;

            customer.setState(HidialerModeCustomerStateEnum.EXTRACTED);
            customer.setModifyTime(new Date());
            customer.setModifyId(customer.getModifyId() + 1);
            customer.setModifyUserId(userId);
            //shareDataItem.setModifyDesc("");

            customerWaitPool.add(userId, customer);

            customerList.add(customer);

            hidialerModeDAO.updateCustomerShareForExtract(customer);

            // 插入共享历史表
            hidialerModeDAO.insertCustomerShareStateHistory(customer);
        }

        return customerList;
    }

    public String hiDialerDialResultNotify(String userId, int bizId, String importBatchId, String customerId,
                                           String shareBatchId, String resultCodeType, String resultCode, String customerCallId)
    {
        if (resultCodeType.equals("1") && resultCode.equals("1"))
        {
            String dialType = "dialType";

            HidialerModeCustomer customer = customerWaitPool.getWaitCustomer(userId, bizId, importBatchId, customerId);
            Date originModifyTime = customer.getModifyTime();
            int  originModifyId = customer.getModifyId();

            Date now = new Date();

            customer.setState(HidialerModeCustomerStateEnum.PHONECONNECTED);
            customer.setEndCodeType(resultCodeType);
            customer.setEndCode(resultCode);
            customer.setModifyUserId(userId);
            customer.setModifyId(customer.getModifyId() + 1);
            customer.setModifyTime(now);

            customerWaitPool.hidialerPhoneConnect(customer, originModifyTime);  // NOTE: 使用原客户，等待池中一个客户有多个key

            hidialerModeDAO.updateCustomerShareForOutboundResult(customer);

            // 插入共享历史表
            hidialerModeDAO.insertCustomerShareStateHistory(customer);

            return "";
        }

        return submitRNAResult(userId, bizId, importBatchId, customerId, resultCodeType, resultCode,
                 null, null, null, null);
    }

    public String submitAgentScreenPopUp(String userId, int bizId, String importBatchId, String customerId) {

        String dialType = "dialType";
        String customerCallId = "customerCallId";

        HidialerModeCustomer customer = customerWaitPool.getWaitCustomer(userId, bizId, importBatchId, customerId);
        Date originModifyTime = customer.getModifyTime();
        int  originModifyId = customer.getModifyId();

        Date now = new Date();

        customer.setState(HidialerModeCustomerStateEnum.SCREENPOPUP);
        customer.setModifyUserId(userId);
        customer.setModifyId(customer.getModifyId() + 1);
        customer.setModifyTime(now);

        customerWaitPool.agentScreenPopUp(customer, originModifyTime); // NOTE: 使用原客户，等待池中一个客户有多个key

        hidialerModeDAO.updateCustomerShareForOutboundResult(customer);

        // 插入共享历史表
        hidialerModeDAO.insertCustomerShareStateHistory(customer);

        // 插入结果表
        //dmDAO.updateDMResult(bizId, customer.getShareBatchId(), importBatchId, customerId, originModifyId); // MODIFYLAST 0
        //dmDAO.insertDMResult(bizId, customer.getShareBatchId(), importBatchId, customerId,
        //        customer.getModifyId(), userId, dialType, lastDialTime, customerCallId,
        //        customer.getEndCodeType(), customer.getEndCode());

        // 数据池记录表update
        ManualModeCustomer poolItem = hidialerModeDAO.getPoolItem(bizId, customer.getShareBatchId(), importBatchId, customerId );
        Integer poolId = dmBizMangeShare.getDataPoolId(bizId, userId, poolItem.getDataPoolIdCur());
        poolItem.setDataPoolIdLast(poolItem.getDataPoolIdCur());
        poolItem.setDataPoolIdCur(poolId);
        poolItem.setAreaTypeLast(poolItem.getAreaTypeCur());
        poolItem.setAreaTypeCur(AreaTypeEnum.NO);
        poolItem.setIsRecover(0);
        poolItem.setModifyUserId(userId);
        poolItem.setModifyTime(new Date());

        hidialerModeDAO.updatePool(poolItem);

        // 数据池操作记录表insert
        hidialerModeDAO.insertPoolOperation(poolItem, OperationNameEnum.Extract);

        return "";
    }

    public String submitRNAResult(String userId, int bizId, String importBatchId, String customerId,
                                       String resultCodeType, String resultCode,
                                       String dialType, Date dialTime, String customerCallId, String customerInfo) {

        HidialerModeCustomer originCustomerItem = customerWaitPool.removeWaitCustomer(
                HiDialerUserId, bizId, importBatchId, customerId);

        // 经过 Outbound 策略处理器
        EndCodeRedialStrategyM2Item strategyItem = endCodeRedialStrategyM2.getEndCodeRedialStrategyItem(
                                                        originCustomerItem.getBizId(), resultCodeType, resultCode);
        procEndcode(userId, originCustomerItem, strategyItem, resultCodeType, resultCode);

        return "";
    }

    public String submitOutboundResult(String userId, int bizId, String importBatchId, String customerId,
                                       String resultCodeType, String resultCode,
                                       String dialType, Date dialTime, String customerCallId, Map<String, String> mapCustomizedResultColumn, String customerInfo) {

        HidialerModeCustomer originCustomer = customerWaitPool.removeWaitCustomer(
                HiDialerUserId, bizId, importBatchId, customerId);

        HidialerModeCustomer newCustomer = originCustomer.deepClone();
        newCustomer.setEndCodeType(resultCodeType);
        newCustomer.setEndCode(resultCode);
        newCustomer.setModifyUserId(userId);
        newCustomer.setModifyTime(new Date());
        newCustomer.setModifyId(originCustomer.getModifyId() + 1);

        newCustomer.setState(HidialerModeCustomerStateEnum.FINISHED);

        // 更新共享状态表
        hidialerModeDAO.updateCustomerShareForOutboundResult(newCustomer);

        // 插入共享历史表
        hidialerModeDAO.insertCustomerShareStateHistory(newCustomer);


        // 插入结果表
        dmDAO.updateDMResult(bizId, originCustomer.getShareBatchId(), importBatchId, customerId, originCustomer.getModifyId()); // MODIFYLAST 0
        dmDAO.insertDMResult(bizId, originCustomer.getShareBatchId(), importBatchId, customerId,
                originCustomer.getModifyId() + 1, userId, dialType, dialTime, customerCallId,
                resultCodeType, resultCode, mapCustomizedResultColumn);

        // 插入导入客户表
        if (null != customerInfo) {
            dataImportJdbc.insertDataToImPortTable(bizId, importBatchId, customerId, userId, customerInfo, originCustomer.getModifyId() + 1);
        }

        return "";
    }

    /**
     *   呼损处理
     */
    public void lostProc(HidialerModeCustomer item, HidialerModeCustomerStateEnum lossState) {
        String dialType = "dialType";
        String customerCallId = "customerCallId";

        int originModifyId = item.getModifyId();

        Date now = new Date();

        item.setCallLossCount(item.getCallLossCount()+1);
        if (item.getCallLossCount() >= Constants.MAX_CALL_LOSS_COUNT) {
            item.setState(HidialerModeCustomerStateEnum.LOSS_FINISHED);
        } else {
            item.setState(lossState);

            // 放回共享池
            addCustomerToSharePool(item);
        }

        item.setModifyTime(now);
        item.setModifyId(originModifyId + 1);

        hidialerModeDAO.updateCustomerShareForOutboundResult(item);

        // 插入共享历史表
        hidialerModeDAO.insertCustomerShareStateHistory(item);

        // 插入结果表
        //dmDAO.updateDMResult(item.getBizId(), item.getShareBatchId(), item.getImportBatchId(), item.getCustomerId(),
        //        originModifyId); // MODIFYLAST 0
        //dmDAO.insertDMResult(item.getBizId(), item.getShareBatchId(), item.getImportBatchId(), item.getCustomerId(),
        //        originModifyId + 1, item.getModifyUserId(), dialType, originPhoneDialInfo.getLastDialTime(), customerCallId,
        //        item.getEndCodeType(), item.getEndCode());
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

        customerWaitPool.markShareBatchStopFromCustomerWaitPool(bizId, shareBatchIds);
    }

    public Boolean appendCustomersToShareBatch(int bizId, List<String> shareBatchIds) {
        // 获取ACTIVE状态的 shareBatchIds
        List<ShareBatchItem> shareBatchItemList = new ArrayList<ShareBatchItem>();
        dmDAO.getActiveShareBatchItems(shareBatchIds, shareBatchItemList);

        loadCustomersAppend(bizId, shareBatchItemList);
        return true;
    }

    // 用户登录通知
    public void onLogin(String userId) {

        // TODO ??? 需要处理吗 ？

    }


    public void initialize() {
        customerPool.initialize();
    }

    public void dailyProc(List<ShareBatchItem> shareBatchItems) {
        customerPool.clear();

        loadCustomersDaily(shareBatchItems);
    }

    public void timeoutProc() {
        customerWaitPool.timeoutProc();
    }


    /////////////////////////////////////////////////////////////

    private void procEndcode(String userId, HidialerModeCustomer originCustomerItem,
                             EndCodeRedialStrategyM2Item strategyItem,
                             String resultCodeType, String resultCode)
    {
        Date now = new Date();

        HidialerModeCustomer item = originCustomerItem.deepClone();
        item.setEndCodeType(resultCodeType);
        item.setEndCode(resultCode);
        item.setModifyUserId(userId);
        item.setModifyTime(now);
        item.setModifyId(originCustomerItem.getModifyId() + 1);

        if (strategyItem.getCustomerDialFinished()) {
            item.setState(HidialerModeCustomerStateEnum.FINISHED);

            // 更新共享状态表
            hidialerModeDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            hidialerModeDAO.insertCustomerShareStateHistory(item);

        } else /*重拨*/ {
            item.setRedialCount(item.getRedialCount()+1);
            if (item.getRedialCount() >= strategyItem.getMaxRedialNum()) {
                item.setState(HidialerModeCustomerStateEnum.FINISHED);
            } else {
                item.setState(HidialerModeCustomerStateEnum.WAIT_REDIAL);
                item.setNextDialTime(DateUtil.getNextXMinute(strategyItem.getRedialDelayMinutes()));
            }

            if (!HidialerModeCustomerStateEnum.FINISHED.equals(item.getState())) {
                addCustomerToSharePool(item);
            }

            hidialerModeDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            hidialerModeDAO.insertCustomerShareStateHistory(item);
        }

    }

    //
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
        List<HidialerModeCustomerStateEnum> shareCustomerStateList = new ArrayList<HidialerModeCustomerStateEnum>();
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.CREATED);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.APPENDED);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.HIDIALER_LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.REVERT);

        List<HidialerModeCustomerStateEnum> shareStateListWithDialTime = new ArrayList<HidialerModeCustomerStateEnum>();
        shareStateListWithDialTime.add(HidialerModeCustomerStateEnum.WAIT_REDIAL);

        List<HidialerModeCustomer> shareDataItems = new ArrayList<HidialerModeCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            shareDataItems.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = hidialerModeDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = hidialerModeDAO.getGivenBizCustomersByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareStateListWithDialTime, shareDataItems);


            // 收集客户共享状态为 HidialerModeCustomerStateEnum.APPENDED 的客户信息
            // 后续需要更改状态为 HidialerModeCustomerStateEnum.CREATED
            HashSet<String> appendedStateShareBatchIdSet = new HashSet<String>();

            for (HidialerModeCustomer customerItem : shareDataItems) {
                addCustomerToSharePool(customerItem);

                if (HidialerModeCustomerStateEnum.APPENDED.equals(customerItem.getState())) {
                    appendedStateShareBatchIdSet.add(customerItem.getShareBatchId());
                }
            }

            if (!appendedStateShareBatchIdSet.isEmpty())
                hidialerModeDAO.updateCustomerShareState(bizId, appendedStateShareBatchIdSet,
                        HidialerModeCustomerStateEnum.APPENDED, HidialerModeCustomerStateEnum.CREATED);
        }

        // 初始化等待池
        List<HidialerModeCustomerStateEnum> waitPoolStates = new ArrayList<HidialerModeCustomerStateEnum>();
        waitPoolStates.add(HidialerModeCustomerStateEnum.EXTRACTED);
        waitPoolStates.add(HidialerModeCustomerStateEnum.PHONECONNECTED);
        waitPoolStates.add(HidialerModeCustomerStateEnum.SCREENPOPUP);

        List<HidialerModeCustomer> waitResultCustoms = new ArrayList<HidialerModeCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            waitResultCustoms.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = hidialerModeDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, waitPoolStates, waitResultCustoms);

            for (HidialerModeCustomer customerItem : waitResultCustoms) {
                addCustomerToWaitPool(customerItem.getModifyUserId(), customerItem);
            }
        }

        waitPoolPostProcess();
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

        List<HidialerModeCustomerStateEnum> shareCustomerStateList = new ArrayList<HidialerModeCustomerStateEnum>();
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.CREATED);
        //shareCustomerStateList.add(HidialerModeCustomerStateEnum.APPENDED);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.HIDIALER_LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.REVERT);

        List<HidialerModeCustomerStateEnum> shareCustomerStateList2 = new ArrayList<HidialerModeCustomerStateEnum>();
        shareCustomerStateList2.add(HidialerModeCustomerStateEnum.WAIT_REDIAL);

        List<HidialerModeCustomer> shareDataItems = new ArrayList<HidialerModeCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            shareDataItems.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = hidialerModeDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = hidialerModeDAO.getGivenBizCustomersByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareCustomerStateList2, shareDataItems);

            for (HidialerModeCustomer customerItem : shareDataItems) {
                addCustomerToSharePool(customerItem);
            }

        }
    }

    public void addCustomerToSharePool(HidialerModeCustomer newCustomerItem) {
        customerPool.add(newCustomerItem);

        System.out.println("M2 add customer: bizId[" + newCustomerItem.getBizId()
                + "] shareId[" + newCustomerItem.getShareBatchId() + "] IID[" + newCustomerItem.getImportBatchId()
                + "] CID[" + newCustomerItem.getCustomerId() + "] " );
    }

    private void addCustomerToWaitPool(String userId, HidialerModeCustomer newCustomerItem) {
        customerWaitPool.add(userId, newCustomerItem);

        System.out.println("M2 add wait customer: bizId[" + newCustomerItem.getBizId()
                + "] shareId[" + newCustomerItem.getShareBatchId() + "] IID[" + newCustomerItem.getImportBatchId()
                + "] CID[" + newCustomerItem.getCustomerId() + "]");
    }

    private void waitPoolPostProcess() {
        customerWaitPool.postProcess();
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

    // 处理追加客户的情形
    private void loadCustomersAppend(int bizId, List<ShareBatchItem> shareBatchItems) {
        System.out.println("bizId : " + bizId);

        List<HidialerModeCustomerStateEnum> shareCustomerStateList = new ArrayList<HidialerModeCustomerStateEnum>();
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.APPENDED);

        List<HidialerModeCustomer> shareDataItems = new ArrayList<HidialerModeCustomer>();

        // TODO 成批从DB取数据
        Boolean result = hidialerModeDAO.getGivenBizCustomersByState(
                bizId, shareBatchItems, shareCustomerStateList, shareDataItems);

        // 记录客户共享状态为 MultiNumberPredictStateEnum.APPENDED 的客户信息
        // 后续需要更改状态为 MultiNumberPredictStateEnum.CREATED
        HashSet<String> appendedStateShareBatchIdSet = new HashSet<String>();

        for (HidialerModeCustomer customerItem : shareDataItems) {
            addCustomerToSharePool(customerItem);

            if (HidialerModeCustomerStateEnum.APPENDED.equals(customerItem.getState())) {
                appendedStateShareBatchIdSet.add(customerItem.getShareBatchId());
            }
        }

        if (!appendedStateShareBatchIdSet.isEmpty()) {
            hidialerModeDAO.updateCustomerShareState(bizId, appendedStateShareBatchIdSet,
                    HidialerModeCustomerStateEnum.APPENDED, HidialerModeCustomerStateEnum.CREATED);
        }
    }

}
