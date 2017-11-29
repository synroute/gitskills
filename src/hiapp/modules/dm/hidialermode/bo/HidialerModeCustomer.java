package hiapp.modules.dm.hidialermode.bo;

import java.util.Date;

public class HidialerModeCustomer {
    private Integer id; //ID
    private Integer bizId;
    private String shareBatchId;          //分配批次号
    private String importBatchId;        //导入批次号
    private String customerId;          //客户号
    private HidialerModeCustomerStateEnum state;
    private Integer modifyId;
    private String modifyUserId;    //修改人工号
    private Date modifyTime;        //修改时间
    private String modifyDesc;
    private Integer isAppend;
    private String customerCallId;
    private String endCodeType;
    private String endCode;
    private String phoneNumber;
    private Date lastDialTime;
    private Integer callLossCount;
    private Date nextDialTime;

    // 非数据库字段
    Date shareBatchStartTime;  //用于优先级控制
    Boolean invalid = false;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBizId() {
        return bizId;
    }

    public void setBizId(Integer bizId) {
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

    public HidialerModeCustomerStateEnum getState() {
        return state;
    }

    public void setState(HidialerModeCustomerStateEnum state) {
        this.state = state;
    }

    public Integer getModifyId() {
        return modifyId;
    }

    public void setModifyId(Integer modifyId) {
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

    public Integer getIsAppend() {
        return isAppend;
    }

    public void setIsAppend(Integer isAppend) {
        this.isAppend = isAppend;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getLastDialTime() {
        return lastDialTime;
    }

    public void setLastDialTime(Date lastDialTime) {
        this.lastDialTime = lastDialTime;
    }

    public Integer getCallLossCount() {
        return callLossCount;
    }

    public void setCallLossCount(Integer callLossCount) {
        this.callLossCount = callLossCount;
    }

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

    public Date getNextDialTime() {
        return nextDialTime;
    }

    public void setNextDialTime(Date nextDialTime) {
        this.nextDialTime = nextDialTime;
    }

    public HidialerModeCustomer deepClone() {

        HidialerModeCustomer customer = new HidialerModeCustomer();
        customer.setId(id);
        customer.setBizId(bizId);
        customer.setShareBatchId(shareBatchId);
        customer.setImportBatchId(importBatchId);
        customer.setCustomerId(customerId);
        customer.setState(state);
        customer.setModifyId(modifyId);
        customer.setModifyUserId(modifyUserId);
        customer.setModifyTime(modifyTime);
        customer.setModifyDesc(modifyDesc);
        customer.setIsAppend(isAppend);
        customer.setCustomerCallId(customerCallId);
        customer.setEndCodeType(endCodeType);
        customer.setEndCode(endCode);
        customer.setPhoneNumber(phoneNumber);
        customer.setLastDialTime(lastDialTime);
        customer.setCallLossCount(callLossCount);
        customer.setNextDialTime(nextDialTime);
        customer.setShareBatchStartTime(shareBatchStartTime);
        customer.setInvalid(invalid);
        return customer;
    }

}
