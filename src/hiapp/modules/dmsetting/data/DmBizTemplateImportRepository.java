package hiapp.modules.dmsetting.data;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.modules.dmsetting.DMBizImportTemplate;
import hiapp.modules.dmsetting.result.DMBizTemplateExcelColums;
import hiapp.modules.dmsetting.result.DMBizTemplateImportTableColumns;
import hiapp.modules.dmsetting.result.DMBizTemplateImportTableName;
import hiapp.system.dictionary.data.DictRepository;
import hiapp.system.worksheet.bean.WorkSheet;
import hiapp.system.worksheet.bean.WorkSheetColumn;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.serviceresult.ServiceResultCode;
@Repository
public class DmBizTemplateImportRepository extends BaseRepository {
	Connection dbConn = null;
	
	@Autowired
	 private WorkSheet workSheet;
	//获取所有导入模板接口
	public   List<DMBizImportTemplate> getAllTemplates(int bizId){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<DMBizImportTemplate> listDmBizImportTemplate=new ArrayList<DMBizImportTemplate>();
		try {
			dbConn =this.getDbConnection();
			String szSql = "SELECT TemplateID,Name,Description,IsDefault,SourceType FROM HASYS_DM_BIZTEMPLATEIMPORT WHERE BusinessId=? ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1,bizId);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMBizImportTemplate dmBizImportTemplate=new DMBizImportTemplate();
				dmBizImportTemplate.setBizId(bizId);
				dmBizImportTemplate.setTemplateId(rs.getInt(1));
				dmBizImportTemplate.setTemplateName(rs.getString(2));
				dmBizImportTemplate.setDesc(rs.getString(3));
				dmBizImportTemplate.setIsDefault(rs.getInt(4));
				dmBizImportTemplate.setDataSourceType(rs.getString(5));
				listDmBizImportTemplate.add(dmBizImportTemplate);
			}
					
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		
		return listDmBizImportTemplate;
	}
	//新增客户信息导入模板接口
	public   boolean dmCreateCustomerImportTemplate(DMBizImportTemplate dmBizImportTemplate,StringBuffer errMessage){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn =this.getDbConnection();
			try {
				
				int count = 0;
				String szSql = String.format("select COUNT(*) from HASYS_DM_BIZTEMPLATEIMPORT where BUSINESSID='%s' and TemplateID='%s'",dmBizImportTemplate.getBizId(),dmBizImportTemplate.getTemplateId());
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				if (rs.next()) {
					count = rs.getInt(1);
				}
				if (count > 0) {
					errMessage.append("模板ID冲突！");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				
				int count = 0;
				 String szSql = String.format("select COUNT(*) from HASYS_DM_BIZTEMPLATEIMPORT where BUSINESSID='%s' and NAME='%s'",dmBizImportTemplate.getBizId(),dmBizImportTemplate.getTemplateName());
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				if (rs.next()) {
					count = rs.getInt(1);
				}
				if (count > 0) {
					errMessage.append("模板名称冲突！");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			JsonObject jsonObject =new JsonObject();
			JsonObject jsonObject_row=new JsonObject();
			JsonArray jsonArray_Import=new JsonArray();
			if(dmBizImportTemplate.getDataSourceType().equals("Excel"))
			{
				
				jsonObject_row.addProperty("ExcelDefaultExt", ".xlsx");
				jsonObject_row.addProperty("ExcelColEnd", "26");
				jsonObject_row.addProperty("ExcelRowStart", "2");
				jsonObject_row.addProperty("ExcelColStart", "0");
				jsonObject_row.addProperty("ExcelCsvFastInsertTable", "1");
				jsonObject_row.addProperty("RepetitionExcludeType", "根据导入时间排重");
				jsonObject_row.addProperty("RepetitionExcludeWorkSheetColumn", "id");
				jsonObject_row.addProperty("RepetitionExcludeWorkSheetColumnCh", "编号");
				jsonObject_row.addProperty("RepetitionExcludeDayCount", "2");
				jsonArray_Import.add(jsonObject_row);
				jsonObject.add("ImportExcelTemplate", jsonArray_Import);
				
			}else{
				
				jsonObject_row.addProperty("ImportTableName", "HAU_DM_B"+dmBizImportTemplate.getBizId()+"C_IMPORT");
				jsonObject_row.addProperty("SourceTableName", "");
				jsonObject_row.addProperty("DateTimeFilterField", "ImportDate");
				jsonObject_row.addProperty("ServerAutoImport", "");
				jsonObject_row.addProperty("ServerAutoImportInterval", "");
				jsonObject_row.addProperty("ServerAutoImportFieldLatestSource", "");
				jsonArray_Import.add(jsonObject_row);
				jsonObject.add("ImportExcelTemplate", jsonArray_Import);
			}
			JsonArray jsonArray=new JsonArray();
			
			String szSelectSql="select ID from HASYS_WORKSHEET where NAME='HAU_DM_B"+dmBizImportTemplate.getBizId()+"C_IMPORT'";
			stmt = dbConn.prepareStatement(szSelectSql);
			rs = stmt.executeQuery();
			String workSheetId="";
			while(rs.next()){
				workSheetId=rs.getString(1);
			}
			stmt.close();
			List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
			workSheet.getColumns(dbConn, workSheetId, listColumns);
			
			for (int i = 0; i < listColumns.size(); i++) {
				WorkSheetColumn workSheetColumn=listColumns.get(i);
				JsonObject jsonObject_column=new JsonObject();
				if(dmBizImportTemplate.getDataSourceType().equals("Excel"))
				{
					jsonObject_column.addProperty("RowIndex", "");
					jsonObject_column.addProperty("DbFieldName", workSheetColumn.getColumnName());
					jsonObject_column.addProperty("DbFieldNameCh", workSheetColumn.getColumnNameCh());
					jsonObject_column.addProperty("ExcelHeader", "");
					jsonArray.add(jsonObject_column);
				}else{
					jsonObject_column.addProperty("FieldName", workSheetColumn.getColumnName());
					jsonObject_column.addProperty("FieldNameCh", workSheetColumn.getColumnNameCh());
					jsonObject_column.addProperty("FieldNameSource", "");
					jsonArray.add(jsonObject_column);
				}
			}
			jsonObject.add("FieldMaps", jsonArray);
			
			String szSql = "INSERT INTO HASYS_DM_BIZTEMPLATEIMPORT (ID,TemplateID,BusinessId,Name,Description,IsDefault,SourceType,Xml) VALUES(S_HASYS_DM_BIZTEMPLATEIMPORT.nextval,?,?,?,?,?,?,?) ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1,dmBizImportTemplate.getTemplateId());
			stmt.setInt(2,dmBizImportTemplate.getBizId());
			stmt.setString(3,dmBizImportTemplate.getTemplateName());
			stmt.setString(4,dmBizImportTemplate.getDesc());
			stmt.setInt(5,dmBizImportTemplate.getIsDefault());
			stmt.setString(6,dmBizImportTemplate.getDataSourceType());
			stmt.setString(7,jsonObject.toString());
			stmt.execute();

			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}
	//修改客户信息导入模板接口
	public   boolean dmModifyCustomerImportTemplate(DMBizImportTemplate dmBizImportTemplate){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn =this.getDbConnection();
			String szSql = "UPDATE HASYS_DM_BIZTEMPLATEIMPORT SET Name = ? ,Description = ? ,IsDefault = ? ,SourceType =?  WHERE TemplateID=? AND BusinessId=? ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setString(1,dmBizImportTemplate.getTemplateName());
			stmt.setString(2,dmBizImportTemplate.getDesc());
			stmt.setInt(3,dmBizImportTemplate.getIsDefault());
			stmt.setString(4,dmBizImportTemplate.getDataSourceType());
			stmt.setInt(5,dmBizImportTemplate.getTemplateId());
			stmt.setInt(6,dmBizImportTemplate.getBizId());
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}
	//删除导入模板接口
	public   boolean dmDeleteBizImportTemplate(DMBizImportTemplate dmBizImportTemplate){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn =this.getDbConnection();
			String szSql = "DELETE FROM HASYS_DM_BIZTEMPLATEIMPORT WHERE TemplateID=? AND BusinessId=? ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1,dmBizImportTemplate.getTemplateId());
			stmt.setInt(2,dmBizImportTemplate.getBizId());
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}
	//获取单个导入模板数据源来源所需表
	public List<DMBizTemplateImportTableName> dmGetBizImportTableName()
	{
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<DMBizTemplateImportTableName> listDmTableName=new ArrayList<DMBizTemplateImportTableName>();
		try {
			dbConn =this.getDbConnection();
			String szSql=String.format("select t.table_name,f.comments from user_tables t inner join user_tab_comments f on t.table_name = f.table_name");
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMBizTemplateImportTableName dmBizTemplateImportTableName=new DMBizTemplateImportTableName();
				dmBizTemplateImportTableName.setTableName(rs.getString(1));
				dmBizTemplateImportTableName.setComments(rs.getString(2));
				listDmTableName.add(dmBizTemplateImportTableName);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		
		return listDmTableName;
	}
	//获取所选表的所有列信息
	public List<DMBizTemplateImportTableColumns> dmGetBizImportTableColums(String tableName)
	{
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<DMBizTemplateImportTableColumns> listDmTableColumns=new ArrayList<DMBizTemplateImportTableColumns>();
		try {
			dbConn =this.getDbConnection();
			String szSql = String.format("select column_name,comments from user_col_comments cc where cc.table_name='%s'",tableName);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMBizTemplateImportTableColumns dmBizTemplateImportTableColumns =new DMBizTemplateImportTableColumns();
				dmBizTemplateImportTableColumns.setColumn_Name(rs.getString(1));
				dmBizTemplateImportTableColumns.setComments(rs.getString(2));
				listDmTableColumns.add(dmBizTemplateImportTableColumns);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		
		return listDmTableColumns;
	}
	//获取单个导入模板配置接口
	public String dmGetBizImportMapColumns(DMBizImportTemplate dmBizImportTemplate)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String json="";
		try {
			dbConn =this.getDbConnection();
			String szSql = String.format("select xml from HASYS_DM_BIZTEMPLATEIMPORT where BusinessID="+dmBizImportTemplate.getBizId()+" and TemplateID="+dmBizImportTemplate.getTemplateId()+"");
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				json=rs.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		
		return json;

	}
	//修改单个导入模板配置接口
	public   boolean dmModifyBizImportMapColumns(DMBizImportTemplate dmBizImportTemplate,String mapColumns){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JsonObject jsonObject=new JsonParser().parse(mapColumns).getAsJsonObject();
		try {
			dbConn =this.getDbConnection();
			String szSql =String.format( "update  HASYS_DM_BIZTEMPLATEIMPORT set xml='"+jsonObject.toString()+"' WHERE TemplateID="+dmBizImportTemplate.getTemplateId()+" AND BusinessId="+dmBizImportTemplate.getBizId()+" ");
			stmt = dbConn.prepareStatement(szSql);
			
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}	
	
	//获取单个导出导入模板配置excel数据信息
	public List<DMBizTemplateExcelColums> dmGetBizExcel(MultipartFile file) throws Exception
	{
		
		String fileName=file.getOriginalFilename();
		InputStream in=file.getInputStream();
		Workbook wookbook =null;
		String suffix=fileName.substring(fileName.indexOf("."));//获取后缀名
		if(".xls".equals(suffix)){ 
			wookbook = new HSSFWorkbook(in);
		}else if(".xlsx".equals(suffix)){
			wookbook = new XSSFWorkbook(in);
		}
        Sheet sheet = wookbook.getSheetAt(0);
        Row row = sheet.getRow(sheet.getFirstRowNum());
        short firstCellNum = row.getFirstCellNum();
        short lastCellNum = row.getLastCellNum();
          List<DMBizTemplateExcelColums> listDMBizTemplateExcelColums=new ArrayList<>();
        for(int i=firstCellNum;i<=lastCellNum-1;i++){
        	DMBizTemplateExcelColums dmBizTemplateExcelColums=new  DMBizTemplateExcelColums();
        	dmBizTemplateExcelColums.setExcelColumn(row.getCell(i).toString());
        	listDMBizTemplateExcelColums.add(dmBizTemplateExcelColums);
        }
        
        return listDMBizTemplateExcelColums;
	}
	
	
	
	
	
	
}
