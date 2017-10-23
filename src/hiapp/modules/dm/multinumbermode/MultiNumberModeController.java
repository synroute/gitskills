package hiapp.modules.dm.multinumbermode;

import com.google.gson.Gson;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.modules.dm.singlenumbermode.SingleNumberOutboundDataManage;
import hiapp.modules.dm.singlenumbermode.bo.NextOutboundCustomerResult;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.system.buinfo.User;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class MultiNumberModeController {

    @Autowired
    MultiNumberOutboundDataManage multiNumberOutboundDataManage;

    public String extractNextCustomer(HttpServletRequest request,
                                      String userId,
                                      String bizId) {
        System.out.println(userId + "。。。" + bizId);
        Integer intBizId = Integer.valueOf(bizId);
        MultiNumberCustomer item = multiNumberOutboundDataManage.extractNextOutboundCustomer(userId, intBizId);

        NextOutboundCustomerResult result = new NextOutboundCustomerResult();
        if (null == item) {
            result.setResultCode(ServiceResultCode.CUSTOMER_NONE);
        } else {
            result.setResultCode(ServiceResultCode.SUCCESS);
            result.setCustomerId(item.getCustomerId());
            result.setImportBatchId(item.getImportBatchId());
            result.setShareBatchId(item.getShareBatchId());
            result.setPhoneType(item.getNextDialPhoneType());
        }
        return result.toJson();
    }

    public String submitHiDialerOutboundResult(HttpServletRequest request, String requestBody) {

        //hidialer userId : 0
        HttpSession session = request.getSession();
        User user=(User) session.getAttribute("user");

        Map<String, Object> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId = (String) map.get("bizId");
        String resultCodeType = (String)map.get("resultCodeType");
        String resultCode = (String)map.get("resultCode");
        String importBatchId = (String)map.get("importBatchId");
        //String shareBatchId = (String)map.get("shareBatchId");
        int phoneType = (Integer)map.get("phoneType");
        String customerId = (String)map.get("customerId");

        ServiceResult serviceresult = new ServiceResult();

        multiNumberOutboundDataManage.submitHiDialerOutboundResult(user.getId(), Integer.parseInt(strBizId), importBatchId,
                customerId, phoneType, resultCodeType, resultCode);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    public String submitScreenPopUp(HttpServletRequest request, String requestBody) {

        //
        HttpSession session = request.getSession();
        User user=(User) session.getAttribute("user");

        Map<String, Object> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId = (String) map.get("bizId");
        String importBatchId = (String)map.get("importBatchId");
        //String shareBatchId = (String)map.get("shareBatchId");
        int phoneType = (Integer)map.get("phoneType");
        String customerId = (String)map.get("customerId");

        ServiceResult serviceresult = new ServiceResult();

        multiNumberOutboundDataManage.submitAgentScreenPopUp(user.getId(), Integer.parseInt(strBizId), importBatchId,
                customerId, phoneType);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    public String submitOutboundResult(HttpServletRequest request, String requestBody) {

        HttpSession session = request.getSession();
        User user=(User) session.getAttribute("user");

        Map<String, Object> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId = (String) map.get("bizId");
        String resultCodeType = (String)map.get("resultCodeType");
        String resultCode = (String)map.get("resultCode");
        String importBatchId = (String)map.get("importBatchId");
        //String shareBatchId = (String)map.get("shareBatchId");
        int phoneType = (Integer)map.get("phoneType");
        String customerId = (String)map.get("customerId");
        String strPresetTime = (String) map.get("presetTime");
        Map<String, String> resultData = (Map<String, String>)map.get("resultData");
        Map<String, String> customerInfo = (Map<String, String>)map.get("customerInfo");

        String jsonResultData=new Gson().toJson(resultData);
        String jsonCustomerInfo=new Gson().toJson(customerInfo);
        System.out.println(jsonResultData);
        System.out.println(jsonCustomerInfo);


        ServiceResult serviceresult = new ServiceResult();

        Date presetTime = null;
        if (null != strPresetTime) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                presetTime = sdf.parse(strPresetTime);
            } catch (ParseException e) {
                e.printStackTrace();
                serviceresult.setResultCode(ServiceResultCode.INVALID_PARAM);
                serviceresult.setReturnMessage("preset time invalid");
                return serviceresult.toJson();
            }
        }

        multiNumberOutboundDataManage.submitOutboundResult(user.getId(), Integer.parseInt(strBizId), importBatchId,
                customerId, phoneType, resultCodeType, resultCode, presetTime , jsonResultData, jsonCustomerInfo);

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

    public String appendCustomersToShareBatch( int bizId,  String strShareBatchIds) {
        /*
        ServiceResult serviceresult = new ServiceResult();

        //List<String> shareBatchIds = new Gson().fromJson(strShareBatchIds, List.class);
        List<String> shareBatchIds = new ArrayList<String>();

        String[] arrayShareBatchId = strShareBatchIds.split(",");
        for (String shareBatchId : arrayShareBatchId)
            shareBatchIds.add(shareBatchId);

        singleNumberOutboundDataManage.appendCustomersToShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();*/
        return "";
    }

}
