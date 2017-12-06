package hiapp.modules.dm.multinumberredialmode.bo;

import java.util.Date;
import java.util.List;

public class OnePhoneTypeCustomerPool {

    MultiNumberRedialCustomerSharePool customerSharePool;
    //CustomerWaitPool customerWaitPool;

    int bizId = 0;
    int phoneType;

    public OnePhoneTypeCustomerPool(int bizId, int dialType) {

        this.bizId = bizId;
        this.phoneType = dialType;

        customerSharePool = new MultiNumberRedialCustomerSharePool(bizId);
        //customerWaitPool = new CustomerWaitPool(bizId);

    }

    public MultiNumberRedialCustomer extractCustomer(String userId) {
        MultiNumberRedialCustomer shareDataItem = customerSharePool.extractCustomer(userId);

        Date now = new Date();

        if (null != shareDataItem) {
            shareDataItem.setState(MultiNumberRedialStateEnum.EXTRACTED);
            shareDataItem.setModifyTime(now);
            shareDataItem.setModifyId(shareDataItem.getModifyId() + 1);
            shareDataItem.setModifyUserId(userId);
            //shareDataItem.setModifyDesc("");
            shareDataItem.setCurDialPhoneType(phoneType);
            PhoneDialInfo phoneDialInfo = shareDataItem.getDialInfoByPhoneType(phoneType);
            shareDataItem.setCurDialPhone(phoneDialInfo.getPhoneNumber());
        }

        return shareDataItem;
    }

    public void add(MultiNumberRedialCustomer customer) {
        customerSharePool.add(customer);
    }

    public void stopShareBatch(List<String> shareBatchIds) {
        customerSharePool.stopShareBatch(shareBatchIds);
    }

/*
    public void addWaitResultCustomer(MultiNumberRedialCustomer customer) {
        customerWaitPool.add(customer.getModifyUserId(), customer);
    }

    public MultiNumberRedialCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {

        return customerWaitPool.removeWaitCustomer(userId, bizId, importBatchId, customerId);
    }

    public MultiNumberRedialCustomer getWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {

        return customerWaitPool.getWaitCustome(userId, bizId, importBatchId, customerId);
    }


    public void markShareBatchStopFromCustomerWaitPool(List<String> shareBatchIds) {
        customerWaitPool.markShareBatchStopFromCustomerWaitPool(shareBatchIds);
    }
*/

}

