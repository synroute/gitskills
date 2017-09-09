package hiapp.modules.dm.bo;

import java.util.Date;

/*
 *  共享批次信息表HASYS_DM_SID
 */
public class ShareBatchItem {
    int id;
    int bizId;
    String shareBatchId;
    String shareBatchName;
    int  createUserId;
    Date createTime;
    String description;
    ShareBatchStateEnum	state;
    Date StartTime;
    Date EndTime;
}
