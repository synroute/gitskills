package hiapp.modules.dmsetting.srv;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.modules.dmsetting.data.DMBizPermissionRepository;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
@RestController
public class DMPermissionController {
	@Autowired
	private DMBizPermissionRepository dmBizPermissionRepository;
	
	
	
	@RequestMapping(value = "srv/dm/dmGetAllBizPermission.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBizPermission() {
		JsonObject jsonObject =dmBizPermissionRepository.getAll();
		
		return jsonObject.toString();
	}
	
	@RequestMapping(value = "srv/dm/dmSubmitBizPermission.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmSubmitBizPermission(@RequestParam("MapColumns") String mapColumns) {
		JsonArray returnData = new JsonParser().parse(mapColumns).getAsJsonArray();
		ServiceResult serviceresult = new ServiceResult();
		if(dmBizPermissionRepository.submit(returnData))
		{
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("成功");
		}else {
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("失败");
		}
		return serviceresult.toJson();
	}
	
}
