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

    public int getCurRedialStageCount() {
        return curRedialStageCount;
    }

    public void setCurRedialStageCount(int curRedialStageCount) {
        this.curRedialStageCount = curRedialStageCount;
    }

    public Date getShareBatchStartTime() {
        return shareBatchStartTime;
    }

    public void setShareBatchStartTime(Date shareBatchStartTime) {
        this.shareBatchStartTime = shareBatchStartTime;
    }

    public int getLostCallCurDayCount() {
        return lostCallCurDayCount;
    }

    public void setLostCallCurDayCount(int lostCallCurDayCount) {
        this.lostCallCurDayCount = lostCallCurDayCount;
    }

    public Date getLossCallFirstDay() {
        return lostCallFirstDay;
    }

    public void setLossCallFirstDay(Date lossCallFirstDay) {
        this.lostCallFirstDay = lossCallFirstDay;
    }

    public int getLostCallTotalCount() {
        return lostCallTotalCount;
    }

    public void setLostCallTotalCount(int lostCallTotalCount) {
        this.lostCallTotalCount = lostCallTotalCount;
    }

    public Date getLostCallCurDay() {
        return lostCallCurDay;
    }

    public void setLostCallCurDay(Date lostCallCurDay) {
        this.lostCallCurDay = lostCallCurDay;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(String modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public Boolean getUserUseState() {
        return userUseState;
    }

    public void setUserUseState(Boolean userUseState) {
        this.userUseState = userUseState;
    }

    public Date getLostCallFirstDay() {
        return lostCallFirstDay;
    }

    public void setLostCallFirstDay(Date lostCallFirstDay) {
        this.lostCallFirstDay = lostCallFirstDay;
    }

    public Boolean getIsLoaded() {
        return isLoaded;
    }

    public void setIsLoaded(Boolean loaded) {
        isLoaded = loaded;
    }


    int id;
    int bizId;
    String shareBatchId;
    String importBatchId;
    String customerId;
    SingleNumberModeShareCustomerStateEnum state;
    int  modifyId;
    String  modifyUserId;
    Date  modifyTime;
    String modifyDesc;
    String customerCallId; //客户呼叫流水号
    String endCodeType;
    String endCode;
    Date   lastDailTime;    //最近一次拨打时间
    Date   nextDialTime;    //下次拨打时间
    int lostCallCurDayCount;   //当天未接通次数
    int   curRedialStageCount; //仅用于阶段拨打
    Boolean userUseState; //是否已经被坐席人员抽取
    Boolean isLoaded;   //是否已经加载到内存
    Date  lostCallFirstDay; //第一次未接通日期
    Date  lostCallCurDay; //当前未接通日期
    int   lostCallTotalCount; //未接通总次数

    // 非本表字段
    Date shareBatchStartTime;  //用于优先级控制
}
