package hiapp.modules.dmsetting.dbLayer;

import hiapp.system.buinfo.bean.MapUserGroupRoleManager;
import hiapp.system.buinfo.beanOld.User;
import hiapp.utils.DbUtil;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;



public class DMBusinessManager {

	public static boolean getAllDMBusiness(Connection dbConn,List<DMBusiness> listDMBusiness) {
		String szSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
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
			String msgString = String.format("getAllDMBusiness Error ! sql=[%s]",szSql);
			System.out.print(msgString);
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
		}
		String msgString = String.format("getAllDMBusiness OK sql=[%s]",szSql);
		System.out.print(msgString);
		return true;
	}

	public static ServiceResultCode newDMBusiness(Connection dbConn,
			String id, String name, String description,
			String ownerGroupId, String modeId, String subModeId, StringBuffer errMessage) {
		String szSql = "";
		PreparedStatement stmt = null;	
		ServiceResult serviceresult = new ServiceResult();
		ResultSet rs = null;	
		
		try {//鏌ヨHASYS_DM_BUSINESS锛岃嫢宸插瓨鍦ㄧ浉鍚孖d,鎻愮ず鍐茬獊骞惰繑鍥�///////////////////////////////////////////////////////////////
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
				errMessage.append("Id 冲突！");
				return ServiceResultCode.INVALID_PARAM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{	
			DbUtil.DbCloseQuery(rs, stmt);
		}
			
		try {//鏌ヨHASYS_DM_BUSINESS锛岃嫢宸插瓨鍦ㄧ浉鍚宯ame,鎻愮ず鍐茬獊骞惰繑鍥�///////////////////////////////////////////////////////////////
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
			

		try {//灏嗚緭鍏ュ瓧娈垫彃鍏ユ暟鎹簱鐩稿簲鐨勫垪///////////////////////////////////////////////////////////////	
			szSql = String.format("INSERT INTO HASYS_DM_BUSINESS (ID,NAME,DESCRIPTION,OWNERGROUPID,modeid,submodeid) "+ "VALUES ('%s','%s','%s','%s',%s,%s) ", 
									id,name, description, ownerGroupId,modeId,subModeId);
			stmt = dbConn.prepareStatement(szSql);		
			stmt.execute();				
		} catch (Exception e) {
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("澶辫触");
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseExecute(stmt);		
		}
		DMBusiness dmBusiness=new DMBusiness();
		dmBusiness.setId(Integer.parseInt(id));
		dmBusiness.setName(name);
		dmBusiness.setModeId(Integer.parseInt(modeId));
		dmBusiness.setSubModeId(Integer.parseInt(subModeId));
		return DMWorkSheetManager.newDMBizWorkSheetsSystem(dbConn, dmBusiness, errMessage);
	}

	public static ServiceResultCode modifyDMBusiness(Connection dbConn,
			String szDMBId, String szDMBName, String szDMBDescription,
			String szOwnerGroupId, StringBuffer errMessage) {
		String szSql = "";
		PreparedStatement stmt = null;
		ServiceResult serviceresult = new ServiceResult();
		ResultSet rs = null;
		

		try {//鏌ヨHASYS_DM_BUSINESS锛岃嫢宸插瓨鍦ㄧ浉鍚宯ame,鎻愮ず鍐茬獊骞惰繑鍥�///////////////////////////////////////////////////////////////
			int count=0;
			szSql = String.format(" select COUNT(*) from HASYS_DM_BUSINESS where NAME='%s'AND ID!='%s' ",szDMBName, szDMBId);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count=rs.getInt(1);
			}
			if (count>0) {
				errMessage.append("鍚嶇О 鍐茬獊锛�");
				return ServiceResultCode.INVALID_PARAM;				
			}		
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.DbCloseQuery(rs, stmt);
		}
		try {//鎻掑叆 锛屽畬鎴愪慨鏀�	
			szSql = String.format("UPDATE HASYS_DM_BUSINESS	SET  NAME = '%s',DESCRIPTION = '%s',OWNERGROUPID = '%s' WHERE ID='%s'",
					szDMBName, szDMBDescription, szOwnerGroupId,szDMBId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (SQLException e) {
			String msgString = String.format("modifyDMBusiness Error 锛丼ql=[%s]",szSql);
			System.out.print(msgString);
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("澶辫触");
		} finally {
			DbUtil.DbCloseExecute(stmt);
		}
		String msgString = String.format("modifyDMBusiness OK 锛丼ql=[%s]",szSql);
		System.out.print(msgString);
		return ServiceResultCode.SUCCESS;
	}

	public static ServiceResultCode destroyDMBusiness(Connection dbConn,int bizId, StringBuffer errMessage) {
		String szSql = "";
		PreparedStatement stmt = null;
		try {
			szSql = String.format("DELETE FROM HASYS_DM_BUSINESS WHERE ID='%s'",bizId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (Exception e) {
			String msgString = String.format("destroyDMBusiness Error 锛丼ql=[%s]",szSql);
			System.out.print(msgString);
			return  ServiceResultCode.EXECUTE_SQL_FAIL;
		} finally {
			DbUtil.DbCloseExecute(stmt);
		}
		String msgString = String.format("destroyDMBusiness OK 锛丼ql=[%s]",szSql);
		System.out.print(msgString);
		try {
			return DMWorkSheetManager.destroyWorkSheetAll(dbConn, bizId,errMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  ServiceResultCode.EXECUTE_SQL_FAIL;
		}
	}

	public static ServiceResultCode destroyDMBusinessBatch(Connection dbConn,List<String> listDMBId, StringBuffer errMessage) {
		String szSql = "";
		PreparedStatement stmt = null;
		ServiceResult serviceresult = new ServiceResult();
		try {
			for (int i = 0; i < listDMBId.size(); i++) {
				String bizId=listDMBId.get(i);
				destroyDMBusiness(dbConn,Integer.parseInt(bizId),errMessage);
			}
		} catch (Exception e) {
			return  ServiceResultCode.EXECUTE_SQL_FAIL;
		}
		return  ServiceResultCode.SUCCESS;
	}

	public static boolean DMBusinessQuery(Connection dbConn,
			String searchKeyString, List<DMBusiness> listDMBusiness) {
		String szSql="";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String msgString=null;
		try {
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
			msgString=String.format("DMBusinessQuery Error 锟斤拷Sql=[%s]", szSql);
			System.out.print(msgString);
			return false;
		}
		finally {
			DbUtil.DbCloseQuery(rs, stmt);
		}
		msgString=String.format("DMBusinessQuery OK 锟斤拷Sql=[%s]", szSql);
		System.out.print(msgString);
		return true;
	}

}
