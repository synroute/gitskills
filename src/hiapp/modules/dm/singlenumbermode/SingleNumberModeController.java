package hiapp.modules.dm.singlenumbermode;

import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SingleNumberModeController {

    public SingleNumberModeController() {
        System.out.println("----------");
    }

    @Autowired
    SingleNumberOutboundDataManage singleNumberOutboundDataManage;

    @RequestMapping(value="/srv/dm/extractNextOutboundCustomer.srv", method= RequestMethod.GET, produces="application/json")
    public /*OutboundCustomer*/String extractNextOutboundCustomer(@RequestParam("userId") String userId,
                                                                  @RequestParam("bizId") String bizId) {
        System.out.println(userId + "。。。" + bizId);
        Integer intBizId = Integer.valueOf(bizId);
        SingleNumberModeShareCustomerItem item = singleNumberOutboundDataManage.extractNextOutboundCustomer(userId, intBizId);

        RecordsetResult recordsetResult = new RecordsetResult();
        try {
            List<SingleNumberModeShareCustomerItem> itemList = new ArrayList<SingleNumberModeShareCustomerItem>();
            itemList.add(item);

            recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
            recordsetResult.setPage(0);
            recordsetResult.setTotal(itemList.size());
            recordsetResult.setPageSize(itemList.size());
            recordsetResult.setRows(itemList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recordsetResult.toJson();
    }

    @RequestMapping(value="/srv/dm/submitOutboundResult.srv", method= RequestMethod.POST, produces="application/json")
    public String submitOutboundResult(@RequestBody String requestBody) {
        String resultCodeType; String resultCode; String data;
       return "";
    }

    @RequestMapping(value="/srv/dm/startShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String startShareBatch(@RequestParam("shareBatchID") String shareBatchID) {
        return "";
    }

    @RequestMapping(value="/srv/dm/stopShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String stopShareBatch(@RequestParam("shareBatchID") String shareBatchID) {
        return "";
    }

    @RequestMapping(value="/srv/dm/appendCustomersToShareBatch.srv", method= RequestMethod.GET, produces="application/json")
    public String appendCustomersToShareBatch(@RequestParam("shareBatchID") String shareBatchID) {
        return "";
    }

}
