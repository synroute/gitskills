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
	
	Connection dbConn = null;
	public boolean dmModifyHidialerSetting(String bizid,String mapcolumn)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			dbConn =this.getDbConnection();
			String sql = "update  HASYS_DM_Business set DetailSettingXml='"+mapcolumn+"' where BusinessID="+bizid+"";
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
	
	public String dmGetBizHidialerSetting(String bizid)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String hidialer="";
		try {
			dbConn =this.getDbConnection();
			String sql = "select DetailSettingXml from  HASYS_DM_Business where BusinessID="+bizid+"";
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
