package hiapp.modules.dm.bo;

import java.util.Date;

/*
 *  共享批次信息表HASYS_DM_SID
 */
public class ShareBatchItem {
    Long id;
    Long bizId;
    String shareBatchId;
    String shareBatchName;
    Long createUserId;
    Date createTime;
    String description;
    String	state;       //	ShareBatchStateEnum
    Date StartTime;
    Date EndTime;
}
