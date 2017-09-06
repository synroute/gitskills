package hiapp.modules.dmsetting.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import hiapp.utils.UtilServlet;

public class DMBizTemplatesPageCreation {
	public static boolean getAllTemplates(UtilServlet utilServlet,int bizId,List<DMBizTemplatePageCreation> listTemplate){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String szSql = "SELECT TemplateID,Name,Description FROM HASYS_DM_BIZTEMPLATEPAGES	WHERE BusinessId=? order by TemplateID ";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setInt(1,bizId);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMBizTemplatePageCreation dmBizTemplatePageCreation=new DMBizTemplatePageCreation();
				dmBizTemplatePageCreation.setTemplateId(rs.getInt(1));
				dmBizTemplatePageCreation.setName(rs.getString(2));
				dmBizTemplatePageCreation.setDescription(rs.getString(3));
				listTemplate.add(dmBizTemplatePageCreation);
			}
		} catch (SQLException e) {
			utilServlet.setResultCode(1);
			utilServlet.setResultMessage("查询数据库失败");
			e.printStackTrace();
			return false;
		} finally {
			utilServlet.DbCloseQuery(rs, stmt);
		}
		
		return true;
	}
	public static boolean newTemplate(UtilServlet utilServlet,int bizId,int templateID,String szName,String szDescription){
		PreparedStatement stmt = null;
		try {
			String szSql = "INSERT INTO HASYS_DM_BIZTEMPLATEPAGES (ID,TemplateID,BusinessId,Name,Description,Xml) VALUES(SEQ_BIZADimport.nextval,?,?,?,?,'') ";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setInt(1,templateID);
			stmt.setInt(2,bizId);
			stmt.setString(3,szName);
			stmt.setString(4,szDescription);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			utilServlet.setResultCode(1);
			utilServlet.setResultMessage("插入记录失败");
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}
		return true;
	}
	public static boolean modifyTemplate(UtilServlet utilServlet,int bizId,int templateID,String szName,String szDescription){
		PreparedStatement stmt = null;
		try {
			String szSql = "UPDATE HASYS_DM_BIZTEMPLATEPAGES SET Name = ? ,Description = ?  WHERE TemplateID=? AND BusinessId=? ";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setString(1,szName);
			stmt.setString(2,szDescription);
			stmt.setInt(3,templateID);
			stmt.setInt(4,bizId);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			utilServlet.setResultCode(1);
			utilServlet.setResultMessage("修改记录失败");
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}
		return true;
	}
	public static boolean destroyTemplate(UtilServlet utilServlet,int bizId,int templateID){
		PreparedStatement stmt = null;
		try {
			String szSql = "DELETE FROM HASYS_DM_BIZTEMPLATEPAGES WHERE TemplateID=? AND BusinessId=? ";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setInt(1,templateID);
			stmt.setInt(2,bizId);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			utilServlet.setResultCode(1);
			utilServlet.setResultMessage("删除记录失败");
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}
		return true;
	}

}
