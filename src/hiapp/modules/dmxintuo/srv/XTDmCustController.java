package hiapp.modules.dmxintuo.srv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hiapp.modules.dmxintuo.data.XTDmCustomerRepository;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResultCode;
@RestController
public class XTDmCustController {
	@Autowired
	private XTDmCustomerRepository XTDmCustomerRepository;
	
	@RequestMapping(value = "/srv/dm/dmGetXTBizCustomer.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetXTBizCustomer(@RequestParam("bizId") int bizId,@RequestParam("type") String type,
			@RequestParam("AppId") String AppId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		List<Map<String,String>>  map=new ArrayList<Map<String,String>>(); 
		try {
			
			map=XTDmCustomerRepository.dmGetXTBizCustomer(bizId, type, AppId);
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(map.size());
			recordsetResult.setPageSize(map.size());
			recordsetResult.setRows(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
}
