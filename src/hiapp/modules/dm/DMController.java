package hiapp.modules.dm;

import com.google.gson.Gson;
import hiapp.modules.dm.multinumbermode.MultiNumberModeController;
import hiapp.modules.dm.multinumbermode.bo.BizConfig;
import hiapp.modules.dm.singlenumbermode.SingleNumberModeController;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dmsetting.DMBizOutboundModelEnum;
import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.data.DmBizRepository;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @RequestMapping(value="/srv/dm/submitOutboundResult.srv", method= RequestMethod.POST, consumes="application/json", produces="application/json")
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

    /**
     *
     *   <Msg Result="1">
     *       <JobList>
     *           <Item JobId="1" JobName="任务1" State="0" DMBusinessId="1" DataTypeId="0" ServiceNo="60269524" DialPrefix="9" BusinessID="BU0001" DialMode="2" IVRScript="OBKKDYQ" MaxRingCount="35" DialRatio="1" MaxReadyAgentCount="1" MaxCallingCount="50" TimeUnitLong="1" MaxCountPerTimeUnit="150" />
     *           <Item JobId="2" JobName="任务2" State="0" DMBusinessId="2" DataTypeId="0" ServiceNo="60269503" DialPrefix="9" BusinessID="BU0002" DialMode="2" IVRScript="OBXXQDYQ" MaxRingCount="35" DialRatio="1" MaxReadyAgentCount="1" MaxCallingCount="20" TimeUnitLong="1" MaxCountPerTimeUnit="60" />
     *       </JobList>
     *   </Msg>
     */
    @RequestMapping(value="/srv/dm/hiDialerGetJobList.srv", method= RequestMethod.POST, produces="application/xml")
    public String hiDialerGetJobList(HttpServletRequest request) {

        // step 1: 取数据库记录
        List<DMBusiness> listDMBusiness = new ArrayList<DMBusiness>();
        dmBizRepository.getAllDMBusinessforper(listDMBusiness);

        // step 2: 转成 XML 格式数据
        String curTimeString = DateUtil.getCurTimeString();

        Document doc = new Document();
        Element root = new Element("Msg");
        doc.setRootElement(root);
        Element jobList = new Element("JobList");

        for (DMBusiness dmBiz : listDMBusiness) {
            Element jobItem = new Element("Item");
            jobItem.setAttribute("JobId", String.valueOf(dmBiz.getBizId()));
            jobItem.setAttribute("JobName", dmBiz.getName());
            jobItem.setAttribute("State", "0");
            jobItem.setAttribute("DMBusinessId", String.valueOf(dmBiz.getBizId()));
            jobItem.setAttribute("DataTypeId", "0");
            jobItem.setAttribute("DataTypeId", "0");

            String configJson = dmBiz.getConfigJson();
            BizConfig bizConfig = new Gson().fromJson(configJson, BizConfig.class);
            if (null == bizConfig)
                continue;

            if (checkPermitCall(bizConfig.getPermissionCallTime(), curTimeString)) {
                jobItem.setAttribute("ServiceNo", bizConfig.getServiceNo());
                jobItem.setAttribute("DialPrefix", bizConfig.getDialPrefix());
                jobItem.setAttribute("BusinessID", bizConfig.getBusinessID());
                jobItem.setAttribute("IVRScript", bizConfig.getIVRScript());
                jobItem.setAttribute("MaxRingCount", bizConfig.getMaxRingCount());
                jobItem.setAttribute("DialRatio", bizConfig.getDialRatio());
                jobItem.setAttribute("MaxReadyAgentCount", bizConfig.getMaxReadyAgentCount());
                jobItem.setAttribute("MaxCallingCount", bizConfig.getMaxCallingCount());
                jobItem.setAttribute("DialMode", bizConfig.getDialMode());
                jobItem.setAttribute("TimeUnitLong", bizConfig.getTimeUnitLong());
                jobItem.setAttribute("MaxCountPerTimeUnit", bizConfig.getMaxCountPerTimeUnit());

                jobList.addContent(jobItem);
            }
        }

        root.addContent(jobList);

        XMLOutputter outputter = null;
        Format format = Format.getCompactFormat();
        format.setEncoding("UTF-8");
        format.setIndent("    ");
        outputter = new XMLOutputter(format);

        ByteArrayOutputStream byteRsp = new ByteArrayOutputStream();
        try {
            outputter.output(doc, byteRsp);
            String configXml = byteRsp.toString("UTF-8");
            System.out.println(configXml);
            return configXml;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    @RequestMapping(value="/srv/dm/hiDialerGetCustList.srv", method= RequestMethod.POST, consumes="application/xml", produces="application/xml")
    public String hiDialerGetCustList(HttpServletRequest request, @RequestBody String requestBody) {

        Map<String, Object> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId = (String) map.get("bizId");

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(Integer.valueOf(strBizId));

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            //requestBody = testData();
            return multiNumberModeController.hiDialerGetCustList(request, requestBody);
        }

        return "";
    }

    @RequestMapping(value="/srv/dm/hiDialerDialResultNotify.srv", method= RequestMethod.POST, consumes="application/xml", produces="application/json")
    public String hiDialerDialResultNotify(HttpServletRequest request, @RequestBody String requestBody) {

        Map<String, Object> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId = (String) map.get("bizId");

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(Integer.valueOf(strBizId));

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            //requestBody = testData();
            return multiNumberModeController.hiDialerDialResultNotify(request, requestBody);
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

    private boolean checkPermitCall(List<Map<String, String>> permitCallTimeList, String curTimeString) {
        for (Map<String, String> callTimeScope : permitCallTimeList) {
            if (curTimeString.compareTo(callTimeScope.get(Constants.timeStart)) > 0
                    && curTimeString.compareTo(callTimeScope.get(Constants.timeEnd)) < 0)
                return true;
        }
        return false;
    }

}
