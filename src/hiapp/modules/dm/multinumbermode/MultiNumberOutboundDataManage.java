package hiapp.modules.dm.multinumbermode;

import hiapp.modules.dm.Constants;
import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.bo.PhoneTypeDialSequence;
import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.multinumbermode.bo.*;
import hiapp.modules.dm.multinumbermode.dao.MultiNumberPredictModeDAO;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmsetting.DMBizPresetItem;
import hiapp.modules.dmsetting.DMPresetStateEnum;
import hiapp.modules.dmsetting.data.DmBizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static hiapp.modules.dm.Constants.HiDialerUserId;


@Service
public class MultiNumberOutboundDataManage {

    @Autowired
    MultiNumberPredictModeDAO multiNumberPredictModeDAO;

    @Autowired
    DMDAO dmDAO;

    @Autowired
    MultiNumberPredictCustomerPool customerPool;

    @Autowired
    CustomerWaitPool customerWaitPool;

    @Autowired
    private DataImportJdbc dataImportJdbc;

    @Autowired
    PhoneTypeDialSequence phoneTypeDialSequence;

    @Autowired
    EndCodeRedialStrategyM6 endCodeRedialStrategyM6;

    @Autowired
    DmBizRepository dmBizRepository;

    Timer dailyTimer;
    TimerTask dailyTimerTask;

    Timer timeoutTimer;
    TimerTask timeoutTimerTask;


    public synchronized List<MultiNumberCustomer> extractNextOutboundCustomer(String userId, int bizId, int count) {
        List<MultiNumberCustomer> customerList = new ArrayList<MultiNumberCustomer>();

        for (int i=0; i<count; i++ ) {
            MultiNumberCustomer customer = customerPool.extractCustomer(userId, bizId);
            if (null == customer)
                break;

            customerList.add(customer);

            multiNumberPredictModeDAO.updateCustomerShareForExtract(customer);

            // 插入共享历史表
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(customer);
        }

        return customerList;
    }

    public String hiDialerDialResultNotify(String userId, int bizId, String importBatchId, String customerId, int phoneType,
                                           String resultCodeType, String resultCode, String customerCallId)
    {
        if (resultCodeType.equals("1") && resultCode.equals("1"))
        {
            String dialType = "dialType";

            MultiNumberCustomer customer = customerPool.getWaitCustomer(userId, bizId, importBatchId, customerId, phoneType);
            Date originModifyTime = customer.getModifyTime();
            int  originModifyId = customer.getModifyId();

            Date now = new Date();

            customer.setState(MultiNumberPredictStateEnum.PHONECONNECTED);
            customer.setEndCodeType(resultCodeType);
            customer.setEndCode(resultCode);
            customer.setModifyUserId(userId);
            customer.setModifyId(customer.getModifyId() + 1);
            customer.setModifyTime(now);

            PhoneDialInfo curPhoneDialInfo = customer.getDialInfoByPhoneType(customer.getCurDialPhoneType());
            curPhoneDialInfo.setLastDialTime(now);

            customerPool.hidialerPhoneConnect(customer, originModifyTime);  // NOTE: 使用原客户，等待池中一个客户有多个key

            multiNumberPredictModeDAO.updateCustomerShareForOutboundResult(customer);

            // 插入共享历史表
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(customer);

            // 插入结果表
            //dmDAO.updateDMResult(bizId, customer.getShareBatchId(), importBatchId, customerId, originModifyId); // MODIFYLAST 0
            //dmDAO.insertDMResult(bizId, customer.getShareBatchId(), importBatchId, customerId,
            //        customer.getModifyId(), userId, dialType, now, customerCallId,
            //        resultCodeType, resultCode);

            return "";
        }

        return submitOutboundResult(userId, bizId, importBatchId, customerId, phoneType, resultCodeType, resultCode,
                null, null, null, null, null,
                null, null);
    }

