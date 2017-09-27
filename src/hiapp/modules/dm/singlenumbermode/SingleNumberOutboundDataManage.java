package hiapp.modules.dm.singlenumbermode;

import com.google.gson.Gson;
import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.*;
import hiapp.modules.dm.singlenumbermode.dao.SingleNumberModeDAO;
import hiapp.modules.dm.dao.DMDAO;

import hiapp.modules.dmmanager.data.DMBizMangeShare;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmsetting.DMBizPresetItem;
import hiapp.modules.dmsetting.DMPresetStateEnum;
import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import hiapp.utils.database.DBConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class SingleNumberOutboundDataManage {

    @Autowired
    SingleNumberModeDAO singleNumberModeDAO;

    @Autowired
    DMDAO dmDAO;

    @Autowired
    private DmBizOutboundConfigRepository dmBizOutboundConfig;

    @Autowired
    private DataImportJdbc dataImportJdbc;

    @Autowired
    private DMBizMangeShare dmBizMangeShare;

    @Autowired
    @Qualifier("tenantDBConnectionPool")
    public void setDBConnectionPool(DBConnectionPool dbConnectionPool) {
        this.dbConnectionPool = dbConnectionPool;

        initialize();
    }

    // 客户共享池
    // BizID <==> {ShareBatchID <==> PriorityBlockingQueue<SingleNumberModeShareCustomerItem>}
    Map<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>> mapPresetDialCustomer;
    Map<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>> mapPhaseDialCustomer;
    Map<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>> mapDialCustomer;

    // 客户数据桩， 用于拨打结果处理
    // BizID <==> {ImportID + CustomerID <==> SingleNumberModeShareCustomerItem}
    Map<Integer, Map<String, SingleNumberModeShareCustomerItem>> mapBizIdVsCustomer;

    // 重拨策略
    // BizID <==> EndCodeRedialStrategy
    Map<Integer, EndCodeRedialStrategy> mapBizIdVsEndCodeRedialStrategy;

    DBConnectionPool dbConnectionPool;
    Timer dailyTimer;
    TimerTask dailyTimerTask;


    /**
     * 获取下个外呼客户
     * @param userId
     * @param bizId
     * @return
     */
    public synchronized SingleNumberModeShareCustomerItem extractNextOutboundCustomer(String userId, int bizId) {
        Date now = new Date();

        SingleNumberModeShareCustomerItem shareDataItem = null;

        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);

        Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>> shareBatchIdVsCustomerMap = null;
        PriorityBlockingQueue<SingleNumberModeShareCustomerItem> customerQueue = null;

        // TODO 目前取得就走了，其实可以PEEK遍后比较拨打时间，确定先取那个客户
        shareBatchIdVsCustomerMap = mapPresetDialCustomer.get(bizId);
        if (null != shareBatchIdVsCustomerMap) {
            for (String shareBatchId : shareBatchIdList) {
                customerQueue = shareBatchIdVsCustomerMap.get(shareBatchId);
                if (null == customerQueue)
                    continue;

                shareDataItem = customerQueue.peek();
                if (null == shareDataItem)
                    continue;

                if (shareDataItem.getNextDialTime().before(now)) {
                    shareDataItem = customerQueue.poll();
                    break;
                }
            }
        }

        if (null == shareDataItem) {
            shareBatchIdVsCustomerMap = mapPhaseDialCustomer.get(bizId);
            if (null != shareBatchIdVsCustomerMap) {
                for (String shareBatchId : shareBatchIdList) {
                    customerQueue = shareBatchIdVsCustomerMap.get(shareBatchId);
                    if (null == customerQueue)
                        continue;

                    shareDataItem = customerQueue.poll();
                    if (null != shareDataItem)
                        break;
                }
            }
        }

        if (null == shareDataItem) {
            shareBatchIdVsCustomerMap = mapDialCustomer.get(bizId);
            if (null != shareBatchIdVsCustomerMap) {
                for (String shareBatchId : shareBatchIdList) {
                    customerQueue = shareBatchIdVsCustomerMap.get(shareBatchId);
                    if (null == customerQueue)
                        continue;

                    shareDataItem = customerQueue.poll();
                    if (null != shareDataItem)
                        break;
                }
            }
        }

        if (null != shareDataItem)
            singleNumberModeDAO.setUserUseState(bizId, shareDataItem.getShareBatchId(), shareDataItem.getCustomerId());

        return shareDataItem;
    }

    public String submitOutboundResult(String userId, int bizId, String importBatchId, String shareBatchId, String customerId,
                                       String resultCodeType, String resultCode, Date presetTime, String resultData, String customerInfo) {

        String dialType = "xxx";
        String customerCallId = "xxx";
        String dialTime = new Date().toString();

        Map<String, SingleNumberModeShareCustomerItem> map = mapBizIdVsCustomer.get(bizId);
        SingleNumberModeShareCustomerItem originCustomerItem = map.get(importBatchId + customerId);

        EndCodeRedialStrategy endCodeRedialStrategy = mapBizIdVsEndCodeRedialStrategy.get(bizId);
        if (null == endCodeRedialStrategy) {
            endCodeRedialStrategy = getEndCodeRedialStrategyByBizId(bizId);
            mapBizIdVsEndCodeRedialStrategy.put(bizId, endCodeRedialStrategy);
        }

        // 经过 Outbound 策略处理器
        procEndcode(userId, originCustomerItem, endCodeRedialStrategy, resultCodeType, resultCode, presetTime, resultData);

        // 插入结果表
        //dataImportJdbc.insertDataToResultTable(bizId, shareBatchId, importBatchId, customerId, userId, resultData);
        dmDAO.insertDMResult(bizId, shareBatchId, importBatchId, customerId, originCustomerItem.getModifyId() + 1,
                            userId, dialType, dialTime, customerCallId);
        dmDAO.updateDMResult(bizId, shareBatchId, importBatchId, customerId); // MODIFYLAST 0

        // 插入导入客户表
        dataImportJdbc.insertDataToImPortTable(bizId, importBatchId, customerId, userId, customerInfo);

        return "";
    }

    /*
     * 重新初始化处理不适合，举例说明：系统运行中，进行启用共享批次操作，会导致获取外呼客户的功能暂停一段时间。
     *
     */
    public String startShareBatch(int bizId, List<String> shareBatchIds) {

        // TODO 设置共享批次状态
        singleNumberModeDAO.updateShareBatchState(bizId, shareBatchIds, ShareBatchStateEnum.ENABLE.getName());

        List<ShareBatchItem> shareBatchItems = shareBatchIncrementalProc();

        loadCustomersIncremental(shareBatchItems);
        return "";
    }

    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        PriorityBlockingQueue<SingleNumberModeShareCustomerItem> queue;
        Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>> shareBatchIdVsCustomerMap = null;
        shareBatchIdVsCustomerMap = mapPresetDialCustomer.get(bizId);
        for (String shareBatchId : shareBatchIds) {
            queue = shareBatchIdVsCustomerMap.remove(shareBatchId);
            SingleNumberModeShareCustomerItem customerItem = queue.poll();
            removeBizIdVsCustomerMap(customerItem.getBizId(), customerItem);
        }

        shareBatchIdVsCustomerMap = mapPhaseDialCustomer.get(bizId);
        for (String shareBatchId : shareBatchIds) {
            queue = shareBatchIdVsCustomerMap.remove(shareBatchId);
            SingleNumberModeShareCustomerItem customerItem = queue.poll();
            removeBizIdVsCustomerMap(customerItem.getBizId(), customerItem);
        }

        shareBatchIdVsCustomerMap = mapDialCustomer.get(bizId);
        for (String shareBatchId : shareBatchIds) {
            queue = shareBatchIdVsCustomerMap.remove(shareBatchId);
            SingleNumberModeShareCustomerItem customerItem = queue.poll();
            removeBizIdVsCustomerMap(customerItem.getBizId(), customerItem);
        }
    }

    public String appendCustomersToShareBatch(int bizId, List<String> shareBatchIds) {

        // 获取ACTIVE状态的 shareBatchIds


        List<ShareBatchItem> shareBatchItemList = new ArrayList<ShareBatchItem>();
        dmDAO.getActiveShareBatchItems(shareBatchIds, shareBatchItemList);

        loadCustomersAppend(bizId, shareBatchItemList);
        return "";
    }

    public void initialize() {

        /*EndCodeRedialStrategyFromDB endCodeRedialStrategyFromDB = new EndCodeRedialStrategyFromDB();
        RedialState redialState = new RedialState();
        redialState.setDescription("description");
        redialState.setLoopRedialCountExceedNextState("loopRedialCountExceedNextStateName");
        redialState.setLoopRedialDialCount("9");
        redialState.setLoopRedialFirstDialDayDialCountLimit("2");
        redialState.setLoopRedialPerdayCountLimit("3");
        redialState.setName("statename");
        redialState.setStateType(RedialStateTypeEnum.REDIAL_STATE_FINISHED);
        redialState.setStageRedialDelayDays("20");

        endCodeRedialStrategyFromDB.addRedialState(redialState);
        endCodeRedialStrategyFromDB.addRedialState(redialState);
        endCodeRedialStrategyFromDB.addEndCodeRedialStrategyItem();

        String jsonObject=new Gson().toJson(endCodeRedialStrategyFromDB);
        System.out.println(jsonObject);

        EndCodeRedialStrategyFromDB tmp00 = new Gson().fromJson(jsonObject,
                EndCodeRedialStrategyFromDB.class);*/

        //EndCodeRedialStrategy endCodeRedialStrategy = getEndCodeRedialStrategyByBizId(20);

        /*String tmp = "{\"bizId\":11,\"importBatchId\":77\" }";
        Map<String, String> map = new Gson().fromJson(tmp, Map.class);
        String strBizId = map.get("bizId");
        String resultCodeType = map.get("resultCodeType");
        String resultCode = map.get("resultCode");
        String importBatchId = map.get("importBatchId");
        String shareBatchId = map.get("shareBatchId");
        String customerId = map.get("customerId");
        String resultData = map.get("resultData");
        String customerInfo = map.get("customerInfo");*/


        setDailyRoutine();

        mapPresetDialCustomer = new HashMap<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>>();
        mapPhaseDialCustomer = new HashMap<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>>();
        mapDialCustomer = new HashMap<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>>();

        mapBizIdVsCustomer = new HashMap<Integer, Map<String, SingleNumberModeShareCustomerItem>>();

        //singleNumberModeDAO.resetLoadedFlag();

        List<ShareBatchItem> shareBatchItems = shareBatchDailyProc();
        loadCustomersDaily(shareBatchItems);

//        SingleNumberModeShareCustomerItem item2 = extractNextOutboundCustomer("800", 800);
//        System.out.println("===");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<ShareBatchItem> shareBatchDailyProc() {
        mapPresetDialCustomer.clear();
        mapPhaseDialCustomer.clear();
        mapDialCustomer.clear();

        //List<String> expiredShareBatchIds = new ArrayList<String>();
        dmDAO.expireShareBatchsByEndTime(/*expiredShareBatchIds*/);

        dmDAO.activateShareBatchByStartTime();

        List<ShareBatchItem> shareBatchItems = new ArrayList<ShareBatchItem>();
        Boolean result = dmDAO.getAllActiveShareBatchItems(shareBatchItems);
        return shareBatchItems;
    }

    private List<ShareBatchItem> shareBatchIncrementalProc() {
        //List<String> expiredShareBatchIds = new ArrayList<String>();
        dmDAO.expireShareBatchsByEndTime(/*expiredShareBatchIds*/);

        List<ShareBatchItem> shareBatchItems = new ArrayList<ShareBatchItem>();
        dmDAO.getNeedActiveShareBatchItems(shareBatchItems);

        dmDAO.activateShareBatchByStartTime();

        return shareBatchItems;
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

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.CREATED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.APPENDED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.REVERT);

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList2 = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL);
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);

        List<SingleNumberModeShareCustomerItem> shareDataItems = new ArrayList<SingleNumberModeShareCustomerItem>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {
            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();


            shareDataItems.clear();

            System.out.println("bizId " + bizId + "... ");

            // TODO 根据未接通拨打日期，决定是否清零<当日未接通重拨次数>
            singleNumberModeDAO.clearPreviousDayLostCallCount(bizId);

            // TODO 如果保留<当日未接通重拨次数>，需要判断是否移出候选池（per endcode）
            {
                List<SingleNumberModeShareCustomerItem> shareDataItems2 = new ArrayList<SingleNumberModeShareCustomerItem>();
                singleNumberModeDAO.getCurrentLostCallStateCustomers(bizId, givenBizShareBatchItems, shareDataItems2);
            }

            // TODO 成批从DB取数据
            Boolean result = singleNumberModeDAO.getGivenBizShareDataItemsByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // TODO 可以改成批从DB取数据, 根据nextDialTime
            result = singleNumberModeDAO.getGivenBizShareDataItemsByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareCustomerStateList2, shareDataItems);

            // 记录客户共享状态为 SingleNumberModeShareCustomerStateEnum.APPENDED 的客户信息
            // 后续需要更改状态为 SingleNumberModeShareCustomerStateEnum.CREATED
            List<String> appendedStateCustomerIdList = new ArrayList<String>();

            for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {

                if (needJoinCustomerPool(bizId, customerItem))
                    addCustomerToPool(customerItem);

                if (SingleNumberModeShareCustomerStateEnum.APPENDED.equals(customerItem.getState())) {
                    appendedStateCustomerIdList.add(customerItem.getShareBatchId());
                }
            }

            singleNumberModeDAO.updateCustomerShareState(bizId, appendedStateCustomerIdList,
                    SingleNumberModeShareCustomerStateEnum.CREATED.getName());
        }
    }

    // 用于共享批次的启用，根据共享批次，不会导致重复加载
    private void loadCustomersIncremental(List<ShareBatchItem> shareBatchItems) {

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
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL);
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);

        List<SingleNumberModeShareCustomerItem> shareDataItems = new ArrayList<SingleNumberModeShareCustomerItem>();

        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            System.out.println("key= " + bizId + "...");

            // TODO 成批从DB取数据
            Boolean result = singleNumberModeDAO.getGivenBizShareDataItemsByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // TODO 成批从DB取数据, 根据nextDialTime
            result = singleNumberModeDAO.getGivenBizShareDataItemsByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareCustomerStateList2, shareDataItems);
        }

        for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {
            addCustomerToPool(customerItem);
        }
    }

    //匿名Comparator实现
    private static Comparator<SingleNumberModeShareCustomerItem> nextDialTimeComparator = new Comparator<SingleNumberModeShareCustomerItem>() {

        @Override
        public int compare(SingleNumberModeShareCustomerItem c1, SingleNumberModeShareCustomerItem c2) {
            return (c1.getNextDialTime().before(c2.getNextDialTime())) ? 1 : -1;
        }
    };

    //匿名Comparator实现
    private static Comparator<SingleNumberModeShareCustomerItem> shareBatchBeginTimeComparator = new Comparator<SingleNumberModeShareCustomerItem>() {

        @Override
        public int compare(SingleNumberModeShareCustomerItem c1, SingleNumberModeShareCustomerItem c2) {
            return (c1.getShareBatchStartTime().before(c2.getShareBatchStartTime())) ? 1 : -1;
        }
    };

    private void setDailyRoutine() {
        dailyTimer = new Timer();
        dailyTimerTask = new TimerTask() {
            @Override
            public void run() {
                List<ShareBatchItem> shareBatchItems = shareBatchDailyProc();
                loadCustomersDaily(shareBatchItems);

                /*
                // 移除过期的共享批次
                PriorityBlockingQueue<SingleNumberModeShareCustomerItem> presetDialPriorityQueue;
                PriorityBlockingQueue<SingleNumberModeShareCustomerItem> phaseDialPriorityQueue;
                PriorityBlockingQueue<SingleNumberModeShareCustomerItem> dialPriorityQueue;

                for (presetDialPriorityQueue.) {

                }
                */
            }
        };

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 2); // 控制时
        calendar.set(Calendar.MINUTE, 0);      // 控制分
        calendar.set(Calendar.SECOND, 0);      // 控制秒


        dailyTimer.scheduleAtFixedRate(dailyTimerTask, calendar.getTime(), 1000 * 60 * 60 * 24);
    }

    private void insertBizIdVsCustomerMap(int bizId, SingleNumberModeShareCustomerItem customerItem) {
        Map<String, SingleNumberModeShareCustomerItem> map = mapBizIdVsCustomer.get(bizId);
        if (null == map) {
            map = new HashMap<String, SingleNumberModeShareCustomerItem>();
            mapBizIdVsCustomer.put(bizId, map);
        }

        map.put(customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);
    }

    private void removeBizIdVsCustomerMap(int bizId, SingleNumberModeShareCustomerItem customerItem) {
        Map<String, SingleNumberModeShareCustomerItem> map = mapBizIdVsCustomer.get(bizId);
        if (null == map)
            return;

        map.remove(customerItem.getImportBatchId() + customerItem.getCustomerId(), customerItem);
    }

    private void reviseCustomerInfo() {
        // TODO
    }

    private void procEndcode(String userId, SingleNumberModeShareCustomerItem originCustomerItem,
                             EndCodeRedialStrategy endCodeRedialStrategy,
                             String resultCodeType, String resultCode, Date presetTime, String resultData) {

        Date today = new Date();

        RedialState newRedialState = endCodeRedialStrategy.getNextRedialState(resultCodeType, resultCode);
        RedialStateTypeEnum redialStateType = newRedialState.getStateTypeEnum();

        SingleNumberModeShareCustomerItem item = new SingleNumberModeShareCustomerItem();
        item.setBizId(originCustomerItem.getBizId());
        item.setShareBatchId(originCustomerItem.getShareBatchId());
        item.setCustomerId(originCustomerItem.getCustomerId());
        item.setEndCodeType(resultCodeType);
        item.setEndCode(resultCode);
        item.setModifyUserId(userId);
        item.setModifyTime(today);
        item.setModifyId(originCustomerItem.getModifyId() + 1);

        if (RedialStateTypeEnum.REDIAL_STATE_FINISHED.equals(redialStateType)) {
            item.setState(SingleNumberModeShareCustomerStateEnum.FINISHED);

            // 更新共享状态表
            singleNumberModeDAO.updateCustomerShareStateToFinish(item);

            // 插入共享历史表
            singleNumberModeDAO.insertCustomerShareStateHistory(item);

        } else if (RedialStateTypeEnum.REDIAL_STATE_PRESET.equals(redialStateType)) {
            item.setState(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);

            // 更新共享状态表   nextDialTime
            item.setNextDialTime(presetTime);
            singleNumberModeDAO.updateCustomerShareStateToPreset(item);

            // 插入共享历史表
            singleNumberModeDAO.insertCustomerShareStateHistory(item);

            // 插入预约表
            DMBizPresetItem presetItem = new DMBizPresetItem();
            presetItem.setSourceId(originCustomerItem.getShareBatchId());
            presetItem.setCustomerId(originCustomerItem.getCustomerId());
            presetItem.setImportId(originCustomerItem.getImportBatchId());
            presetItem.setPresetTime(presetTime);
            presetItem.setState(DMPresetStateEnum.InUse.getStateName());
            presetItem.setComment("xxx");
            presetItem.setModifyId(item.getModifyId());
            presetItem.setModifyLast(1);            // TODO 更新原来记录未0
            presetItem.setModifyUserId(userId);
            presetItem.setModifyTime(today);
            presetItem.setModifyDesc("xxx");
            presetItem.setPhoneType("xxx");
            dmDAO.insertPresetItem(originCustomerItem.getBizId(), presetItem);

            // 不要移出候选池，预约在今天
            if (isSameDay(today, presetTime)) {
                addCustomerToPool(item);
            }

        } else if (RedialStateTypeEnum.REDIAL_STATE_PHASE.equals(redialStateType)) {
            item.setState(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL);

            // 更新共享状态表  nextDialTime  curRedialStageCount
            // 到达最后阶段，直接跳转状态
            item.setCurRedialStageCount(originCustomerItem.getCurRedialStageCount() + 1);
            if (item.getCurRedialStageCount() >= endCodeRedialStrategy.getStageLimit()) {
                item.setState(SingleNumberModeShareCustomerStateEnum.getFromString(
                        endCodeRedialStrategy.getStageExceedNextStateName()));
            } else {
                item.setNextDialTime(getNextXDay(newRedialState.getStageRedialDelayDaysNum()));
            }

            singleNumberModeDAO.updateCustomerShareStateToStage(item);

            // 插入共享历史表
            singleNumberModeDAO.insertCustomerShareStateHistory(item);

        } else if (RedialStateTypeEnum.REDIAL_STATE_LOSTCALL.equals(redialStateType)) {
            item.setState(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);

            // 更新共享状态表  lostcallFirstDay  curDayLostCallCount  lostcallTotalCount
            // 总未接通数到达限定值，直接跳转状态
            // 每天未接通数到达限定值，移出候选池。每天处理时重新移回候选池。
            if (0 == originCustomerItem.getLostCallTotalCount()) {
                // 第一次发生未接通 的情形
                item.setLostCallFirstDay(today);
                originCustomerItem.setLostCallFirstDay(today);  // 必须设置，为了保持后续一致
            }

            item.setLostCallTotalCount(originCustomerItem.getLostCallTotalCount() + 1);
            item.setLostCallCurDayCount(originCustomerItem.getLostCallCurDayCount() + 1);
            if (item.getLostCallTotalCount() >= newRedialState.getLoopRedialDialCountNum()) {
                item.setState(SingleNumberModeShareCustomerStateEnum.getFromString(
                        newRedialState.getLoopRedialCountExceedNextState()));
            } else {
                int todayLoopRedialCountLimit = newRedialState.getLoopRedialPerdayCountLimitNum();
                if (isSameDay(today, originCustomerItem.getLostCallFirstDay())) {
                    todayLoopRedialCountLimit = newRedialState.getLoopRedialFirstDialDayDialCountLimitNum();
                }

                if (item.getLostCallCurDayCount() < todayLoopRedialCountLimit) {
                    //不要移出候选池，还需要继续拨打
                    addCustomerToPool(item);
                }
            }

            singleNumberModeDAO.updateCustomerShareStateToLostCall(item);

            // 插入共享历史表
            singleNumberModeDAO.insertCustomerShareStateHistory(item);
        }

        //若是当前是预约拨打，更新 预约状态 @ 预约表
        if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(originCustomerItem.getState())) {
            dmDAO.updatePresetState(item.getBizId(), item.getShareBatchId(), item.getCustomerId(), DMPresetStateEnum.FinishPreset.getStateName());
        }

        removeBizIdVsCustomerMap(originCustomerItem.getBizId(), originCustomerItem);
    }

    EndCodeRedialStrategy getEndCodeRedialStrategyByBizId(int bizId) {
        String jsonEndCodeRedialStrategy = dmBizOutboundConfig.dmGetAllBizOutboundSetting(bizId);

        EndCodeRedialStrategyFromDB endCodeRedialStrategyFromDB = new Gson().fromJson(jsonEndCodeRedialStrategy,
                EndCodeRedialStrategyFromDB.class);

        return EndCodeRedialStrategy.getInstance(endCodeRedialStrategyFromDB);
    }

    public Date getNextXDay(int deltaDayNum) {
        Calendar curDay = Calendar.getInstance();
        curDay.setTime(new Date());
        curDay.add(Calendar.DAY_OF_MONTH, deltaDayNum);
        curDay.set(Calendar.HOUR_OF_DAY, 0);
        curDay.set(Calendar.MINUTE, 0);
        curDay.set(Calendar.SECOND, 0);
        curDay.set(Calendar.MILLISECOND, 0);
        return curDay.getTime();
    }

    public Boolean isSameDay(Date date1, Date date2) {
        return date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth() && date1.getDay() == date2.getDay();
    }


    private void addCustomerToPool(SingleNumberModeShareCustomerItem newCustomerItem) {
        Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>> shareBatchIdVsCustomerMap = null;
        PriorityBlockingQueue<SingleNumberModeShareCustomerItem> queue;
        if (SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL.equals(newCustomerItem.getState())) {
            shareBatchIdVsCustomerMap = mapPhaseDialCustomer.get(newCustomerItem.getBizId());
            if (null == shareBatchIdVsCustomerMap) {
                shareBatchIdVsCustomerMap = new HashMap<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>();
                mapPhaseDialCustomer.put(newCustomerItem.getBizId(), shareBatchIdVsCustomerMap);
            }

            queue = shareBatchIdVsCustomerMap.get(newCustomerItem.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, nextDialTimeComparator);
                shareBatchIdVsCustomerMap.put(newCustomerItem.getShareBatchId(), queue);
            }
        } else if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(newCustomerItem.getState())) {
            shareBatchIdVsCustomerMap = mapPresetDialCustomer.get(newCustomerItem.getBizId());
            if (null == shareBatchIdVsCustomerMap) {
                shareBatchIdVsCustomerMap = new HashMap<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>();
                mapPresetDialCustomer.put(newCustomerItem.getBizId(), shareBatchIdVsCustomerMap);
            }


            queue = shareBatchIdVsCustomerMap.get(newCustomerItem.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, nextDialTimeComparator);
                shareBatchIdVsCustomerMap.put(newCustomerItem.getShareBatchId(), queue);
            }
        } else {
            shareBatchIdVsCustomerMap = mapDialCustomer.get(newCustomerItem.getBizId());
            if (null == shareBatchIdVsCustomerMap) {
                shareBatchIdVsCustomerMap = new HashMap<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>();
                mapDialCustomer.put(newCustomerItem.getBizId(), shareBatchIdVsCustomerMap);
            }

            queue = shareBatchIdVsCustomerMap.get(newCustomerItem.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, shareBatchBeginTimeComparator);
                shareBatchIdVsCustomerMap.put(newCustomerItem.getShareBatchId(), queue);
            }
        }

        queue.put(newCustomerItem);

        insertBizIdVsCustomerMap(newCustomerItem.getBizId(), newCustomerItem);
    }

    // 用于过滤 当日重拨已满的客户
    Boolean needJoinCustomerPool(int bizId, SingleNumberModeShareCustomerItem customerItem) {

        if (!SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL.equals(customerItem.getState()))
            return true;

        Date now = new Date();
        if (!isSameDay(customerItem.getLostCallCurDay(), now))
            return true;

        EndCodeRedialStrategy endCodeRedialStrategy = mapBizIdVsEndCodeRedialStrategy.get(bizId);
        if (null == endCodeRedialStrategy) {
            endCodeRedialStrategy = getEndCodeRedialStrategyByBizId(bizId);
            mapBizIdVsEndCodeRedialStrategy.put(bizId, endCodeRedialStrategy);
        }

        RedialState redialState = endCodeRedialStrategy.getNextRedialState(customerItem.getEndCodeType(), customerItem.getEndCode());

        int curDayLostCallRedialCountLimit = 0;
        if (isSameDay(customerItem.getLostCallFirstDay(), now)) {
            curDayLostCallRedialCountLimit = redialState.getLoopRedialFirstDialDayDialCountLimitNum();
        } else {
            curDayLostCallRedialCountLimit = redialState.getLoopRedialPerdayCountLimitNum();
        }

        if (customerItem.getLostCallCurDayCount() < curDayLostCallRedialCountLimit)
            return true;

        return false;
    }

    // 处理追加客户的情形
    private void loadCustomersAppend(int bizId, List<ShareBatchItem> shareBatchItems) {
        System.out.println("bizId : " + bizId);

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.APPENDED);

        List<SingleNumberModeShareCustomerItem> shareDataItems = new ArrayList<SingleNumberModeShareCustomerItem>();

        // TODO 成批从DB取数据
        Boolean result = singleNumberModeDAO.getGivenBizShareDataItemsByState(
                bizId, shareBatchItems, shareCustomerStateList, shareDataItems);

        for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {
            addCustomerToPool(customerItem);
        }
    }

}

