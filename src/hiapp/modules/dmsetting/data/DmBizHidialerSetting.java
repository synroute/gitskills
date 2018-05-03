package hiapp.modules.dmsetting.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

@Repository
public class DmBizHidialerSetting extends BaseRepository {
	
	
	public boolean dmModifyHidialerSetting(String bizid,String mapcolumn)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection dbConn = null;
		try {
			dbConn =this.getDbConnection();
			String sql = "update  HASYS_DM_Business set CONFIGJSON='"+mapcolumn+"' where BusinessID="+bizid+"";
			stmt = dbConn.prepareStatement(sql);
	        stmt.executeUpdate();
	     	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}
	
	public boolean dmInsertEntityMap(String bizid,String entityId)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection dbConn = null;
		try {
			dbConn =this.getDbConnection();
			
			int count = 0;
			String szSql = String.format("select COUNT(*) from HASYS_ENTITY_MAP_MODULE where MODULEID=%s and TemplateID='%s' and MODULE='数据管理'",bizid,entityId);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			if (count == 0) {
			String sql = "insert into HASYS_ENTITY_MAP_MODULE values(S_HASYS_ENTITY_MAP_MODULE.nextval,'数据管理',"+bizid+",'"+entityId+"')";
			stmt = dbConn.prepareStatement(sql);
	        stmt.executeUpdate();
			}
	     	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}
	
	
	public String dmGetBizHidialerSetting(String bizid)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection dbConn = null;
		String hidialer="";
		try {
			dbConn =this.getDbConnection();
			String sql = "select CONFIGJSON from  HASYS_DM_Business where BusinessID="+bizid+"";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				hidialer=rs.getString(1);
			}
	     	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return hidialer;
	}
	
}
