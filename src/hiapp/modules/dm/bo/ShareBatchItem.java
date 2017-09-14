package hiapp.modules.dm.bo;

import java.util.Date;

/*
 *  共享批次信息表HASYS_DM_SID
 */
public class ShareBatchItem {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBizId() {
        return bizId;
    }

    public void setBizId(int bizId) {
        this.bizId = bizId;
    }

    public String getShareBatchId() {
        return shareBatchId;
    }

    public void setShareBatchId(String shareBatchId) {
        this.shareBatchId = shareBatchId;
    }

    public String getShareBatchName() {
        return shareBatchName;
    }

    public void setShareBatchName(String shareBatchName) {
        this.shareBatchName = shareBatchName;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ShareBatchStateEnum getState() {
        return state;
    }

    public void setState(ShareBatchStateEnum state) {
        this.state = state;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }

    public Date getEndTime() {
        return EndTime;
    }

    public void setEndTime(Date endTime) {
        EndTime = endTime;
    }

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
