package hiapp.modules.dmsetting.srv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.modules.dmsetting.result.*;
import hiapp.modules.dmsetting.DMBizAutomaticConfig;
import hiapp.modules.dmsetting.DMBizImportTemplate;
import hiapp.modules.dmsetting.data.DmBizAutomaticRepository;
import hiapp.system.buinfo.User;
import hiapp.system.worksheet.bean.WorkSheet;
import hiapp.utils.idfactory.IdFactory;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
@RestController
public class AutomaticController {
	@Autowired
	private DmBizAutomaticRepository dmBizAutomatic;
	@Autowired
	private WorkSheet worksheet;
	@Autowired
	private IdFactory idFactory;
	@RequestMapping(value = "/srv/dm/dmGetBizResultColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizResultColumns(@RequestParam("bizId") String bizId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			
			List<DMBizAutomaticColumns> listDMBizAutomaticColumns = new ArrayList<DMBizAutomaticColumns>();
			if (!dmBizAutomatic.getResultColumns(listDMBizAutomaticColumns,bizId)) {
				return null;
			}
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDMBizAutomaticColumns.size());
			recordsetResult.setPageSize(listDMBizAutomaticColumns.size());
			recordsetResult.setRows(listDMBizAutomaticColumns);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	
	@RequestMapping(value = "/srv/dm/dmGetBizCustomerColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizCustomerColumns(@RequestParam("bizId") int bizId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			
			List<DMBizAutomaticColumns> listDMBizAutomaticColumns = new ArrayList<DMBizAutomaticColumns>();
			listDMBizAutomaticColumns=dmBizAutomatic.dmGetBizCustomerColumns(bizId);
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDMBizAutomaticColumns.size());
			recordsetResult.setPageSize(listDMBizAutomaticColumns.size());
			recordsetResult.setRows(listDMBizAutomaticColumns);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	
	
	@RequestMapping(value = "/srv/dm/dmGetBizCustomerColumnsForPhone.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizCustomerColumnsForPhone(@RequestParam("bizId") int bizId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			
			List<DMBizAutomaticColumns> listDMBizAutomaticColumns = new ArrayList<DMBizAutomaticColumns>();
			listDMBizAutomaticColumns=dmBizAutomatic.dmGetBizCustomerColumnsForPhone(bizId);
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDMBizAutomaticColumns.size());
			recordsetResult.setPageSize(listDMBizAutomaticColumns.size());
			recordsetResult.setRows(listDMBizAutomaticColumns);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	
	
	@RequestMapping(value = "/srv/dm/dmGetBizCustomer.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizCustomer(@RequestParam("bizId") int bizId,@RequestParam("Cid") String Cid,@RequestParam("IID") String IID,
			@RequestParam("columns") String columns) {
		RecordsetResult recordsetResult = new RecordsetResult();
		Map<String, String> map=new HashMap<String, String>(); 
		try {
			
			map=dmBizAutomatic.dmGetBizCustomer(bizId,Cid,IID,columns);
			
			recordsetResult.setReturnCode(0);
			recordsetResult.setReturnMessage("成功");
			recordsetResult.getRows().add(map);
		} catch (Exception e) {
			e.printStackTrace();
			recordsetResult.setReturnCode(1);
			recordsetResult.setReturnMessage("失败");
			recordsetResult.getRows().add(map);
		}
		return recordsetResult.toJson();

	}
	
