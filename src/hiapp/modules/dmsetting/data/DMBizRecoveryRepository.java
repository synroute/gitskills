package hiapp.modules.dmsetting.data;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import hiapp.modules.dmsetting.DMBizExportTemplate;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.serviceresult.ServiceResultCode;
@Repository
public class DMBizRecoveryRepository  extends BaseRepository{
	
	public ServiceResultCode newRecoveryTemplate(String bizId, String templateID,
			String name, String description, String isDefault,
			StringBuffer errMessage) {
		Connection dbConn = null;
		// 查询HASYS_DM_BIZTEMPLATERECOVER，若已同业务下存在相同name,提示冲突并返回
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String szSql = null;
		try {
			dbConn = this.getDbConnection();
			int count = 0;
			szSql = String.format("select COUNT(*) from HASYS_DM_BIZTEMPLATERECOVER where BUSINESSID='%s' and TemplateID='%s'",bizId,templateID);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			if (count > 0) {
				errMessage.append("模板ID冲突！");
				return ServiceResultCode.INVALID_PARAM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			dbConn = this.getDbConnection();
			int count = 0;
			szSql = String.format("select COUNT(*) from HASYS_DM_BIZTEMPLATERECOVER where BUSINESSID='%s' and NAME='%s'",bizId,name);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			if (count > 0) {
				errMessage.append("模板名称冲突！");
				return ServiceResultCode.INVALID_PARAM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			dbConn = this.getDbConnection();
			String szSql2 = String.format("INSERT INTO HASYS_DM_BIZTEMPLATERECOVER (ID,TemplateID,BusinessId,Name,Description,IsDefault) VALUES(S_HASYS_DM_BIZTEMPLATERECOVER.nextval,'%s','%s','%s','%s','%s')",templateID,bizId,name,description,isDefault);
			PreparedStatement stmt2 = dbConn.prepareStatement(szSql2);
			stmt2.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			errMessage.append("新建模板失败！");
			return ServiceResultCode.INVALID_PARAM;
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return ServiceResultCode.SUCCESS;
	}

	public boolean deleteRecoveryTemplate(String bizId, String templateID) {
		Connection dbConn = null;
		PreparedStatement stmt = null;
		try {
			dbConn = this.getDbConnection();
			String szSql = "DELETE FROM HASYS_DM_BIZTEMPLATERECOVER WHERE TemplateID=? AND BusinessId=? ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setString(1,templateID);
			stmt.setString(2,bizId);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}

	public void getAllRecoveryTemplateByBizId(String bizId,List<DMBizExportTemplate> listDMBizRecoveryTemplate) {
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn = this.getDbConnection();
			String szSql =String.format("SELECT TemplateID,Name,Description,IsDefault FROM HASYS_DM_BIZTEMPLATERECOVER WHERE BusinessId='%s' order by TemplateID", bizId) ;
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMBizExportTemplate dmBizTemplateRecovery=new DMBizExportTemplate();
				dmBizTemplateRecovery.setTemplateId(rs.getInt(1));
				dmBizTemplateRecovery.setTemplateName(rs.getString(2));
				dmBizTemplateRecovery.setDesc(rs.getString(3));
				dmBizTemplateRecovery.setIsDefault(rs.getInt(4));
				listDMBizRecoveryTemplate.add(dmBizTemplateRecovery);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return;
	}

	public ServiceResultCode modifyRecoveryTemplate(String bizId, String templateId,
			String name, String description, String isDefault, StringBuffer errMessage) {
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String szSql = null;
		// 查询HASYS_DM_BIZTEMPLATERECOVER，若已同业务下存在相同name,提示冲突并返回
		try {
			dbConn = this.getDbConnection();
			int count = 0;
			szSql = String.format("select COUNT(*) from HASYS_DM_BIZTEMPLATERECOVER where BUSINESSID='%s' and NAME='%s' and TemplateID!='%s'",bizId,name,templateId);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			if (count > 0) {
				errMessage.append("模板名称冲突！");
				return ServiceResultCode.INVALID_PARAM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			dbConn = this.getDbConnection();
			String szSql2 = String.format("UPDATE HASYS_DM_BIZTEMPLATERECOVER SET Name = '%s' ,Description = '%s' ,IsDefault = '%s'  WHERE TemplateID='%s' AND BusinessId='%s' ",name,description,isDefault,templateId,bizId) ;
			PreparedStatement stmt2 = dbConn.prepareStatement(szSql2);
			stmt2.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return ServiceResultCode.SUCCESS;
	}

	public String getBizRecoveryMapColumn(String bizId, String templateId) {
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String RecoveryJson = "";
		try {
			dbConn = this.getDbConnection();	
			String szSql = String.format("SELECT CONFIGJSON FROM HASYS_DM_BIZTEMPLATERECOVER WHERE BusinessId='%s' AND TEMPLATEID = '%s' ",bizId,templateId) ;
//			String szSql = String.format("SELECT codetype FROM HASYS_DM_BIZENDCODE") ;
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				RecoveryJson = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return RecoveryJson;
	}

	public boolean modifyRecoveryMapColumns(String bizId, String templateId,
			String mapColumns) {
		Connection dbConn = null;
		PreparedStatement stmt = null;
		String szSql = null;
		try {
			dbConn = this.getDbConnection();
			
			
			PreparedStatement stat=dbConn.prepareStatement("UPDATE HASYS_DM_BIZTEMPLATERECOVER SET CONFIGJSON = ? WHERE TemplateID='"+templateId+"' AND BusinessId='"+bizId+"' ") ;
			
			String clobContent = mapColumns;  
		     StringReader reader = new StringReader(clobContent);  
		     stat.setCharacterStream(1, reader, clobContent.length());
		     stat.executeUpdate();
			/*szSql = String.format("UPDATE HASYS_DM_BIZTEMPLATERECOVER SET CONFIGJSON = '%s' WHERE TemplateID='%s' AND BusinessId='%s' ",mapColumns,templateId,bizId) ;
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();*/
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}	
}

