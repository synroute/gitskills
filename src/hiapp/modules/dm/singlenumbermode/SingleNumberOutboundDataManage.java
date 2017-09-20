package hiapp.modules.dm.singlenumbermode;

import com.google.gson.Gson;
import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.*;
import hiapp.modules.dm.singlenumbermode.dao.SingleNumberModeDAO;
import hiapp.modules.dm.dao.DMDAO;

import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmsetting.DMBizPresetItem;
import hiapp.modules.dmsetting.DMPresetStateEnum;
import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import hiapp.utils.database.DBConnectionPool;
import hiapp.utils.serviceresult.RecordsetResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    @Qualifier("tenantDBConnectionPool")
    public void setDBConnectionPool(DBConnectionPool dbConnectionPool) {
        this.dbConnectionPool = dbConnectionPool;

        initialize();
    }

    DBConnectionPool dbConnectionPool;
    Map<Integer, EndCodeRedialStrategy> mapBizIdVsEndCodeRedialStrategy;

    Map<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>> mapPresetDialCustomer;
    Map<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>> mapPhaseDialCustomer;
    Map<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>> mapDialCustomer;

    Map<Integer, Map<String,SingleNumberModeShareCustomerItem>>  mapBizIdVsCustomer;

    Timer     dailyTimer;
    TimerTask dailyTimerTask;


    public synchronized SingleNumberModeShareCustomerItem extractNextOutboundCustomer(
                                                            String userId, int bizId) {
        System.out.println(userId + "。。。" + bizId);

        Date now = new Date();

        // 根据userID，获取有权限访问的shareBatchIds

        SingleNumberModeShareCustomerItem shareDataItem = null;

        PriorityBlockingQueue<SingleNumberModeShareCustomerItem> customerQueue = null;

        customerQueue = mapPresetDialCustomer.get(bizId);
        if (null != customerQueue) {
            shareDataItem = customerQueue.peek();
            if (shareDataItem.getNextDialTime().before(now)) {
                shareDataItem = customerQueue.poll();
            }
        }

        if (null == shareDataItem) {
            customerQueue = mapPhaseDialCustomer.get(bizId);
            if (null != customerQueue) {
                shareDataItem = customerQueue.poll();
            }
        }

        if (null == shareDataItem) {
            customerQueue = mapDialCustomer.get(bizId);
            if (null != customerQueue) {
                shareDataItem = customerQueue.poll();
            }
        }

        if (null != shareDataItem)
            singleNumberModeDAO.setAgentOccupied(bizId, shareDataItem.getShareBatchId(), shareDataItem.getCustomerId());

        return shareDataItem;
    }

    public String submitOutboundResult(String userId, int bizId, String importBatchId, String shareBatchId, String customerId,
                                       String resultCodeType, String resultCode, Date presetTime, String resultData, String customerInfo) {

        // 客户原信息变更、拨打信息、结果信息

        Map<String,SingleNumberModeShareCustomerItem> map = mapBizIdVsCustomer.get(bizId);
        SingleNumberModeShareCustomerItem customerItem = map.get(importBatchId + customerId);

        EndCodeRedialStrategy endCodeRedialStrategy = getEndCodeRedialStrategyByBizId(bizId);

        // 经过 Outbound 策略处理器
        procEndcode(userId, customerItem, endCodeRedialStrategy, resultCodeType, resultCode, presetTime, resultData);

        // 插入结果表
        //dmDAO.insertDMResult();
        dataImportJdbc.insertDataToResultTable(bizId, shareBatchId, importBatchId, customerId, userId, resultData);

        // 插入导入客户表
        dataImportJdbc.insertDataToImPortTable(bizId, importBatchId, customerId, userId, customerInfo);

        return "";
    }

    /*
     * 重新初始化处理不适合，举例说明：系统运行中，进行启用共享批次操作，会导致获取外呼客户的功能暂停一段时间。
     * 不需要传入 共享批次信息，通过 启用状态 和 创建日期 获取
     */
    public String startShareBatch(int bizId, List<String> shareBatchIds) {

        // TODO 设置共享批次状态
        singleNumberModeDAO.updateShareBatchState(shareBatchIds, ShareBatchStateEnum.ACTIVE.getName());

        List<ShareBatchItem> shareBatchItems = shareBatchIncrementalProc();

        loadCustomersIncremental(shareBatchItems);
        return "";
    }

    public String stopShareBatch(int bizId, List<String> shareBatchIds) {
        return "";
    }

    public String appendCustomersToShareBatch(int bizId, List<String> shareBatchIds) {
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

        EndCodeRedialStrategy endCodeRedialStrategy = getEndCodeRedialStrategyByBizId(20);


        setDailyRoutine();

        mapPresetDialCustomer = new HashMap<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>();
        mapPhaseDialCustomer  = new HashMap<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>();
        mapDialCustomer = new HashMap<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>();

        mapBizIdVsCustomer = new HashMap<Integer, Map<String,SingleNumberModeShareCustomerItem>>();

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
        dmDAO.expireCurDayShareBatchByEndTime(/*expiredShareBatchIds*/);

        dmDAO.activateCurDayShareBatchByStartTime();


        List< ShareBatchStateEnum > shareBatchStateList = new ArrayList<ShareBatchStateEnum>();
        shareBatchStateList.add(ShareBatchStateEnum.ACTIVE);
        List<ShareBatchItem> shareBatchItems = new ArrayList<ShareBatchItem>();
        Boolean result = dmDAO.getCurDayShareBatchItemsByState(shareBatchStateList, shareBatchItems);
        return shareBatchItems;
    }

    private List<ShareBatchItem> shareBatchIncrementalProc() {
        //List<String> expiredShareBatchIds = new ArrayList<String>();
        dmDAO.expireCurDayShareBatchByEndTime(/*expiredShareBatchIds*/);

        List<ShareBatchItem> shareBatchItems = new ArrayList<ShareBatchItem>();
        dmDAO.getCurDayUsingShareBatchItems(shareBatchItems);

        dmDAO.activateCurDayShareBatchByStartTime();

        return shareBatchItems;
    }

    // 加载数据后会设置已加载标记
    private void loadCustomersDaily(List<ShareBatchItem> shareBatchItems) {

        // TODO 根据未接通拨打日期，决定是否清零<当日未接通重拨次数>
        singleNumberModeDAO.clearPreviousDayLostCallCount();

        // TODO 如果保留<当日未接通重拨次数>，需要判断是否移出候选池（per endcode）
        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList3 = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList3.add(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);
        List<SingleNumberModeShareCustomerItem> shareDataItems2 = new ArrayList<SingleNumberModeShareCustomerItem>();
        singleNumberModeDAO.getXXX(shareBatchItems,shareCustomerStateList3,shareDataItems2);

        // TODO 根据未接通拨打日期, 决定是否移回候选池  <每次重新加载已不需要本操作>

        // TODO 成批从DB取数据
        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.CREATED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.APPENDED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.REVERT);
        List<SingleNumberModeShareCustomerItem> shareDataItems = new ArrayList<SingleNumberModeShareCustomerItem>();
        Boolean result = singleNumberModeDAO.getShareDataItemsByState(shareBatchItems, shareCustomerStateList, shareDataItems);
        for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {
            addCustomerToPool(customerItem);
        }

        // 是否不需要 loaded 标记
        // singleNumberModeDAO.setLoadedFlagShareDataItemsByState(shareBatchItem.getShareBatchId(), shareCustomerStateList);


        // TODO 可以改成批从DB取数据, 根据nextDialTime
        shareCustomerStateList.clear();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);
        List<SingleNumberModeShareCustomerItem> shareDataItems3 = new ArrayList<SingleNumberModeShareCustomerItem>();
        /*Boolean result = */ singleNumberModeDAO.getShareDataItemsByStateAndNextDialTime(shareBatchItems, shareCustomerStateList, shareDataItems3);

        for (SingleNumberModeShareCustomerItem customerItem : shareDataItems2) {
            addCustomerToPool(customerItem);
        }

        // 是否不需要 loaded 标记
        // singleNumberModeDAO.setLoadedFlagShareDataItemsByState(shareBatchItem.getShareBatchId(), shareCustomerStateList);
    }

    private void loadCustomersIncremental(List<ShareBatchItem> shareBatchItems) {

        // TODO 成批从DB取数据
        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.CREATED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.APPENDED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.REVERT);
        List<SingleNumberModeShareCustomerItem> shareDataItems = new ArrayList<SingleNumberModeShareCustomerItem>();
        Boolean result = singleNumberModeDAO.getShareDataItemsByState(shareBatchItems, shareCustomerStateList, shareDataItems);
        for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {
            addCustomerToPool(customerItem);
        }

        // 是否不需要 loaded 标记
        // singleNumberModeDAO.setLoadedFlagShareDataItemsByState(shareBatchItem.getShareBatchId(), shareCustomerStateList);


        // TODO 成批从DB取数据, 根据nextDialTime
        shareCustomerStateList.clear();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);
        List<SingleNumberModeShareCustomerItem> shareDataItems2 = new ArrayList<SingleNumberModeShareCustomerItem>();
        result = singleNumberModeDAO.getShareDataItemsByStateAndNextDialTime(shareBatchItems, shareCustomerStateList, shareDataItems2);

        for (SingleNumberModeShareCustomerItem customerItem : shareDataItems2) {
            addCustomerToPool(customerItem);
        }

        // 是否不需要 loaded 标记
        // singleNumberModeDAO.setLoadedFlagShareDataItemsByState(shareBatchItem.getShareBatchId(), shareCustomerStateList);
    }

    //匿名Comparator实现
    private static Comparator<SingleNumberModeShareCustomerItem> nextDialTimeComparator = new Comparator<SingleNumberModeShareCustomerItem>(){

        @Override
        public int compare(SingleNumberModeShareCustomerItem c1, SingleNumberModeShareCustomerItem c2) {
            return (c1.getNextDialTime().before(c2.getNextDialTime())) ? 1 : -1;
        }
    };

    //匿名Comparator实现
    private static Comparator<SingleNumberModeShareCustomerItem> shareBatchBeginTimeComparator = new Comparator<SingleNumberModeShareCustomerItem>(){

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
        Map<String,SingleNumberModeShareCustomerItem> map = mapBizIdVsCustomer.get(bizId);
        if (null == map) {
            map = new HashMap<String,SingleNumberModeShareCustomerItem>();
            mapBizIdVsCustomer.put(bizId, map);
        }

        map.put(customerItem.getImportBatchId()+customerItem.getCustomerId(), customerItem);
    }

    private void removeBizIdVsCustomerMap(int bizId, SingleNumberModeShareCustomerItem customerItem) {
        Map<String,SingleNumberModeShareCustomerItem> map = mapBizIdVsCustomer.get(bizId);
        if (null == map)
            return;

        map.remove(customerItem.getImportBatchId()+customerItem.getCustomerId(), customerItem);
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
            if ( item.getCurRedialStageCount() >= endCodeRedialStrategy.getStageLimit()) {
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

        removeCustomerFromPool(originCustomerItem);
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

    private void removeCustomerFromPool(SingleNumberModeShareCustomerItem originCustomerItem ) {
        /*PriorityBlockingQueue<SingleNumberModeShareCustomerItem> queue;
        if (SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL.equals(originCustomerItem.getState())) {
            queue = mapPhaseDialCustomer.get(originCustomerItem.getBizId());
        } else if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(originCustomerItem.getState())) {
            queue = mapPresetDialCustomer.get(originCustomerItem.getBizId());
        } else {
            queue = mapDialCustomer.get(originCustomerItem.getBizId());
        }

        queue.remove(originCustomerItem);*/

        removeBizIdVsCustomerMap(originCustomerItem.getBizId(), originCustomerItem);
    }

    private void addCustomerToPool(SingleNumberModeShareCustomerItem newCustomerItem) {
        PriorityBlockingQueue<SingleNumberModeShareCustomerItem> queue;
        if (SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL.equals(newCustomerItem.getState())) {
            queue = mapPhaseDialCustomer.get(newCustomerItem.getBizId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, nextDialTimeComparator);
                mapPhaseDialCustomer.put(newCustomerItem.getBizId(), queue);
            }
        } else if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(newCustomerItem.getState())) {
            queue = mapPresetDialCustomer.get(newCustomerItem.getBizId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, nextDialTimeComparator);
                mapPresetDialCustomer.put(newCustomerItem.getBizId(), queue);
            }
        } else {
            queue = mapDialCustomer.get(newCustomerItem.getBizId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, shareBatchBeginTimeComparator);
                mapDialCustomer.put(newCustomerItem.getBizId(), queue);
            }
        }

        queue.put(newCustomerItem);

        insertBizIdVsCustomerMap(newCustomerItem.getBizId(), newCustomerItem);
    }

}

