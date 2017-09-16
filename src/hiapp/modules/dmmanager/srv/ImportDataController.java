package hiapp.modules.dmmanager.srv;

import hiapp.modules.dmmanager.bean.Business;
import hiapp.modules.dmmanager.bean.ImportQueryCondition;
import hiapp.modules.dmmanager.bean.ImportTemplate;
import hiapp.modules.dmmanager.bean.WorkSheet;
import hiapp.modules.dmmanager.bean.WorkSheetColumn;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.system.buinfo.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;











import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@Controller
public class ImportDataController {
	@Autowired
	private DataImportJdbc dataImportJdbc;
	/**
	 * 获取所有业务
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping(value="/srv/ImportDataController/GetAllBusiness.srv")
	public void  getAllBusiness(HttpServletRequest request, HttpServletResponse response) throws IOException{
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		List<Business> busList=dataImportJdbc.getBusinessData(userId);
		String gson=new Gson().toJson(busList);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(gson);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	} 
	/**
	 * 获取导入模板
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/ImportDataController/getImportCustomerTemplates.srv")
	public void getImportCustomerTemplates(HttpServletRequest request, HttpServletResponse response){
			Integer businessId=Integer.valueOf(request.getParameter("businessId"));
			List<ImportTemplate> temPlateList = dataImportJdbc.getAllTemplates(businessId);
			String gson=new Gson().toJson(temPlateList);
			try {
				PrintWriter printWriter = response.getWriter();
				printWriter.print(gson);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	/**
	 * 获取前台要展示的列
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/srv/ImportDataController/getAllTemplateColums.srv")
	public void getAllTemplateColums (HttpServletRequest request, HttpServletResponse response){
		Integer temPlateId=Integer.valueOf(request.getParameter("temPlateId"));
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String workSheetId=dataImportJdbc.getWookSeetId(bizId);
		WorkSheet workSheet=dataImportJdbc.getWorkSheet(workSheetId);
		List<WorkSheetColumn> sheetColumnList=dataImportJdbc.getWorkSeetColumnList(workSheetId);
		String jsonObject=new Gson().toJson(sheetColumnList);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 导入EXCEL数据
	 * @param request
	 * @param response
	 * @param uploadExcel
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/srv/ImportDataController/ImportExcelCustomerData.srv")
	public void ImportExceCustomerData(HttpServletRequest request, HttpServletResponse response,@RequestParam("file") MultipartFile file) throws IOException{
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String workSheetId=dataImportJdbc.getWookSeetId(bizId);
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
        String fileName=file.getOriginalFilename();
       try {
			InputStream in=file.getInputStream();
			Workbook wookbook =null;
			String suffix=fileName.substring(fileName.indexOf("."));//获取后缀名
			if(".xls".equals(suffix)){ 
				wookbook = new HSSFWorkbook(in);
			}else if(".xlsx".equals(suffix)){
				wookbook = new XSSFWorkbook(in);
			}
			Sheet sheet = wookbook.getSheetAt(0);
			int totalRowNum = sheet.getLastRowNum(); 
			//获取前台要展示的字段
			List<WorkSheetColumn> sheetColumnList=dataImportJdbc.getWorkSeetColumnList(workSheetId);
			for (int i = 0; i <= totalRowNum; i++) {
				 //获取当前行的数据
	            Row row = sheet.getRow(i);
	            Map<String,Object> map=new HashMap<String, Object>();
	            for(int j=0;j<sheetColumnList.size();j++){
		            	//获取当前单元格的数据
	            	String value=null;
	            	if(row.getCell(j)!=null){
	                    row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
	                	value=row.getCell(j).getRichStringCellValue().toString();
	
	               }
	            	if(value==null||"".equals(value)){
	            		map.put(sheetColumnList.get(j).getField(),"");
	            	}else{
		            	map.put(sheetColumnList.get(j).getField(), value);
		            }
	         	
		            }
		            dataList.add(map);
				}
				
			String jsonObject=new Gson().toJson(dataList);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获取数据源的数据
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/srv/ImportDataController/ImportDBCustomerData.srv")
	public void ImportDBCustomerData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Integer temPlateId=Integer.valueOf(request.getParameter("temPlateId"));
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String workSheetId=dataImportJdbc.getWookSeetId(bizId);
		List<Map<String,Object>> allDataList=new ArrayList<Map<String,Object>>();
		//获取要展示的列
		List<WorkSheetColumn> sheetColumnList=dataImportJdbc.getWorkSeetColumnList(workSheetId);
		List<Map<String,Object>> dataList=dataImportJdbc.getDbData(temPlateId, bizId);
		for (int i = 0; i < dataList.size(); i++) {
			Map<String,Object> map=new HashMap<String, Object>();
			for (int j = 0; j < sheetColumnList.size(); j++) {
				if(dataList.get(i).keySet().contains(sheetColumnList.get(j).getField())){
					map.put(sheetColumnList.get(j).getField(),dataList.get(i).get(sheetColumnList.get(j).getField()));
				}else{
					map.put(sheetColumnList.get(j).getField(),"");
				}
			}
			allDataList.add(map);
		}
		String jsonObject=new Gson().toJson(allDataList);
		PrintWriter printWriter = response.getWriter();
		printWriter.print(jsonObject);
		
	}
	/**
	 * 将数据保存到导入表中
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public String saveCustomerDataToDB(HttpServletRequest request, HttpServletResponse response,ImportQueryCondition queryCondition) throws IOException{
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		Integer temPlateId=Integer.valueOf(request.getParameter("temPlateId"));
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String workSheetId=dataImportJdbc.getWookSeetId(bizId);
		String operationName=request.getParameter("operationName");
		WorkSheet workSheet=dataImportJdbc.getWorkSheet(workSheetId);
		String tableName=workSheet.getName();
		List<WorkSheetColumn> sheetColumnList=dataImportJdbc.getWorkSeetColumnList(workSheetId);
		List<Map<String,Object>> isnertData=queryCondition.getImportData();
		Boolean result = dataImportJdbc.insertImportData(temPlateId, bizId,workSheetId, sheetColumnList, isnertData, tableName, userId,operationName);
		String isSuccess=null;
		if(result){
			isSuccess= "success";
		}else{
			isSuccess=  "fail";
		}
		return isSuccess;
		 
	};
	
}
