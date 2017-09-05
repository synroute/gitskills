package hiapp.modules.dmsetting.dbLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.JsonObject;

import hiapp.utils.UtilServlet;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

public class DMBizTemplatesExport {
	public static boolean getAllTemplates(Connection dbConn,int bizId,List<DMBizTemplateExport> listTemplate){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String szSql = "SELECT TemplateID,Name,Description,IsDefault FROM HASYS_DM_BIZTEMPLATEExport WHERE BusinessId=? ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1,bizId);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMBizTemplateExport dmBizTemplateExport=new DMBizTemplateExport();
				dmBizTemplateExport.setTemplateID(rs.getInt(1));
				dmBizTemplateExport.setName(rs.getString(2));
				dmBizTemplateExport.setDescription(rs.getString(3));
				dmBizTemplateExport.setIsDefault(rs.getInt(4));
				listTemplate.add(dmBizTemplateExport);
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
	public static boolean newTemplate(Connection dbConn,int bizId,int templateID,String name,String description,int isDefault){
		PreparedStatement stmt = null;
		try {
			String szSql = "INSERT INTO HASYS_DM_BIZTEMPLATEExport (ID,TemplateID,BusinessId,Name,Description,IsDefault,Xml) VALUES(HASYS_DM_BIZTEMPLATEEXPORT_ID.nextval,?,?,?,?,?,'')  ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1,templateID);
			stmt.setInt(2,bizId);
			stmt.setString(3,name);
			stmt.setString(4,description);
			stmt.setInt(5,isDefault);
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
	public static boolean modifyTemplate(Connection dbConn,int bizId,int templateID,String name,String description,int isDefault){
		PreparedStatement stmt = null;
		try {
			String szSql = "UPDATE HASYS_DM_BIZTEMPLATEExport SET Name = ? ,Description = ? ,IsDefault = ? ,Xml = '' WHERE TemplateID=? AND BusinessId=? ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setString(1,name);
			stmt.setString(2,description);
			stmt.setInt(3,isDefault);
			stmt.setInt(5,templateID);
			stmt.setInt(6,bizId);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			ServiceResult Result = new ServiceResult();
			Result.setResultCode(ServiceResultCode.FILE_FAIL);
			Result.setReturnMessage("修改失败");
		} finally {
			
		}
		return true;
	}
	public static boolean destroyTemplate(Connection dbConn,int bizId,int templateID){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String szSql = "DELETE FROM HASYS_DM_BIZTEMPLATEExport WHERE TemplateID=? AND BusinessId=? ";
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
