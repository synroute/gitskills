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

import hiapp.modules.dmsetting.DMBizExportTemplate;
import hiapp.modules.dmsetting.data.DMBizRecoveryRepository;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
@RestController
public class TemplateRecoveryController {
	@Autowired
	private DMBizRecoveryRepository templateRecoveryRepository;
	//添加分配模板
	@RequestMapping(value = "/srv/dm/dmCreateBizRecoveryTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmCreateBizRecoveryTemplate(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId,
			@RequestParam("templateName") String name,
			@RequestParam("description") String description,
			@RequestParam("isDefault") String isDefault) {
		ServiceResult serviceresult = new ServiceResult();		
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = templateRecoveryRepository.newRecoveryTemplate(bizId, templateId,name, description,isDefault, errMessage);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage(errMessage.toString());
			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("添加成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceresult.toJson();
	}
	//根据模板ID删除分配模板
	@RequestMapping(value = "/srv/dm/dmDeleteBizRecoveryTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmDeleteBizRecoveryTemplate(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId) {
		String tId = "";
		ServiceResult serviceresult = new ServiceResult();
		JsonParser jsonParser = new JsonParser();
		JsonArray jsonArray = jsonParser.parse(templateId).getAsJsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObject = (JsonObject) jsonArray.get(i);
			tId = jsonObject.get("templateId").getAsString();
			try {
				if (!templateRecoveryRepository.deleteRecoveryTemplate(bizId, tId)) {
					serviceresult.setReturnMessage("删除失败！");
				} else {
					serviceresult.setResultCode(ServiceResultCode.SUCCESS);
					serviceresult.setReturnMessage("删除成功");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return serviceresult.toJson();
	}
	//获取所有分配模板
	@RequestMapping(value = "/srv/dm/dmGetAllBizRecoveryTemplates.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBizRecoveryTemplates(@RequestParam("bizId") String bizId) {
		RecordsetResult recordsetResult=new RecordsetResult();
		List<DMBizExportTemplate> listDMBizRecoveryTemplate=new ArrayList<DMBizExportTemplate>();
		try {
			templateRecoveryRepository.getAllRecoveryTemplateByBizId(bizId,listDMBizRecoveryTemplate);
			recordsetResult.setPage(0);	
			recordsetResult.setTotal(listDMBizRecoveryTemplate.size());
			recordsetResult.setPageSize(listDMBizRecoveryTemplate.size());
			recordsetResult.setRows(listDMBizRecoveryTemplate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	//修改分配模板信息
	@RequestMapping(value = "/srv/dm/dmModifyBizRecoveryTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizRecoveryTemplate(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId,
			@RequestParam("templateName") String name,
			@RequestParam("description") String description,
			@RequestParam("isDefault") String isDefault) {
		ServiceResult serviceresult = new ServiceResult();	
		
		StringBuffer errMessage = new StringBuffer();
		ServiceResultCode serviceResultCode = templateRecoveryRepository.modifyRecoveryTemplate(bizId, templateId,name,description,isDefault,errMessage);
		if (serviceResultCode != ServiceResultCode.SUCCESS) {
			serviceresult.setResultCode(serviceResultCode);
			serviceresult.setReturnMessage(errMessage.toString());
		} else {
			serviceresult.setResultCode(ServiceResultCode.SUCCESS);
			serviceresult.setReturnMessage("添加成功");
		}
		return serviceresult.toJson();
	}
	
	//获取单个分配模板配置信息
	@RequestMapping(value = "/srv/dm/dmGetBizRecoveryMapColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizRecoveryMapColumns(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId) {
		ServiceResult serviceresult = new ServiceResult();
		String RecoveryJson = "";	
		RecoveryJson = templateRecoveryRepository.getBizRecoveryMapColumn(bizId,templateId);	
		
		serviceresult.setReturnCode(0);
		serviceresult.setReturnMessage(RecoveryJson);
		return serviceresult.toJson();
	}
	
	//修改单个分配模板配置信息
	@RequestMapping(value = "/srv/dm/dmModifyBizRecoveryMapColumus.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizRecoveryMapColumus(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId,
			@RequestParam("mapColumns") String mapColumns) {
		ServiceResult serviceresult = new ServiceResult();	
		try {
			if (!templateRecoveryRepository.modifyRecoveryMapColumns(bizId, templateId,mapColumns)) {
				serviceresult.setReturnMessage("修改失败！");
			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("修改成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceresult.toJson();
	}
}
