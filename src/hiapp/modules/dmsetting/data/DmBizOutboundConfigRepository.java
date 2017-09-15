package hiapp.modules.dmsetting.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
@Repository
public class DmBizOutboundConfigRepository extends BaseRepository {
	Connection conn = null;
	//获取所有外呼策略配置接口
	public String dmGetAllBizOutboundSetting(int bizId)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String xml="";
		try {
			conn =this.getDbConnection();
			String szSql = String.format("select xml from HASYS_DM_BIZADDSETTING where BusinessID="+bizId+"");
			stmt = conn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				xml=rs.getString("xml");
			}
			  
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		} 
		finally {
			DbUtil.DbCloseConnection(conn);
			DbUtil.DbCloseExecute(stmt);
		}
		return xml;
	}
	//修改外呼策略接口
	public boolean dmModifyBizRedailState(int bizId,String MapColumns)
	{
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String xml="";
		try {
			conn =this.getDbConnection();
			
			String selectsql=String.format("select count(*) from HASYS_DM_SID where BusinessID="+bizId+" and State='启动'");
			stmt = conn.prepareStatement(selectsql);
			rs = stmt.executeQuery();
			int count=0;
			while(rs.next())
			{
				count=rs.getInt(1);
			}
			if (count>0) {
				
				
				return false;
			}
			
			
			
			String xmlszSql = String.format("select xml from HASYS_DM_BIZADDSETTING where BusinessID="+bizId+"");
			stmt = conn.prepareStatement(xmlszSql);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				xml=rs.getString("xml");
			}

			JsonObject jsonObject=new JsonParser().parse(xml).getAsJsonObject();
			jsonObject.remove("RedialState");
			JsonObject redialState= new JsonParser().parse(MapColumns).getAsJsonObject();
			JsonArray jsonArray=new JsonArray();
			jsonArray.add(redialState);
			jsonObject.add("RedialState", jsonArray);
			
			String szSql = String.format("update HASYS_DM_BIZADDSETTING set xml='"+jsonObject.toString()+"' where BusinessID="+bizId+"");
			stmt = conn.prepareStatement(szSql);
			stmt.executeUpdate();
			
			
			  
		} catch (SQLException e) {
			e.printStackTrace();
			
			return false;
		} 
		finally {
			DbUtil.DbCloseConnection(conn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;

	}
	//修改外呼策略重拨状态接口
	public boolean dmModifyOutboundSetting(int bizId,String MapColumns,StringBuffer err)
	{
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String xml="";
		try {
			conn =this.getDbConnection();
			
			String selectsql=String.format("select count(*) from HASYS_DM_SID where BusinessID="+bizId+" and State='启动'");
			stmt = conn.prepareStatement(selectsql);
			rs = stmt.executeQuery();
			int count=0;
			while(rs.next())
			{
				count=rs.getInt(1);
			}
			if (count>0) {
				
				err.append("有正在活动的共享批次，请停止后再添加结束码！");
				return false;
			}
			
			
			
			
			String szSql = String.format("update HASYS_DM_BIZADDSETTING set xml='"+MapColumns+"' where BusinessID="+bizId+"");
			stmt = conn.prepareStatement(szSql);
			stmt.executeUpdate();
			
			  
		} catch (SQLException e) {
			e.printStackTrace();
			err.append("失败！");
			return false;
		} 
		finally {
			DbUtil.DbCloseConnection(conn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;

	}
	
	
}
