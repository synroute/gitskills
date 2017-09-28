package hiapp.modules.dmsetting.srv;

import java.util.ArrayList;
import java.util.List;

import hiapp.modules.dmsetting.DMWorkSheet;
import hiapp.modules.dmsetting.data.DmBizRepository;
import hiapp.modules.dmsetting.data.DmWorkSheetRepository;
import hiapp.system.worksheet.bean.WorkSheetColumn;
import hiapp.system.worksheet.data.WorkSheetRepository;
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
public class WorkSheetController {
	@Autowired
	private DmBizRepository dmBizRepository;
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
	@Autowired
	private WorkSheetRepository workSheetRepository;
	//根据业务ID获取业务下所有工作表
	@RequestMapping(value = "/srv/dm/dmGetAllWorkSheet.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllWorkSheet(@RequestParam("bizId") String bizId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			List<DMWorkSheet> listDMWorkSheet = new ArrayList<DMWorkSheet>();
			if (!dmWorkSheetRepository.getAllDMWorkSheetByBizId(listDMWorkSheet,bizId)) {
				return null;
			}
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDMWorkSheet.size());
			recordsetResult.setPageSize(listDMWorkSheet.size());
			recordsetResult.setRows(listDMWorkSheet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	//获取根据工作表ID获取业务下的工作表列信息
	@RequestMapping(value = "/srv/dm/dmGetAllBizWorkSheetColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBizWorkSheetColumns(@RequestParam("worksheetId") String worksheetId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			List<WorkSheetColumn> listWorkSheetColumn = new ArrayList<WorkSheetColumn>();
			if (!dmWorkSheetRepository.getWorkSheetColumnByWorksheetId(listWorkSheetColumn,worksheetId)) {
				return null;
			}
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listWorkSheetColumn.size());
			recordsetResult.setPageSize(listWorkSheetColumn.size());
			recordsetResult.setRows(listWorkSheetColumn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}

	//修改列中文名
	@RequestMapping(value = "/srv/dm/dmModifyBizWorkSheetColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizWorkSheetColumns(
			@RequestParam("worksheetId") String worksheetId,
			@RequestParam("fixedColumn") String fixedColumn,
			@RequestParam("columnName") String columnName,
			@RequestParam("columnNameCh") String columnNameCh,
			@RequestParam("columnType") String columnType,
			@RequestParam("columnLength") String columnLength,
			@RequestParam("columnDes") String columnDes) {
		ServiceResult serviceresult = new ServiceResult();
		StringBuffer errMessage = new StringBuffer();
		ServiceResultCode serviceResultCode = dmWorkSheetRepository.modifyColumnNameCh(worksheetId,columnName,columnNameCh,columnDes,columnLength,errMessage);
		if (serviceResultCode != ServiceResultCode.SUCCESS) {
			serviceresult.setResultCode(serviceResultCode);
			serviceresult.setReturnMessage(errMessage.toString());
		} else {
			serviceresult.setResultCode(ServiceResultCode.SUCCESS);
			serviceresult.setReturnMessage("成功");
		}
		return serviceresult.toJson();
	}
	
	@RequestMapping(value = "/srv/dm/dmNewBizWorkSheetColumn.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmNewBizWorkSheetColumn(@RequestParam("worksheetId") String worksheetId,
			@RequestParam("columns") String columns) {
		
		String fixedColumn = "";
		String columnName = "";
		String columnNameCh = "";
		String columnType = "";
		String columnLength = "";
		String columnDes = "";
		ServiceResultCode serviceResultCode = ServiceResultCode.SUCCESS;
		StringBuffer errMessage = new StringBuffer();
		ServiceResult serviceresult = new ServiceResult();
		JsonParser jsonParser = new JsonParser();
		JsonArray jsonArray = jsonParser.parse(columns).getAsJsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObject = (JsonObject) jsonArray.get(i);
			
			fixedColumn = jsonObject.get("fixedColumn").getAsString();
			columnName = jsonObject.get("columnName").getAsString();
			columnNameCh = jsonObject.get("columnNameCh").getAsString();
			columnType = jsonObject.get("columnType").getAsString();
			columnLength = jsonObject.get("columnLength").getAsString();
			columnDes = jsonObject.get("columnDes").getAsString();
			serviceResultCode = dmWorkSheetRepository.newColumn(worksheetId,fixedColumn,columnName,columnNameCh,columnType,columnLength,columnDes,errMessage);
		}
		if (serviceResultCode != ServiceResultCode.SUCCESS) {
			serviceresult.setResultCode(serviceResultCode);
			serviceresult.setReturnMessage(errMessage.toString());
		} else {
			serviceresult.setResultCode(ServiceResultCode.SUCCESS);
			serviceresult.setReturnMessage("成功");
		}
		return serviceresult.toJson();
	}
}
