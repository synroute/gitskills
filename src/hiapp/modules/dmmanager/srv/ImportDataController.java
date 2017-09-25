package hiapp.modules.dmmanager.srv;

import hiapp.modules.dmmanager.bean.Business;
import hiapp.modules.dmmanager.bean.ImportTemplate;
import hiapp.modules.dmmanager.bean.WorkSheet;
import hiapp.modules.dmmanager.bean.WorkSheetColumn;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmsetting.DMWorkSheetTypeEnum;
import hiapp.modules.dmsetting.data.DmWorkSheetRepository;
import hiapp.system.buinfo.Permission;
import hiapp.system.buinfo.RoleInGroupSet;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.data.GroupRepository;
import hiapp.system.buinfo.data.PermissionRepository;
import hiapp.system.buinfo.data.UserRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
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
		RoleInGroupSet roleInGroupSet=userRepository.getRoleInGroupSetByUserId(userId);
		Permission permission = permissionRepository.getPermission(roleInGroupSet);
		int permissionId = permission.getId();
		List<Business> busList=dataImportJdbc.getBusinessData(permissionId);
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
	@SuppressWarnings("unused")
	@RequestMapping(value="/srv/ImportDataController/getAllTemplateColums.srv")
	public void getAllTemplateColums (HttpServletRequest request, HttpServletResponse response){
		Integer temPlateId=Integer.valueOf(request.getParameter("temPlateId"));
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String workSheetId=dataImportJdbc.getWookSeetId(bizId);
		//String workSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());
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
	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	@RequestMapping(value="/srv/ImportDataController/ImportExcelCustomerData.srv")
	public void ImportExceCustomerData(HttpServletRequest request, HttpServletResponse response,@RequestParam("file") MultipartFile file) throws IOException{
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		Integer temPlateId=Integer.valueOf(request.getParameter("temPlateId"));
		String workSheetId=dataImportJdbc.getWookSeetId(bizId);
		//String workSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());
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
			int totalRowNum = sheet.getLastRowNum()+1; //总行数
			int coloumNum=sheet.getRow(0).getPhysicalNumberOfCells();//总列数
			//获取前台要展示的字段
			List<WorkSheetColumn> sheetColumnList=dataImportJdbc.getWorkSeetColumnList(workSheetId);
			Map<String,Object> excelMap=dataImportJdbc.getExcelData(temPlateId, bizId);
			Map<String,String> map1=(Map<String, String>) excelMap.get("exMap");
			Map<String,Integer> intMap=new HashMap<String, Integer>();
			List<String>  exList=(List<String>) excelMap.get("exList");
			List<String>  newList=new ArrayList<String>(new HashSet(exList));
			for (int i = 0; i < newList.size(); i++) {
				for (int j = 0; j <=coloumNum; j++) {
					String cellVlue=null;
	            	if(sheet.getRow(0).getCell(j)!=null){
	            		String value=getStringcell(sheet.getRow(0).getCell(j));
	            		if(value!=null){
	            			cellVlue=value;
	            		}else{
	            			sheet.getRow(0).getCell(j).setCellType(Cell.CELL_TYPE_STRING);
			            	cellVlue=sheet.getRow(0).getCell(j).getRichStringCellValue().toString();
	            		}
	            		
	            		
	               }
					if(newList.get(i).equals(cellVlue)){
						intMap.put(newList.get(i),j);
					}
				}
			}
			for (int i = 1; i < totalRowNum; i++) {
				 //获取当前行的数据
	            Row row = sheet.getRow(i);
	            Map<String,Object> map=new HashMap<String, Object>();
	            for(int j=0;j<sheetColumnList.size();j++){
	            	String column=sheetColumnList.get(j).getField().toUpperCase();
	            	if(map1.keySet().contains(column)){
	            		String value=null;
	            		Integer columnValue=intMap.get(map1.get(column));
	            		if(columnValue==null){
	            			continue;
	            		}
	            		if(row.getCell(intMap.get(map1.get(column)))!=null){
	            			String value1=getStringcell(row.getCell(intMap.get(map1.get(column))));
		            		if(value1!=null){
		            			value=value1;
		            		}else{
		            			row.getCell(intMap.get(map1.get(column))).setCellType(Cell.CELL_TYPE_STRING);
		            			value=row.getCell(intMap.get(map1.get(column))).getRichStringCellValue().toString();
		            		}
	            		
	            		}
	            		map.put(sheetColumnList.get(j).getField(),value);
	            	}
	         	
		            }
		            dataList.add(map);
				}
			Map<String,Object> resultMap=dataImportJdbc.createTepporaryImportTable(sheetColumnList, dataList, bizId);
			String jsonObject=new Gson().toJson(resultMap);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获取中间表数据
	 */
	@RequestMapping(value="/srv/ImportDataController/getImportExcelData.srv")
	public  void getImportExcelData(HttpServletRequest request, HttpServletResponse response){
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String workSheetId=dataImportJdbc.getWookSeetId(bizId);
		Integer pageNum=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		//String workSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());
		//获取前台要展示的字段
		List<WorkSheetColumn> sheetColumnList=dataImportJdbc.getWorkSeetColumnList(workSheetId);
		Map<String,Object> resultMap = dataImportJdbc.getImportExcelData(bizId, sheetColumnList, pageNum,pageSize);
		String jsonObject=new Gson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
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
		Integer pageNum=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		String workSheetId=dataImportJdbc.getWookSeetId(bizId);
		//String workSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());
		List<Map<String,Object>> allDataList=new ArrayList<Map<String,Object>>();
		//获取要展示的列
		List<WorkSheetColumn> sheetColumnList=dataImportJdbc.getWorkSeetColumnList(workSheetId);
		Map<String,Object> resultMap=dataImportJdbc.getDbData(temPlateId, bizId,pageNum,pageSize);
		List<Map<String,Object>> dataList=(List<Map<String, Object>>) resultMap.get("rows");
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
		resultMap.put("rows",allDataList);
		String jsonObject=new Gson().toJson(resultMap);
		PrintWriter printWriter = response.getWriter();
		printWriter.print(jsonObject);
		
	}
	/**
	 * 将数据保存到导入表中
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/srv/ImportDataController/saveCustomerDataToDB.srv")
	public void saveCustomerDataToDB(HttpServletRequest request, HttpServletResponse response) throws IOException{
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		String importData=request.getParameter("importData");
		Integer temPlateId=Integer.valueOf(request.getParameter("temPlateId"));
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String workSheetId=dataImportJdbc.getWookSeetId(bizId);
		//String workSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());
		String operationName=request.getParameter("operationName");
		WorkSheet workSheet=dataImportJdbc.getWorkSheet(workSheetId);
		String tableName=workSheet.getName();
		List<WorkSheetColumn> sheetColumnList=dataImportJdbc.getWorkSeetColumnList(workSheetId);
		List<Map<String,Object>> isnertData=new Gson().fromJson(importData, List.class);
		Map<String,Object> resultMap = dataImportJdbc.insertImportData(temPlateId, bizId,workSheetId, sheetColumnList, isnertData, tableName, userId,operationName);
		String jsonObject=new Gson().toJson(resultMap);
		PrintWriter printWriter = response.getWriter();
		printWriter.print(jsonObject);
		 
	};

	public String getStringcell(Cell cell){
		String value=null;
		if("yyyy/mm/dd".equals(cell.getCellStyle().getDataFormatString()) || "m/d/yy".equals(cell.getCellStyle().getDataFormatString())
		        || "yy/m/d".equals(cell.getCellStyle().getDataFormatString()) || "mm/dd/yy".equals(cell.getCellStyle().getDataFormatString())
		        || "dd-mmm-yy".equals(cell.getCellStyle().getDataFormatString())|| "yyyy/m/d".equals(cell.getCellStyle().getDataFormatString())){
			value= new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue());
		}
		return value;
	}
}
