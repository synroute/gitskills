package hiapp.modules.dmmanager;

import hiapp.modules.dm.bo.ShareBatchStateEnum;

import java.util.Date;

public class ShareBatchItemS {
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

	    public String getCreateUserId() {
	        return createUserId;
	    }

	    public void setCreateUserId(String createUserId) {
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
	    String  createUserId;
	    Date createTime;
	    String description;
	    public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		String	state;
	    Date StartTime;
	    Date EndTime;
	    int abc;
		public int getAbc() {
			return abc;
		}

		public void setAbc(int abc) {
			this.abc = abc;
		}
}