    public String submitAgentScreenPopUp(String userId, int bizId, String importBatchId, String customerId, int phoneType) {

        String dialType = "dialType";
        String customerCallId = "customerCallId";

        MultiNumberCustomer customer = customerPool.getWaitCustomer(userId, bizId, importBatchId, customerId, phoneType);
        Date originModifyTime = customer.getModifyTime();
        int  originModifyId = customer.getModifyId();

        Date now = new Date();

        customer.setState(MultiNumberPredictStateEnum.SCREENPOPUP);
        customer.setModifyUserId(userId);
        customer.setModifyId(customer.getModifyId() + 1);
        customer.setModifyTime(now);

        PhoneDialInfo originPhoneDialInfo = customer.getDialInfoByPhoneType(customer.getCurDialPhoneType());
        Date lastDialTime = originPhoneDialInfo.getLastDialTime();

        customerPool.agentScreenPopUp(customer, originModifyTime); // NOTE: 使用原客户，等待池中一个客户有多个key

        multiNumberPredictModeDAO.updateCustomerShareForOutboundResult(customer);

        // 插入共享历史表
        multiNumberPredictModeDAO.insertCustomerShareStateHistory(customer);

        // 插入结果表
        //dmDAO.updateDMResult(bizId, customer.getShareBatchId(), importBatchId, customerId, originModifyId); // MODIFYLAST 0
        //dmDAO.insertDMResult(bizId, customer.getShareBatchId(), importBatchId, customerId,
        //        customer.getModifyId(), userId, dialType, lastDialTime, customerCallId,
        //        customer.getEndCodeType(), customer.getEndCode());

        return "";
    }

    public String submitOutboundResult(String userId, int bizId, String importBatchId, String customerId, int phoneType,
                                       String resultCodeType, String resultCode, Boolean isPreset, Date presetTime,
                                       String dialType, Date dialTime, String customerCallId, Map<String, String> mapCustomizedResultColumn, String customerInfo) {

        MultiNumberCustomer originCustomerItem = customerPool.removeWaitCustomer(
                HiDialerUserId, bizId, importBatchId, customerId, phoneType);

        // 经过 Outbound 策略处理器
        EndCodeRedialStrategyM6Item strategyItem = endCodeRedialStrategyM6.getEndCodeRedialStrategyItem(
                                                        originCustomerItem.getBizId(), resultCodeType, resultCode);
        procEndcode(userId, originCustomerItem, strategyItem, resultCodeType, resultCode, presetTime);

        if (!HiDialerUserId.equals(userId)) {
            // 插入结果表
            dmDAO.updateDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId, originCustomerItem.getModifyId()); // MODIFYLAST 0
            dmDAO.insertDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId,
                    originCustomerItem.getModifyId() + 1, userId, dialType, dialTime, customerCallId,
                    resultCodeType, resultCode, mapCustomizedResultColumn);

