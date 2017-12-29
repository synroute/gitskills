package hiapp.modules.dm;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.hidialermode.HidialerOutboundDataManage;
import hiapp.modules.dm.manualmode.ManualOutboundDataManage;
import hiapp.modules.dm.multinumbermode.MultiNumberModeController;
import hiapp.modules.dm.multinumbermode.MultiNumberOutboundDataManage;
import hiapp.modules.dm.multinumberredialmode.MultiNumberRedialDataManage;
import hiapp.modules.dm.singlenumbermode.SingleNumberModeController;
import hiapp.modules.dm.singlenumbermode.SingleNumberOutboundDataManage;
import hiapp.modules.dmsetting.DMBizOutboundModelEnum;
import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.data.DmBizRepository;
import hiapp.utils.database.DBConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.*;

@Service
public class DMService {

    @Autowired
    SingleNumberOutboundDataManage singleNumberOutboundDataManage;

    @Autowired
    MultiNumberOutboundDataManage multiNumberOutboundDataManage;

    @Autowired
    ManualOutboundDataManage manualOutboundDataManage;

    @Autowired
    MultiNumberRedialDataManage multiNumberRedialDataManage;

    @Autowired
    HidialerOutboundDataManage hidialerOutboundDataManage;

    @Autowired
    DMDAO dmDAO;


    @Autowired
    private DmBizRepository dmBizRepository;


    @Autowired
    @Qualifier("tenantDBConnectionPool")
    public void setDBConnectionPool(DBConnectionPool dbConnectionPool) {
        //this.dbConnectionPool = dbConnectionPool;

        singleNumberOutboundDataManage.initialize();
        multiNumberOutboundDataManage.initialize();
        manualOutboundDataManage.initialize();
        hidialerOutboundDataManage.initialize();
        multiNumberRedialDataManage.initialize();

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
            return hidialerOutboundDataManage.appendCustomersToShareBatch(bizId, shareBatchIds);

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            return singleNumberOutboundDataManage.appendCustomersToShareBatch(bizId, shareBatchIds);

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            return multiNumberRedialDataManage.appendCustomersToShareBatch(bizId, shareBatchIds);
        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            return multiNumberOutboundDataManage.appendCustomersToShareBatch(bizId, shareBatchIds);
        }

        return false;
    }

    /**
     * 后台直接调用
     */
    public void cancelOutboundTask(int bizId, List<CustomerBasic> customerBasicList) {
        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(bizId);

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {
            manualOutboundDataManage.cancelOutboundTask(bizId, customerBasicList);
        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            hidialerOutboundDataManage.cancelOutboundTask(bizId, customerBasicList);
        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            singleNumberOutboundDataManage.cancelOutboundTask(bizId, customerBasicList);
        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            multiNumberRedialDataManage.cancelOutboundTask(bizId, customerBasicList);
        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            multiNumberOutboundDataManage.cancelOutboundTask(bizId, customerBasicList);
        }
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

            if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == modeId) {
                hidialerOutboundDataManage.dailyProc(oneModeShareBatchItems);
            } else if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == modeId) {
                manualOutboundDataManage.dailyProc(oneModeShareBatchItems);
            } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == modeId) {
                singleNumberOutboundDataManage.dailyProc(oneModeShareBatchItems);
            } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == modeId) {
                multiNumberRedialDataManage.dailyProc(oneModeShareBatchItems);
            } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == modeId) {
                multiNumberOutboundDataManage.dailyProc(oneModeShareBatchItems);
            } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == modeId) {
                multiNumberRedialDataManage.dailyProc(oneModeShareBatchItems);
            }
        }
    }

    private void setDailyRoutine() {

        final TimerTask dailyTimerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Daily TimerTask...");
                try {
                    dailyProc();
                } catch (Throwable exception) {
                    System.out.println("Daily TimerTask XXX Exception ");
                    exception.printStackTrace();
                }
            }
        };

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 2); // 控制时
        calendar.set(Calendar.MINUTE, 0);      // 控制分
        calendar.set(Calendar.SECOND, 0);      // 控制秒

        new Timer().scheduleAtFixedRate(dailyTimerTask, calendar.getTime(), 1000 * 60 * 60 * 24);

        //ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        //pool.scheduleAtFixedRate(dailyTimerTask, timeSlotSpan , timeSlotSpan, TimeUnit.MILLISECONDS);
    }

    private void setTimeOutRoutine(Long timeSlotSpan) {
        final TimerTask m1TimerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("M1 TimerTask...");
                try {
                    manualOutboundDataManage.timeoutProc();
                } catch (Throwable exception) {
                    System.out.println("M1 TimerTask XXX Exception ");
                    exception.printStackTrace();
                }
            }
        };

        final TimerTask m2TimerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("M2 TimerTask...");
                try {
                    hidialerOutboundDataManage.timeoutProc();
                } catch (Throwable exception) {
                    System.out.println("M2 TimerTask XXX Exception ");
                    exception.printStackTrace();
                }
            }
        };

        final TimerTask m3TimerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("M3 TimerTask...");
                try {
                    singleNumberOutboundDataManage.timeoutProc();
                } catch (Throwable exception) {
                    System.out.println("M3 TimerTask XXX Exception ");
                    exception.printStackTrace();
                }
            }
        };

        final TimerTask m4TimerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("M4 TimerTask...");
                try {
                    multiNumberRedialDataManage.timeoutProc();
                } catch (Throwable exception) {
                    System.out.println("M4 TimerTask XXX Exception ");
                    exception.printStackTrace();
                }
            }
        };

        final TimerTask m6TimerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("M6 TimerTask...");
                try {
                    multiNumberOutboundDataManage.timeoutProc();
                } catch (Throwable exception) {
                    System.out.println("M6 TimerTask XXX Exception ");
                    exception.printStackTrace();
                }
            }
        };

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleAtFixedRate(m1TimerTask, timeSlotSpan , timeSlotSpan, TimeUnit.MILLISECONDS);
        pool.scheduleAtFixedRate(m2TimerTask, timeSlotSpan , timeSlotSpan, TimeUnit.MILLISECONDS);
        pool.scheduleAtFixedRate(m3TimerTask, timeSlotSpan , timeSlotSpan, TimeUnit.MILLISECONDS);
        pool.scheduleAtFixedRate(m4TimerTask, timeSlotSpan , timeSlotSpan, TimeUnit.MILLISECONDS);
        pool.scheduleAtFixedRate(m6TimerTask, timeSlotSpan , timeSlotSpan, TimeUnit.MILLISECONDS);
    }

}
