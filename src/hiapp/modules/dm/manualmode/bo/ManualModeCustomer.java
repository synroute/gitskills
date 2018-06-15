package hiapp.modules.dm.manualmode.bo;

import hiapp.modules.dmmanager.AreaTypeEnum;
import hiapp.modules.dmmanager.OperationNameEnum;

import java.io.Serializable;
import java.util.Date;

public class ManualModeCustomer implements Serializable{
    private Integer id; //ID
    private String sourceId;          //分配批次号
    private String importBatchId;        //导入批次号
    private String customerId;          //客户号
    private Integer dataPoolIdLast; //上次所在数据池数据池
    private Integer dataPoolIdCur;  //当前所在数据池
    private AreaTypeEnum areaTypeLast;  //上次所在数据池分区
    private AreaTypeEnum areaTypeCur;    //当前所在数据池分区
    private Integer isRecover;      //是否被回收
    private OperationNameEnum operationName;
    private String modifyUserId;    //修改人工号
    private Date modifyTime;        //修改时间


    // 非数据库字段
    int bizId;
    Date shareBatchStartTime;  //用于优先级控制
    Boolean invalid = false;
    Date initTimeout; //共享数据时的初始化时间，用于判断是否超时
    boolean isTimeoutPro; // 用于判断是否已经超时处理了
    boolean isExtracted; // 用于判断是否已经被抽取过了

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
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

    public Integer getDataPoolIdLast() {
        return dataPoolIdLast;
    }

    public void setDataPoolIdLast(Integer dataPoolIdLast) {
        this.dataPoolIdLast = dataPoolIdLast;
    }

    public Integer getDataPoolIdCur() {
        return dataPoolIdCur;
    }

    public void setDataPoolIdCur(Integer dataPoolIdCur) {
        this.dataPoolIdCur = dataPoolIdCur;
    }

    public AreaTypeEnum getAreaTypeLast() {
        return areaTypeLast;
    }

    public void setAreaTypeLast(AreaTypeEnum areaTypeLast) {
        this.areaTypeLast = areaTypeLast;
    }

    public AreaTypeEnum getAreaTypeCur() {
        return areaTypeCur;
    }

    public void setAreaTypeCur(AreaTypeEnum areaTypeCur) {
        this.areaTypeCur = areaTypeCur;
    }

    public Integer getIsRecover() {
        return isRecover;
    }

    public void setIsRecover(Integer isRecover) {
        this.isRecover = isRecover;
    }

    public OperationNameEnum getOperationName() {
        return operationName;
    }

    public void setOperationName(OperationNameEnum operationName) {
        this.operationName = operationName;
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

    public int getBizId() {
        return bizId;
    }

    public void setBizId(int bizId) {
        this.bizId = bizId;
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

    public String getCustomerToken() {
        return bizId + importBatchId + customerId;
    }

    public Date getInitTimeout() {
        return initTimeout;
    }

    public void setInitTimeout(Date initTimeout) {
        this.initTimeout = initTimeout;
    }

    public boolean isTimeoutPro() {
        return isTimeoutPro;
    }

    public void setTimeoutPro(boolean timeoutPro) {
        isTimeoutPro = timeoutPro;
    }

    public boolean isExtracted() {
        return isExtracted;
    }

    public void setExtracted(boolean extracted) {
        isExtracted = extracted;
    }
}
