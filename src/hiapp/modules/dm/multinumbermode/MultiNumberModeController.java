package hiapp.modules.dm.multinumbermode;

import com.google.gson.Gson;
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

    @RequestMapping(value="/srv/dm/extractNextCustomer.srv", method= RequestMethod.GET, produces="application/json")
    public String extractNextCustomer(HttpServletRequest request,
                                      @RequestParam("userId") String userId,
                                      @RequestParam("bizId") String bizId) {
        /*System.out.println(userId + "。。。" + bizId);
        Integer intBizId = Integer.valueOf(bizId);
        SingleNumberModeShareCustomerItem item = singleNumberOutboundDataManage.extractNextOutboundCustomer(userId, intBizId);


        NextOutboundCustomerResult result = new NextOutboundCustomerResult();
        if (null == item) {
            result.setResultCode(ServiceResultCode.CUSTOMER_NONE);
        } else {
            result.setResultCode(ServiceResultCode.SUCCESS);
            result.setCustomerId(item.getCustomerId());
            result.setImportBatchId(item.getImportBatchId());
            result.setShareBatchId(item.getShareBatchId());
        }
        return result.toJson();*/
        return "";
    }

    @RequestMapping(value="/srv/dm/submitHiDialerOutboundResult.srv", method= RequestMethod.POST, consumes="application/json", produces="application/json")
    public String submitHiDialerOutboundResult(HttpServletRequest request, @RequestBody String requestBody) {
        //TODO
        return "";
    }

    @RequestMapping(value="/srv/dm/submitOutboundResult.srv", method= RequestMethod.POST, consumes="application/json", produces="application/json")
    public String submitOutboundResult(HttpServletRequest request, @RequestBody String requestBody) {
        /*
        HttpSession session = request.getSession();
        User user=(User) session.getAttribute("user");

        Map<String, Object> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId = (String) map.get("bizId");
        String resultCodeType = (String)map.get("resultCodeType");
        String resultCode = (String)map.get("resultCode");
        String importBatchId = (String)map.get("importBatchId");
        //String shareBatchId = (String)map.get("shareBatchId");
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

        singleNumberOutboundDataManage.submitOutboundResult(user.getId(), Integer.parseInt(strBizId), importBatchId,
                customerId, resultCodeType, resultCode, presetTime , jsonResultData, jsonCustomerInfo);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();*/
        return "";
    }

    @RequestMapping(value="/srv/dm/startShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String startShareBatch(@RequestParam("bizId") int bizId, @RequestParam("shareBatchIDs") String strShareBatchIds) {

        /*ServiceResult serviceresult = new ServiceResult();

        //List<String> shareBatchIds = new Gson().fromJson(jsonShareBatchIds, List.class);
        List<String> shareBatchIds = new ArrayList<String>();

        String[] arrayShareBatchId = strShareBatchIds.split(",");
        for (String shareBatchId : arrayShareBatchId)
            shareBatchIds.add(shareBatchId);

        singleNumberOutboundDataManage.startShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();*/
        return "";
    }

    @RequestMapping(value="/srv/dm/stopShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String stopShareBatch(@RequestParam("bizId") int bizId, @RequestParam("shareBatchIDs") String strShareBatchIds) {
        /*
        ServiceResult serviceresult = new ServiceResult();

        //List<String> shareBatchIds = new Gson().fromJson(strShareBatchIds, List.class);
        List<String> shareBatchIds = new ArrayList<String>();

        String[] arrayShareBatchId = strShareBatchIds.split(",");
        for (String shareBatchId : arrayShareBatchId)
            shareBatchIds.add(shareBatchId);

        singleNumberOutboundDataManage.stopShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();*/
        return "";
    }

    @RequestMapping(value="/srv/dm/appendCustomersToShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String appendCustomersToShareBatch(@RequestParam("bizId") int bizId, @RequestParam("shareBatchIDs") String strShareBatchIds) {
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
