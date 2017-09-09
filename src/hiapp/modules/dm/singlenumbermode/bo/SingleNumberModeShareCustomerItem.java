package hiapp.modules.dm.singlenumbermode.bo;

import java.util.Date;

/*
 *  单号码重拨模式共享数据状态表
 */
public class SingleNumberModeShareCustomerItem {
    int id;
    int bizId;
    String shareBatchId;
    String importBatchId;
    int customerId;
    SingleNumberModeShareCustomerStateEnum state;
    int  modifyId;
    int  modifyUserId;
    Date  modifyTime;
    String modifyDesc;
    String customerCallId; //客户呼叫流水号
    String endCodeType;
    String endCode;
    Date   lastDailTime;    //最近一次拨打时间
    Date   nextDialTime;    //下次拨打时间
    int   thisDayDialCnt;   //当天拨打次数

}
