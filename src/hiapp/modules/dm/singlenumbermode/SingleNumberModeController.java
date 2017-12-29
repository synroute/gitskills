package hiapp.modules.dm.singlenumbermode;

import com.google.gson.Gson;
import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.singlenumbermode.bo.NextOutboundCustomerResult;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.system.buinfo.User;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class SingleNumberModeController {

    @Autowired
    SingleNumberOutboundDataManage singleNumberOutboundDataManage;

    public /*OutboundCustomer*/String extractNextCustomer(String userId,
                                                          String bizId) {

/*        HttpSession session = request.getSession();
        User user=(User) session.getAttribute("user");

        userId = user.getId();
        bizId = "25";*/

        System.out.println(userId + "。。。" + bizId);
        Integer intBizId = Integer.valueOf(bizId);
        SingleNumberModeShareCustomerItem item = singleNumberOutboundDataManage.extractNextOutboundCustomer(userId, intBizId);

        /*item = new SingleNumberModeShareCustomerItem();
        item.setCustomerId("customer");
        item.setShareBatchId("sharebatch");
        item.setImportBatchId("importbatch");*/

        NextOutboundCustomerResult result = new NextOutboundCustomerResult();
        if (null == item) {
            result.setResultCode(ServiceResultCode.CUSTOMER_NONE);
        } else {
            result.setResultCode(ServiceResultCode.SUCCESS);
            result.setCustomerId(item.getCustomerId());
            result.setImportBatchId(item.getImportBatchId());
            result.setShareBatchId(item.getShareBatchId());
        }
        return result.toJson();
    }

    public String submitOutboundResult(String userId, int bizId, String shareBatchId, String importBatchId,
                        String customerId, String resultCodeType, String resultCode,Boolean isPreset,Date presetTime,
                        String dialType, Date dialTime, String customerCallId,
                        Map<String, String> mapCustomizedResultColumn, String jsonCustomerInfo) {

        ServiceResult serviceresult = new ServiceResult();

        singleNumberOutboundDataManage.submitOutboundResult(userId, bizId, shareBatchId, importBatchId,
                customerId, resultCodeType, resultCode, isPreset, presetTime, dialType, dialTime, customerCallId,
                mapCustomizedResultColumn, jsonCustomerInfo);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    public String startShareBatch( int bizId,  String strShareBatchIds) {

        ServiceResult serviceresult = new ServiceResult();

        //List<String> shareBatchIds = new Gson().fromJson(jsonShareBatchIds, List.class);
        List<String> shareBatchIds = new ArrayList<String>();

        String[] arrayShareBatchId = strShareBatchIds.split(",");
        for (String shareBatchId : arrayShareBatchId)
            shareBatchIds.add(shareBatchId);

        singleNumberOutboundDataManage.startShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    public String stopShareBatch( int bizId, String strShareBatchIds) {

        ServiceResult serviceresult = new ServiceResult();

        //List<String> shareBatchIds = new Gson().fromJson(strShareBatchIds, List.class);
        List<String> shareBatchIds = new ArrayList<String>();

        String[] arrayShareBatchId = strShareBatchIds.split(",");
        for (String shareBatchId : arrayShareBatchId)
            shareBatchIds.add(shareBatchId);

        singleNumberOutboundDataManage.stopShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    public Boolean appendCustomersToShareBatch( int bizId, List<String> shareBatchIds) {

        //ServiceResult serviceresult = new ServiceResult();

        //List<String> shareBatchIds = new Gson().fromJson(strShareBatchIds, List.class);
        //List<String> shareBatchIds = new ArrayList<String>();

        //String[] arrayShareBatchId = strShareBatchIds.split(",");
        //for (String shareBatchId : arrayShareBatchId)
        //    shareBatchIds.add(shareBatchId);

        return singleNumberOutboundDataManage.appendCustomersToShareBatch(bizId, shareBatchIds);

        //serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        //return serviceresult.toJson();
    }

    public void cancelOutboundTask(int bizId, List<CustomerBasic> customerBasicList) {
        singleNumberOutboundDataManage.cancelOutboundTask(bizId, customerBasicList);
    }

}
