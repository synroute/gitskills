package hiapp.modules.dmsetting.srv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hiapp.modules.dmsetting.beanOld.DMEndCode;
import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResultCode;
@RestController
public class ADDSettingController {
	@Autowired
	private DmBizOutboundConfigRepository dmBizOutboundConfig;
	//获取所有数据池接口
		@RequestMapping(value = "srv/dm/dmGetAllBizOutboundSetting.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
		public String dmGetAllBizOutboundSetting(@RequestParam("bizId") int bizId) {
			RecordsetResult recordsetResult = new RecordsetResult();
			String result="";
			try{
				result=dmBizOutboundConfig.dmGetAllBizOutboundSetting(bizId);
					
				} catch (Exception e) {
					e.printStackTrace();
					recordsetResult.setReturnCode(1);
					recordsetResult.setReturnMessage("失败");
				}
				return result;
		}
		@RequestMapping(value = "srv/dm/dmModifyOutboundSetting.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
		public String dmModifyOutboundSetting(@RequestParam("bizId") int bizId,
				@RequestParam("MapColumns") String MapColumns) {
			RecordsetResult recordsetResult = new RecordsetResult();
			try{
				
					StringBuffer err=new StringBuffer();
					if(dmBizOutboundConfig.dmModifyOutboundSetting(bizId,MapColumns,err))
					{
						recordsetResult.setReturnCode(0);
						recordsetResult.setReturnMessage("成功");
					}else
					{
						recordsetResult.setReturnCode(1);
						recordsetResult.setReturnMessage(err.toString());
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					recordsetResult.setReturnCode(1);
					recordsetResult.setReturnMessage("失败");
				}
				return recordsetResult.toJson();
		}

		@RequestMapping(value = "srv/dm/dmModifyBizRedailState.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
		public String dmModifyBizRedailState(@RequestParam("bizId") int bizId,
				@RequestParam("MapColumns") String MapColumns) {
			RecordsetResult recordsetResult = new RecordsetResult();
			try{
				
					
					if(dmBizOutboundConfig.dmModifyBizRedailState(bizId,MapColumns))
					{
						recordsetResult.setReturnCode(0);
						recordsetResult.setReturnMessage("成功");
					}else
					{
						recordsetResult.setReturnCode(1);
						recordsetResult.setReturnMessage("失败");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					recordsetResult.setReturnCode(1);
					recordsetResult.setReturnMessage("失败");
				}
				return recordsetResult.toJson();
		}

		
}
