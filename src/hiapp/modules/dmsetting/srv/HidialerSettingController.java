package hiapp.modules.dmsetting.srv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hiapp.modules.dmsetting.data.DmBizHidialerSetting;
import hiapp.utils.serviceresult.ServiceResult;

@RestController
public class HidialerSettingController {
	@Autowired
	private DmBizHidialerSetting dmBizHidialerSetting;
	
	//获取hidialer外呼参数
		@RequestMapping(value = "srv/dm/dmGetBizHidialerSetting.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
		public String dmGetBizHidialerSetting(@RequestParam("bizId") String bizId) {
			
			ServiceResult serviceresult = new ServiceResult();
			try{
				String hidialer=dmBizHidialerSetting.dmGetBizHidialerSetting(bizId);
				serviceresult.setReturnCode(0);
				serviceresult.setReturnMessage(hidialer);
			}catch (Exception e) {
				e.printStackTrace();
				serviceresult.setReturnCode(1);
				serviceresult.setReturnMessage("失败");
			} 
			return serviceresult.toJson();
		}
		
		//修改hidialer外呼参数
				@RequestMapping(value = "srv/dm/dmModifyHidialerSetting.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
				public String dmModifyHidialerSetting(@RequestParam("bizId") String bizId,@RequestParam("mapColumns") String mapColumns) {
					
					ServiceResult serviceresult = new ServiceResult();
					
						if(dmBizHidialerSetting.dmModifyHidialerSetting(bizId, mapColumns))
						{
							serviceresult.setReturnCode(0);
							serviceresult.setReturnMessage("成功");
						}else {
							serviceresult.setReturnCode(1);
							serviceresult.setReturnMessage("失败");
						}
					return serviceresult.toJson();
				}
		
}
