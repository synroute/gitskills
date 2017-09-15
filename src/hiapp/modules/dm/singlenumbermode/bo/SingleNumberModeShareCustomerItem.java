package hiapp.modules.dm.singlenumbermode.bo;

import java.util.Date;

/*
 *  单号码重拨模式共享数据状态表
 */
public class SingleNumberModeShareCustomerItem {
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

    public String getImportBatchId() {
        return importBatchId;
    }

    public void setImportBatchId(String importBatchId) {
        this.importBatchId = importBatchId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public SingleNumberModeShareCustomerStateEnum getState() {
        return state;
    }

    public void setState(SingleNumberModeShareCustomerStateEnum state) {
        this.state = state;
    }

    public int getModifyId() {
        return modifyId;
    }

    public void setModifyId(int modifyId) {
        this.modifyId = modifyId;
    }

    public int getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(int modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyDesc() {
        return modifyDesc;
    }

    public void setModifyDesc(String modifyDesc) {
        this.modifyDesc = modifyDesc;
    }

    public String getCustomerCallId() {
        return customerCallId;
    }

    public void setCustomerCallId(String customerCallId) {
        this.customerCallId = customerCallId;
    }

    public String getEndCodeType() {
        return endCodeType;
    }

    public void setEndCodeType(String endCodeType) {
        this.endCodeType = endCodeType;
    }

    public String getEndCode() {
        return endCode;
    }

    public void setEndCode(String endCode) {
        this.endCode = endCode;
    }

    public Date getLastDailTime() {
        return lastDailTime;
    }

    public void setLastDailTime(Date lastDailTime) {
        this.lastDailTime = lastDailTime;
    }

    public Date getNextDialTime() {
        return nextDialTime;
    }

    public void setNextDialTime(Date nextDialTime) {
        this.nextDialTime = nextDialTime;
    }

    public int getThisDayDialCount() {
        return thisDayDialCount;
    }

    public void setThisDayDialCount(int thisDayDialCount) {
        this.thisDayDialCount = thisDayDialCount;
    }

    public int getCurRedialStageCount() {
        return curRedialStageCount;
    }

    public void setCurRedialStageCount(int curRedialStageCount) {
        this.curRedialStageCount = curRedialStageCount;
    }

    public Boolean getAgentOccupied() {
        return isAgentOccupied;
    }

    public void setAgentOccupied(Boolean agentOccupied) {
        isAgentOccupied = agentOccupied;
    }

    public Boolean getLoaded() {
        return isLoaded;
    }

    public void setLoaded(Boolean loaded) {
        isLoaded = loaded;
    }

    public Date getShareBatchBeginTime() {
        return shareBatchBeginTime;
    }

    public void setShareBatchBeginTime(Date shareBatchBeginTime) {
        this.shareBatchBeginTime = shareBatchBeginTime;
    }

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
    int   thisDayDialCount;   //当天拨打次数
    int   curRedialStageCount; //仅用于阶段拨打
    Boolean isAgentOccupied; //是否已经被坐席人员抽取
    Boolean isLoaded;   //是否已经加载到内存

    // 非本表字段
    Date shareBatchBeginTime;
}
