package hiapp.modules.dmsetting.srv;

import java.util.ArrayList;
import java.util.List;

import hiapp.modules.dmsetting.beanOld.DMBusiness;
import hiapp.modules.dmsetting.data.DmBizRepository;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



@RestController
public class BuinessController {
	@Autowired
	private DmBizRepository dmBizRepository;
	
	//新增业务
	@RequestMapping(value = "/srv/buinfo/dmCreateBiz.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmCreateBiz(@RequestParam("id") String id,
			@RequestParam("name") String name,
			@RequestParam("description") String description,
			@RequestParam("ownerGroupId") String ownerGroupId,
			@RequestParam("modeId") String modeId,
			@RequestParam("subModeId") String subModeId) {
		ServiceResult serviceresult = new ServiceResult();		
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = dmBizRepository.newDMBusiness(id, name,description, ownerGroupId,modeId,subModeId, errMessage);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage(errMessage.toString());
			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceresult.toJson();
	}
	
	//获取全部业务
	@RequestMapping(value = "/srv/buinfo/dmGetAllBiz.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBiz() {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			List<DMBusiness> listDMBusiness = new ArrayList<DMBusiness>();
			if (!dmBizRepository.getAllDMBusiness(listDMBusiness)) {
				return null;
			}
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDMBusiness.size());
			recordsetResult.setPageSize(listDMBusiness.size());
			recordsetResult.setRows(listDMBusiness);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	
	//修改业务
	@RequestMapping(value = "/srv/buinfo/dmModifyBiz.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBiz(@RequestParam("DMBId") String szDMBId,
			@RequestParam("DMBName") String szDMBName,
			@RequestParam("DMBDescription") String szDMBDescription,
			@RequestParam("OwnerGroupId") String szOwnerGroupId) {
		ServiceResult serviceresult = new ServiceResult();
		if (szDMBId == "")
			szDMBId = "0";
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = dmBizRepository
					.modifyDMBusiness(szDMBId, szDMBName,
							szDMBDescription, szOwnerGroupId, errMessage);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage(errMessage.toString());
			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceresult.toJson();
	}
	
	//删除业务
	@RequestMapping(value = "/srv/buinfo/dmDeleteBiz.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmDeleteBiz(@RequestParam("id") String id) {
		ServiceResult serviceresult = new ServiceResult();
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = dmBizRepository.destroyDMBusiness(Integer.parseInt(id),errMessage);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage(errMessage.toString());

			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("删除业务成功！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceresult.toJson();
	}
	
	//批量删除业务
	@RequestMapping(value = "/srv/buinfo/dmDeleteBizBatch.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmDeleteBizBatch(@RequestParam("ids") String szDMBIds) {
		List<String> listDMBId = new ArrayList<String>() ;
		ServiceResult serviceresult = new ServiceResult();
		JsonParser jsonParser=new JsonParser();
		JsonArray jsonArray=jsonParser.parse(szDMBIds).getAsJsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObject = (JsonObject) jsonArray.get(i);
			String bizId = jsonObject.get("id").getAsString();
			listDMBId.add(bizId);
		}
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = dmBizRepository.destroyDMBusinessBatch(listDMBId,errMessage);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage(errMessage.toString());
			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("删除业务成功！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceresult.toJson();
	}
	
	//获取全部外呼模式信息
	@RequestMapping(value = "/srv/buinfo/dmGetOutboundModels.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetOutboundModels() {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			List<DMBusiness> listDMBusiness = new ArrayList<DMBusiness>();
//			if (!dmBizRepository.getAllDMBusiness(listDMBusiness)) {
//				return null;
//			}
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDMBusiness.size());
			recordsetResult.setPageSize(listDMBusiness.size());
			recordsetResult.setRows(listDMBusiness);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
}
