package hiapp.modules.dmmanager.srv;

import hiapp.modules.dmmanager.bean.ExcelUtils;
import hiapp.modules.dmmanager.bean.MonitorData;
import hiapp.modules.dmmanager.data.DataMonitorJdbc;

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
}
