package hiapp.modules.dm.multinumberredialmode;

import hiapp.modules.dm.Constants;
import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.multinumberredialmode.bo.MultiNumberRedialCustomer;
import hiapp.modules.dm.singlenumbermode.bo.NextOutboundCustomerResult;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dm.util.XMLUtil;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class MultiNumberRedialController {

    @Autowired
    MultiNumberRedialDataManage multiNumberOutboundDataManage;

    public String extractNextCustomer(String userId, String bizId) {
        Integer intBizId = Integer.valueOf(bizId);
        MultiNumberRedialCustomer item = multiNumberOutboundDataManage.extractNextOutboundCustomer(userId, intBizId);

        NextOutboundCustomerResult result = new NextOutboundCustomerResult();
        if (null == item) {
            result.setResultCode(ServiceResultCode.CUSTOMER_NONE);
        } else {
            result.setResultCode(ServiceResultCode.SUCCESS);
            result.setCustomerId(item.getCustomerId());
            result.setImportBatchId(item.getImportBatchId());
            result.setShareBatchId(item.getShareBatchId());
            result.setPhoneType(item.getCurDialPhoneType());
        }
        return result.toJson();
    }

    public String submitOutboundResult(String userId, int bizId, String shareBatchId, String importBatchId,
                       String customerId, String strPhoneType, String resultCodeType, String resultCode,
                       Boolean isPreset, Date presetTime, String dialType, Date dialTime, String customerCallId,
                       Map<String, String> mapCustomizedResultColumn, String jsonCustomerInfo) {

        ServiceResult serviceresult = new ServiceResult();

        multiNumberOutboundDataManage.submitOutboundResult(userId, bizId, importBatchId, customerId,
                Integer.valueOf(strPhoneType), resultCodeType, resultCode, isPreset, presetTime,
                dialType, dialTime, customerCallId, mapCustomizedResultColumn, jsonCustomerInfo);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    public String startShareBatch( int bizId, String strShareBatchIds) {

        ServiceResult serviceresult = new ServiceResult();

        //List<String> shareBatchIds = new Gson().fromJson(jsonShareBatchIds, List.class);
        List<String> shareBatchIds = new ArrayList<String>();

        String[] arrayShareBatchId = strShareBatchIds.split(",");
        for (String shareBatchId : arrayShareBatchId)
            shareBatchIds.add(shareBatchId);

        multiNumberOutboundDataManage.startShareBatch(bizId, shareBatchIds);

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

        multiNumberOutboundDataManage.stopShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    public void cancelOutboundTask(int bizId, List<CustomerBasic> customerBasicList) {
        multiNumberOutboundDataManage.cancelOutboundTask(bizId, customerBasicList);
    }

}

