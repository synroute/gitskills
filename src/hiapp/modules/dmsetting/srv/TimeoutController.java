package hiapp.modules.dmsetting.srv;

import java.util.ArrayList;
import java.util.List;

import hiapp.modules.dmsetting.DMBizPhoneType;
import hiapp.modules.dmsetting.DMTimeoutManagement;
import hiapp.modules.dmsetting.data.DmTimeoutRepository;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TimeoutController {
	@Autowired
	private DmTimeoutRepository dmTimeoutRepository;
	
	@RequestMapping(value = "srv/dm/dmInsertTimeout.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmInsertTimeout(@RequestParam("bizId") String bizId,@RequestParam("isEnable") String isEnable,@RequestParam("timeOutConfig") String timeOutConfig) {
		
		ServiceResult serviceresult = new ServiceResult();
		try{
			if(dmTimeoutRepository.dmInsertTimeoutConfig(bizId,isEnable,timeOutConfig))
			{
				serviceresult.setReturnCode(0);
				serviceresult.setReturnMessage("成功");
			}else {
				serviceresult.setReturnCode(1);
				serviceresult.setReturnMessage("失败");
			}
		}catch (Exception e) {
			e.printStackTrace();
			serviceresult.setReturnCode(1);
			serviceresult.setReturnMessage("失败");
		} 
		return serviceresult.toJson();
	}
	
	@RequestMapping(value = "srv/dm/dmGetTimeoutConfig.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetTimeoutConfig(@RequestParam("bizId") String bizId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
			List<DMTimeoutManagement> listDmBizPhoneType=new ArrayList<DMTimeoutManagement>();
			listDmBizPhoneType=dmTimeoutRepository.dmGetAllTimeoutConfig(bizId);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDmBizPhoneType.size());
			recordsetResult.setPageSize(listDmBizPhoneType.size());
			recordsetResult.setRows(listDmBizPhoneType);
		} catch (Exception e) {
			e.printStackTrace();
			recordsetResult.setReturnCode(1);
			recordsetResult.setReturnMessage("失败");
		}
		return recordsetResult.toJson();
	}
	
	
	
}