            // 插入导入客户表
            if (null != customerInfo) {
                dataImportJdbc.insertDataToImPortTable(bizId, importBatchId, customerId, userId, customerInfo, originCustomerItem.getModifyId() + 1);
            }
        }

        return "";
    }


    /**
     *   呼损处理
     */
    //,,,
    public void lostProc(MultiNumberCustomer item, MultiNumberPredictStateEnum lossState) {
        String dialType = "dialType";
        String customerCallId = "customerCallId";

        int originModifyId = item.getModifyId();
        PhoneDialInfo originPhoneDialInfo = item.getDialInfoByPhoneType(item.getCurDialPhoneType());

        Date now = new Date();

        item.setCallLossCount(item.getCallLossCount() + 1);
        if (item.getCallLossCount() < Constants.MAX_CALL_LOSS_COUNT) {
            item.setState(lossState);
        } else {
            Integer nextDialPhoneType = customerPool.calcNextDialPhoneType(item);
            if (null != nextDialPhoneType) {
                item.setState(MultiNumberPredictStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
                item.setNextDialPhoneType(nextDialPhoneType);
                item.setCallLossCount(0);
            } else {
                item.setState(MultiNumberPredictStateEnum.FINISHED);
            }
        }

        if (!MultiNumberPredictStateEnum.FINISHED.equals(item.getState())) {
            addCustomerToSharePool(item);
        }

        item.setModifyTime(now);
        item.setModifyId(originModifyId + 1);

        multiNumberPredictModeDAO.updateCustomerShareForOutboundResult(item);

        // 插入共享历史表
        multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);

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

        customerPool.markShareBatchStopFromCustomerWaitPool(bizId, shareBatchIds);
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

        // TODO ??? 多号码预测模式需要处理吗 ？

    }


    public void initialize() {
        customerPool.initialize();
        customerWaitPool.initialize();
    }

    public void dailyProc(List<ShareBatchItem> shareBatchItems) {
        customerPool.clear();

        loadCustomersDaily(shareBatchItems);
    }

    public void timeoutProc() {
        customerPool.timeoutProc();
    }

    public void cancelOutboundTask(int bizId, List<CustomerBasic> customerBasicList) {
        // step 1 : remove from share pool
        List<MultiNumberCustomer> customerList = customerPool.cancelShare(bizId, customerBasicList);

        // step 2 : update state and insert share history table
        List<Integer> customerDBIdList = new ArrayList<Integer>();
        for (MultiNumberCustomer customer : customerList) {
            customer.setState(MultiNumberPredictStateEnum.CANCELLED);
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(customer);

            customerDBIdList.add(customer.getId());
        }

        multiNumberPredictModeDAO.updateCustomerShareStateToCancel(bizId, customerDBIdList, MultiNumberPredictStateEnum.CANCELLED);

    }

    /////////////////////////////////////////////////////////////

    private void procEndcode(String userId, MultiNumberCustomer originCustomerItem,
                             EndCodeRedialStrategyM6Item strategyItem,
                             String resultCodeType, String resultCode, Date presetTime) {

        Date now = new Date();

        MultiNumberCustomer item = originCustomerItem.deepClone();
        item.setEndCodeType(resultCodeType);
        item.setEndCode(resultCode);
        item.setModifyUserId(userId);
        item.setModifyTime(now);
        item.setModifyId(originCustomerItem.getModifyId() + 1);

        PhoneDialInfo curPhoneDialInfo = item.getDialInfoByPhoneType(item.getCurDialPhoneType());
        curPhoneDialInfo.setDialCount( curPhoneDialInfo.getDialCount() + 1);
        curPhoneDialInfo.setLastDialTime(now);

        if (strategyItem.getCustomerDialFinished()) {
            item.setState(MultiNumberPredictStateEnum.FINISHED);

            // 更新共享状态表
            multiNumberPredictModeDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);

        } else if (strategyItem.getPresetDial()) {
            item.setState(MultiNumberPredictStateEnum.PRESET_DIAL);
            item.setNextDialPhoneType(item.getCurDialPhoneType());
            PhoneDialInfo phoneDialInfo = item.getDialInfoByPhoneType(item.getCurDialPhoneType());
            phoneDialInfo.setCausePresetDialCount( phoneDialInfo.getCausePresetDialCount() + 1);

            // 更新共享状态表
            item.setCurPresetDialTime(presetTime);
            multiNumberPredictModeDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);

            // 插入预约表
            DMBizPresetItem presetItem = new DMBizPresetItem();
            presetItem.setSourceId(originCustomerItem.getShareBatchId());
            presetItem.setCustomerId(originCustomerItem.getCustomerId());
            presetItem.setImportId(originCustomerItem.getImportBatchId());
            presetItem.setPresetTime(presetTime);
            presetItem.setState(DMPresetStateEnum.InUse.getStateName());
            presetItem.setComment("xxx");
            presetItem.setModifyId(item.getModifyId());
            presetItem.setModifyLast(1);
            presetItem.setModifyUserId(userId);
            presetItem.setModifyTime(now);
            presetItem.setModifyDesc("xxx");
            presetItem.setPhoneType("xxx");
            dmDAO.insertPresetItem(originCustomerItem.getBizId(), presetItem);

            // 放回候选池，预约在今天
            if (DateUtil.isSameDay(now, presetTime)) {
                addCustomerToSharePool(item);
            }

        } else if (strategyItem.getPhoneTypeDialFinished()) {
            Integer nextDialPhoneType = customerPool.calcNextDialPhoneType(item);
            if (null != nextDialPhoneType) {
                item.setState(MultiNumberPredictStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
                item.setNextDialPhoneType(nextDialPhoneType);
                item.setCallLossCount(0);
            } else {
                item.setState(MultiNumberPredictStateEnum.FINISHED);
            }

            if (!MultiNumberPredictStateEnum.FINISHED.equals(item.getState())) {
                addCustomerToSharePool(item);
            }

            // 更新共享状态表
            multiNumberPredictModeDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);

        } else /*当前号码类型重拨*/ {

            PhoneDialInfo phoneDialInfo = item.getDialInfoByPhoneType(item.getCurDialPhoneType());

            if ((phoneDialInfo.getDialCount() - phoneDialInfo.getCausePresetDialCount()) >= strategyItem.getMaxRedialNum()) {
                Integer nextDialPhoneType = customerPool.calcNextDialPhoneType(item);
                if (null != nextDialPhoneType) {
                    item.setState(MultiNumberPredictStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
                    item.setNextDialPhoneType(nextDialPhoneType);
                    item.setCallLossCount(0);
                } else {
                    item.setState(MultiNumberPredictStateEnum.FINISHED);
                }
            } else {
                item.setState(MultiNumberPredictStateEnum.WAIT_REDIAL);
                item.setCurPresetDialTime(DateUtil.getNextXMinute(strategyItem.getRedialDelayMinutes()));
            }

            if (!MultiNumberPredictStateEnum.FINISHED.equals(item.getState())) {
                addCustomerToSharePool(item);
            }

            multiNumberPredictModeDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);
        }

        //若是当前是预约拨打，更新 预约状态 @ 预约表
        if (MultiNumberPredictStateEnum.PRESET_DIAL.equals(originCustomerItem.getState())) {
            dmDAO.updatePresetState(item.getBizId(), item.getShareBatchId(), item.getImportBatchId(),
                    item.getCustomerId(), originCustomerItem.getModifyId(),
                    DMPresetStateEnum.FinishPreset.getStateName());
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
        List<MultiNumberPredictStateEnum> shareCustomerStateList = new ArrayList<MultiNumberPredictStateEnum>();
        shareCustomerStateList.add(MultiNumberPredictStateEnum.CREATED);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.APPENDED);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.HIDIALER_LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.REVERT);

        List<MultiNumberPredictStateEnum> shareStateListWithDialTime = new ArrayList<MultiNumberPredictStateEnum>();
        shareStateListWithDialTime.add(MultiNumberPredictStateEnum.PRESET_DIAL);
        shareStateListWithDialTime.add(MultiNumberPredictStateEnum.WAIT_REDIAL);

        List<MultiNumberCustomer> shareDataItems = new ArrayList<MultiNumberCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            shareDataItems.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = multiNumberPredictModeDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = multiNumberPredictModeDAO.getGivenBizCustomersByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareStateListWithDialTime, shareDataItems);


            // 收集客户共享状态为 MultiNumberPredictStateEnum.APPENDED 的客户信息
            // 后续需要更改状态为 MultiNumberPredictStateEnum.CREATED
            HashSet<String> appendedStateShareBatchIdSet = new HashSet<String>();

            for (MultiNumberCustomer customerItem : shareDataItems) {
                addCustomerToSharePool(customerItem);

                if (MultiNumberPredictStateEnum.APPENDED.equals(customerItem.getState())) {
                    appendedStateShareBatchIdSet.add(customerItem.getShareBatchId());
                }
            }

            if (!appendedStateShareBatchIdSet.isEmpty())
                multiNumberPredictModeDAO.updateCustomerShareState(bizId, appendedStateShareBatchIdSet,
                        MultiNumberPredictStateEnum.APPENDED, MultiNumberPredictStateEnum.CREATED);
        }

        // 初始化等待池
        List<MultiNumberPredictStateEnum> waitPollStates = new ArrayList<MultiNumberPredictStateEnum>();
        waitPollStates.add(MultiNumberPredictStateEnum.EXTRACTED);
        waitPollStates.add(MultiNumberPredictStateEnum.PHONECONNECTED);
        waitPollStates.add(MultiNumberPredictStateEnum.SCREENPOPUP);

        List<MultiNumberCustomer> waitResultCustoms = new ArrayList<MultiNumberCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            waitResultCustoms.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = multiNumberPredictModeDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, waitPollStates, waitResultCustoms);

            for (MultiNumberCustomer customerItem : waitResultCustoms) {
                addCustomerToWaitPool(customerItem);
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

        List<MultiNumberPredictStateEnum> shareCustomerStateList = new ArrayList<MultiNumberPredictStateEnum>();
        shareCustomerStateList.add(MultiNumberPredictStateEnum.CREATED);
        //shareCustomerStateList.add(MultiNumberPredictStateEnum.APPENDED);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.HIDIALER_LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.REVERT);

        List<MultiNumberPredictStateEnum> shareCustomerStateList2 = new ArrayList<MultiNumberPredictStateEnum>();
        shareCustomerStateList2.add(MultiNumberPredictStateEnum.PRESET_DIAL);
        shareCustomerStateList2.add(MultiNumberPredictStateEnum.WAIT_REDIAL);

        List<MultiNumberCustomer> shareDataItems = new ArrayList<MultiNumberCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            shareDataItems.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();


            // 成批从DB取数据
            Boolean result = multiNumberPredictModeDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = multiNumberPredictModeDAO.getGivenBizCustomersByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareCustomerStateList2, shareDataItems);

            for (MultiNumberCustomer customerItem : shareDataItems) {
                addCustomerToSharePool(customerItem);
            }

        }
    }

    public void addCustomerToSharePool(MultiNumberCustomer newCustomerItem) {
        Boolean ret = customerPool.add(newCustomerItem);

        System.out.println("M6 add customer: bizId[" + newCustomerItem.getBizId()
                + "] shareId[" + newCustomerItem.getShareBatchId() + "] IID[" + newCustomerItem.getImportBatchId()
                + "] CID[" + newCustomerItem.getCustomerId() + "] " + ret);
    }

    private void addCustomerToWaitPool(MultiNumberCustomer newCustomerItem) {
        customerPool.addWaitResultCustomer(newCustomerItem);

        System.out.println("M6 add wait customer: bizId[" + newCustomerItem.getBizId()
                + "] shareId[" + newCustomerItem.getShareBatchId() + "] IID[" + newCustomerItem.getImportBatchId()
                + "] CID[" + newCustomerItem.getCustomerId() + "]");
    }

    private void waitPoolPostProcess() {
        customerPool.waitPoolPostProcess();
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

        List<MultiNumberPredictStateEnum> shareCustomerStateList = new ArrayList<MultiNumberPredictStateEnum>();
        shareCustomerStateList.add(MultiNumberPredictStateEnum.APPENDED);

        List<MultiNumberCustomer> shareDataItems = new ArrayList<MultiNumberCustomer>();

        // TODO 成批从DB取数据
        Boolean result = multiNumberPredictModeDAO.getGivenBizCustomersByState(
                bizId, shareBatchItems, shareCustomerStateList, shareDataItems);

        // 记录客户共享状态为 MultiNumberPredictStateEnum.APPENDED 的客户信息
        // 后续需要更改状态为 MultiNumberPredictStateEnum.CREATED
        HashSet<String> appendedStateShareBatchIdSet = new HashSet<String>();

        for (MultiNumberCustomer customerItem : shareDataItems) {
            addCustomerToSharePool(customerItem);

            if (MultiNumberPredictStateEnum.APPENDED.equals(customerItem.getState())) {
                appendedStateShareBatchIdSet.add(customerItem.getShareBatchId());
            }
        }

        if (!appendedStateShareBatchIdSet.isEmpty()) {
            multiNumberPredictModeDAO.updateCustomerShareState(bizId, appendedStateShareBatchIdSet,
                    MultiNumberPredictStateEnum.APPENDED, MultiNumberPredictStateEnum.CREATED);
        }
    }

}
