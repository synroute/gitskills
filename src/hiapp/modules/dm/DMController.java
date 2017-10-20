package hiapp.modules.dm;

import com.google.gson.Gson;
import hiapp.modules.dm.multinumbermode.MultiNumberModeController;
import hiapp.modules.dm.singlenumbermode.SingleNumberModeController;
import hiapp.modules.dm.singlenumbermode.SingleNumberOutboundDataManage;
import hiapp.modules.dm.singlenumbermode.bo.NextOutboundCustomerResult;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dmsetting.DMBizOutboundModelEnum;
import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.data.DmBizRepository;
import hiapp.system.buinfo.User;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class DMController {

    @Autowired
    private DmBizRepository dmBizRepository;


    @Autowired
    SingleNumberModeController singleNumberModeController;

    @Autowired
    MultiNumberModeController multiNumberModeController;

    @RequestMapping(value="/srv/dm/extractNextCustomer.srv", method= RequestMethod.GET, produces="application/json")
    public String extractNextCustomer(HttpServletRequest request,
                                                          @RequestParam("userId") String userId,
                                                          @RequestParam("bizId") String bizId) {
        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(Integer.valueOf(bizId));

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            return singleNumberModeController.extractNextCustomer(request, userId, bizId);

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            return multiNumberModeController.extractNextCustomer(request, userId, bizId);
        }

        return "";
    }

    @RequestMapping(value="/srv/dm/submitOutboundResult.srv", method= RequestMethod.GET, consumes="application/json", produces="application/json")
    public String submitOutboundResult(HttpServletRequest request, @RequestBody String requestBody) {

        //String requestBody = testData();

        Map<String, Object> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId = (String) map.get("bizId");

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(Integer.valueOf(strBizId));

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            return singleNumberModeController.submitOutboundResult(request, requestBody);

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            return multiNumberModeController.submitOutboundResult(request, requestBody);
        }

        return "";
    }

    @RequestMapping(value="/srv/dm/submitHiDialerOutboundResult.srv", method= RequestMethod.POST, consumes="application/json", produces="application/json")
    public String submitHiDialerOutboundResult(HttpServletRequest request, @RequestBody String requestBody) {

        Map<String, Object> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId = (String) map.get("bizId");

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(Integer.valueOf(strBizId));

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            //return singleNumberModeController.submitOutboundResult(request, requestBody);

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            //requestBody = testData();
            return multiNumberModeController.submitHiDialerOutboundResult(request, requestBody);
        }

        return "";
    }

    @RequestMapping(value="/srv/dm/submitAgentScreenPopUp.srv", method= RequestMethod.POST, consumes="application/json", produces="application/json")
    public String submitScreenPopUp(HttpServletRequest request, @RequestBody String requestBody) {

        Map<String, Object> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId = (String) map.get("bizId");

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(Integer.valueOf(strBizId));

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            //return singleNumberModeController.submitOutboundResult(request, requestBody);

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            //requestBody = testData();
            return multiNumberModeController.submitScreenPopUp(request, requestBody);
        }

        return "";
    }

    @RequestMapping(value="/srv/dm/startShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String startShareBatch(@RequestParam("bizId") int bizId, @RequestParam("shareBatchIDs") String strShareBatchIds) {

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(bizId);

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            return singleNumberModeController.startShareBatch(bizId, strShareBatchIds);

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            return multiNumberModeController.startShareBatch(bizId, strShareBatchIds);
        }

        return "";
    }

    @RequestMapping(value="/srv/dm/stopShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String stopShareBatch(@RequestParam("bizId") int bizId, @RequestParam("shareBatchIDs") String strShareBatchIds) {

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(bizId);

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            return singleNumberModeController.stopShareBatch(bizId, strShareBatchIds);

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            return multiNumberModeController.stopShareBatch(bizId, strShareBatchIds);
        }

        return "";
    }

    @RequestMapping(value="/srv/dm/appendCustomersToShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String appendCustomersToShareBatch(@RequestParam("bizId") int bizId, @RequestParam("shareBatchIDs") String strShareBatchIds) {

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(bizId);

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            return singleNumberModeController.appendCustomersToShareBatch(bizId, strShareBatchIds);

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            return multiNumberModeController.appendCustomersToShareBatch(bizId, strShareBatchIds);
        }

        return "";
    }

    // TODO JUST FOR TEST
    private String testData() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("bizId", "1102");
        map.put("resultCodeType", "结束类型1");
        map.put("resultCode", "结束码");
        map.put("importBatchId", "DM_IID_20171018_301");
        //String shareBatchId = (String)map.get("shareBatchId");
        map.put("phoneType", "1");
        map.put("customerId", "DM_CID_20171018_30001");
        map.put("presetTime", "2017-10-25 10:00:00");
        //Map<String, String> resultData = (Map<String, String>)map.get("resultData");
        //Map<String, String> customerInfo = (Map<String, String>)map.get("customerInfo");

        //String jsonResultData=new Gson().toJson(resultData);
        //String jsonCustomerInfo=new Gson().toJson(customerInfo);

        return new Gson().toJson(map);
    }

}
