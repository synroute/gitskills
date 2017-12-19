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

            shareDataItem.setExtractTime(new Date());

            //注意：只是在内存中清零了拨打计数
            if (MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL.equals(shareDataItem.getState())
                || MultiNumberRedialStateEnum.WAIT_NEXT_DAY_DIAL.equals(shareDataItem.getState())) {
                for (int i = 1; i <= 10; i++) {
                    PhoneDialInfo dialInfo = shareDataItem.getDialInfoByPhoneType(i);
                    dialInfo.setDialCount(0);
                    dialInfo.setCausePresetDialCount(0);
                }
            }
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

