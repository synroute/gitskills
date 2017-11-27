package hiapp.modules.dm.hidialermode;

import hiapp.modules.dm.hidialermode.bo.*;
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

/**
 * M2 Hidialer自动外呼
 * Hidialer 抽取数据，客户信息不需要按照共享批次分类，由于不存在访问权限问题
 */

@Service
public class HidialerOutboundDataManage {

    @Autowired
    HidialerModeDAO hidialerModeDAO;

    @Autowired
    DMDAO dmDAO;

    @Autowired
    HidialerModeCustomerSharePool customerPool;

    @Autowired
    private DataImportJdbc dataImportJdbc;

    @Autowired
    EndCodeRedialStrategyM2 endCodeRedialStrategyM2;

    @Autowired
    DmBizRepository dmBizRepository;

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

            customerList.add(customer);

            hidialerModeDAO.updateCustomerShareForExtract(customer);

            // 插入共享历史表
            hidialerModeDAO.insertCustomerShareStateHistory(customer);
        }

        return customerList;
    }

    public String hiDialerDialResultNotify(String userId, int bizId, String importBatchId, String customerId, int phoneType,
                                           String resultCodeType, String resultCode, String customerCallId)
    {
        if (resultCodeType.equals("1") && resultCode.equals("1"))
        {
            String dialType = "dialType";

            HidialerModeCustomer customer = customerPool.getWaitCustomer(userId, bizId, importBatchId, customerId);
            Date originModifyTime = customer.getModifyTime();
            int  originModifyId = customer.getModifyId();

            Date now = new Date();

            customer.setState(HidialerModeCustomerStateEnum.PHONECONNECTED);
            customer.setEndCodeType(resultCodeType);
            customer.setEndCode(resultCode);
            customer.setModifyUserId(userId);
            customer.setModifyId(customer.getModifyId() + 1);
            customer.setModifyTime(now);

            customerPool.hidialerPhoneConnect(customer, originModifyTime);  // NOTE: 使用原客户，等待池中一个客户有多个key

            hidialerModeDAO.updateCustomerShareForOutboundResult(customer);

            // 插入共享历史表
            hidialerModeDAO.insertCustomerShareStateHistory(customer);

            // 插入结果表
            //dmDAO.updateDMResult(bizId, customer.getShareBatchId(), importBatchId, customerId, originModifyId); // MODIFYLAST 0
            //dmDAO.insertDMResult(bizId, customer.getShareBatchId(), importBatchId, customerId,
            //        customer.getModifyId(), userId, dialType, now, customerCallId,
            //        resultCodeType, resultCode);

            return "";
        }

        return submitOutboundResult(userId, bizId, importBatchId, customerId, resultCodeType, resultCode,
                null, null, null, null, null, null);
    }

    public String submitAgentScreenPopUp(String userId, int bizId, String importBatchId, String customerId, int phoneType) {

        String dialType = "dialType";
        String customerCallId = "customerCallId";

        HidialerModeCustomer customer = customerPool.getWaitCustomer(userId, bizId, importBatchId, customerId);
        Date originModifyTime = customer.getModifyTime();
        int  originModifyId = customer.getModifyId();

        Date now = new Date();

        customer.setState(HidialerModeCustomerStateEnum.SCREENPOPUP);
        customer.setModifyUserId(userId);
        customer.setModifyId(customer.getModifyId() + 1);
        customer.setModifyTime(now);

        customerPool.agentScreenPopUp(customer, originModifyTime); // NOTE: 使用原客户，等待池中一个客户有多个key

        hidialerModeDAO.updateCustomerShareForOutboundResult(customer);

        // 插入共享历史表
        hidialerModeDAO.insertCustomerShareStateHistory(customer);

        // 插入结果表
        //dmDAO.updateDMResult(bizId, customer.getShareBatchId(), importBatchId, customerId, originModifyId); // MODIFYLAST 0
        //dmDAO.insertDMResult(bizId, customer.getShareBatchId(), importBatchId, customerId,
        //        customer.getModifyId(), userId, dialType, lastDialTime, customerCallId,
        //        customer.getEndCodeType(), customer.getEndCode());

        return "";
    }

    public String submitOutboundResult(String userId, int bizId, String importBatchId, String customerId,
                                       String resultCodeType, String resultCode, Boolean isPreset, Date presetTime,
                                       String dialType, Date dialTime, String customerCallId, String customerInfo) {

        HidialerModeCustomer originCustomerItem = customerPool.removeWaitCustomer(
                HiDialerUserId, bizId, importBatchId, customerId);

        // 经过 Outbound 策略处理器
        EndCodeRedialStrategyM2Item strategyItem = endCodeRedialStrategyM2.getEndCodeRedialStrategyItem(
                                                        originCustomerItem.getBizId(), resultCodeType, resultCode);
        procEndcode(userId, originCustomerItem, strategyItem, resultCodeType, resultCode, presetTime);

        if (!HiDialerUserId.equals(userId)) {
            // 插入结果表
            dmDAO.updateDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId, originCustomerItem.getModifyId()); // MODIFYLAST 0
            dmDAO.insertDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId,
                    originCustomerItem.getModifyId() + 1, userId, dialType, dialTime, customerCallId,
                    resultCodeType, resultCode);

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
    public void lostProc(HidialerModeCustomer item) {
        String dialType = "dialType";
        String customerCallId = "customerCallId";

        int originModifyId = item.getModifyId();

        Date now = new Date();

        item.setState(HidialerModeCustomerStateEnum.LOSS_WAIT_REDIAL);
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
    }

    public void dailyProc(List<ShareBatchItem> shareBatchItems) {
        customerPool.clear();

        loadCustomersDaily(shareBatchItems);
    }

    public void timeoutProc() {
        System.out.println("MultiNumberOutboundDataManage TimeOut ...");
        customerPool.timeoutProc();
    }


    /////////////////////////////////////////////////////////////

    private void procEndcode(String userId, HidialerModeCustomer originCustomerItem,
                             EndCodeRedialStrategyM2Item strategyItem,
                             String resultCodeType, String resultCode, Date presetTime) {

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

        } else if (strategyItem.getPresetDial()) {
            //item.setState(HidialerModeCustomerStateEnum.PRESET_DIAL);

            // 更新共享状态表
            //item.setCurPresetDialTime(presetTime);
            hidialerModeDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            hidialerModeDAO.insertCustomerShareStateHistory(item);

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
//            Integer nextDialPhoneType = customerPool.calcNextDialPhoneType(item);
//            if (null != nextDialPhoneType) {
//                item.setState(HidialerModeCustomerStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
//                item.setNextDialPhoneType(nextDialPhoneType);
//            } else {
//                item.setState(HidialerModeCustomerStateEnum.FINISHED);
//            }

            if (!HidialerModeCustomerStateEnum.FINISHED.equals(item.getState())) {
                addCustomerToSharePool(item);
            }

            // 更新共享状态表
            hidialerModeDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            hidialerModeDAO.insertCustomerShareStateHistory(item);

        } else /*当前号码类型重拨*/ {

//            if ((phoneDialInfo.getDialCount() - phoneDialInfo.getCausePresetDialCount()) >= strategyItem.getMaxRedialNum()) {
//                Integer nextDialPhoneType = customerPool.calcNextDialPhoneType(item);
//                if (null != nextDialPhoneType) {
//                    item.setState(HidialerModeCustomerStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
//                    item.setNextDialPhoneType(nextDialPhoneType);
//                } else {
//                    item.setState(HidialerModeCustomerStateEnum.FINISHED);
//                }
//            } else {
//                item.setState(HidialerModeCustomerStateEnum.WAIT_REDIAL);
//                item.setCurPresetDialTime(DateUtil.getNextXMinute(strategyItem.getRedialDelayMinutes()));
//            }

            if (!HidialerModeCustomerStateEnum.FINISHED.equals(item.getState())) {
                addCustomerToSharePool(item);
            }

            hidialerModeDAO.updateCustomerShareForOutboundResult(item);

            // 插入共享历史表
            hidialerModeDAO.insertCustomerShareStateHistory(item);
        }

        //若是当前是预约拨打，更新 预约状态 @ 预约表
