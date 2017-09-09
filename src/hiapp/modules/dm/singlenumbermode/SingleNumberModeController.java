package hiapp.modules.dm.singlenumbermode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SingleNumberModeController {

    @Autowired
    SingleNumberOutboundDataManage singleNumberOutboundDataManage;

    @RequestMapping(value="/srv/dm/extractNextOutboundCustomer.srv", method= RequestMethod.GET, produces="application/json")
    public /*OutboundCustomer*/String extractNextOutboundCustomer(@RequestParam("userId") String userId,
                                                                  @RequestParam("bizId") String bizId) {
        System.out.println(userId + "。。。" + bizId);
        return "";
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
