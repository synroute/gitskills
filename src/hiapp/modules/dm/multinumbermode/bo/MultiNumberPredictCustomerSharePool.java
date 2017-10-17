package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dm.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MultiNumberPredictCustomerSharePool {

    @Autowired
    PhoneTypeDialSequence phoneTypeDialSequence;
    
    // bizId <==> {号码类型 <==> 相应拨打号码的客户池}
    Map<Integer, Map<Integer, OnePhoneTypeCustomerPool>> mapCustomerManage;

    CustomerWaitPool customerWaitPool;

    public MultiNumberCustomer extractCustomer(String userId, int bizId) {
        MultiNumberCustomer customer;

        for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.getPhoneTypeNum(bizId); dialIndex++) {
            int phoneType = phoneTypeDialSequence.getPhoneType(bizId, dialIndex);

            Map<Integer, OnePhoneTypeCustomerPool> oneBizCustomerSharePool = mapCustomerManage.get(bizId);
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = oneBizCustomerSharePool.get(phoneType);
            customer = onePhoneTypeCustomerPool.extractCustomer(userId);
            if (null != customer) {
                // 放入 客户等待池
                customerWaitPool.add(userId, customer);
                return customer;
            }
        }

        return null;
    }

    public void add(MultiNumberCustomer customer) {

        Map<Integer, OnePhoneTypeCustomerPool> oneBizCustomerSharePool = mapCustomerManage.get(customer.getBizId());
        if (null == oneBizCustomerSharePool) {
            oneBizCustomerSharePool = new HashMap<Integer, OnePhoneTypeCustomerPool>();
            mapCustomerManage.put(customer.getBizId(), oneBizCustomerSharePool);
        }

        OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = oneBizCustomerSharePool.get(customer.getNextDialPhoneType());
        if (null == onePhoneTypeCustomerPool) {
            onePhoneTypeCustomerPool = new OnePhoneTypeCustomerPool(customer.getBizId(), customer.getNextDialPhoneType());
            oneBizCustomerSharePool.put(customer.getNextDialPhoneType(), onePhoneTypeCustomerPool);
        }

        onePhoneTypeCustomerPool.add(customer);
    }

    public void addWaitResultCustomer(MultiNumberCustomer customer) {
        customerWaitPool.add(customer.getModifyUserId(), customer);
    }


    public void clear() {
        mapCustomerManage.clear();
    }

    public MultiNumberCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId, int phoneType) {
        return customerWaitPool.removeWaitCustomer(userId, bizId, importBatchId, customerId);
    }

    public MultiNumberCustomer getWaitCustomer(String userId, int bizId, String importBatchId, String customerId, int phoneType) {
        return customerWaitPool.getWaitCustome(userId, bizId, importBatchId, customerId);
    }


    public void markShareBatchStopFromCustomerWaitPool(int bizId, List<String> shareBatchIds) {
        customerWaitPool.markShareBatchStopFromCustomerWaitPool(shareBatchIds);
    }

    public void removeShareCustomer(int bizId, List<String> shareBatchIds) {
        Map<Integer, OnePhoneTypeCustomerPool> oneBizCustomerSharePool = mapCustomerManage.get(bizId);
        if (null == oneBizCustomerSharePool)
            return;

        // 号码类型遍历
        for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.getPhoneTypeNum(bizId); dialIndex++) {
            int phoneType = phoneTypeDialSequence.getPhoneType(bizId, dialIndex);
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = oneBizCustomerSharePool.get(phoneType);
            if (null == onePhoneTypeCustomerPool)
                continue;

            onePhoneTypeCustomerPool.removeShareCustomer(shareBatchIds);
        }
    }

    public void timeoutProc() {
        customerWaitPool.timeoutProc();
    }

    // 用户登录通知
    public void onLogin(String userId) {
        customerWaitPool.onLogin(userId);
    }


    void initialize() {

        /*
        for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.size(); dialIndex++) {
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = new OnePhoneTypeCustomerPool(bizId, dialIndex);
            mapCustomerManage.put(dialIndex, onePhoneTypeCustomerPool);

            //bizCustomerSharePool;
        }*/

    }

}