//        if (HidialerModeCustomerStateEnum.PRESET_DIAL.equals(originCustomerItem.getState())) {
//            dmDAO.updatePresetState(item.getBizId(), item.getShareBatchId(), item.getImportBatchId(),
//                    item.getCustomerId(), originCustomerItem.getModifyId(),
//                    DMPresetStateEnum.FinishPreset.getStateName());
//        }

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
        //shareCustomerStateList.add(HidialerModeCustomerStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.REVERT);

        List<HidialerModeCustomerStateEnum> shareStateListWithDialTime = new ArrayList<HidialerModeCustomerStateEnum>();
        //shareStateListWithDialTime.add(HidialerModeCustomerStateEnum.PRESET_DIAL);
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


            // 收集客户共享状态为 MultiNumberPredictStateEnum.APPENDED 的客户信息
            // 后续需要更改状态为 MultiNumberPredictStateEnum.CREATED
            List<String> appendedStateCustomerIdList = new ArrayList<String>();

            for (HidialerModeCustomer customerItem : shareDataItems) {
                addCustomerToSharePool(customerItem);

                if (HidialerModeCustomerStateEnum.APPENDED.equals(customerItem.getState())) {
                    appendedStateCustomerIdList.add(customerItem.getShareBatchId());
                }
            }

            if (!appendedStateCustomerIdList.isEmpty())
                hidialerModeDAO.updateCustomerShareState(bizId, appendedStateCustomerIdList, HidialerModeCustomerStateEnum.CREATED);
        }

        // 初始化等待池
        List<HidialerModeCustomerStateEnum> waitPollStates = new ArrayList<HidialerModeCustomerStateEnum>();
        waitPollStates.add(HidialerModeCustomerStateEnum.EXTRACTED);
        waitPollStates.add(HidialerModeCustomerStateEnum.PHONECONNECTED);
        waitPollStates.add(HidialerModeCustomerStateEnum.SCREENPOPUP);

        List<HidialerModeCustomer> waitResultCustoms = new ArrayList<HidialerModeCustomer>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            waitResultCustoms.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = hidialerModeDAO.getGivenBizCustomersByState(
                    bizId, givenBizShareBatchItems, waitPollStates, waitResultCustoms);

            for (HidialerModeCustomer customerItem : waitResultCustoms) {
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

        List<HidialerModeCustomerStateEnum> shareCustomerStateList = new ArrayList<HidialerModeCustomerStateEnum>();
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.CREATED);
        //shareCustomerStateList.add(HidialerModeCustomerStateEnum.APPENDED);
        //shareCustomerStateList.add(HidialerModeCustomerStateEnum.NEXT_PHONETYPE_WAIT_DIAL);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.LOSS_WAIT_REDIAL);
        shareCustomerStateList.add(HidialerModeCustomerStateEnum.REVERT);

        List<HidialerModeCustomerStateEnum> shareCustomerStateList2 = new ArrayList<HidialerModeCustomerStateEnum>();
        //shareCustomerStateList2.add(HidialerModeCustomerStateEnum.PRESET_DIAL);
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
        //Boolean ret = customerPool.add(newCustomerItem);

        System.out.println("share multinumber customer: bizId[" + newCustomerItem.getBizId()
                + "] shareId[" + newCustomerItem.getShareBatchId() + "] IID[" + newCustomerItem.getImportBatchId()
                + "] CID[" + newCustomerItem.getCustomerId() + "] " );
    }

    private void addCustomerToWaitPool(HidialerModeCustomer newCustomerItem) {
        //customerPool.addWaitResultCustomer(newCustomerItem);

        System.out.println("wait result multinumber customer: bizId[" + newCustomerItem.getBizId()
                + "] shareId[" + newCustomerItem.getShareBatchId() + "] IID[" + newCustomerItem.getImportBatchId()
                + "] CID[" + newCustomerItem.getCustomerId() + "]");
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
        List<String> appendedStateCustomerIdList = new ArrayList<String>();

        for (HidialerModeCustomer customerItem : shareDataItems) {
            addCustomerToSharePool(customerItem);

            if (HidialerModeCustomerStateEnum.APPENDED.equals(customerItem.getState())) {
                appendedStateCustomerIdList.add(customerItem.getShareBatchId());
            }
        }

        if (!appendedStateCustomerIdList.isEmpty()) {
            hidialerModeDAO.updateCustomerShareState(bizId, appendedStateCustomerIdList,
                    HidialerModeCustomerStateEnum.CREATED);
        }
    }

}
