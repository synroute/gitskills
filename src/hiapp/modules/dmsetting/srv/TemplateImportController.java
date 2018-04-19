package hiapp.modules.dmsetting.srv;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import hiapp.modules.dmsetting.DMBizImportTemplate;
import hiapp.modules.dmsetting.data.DmBizTemplateImportRepository;
import hiapp.modules.dmsetting.result.DMBizTemplateExcelColums;
import hiapp.modules.dmsetting.result.DMBizTemplateImportTableColumns;
import hiapp.modules.dmsetting.result.DMBizTemplateImportTableName;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

@RestController
public class TemplateImportController {
	@Autowired
	private DmBizTemplateImportRepository dmBizTemplateImport;
	

	@RequestMapping(value = "srv/dm/dmGetAllBizImportTemplates.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBizImportTemplates(@RequestParam("bizId") int bizId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
			List<DMBizImportTemplate> listDmBizImportTemplate=new ArrayList<DMBizImportTemplate>();
			listDmBizImportTemplate=dmBizTemplateImport.getAllTemplates(bizId);
				recordsetResult.setPage(0);
				recordsetResult.setTotal(listDmBizImportTemplate.size());
				recordsetResult.setPageSize(listDmBizImportTemplate.size());
				recordsetResult.setRows(listDmBizImportTemplate);
				
				recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();

	}
	@RequestMapping(value = "srv/dm/dmGetBizImportTemplates.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getTemplates(@RequestParam("bizId") int bizId,@RequestParam("templateId") int templateId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
			List<DMBizImportTemplate> listDmBizImportTemplate=new ArrayList<DMBizImportTemplate>();
			listDmBizImportTemplate=dmBizTemplateImport.getTemplates(bizId,templateId);
				recordsetResult.setPage(0);
				recordsetResult.setTotal(listDmBizImportTemplate.size());
				recordsetResult.setPageSize(listDmBizImportTemplate.size());
				recordsetResult.setRows(listDmBizImportTemplate);
				
				recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();

		
		
	}
	
	
	
	@RequestMapping(value = "srv/dm/dmCreateCustomerImportTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmCreateCustomerImportTemplate(@RequestParam("bizId") int bizId,@RequestParam("templateId") int templateId,
			@RequestParam("templateName") String templateName,@RequestParam("description") String description,
			@RequestParam("isDefault") int isDefault,@RequestParam("sourceType") String sourceType) {
		
		ServiceResult serviceresult = new ServiceResult();
		DMBizImportTemplate dmBizImportTemplate=new DMBizImportTemplate();
		dmBizImportTemplate.setBizId(bizId);
		dmBizImportTemplate.setTemplateId(templateId);
		dmBizImportTemplate.setTemplateName(templateName);
		dmBizImportTemplate.setDesc(description);
		dmBizImportTemplate.setIsDefault(isDefault);
		dmBizImportTemplate.setDataSourceType(sourceType);
		StringBuffer errMessage=new StringBuffer();
		if(dmBizTemplateImport.dmCreateCustomerImportTemplate(dmBizImportTemplate,errMessage))
		{
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("成功");
		}else {
			serviceresult.setReturnCode(1);
			serviceresult.setReturnMessage(errMessage.toString());
		}
		return serviceresult.toJson();
	}

	@RequestMapping(value = "srv/dm/dmModifyCustomerImportTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyCustomerImportTemplate(@RequestParam("bizId") int bizId,@RequestParam("templateId") int templateId,
			@RequestParam("templateName") String templateName,@RequestParam("description") String description,
			@RequestParam("isDefault") int isDefault,@RequestParam("sourceType") String sourceType) {
		
		ServiceResult serviceresult = new ServiceResult();
		DMBizImportTemplate dmBizImportTemplate=new DMBizImportTemplate();
		dmBizImportTemplate.setBizId(bizId);
		dmBizImportTemplate.setTemplateId(templateId);
		dmBizImportTemplate.setTemplateName(templateName);
		dmBizImportTemplate.setDesc(description);
		dmBizImportTemplate.setIsDefault(isDefault);
		dmBizImportTemplate.setDataSourceType(sourceType);
		if(dmBizTemplateImport.dmModifyCustomerImportTemplate(dmBizImportTemplate))
		{
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("成功");
		}else {
			serviceresult.setReturnCode(1);
			serviceresult.setReturnMessage("失败");
		}
		return serviceresult.toJson();
	}

