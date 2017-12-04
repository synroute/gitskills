package hiapp.modules.dm.manualmode;

import com.google.gson.Gson;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomer;
import hiapp.modules.dm.multinumbermode.MultiNumberOutboundDataManage;
import hiapp.modules.dm.singlenumbermode.bo.NextOutboundCustomerResult;
import hiapp.system.buinfo.User;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class ManualModeController {

    @Autowired
    ManualOutboundDataManage manualOutboundDataManage;

    public String extractNextCustomer(String userId, String bizId) {
        Integer intBizId = Integer.valueOf(bizId);
        ManualModeCustomer item = manualOutboundDataManage.extractNextOutboundCustomer(userId, intBizId);


        NextOutboundCustomerResult result = new NextOutboundCustomerResult();
        if (null == item) {
            result.setResultCode(ServiceResultCode.CUSTOMER_NONE);
        } else {
            result.setResultCode(ServiceResultCode.SUCCESS);
            result.setCustomerId(item.getCustomerId());
            result.setImportBatchId(item.getImportBatchId());
            result.setShareBatchId(item.getSourceId());
        }
        return result.toJson();
    }

    public String submitOutboundResult(String userId, int bizId, String shareBatchId, String importBatchId,
                       String customerId, String resultCodeType, String resultCode,Boolean isPreset,Date presetTime,
                       String dialType, Date dialTime, String customerCallId,
                       Map<String, String> mapCustomizedResultColumn, String jsonCustomerInfo) {

        ServiceResult serviceresult = new ServiceResult();

        manualOutboundDataManage.submitOutboundResult(userId, bizId,
                shareBatchId, importBatchId, customerId, resultCodeType, resultCode,
                isPreset, presetTime, dialType, dialTime, customerCallId, mapCustomizedResultColumn, jsonCustomerInfo);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    public String startShareBatch(int bizId, String strShareBatchIds) {
        ServiceResult serviceresult = new ServiceResult();

        //List<String> shareBatchIds = new Gson().fromJson(jsonShareBatchIds, List.class);
        List<String> shareBatchIds = new ArrayList<String>();

        String[] arrayShareBatchId = strShareBatchIds.split(",");
        for (String shareBatchId : arrayShareBatchId)
            shareBatchIds.add(shareBatchId);

        manualOutboundDataManage.startShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    public String stopShareBatch(int bizId, String strShareBatchIds) {
        ServiceResult serviceresult = new ServiceResult();

        //List<String> shareBatchIds = new Gson().fromJson(strShareBatchIds, List.class);
        List<String> shareBatchIds = new ArrayList<String>();

        String[] arrayShareBatchId = strShareBatchIds.split(",");
        for (String shareBatchId : arrayShareBatchId)
            shareBatchIds.add(shareBatchId);

        manualOutboundDataManage.stopShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

}
