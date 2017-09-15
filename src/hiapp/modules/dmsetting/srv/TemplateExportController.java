package hiapp.modules.dmsetting.srv;

import java.util.ArrayList;
import java.util.List;

import hiapp.modules.dmsetting.DMBizExportTemplate;
import hiapp.modules.dmsetting.data.TemplateExportRepository;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateExportController {
	@Autowired
	private TemplateExportRepository templateExportRepository;
	//添加导出模板
	@RequestMapping(value = "/srv/dmsetting/dmCreateBizExportTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmCreateBizExportTemplate(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId,
			@RequestParam("name") String name,
			@RequestParam("description") String description,
			@RequestParam("isDefault") String isDefault) {
		ServiceResult serviceresult = new ServiceResult();		
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = templateExportRepository.newExportTemplate(bizId, templateId,name, description,isDefault, errMessage);
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
	//根据模板ID删除导出模板
	@RequestMapping(value = "/srv/dmsetting/dmDeleteBizExportTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmDeleteBizExportTemplate(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId) {
		ServiceResult serviceresult = new ServiceResult();	
		try {
			if (!templateExportRepository.deleteExportTemplate(bizId, templateId)) {
				serviceresult.setReturnMessage("删除失败！");
			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("删除成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceresult.toJson();
	}
	//获取所有导出模板
	@RequestMapping(value = "/srv/dmsetting/dmGetAllBizExportTemplates.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBizExportTemplates(@RequestParam("bizId") String bizId) {
		RecordsetResult recordsetResult=new RecordsetResult();
		List<DMBizExportTemplate> listDMBizExportTemplate=new ArrayList<DMBizExportTemplate>();
		try {
			templateExportRepository.getAllExportTemplateByBizId(bizId,listDMBizExportTemplate);
			recordsetResult.setPage(0);	
			recordsetResult.setTotal(listDMBizExportTemplate.size());
			recordsetResult.setPageSize(listDMBizExportTemplate.size());
			recordsetResult.setRows(listDMBizExportTemplate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	//修改导出模板信息
	@RequestMapping(value = "/srv/dmsetting/dmModifyBizExportTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizExportTemplate(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId,
			@RequestParam("name") String name,
			@RequestParam("description") String description,
			@RequestParam("isDefault") String isDefault) {
		ServiceResult serviceresult = new ServiceResult();	
		
		StringBuffer errMessage = new StringBuffer();
		ServiceResultCode serviceResultCode = templateExportRepository.modifyExportTemplate(bizId, templateId,name,description,isDefault,errMessage);
		if (serviceResultCode != ServiceResultCode.SUCCESS) {
			serviceresult.setResultCode(serviceResultCode);
			serviceresult.setReturnMessage(errMessage.toString());
		} else {
			serviceresult.setResultCode(ServiceResultCode.SUCCESS);
			serviceresult.setReturnMessage("添加成功");
		}
		return serviceresult.toJson();
	}
	//获取单个导出模板配置信息
	@RequestMapping(value = "/srv/dmsetting/dmGetBizExportMapColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizExportMapColumns(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId) {
		String exportJson = "";	
		exportJson = templateExportRepository.getBizExportMapColumn(bizId,templateId);
		return exportJson;
	}
	//修改单个导出模板配置信息
	@RequestMapping(value = "/srv/dmsetting/dmModifyBizExportMapColumus.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizExportMapColumus(@RequestParam("bizId") String bizId,
			@RequestParam("templateId") String templateId,
			@RequestParam("mapColumns") String mapColumns) {
		ServiceResult serviceresult = new ServiceResult();	
		try {
			if (!templateExportRepository.modifyExportMapColumns(bizId, templateId,mapColumns)) {
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