	@RequestMapping(value = "srv/dm/dmDeleteBizImportTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmDeleteBizImportTemplate(@RequestParam("bizId") int bizId,@RequestParam("templateId") int templateId) {
		
		ServiceResult serviceresult = new ServiceResult();
		DMBizImportTemplate dmBizImportTemplate=new DMBizImportTemplate();
		dmBizImportTemplate.setBizId(bizId);
		dmBizImportTemplate.setTemplateId(templateId);
		if(dmBizTemplateImport.dmDeleteBizImportTemplate(dmBizImportTemplate))
		{
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("成功");
		}else {
			serviceresult.setReturnCode(1);
			serviceresult.setReturnMessage("失败");
		}
		return serviceresult.toJson();
	}
	
	@RequestMapping(value = "srv/dm/dmGetBizImportMapColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizImportMapColumns(@RequestParam("bizId") int bizId,@RequestParam("templateId") int templateId) {
		
		ServiceResult serviceresult = new ServiceResult();
		DMBizImportTemplate dmBizImportTemplate=new DMBizImportTemplate();
		dmBizImportTemplate.setBizId(bizId);
		dmBizImportTemplate.setTemplateId(templateId);
		String json= dmBizTemplateImport.dmGetBizImportMapColumns(dmBizImportTemplate);
		
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage(json);
		
		return serviceresult.toJson();
	}

	
	@RequestMapping(value = "srv/dm/dmGetBizImportTableName.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizImportTableName() {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
			List<DMBizTemplateImportTableName> listDmBizImportTableName=new ArrayList<DMBizTemplateImportTableName>();
			listDmBizImportTableName=dmBizTemplateImport.dmGetBizImportTableName();
				recordsetResult.setPage(0);
				recordsetResult.setTotal(listDmBizImportTableName.size());
				recordsetResult.setPageSize(listDmBizImportTableName.size());
				recordsetResult.setRows(listDmBizImportTableName);
				
				recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();

		
		
	}

	@RequestMapping(value = "srv/dm/dmGetBizImportTableColums.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizImportTableColums(@RequestParam("tableName") String tableName) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
			List<DMBizTemplateImportTableColumns> listDmBizImportTableColumns=new ArrayList<DMBizTemplateImportTableColumns>();
			listDmBizImportTableColumns=dmBizTemplateImport.dmGetBizImportTableColums(tableName);
				recordsetResult.setPage(0);
				recordsetResult.setTotal(listDmBizImportTableColumns.size());
				recordsetResult.setPageSize(listDmBizImportTableColumns.size());
				recordsetResult.setRows(listDmBizImportTableColumns);
				
				recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();

		
		
	}

	@RequestMapping(value = "srv/dm/dmModifyBizImportMapColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizImportMapColumns(@RequestParam("bizId") int bizId,@RequestParam("templateId") int templateId,
			@RequestParam("mapColumns") String mapColumns) {
		
		ServiceResult serviceresult = new ServiceResult();
		DMBizImportTemplate dmBizImportTemplate=new DMBizImportTemplate();
		dmBizImportTemplate.setBizId(bizId);
		dmBizImportTemplate.setTemplateId(templateId);
		if(dmBizTemplateImport.dmModifyBizImportMapColumns(dmBizImportTemplate,mapColumns))
		{
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("成功");
		}else {
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("失败");
		}
		return serviceresult.toJson();
	}
	
	@RequestMapping(value = "srv/dm/dmGetBizExcel.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizExcel(@RequestParam("file") MultipartFile file) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
			List<DMBizTemplateExcelColums> listDMBizTemplateExcelColums=new ArrayList<DMBizTemplateExcelColums>();
			listDMBizTemplateExcelColums=dmBizTemplateImport.dmGetBizExcel(file);
				recordsetResult.setPage(0);
				recordsetResult.setTotal(listDMBizTemplateExcelColums.size());
				recordsetResult.setPageSize(listDMBizTemplateExcelColums.size());
				recordsetResult.setRows(listDMBizTemplateExcelColums);
				
				recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();

		
		
	}
	
}
