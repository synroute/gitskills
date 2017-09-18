package hiapp.modules.dm.singlenumbermode;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.*;
import hiapp.modules.dm.singlenumbermode.dao.SingleNumberModeDAO;
import hiapp.modules.dm.dao.DMDAO;

import hiapp.modules.dmsetting.DMBizPresetItem;
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
    EndCodeRedialStrategy endCodeRedialStrategy;

    DBConnectionPool dbConnectionPool;

    @Autowired
    @Qualifier("tenantDBConnectionPool")
    public void setDBConnectionPool(DBConnectionPool dbConnectionPool) {
        this.dbConnectionPool = dbConnectionPool;

        //initialize();
    }

    Map<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>> mapPresetDialCustomer;
    Map<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>> mapPhaseDialCustomer;
    Map<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>> mapDialCustomer;

    Timer     dailyTimer;
    TimerTask dailyTimerTask;


    public SingleNumberModeShareCustomerItem extractNextOutboundCustomer(String userId,
                                                                  int bizId) {
        System.out.println(userId + "。。。" + bizId);

        // 根据userID，获取有权限访问的shareBatchIds

        SingleNumberModeShareCustomerItem shareDataItem = null;

        PriorityBlockingQueue<SingleNumberModeShareCustomerItem> customerQueue = null;

        customerQueue = mapPresetDialCustomer.get(bizId);
        if (null != customerQueue) {
            shareDataItem = customerQueue.poll();
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

    public String submitOutboundResult(int bizId, String importBatchId, String shareBatchId, String customerId,
                                       String resultCodeType, String resultCode, Date presetTime /*, customerInfo*/) {

        String data;  // 客户原信息变更、拨打信息、结果信息

        SingleNumberModeShareCustomerStateEnum originalShareState = SingleNumberModeShareCustomerStateEnum.CREATED;

        // 经过 Outbound 策略处理器
        procEndcode(bizId, shareBatchId, customerId, originalShareState, resultCodeType, resultCode);

        // 更新导入表
        reviseCustomerInfo();

        return "";
    }

    /*
     * 重新初始化处理不适合，举例说明：系统运行中，进行启用共享批次操作，会导致获取外呼客户的功能暂停一段时间。
     * 不需要传入 共享批次信息，通过 启用状态 和 创建日期 获取
     */
    public String startShareBatch(int bizId, List<String> shareBatchIds) {

        // TODO 设置共享批次状态


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

        setDailyRoutine();

        mapPresetDialCustomer = new HashMap<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>();
        mapPhaseDialCustomer  = new HashMap<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>();
        mapDialCustomer = new HashMap<Integer, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>();

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
            int bizId = customerItem.getBizId();
            PriorityBlockingQueue<SingleNumberModeShareCustomerItem> dialPriorityQueue = mapDialCustomer.get(bizId);
            if (null == dialPriorityQueue) {
                dialPriorityQueue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, shareBatchBeginTimeComparator);
                mapDialCustomer.put(bizId, dialPriorityQueue);
            }

            dialPriorityQueue.put(customerItem);
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
            if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(customerItem.getState())) {
                int bizId = customerItem.getBizId();
                PriorityBlockingQueue<SingleNumberModeShareCustomerItem> presetDialPriorityQueue = mapPresetDialCustomer.get(bizId);
                if (null == presetDialPriorityQueue) {
                    presetDialPriorityQueue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, nextDialTimeComparator);
                    mapPresetDialCustomer.put(bizId, presetDialPriorityQueue);
                }
                presetDialPriorityQueue.put(customerItem);
            }
            else if (SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL.equals(customerItem.getState())) {
                int bizId = customerItem.getBizId();
                PriorityBlockingQueue<SingleNumberModeShareCustomerItem> phaseDialPriorityQueue = mapPhaseDialCustomer.get(bizId);
                if (null == phaseDialPriorityQueue) {
                    phaseDialPriorityQueue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, nextDialTimeComparator);
                    mapPhaseDialCustomer.put(bizId, phaseDialPriorityQueue);
                }
                phaseDialPriorityQueue.put(customerItem);
            }
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
            int bizId = customerItem.getBizId();
            PriorityBlockingQueue<SingleNumberModeShareCustomerItem> dialPriorityQueue = mapDialCustomer.get(bizId);
            if (null == dialPriorityQueue) {
                dialPriorityQueue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, shareBatchBeginTimeComparator);
                mapDialCustomer.put(bizId, dialPriorityQueue);
            }

            dialPriorityQueue.put(customerItem);
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
            if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(customerItem.getState())) {
                int bizId = customerItem.getBizId();
                PriorityBlockingQueue<SingleNumberModeShareCustomerItem> presetDialPriorityQueue = mapPresetDialCustomer.get(bizId);
                if (null == presetDialPriorityQueue) {
                    presetDialPriorityQueue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, nextDialTimeComparator);
                    mapPresetDialCustomer.put(bizId, presetDialPriorityQueue);
                }
                presetDialPriorityQueue.put(customerItem);
            }
            else if (SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL.equals(customerItem.getState())) {
                int bizId = customerItem.getBizId();
                PriorityBlockingQueue<SingleNumberModeShareCustomerItem> phaseDialPriorityQueue = mapPhaseDialCustomer.get(bizId);
                if (null == phaseDialPriorityQueue) {
                    phaseDialPriorityQueue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, nextDialTimeComparator);
                    mapPhaseDialCustomer.put(bizId, phaseDialPriorityQueue);
                }
                phaseDialPriorityQueue.put(customerItem);
            }
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

    private void reviseCustomerInfo() {
        // TODO
    }

    private void procEndcode(int bizId, String shareBatchId, String customerId,
                             SingleNumberModeShareCustomerStateEnum originalShareState,
                             String resultCodeType, String resultCode) {
        RedialState redialState = endCodeRedialStrategy.getNextRedialState(resultCodeType, resultCode);

        SingleNumberModeShareCustomerItem item = new SingleNumberModeShareCustomerItem();
        item.setBizId(bizId);
        item.setShareBatchId(shareBatchId);
        item.setCustomerId(customerId);
        item.setEndCodeType(resultCodeType);
        item.setEndCode(resultCode);

        RedialStateTypeEnum redialStateType = redialState.getRedialStateType();
        if (RedialStateTypeEnum.REDIAL_STATE_FINISHED.equals(redialStateType)) {
            // 更新共享状态表
            singleNumberModeDAO.updateCustomerShareStateToFinish(item);

            singleNumberModeDAO.insertCustomerShareStateHistory(item);

            // 更新结果表
            dmDAO.insertDMResult();

        } else if (RedialStateTypeEnum.REDIAL_STATE_PRESET.equals(redialStateType)) {
            // 更新共享状态表   nextDialTime
            singleNumberModeDAO.updateCustomerShareStateToPreset(item);

            singleNumberModeDAO.insertCustomerShareStateHistory(item);

            // 更新结果表
            dmDAO.insertDMResult();

            // 更新预约表
            dmDAO.insertPresetItem(null);

        } else if (RedialStateTypeEnum.REDIAL_STATE_PHASE.equals(redialStateType)) {
            // 更新共享状态表  nextDialTime  curRedialStageCount
            // 到达最后阶段，直接跳转状态
            singleNumberModeDAO.updateCustomerShareStateToStage(item);

            singleNumberModeDAO.insertCustomerShareStateHistory(item);

            // 更新结果表
            dmDAO.insertDMResult();

        } else if (RedialStateTypeEnum.REDIAL_STATE_LOSTCALL.equals(redialStateType)) {
            // 更新共享状态表  lostcallFirstDay  curDayLostCallCount  lostcallTotalCount
            // 总未接通数到达限定值，直接跳转状态
            // 每天未接通数到达限定值，移出候选池。每天处理时重新移回候选池。
            singleNumberModeDAO.updateCustomerShareStateToLostCall(item);

            singleNumberModeDAO.insertCustomerShareStateHistory(item);

            // 更新结果表
            dmDAO.insertDMResult();
        }

        //若是预约拨打，更新 预约状态 @ 预约表
        if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(originalShareState)) {
            dmDAO.updatePresetState(bizId, shareBatchId, customerId, "???");
        }

    }

}

