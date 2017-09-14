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
	@RequestMapping(value = "/srv/dmsetting/dmGetAllWorkSheet.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
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
	@RequestMapping(value = "/srv/dmsetting/dmGetAllBizWorkSheetColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
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
	@RequestMapping(value = "/srv/dmsetting/dmModifyBizWorkSheetColumns.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizWorkSheetColumns(
			@RequestParam("worksheetId") String worksheetId,
			@RequestParam("columnsJson") String columnsJson) {
		ServiceResult serviceresult = new ServiceResult();
		JsonParser jsonParser = new JsonParser();
		JsonArray jsonArray = jsonParser.parse(columnsJson).getAsJsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObject = (JsonObject) jsonArray.get(i);
			String columnName = jsonObject.get("columnName").getAsString();
			String columnNameCh = jsonObject.get("columnNameCh").getAsString();
			if (!dmWorkSheetRepository.modifyColumnNameCh(worksheetId,columnName,columnNameCh)) {
				serviceresult.setReturnMessage("失败");	
			} else {
				serviceresult.setReturnMessage("修改成功");
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
			}
		}
		return serviceresult.toJson();
	}
	
}
