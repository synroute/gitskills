package hiapp.modules.dm.multinumbermode;

import hiapp.modules.dm.Constants;
import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.multinumbermode.bo.*;
import hiapp.modules.dm.multinumbermode.dao.MultiNumberPredictModeDAO;
import hiapp.modules.dm.singlenumbermode.bo.*;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmsetting.DMBizPresetItem;
import hiapp.modules.dmsetting.DMPresetStateEnum;
import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MultiNumberOutboundDataManage {

    @Autowired
    MultiNumberPredictModeDAO multiNumberPredictModeDAO;

    @Autowired
    DMDAO dmDAO;

    @Autowired
    MultiNumberPredictCustomerSharePool customerSharePool;


    @Autowired
    private DataImportJdbc dataImportJdbc;

    @Autowired
    PhoneTypeDialSequence phoneTypeDialSequence;

    @Autowired
    EndCodeRedialStrategyM6 endCodeRedialStrategyM6;

    Timer dailyTimer;
    TimerTask dailyTimerTask;

    Timer timeoutTimer;
    TimerTask timeoutTimerTask;




    public synchronized MultiNumberCustomer extractNextOutboundCustomer(String userId, int bizId) {
        return customerSharePool.extractCustomer(userId, bizId);
    }

    public String submitHiDialerOutboundResult(String userId, int bizId, String importBatchId, String customerId, int phoneType,
                                                String resultCodeType, String resultCode) {
        if (resultCodeType.equals("1") && resultCode.equals("1"))
        {
            String dialType = "dialType";
            String customerCallId = "customerCallId";

            MultiNumberCustomer originCustomerItem = customerSharePool.getWaitCustomer(userId, bizId, importBatchId, customerId, phoneType);

            Date now = new Date();

            MultiNumberCustomer item = new MultiNumberCustomer();
            item.setState(MultiNumberPredictStateEnum.PHONECONNECTED);
            item.setBizId(originCustomerItem.getBizId());
            item.setShareBatchId(originCustomerItem.getShareBatchId());
            item.setImportBatchId(originCustomerItem.getImportBatchId());
            item.setCustomerId(originCustomerItem.getCustomerId());
            item.setEndCodeType(resultCodeType);
            item.setEndCode(resultCode);
            item.setModifyUserId(userId);
            item.setModifyTime(now);
            item.setModifyId(originCustomerItem.getModifyId() + 1);
            item.setCurDialPhoneType(originCustomerItem.getCurDialPhoneType());
            item.setCurDialPhone(originCustomerItem.getCurDialPhone());
            item.setShareBatchStartTime(originCustomerItem.getShareBatchStartTime());

            PhoneDialInfo originPhoneDialInfo = originCustomerItem.getDialInfo(originCustomerItem.getCurDialPhoneType());
            //originPhoneDialInfo.setDialCount( originPhoneDialInfo.getDialCount() + 1);
            originPhoneDialInfo.setLastDialTime(now);
            item.setDialInfo(originCustomerItem.getCurDialPhoneType(), originPhoneDialInfo);

            multiNumberPredictModeDAO.updateCustomerShareState(item);

            // 插入共享历史表
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);

            // 插入结果表
            dmDAO.updateDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId, originCustomerItem.getModifyId()); // MODIFYLAST 0
            dmDAO.insertDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId,
                    originCustomerItem.getModifyId() + 1, userId, dialType, now, customerCallId,
                    resultCodeType, resultCode);

            return "";
        }

        return submitOutboundResult(userId, bizId, importBatchId, customerId, phoneType, resultCodeType, resultCode,
                null, null, null);
    }

    public String submitAgentScreenPopUp(String userId, int bizId, String importBatchId, String customerId, int phoneType) {

        String dialType = "dialType";
        String customerCallId = "customerCallId";

        MultiNumberCustomer originCustomerItem = customerSharePool.getWaitCustomer(userId, bizId, importBatchId, customerId, phoneType);

        Date now = new Date();

        MultiNumberCustomer item = new MultiNumberCustomer();
        item.setState(MultiNumberPredictStateEnum.PHONECONNECTED);
        item.setBizId(originCustomerItem.getBizId());
        item.setShareBatchId(originCustomerItem.getShareBatchId());
        item.setImportBatchId(originCustomerItem.getImportBatchId());
        item.setCustomerId(originCustomerItem.getCustomerId());
        item.setEndCodeType(originCustomerItem.getEndCodeType());
        item.setEndCode(originCustomerItem.getEndCode());
        item.setModifyUserId(userId);
        item.setModifyTime(now);
        item.setModifyId(originCustomerItem.getModifyId() + 1);
        item.setCurDialPhoneType(originCustomerItem.getCurDialPhoneType());
        item.setCurDialPhone(originCustomerItem.getCurDialPhone());
        item.setShareBatchStartTime(originCustomerItem.getShareBatchStartTime());

        PhoneDialInfo originPhoneDialInfo = originCustomerItem.getDialInfo(originCustomerItem.getCurDialPhoneType());
        //originPhoneDialInfo.setDialCount( originPhoneDialInfo.getDialCount() + 1);
        //originPhoneDialInfo.setLastDialTime(now);
        item.setDialInfo(originCustomerItem.getCurDialPhoneType(), originPhoneDialInfo);

        multiNumberPredictModeDAO.updateCustomerShareState(item);

        // 插入共享历史表
        multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);

        // 插入结果表
        dmDAO.updateDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId, originCustomerItem.getModifyId()); // MODIFYLAST 0
        dmDAO.insertDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId,
                originCustomerItem.getModifyId() + 1, userId, dialType, originPhoneDialInfo.getLastDialTime(), customerCallId,
                item.getEndCodeType(), item.getEndCode());

        return "";
    }

    public String submitOutboundResult(String userId, int bizId, String importBatchId, String customerId, int phoneType,
                                       String resultCodeType, String resultCode, Date presetTime, String resultData,
                                       String customerInfo) {
        // TODO
        String dialType = "xxx";
        String customerCallId = "xxx";
        Date dialTime = new Date();

        MultiNumberCustomer originCustomerItem = customerSharePool.removeWaitCustomer(userId, bizId, importBatchId, customerId, phoneType);

        // 经过 Outbound 策略处理器
        EndCodeRedialStrategyM6Item strategyItem = endCodeRedialStrategyM6.getEndCodeRedialStrategyItem(
                                                        originCustomerItem.getBizId(), resultCodeType, resultCode);
        procEndcode(userId, originCustomerItem, strategyItem, resultCodeType, resultCode, presetTime, resultData);

        // 插入结果表
        //dataImportJdbc.insertDataToResultTable(bizId, shareBatchId, importBatchId, customerId, userId, resultData);
        dmDAO.updateDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId, originCustomerItem.getModifyId()); // MODIFYLAST 0
        dmDAO.insertDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId,
                originCustomerItem.getModifyId() + 1, userId, dialType, dialTime, customerCallId,
                resultCodeType, resultCode);

        // 插入导入客户表
        if (null == customerInfo) {
            dataImportJdbc.insertDataToImPortTable(bizId, importBatchId, customerId, userId, customerInfo, originCustomerItem.getModifyId() + 1);
        }

        return "";
    }

    public void lostProc(MultiNumberCustomer item) {
        String dialType = "dialType";
        String customerCallId = "customerCallId";

        int originModifyId = item.getModifyId();
        PhoneDialInfo originPhoneDialInfo = item.getDialInfo(item.getCurDialPhoneType());

        Date now = new Date();

        item.setState(MultiNumberPredictStateEnum.LOSS_WAIT_REDIAL);
        item.setModifyTime(now);
        item.setModifyId(originModifyId + 1);


        multiNumberPredictModeDAO.updateCustomerShareState(item);

        // 插入共享历史表
        multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);

        // 插入结果表
        dmDAO.updateDMResult(item.getBizId(), item.getShareBatchId(), item.getImportBatchId(), item.getCustomerId(),
                originModifyId); // MODIFYLAST 0
        dmDAO.insertDMResult(item.getBizId(), item.getShareBatchId(), item.getImportBatchId(), item.getCustomerId(),
                originModifyId + 1, item.getUserId(), dialType, originPhoneDialInfo.getLastDialTime(), customerCallId,
                item.getEndCodeType(), item.getEndCode());
    }

    /*
     * 重新初始化处理不适合，举例说明：系统运行中，进行启用共享批次操作，会导致获取外呼客户的功能暂停一段时间。
     *
     */
    public String startShareBatch(int bizId, List<String> shareBatchIds) {
        // 设置共享批次状态
        dmDAO.updateShareBatchState(bizId, shareBatchIds, ShareBatchStateEnum.ENABLE.getName());

        List<ShareBatchItem> shareBatchItems = shareBatchIncrementalProc();

        loadCustomersIncremental(shareBatchItems);

        return "";
    }

    public void stopShareBatch(int bizId, List<String> shareBatchIds) {

        customerSharePool.removeShareCustomer(bizId, shareBatchIds);

        customerSharePool.markShareBatchStopFromCustomerWaitPool(bizId, shareBatchIds);
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



    }


    public void initialize() {

    }

    public void dailyProc(List<ShareBatchItem> shareBatchItems) {
        customerSharePool.clear();

        loadCustomersDaily(shareBatchItems);
    }

    public void timeoutProc() {
        customerSharePool.timeoutProc();
    }


    /////////////////////////////////////////////////////////////

    private void procEndcode(String userId, MultiNumberCustomer originCustomerItem,
                             EndCodeRedialStrategyM6Item strategyItem,
                             String resultCodeType, String resultCode, Date presetTime, String resultData) {

        Date now = new Date();

        MultiNumberCustomer item = new MultiNumberCustomer();
        item.setBizId(originCustomerItem.getBizId());
        item.setShareBatchId(originCustomerItem.getShareBatchId());
        item.setImportBatchId(originCustomerItem.getImportBatchId());
        item.setCustomerId(originCustomerItem.getCustomerId());
        item.setEndCodeType(resultCodeType);
        item.setEndCode(resultCode);
        item.setModifyUserId(userId);
        item.setModifyTime(now);
        item.setModifyId(originCustomerItem.getModifyId() + 1);
        item.setCurDialPhoneType(originCustomerItem.getCurDialPhoneType());
        item.setCurDialPhone(originCustomerItem.getCurDialPhone());

        PhoneDialInfo originPhoneDialInfo = originCustomerItem.getDialInfo(originCustomerItem.getCurDialPhoneType());
        originPhoneDialInfo.setDialCount( originPhoneDialInfo.getDialCount() + 1);
        originPhoneDialInfo.setLastDialTime(now);
        item.setDialInfo(originCustomerItem.getCurDialPhoneType(), originPhoneDialInfo);

        item.setShareBatchStartTime(originCustomerItem.getShareBatchStartTime());

        if (strategyItem.getCustomerDialFinished()) {
            item.setState(MultiNumberPredictStateEnum.FINISHED);

            // 更新共享状态表
            multiNumberPredictModeDAO.updateCustomerShareState(item);

            // 插入共享历史表
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);

        } else if (strategyItem.getPresetDial()) {
            item.setState(MultiNumberPredictStateEnum.PRESET_DIAL);
            PhoneDialInfo phoneDialInfo = item.getDialInfo(item.getCurDialPhoneType());
            phoneDialInfo.setCausePresetDialCount( phoneDialInfo.getCausePresetDialCount() + 1);

            // 更新共享状态表
            item.setCurPresetDialTime(presetTime);
            multiNumberPredictModeDAO.updateCustomerShareState(item);

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

            // 不要移出候选池，预约在今天
            if (DateUtil.isSameDay(now, presetTime)) {
                addCustomerToSharePool(item);
            }

        } else if (strategyItem.getPhoneTypeDialFinished()) {
            item.setState(MultiNumberPredictStateEnum.WAIT_DIAL);

            int nextDialPhoneType = phoneTypeDialSequence.getNextDialPhoneType(item.getBizId(), item.getCurDialPhoneType());
            item.setNextDialPhoneType(nextDialPhoneType);

            multiNumberPredictModeDAO.updateCustomerShareState(item);

            // 插入共享历史表
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);

        } else /*当前号码类型重拨*/ {

            PhoneDialInfo phoneDialInfo = item.getDialInfo(item.getCurDialPhoneType());

            if ((phoneDialInfo.getDialCount() - phoneDialInfo.getCausePresetDialCount()) >= strategyItem.getMaxRedialNum()) {
                item.setState(MultiNumberPredictStateEnum.WAIT_DIAL);
                int nextDialPhoneType = phoneTypeDialSequence.getNextDialPhoneType(item.getBizId(), item.getCurDialPhoneType());
                item.setNextDialPhoneType(nextDialPhoneType);
            } else {
                item.setState(MultiNumberPredictStateEnum.WAIT_REDIAL);
                item.setCurPresetDialTime(DateUtil.getNextXMinute(strategyItem.getRedialDelayMinutes()));
            }

            addCustomerToSharePool(item);

            multiNumberPredictModeDAO.updateCustomerShareState(item);

            // 插入共享历史表
            multiNumberPredictModeDAO.insertCustomerShareStateHistory(item);
        }

        //若是当前是预约拨打，更新 预约状态 @ 预约表
        if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(originCustomerItem.getState())) {
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
        shareCustomerStateList.add(MultiNumberPredictStateEnum.WAIT_DIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.REVERT);

        List<MultiNumberPredictStateEnum> shareCustomerStateList2 = new ArrayList<MultiNumberPredictStateEnum>();
        shareCustomerStateList2.add(MultiNumberPredictStateEnum.PRESET_DIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.WAIT_REDIAL);

        List<MultiNumberCustomer> shareDataItems = new ArrayList<MultiNumberCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            shareDataItems.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();


            // 根据未接通拨打日期，决定是否清零<当日未接通重拨次数>
            //singleNumberModeDAO.clearPreviousDayLostCallCount(bizId);

            // 成批从DB取数据
            Boolean result = multiNumberPredictModeDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = multiNumberPredictModeDAO.getGivenBizCustomersByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareCustomerStateList2, shareDataItems);


            // 收集客户共享状态为 MultiNumberPredictStateEnum.APPENDED 的客户信息
            // 后续需要更改状态为 MultiNumberPredictStateEnum.CREATED
            List<String> appendedStateCustomerIdList = new ArrayList<String>();

            for (MultiNumberCustomer customerItem : shareDataItems) {
                addCustomerToSharePool(customerItem);

                if (MultiNumberPredictStateEnum.APPENDED.equals(customerItem.getState())) {
                    appendedStateCustomerIdList.add(customerItem.getShareBatchId());
                }
            }

            multiNumberPredictModeDAO.updateCustomerShareState(bizId, appendedStateCustomerIdList,
                                                    MultiNumberPredictStateEnum.CREATED);
        }

        // 初始化等待池
        List<MultiNumberPredictStateEnum> shareCustomerStateList3 = new ArrayList<MultiNumberPredictStateEnum>();
        shareCustomerStateList2.add(MultiNumberPredictStateEnum.PHONECONNECTED);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.SCREENPOPUP);

        List<MultiNumberCustomer> waitResultCustoms = new ArrayList<MultiNumberCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            waitResultCustoms.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = multiNumberPredictModeDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, waitResultCustoms);

            for (MultiNumberCustomer customerItem : waitResultCustoms) {
                addCustomerToWaitPool(customerItem);
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

        List<MultiNumberPredictStateEnum> shareCustomerStateList = new ArrayList<MultiNumberPredictStateEnum>();
        shareCustomerStateList.add(MultiNumberPredictStateEnum.CREATED);
        //shareCustomerStateList.add(MultiNumberPredictStateEnum.APPENDED);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.WAIT_DIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.REVERT);

        List<MultiNumberPredictStateEnum> shareCustomerStateList2 = new ArrayList<MultiNumberPredictStateEnum>();
        shareCustomerStateList2.add(MultiNumberPredictStateEnum.PRESET_DIAL);
        shareCustomerStateList.add(MultiNumberPredictStateEnum.WAIT_REDIAL);

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
        customerSharePool.add(newCustomerItem);

        System.out.println("share multinumber customer: bizId[" + newCustomerItem.getBizId()
                + "] shareId[" + newCustomerItem.getShareBatchId() + "] IID[" + newCustomerItem.getImportBatchId()
                + "] CID[" + newCustomerItem.getCustomerId() + "]");
    }

    private void addCustomerToWaitPool(MultiNumberCustomer newCustomerItem) {
        customerSharePool.addWaitResultCustomer(newCustomerItem);

        System.out.println("wait result multinumber customer: bizId[" + newCustomerItem.getBizId()
                + "] shareId[" + newCustomerItem.getShareBatchId() + "] IID[" + newCustomerItem.getImportBatchId()
                + "] CID[" + newCustomerItem.getCustomerId() + "]");
    }

    private List<ShareBatchItem> shareBatchIncrementalProc() {
        //List<String> expiredShareBatchIds = new ArrayList<String>();
        dmDAO.expireShareBatchsByEndTime(/*expiredShareBatchIds*/);

        List<ShareBatchItem> shareBatchItems = new ArrayList<ShareBatchItem>();
        dmDAO.getCurDayNeedActiveShareBatchItems(shareBatchItems);

        dmDAO.activateShareBatchByStartTime();

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

        // 记录客户共享状态为 SingleNumberModeShareCustomerStateEnum.APPENDED 的客户信息
        // 后续需要更改状态为 SingleNumberModeShareCustomerStateEnum.CREATED
        List<String> appendedStateCustomerIdList = new ArrayList<String>();

        for (MultiNumberCustomer customerItem : shareDataItems) {
            addCustomerToSharePool(customerItem);

            if (SingleNumberModeShareCustomerStateEnum.APPENDED.equals(customerItem.getState())) {
                appendedStateCustomerIdList.add(customerItem.getShareBatchId());
            }
        }

        multiNumberPredictModeDAO.updateCustomerShareState(bizId, appendedStateCustomerIdList,
                MultiNumberPredictStateEnum.CREATED);
    }

}
