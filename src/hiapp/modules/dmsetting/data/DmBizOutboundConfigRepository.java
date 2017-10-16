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
		JsonObject jsonObject=new JsonObject();
		try {
			conn =this.getDbConnection();
			String szSql = String.format("select xml from HASYS_DM_BIZOUTBOUNDSETTING where BusinessID="+bizId+"");
			stmt = conn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				xml=rs.getString("xml");
			}
			/*
			JsonObject newJsonObject=new JsonObject();
			
			jsonObject=new JsonParser().parse(xml).getAsJsonObject();
			
			
			
			JsonArray jsonArray_Endcode=jsonObject.get("EndCodeRedialStrategyM6").getAsJsonArray();
			JsonObject jsonObject_Endcode=new JsonObject();
			
			
			JsonArray jsonArray=new JsonArray();
			
			if (jsonArray_Endcode.size()>1) {
				jsonObject_Endcode=jsonArray_Endcode.get(0).getAsJsonObject();
				jsonArray.add(jsonObject_Endcode);
			}
			newJsonObject.add("dataShow", jsonArray);
			JsonArray jsonArray_dataInfo=new JsonArray();
			for(int i=0;i<jsonArray_Endcode.size();i++)
			{
				jsonArray_dataInfo.add(jsonArray_Endcode.get(i).getAsJsonObject());
			}
			newJsonObject.add("dataInfo", jsonArray_dataInfo);
			jsonObject.remove("EndCodeRedialStrategyM6");
			jsonObject.add("EndCodeRedialStrategyM6", newJsonObject);*/
			
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
			
			
			
			String xmlszSql = String.format("select xml from HASYS_DM_BIZOUTBOUNDSETTING where BusinessID="+bizId+"");
			stmt = conn.prepareStatement(xmlszSql);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				xml=rs.getString("xml");
			}
			int type=1;
			
			String szSql ="";
			if(type==1)
				{
				JsonObject jsonObject=new JsonParser().parse(xml).getAsJsonObject();
				jsonObject.remove("RedialState");
				JsonArray jsonArray_Map=new JsonParser().parse(MapColumns).getAsJsonArray();
				
				jsonObject.add("RedialState", jsonArray_Map);
				
				szSql = String.format("update HASYS_DM_BIZOUTBOUNDSETTING set xml='"+jsonObject.toString()+"' where BusinessID="+bizId+"");
			}else if(type==6)
			{
				szSql = String.format("update HASYS_DM_BIZOUTBOUNDSETTING set xml='"+MapColumns+"' where BusinessID="+bizId+"");
			}
			
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
			
			
			String xmlszSql = String.format("select xml from HASYS_DM_BIZOUTBOUNDSETTING where BusinessID="+bizId+"");
			stmt = conn.prepareStatement(xmlszSql);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				xml=rs.getString("xml");
			}

			JsonObject jsonObject=new JsonParser().parse(xml).getAsJsonObject();
			jsonObject.remove("EndCodeRedialStrategyM6");
			JsonArray jsonArray_Map=new JsonParser().parse(MapColumns).getAsJsonArray();
			
			jsonObject.add("EndCodeRedialStrategyM6", jsonArray_Map);
			String szSql = String.format("update HASYS_DM_BIZOUTBOUNDSETTING set xml='"+jsonObject.toString()+"' where BusinessID="+bizId+"");
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
