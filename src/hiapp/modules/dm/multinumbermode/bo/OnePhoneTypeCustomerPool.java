package hiapp.modules.dm.multinumbermode.bo;

import java.util.Date;

public class OnePhoneTypeCustomerPool {

    CustomerSharePool customerSharePool;
    CustomerWaitPool customerWaitPool;

    int bizId = 0;
    int phoneType;

    public OnePhoneTypeCustomerPool(int bizId, int dialSequence) {

        this.bizId = bizId;
        this.phoneType = dialSequence;

        customerSharePool = new CustomerSharePool(bizId);
        customerWaitPool = new CustomerWaitPool(bizId);

    }

    public MultiNumberCustomer extractCustomer(String userId) {
        MultiNumberCustomer shareDataItem = customerSharePool.extractCustomer(userId);

        Date now = new Date();

        if (null != shareDataItem) {
            shareDataItem.setExtractTime(now);
            shareDataItem.setUserId(userId);

            // 放入 客户等待池
            customerWaitPool.add(userId, shareDataItem);
        }

        return shareDataItem;
    }

    public void add(MultiNumberCustomer customer) {
        customerSharePool.add(customer);
    }

    public MultiNumberCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {

        return customerWaitPool.removeWaitCustomer(userId, bizId, importBatchId, customerId);
    }


}

