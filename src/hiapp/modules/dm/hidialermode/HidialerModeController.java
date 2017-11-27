package hiapp.modules.dm.hidialermode;

import hiapp.modules.dm.Constants;
import hiapp.modules.dm.hidialermode.bo.HidialerModeCustomer;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
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

@Component
public class HidialerModeController {

    @Autowired
    HidialerOutboundDataManage hidialerOutboundDataManage;


    /**
     * @param bizId
     * @param count
     * @return
     *
     * <Msg Result="1" CustomerCount="1">
     *    <CustList>
     *    <Item DMBusinessId="24" IID="20170309P_0001" CID="2398636" TaskID="20170309T0001" PhoneType="1" PhoneNum="15777731180"/>
     *    </CustList>
     * </Msg>
     *
     */
    public String hiDialerGetCustList(int bizId, int count) {

        List<HidialerModeCustomer> customerList = hidialerOutboundDataManage.extractNextOutboundCustomer(
                Constants.HiDialerUserId, bizId, count);

        Document doc = new Document();
        Element root = new Element("Msg");
        root.setAttribute("Result", "1");
        doc.setRootElement(root);
        Element custList = new Element("CustList");

        for (HidialerModeCustomer customer : customerList) {
            Element item = new Element("Item");
            item.setAttribute("DMBusinessId", String.valueOf(customer.getBizId()));
            item.setAttribute("IID", customer.getImportBatchId());
            item.setAttribute("CID", customer.getCustomerId());
            item.setAttribute("TaskID", customer.getShareBatchId());
//            item.setAttribute("PhoneType", String.valueOf(customer.getCurDialPhoneType()));
            item.setAttribute("PhoneNum", customer.getPhoneNumber());

            custList.addContent(item);
        }

        root.addContent(custList);

        return XMLUtil.outputDocumentToString(doc);
    }

    public String hiDialerDialResultNotify(int bizId, String importBatchId, String customerId, String shareBatchId,
                                           String phoneType, String resultCode, String customerCallID)
    {
        hidialerOutboundDataManage.hiDialerDialResultNotify(Constants.HiDialerUserId, bizId, importBatchId,
                customerId, Integer.valueOf(phoneType), resultCode, resultCode, customerCallID);

        Document doc = new Document();
        Element root = new Element("Msg");
        root.setAttribute("Result", "1");
        root.setAttribute("Description", "");
        doc.setRootElement(root);
        return XMLUtil.outputDocumentToString(doc);
    }

    public String submitScreenPopUp(String userId, String strBizId, String importBatchId, String customerId, String strPhoneType) {

        ServiceResult serviceresult = new ServiceResult();

        hidialerOutboundDataManage.submitAgentScreenPopUp(userId, Integer.parseInt(strBizId), importBatchId,
                customerId, Integer.valueOf(strPhoneType));

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

    public String submitOutboundResult(String userId, int bizId, String shareBatchId, String importBatchId,
                       String customerId, String resultCodeType, String resultCode,
                       Boolean isPreset, Date presetTime,
                       String dialType, Date dialTime, String customerCallId, String jsonCustomerInfo) {

        ServiceResult serviceresult = new ServiceResult();

        hidialerOutboundDataManage.submitOutboundResult(userId, bizId, importBatchId, customerId,
                resultCodeType, resultCode, isPreset, presetTime,
                dialType, dialTime, customerCallId, jsonCustomerInfo);

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

        hidialerOutboundDataManage.startShareBatch(bizId, shareBatchIds);

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

        hidialerOutboundDataManage.stopShareBatch(bizId, shareBatchIds);

        serviceresult.setResultCode(ServiceResultCode.SUCCESS);
        return serviceresult.toJson();
    }

}

