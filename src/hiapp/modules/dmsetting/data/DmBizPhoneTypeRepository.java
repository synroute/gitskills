package hiapp.modules.dmsetting.data;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import hiapp.modules.dmsetting.DMBizPhoneType;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

@Repository
public class DmBizPhoneTypeRepository extends BaseRepository {
	
	
	//添加号码类型
	public boolean dmAddBizPhoneType(String bizId,JsonArray jsonArray,StringBuffer errMessage)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection dbConn = null;
		dmDeleteBizPhoneType(bizId);
		try {
			dbConn =this.getDbConnection();
			for(int i=0;i<jsonArray.size();i++)
			{
				JsonObject jsonObject =jsonArray.get(i).getAsJsonObject();
				try {
					
					int count = 0;
					String szSql = String.format("select COUNT(*) from HASYS_DM_BIZPHONETYPE where BusinessId=%s and name='%s'",bizId,jsonObject.get("name").getAsString());
					stmt = dbConn.prepareStatement(szSql);
					rs = stmt.executeQuery();
					if (rs.next()) {
						count = rs.getInt(1);
					}
					if (count > 0) {
						errMessage.append("名称冲突！");
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					
					int count = 0;
					String szSql = String.format("select COUNT(*) from HASYS_DM_BIZPHONETYPE where BusinessId=%s and CustomerColumnMap='%s'",bizId,jsonObject.get("customerColumnMap").getAsString());
					stmt = dbConn.prepareStatement(szSql);
					rs = stmt.executeQuery();
					if (rs.next()) {
						count = rs.getInt(1);
					}
					if (count > 0) {
						errMessage.append("导入表设置字段冲突！");
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					
					int count = 0;
					String szSql = String.format("select COUNT(*) from HASYS_DM_BIZPHONETYPE where BusinessId=%s and DialOrder=%s",bizId,jsonObject.get("dialOrder").getAsString());
					stmt = dbConn.prepareStatement(szSql);
					rs = stmt.executeQuery();
					if (rs.next()) {
						count = rs.getInt(1);
					}
					if (count > 0) {
						errMessage.append("拨打顺序冲突！");
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				String sql="insert into HASYS_DM_BIZPHONETYPE (BusinessId,name,nameCh,decription,CustomerColumnMap,dialOrder,DIALTYPE) values("+bizId+",'"+
				jsonObject.get("name").getAsString()+"','"+jsonObject.get("nameCh").getAsString()+"','"+jsonObject.get("description").getAsString()+"','"+jsonObject.get("customerColumnMap").getAsString()+"',"+jsonObject.get("dialOrder").getAsString()+","+jsonObject.get("dialType").getAsString()+")";
				stmt = dbConn.prepareStatement(sql);
		        stmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseExecute(stmt);
			DbUtil.DbCloseConnection(dbConn);
			
		}
		return true;
	}
	
	
	/*public boolean dmModifyBizPhoneType(DMBizPhoneType dmBizPhoneType,StringBuffer errMessage)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			dbConn =this.getDbConnection();
			try {
				
				int count = 0;
				String szSql = String.format("select COUNT(*) from HASYS_DM_BIZPHONETYPE where BusinessId=%s and name='%s'",dmBizPhoneType.getBizId(),dmBizPhoneType.getName());
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				if (rs.next()) {
					count = rs.getInt(1);
				}
				if (count > 0) {
					errMessage.append("名称冲突！");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				
				int count = 0;
				String szSql = String.format("select COUNT(*) from HASYS_DM_BIZPHONETYPE where BusinessId=%s and CustomerColumnMap='%s'",dmBizPhoneType.getBizId(),dmBizPhoneType.getCustomerColumnMap());
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				if (rs.next()) {
					count = rs.getInt(1);
				}
				if (count > 0) {
					errMessage.append("导入表设置字段冲突！");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				
				int count = 0;
				String szSql = String.format("select COUNT(*) from HASYS_DM_BIZPHONETYPE where BusinessId=%s and DialOrder=%s",dmBizPhoneType.getBizId(),dmBizPhoneType.getDialOrder());
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				if (rs.next()) {
					count = rs.getInt(1);
				}
				if (count > 0) {
					errMessage.append("拨打顺序冲突！");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String sql = "update HASYS_DM_BIZPHONETYPE set name='"+dmBizPhoneType.getName()+"',nameCh='"+dmBizPhoneType.getNameCh()+"',decription='"+dmBizPhoneType.getDescription()+"',CustomerColumnMap='"+dmBizPhoneType.getCustomerColumnMap()+"',dialOrder='"+dmBizPhoneType.getDialOrder()+"' where BusinessId="+dmBizPhoneType.getBizId()+" and CustomerColumnMap='"+dmBizPhoneType.getCustomerColumnMap()+"'";
			stmt = dbConn.prepareStatement(sql);
	        stmt.executeUpdate();
	     	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}*/
	
	public boolean dmDeleteBizPhoneType(String bizid)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection dbConn = null;
		try {
			dbConn =this.getDbConnection();
			
			
			String sql = "delete from HASYS_DM_BIZPHONETYPE where BusinessId="+bizid+"";
			stmt = dbConn.prepareStatement(sql);
	        stmt.executeUpdate();
	     	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseExecute(stmt);
			DbUtil.DbCloseConnection(dbConn);
			
		}
		return true;
	}
	
	//获取所有号码类型
	public List<DMBizPhoneType> dmGetAllBizPhoneType(String bizId)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection dbConn = null;
		List<DMBizPhoneType> listDmBizPhoneType=new ArrayList<DMBizPhoneType>();
		try {
			dbConn =this.getDbConnection();
			try {
				
				int count = 0;
				String szSql = String.format("select BusinessId,Name,NameCh,Decription,DialOrder,CustomerColumnMap,DIALTYPE from HASYS_DM_BIZPHONETYPE where BusinessId=%s",bizId);
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					DMBizPhoneType dmBizPhoneType =new DMBizPhoneType();
					dmBizPhoneType.setBizId(bizId);
					dmBizPhoneType.setName(rs.getString(2));
					dmBizPhoneType.setNameCh(rs.getString(3));
					dmBizPhoneType.setDescription(rs.getString(4));
					dmBizPhoneType.setDialOrder(rs.getInt(5));
					dmBizPhoneType.setCustomerColumnMap(rs.getString(6));
					dmBizPhoneType.setDialType(rs.getInt(7));
					listDmBizPhoneType.add(dmBizPhoneType);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
	     	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseExecute(stmt);
			DbUtil.DbCloseConnection(dbConn);
			
		}
		return listDmBizPhoneType;
	}
	
	
	
}
