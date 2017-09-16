package hiapp.modules.dm.bo;

import java.util.Date;

public class DMResultItem {
    int id;
    String sourceID; //来源编号是指分配编号或共享编号
    String importBatchID; //客户导入批次ID
    String customerID; //客户ID
    int modifyID; //
    String 	modifyUserID;
    Date modifyTime;
    Boolean	modifylast;
    String 	dialType; // 拨打类型 拨打提交；修改提交
    Date 	dialTime; //
    String 	customerCallId; // 呼叫流水号
}
