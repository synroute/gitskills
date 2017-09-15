package hiapp.modules.dmsetting.srv;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

import hiapp.modules.dmsetting.result.*;
import hiapp.modules.dmsetting.data.DmBizAutomaticRepository;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResultCode;
@RestController
public class AutomaticController {
	@Autowired
	private DmBizAutomaticRepository dmBizAutomatic;
	
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
	
	@RequestMapping(value = "/srv/dm/dmGetBizCustomer.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizCustomer(@RequestParam("bizId") int bizId,@RequestParam("Cid") String Cid,@RequestParam("IID") String IID,
			@RequestParam("columns") String columns) {
		RecordsetResult recordsetResult = new RecordsetResult();
		JsonObject jsonObject=new JsonObject();
		try {
			
			
			jsonObject=dmBizAutomatic.dmGetBizCustomer(bizId,Cid,IID,columns);
			recordsetResult.setReturnCode(0);
			recordsetResult.setReturnMessage(jsonObject.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			recordsetResult.setReturnCode(1);
			recordsetResult.setReturnMessage("失败");
		}
		return recordsetResult.toJson();
	}
	
	
	@RequestMapping(value = "/srv/dm/dmGetBizCustomerHis.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizCustomerHis(@RequestParam("bizId") int bizId,@RequestParam("Cid") String Cid,
			@RequestParam("columns") String columns) {
		RecordsetResult recordsetResult = new RecordsetResult();
		JsonObject jsonObject=new JsonObject();
		try {
			jsonObject=dmBizAutomatic.dmGetBizCustomerHis(bizId,Cid,columns);
			recordsetResult.setReturnCode(0);
			recordsetResult.setReturnMessage(jsonObject.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			recordsetResult.setReturnCode(1);
			recordsetResult.setReturnMessage("失败");
		}
		return recordsetResult.toJson();
	}
	
	@RequestMapping(value = "/srv/dm/dmGetAllBizColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBizColumns(@RequestParam("bizId") String bizId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			List<DMBizAutomaticColumns> listDMBizAutomaticColumns = new ArrayList<DMBizAutomaticColumns>();
			if (!dmBizAutomatic.getAllBizColumns(listDMBizAutomaticColumns,bizId)) {
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
	
	
}
