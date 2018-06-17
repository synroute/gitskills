package hiapp.modules.dmsetting.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



import hiapp.modules.dmsetting.DMTimeoutManagement;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public class DmTimeoutRepository extends BaseRepository {
	
	public boolean dmInsertTimeoutConfig(String bizid,String isEnable,String timeOutConfig)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection dbConn = null;
		try {
			dbConn =this.getDbConnection();
			
			String szSql = String.format("delete from HASYS_DM_BIZTIMEOUTCONFIG where BusinessID="+bizid+"");
			stmt = dbConn.prepareStatement(szSql);
			int count=stmt.executeUpdate();
			
			String sql = "insert into HASYS_DM_BIZTIMEOUTCONFIG values(S_HASYS_DM_BIZTIMEOUTCONFIG.nextval,"+bizid+","+isEnable+",'"+timeOutConfig+"')";
			stmt = dbConn.prepareStatement(sql);
	        stmt.executeUpdate();
			
	     	
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}
	
	public List<DMTimeoutManagement> dmGetAllTimeoutConfig(String bizId)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection dbConn = null;
		List<DMTimeoutManagement> listDMTimeoutManagement=new ArrayList<DMTimeoutManagement>();
		try {
			dbConn =this.getDbConnection();
			try {
				String szSql = String.format("select BusinessID,ISEnable,TIMEOUTCONFIG from HASYS_DM_BIZTIMEOUTCONFIG where businessid='"+bizId+"'");
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					DMTimeoutManagement dmTimeoutManagement =new DMTimeoutManagement();
					dmTimeoutManagement.setBizId(rs.getInt(1));
					dmTimeoutManagement.setIsEnable(rs.getInt(2));
					dmTimeoutManagement.setTimeOutConfgi(rs.getString(3));
					listDMTimeoutManagement.add(dmTimeoutManagement);
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
		return listDMTimeoutManagement;
	}
	
	
}
