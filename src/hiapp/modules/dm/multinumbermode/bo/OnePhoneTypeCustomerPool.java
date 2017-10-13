package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dm.multinumbermode.bo.CustomerWaitPool;
import hiapp.modules.dm.multinumbermode.bo.CustomerSharePool;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class OnePhoneTypeCustomerPool {

    CustomerSharePool customerSharePool;
    CustomerWaitPool customerWaitPool;

    int bizId = 0;
    int dialSequence;

    public OnePhoneTypeCustomerPool(int bizId, int dialSequence) {

        this.bizId = bizId;
        this.dialSequence = dialSequence;

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

}
