package hiapp.modules.dmsetting.data;

import hiapp.modules.dmsetting.DMBizOutboundModelEnum;
import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.result.DMBizOutboundModel;
import hiapp.system.buinfo.data.GroupRepository;
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
import org.springframework.stereotype.Repository;
@Repository
public class DmBizRepository extends BaseRepository{
	@Autowired
	private DmBizRepository dmBizRepository;
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
	@Autowired
	private GroupRepository groupRepository;
	
	public boolean getAllDMBusiness(List<DMBusiness> listDMBusiness) {
		Connection dbConn = null;
		String szSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn = this.getDbConnection();
			szSql = String.format("SELECT BUSINESSID,NAME,DESCRIPTION,OWNERGROUPID,OUTBOUNDMDDEID FROM HASYS_DM_BUSINESS ORDER BY BUSINESSID ");
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				DMBusiness DMBusiness = new DMBusiness();
				DMBusiness.setBizId(rs.getInt(1));
				DMBusiness.setName(rs.getString(2));
				DMBusiness.setDesc(rs.getString(3));
				DMBusiness.setOwnerGroupId(rs.getString(4));
				String groupName = groupRepository.getGroupById(rs.getInt(4)).getName();
				DMBusiness.setOwnerGroupName(groupName);
				DMBusiness.setModeId(rs.getInt(5));
				DMBizOutboundModelEnum modelEnum = DMBizOutboundModelEnum.values()[DMBusiness.getModeId()-1];
				DMBusiness.setModeInfo(modelEnum.getOutboundID()+";"+modelEnum.getOutboundType()+";"+modelEnum.getOutboundMode());
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
			String ownerGroupId, String modeId, StringBuffer errMessage) {
		Connection dbConn = null;
		String szSql = "";
		PreparedStatement stmt = null;	
		ResultSet rs = null;	
		try {/////////////////////////////////////////////////////////////////
			dbConn = this.getDbConnection();
			int count=0;
			szSql = " select COUNT(*) from HASYS_DM_BUSINESS where BUSINESSID='";
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
			szSql = String.format("INSERT INTO HASYS_DM_BUSINESS (BUSINESSID,NAME,DESCRIPTION,OWNERGROUPID,OUTBOUNDMDDEID) "+ "VALUES ('%s','%s','%s','%s',%s) ", 
									id,name, description, ownerGroupId,modeId);
			stmt = dbConn.prepareStatement(szSql);		
			stmt.execute();				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseExecute(stmt);		
		}
		
		try {//增加数据源池
			szSql = String.format("INSERT INTO HASYS_DM_DATAPOOL (ID,BUSINESSID,DATAPOOLNAME,DATAPOOLTYPE,DATAPOOLDES,PID,AREATYPE,POOLTOPLIMIT) "+ "VALUES (S_HASYS_DM_DATAPOOL.nextval,'%s','数据源池',1,'数据源池',-1,2,10000000) ", 
									id);
			stmt = dbConn.prepareStatement(szSql);		
			stmt.execute();				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);		
		}
		DMBusiness dmBusiness=new DMBusiness();
		dmBusiness.setBizId(Integer.parseInt(id));
		dmBusiness.setName(name);
		dmBusiness.setModeId(Integer.parseInt(modeId));
		//判断外拨模式，创建相应worksheet
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
			szSql = String.format(" select COUNT(*) from HASYS_DM_BUSINESS where NAME='%s' AND BUSINESSID!='%s' ",szDMBName, szDMBId);
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
			szSql = String.format("UPDATE HASYS_DM_BUSINESS	SET  NAME = '%s',DESCRIPTION = '%s',OWNERGROUPID = '%s' WHERE BUSINESSID='%s'",
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
	//删除业务
	public ServiceResultCode destroyDMBusiness(int bizId, StringBuffer errMessage) {
		Connection dbConn = null;
		String szSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		//判断业务状态
		try {
			dbConn = this.getDbConnection();
			szSql = String.format("SELECT STATE FROM HASYS_DM_SID WHERE BUSINESSID='%s'",bizId);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				if (rs.getString(1).equals("启动")) {
					errMessage.append("请先停止正在活动的共享批次再删除");
					return ServiceResultCode.EXECUTE_SQL_FAIL;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
			DbUtil.DbCloseConnection(dbConn);
		}
		//删除业务表中信息
		try {
			dbConn = this.getDbConnection();
			szSql = String.format("DELETE FROM HASYS_DM_BUSINESS WHERE BUSINESSID='%s'",bizId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			return  ServiceResultCode.EXECUTE_SQL_FAIL;
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		//同步删除业务相关表中数据
		this.destroyDMBizInOtherTables(bizId);
		//删除业务下相关工作表
		try {
			dmWorkSheetRepository.destroyWorkSheetAll(bizId,errMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  ServiceResultCode.EXECUTE_SQL_FAIL;
		}
		return ServiceResultCode.SUCCESS;
	}

	@SuppressWarnings("resource")
	private void destroyDMBizInOtherTables(int bizId) {
		Connection dbConn = null;
		String szSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn = this.getDbConnection();
			szSql = String.format("DELETE FROM Hasys_DM_BIZOutboundSetting WHERE BUSINESSID='%s'",bizId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
			szSql = String.format("DELETE FROM HASYS_DM_BIZENDCODE WHERE BUSINESSID='%s'",bizId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
			szSql = String.format("DELETE FROM HASYS_DM_BIZTEMPLATEEXPORT WHERE BUSINESSID='%s'",bizId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
			szSql = String.format("DELETE FROM HASYS_DM_BIZTEMPLATEIMPORT WHERE BUSINESSID='%s'",bizId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
			szSql = String.format("DELETE FROM HASYS_DM_BIZPHONETYPE WHERE BUSINESSID='%s'",bizId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
			szSql = String.format("DELETE FROM HASYS_DM_DATAPOOL WHERE BUSINESSID='%s'",bizId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return;
	}

	public boolean DMBusinessQuery(
			String searchKeyString, List<DMBusiness> listDMBusiness) {
		Connection dbConn = null;
		String szSql="";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn = this.getDbConnection();
			szSql=String.format("SELECT BUSINESSID,NAME,DESCRIPTION,OWNERGROUPID,OUTBOUNDMDDEID FROM HASYS_DM_BUSINESS "
					+ "WHERE ID LIKE '%%%s%%' OR NAME LIKE '%%%s%%' "
					+ "ORDER BY BUSINESSID",
					searchKeyString,searchKeyString);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				DMBusiness DMBusiness = new DMBusiness();
				DMBusiness.setBizId(rs.getInt(1));
				DMBusiness.setName(rs.getString(2));
				DMBusiness.setDesc(rs.getString(3));
				DMBusiness.setOwnerGroupId(rs.getString(4));
				DMBusiness.setModeId(rs.getInt(5));
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

	public boolean getAllDMBizModel(
			List<DMBizOutboundModel> listDMBizOutboundModel) {
		int num = DMBizOutboundModelEnum.values().length;
		for (int i = 0; i < num; i++) {
			DMBizOutboundModelEnum modelEnum = DMBizOutboundModelEnum.values()[i];
			DMBizOutboundModel DMBizOutboundModel = new DMBizOutboundModel();
			DMBizOutboundModel.setModelId(modelEnum.getOutboundID());
			DMBizOutboundModel.setModelType(modelEnum.getOutboundType());
			DMBizOutboundModel.setModelName(modelEnum.getOutboundMode());
			DMBizOutboundModel.setDescription(modelEnum.getOutboundMode());
			listDMBizOutboundModel.add(DMBizOutboundModel);
		}
		return true;
	}
}
