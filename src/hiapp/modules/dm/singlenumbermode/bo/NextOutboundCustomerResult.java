package hiapp.modules.dm.singlenumbermode.bo;

import hiapp.utils.serviceresult.ServiceResult;

public class NextOutboundCustomerResult extends ServiceResult {

    _inner_info data = new _inner_info();

    public void setImportBatchId(String importBatchId) {
        data.setImportBatchId(importBatchId);
    }

    public void setShareBatchId(String shareBatchId) {
        data.setShareBatchId(shareBatchId);
    }

    public void setCustomerId(String customerId) {
        data.setCustomerId(customerId);
    }
    public void setPhoneType(int phoneType) { data.setPhoneType(phoneType); }

    class _inner_info {

        public void setImportBatchId(String importBatchId) {
            this.importBatchId = importBatchId;
        }

        public void setShareBatchId(String shareBatchId) {
            this.shareBatchId = shareBatchId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public void setPhoneType(int phoneType) { this.phoneType = phoneType; }

        String importBatchId;
        String shareBatchId;
        String customerId;
        int phoneType;
    }
}
