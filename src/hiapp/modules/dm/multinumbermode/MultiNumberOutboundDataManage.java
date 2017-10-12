package hiapp.modules.dm.multinumbermode;

import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.modules.dm.multinumbermode.bo.OneBizCustomerSharePool;
import hiapp.modules.dm.multinumbermode.dao.MultiNumberPredictModeDAO;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MultiNumberOutboundDataManage {

    @Autowired
    MultiNumberPredictModeDAO multiNumberPredictModeDAO;


    // bizId <==> OneBizCustomerSharePool
    Map<Integer, OneBizCustomerSharePool> mapBizIdVsCustomerPool;


    public synchronized SingleNumberModeShareCustomerItem extractNextOutboundCustomer(String userId, int bizId) {
        OneBizCustomerSharePool bizCustomerSharePool = mapBizIdVsCustomerPool.get(bizId);
        bizCustomerSharePool.extractCustomer(userId);

        return null;
    }

    public String submitHiDialerOutboundResult(String userId, int bizId, String importBatchId, String customerId,
                                       String resultCodeType, String resultCode, Date presetTime, String resultData,
                                       String customerInfo) {
        return "";
    }

    public String submitOutboundResult(String userId, int bizId, String importBatchId, String customerId,
                                       String resultCodeType, String resultCode, Date presetTime, String resultData,
                                       String customerInfo) {
        return "";
    }

    /*
     * 重新初始化处理不适合，举例说明：系统运行中，进行启用共享批次操作，会导致获取外呼客户的功能暂停一段时间。
     *
     */
    public String startShareBatch(int bizId, List<String> shareBatchIds) {
        return "";
    }

    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
    }

    public String appendCustomersToShareBatch(int bizId, List<String> shareBatchIds) {
        return "";
    }

    // 用户登录通知
    public void onLogin(String userId) {
    }


    ////////////////////////////////////////////////////////////

    private void initialize() {

        mapBizIdVsCustomerPool = new HashMap<Integer, OneBizCustomerSharePool>();

        List<MultiNumberCustomer> customerList = multiNumberPredictModeDAO.getAllActiveCustomers();
        for (MultiNumberCustomer customer : customerList) {
            OneBizCustomerSharePool bizCustomerSharePool = mapBizIdVsCustomerPool.get(customer.getBizId());
            if (null == bizCustomerSharePool) {
                bizCustomerSharePool = new OneBizCustomerSharePool(customer.getBizId());
                bizCustomerSharePool.add(customer);
            }
        }

    }

}
