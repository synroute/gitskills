package hiapp.modules.dm.multinumbermode;

import hiapp.modules.dm.multinumbermode.bo.CustomerSharePool;
import hiapp.modules.dm.multinumbermode.bo.CustomerWaitPool;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MultiNumberOutboundDataManage {

    CustomerSharePool customerSharePool;
    CustomerWaitPool customerWaitPool;

    public synchronized SingleNumberModeShareCustomerItem extractNextOutboundCustomer(String userId, int bizId) {
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

    }

}

