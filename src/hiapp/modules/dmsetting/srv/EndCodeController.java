package hiapp.modules.dmsetting.srv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hiapp.modules.dmsetting.DMEndCode;
import hiapp.modules.dmsetting.data.DmBizEndCodeRepository;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResultCode;
@RestController
public class EndCodeController {
	@Autowired
	private DmBizEndCodeRepository dmBizEndCode;
	//获取所有数据池接口
		@RequestMapping(value = "srv/dm/dmGetAllBizEndCode.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
		public String dmGetAllBizEndCode(@RequestParam("bizId") String bizId,@RequestParam("code") String code) {
			RecordsetResult recordsetResult = new RecordsetResult();
			try{
					List<DMEndCode> listDmEndCode=dmBizEndCode.dmGetAllBizEndCode(bizId,code);
					recordsetResult.setPage(0);
					recordsetResult.setTotal(listDmEndCode.size());
					recordsetResult.setPageSize(listDmEndCode.size());
					recordsetResult.setRows(listDmEndCode);
					
					recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
				} catch (Exception e) {
					e.printStackTrace();
					recordsetResult.setReturnCode(1);
					recordsetResult.setReturnMessage("失败");
				}
				return recordsetResult.toJson();
		}
		
		@RequestMapping(value = "/srv/dm/dmAddBizEndCode.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
		public String dmAddBizEndCode(@RequestParam("bizId") int bizId,
				@RequestParam("endCodeType") String endCodeType,@RequestParam("endCode") String endCode,
				@RequestParam("desc") String description) {
			RecordsetResult recordsetResult = new RecordsetResult();
			try{
				DMEndCode dmEndCode =new DMEndCode();
				dmEndCode.setBizId(bizId);
				dmEndCode.setEndCodeType(endCodeType);
				dmEndCode.setEndCode(endCode);
				dmEndCode.setDesc(description);
				StringBuffer err=new StringBuffer();
					if(dmBizEndCode.dmAddBizEndCode(dmEndCode,err))
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
		
		
		@RequestMapping(value = "srv/dm/dmModifyBizEndCode.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
		public String dmModifyBizEndCode(@RequestParam("bizId") int bizId,
				@RequestParam("endCodeType") String endCodeType,@RequestParam("endCode") String endCode,
				@RequestParam("description") String description,@RequestParam("endCodeType_old") String endCodeType_old,@RequestParam("endCode_old") String endCode_old) {
			RecordsetResult recordsetResult = new RecordsetResult();
			try{
				DMEndCode dmEndCode =new DMEndCode();
				dmEndCode.setBizId(bizId);
				dmEndCode.setEndCodeType(endCodeType);
				dmEndCode.setEndCode(endCode);
				dmEndCode.setDesc(description);
				StringBuffer err=new StringBuffer();
					if(dmBizEndCode.dmModifyBizEndCode(dmEndCode,endCodeType_old,endCode_old,err))
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

		@RequestMapping(value = "srv/dm/dmDeleteBizEndCode.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
		public String dmDeleteBizEndCode(@RequestParam("bizId") int bizId,
				@RequestParam("endCodeType") String endCodeType,@RequestParam("endCode") String endCode) {
			RecordsetResult recordsetResult = new RecordsetResult();
			try{
				DMEndCode dmEndCode =new DMEndCode();
				dmEndCode.setBizId(bizId);
				dmEndCode.setEndCodeType(endCodeType);
				dmEndCode.setEndCode(endCode);
				StringBuffer err=new StringBuffer();
					if(dmBizEndCode.dmDeleteBizEndCode(dmEndCode,err))
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
		
}
