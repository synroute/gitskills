package hiapp.modules.dm.singlenumbermode.bo;

import java.io.Serializable;
import java.util.Date;

/*
 *  单号码重拨模式共享数据状态表
 */
public class SingleNumberModeShareCustomerItem implements Serializable{
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

    public Date getLastDialTime() {
        return lastDialTime;
    }

    public void setLastDialTime(Date lastDialTime) {
        this.lastDialTime = lastDialTime;
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

    public String getUserUseState() {
        return userUseState;
    }

    public void setUserUseState(String userUseState) {
        this.userUseState = userUseState;
    }

    public Date getLostCallFirstDay() {
        return lostCallFirstDay;
    }

    public void setLostCallFirstDay(Date lostCallFirstDay) {
        this.lostCallFirstDay = lostCallFirstDay;
    }

    public Integer getIsLoaded() {
        return isLoaded;
    }

    public void setIsLoaded(Integer loaded) {
        isLoaded = loaded;
    }

    public void setInvalid(Boolean flag) { invalid = flag; }

    public Boolean getInvalid() { return null == invalid ? false : invalid; }

    public Date getExtractTime() {
        return extractTime;
    }

    public void setExtractTime(Date extractTime) {
        this.extractTime = extractTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShareToken() { return bizId + shareBatchId; }

    public String getCustomerToken() { return bizId + importBatchId + customerId; }


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
    Date   lastDialTime;    //最近一次拨打时间
    Date   nextDialTime;    //下次拨打时间
    int lostCallCurDayCount;   //当天未接通次数
    int   curRedialStageCount; //仅用于阶段拨打
    String userUseState;       //是否已经被坐席人员抽取  使用中 使用完毕
    int isLoaded;   //是否已经加载到内存
    Date  lostCallFirstDay; //第一次未接通日期
    Date  lostCallCurDay; //当前未接通日期
    int   lostCallTotalCount; //未接通总次数

    // 非本表字段
    Date shareBatchStartTime;  //用于优先级控制
    String userId;    // 抽取该客户的坐席人员
    Date extractTime; // 用于记录抽取时间
    Boolean invalid = false;
}
