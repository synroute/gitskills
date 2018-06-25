package hiapp.modules.dm.bo;

import java.util.Date;

public class CustomerBasic {
    private String bizId;
    private String sourceId;        // 共享批次 / 分配批次
    private String importBatchId;   // 导入批次号
    private String customerId;      // 客户号

    private Date initDate;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
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

    public String getSourceToken() {
        return bizId + sourceId;
    }

    public String getCustomerToken() { return bizId + importBatchId + customerId; }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }
}