	@RequestMapping(value = "/srv/dm/dmGetBizResult.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizResult(@RequestParam("bizId") int bizId,@RequestParam("Cid") String Cid,@RequestParam("IID") String IID,
			@RequestParam("SID") String SID,@RequestParam("MODIFYID") String MODIFYID,
			@RequestParam("columns") String columns) {
		RecordsetResult recordsetResult = new RecordsetResult();
		Map<String, String> map=new HashMap<String, String>(); 
		try {
			
			map=dmBizAutomatic.dmGetBizResult(bizId,Cid,IID,SID,MODIFYID,columns);
			
			recordsetResult.setReturnCode(0);
			recordsetResult.setReturnMessage("成功");
			recordsetResult.getRows().add(map);
		} catch (Exception e) {
			e.printStackTrace();
			recordsetResult.setReturnCode(1);
			recordsetResult.setReturnMessage("失败");
			recordsetResult.getRows().add(map);
		}
		return recordsetResult.toJson();

	}
	
	
	@RequestMapping(value = "/srv/dm/dmGetBizCustomerHis.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizCustomerHis(@RequestParam("bizId") int bizId,@RequestParam("Cid") String Cid,
			@RequestParam("columns") String columns) {
		RecordsetResult recordsetResult = new RecordsetResult();
		List<Map<String,String>>  map=new ArrayList<Map<String,String>>(); 
		try {
			
			map=dmBizAutomatic.dmGetBizCustomerHis(bizId,Cid,columns);
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(map.size());
			recordsetResult.setPageSize(map.size());
			recordsetResult.setRows(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	
	@RequestMapping(value = "/srv/dm/dmGetAllBizColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBizColumns(@RequestParam("bizId") String bizId,@RequestParam("type") String type) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			List<DMBizAutomaticColumns> listDMBizAutomaticColumns = dmBizAutomatic.getAllBizColumns(bizId,type);
			
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDMBizAutomaticColumns.size());
			recordsetResult.setPageSize(listDMBizAutomaticColumns.size());
			recordsetResult.setRows(listDMBizAutomaticColumns);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	
	@RequestMapping(value = "srv/dm/dmCreateAutomaticPageUrl.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmCreateAutomaticPageUrl(@RequestParam("bizId") int bizId,@RequestParam("pageName") String pageName,
			@RequestParam("pageUrl") String pageUrl,@RequestParam("pageParameter") String pageParameter,@RequestParam("sourceModular") String sourceModular) {
		
		ServiceResult serviceresult = new ServiceResult();
		if(dmBizAutomatic.dmCreateAutomaticPageUrl(bizId,pageName,pageUrl,pageParameter,sourceModular))
		{
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("成功");
		}else {
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("失败");
		}
		return serviceresult.toJson();
	}
	@RequestMapping(value = "srv/dm/dmGetAutomaticPageUrl.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAutomaticPageUrl(@RequestParam("sourceId") int sourceId,@RequestParam("sourceModular") String sourceModular,@RequestParam("pageName") String pageName) {
		Map<String, String> map=new HashMap<String, String>(); 
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
		map= dmBizAutomatic.dmGetAutomaticPageUrl(sourceId,sourceModular,pageName);
		
		recordsetResult.setReturnCode(0);
		recordsetResult.setReturnMessage("成功");
		recordsetResult.getRows().add(map);
		} catch (Exception e) {
			e.printStackTrace();
			recordsetResult.setReturnCode(1);
			recordsetResult.setReturnMessage("失败");
			recordsetResult.getRows().add(map);
		}
		return recordsetResult.toJson();
	}
	
	
	@RequestMapping(value = "srv/dm/dmGetAutomaticConfig.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAutomaticConfig(@RequestParam("sourceId") int sourceId,@RequestParam("sourceModular") String sourceModular,
			@RequestParam("pageName") String pageName) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			List<DMBizAutomaticConfig> listDMBizAutomaticConfig=new ArrayList<DMBizAutomaticConfig>();
			listDMBizAutomaticConfig= dmBizAutomatic.dmGetAutomaticConfig(sourceId,sourceModular,pageName);
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDMBizAutomaticConfig.size());
			recordsetResult.setPageSize(listDMBizAutomaticConfig.size());
			recordsetResult.setRows(listDMBizAutomaticConfig);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
		
	}
	
	@RequestMapping(value = "srv/dm/dmCreateAutomaticConfig.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmCreateAutomaticConfig(HttpServletRequest request,@RequestParam("mapColumn") String mapColumn) {
		User user = (User)request.getSession(false).getAttribute("user");
		String userid=user.getId();
		ServiceResult serviceresult = new ServiceResult();
		JsonArray jsonArray= new JsonParser().parse(mapColumn).getAsJsonArray();
		String idPrefix = "PG";
		//获取工作表ID
		String pageId = idFactory.newId(idPrefix);
		for(int i =0;i<jsonArray.size();i++)
		{
			JsonObject jsonObject=jsonArray.get(i).getAsJsonObject();
			int sourceId=jsonObject.get("sourceId").getAsInt();
			String sourceModular=jsonObject.get("sourceModular").getAsString();
			String pageName=jsonObject.get("pageName").getAsString();
			String panleName=jsonObject.get("panleName").getAsString();
			JsonArray jsonArray2=new JsonParser().parse(jsonObject.get("column").getAsString()).getAsJsonArray();
			String state=jsonObject.get("state").getAsString();
			String displayType=jsonObject.get("displayType").getAsString();
			int isDelete=jsonObject.get("isDelete").getAsInt();
			
			if(dmBizAutomatic.dmCreateAutomaticConfig(sourceId,sourceModular,pageName,panleName,jsonArray2,state,displayType,userid,isDelete,pageId))
			{
				serviceresult.setReturnCode(0);
				serviceresult.setReturnMessage("成功");
			}else {
				serviceresult.setReturnCode(0);
				serviceresult.setReturnMessage("失败");
			}
		}
		return serviceresult.toJson();
	}
	
	
}
