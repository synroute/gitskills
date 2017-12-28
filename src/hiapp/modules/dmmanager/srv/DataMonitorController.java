package hiapp.modules.dmmanager.srv;

import hiapp.modules.dmmanager.bean.ExcelUtils;
import hiapp.modules.dmmanager.bean.OutputFirstRow;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmmanager.data.DataMonitorJdbc;
import hiapp.modules.dmsetting.DMWorkSheetTypeEnum;
import hiapp.modules.dmsetting.data.DmWorkSheetRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

@Controller
public class DataMonitorController {
	@Autowired
	private DataMonitorJdbc dataMonitorJdbc;
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
	@Autowired
	private DataImportJdbc dataImportJdbc;
	
	@RequestMapping(value="/srv/DataMonitorController/getMonitorData.srv")
	public void getMonitorData(HttpServletRequest request, HttpServletResponse response){
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		String importId=request.getParameter("importId");
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		Integer pageNum=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultMap = dataMonitorJdbc.getMonitorData(bizId, startTime, endTime, importId, pageNum, pageSize);
		String jsonObject=new Gson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="/srv/DataMonitorController/exportData.srv")
	@SuppressWarnings("unchecked")
	public void exportData(HttpServletRequest request, HttpServletResponse response){
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		String importId=request.getParameter("importId");
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String importData=request.getParameter("importData");
		List<Map<String,Object>> dataList=null;
		if(importData!=null&&!"".equals(importData)){
			dataList=new Gson().fromJson(importData, List.class);
		}else{
			dataList=dataMonitorJdbc.getExportData(bizId, startTime, endTime, importId);
		}
		List<String> excelHeader =new ArrayList<String>();
		List<String> sheetCulomn =new ArrayList<String>();
		excelHeader.add("导入批次号");
		excelHeader.add("数据总条数");
		excelHeader.add("数据源池数量");
		excelHeader.add("中间池数量");
		excelHeader.add("坐席池数量");
		sheetCulomn.add("importId");
		sheetCulomn.add("totalNum");
		sheetCulomn.add("sourceNum");
		sheetCulomn.add("midNum");
		sheetCulomn.add("zxNum");
		ExcelUtils excelUtils=new ExcelUtils();
		excelUtils.exportExcel(excelHeader, dataList, sheetCulomn, request, response);
	}
	/**
	 * 根据业务id获取要展示的列
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/DataMonitorController/getAllColumn.srv")
	public  void getAllColumn(HttpServletRequest request, HttpServletResponse response){
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String importWorkSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());
		String resultWorkSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_RESULT.getType());
		List<OutputFirstRow>  resultList=dataMonitorJdbc.getAllSheetColumn(importWorkSheetId, resultWorkSheetId);
		List<OutputFirstRow> resultList1=new ArrayList<OutputFirstRow>();
		for (int i = 0; i < resultList.size(); i++) {
			String field=resultList.get(i).getField().toUpperCase();
			String workSheetId=resultList.get(i).getWorkSheetId();
			if(workSheetId.equals(importWorkSheetId)){
				if(!"MODIFYLAST".equals(field)&&!"ID".equals(field)&&!"MODIFYID".equals(field)&&!"MODIFYUSERID".equals(field)){
					resultList1.add(resultList.get(i));
				}
			}else{
				if(!"MODIFYLAST".equals(field)&&!"ID".equals(field)&&!"MODIFYID".equals(field)&&!"MODIFYUSERID".equals(field)&&
						!"SOURCEID".equals(field)&&!"IID".equals(field)&&!"CID".equals(field)&&!"MODIFYTIME".equals(field)){
					resultList1.add(resultList.get(i));
				}
			}
		}
		String jsonObject=new Gson().toJson(resultList1);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 根据时间查询数据
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/srv/DataMonitorController/getAllDataByTime.srv")
	public void getAllDataByTime(HttpServletRequest request, HttpServletResponse response){
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String jsonData=request.getParameter("jsonData");
		Integer ifDial=Integer.valueOf(request.getParameter("ifDial"));
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		Integer pageNum=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		String importWorkSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());
		String resultWorkSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_RESULT.getType());
		List<Map<String,Object>> titleList = new Gson().fromJson(jsonData, List.class);
		Map<String, Object> resultMap = dataMonitorJdbc.getAllDataByTime(importWorkSheetId, resultWorkSheetId, startTime, endTime, titleList, ifDial, bizId, pageNum, pageSize);
		String jsonObject=new Gson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
