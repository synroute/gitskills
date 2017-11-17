package hiapp.modules.dm;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.ManualOutboundDataManage;
import hiapp.modules.dm.multinumbermode.MultiNumberModeController;
import hiapp.modules.dm.multinumbermode.MultiNumberOutboundDataManage;
import hiapp.modules.dm.singlenumbermode.SingleNumberModeController;
import hiapp.modules.dm.singlenumbermode.SingleNumberOutboundDataManage;
import hiapp.modules.dmsetting.DMBizOutboundModelEnum;
import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.data.DmBizRepository;
import hiapp.utils.database.DBConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DMService {

    @Autowired
    SingleNumberOutboundDataManage singleNumberOutboundDataManage;

    @Autowired
    MultiNumberOutboundDataManage multiNumberOutboundDataManage;

    @Autowired
    ManualOutboundDataManage manualOutboundDataManage;

    @Autowired
    DMDAO dmDAO;


    @Autowired
    private DmBizRepository dmBizRepository;


    Timer dailyTimer;
    TimerTask dailyTimerTask;

    Timer timeoutTimer;
    TimerTask timeoutTimerTask;


    @Autowired
    @Qualifier("tenantDBConnectionPool")
    public void setDBConnectionPool(DBConnectionPool dbConnectionPool) {
        //this.dbConnectionPool = dbConnectionPool;

        singleNumberOutboundDataManage.initialize();
        multiNumberOutboundDataManage.initialize();
        manualOutboundDataManage.initialize();

        setDailyRoutine();
        setTimeOutRoutine(Constants.timeSlotSpan);

        dailyProc();
    }

    /**
     * 后台直接调用
     * @param bizId
     * @param shareBatchIds
     * @return
     */
    //@RequestMapping(value="/srv/dm/appendCustomersToShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    //public String appendCustomersToShareBatch(@RequestParam("bizId") int bizId, @RequestParam("shareBatchIDs") String strShareBatchIds) {
    public Boolean appendCustomersToShareBatch(int bizId, List<String> shareBatchIds) {

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(bizId);

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {
            return manualOutboundDataManage.appendCustomersToShareBatch(bizId, shareBatchIds);
        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            return singleNumberOutboundDataManage.appendCustomersToShareBatch(bizId, shareBatchIds);

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            return multiNumberOutboundDataManage.appendCustomersToShareBatch(bizId, shareBatchIds);
        }

        return false;
    }

    private Boolean shareBatchDailyProc(/*OUT*/List<ShareBatchItem> shareBatchItems) {
        //List<String> expiredShareBatchIds = new ArrayList<String>();
        dmDAO.expireShareBatchsByEndTime(/*expiredShareBatchIds*/);

        dmDAO.activateShareBatchByStartTime();

        Boolean result = dmDAO.getAllActiveShareBatchItems(shareBatchItems);
        return  result;
    }

    private void dailyProc() {
        List<ShareBatchItem> shareBatchItems = new ArrayList<ShareBatchItem>();
        Boolean ret = shareBatchDailyProc(shareBatchItems);

        Map<Integer, List<ShareBatchItem>> mapOutboundModeIdVsShareBatchs = new HashMap<Integer, List<ShareBatchItem>>();
        List<ShareBatchItem> oneModeShareBatchItems;

        // 根据Outbound Mode, 分类共享批次
        for (ShareBatchItem shareBatchItem : shareBatchItems) {
            int outboundModeId = shareBatchItem.getOutboundModeId();
            oneModeShareBatchItems = mapOutboundModeIdVsShareBatchs.get(outboundModeId);
            if (null == oneModeShareBatchItems) {
                oneModeShareBatchItems = new ArrayList<ShareBatchItem>();
                mapOutboundModeIdVsShareBatchs.put(outboundModeId, oneModeShareBatchItems);
            }
            oneModeShareBatchItems.add(shareBatchItem);
        }

        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapOutboundModeIdVsShareBatchs.entrySet()) {
            Integer modeId = entry.getKey();
            oneModeShareBatchItems = entry.getValue();

            if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == modeId) {
                manualOutboundDataManage.dailyProc(oneModeShareBatchItems);
            } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == modeId) {
                singleNumberOutboundDataManage.dailyProc(oneModeShareBatchItems);
            } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == modeId) {
                multiNumberOutboundDataManage.dailyProc(oneModeShareBatchItems);
            }

        }
    }

    private void setDailyRoutine() {
        dailyTimer = new Timer();
        dailyTimerTask = new TimerTask() {
            @Override
            public void run() {
                dailyProc();
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

    private void setTimeOutRoutine(Long timeSlotSpan) {
        timeoutTimer = new Timer();
        timeoutTimerTask = new TimerTask() {
            @Override
            public void run() {
                singleNumberOutboundDataManage.timeoutProc();
                multiNumberOutboundDataManage.timeoutProc();
                manualOutboundDataManage.timeoutProc();
            }
        };

        dailyTimer.scheduleAtFixedRate(timeoutTimerTask, timeSlotSpan, timeSlotSpan);
    }

}
