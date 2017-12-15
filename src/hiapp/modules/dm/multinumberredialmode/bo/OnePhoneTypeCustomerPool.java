package hiapp.modules.dm.multinumberredialmode.bo;

import java.util.Date;
import java.util.List;

public class OnePhoneTypeCustomerPool {

    MultiNumberRedialCustomerSharePool customerSharePool;

    int bizId = 0;
    int phoneType;

    public OnePhoneTypeCustomerPool(int bizId, int dialType) {

        this.bizId = bizId;
        this.phoneType = dialType;

        customerSharePool = new MultiNumberRedialCustomerSharePool(bizId);

    }

    public MultiNumberRedialCustomer extractCustomer(String userId, List<String> shareBatchIdList) {
        MultiNumberRedialCustomer shareDataItem = customerSharePool.extractCustomer(userId, shareBatchIdList);

        if (null != shareDataItem) {
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

}

