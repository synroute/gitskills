package hiapp.modules.dm.singlenumbermode;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import hiapp.modules.dm.singlenumbermode.dao.SingleNumberModeDAO;
import hiapp.modules.dm.dao.DMDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class SingleNumberOutboundDataManage {

    @Autowired
    SingleNumberModeDAO singleNumberModeDAO;

    @Autowired
    DMDAO dmDAO;

    PriorityBlockingQueue PresetDialPriorityQueue;
    PriorityBlockingQueue PhaseDialPriorityQueue;
    PriorityBlockingQueue dialPriorityQueue;

    Timer     dailyTimer;
    TimerTask dailyTimerTask;

    public /*OutboundCustomer*/String extractNextOutboundCustomer(String agentId,
                                                                  String bizId) {
        System.out.println(agentId + "。。。" + bizId);

        SingleNumberModeShareCustomerItem shareDataItem;


        return "";
    }

    public String submitOutboundResult(String requestBody) {
        String resultCodeType; String resultCode; String data;
        return "";
    }

    public String startShareBatch(String shareBatchID) {

        return "";
    }

    public String stopShareBatch(String shareBatchID) {
        return "";
    }

    public String appendCustomersToShareBatch(String shareBatchID) {
        return "";
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initialize() {
        setDailyRoutine();

        PresetDialPriorityQueue = new PriorityBlockingQueue(1, nextDialTimeComparator);
        PhaseDialPriorityQueue = new PriorityBlockingQueue(1, nextDialTimeComparator);
        dialPriorityQueue = new PriorityBlockingQueue(1, shareBatchBeginTimeComparator);

        singleNumberModeDAO.resetLoadedFlag();

        List<ShareBatchItem> shareBatchItems = activateAndRetrieveShareBatchDaily();
        loadCustomersDaily(shareBatchItems);
    }

    private List<ShareBatchItem> activateAndRetrieveShareBatchDaily() {
        dmDAO.activateCurDayShareBatchByStartTime();

        List< ShareBatchStateEnum > shareBatchStateList = new ArrayList<ShareBatchStateEnum>();
        shareBatchStateList.add(ShareBatchStateEnum.ACTIVE);
        List<ShareBatchItem> shareBatchItems = dmDAO.getCurDayShareBatchItemsByState(shareBatchStateList);
        return shareBatchItems;
    }

    // 加载数据后会设置已加载标记
    private void loadCustomersDaily(List<ShareBatchItem> shareBatchItems) {

        // TODO 可以改成批从DB取数据
        for (ShareBatchItem shareBatchItem: shareBatchItems) {
            List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
            shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.CREATED);
            shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.APPENDED);
            shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);
            shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.REVERT);
            List<SingleNumberModeShareCustomerItem> shareDataItems = singleNumberModeDAO.getShareDataItemsByState(shareBatchItem.getShareBatchId(), shareCustomerStateList);
            for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {
                dialPriorityQueue.add(customerItem);
            }

            singleNumberModeDAO.setLoadedFlagShareDataItemsByState(shareBatchItem.getShareBatchId(), shareCustomerStateList);
        }

        // TODO 可以改成批从DB取数据, 根据nextDialTime
        for (ShareBatchItem shareBatchItem: shareBatchItems) {
            List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
            shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL);
            shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);
            List<SingleNumberModeShareCustomerItem> shareDataItems2 = singleNumberModeDAO.getShareDataItemsByStateAndNextDialTime(shareBatchItem.getShareBatchId(), shareCustomerStateList);

            for (SingleNumberModeShareCustomerItem customerItem : shareDataItems2) {
                if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(customerItem.getState()))
                    PresetDialPriorityQueue.add(customerItem);
                else if (SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL.equals(customerItem.getState())) {
                    PhaseDialPriorityQueue.add(customerItem);
                }
            }

            singleNumberModeDAO.setLoadedFlagShareDataItemsByState(shareBatchItem.getShareBatchId(), shareCustomerStateList);
        }
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
            return (c1.getShareBatchBeginTime().before(c2.getShareBatchBeginTime())) ? 1 : -1;
        }
    };

    private void setDailyRoutine() {
        dailyTimer = new Timer();
        dailyTimerTask = new TimerTask() {
            @Override
            public void run() {
                List<ShareBatchItem> shareBatchItems = activateAndRetrieveShareBatchDaily();
                loadCustomersDaily(shareBatchItems);
            }
        };

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 2); // 控制时
        calendar.set(Calendar.MINUTE, 0);      // 控制分
        calendar.set(Calendar.SECOND, 0);      // 控制秒

        dailyTimer.scheduleAtFixedRate(dailyTimerTask, calendar.getTime(), 1000 * 60 * 60 * 24);
    }


}


