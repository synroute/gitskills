package hiapp.modules.dm.multinumbermode.bo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MultiNumberCustomer {

    public Date getShareBatchStartTime() {
        return shareBatchStartTime;
    }

    public void setShareBatchStartTime(Date shareBatchStartTime) {
        this.shareBatchStartTime = shareBatchStartTime;
    }

    public Boolean getInvalid() {
        return invalid;
    }

    public void setInvalid(Boolean invalid) {
        this.invalid = invalid;
    }

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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public MultiNumberPredictStateEnum getState() {
        return state;
    }

    public void setState(MultiNumberPredictStateEnum state) {
        this.state = state;
    }

    public int getModifyId() {
        return modifyId;
    }

    public void setModifyId(int modifyId) {
        this.modifyId = modifyId;
    }

    public String getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(String modifyUserId) {
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

    public String getCurDialPhone() {
        return curDialPhone;
    }

    public void setCurDialPhone(String curDialPhone) {
        this.curDialPhone = curDialPhone;
    }

    public Date getCurPresetDialTime() {
        return curPresetDialTime;
    }

    public void setCurPresetDialTime(Date curPresetDialTime) {
        this.curPresetDialTime = curPresetDialTime;
    }

    public Integer getNextDialPhoneType() {
        return nextDialPhoneType;
    }

    public void setNextDialPhoneType(Integer nextDialPhoneType) {
        this.nextDialPhoneType = nextDialPhoneType;
    }

    public Map<Integer, PhoneDialInfo> getDialInfo() {
        return mapPhoneTypeVsPhoneInfo;
    }

    public void setDialInfo(Map<Integer, PhoneDialInfo> dialInfo) {
        this.mapPhoneTypeVsPhoneInfo = dialInfo;
    }

    public PhoneDialInfo getDialInfo(Integer phoneType) {
        return mapPhoneTypeVsPhoneInfo.get(phoneType);
    }

    public void setDialInfo(int phoneType, PhoneDialInfo dialInfo) {
        mapPhoneTypeVsPhoneInfo.put(phoneType, dialInfo);
    }

    public int getCurDialPhoneType() {
        return curDialPhoneType;
    }

    public void setCurDialPhoneType(int curDialPhoneType) {
        this.curDialPhoneType = curDialPhoneType;
    }

    public int getIsAppend() {
        return isAppend;
    }

    public void setIsAppend(int isAppend) {
        this.isAppend = isAppend;
    }


    int id;
    int bizId;
    String shareBatchId;
    String importBatchId;
    String customerId;
    MultiNumberPredictStateEnum state;
    int modifyId;
    String modifyUserId;
    Date  modifyTime;
    String modifyDesc;
    String customerCallId;
    String endCodeType;
    String endCode;

    String curDialPhone;
    Date   curPresetDialTime;          // NOTE: 预约拨打时间 或者 重拨时间
    Integer    curDialPhoneType;
    Integer    nextDialPhoneType;
    int    isAppend;
    Map<Integer, PhoneDialInfo> mapPhoneTypeVsPhoneInfo = new HashMap<Integer, PhoneDialInfo>();

    // 非本表字段
    Date shareBatchStartTime;  //用于优先级控制
    Boolean invalid = false;
}

