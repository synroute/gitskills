package hiapp.modules.dm.multinumbermode.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MultiNumberCustomer implements Serializable{

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

    public Map<Integer, PhoneDialInfo> getDialInfoByPhoneType() {
        return mapPhoneTypeVsPhoneInfo;
    }

    public void setDialInfo(Map<Integer, PhoneDialInfo> dialInfo) {
        this.mapPhoneTypeVsPhoneInfo = dialInfo;
    }

    public PhoneDialInfo getDialInfoByPhoneType(Integer phoneType) {
        return mapPhoneTypeVsPhoneInfo.get(phoneType);
    }

    public void setDialInfo(int phoneType, PhoneDialInfo dialInfo) {
        mapPhoneTypeVsPhoneInfo.put(phoneType, dialInfo);
    }

    public Integer getCurDialPhoneType() {
        return curDialPhoneType;
    }

    public void setCurDialPhoneType(Integer curDialPhoneType) {
        this.curDialPhoneType = curDialPhoneType;
    }

    public Integer getCallLossCount() {
        return callLossCount;
    }

    public void setCallLossCount(Integer callLossCount) {
        this.callLossCount = callLossCount;
    }

    public int getIsAppend() {
        return isAppend;
    }

    public void setIsAppend(int isAppend) {
        this.isAppend = isAppend;
    }

    public String getShareToken() { return bizId + shareBatchId; }

    public String getCustomerToken() { return bizId + importBatchId + customerId; }

    public MultiNumberCustomer deepClone() {
        MultiNumberCustomer cloneItem = new MultiNumberCustomer();

        cloneItem.setId(id);
        cloneItem.setBizId(bizId);
        cloneItem.setShareBatchId(shareBatchId);
        cloneItem.setImportBatchId(importBatchId);
        cloneItem.setCustomerId(customerId);
        cloneItem.setState(state);
        cloneItem.setModifyId(modifyId);
        cloneItem.setModifyUserId(modifyUserId);
        cloneItem.setModifyTime(modifyTime);
        cloneItem.setModifyDesc(modifyDesc);
        cloneItem.setCustomerCallId(customerCallId);
        cloneItem.setEndCodeType(endCodeType);
        cloneItem.setEndCode(endCode);
        cloneItem.setCurDialPhone(curDialPhone);
        cloneItem.setCurPresetDialTime(curPresetDialTime);
        cloneItem.setCurDialPhoneType(curDialPhoneType);
        cloneItem.setNextDialPhoneType(nextDialPhoneType);
        cloneItem.setCallLossCount(callLossCount);
        cloneItem.setIsAppend(isAppend);

        //
        for (Map.Entry<Integer, PhoneDialInfo> entry : mapPhoneTypeVsPhoneInfo.entrySet()) {
            Integer phoneType =  entry.getKey();
            PhoneDialInfo phoneDialInfo = entry.getValue();
            cloneItem.setDialInfo(phoneType, phoneDialInfo.clone());
        }

        cloneItem.setShareBatchStartTime(shareBatchStartTime);
        cloneItem.setInvalid(invalid);
        return cloneItem;
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
    Integer    callLossCount;
    int    isAppend;

    Map<Integer, PhoneDialInfo> mapPhoneTypeVsPhoneInfo = new HashMap<Integer, PhoneDialInfo>();

    // 非本表字段
    Date shareBatchStartTime;  //用于优先级控制
    Boolean invalid = false;
}

