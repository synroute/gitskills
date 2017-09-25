package hiapp.modules.dm.singlenumbermode;

import com.google.gson.Gson;
import hiapp.modules.dm.singlenumbermode.bo.NextOutboundCustomerResult;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.system.buinfo.User;
import hiapp.utils.serviceresult.RecordsetResult;
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
public class SingleNumberModeController {

    @Autowired
    SingleNumberOutboundDataManage singleNumberOutboundDataManage;

    @RequestMapping(value="/srv/dm/extractNextCustomer.srv", method= RequestMethod.GET, produces="application/json")
    public /*OutboundCustomer*/String extractNextCustomer(HttpServletRequest request,
                                                          @RequestParam("userId") String userId,
                                                          @RequestParam("bizId") String bizId) {

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

    @RequestMapping(value="/srv/dm/submitOutboundResult.srv", method= RequestMethod.POST, produces="application/json")
    public String submitOutboundResult(HttpServletRequest request, @RequestBody String requestBody) {

        // 客户原信息变更、拨打信息、结果信息
        /*{"bizId":11,"importBatchId":77","shareBatchId":"66","customerId":91,"resultCodeType":"结束","resultCode":"结案"," +
            "presetTime":"2017-10-05 15:30:00","resultData":{xxx},"customerInfo":{xxx}}*/

        HttpSession session = request.getSession();
        User user=(User) session.getAttribute("user");

        Map<String, String> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId = map.get("bizId");
        String resultCodeType = map.get("resultCodeType");
        String resultCode = map.get("resultCode");
        String importBatchId = map.get("importBatchId");
        String shareBatchId = map.get("shareBatchId");
        String customerId = map.get("customerId");
        String resultData = map.get("resultData");
        String customerInfo = map.get("customerInfo");

        ServiceResult serviceresult = new ServiceResult();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date presetTime = null;
        try {
            presetTime = sdf.parse(map.get("presetTime"));
        } catch (ParseException e) {
            e.printStackTrace();
            serviceresult.setResultCode(ServiceResultCode.INVALID_PARAM);
            serviceresult.setReturnMessage("preset time invalid");
            return serviceresult.toJson();
        }

        singleNumberOutboundDataManage.submitOutboundResult(user.getId(), Integer.parseInt(strBizId), importBatchId, shareBatchId, customerId,
                resultCodeType, resultCode, presetTime , resultData, customerInfo);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    @RequestMapping(value="/srv/dm/startShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String startShareBatch(@RequestParam("bizId") int bizId, @RequestParam("shareBatchIDs") String jsonShareBatchIds) {

        ServiceResult serviceresult = new ServiceResult();

        //List<String> shareBatchIds = new Gson().fromJson(jsonShareBatchIds, List.class);
        List<String> shareBatchIds = new ArrayList<String>();

        String[] arrayShareBatchId = jsonShareBatchIds.split(",");
        for (String shareBatchId : arrayShareBatchId)
            shareBatchIds.add(shareBatchId);

        singleNumberOutboundDataManage.startShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    @RequestMapping(value="/srv/dm/stopShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String stopShareBatch(@RequestParam("bizId") int bizId, @RequestParam("shareBatchIDs") String strShareBatchIds) {

        ServiceResult serviceresult = new ServiceResult();

        List<String> shareBatchIds = new Gson().fromJson(strShareBatchIds, List.class);

        singleNumberOutboundDataManage.stopShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    @RequestMapping(value="/srv/dm/appendCustomersToShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String appendCustomersToShareBatch(@RequestParam("bizId") int bizId, @RequestParam("shareBatchIDs") String strShareBatchIds) {

        ServiceResult serviceresult = new ServiceResult();

        List<String> shareBatchIds = new Gson().fromJson(strShareBatchIds, List.class);

        singleNumberOutboundDataManage.appendCustomersToShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

}
