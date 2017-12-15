package hiapp.modules.dm.multinumberredialmode;

import hiapp.modules.dm.bo.PhoneTypeDialSequence;
import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberPredictStateEnum;
import hiapp.modules.dm.multinumberredialmode.bo.*;
import hiapp.modules.dm.multinumberredialmode.dao.MultiNumberRedialDAO;
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
public class MultiNumberRedialDataManage {

    @Autowired
    MultiNumberRedialDAO multiNumberRedialDAO;

    @Autowired
    DMDAO dmDAO;

    @Autowired
    MultiNumberRedialCustomerPool customerPool;

    @Autowired
    private DataImportJdbc dataImportJdbc;

    @Autowired
    PhoneTypeDialSequence phoneTypeDialSequence;

    @Autowired
    MultiNumberRedialStrategy multiNumberRedialStrategy;

    @Autowired
    DmBizRepository dmBizRepository;


    public synchronized MultiNumberRedialCustomer extractNextOutboundCustomer(String userId, int bizId) {
        MultiNumberRedialCustomer customer = customerPool.extractCustomer(userId, bizId);
        return customer;
    }

    public String submitOutboundResult(String userId, int bizId, String importBatchId, String customerId, int phoneType,
                                       String resultCodeType, String resultCode, Boolean isPreset, Date presetTime,
                                       String dialType, Date dialTime, String customerCallId, Map<String, String> mapCustomizedResultColumn, String customerInfo) {

        MultiNumberRedialCustomer originCustomerItem = customerPool.removeWaitCustomer(
                userId, bizId, importBatchId, customerId, phoneType);

        // 经过 Outbound 策略处理器
        EndCodeRedialStrategyM4 strategy = multiNumberRedialStrategy.getEndCodeRedialStrategyItem(bizId);
        procEndcode(userId, originCustomerItem, strategy, phoneType, resultCodeType, resultCode, presetTime);

        // 插入结果表
        dmDAO.updateDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId, originCustomerItem.getModifyId()); // MODIFYLAST 0
        dmDAO.insertDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId,
                originCustomerItem.getModifyId() + 1, userId, dialType, dialTime, customerCallId,
                resultCodeType, resultCode, mapCustomizedResultColumn);

        // 插入导入客户表
        if (null != customerInfo) {
            dataImportJdbc.insertDataToImPortTable(bizId, importBatchId, customerId, userId, customerInfo, originCustomerItem.getModifyId() + 1);
        }

        return "";
    }

    /**
     *   呼损处理
     */
    public void lostProc(MultiNumberRedialCustomer item, MultiNumberRedialStateEnum lossState) {
        String dialType = "dialType";
        String customerCallId = "customerCallId";

        int originModifyId = item.getModifyId();
        PhoneDialInfo originPhoneDialInfo = item.getDialInfoByPhoneType(item.getCurDialPhoneType());

        //Date now = new Date();

        addCustomerToSharePool(item);

        //item.setModifyTime(now);
        //item.setModifyId(originModifyId + 1);

        //multiNumberRedialDAO.updateCustomerShareForOutboundResult(item);

        // 插入共享历史表
        //multiNumberRedialDAO.insertCustomerShareStateHistory(item);
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

        // TODO ??? M4模式需要处理吗 ？

    }


    public void initialize() {
        customerPool.initialize();
    }

    public void dailyProc(List<ShareBatchItem> shareBatchItems) {
        customerPool.clear();

        loadCustomersDaily(shareBatchItems);
    }

    public void timeoutProc() {
        customerPool.timeoutProc();
    }

    /////////////////////////////////////////////////////////////

    private void procEndcode(String userId, MultiNumberRedialCustomer originCustomerItem,
                             EndCodeRedialStrategyM4 strategyItem, int phoneType,
                             String resultCodeType, String resultCode, Date presetTime) {

        Date now = new Date();

        MultiNumberRedialCustomer item = originCustomerItem.deepClone();
        item.setEndCodeType(resultCodeType);
        item.setEndCode(resultCode);
        item.setModifyUserId(userId);
        item.setModifyTime(now);
        item.setModifyId(originCustomerItem.getModifyId() + 1);

        PhoneDialInfo curPhoneDialInfo = item.getDialInfoByPhoneType(item.getCurDialPhoneType());
        curPhoneDialInfo.setDialCount( curPhoneDialInfo.getDialCount() + 1);
        curPhoneDialInfo.setLastDialTime(now);

        if (null == item.getFirstDialDate()) {
            item.setFirstDialDate(now);
        }

        // 调整 阶段数
        if (MultiNumberRedialStateEnum.CREATED.equals(originCustomerItem.getState())
            || MultiNumberRedialStateEnum.APPENDED.equals(originCustomerItem.getState())
            || MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL.equals(originCustomerItem.getState())) {
            item.setCurStageNum(item.getCurStageNum() + 1);
        }

        int strategyDayIndex = getStrategyDayIndex(item.getFirstDialDate(), strategyItem.getStageDelayDays());
        Map<Integer, Integer> phoneTypeVsDialCount = strategyItem.getPhoneTypeVsDialCount(strategyDayIndex);
        MultiNumberRedialStrategyEnum strategyEnum = strategyItem.getEndCodeRedialStrategy(resultCodeType, resultCode);

        if (MultiNumberRedialStrategyEnum.IsCustStop.equals(strategyEnum)) {
            item.setState(MultiNumberRedialStateEnum.FINISHED);

            // 更新共享状态表
            multiNumberRedialDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            multiNumberRedialDAO.insertCustomerShareStateHistory(item);

        } else if (MultiNumberRedialStrategyEnum.IsPresetDial.equals(strategyEnum)) {
            item.setState(MultiNumberRedialStateEnum.PRESET_DIAL);
            item.setNextDialPhoneType(item.getCurDialPhoneType());
            PhoneDialInfo phoneDialInfo = item.getDialInfoByPhoneType(item.getCurDialPhoneType());
            phoneDialInfo.setCausePresetDialCount( phoneDialInfo.getCausePresetDialCount() + 1);

            // 更新共享状态表
            item.setCurPresetDialTime(presetTime);
            multiNumberRedialDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            multiNumberRedialDAO.insertCustomerShareStateHistory(item);

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

        } else if (MultiNumberRedialStrategyEnum.IsPhoneStop.equals(strategyEnum)) {
            Integer nextDialPhoneType = customerPool.calcNextDialPhoneType(item);
            if (null != nextDialPhoneType) {
                item.setState(MultiNumberRedialStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
                item.setNextDialPhoneType(nextDialPhoneType);
            } else if (determineStageOver(item, strategyItem.getMaxStageCount())) {
                item.setState(MultiNumberRedialStateEnum.FINISHED);
            } else {
                item.setState(MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL);
                item.setCurPresetDialTime(DateUtil.getNextXDay(strategyItem.getStageDelayDays()));
                item.setNextDialPhoneType(null);
                clearPhoneTypeDialInfo(item);
            }

            if (!MultiNumberRedialStateEnum.FINISHED.equals(item.getState())) {
                if (MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL.equals(item.getState())) {
                    // 下阶段在今天，放回候选池
                    if (DateUtil.isSameDay(now, item.getCurPresetDialTime())) {
                        addCustomerToSharePool(item);
                    }
                } else {
                    addCustomerToSharePool(item);
                }
            }

            // 更新共享状态表
            multiNumberRedialDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            multiNumberRedialDAO.insertCustomerShareStateHistory(item);

        } else /*当前号码类型重拨*/ {

            if ((curPhoneDialInfo.getDialCount() - curPhoneDialInfo.getCausePresetDialCount()) >= phoneTypeVsDialCount.get(phoneType)) {
                Integer nextDialPhoneType = customerPool.calcNextDialPhoneType(item);
                if (null != nextDialPhoneType) {
                    item.setState(MultiNumberRedialStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
                    item.setNextDialPhoneType(nextDialPhoneType);
                } else if (determineStageOver(item, strategyItem.getMaxStageCount())) {
                    item.setState(MultiNumberRedialStateEnum.FINISHED);
                } else {
                    item.setState(MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL);
                    item.setCurPresetDialTime(DateUtil.getNextXDay(strategyItem.getStageDelayDays()));
                    item.setNextDialPhoneType(null);
                    clearPhoneTypeDialInfo(item);
                }
            } else {
                item.setState(MultiNumberRedialStateEnum.WAIT_REDIAL);
                //item.setCurPresetDialTime(DateUtil.getNextXMinute(strategyItem.getRedialDelayMinutes()));
            }

            if (!MultiNumberRedialStateEnum.FINISHED.equals(item.getState())) {
                if (MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL.equals(item.getState())) {
                    // 下阶段在今天，放回候选池
                    if (DateUtil.isSameDay(now, item.getCurPresetDialTime())) {
                        addCustomerToSharePool(item);
                    }
                } else {
                    addCustomerToSharePool(item);
                }
            }

            multiNumberRedialDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            multiNumberRedialDAO.insertCustomerShareStateHistory(item);
        }

        //若是当前是预约拨打，更新 预约状态 @ 预约表
        if (MultiNumberRedialStateEnum.PRESET_DIAL.equals(originCustomerItem.getState())) {
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
        List<MultiNumberRedialStateEnum> shareCustomerStateList = new ArrayList<MultiNumberRedialStateEnum>();
        shareCustomerStateList.add(MultiNumberRedialStateEnum.CREATED);
        shareCustomerStateList.add(MultiNumberRedialStateEnum.APPENDED);
        shareCustomerStateList.add(MultiNumberRedialStateEnum.WAIT_REDIAL);  // 没有重拨时间的
        shareCustomerStateList.add(MultiNumberRedialStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
        shareCustomerStateList.add(MultiNumberRedialStateEnum.REVERT);

        List<MultiNumberRedialStateEnum> shareStateListWithDialTime = new ArrayList<MultiNumberRedialStateEnum>();
        shareStateListWithDialTime.add(MultiNumberRedialStateEnum.PRESET_DIAL);
        shareStateListWithDialTime.add(MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL);

        List<MultiNumberRedialCustomer> shareDataItems = new ArrayList<MultiNumberRedialCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            shareDataItems.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = multiNumberRedialDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = multiNumberRedialDAO.getGivenBizCustomersByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareStateListWithDialTime, shareDataItems);


            // 收集客户共享状态为 MultiNumberRedialStateEnum.APPENDED 的客户信息
            // 后续需要更改状态为 MultiNumberRedialStateEnum.CREATED
            List<String> appendedStateCustomerIdList = new ArrayList<String>();

            for (MultiNumberRedialCustomer customerItem : shareDataItems) {
                addCustomerToSharePool(customerItem);

                if (MultiNumberRedialStateEnum.APPENDED.equals(customerItem.getState())) {
                    customerItem.setState(MultiNumberRedialStateEnum.CREATED);
                    appendedStateCustomerIdList.add(customerItem.getCustomerId());
                }
            }

            if (!appendedStateCustomerIdList.isEmpty())
                multiNumberRedialDAO.updateCustomerShareState(bizId, appendedStateCustomerIdList, MultiNumberRedialStateEnum.CREATED);
        }

        // 初始化等待池
        /*List<MultiNumberRedialStateEnum> waitPollStates = new ArrayList<MultiNumberRedialStateEnum>();
        waitPollStates.add(MultiNumberRedialStateEnum.EXTRACTED);
        waitPollStates.add(MultiNumberRedialStateEnum.PHONECONNECTED);
        waitPollStates.add(MultiNumberRedialStateEnum.SCREENPOPUP);

        List<MultiNumberRedialCustomer> waitResultCustoms = new ArrayList<MultiNumberRedialCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            waitResultCustoms.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = multiNumberRedialDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, waitPollStates, waitResultCustoms);

            for (MultiNumberRedialCustomer customerItem : waitResultCustoms) {
                addCustomerToWaitPool(customerItem);
            }
        }

        waitPoolPostProcess();*/
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

        List<MultiNumberRedialStateEnum> shareCustomerStateList = new ArrayList<MultiNumberRedialStateEnum>();
        shareCustomerStateList.add(MultiNumberRedialStateEnum.CREATED);
        //shareCustomerStateList.add(MultiNumberRedialStateEnum.APPENDED);
        shareCustomerStateList.add(MultiNumberRedialStateEnum.WAIT_REDIAL);  // 没有重拨时间的
        shareCustomerStateList.add(MultiNumberRedialStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
        shareCustomerStateList.add(MultiNumberRedialStateEnum.REVERT);

        List<MultiNumberRedialStateEnum> shareCustomerStateList2 = new ArrayList<MultiNumberRedialStateEnum>();
        shareCustomerStateList2.add(MultiNumberRedialStateEnum.PRESET_DIAL);
        shareCustomerStateList2.add(MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL);

        List<MultiNumberRedialCustomer> shareDataItems = new ArrayList<MultiNumberRedialCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            shareDataItems.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();


            // 成批从DB取数据
            Boolean result = multiNumberRedialDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = multiNumberRedialDAO.getGivenBizCustomersByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareCustomerStateList2, shareDataItems);

            for (MultiNumberRedialCustomer customerItem : shareDataItems) {
                addCustomerToSharePool(customerItem);
            }

        }
    }

    public void addCustomerToSharePool(MultiNumberRedialCustomer newCustomerItem) {
        Boolean ret = customerPool.add(newCustomerItem);

        System.out.println("M4 add customer: bizId[" + newCustomerItem.getBizId()
                + "] shareId[" + newCustomerItem.getShareBatchId() + "] IID[" + newCustomerItem.getImportBatchId()
                + "] CID[" + newCustomerItem.getCustomerId() + "] " + ret);
    }

    private void addCustomerToWaitPool(MultiNumberRedialCustomer newCustomerItem) {
        customerPool.addWaitResultCustomer(newCustomerItem);

        System.out.println("M4 add wait customer: bizId[" + newCustomerItem.getBizId()
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

        List<MultiNumberRedialStateEnum> shareCustomerStateList = new ArrayList<MultiNumberRedialStateEnum>();
        shareCustomerStateList.add(MultiNumberRedialStateEnum.APPENDED);

        List<MultiNumberRedialCustomer> shareDataItems = new ArrayList<MultiNumberRedialCustomer>();

        // TODO 成批从DB取数据
        Boolean result = multiNumberRedialDAO.getGivenBizCustomersByState(
                bizId, shareBatchItems, shareCustomerStateList, shareDataItems);

        // 记录客户共享状态为 MultiNumberRedialStateEnum.APPENDED 的客户信息
        // 后续需要更改状态为 MultiNumberRedialStateEnum.CREATED
        List<String> appendedStateCustomerIdList = new ArrayList<String>();

        for (MultiNumberRedialCustomer customerItem : shareDataItems) {
            addCustomerToSharePool(customerItem);

            if (MultiNumberRedialStateEnum.APPENDED.equals(customerItem.getState())) {
                customerItem.setState(MultiNumberRedialStateEnum.CREATED);
                appendedStateCustomerIdList.add(customerItem.getCustomerId());
            }
        }

        if (!appendedStateCustomerIdList.isEmpty()) {
            multiNumberRedialDAO.updateCustomerShareState(bizId, appendedStateCustomerIdList,
                    MultiNumberRedialStateEnum.CREATED);
        }
    }

    private int getStrategyDayIndex(Date firstDialDate, int stageDelayDays) {
        if (null == firstDialDate)
            return 1;

        int elapseDayNum = DateUtil.elapseDayNum(firstDialDate);
        return (elapseDayNum % (15 + stageDelayDays)) + 1;
    }

    private Boolean determineStageOver(MultiNumberRedialCustomer item, int maxStageCount) {
        return item.getCurStageNum() >= maxStageCount;
    }

    private void clearPhoneTypeDialInfo(MultiNumberRedialCustomer item) {
        for (int i=1; i<=10; i++) {
            PhoneDialInfo phoneDialInfo = item.getDialInfoByPhoneType(i);
            phoneDialInfo.setDialCount(0);
            phoneDialInfo.setCausePresetDialCount(0);
        }
    }

}
