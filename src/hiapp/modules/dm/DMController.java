package hiapp.modules.dm;

import com.google.gson.Gson;
import hiapp.modules.dm.multinumbermode.MultiNumberModeController;
import hiapp.modules.dm.multinumbermode.bo.BizConfig;
import hiapp.modules.dm.singlenumbermode.SingleNumberModeController;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dm.util.XMLUtil;
import hiapp.modules.dmsetting.DMBizOutboundModelEnum;
import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.data.DmBizRepository;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
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

        return XMLUtil.outputDocumentToString(doc);
    }

    /**
     * @param request
     * @param requestBody   :  <Msg JobId="13" Count="10"></Msg>
     * @return
     */
    @RequestMapping(value="/srv/dm/hiDialerGetCustList.srv", method= RequestMethod.POST, consumes="application/xml", produces="application/xml")
    public String hiDialerGetCustList(HttpServletRequest request, @RequestBody String requestBody) {

        StringReader xmlString = new StringReader(requestBody);
        InputSource source = new InputSource(xmlString);
        // 创建一个新的SAXBuilder
        SAXBuilder saxb = new SAXBuilder();
        Document doc;

        try {
            doc = saxb.build(source);
        } catch (JDOMException e) {
            e.printStackTrace();
            ServiceResult result = new ServiceResult();
            result.setResultCode(ServiceResultCode.INVALID_PARAM);
            return result.toJson();
        } catch (IOException e) {
            e.printStackTrace();
            ServiceResult result = new ServiceResult();
            result.setResultCode(ServiceResultCode.INVALID_PARAM);
            return result.toJson();
        }

        // 取的根元素
        Element root = doc.getRootElement();
        String strBizId = root.getAttributeValue("JobId");
        String strCount = root.getAttributeValue("Count");

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(Integer.valueOf(strBizId));

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            //requestBody = testData();
            return multiNumberModeController.hiDialerGetCustList(Integer.valueOf(strBizId), Integer.valueOf(strCount));
        }

        return "";
    }

    /**
     *
     * @param request
     * @param requestBody
     *
     *   <Msg DMBusinessId="1" IID="20171010P_0001" CID="4575046" TaskID="20171010T_0001" PhoneType="1" PhoneNum="013582329718" ResultCode="1" CustomerCallID="978355660"></Msg>
     *
     * @return
     */

    @RequestMapping(value="/srv/dm/hiDialerDialResultNotify.srv", method= RequestMethod.POST, consumes="application/xml", produces="application/xml")
    public String hiDialerDialResultNotify(HttpServletRequest request, @RequestBody String requestBody) {

        StringReader xmlString = new StringReader(requestBody);
        InputSource source = new InputSource(xmlString);
        // 创建一个新的SAXBuilder
        SAXBuilder saxb = new SAXBuilder();
        Document doc;

        try {
            doc = saxb.build(source);
        } catch (JDOMException e) {
            e.printStackTrace();
            ServiceResult result = new ServiceResult();
            result.setResultCode(ServiceResultCode.INVALID_PARAM);
            return result.toJson();
        } catch (IOException e) {
            e.printStackTrace();
            ServiceResult result = new ServiceResult();
            result.setResultCode(ServiceResultCode.INVALID_PARAM);
            return result.toJson();
        }

        // 取的根元素
        Element root = doc.getRootElement();
        String strBizId = root.getAttributeValue("DMBusinessId");
        String importBatchId = root.getAttributeValue("IID");
        String customerId = root.getAttributeValue("CID");
        String shareBatchId = root.getAttributeValue("TaskID");
        String phoneType = root.getAttributeValue("PhoneType");
        String resultCode = root.getAttributeValue("ResultCode");
        String customerCallID = root.getAttributeValue("CustomerCallID");

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(Integer.valueOf(strBizId));

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            //requestBody = testData();
            return multiNumberModeController.hiDialerDialResultNotify(Integer.valueOf(strBizId), importBatchId,
                    customerId, shareBatchId, phoneType, resultCode, customerCallID);
        }

        return "";
    }

    @RequestMapping(value="/srv/dm/submitAgentScreenPopUp.srv", method= RequestMethod.POST, consumes="application/json", produces="application/json")
    public String submitScreenPopUp(HttpServletRequest request, @RequestBody String requestBody) {

        //HttpSession session = request.getSession();
        //User user=(User) session.getAttribute("user");
        String userId = "0"; // hiDialer 用户Id

        Map<String, String> map = new Gson().fromJson(requestBody, Map.class);
        String strBizId =  map.get("bizId");
        String importBatchId = map.get("importBatchId");
        //String shareBatchId = map.get("shareBatchId");
        String strPhoneType = map.get("phoneType");
        String customerId = map.get("customerId");

        DMBusiness dmBusiness = dmBizRepository.getDMBusinessByBizId(Integer.valueOf(strBizId));

        if (DMBizOutboundModelEnum.ManualDistributeShare.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleDialHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberRedial.getOutboundID() == dmBusiness.getModeId()) {
            //return singleNumberModeController.submitOutboundResult(request, requestBody);

        } else if (DMBizOutboundModelEnum.MultiNumberRedial.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.SingleNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {

        } else if (DMBizOutboundModelEnum.MultiNumberHiDialer.getOutboundID() == dmBusiness.getModeId()) {
            //requestBody = testData();
            return multiNumberModeController.submitScreenPopUp(userId, strBizId, importBatchId, customerId, strPhoneType);
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

    private boolean checkPermitCall(List<Map<String, String>> permitCallTimeList, String curTimeString) {
        for (Map<String, String> callTimeScope : permitCallTimeList) {
            if (curTimeString.compareTo(callTimeScope.get(Constants.timeStart)) > 0
                    && curTimeString.compareTo(callTimeScope.get(Constants.timeEnd)) < 0)
                return true;
        }
        return false;
    }

}
