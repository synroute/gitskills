package hiapp.modules.dmsetting.srv;

import hiapp.system.buinfo.data.UserRepository;
import hiapp.utils.DbUtil;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.io.PrintWriter;

import hiapp.modules.dmsetting.data.DMBusinessRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月6日 上午10:20:09 
 * 类说明 
 */
@RestController
public class BuinessController {
	@Autowired
	private DMBusinessRepository DMBusinessRepository;
	
	@RequestMapping(value="/srv/buinfo/destroyPermission.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public String destroyPermission(@RequestParam("id") String id) {
		ServiceResult serviceresult = new ServiceResult();
		StringBuffer errMessage = new StringBuffer();
		ServiceResultCode serviceResultCode = DMBusinessRepository.destroyDMBusiness(Integer.parseInt(id),errMessage);
		if (serviceResultCode != ServiceResultCode.SUCCESS) {
			serviceresult.setResultCode(serviceResultCode);
			serviceresult.setReturnMessage(errMessage.toString());
		} else {
			serviceresult.setResultCode(ServiceResultCode.SUCCESS);
			serviceresult.setReturnMessage("删除业务成功！");
		}
		return serviceresult.toJson();
	}
	
	
	
}
