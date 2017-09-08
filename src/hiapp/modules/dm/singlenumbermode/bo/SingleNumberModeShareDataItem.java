package hiapp.modules.dm.singlenumbermode.bo;

import java.util.Date;

/*
 *  单号码重拨模式共享数据状态表
 */
public class SingleNumberModeShareDataItem {
    Long id;
    String bizId;
    String shareBatchId;
    String importBatchId;
    String customerId;
    String state;
    Long  modifyId;
    Long  modifyUserId;
    Date  modifyTime;
    String modifyDesc;
    String customerCallId; //客户呼叫流水号
    String endCodeType;
    String endCode;
    Date   lastDailTime; //最近一次拨打时间
    Date   nextDialTime;
    Long   thisDayDialCnt;
}
