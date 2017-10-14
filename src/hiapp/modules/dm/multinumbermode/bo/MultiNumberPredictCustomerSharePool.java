package hiapp.modules.dm.multinumbermode.bo;

import java.util.HashMap;
import java.util.Map;

public class OneBizCustomerSharePool {

    // 拨打顺序 <==> 号码类型
    Map<Integer, Integer>  mapPhoneTypeDialSequence;

    // 号码类型 <==> 相应拨打号码的客户池
    Map<Integer, Map<Integer, OnePhoneTypeCustomerPool>> mapCustomerManage;

    public MultiNumberCustomer extractCustomer(String userId, int bizId) {
        MultiNumberCustomer customer;

        for (Map.Entry<Integer, Integer> entry : mapPhoneTypeDialSequence.entrySet()) {
            int phoneType = entry.getValue();

            Map<Integer, OnePhoneTypeCustomerPool> oneBizCustomerSharePool = mapCustomerManage.get(bizId);
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = oneBizCustomerSharePool.get(phoneType);
            customer = onePhoneTypeCustomerPool.extractCustomer(userId);
            if (null != customer)
                return customer;
        }

        return null;
    }

    public void add(MultiNumberCustomer customer) {

        for (int dialIndex = 1; dialIndex <= mapPhoneTypeDialSequence.size(); dialIndex++) {
            Map<Integer, OnePhoneTypeCustomerPool> oneBizCustomerSharePool = mapCustomerManage.get(customer.getBizId());
            if (null == oneBizCustomerSharePool) {
                oneBizCustomerSharePool = new HashMap<Integer, OnePhoneTypeCustomerPool>();
                mapCustomerManage.put(customer.getBizId(), oneBizCustomerSharePool);
            }
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = oneBizCustomerSharePool.get(dialIndex);
            if (null == onePhoneTypeCustomerPool) {
                onePhoneTypeCustomerPool = new OnePhoneTypeCustomerPool(customer.getBizId(), );
                mapCustomerManage.put(dialIndex, oneBizCustomerSharePool);
            }

            onePhoneTypeCustomerPool.add(customer);
        }
    }

    private MultiNumberCustomer removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {
        return null;
    }


    void initialize() {

        // TODO
        mapPhoneTypeDialSequence = new HashMap<Integer, String>();


        for (int dialIndex = 1; dialIndex <= mapPhoneTypeDialSequence.size(); dialIndex++) {
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = new OnePhoneTypeCustomerPool(bizId, dialIndex);
            mapCustomerManage.put(dialIndex, onePhoneTypeCustomerPool);

            //bizCustomerSharePool;
        }

    }

}

