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
import hiapp.modules.dmsetting.data.DMBizDistributiontRepository;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
@RestController
public class TemplateDistributiontController {
	@Autowired
	private DMBizDistributiontRepository templateDistributiontRepository;
	//添加分配模板
	@RequestMapping(value = "/srv/dm/dmCreateBizDistributiontTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmCreateBizDistributiontTemplate(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId,
			@RequestParam("templateName") String name,
			@RequestParam("description") String description,
			@RequestParam("isDefault") String isDefault) {
		ServiceResult serviceresult = new ServiceResult();		
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = templateDistributiontRepository.newDistributiontTemplate(bizId, templateId,name, description,isDefault, errMessage);
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
	@RequestMapping(value = "/srv/dm/dmDeleteBizDistributiontTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmDeleteBizDistributiontTemplate(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId) {
		String tId = "";
		ServiceResult serviceresult = new ServiceResult();
		JsonParser jsonParser = new JsonParser();
		JsonArray jsonArray = jsonParser.parse(templateId).getAsJsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObject = (JsonObject) jsonArray.get(i);
			tId = jsonObject.get("templateId").getAsString();
			try {
				if (!templateDistributiontRepository.deleteDistributiontTemplate(bizId, tId)) {
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
	@RequestMapping(value = "/srv/dm/dmGetAllBizDistributiontTemplates.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBizDistributiontTemplates(@RequestParam("bizId") String bizId) {
		RecordsetResult recordsetResult=new RecordsetResult();
		List<DMBizExportTemplate> listDMBizDistributiontTemplate=new ArrayList<DMBizExportTemplate>();
		try {
			templateDistributiontRepository.getAllDistributiontTemplateByBizId(bizId,listDMBizDistributiontTemplate);
			recordsetResult.setPage(0);	
			recordsetResult.setTotal(listDMBizDistributiontTemplate.size());
			recordsetResult.setPageSize(listDMBizDistributiontTemplate.size());
			recordsetResult.setRows(listDMBizDistributiontTemplate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	//修改分配模板信息
	@RequestMapping(value = "/srv/dm/dmModifyBizDistributiontTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizDistributiontTemplate(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId,
			@RequestParam("templateName") String name,
			@RequestParam("description") String description,
			@RequestParam("isDefault") String isDefault) {
		ServiceResult serviceresult = new ServiceResult();	
		
		StringBuffer errMessage = new StringBuffer();
		ServiceResultCode serviceResultCode = templateDistributiontRepository.modifyDistributiontTemplate(bizId, templateId,name,description,isDefault,errMessage);
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
	@RequestMapping(value = "/srv/dm/dmGetBizDistributiontMapColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizDistributiontMapColumns(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId) {
		ServiceResult serviceresult = new ServiceResult();
		String DistributiontJson = "";	
		DistributiontJson = templateDistributiontRepository.getBizDistributiontMapColumn(bizId,templateId);	
		
		serviceresult.setReturnCode(0);
		serviceresult.setReturnMessage(DistributiontJson);
		return serviceresult.toJson();
	}
	
	//修改单个分配模板配置信息
	@RequestMapping(value = "/srv/dm/dmModifyBizDistributiontMapColumus.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizDistributiontMapColumus(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId,
			@RequestParam("mapColumns") String mapColumns) {
		ServiceResult serviceresult = new ServiceResult();	
		try {
			if (!templateDistributiontRepository.modifyDistributiontMapColumns(bizId, templateId,mapColumns)) {
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
