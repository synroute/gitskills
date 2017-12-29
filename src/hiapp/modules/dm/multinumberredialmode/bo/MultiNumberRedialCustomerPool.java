package hiapp.modules.dm.multinumberredialmode.bo;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.bo.PhoneTypeDialSequence;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MultiNumberRedialCustomerPool {

    @Autowired
    PhoneTypeDialSequence phoneTypeDialSequence;
    
    @Autowired
    MultiNumberRedialCustomerWaitPool customerWaitPool;

    @Autowired
    private DMBizMangeShare dmBizMangeShare;


    // bizId <==> {号码类型 <==> 号码类型对应的客户池}
    Map<Integer, Map<Integer, OnePhoneTypeCustomerPool>> customerSharePool;


    public MultiNumberRedialCustomer extractCustomer(String userId, int bizId) {
        MultiNumberRedialCustomer customer;

        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);

        for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.getPhoneTypeNum(bizId); dialIndex++) {
            int phoneType = phoneTypeDialSequence.getPhoneTypeByPhoneDialSequence(bizId, dialIndex);

            Map<Integer, OnePhoneTypeCustomerPool> oneBizCustomerSharePool = customerSharePool.get(bizId);
            if (null == oneBizCustomerSharePool)
                continue;

            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = oneBizCustomerSharePool.get(phoneType);
            if (null == onePhoneTypeCustomerPool)
                continue;

            customer = onePhoneTypeCustomerPool.extractCustomer(userId, shareBatchIdList);
            if (null != customer) {
                // 放入 客户等待池
                customerWaitPool.add(userId, customer);
                return customer;
            }
        }

        return null;
    }

    /**
     *
     * @param customer
     * @param theDayDialStrategy   phoneType <==> dialNum
     * @return
     */
    public Boolean add(MultiNumberRedialCustomer customer, Map<Integer, Integer> theDayDialStrategy) {
        Integer nextDialPhoneType = initNextDialPhoneType(customer, theDayDialStrategy);
        if (null == nextDialPhoneType)
            return false;

        Map<Integer, OnePhoneTypeCustomerPool> oneBizCustomerSharePool = customerSharePool.get(customer.getBizId());
        if (null == oneBizCustomerSharePool) {
            oneBizCustomerSharePool = new HashMap<Integer, OnePhoneTypeCustomerPool>();
            customerSharePool.put(customer.getBizId(), oneBizCustomerSharePool);
        }

        OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = oneBizCustomerSharePool.get(nextDialPhoneType);
        if (null == onePhoneTypeCustomerPool) {
            onePhoneTypeCustomerPool = new OnePhoneTypeCustomerPool(customer.getBizId(), nextDialPhoneType);
            oneBizCustomerSharePool.put(nextDialPhoneType, onePhoneTypeCustomerPool);
        }

        onePhoneTypeCustomerPool.add(customer);

        return true;
    }

    public void clear() {
        customerSharePool.clear();
    }

    public MultiNumberRedialCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId, int phoneType) {
        return customerWaitPool.removeWaitCustomer(userId, bizId, importBatchId, customerId);
    }

    public MultiNumberRedialCustomer getWaitCustomer(String userId, int bizId, String importBatchId, String customerId, int phoneType) {
        return customerWaitPool.getWaitCustome(userId, bizId, importBatchId, customerId);
    }


    public void markShareBatchStopFromCustomerWaitPool(int bizId, List<String> shareBatchIds) {
        customerWaitPool.markShareBatchStopFromCustomerWaitPool(bizId, shareBatchIds);
    }

    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        Map<Integer, OnePhoneTypeCustomerPool> oneBizCustomerSharePool = customerSharePool.get(bizId);
        if (null == oneBizCustomerSharePool)
            return;

        // 号码类型遍历
        for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.getPhoneTypeNum(bizId); dialIndex++) {
            int phoneType = phoneTypeDialSequence.getPhoneTypeByPhoneDialSequence(bizId, dialIndex);
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = oneBizCustomerSharePool.get(phoneType);
            if (null == onePhoneTypeCustomerPool)
                continue;

            onePhoneTypeCustomerPool.stopShareBatch(shareBatchIds);
        }
    }

    public void timeoutProc() {
        customerWaitPool.timeoutProc();
    }

    // 用户登录通知
    public void onLogin(String userId) {
        customerWaitPool.onLogin(userId);
    }


    public void initialize() {

        customerSharePool = new HashMap<Integer, Map<Integer, OnePhoneTypeCustomerPool>>();
        //customerWaitPool = new CustomerWaitPool();

        /*
        for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.size(); dialIndex++) {
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = new OnePhoneTypeCustomerPool(bizId, dialIndex);
            customerSharePool.put(dialIndex, onePhoneTypeCustomerPool);

            //bizCustomerSharePool;
        }*/

    }

    public Integer calcNextDialPhoneType(MultiNumberRedialCustomer customer) {
        Integer curPhoneType = customer.getCurDialPhoneType();
        if (null == curPhoneType || 0 == curPhoneType)
            return  null;

        for (int dialSeq = 1; dialSeq <= 10; dialSeq++) {
            Integer nextPhoneType = phoneTypeDialSequence.getNextDialPhoneType(customer.getBizId(), curPhoneType);
            if (null == nextPhoneType)
                return null;

            PhoneDialInfo nextPhoneDialInfo = customer.getDialInfoByPhoneType(nextPhoneType);
            if (null != nextPhoneDialInfo.getPhoneNumber() && !nextPhoneDialInfo.getPhoneNumber().isEmpty()) {
                return nextPhoneType;
            }

            curPhoneType = nextPhoneType;
        }

        return null;
    }

    /**
     *
     * @param customer
     * @param theDayDialStrategy   phoneType <==> dialNum
     * @return
     */
    public Integer initNextDialPhoneType(MultiNumberRedialCustomer customer, Map<Integer, Integer> theDayDialStrategy) {
        Integer nextPhoneType = customer.getNextDialPhoneType();
        if (null != nextPhoneType && 0 != nextPhoneType)
            return nextPhoneType;

        for (int dialSeq=1; dialSeq<=phoneTypeDialSequence.getPhoneTypeNum(customer.getBizId()); dialSeq++) {
            nextPhoneType = phoneTypeDialSequence.getPhoneTypeByPhoneDialSequence(customer.getBizId(), dialSeq);

            String phoneTypeNumber = customer.getDialInfoByPhoneType(nextPhoneType).getPhoneNumber();
            Integer phoneTypeDialNum = theDayDialStrategy.get(nextPhoneType);
            if (null != phoneTypeNumber && !phoneTypeNumber.isEmpty() && null != phoneTypeDialNum && phoneTypeDialNum != 0 )
                break;

            nextPhoneType = null;  // NOTE: 需要清除
        }

        customer.setNextDialPhoneType(nextPhoneType);
        return nextPhoneType;
    }

    public List<MultiNumberRedialCustomer> cancelShare(int bizId, List<CustomerBasic> customerBasicList) {
        Map<Integer, OnePhoneTypeCustomerPool> oneBizCustomerSharePool = customerSharePool.get(bizId);
        if (null == oneBizCustomerSharePool)
            return null;

        List<MultiNumberRedialCustomer> customerList = new ArrayList<MultiNumberRedialCustomer>();

        // 号码类型遍历
        for (int dialIndex = 1; dialIndex <= phoneTypeDialSequence.getPhoneTypeNum(bizId); dialIndex++) {
            int phoneType = phoneTypeDialSequence.getPhoneTypeByPhoneDialSequence(bizId, dialIndex);
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = oneBizCustomerSharePool.get(phoneType);
            if (null == onePhoneTypeCustomerPool)
                continue;

            customerList.addAll(onePhoneTypeCustomerPool.cancelShare(customerBasicList));
        }

        return customerList;
    }

}
