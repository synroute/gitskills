package hiapp.modules.dmmanager.dbLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import hiapp.utils.UtilServlet;

public class DMBizTaskManager {
	public static boolean getTaskList(Connection dbConn,int bizId,String startTime,String endTime,List<DMTask> listTask){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String szTimeStart=String.format("%s 00:00:00",startTime);
			String szTimeEnd=String.format("%s 23:59:59",endTime);
			String szSqlTimeFilter=String.format("CreateTime BETWEEN to_date('%s','yyyy-mm-dd HH24:MI:SS')  AND to_date('%s','yyyy-mm-dd HH24:MI:SS') ",szTimeStart,szTimeEnd);

			String szSql = String.format("select tid,Name,CreateUserID,CreateTime, Description from HASYS_DM_TASK where  BusinessID=%d AND %s ORDER BY CreateTime DESC",
											bizId,szSqlTimeFilter);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMTask dmTask=new DMTask();
				dmTask.setId(rs.getString(1));
				dmTask.setName(rs.getString(2));
				dmTask.setCreateUserId(rs.getString(3));
				dmTask.setCreateTime(rs.getString(4));
				dmTask.setDescription(rs.getString(5));
				listTask.add(dmTask);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			
		}
		return true;
	}
}
