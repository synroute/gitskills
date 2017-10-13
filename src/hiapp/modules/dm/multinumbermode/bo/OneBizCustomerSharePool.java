package hiapp.modules.dm.multinumbermode.bo;

import java.util.HashMap;
import java.util.Map;

public class OneBizCustomerSharePool {

    int bizId;

    Map<Integer, String>  mapPhoneTypeDialSequence;

    // 拨打顺序号 <==> 相应拨打号码的客户池
    Map<Integer, OnePhoneTypeCustomerPool> mapCustomerManage;

    public OneBizCustomerSharePool(int bizId) {
        this.bizId = bizId;
    }

    public MultiNumberCustomer extractCustomer(String userId) {
        MultiNumberCustomer customer;
        for (int dialIndex = 1; dialIndex <= mapPhoneTypeDialSequence.size(); dialIndex++) {
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = mapCustomerManage.get(dialIndex);
            customer = onePhoneTypeCustomerPool.extractCustomer(userId);
            if (null != customer)
                return customer;
        }

        return null;
    }

    public void add(MultiNumberCustomer customer) {

        for (int dialIndex = 1; dialIndex <= mapPhoneTypeDialSequence.size(); dialIndex++) {
            OnePhoneTypeCustomerPool onePhoneTypeCustomerPool = mapCustomerManage.get(dialIndex);
            onePhoneTypeCustomerPool.add(customer);
        }
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

