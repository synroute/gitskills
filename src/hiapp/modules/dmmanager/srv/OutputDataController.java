package hiapp.modules.dmmanager.srv;



import hiapp.modules.dmmanager.bean.ExcelUtils;
import hiapp.modules.dmmanager.bean.OutputFirstRow;
import hiapp.modules.dmmanager.data.DataOutputJdbc;
import hiapp.modules.dmsetting.DMBizExportTemplate;

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
public class OutputDataController{
	@Autowired
	private DataOutputJdbc dataOutputJdbc;
	/**
	 * 获取所有导出模板
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/srv/DataShareController/getOutputTemplates.srv")
	public void getOutputTemplates(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		List<DMBizExportTemplate> templateList = dataOutputJdbc.getOutputTemplates(bizId);
		String jsonObject=new Gson().toJson(templateList);
		PrintWriter printWriter = response.getWriter();
		printWriter.print(jsonObject);
	}
	/**
	 * 获取导出数据的列
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/srv/ImportDataController/getOutDataColumns.srv")
	public void getOutDataColumns(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Integer templateId=Integer.valueOf(request.getParameter("templateId"));
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		List<OutputFirstRow> columnList = dataOutputJdbc.getOutDataColumns(bizId,templateId);
		String jsonObject=new Gson().toJson(columnList);
		PrintWriter printWriter = response.getWriter();
		printWriter.print(jsonObject);
	}
	/**
	 * 获得要导出的数据集合
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/srv/DataShareController/getOutputDataByTime.srv")
	public void getOutputDataByTime(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		Integer ifDial=Integer.valueOf(request.getParameter("ifDial"));
		Integer templateId=Integer.valueOf(request.getParameter("templateId"));
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		Integer pageNum=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultMap = dataOutputJdbc.getOutputDataByTime(startTime, endTime, templateId,bizId,ifDial,pageNum,pageSize);
		String jsonObject=new Gson().toJson(resultMap);
		PrintWriter printWriter = response.getWriter();
		printWriter.print(jsonObject);
	}
	/**
	 * 导出EXCEL
	 * @param request
	 * @param response
	 * @param queryCondition
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/srv/DataShareController/GetOutputExcelData.srv")
	public void getOutputExcelCustomerData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Integer templateId=Integer.valueOf(request.getParameter("templateId"));
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String importData=request.getParameter("importData");
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		Integer ifDial=Integer.valueOf(request.getParameter("ifDial"));
		List<Map<String,Object>> dataList=null;
		if(importData!=null&&!"".equals(importData)){
			dataList=new Gson().fromJson(importData, List.class);
		}else{
			dataList=dataOutputJdbc.getOutputDataByTime(startTime, endTime, templateId, bizId,ifDial);
		}
	
		List<OutputFirstRow> columnList = dataOutputJdbc.getOutDataColumns(bizId,templateId);
		List<String> excelHeader =new ArrayList<String>();
		List<String> sheetCulomn =new ArrayList<String>();
		for (int i = 0; i < columnList.size(); i++) {
			excelHeader.add(columnList.get(i).getExcelHeader());
			sheetCulomn.add(columnList.get(i).getField());
		}
		ExcelUtils excelUtils=new ExcelUtils();
		excelUtils.exportExcel(excelHeader, dataList, sheetCulomn, request, response);
	}
}
