package hiapp.modules.dmsetting.data;

import hiapp.modules.dmsetting.beanOld.DMBusiness;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class DmBizRepository extends BaseRepository{
	@Autowired
	private DmBizRepository dmBizRepository;
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
	
	public boolean getAllDMBusiness(List<DMBusiness> listDMBusiness) {
		Connection dbConn = null;
		String szSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn = this.getDbConnection();
			szSql = String.format("SELECT ID,NAME,DESCRIPTION,OWNERGROUPID,MODEID,SUBMODEID FROM HASYS_DM_BUSINESS ORDER BY ID ");
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				DMBusiness DMBusiness = new DMBusiness();
				DMBusiness.setId(rs.getInt(1));
				DMBusiness.setName(rs.getString(2));
				DMBusiness.setDescription(rs.getString(3));
				DMBusiness.setOwnerGroupId(rs.getInt(4));
				DMBusiness.setModeId(rs.getInt(5));
				DMBusiness.setSubModeId(rs.getInt(6));
				String modeSubModeTextString=String.format("%d,%d",DMBusiness.getModeId(),DMBusiness.getSubModeId());
				DMBusiness.setModeSubmodeIdString(modeSubModeTextString);
				listDMBusiness.add(DMBusiness);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return true;
	}
	
	public ServiceResultCode newDMBusiness(
			String id, String name, String description,
			String ownerGroupId, String modeId, String subModeId, StringBuffer errMessage) {
		Connection dbConn = null;
		String szSql = "";
		PreparedStatement stmt = null;	
		ServiceResult serviceresult = new ServiceResult();
		ResultSet rs = null;	
		try {/////////////////////////////////////////////////////////////////
			dbConn = this.getDbConnection();
			int count=0;
			szSql = " select COUNT(*) from HASYS_DM_BUSINESS where ID='";
			szSql+=id;
			szSql+="'";
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count=rs.getInt(1);
			}
			if (count>0) {
				errMessage.append("ID 冲突！");
				return ServiceResultCode.INVALID_PARAM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{	
			DbUtil.DbCloseQuery(rs, stmt);
		}
		try {/////////////////////////////////////////////////////////////////
			dbConn = this.getDbConnection();
			int count=0;
			szSql = " select COUNT(*) from HASYS_DM_BUSINESS where NAME='";
			szSql+=name;
			szSql+="'";
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count=rs.getInt(1);
			}
			if (count>0) {
				errMessage.append("业务名称冲突！");
				return ServiceResultCode.INVALID_PARAM;				
			}			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.DbCloseQuery(rs, stmt);
		}
		try {/////////////////////////////////////////////////////////////////
			dbConn = this.getDbConnection();
			szSql = String.format("INSERT INTO HASYS_DM_BUSINESS (ID,NAME,DESCRIPTION,OWNERGROUPID,modeid,submodeid) "+ "VALUES ('%s','%s','%s','%s',%s,%s) ", 
									id,name, description, ownerGroupId,modeId,subModeId);
			stmt = dbConn.prepareStatement(szSql);		
			stmt.execute();				
		} catch (Exception e) {
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("失败");
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);		
		}
		DMBusiness dmBusiness=new DMBusiness();
		dmBusiness.setId(Integer.parseInt(id));
		dmBusiness.setName(name);
		dmBusiness.setModeId(Integer.parseInt(modeId));
		dmBusiness.setSubModeId(Integer.parseInt(subModeId));
		return dmWorkSheetRepository.newDMBizWorkSheetsSystem(dmBusiness, errMessage);
	}

	public ServiceResultCode modifyDMBusiness(
			String szDMBId, String szDMBName, String szDMBDescription,
			String szOwnerGroupId, StringBuffer errMessage) {
		Connection dbConn = null;
		String szSql = "";
		PreparedStatement stmt = null;
		ServiceResult serviceresult = new ServiceResult();
		ResultSet rs = null;
		try {////////////////////////////////////////////////////////////////
			dbConn = this.getDbConnection();
			int count=0;
			szSql = String.format(" select COUNT(*) from HASYS_DM_BUSINESS where NAME='%s'AND ID!='%s' ",szDMBName, szDMBId);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count=rs.getInt(1);
			}
			if (count>0) {
				errMessage.append("冲突");
				return ServiceResultCode.INVALID_PARAM;				
			}		
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.DbCloseQuery(rs, stmt);
		}
		try {//
			dbConn = this.getDbConnection();
			szSql = String.format("UPDATE HASYS_DM_BUSINESS	SET  NAME = '%s',DESCRIPTION = '%s',OWNERGROUPID = '%s' WHERE ID='%s'",
					szDMBName, szDMBDescription, szOwnerGroupId,szDMBId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (SQLException e) {
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("失败");
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return ServiceResultCode.SUCCESS;
	}

	public ServiceResultCode destroyDMBusiness(int bizId, StringBuffer errMessage) {
		Connection dbConn = null;
		String szSql = "";
		PreparedStatement stmt = null;
		try {
			dbConn = this.getDbConnection();
			szSql = String.format("DELETE FROM HASYS_DM_BUSINESS WHERE ID='%s'",bizId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (Exception e) {
			return  ServiceResultCode.EXECUTE_SQL_FAIL;
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		try {
			return dmWorkSheetRepository.destroyWorkSheetAll(bizId,errMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  ServiceResultCode.EXECUTE_SQL_FAIL;
		}
	}

	public ServiceResultCode destroyDMBusinessBatch(List<String> listDMBId, StringBuffer errMessage) {
		try {
			for (int i = 0; i < listDMBId.size(); i++) {
				String bizId=listDMBId.get(i);
				destroyDMBusiness(Integer.parseInt(bizId),errMessage);
			}
		} catch (Exception e) {
			return  ServiceResultCode.EXECUTE_SQL_FAIL;
		}
		return  ServiceResultCode.SUCCESS;
	}

	public boolean DMBusinessQuery(
			String searchKeyString, List<DMBusiness> listDMBusiness) {
		Connection dbConn = null;
		String szSql="";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn = this.getDbConnection();
			szSql=String.format("SELECT ID,NAME,DESCRIPTION,OWNERGROUPID,MODEID,SUBMODEID FROM HASYS_DM_BUSINESS "
					+ "WHERE ID LIKE '%%%s%%' OR NAME LIKE '%%%s%%' "
					+ "ORDER BY ID",
					searchKeyString,searchKeyString);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				DMBusiness DMBusiness = new DMBusiness();
				DMBusiness.setId(rs.getInt(1));
				DMBusiness.setName(rs.getString(2));
				DMBusiness.setDescription(rs.getString(3));
				DMBusiness.setOwnerGroupId(rs.getInt(4));
				DMBusiness.setModeId(rs.getInt(5));
				DMBusiness.setSubModeId(rs.getInt(6));
				listDMBusiness.add(DMBusiness);
			}
		} 
		catch (Exception e) {
			return false;
		}
		finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return true;
	}
}
