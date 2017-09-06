package hiapp.modules.dmsetting.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import hiapp.system.worksheet.dblayer.CreationInfoWorkSheet;
import hiapp.system.worksheet.dblayer.WorkSheetManager;
import hiapp.utils.UtilServlet;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

public class DMBizTemplatesImport {
	public static boolean getAllTemplates(Connection dbConn,int bizId,List<DMBizTemplateImport> listTemplate){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String szSql = "SELECT TemplateID,Name,Description,IsDefault,SourceType FROM HASYS_DM_BIZTEMPLATEIMPORT WHERE BusinessId=? ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1,bizId);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMBizTemplateImport dmBizTemplateImport=new DMBizTemplateImport();
				dmBizTemplateImport.setTemplateId(rs.getInt(1));
				dmBizTemplateImport.setName(rs.getString(2));
				dmBizTemplateImport.setDescription(rs.getString(3));
				dmBizTemplateImport.setDefault(rs.getBoolean(4));
				dmBizTemplateImport.setSourceType(rs.getString(5));
				listTemplate.add(dmBizTemplateImport);
			}
		} catch (SQLException e) {
			ServiceResult Result = new ServiceResult();
			Result.setResultCode(ServiceResultCode.FILE_FAIL);
			Result.setReturnMessage("查询失败");
			e.printStackTrace();
			return false;
		} finally {
			
		}
		
		return true;
	}
	public static boolean newTemplate(Connection dbConn,int bizId,int templateID,String szName,String szDescription,int isDefault,String szSourceType){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String szSql = "INSERT INTO HASYS_DM_BIZTEMPLATEIMPORT (ID,TemplateID,BusinessId,Name,Description,IsDefault,SourceType,Xml) VALUES(SEQ_BIZADimport.nextval,?,?,?,?,?,?,'') ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1,templateID);
			stmt.setInt(2,bizId);
			stmt.setString(3,szName);
			stmt.setString(4,szDescription);
			stmt.setInt(5,isDefault);
			stmt.setString(6,szSourceType);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			ServiceResult Result = new ServiceResult();
			Result.setResultCode(ServiceResultCode.FILE_FAIL);
			Result.setReturnMessage("新建失败");
		} finally {
			
		}
		return true;
	}
	public static boolean modifyTemplate(UtilServlet utilServlet,int bizId,int templateID,String szName,String szDescription,int isDefault,String szSourceType){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String szSql = "UPDATE HASYS_DM_BIZTEMPLATEIMPORT SET Name = ? ,Description = ? ,IsDefault = ? ,SourceType =? ,Xml = '' WHERE TemplateID=? AND BusinessId=? ";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setString(1,szName);
			stmt.setString(2,szDescription);
			stmt.setInt(3,isDefault);
			stmt.setString(4,szSourceType);
			stmt.setInt(5,templateID);
			stmt.setInt(6,bizId);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			utilServlet.setResultCode(1);
			utilServlet.setResultMessage("淇敼璁板綍澶辫触");
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}
		return true;
	}
	public static boolean destroyTemplate(Connection dbConn,int bizId,int templateID){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String szSql = "DELETE FROM HASYS_DM_BIZTEMPLATEIMPORT WHERE TemplateID=? AND BusinessId=? ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1,templateID);
			stmt.setInt(2,bizId);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			ServiceResult Result = new ServiceResult();
			Result.setResultCode(ServiceResultCode.FILE_FAIL);
			Result.setReturnMessage("删除失败");
		} finally {
			
		}
		return true;
	}
}
