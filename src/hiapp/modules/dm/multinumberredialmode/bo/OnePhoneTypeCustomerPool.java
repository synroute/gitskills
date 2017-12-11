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

    public MultiNumberRedialCustomer extractCustomer(String userId) {
        MultiNumberRedialCustomer shareDataItem = customerSharePool.extractCustomer(userId);

        Date now = new Date();

        if (null != shareDataItem) {
            //shareDataItem.setState(MultiNumberRedialStateEnum.EXTRACTED);
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

}

