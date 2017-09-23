package hiapp.modules.dmsetting.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.modules.dmsetting.DMEndCode;
import hiapp.system.dictionary.Dict;
import hiapp.system.dictionary.data.DictRepository;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
@Repository
public class DmBizEndCodeRepository extends BaseRepository {
	Connection conn = null;
	@Autowired
	 private DictRepository dictManager;
	//添加结束码
	public boolean dmAddBizEndCode(String mapColmns,int bizId,StringBuffer err)throws SQLException, IOException
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn =this.getDbConnection();
			//判断是否有活动的共享批次
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
			JsonArray jsonArray= new JsonParser().parse(mapColmns).getAsJsonArray();
			for(int i=0;i<jsonArray.size();i++)
			{
				JsonObject jsonObject=jsonArray.get(i).getAsJsonObject();
				//插入结束码表信息
				String szSql = String.format("INSERT INTO HASYS_DM_BIZENDCODE (BusinessId,CodeType,Code,Description) values("+bizId+",'"+jsonObject.get("endCodeType").getAsString()+"','"+jsonObject.get("endCode").getAsString()+"','"+jsonObject.get("desc").getAsString()+"')");
				stmt = conn.prepareStatement(szSql);
				stmt.executeUpdate();
				StringBuffer errMessage=new StringBuffer();
				//创建字典表
				String dictionary=String.format("select CLASSID from (select CLASSID from HASYS_DIC_CLASS order by CLASSID desc)  WHERE ROWNUM <=1");
				stmt = conn.prepareStatement(dictionary);
				rs = stmt.executeQuery();
				Integer CLASSID=0;
				while(rs.next())
				{
					CLASSID=rs.getInt("CLASSID");
				}
				CLASSID=CLASSID+1;
				dictManager.newDictionaryClass(CLASSID.toString(), bizId+jsonObject.get("endCodeType").getAsString(), "", errMessage);
				Dict dict=new Dict();
				dict.setClassId(CLASSID);
				dict.setClassName(jsonObject.get("endCodeType").getAsString());
				dict.setName(jsonObject.get("endCode").getAsString());
				
				dictManager.newDictionay(dict);
				add( bizId, jsonObject.get("endCodeType").getAsString(), jsonObject.get("endCode").getAsString(),jsonObject.get("desc").getAsString());
			}
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
	
	
	@SuppressWarnings("resource")
	//修改结束码
	public boolean dmModifyBizEndCode(DMEndCode dmEndCode, String codetype_old,String code_old,StringBuffer err) throws SQLException, IOException
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			
			conn =this.getDbConnection();
			//判断是否有正在活动的共享批次
			String selectsql=String.format("select count(*) from HASYS_DM_SID where BusinessID="+dmEndCode.getBizId()+" and State='启动'");
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
			//修改结束码信息
			String szSql = String.format("update HASYS_DM_BIZENDCODE set CodeType='"+dmEndCode.getEndCodeType()+"',Code='"+dmEndCode.getEndCode()+"',Description='"+dmEndCode.getDesc()+"' where BusinessId="+dmEndCode.getBizId()+" and CodeType='"+codetype_old+"' and Code='"+code_old+"'");
			stmt = conn.prepareStatement(szSql);
			stmt.executeUpdate();
			stmt.close();
			//修改字段表信息
			String updatecode=String.format("update HASYS_DIC_INDEX set NAME='"+dmEndCode.getEndCode()+"' where NAME='"+code_old+"' and classid=(select classid from HASYS_DIC_CLASS where classname='"+dmEndCode.getBizId()+dmEndCode.getEndCodeType()+"')");
			stmt = conn.prepareStatement(updatecode);
			stmt.executeUpdate();
			stmt.close();
			String updateDictionary=String.format("update HASYS_DIC_CLASS set CLASSNAME='"+dmEndCode.getBizId()+dmEndCode.getEndCodeType()+"' where CLASSNAME='"+dmEndCode.getBizId()+dmEndCode.getEndCodeType()+"'");
			stmt = conn.prepareStatement(updateDictionary);
			stmt.executeUpdate();
			modift(dmEndCode.getBizId(), dmEndCode.getEndCodeType(), dmEndCode.getEndCode(),codetype_old,code_old);
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
	
	@SuppressWarnings("resource")
	//修改addsetting的字符串
	public void modift(int bizid,String EndCodeType,String EndCode,String codetype_old,String code_old ) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count=0;
		String xml="";
		try {
			conn =this.getDbConnection();
			String szSql = String.format("select xml from HASYS_DM_BIZADDSETTING where BusinessID="+bizid+"");
			stmt = conn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				xml=rs.getString("xml");
				count+=1;
			}
			
			xml.replaceAll(codetype_old, EndCodeType);
			xml.replaceAll(code_old, EndCode);
			
		    String updatesql = "update HASYS_DM_BIZADDSETTING set XML='"+xml+"' where BusinessId="+bizid+"";
		    stmt = conn.prepareStatement(updatesql);
		    int i=stmt.executeUpdate();
		            
		} catch (SQLException e) {
			e.printStackTrace();
			
		} 
		finally {
			DbUtil.DbCloseConnection(conn);
			DbUtil.DbCloseExecute(stmt);
		}
	}
	
	
	@SuppressWarnings("resource")
	//添加addsettingjson字符串
	public void add(int bizid,String EndCodeType,String EndCode,String Description) throws SQLException, IOException
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count=0;
		String xml="";
		try{
			conn =this.getDbConnection();
			String sql="select xml from HASYS_DM_BIZOUTBOUNDSETTING where BusinessID="+bizid+"";
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				xml=rs.getString("xml");
				count+=1;
			}
			
			//判断是否存在以前的配置
			if (count==0) {
				// 鍒涘缓XML鏂囨。鏍�  
		        JsonObject jsonObject =new JsonObject();
		        JsonArray jsonArray_RedialState=new JsonArray();
		        
		        jsonObject.add("RedialState", jsonArray_RedialState);
		        
		        
		        JsonArray jsonArray_EndCode= new JsonArray();
		        JsonObject jsonobject_info=new JsonObject();
		        
		        JsonObject jsonObject_EndCode=new JsonObject();
		        jsonObject_EndCode.addProperty("endCodeType", EndCodeType);
		        jsonObject_EndCode.addProperty("endCode", EndCode);
		        jsonObject_EndCode.addProperty("endCodeDescription", Description);
		        jsonObject_EndCode.addProperty("redialStateName", "");
		        jsonArray_EndCode.add(jsonObject_EndCode);
		        jsonobject_info.add("dataInfo", jsonArray_EndCode);
		        
		        JsonArray jsonArray_show= new JsonArray();
		        jsonobject_info.add("dataShow", jsonArray_show);
		        JsonArray jsonArray_zong= new JsonArray();
		        jsonArray_zong.add(jsonobject_info);
		        jsonObject.add("EndCodeRedialStrategy", jsonArray_zong);
		        
		        
		        	
		            String insertsql = "INSERT INTO HASYS_DM_BIZOUTBOUNDSETTING (ID,BusinessId,XML) values(S_HASYS_DM_BIZOUTBOUNDSETTING.nextval,"+bizid+",'"+jsonObject.toString()+"')";
		            stmt = conn.prepareStatement(insertsql);
		            stmt.executeUpdate();
		        
			}else
			{
				
				JsonObject jsonObject= new JsonParser().parse(xml).getAsJsonObject();
				JsonArray jsonArray=jsonObject.get("EndCodeRedialStrategy").getAsJsonArray();
				JsonObject jsonObject_endChild=jsonArray.get(0).getAsJsonObject();
				JsonArray jsonArry_endChild=jsonObject_endChild.get("dataInfo").getAsJsonArray();
				JsonObject jsonObject_EndCode=new  JsonObject();
				
				
				jsonObject_EndCode.addProperty("endCodeType", EndCodeType);
		        jsonObject_EndCode.addProperty("endCode", EndCode);
		        jsonObject_EndCode.addProperty("endCodeDescription", Description);
		        jsonObject_EndCode.addProperty("redialStateName", "");
		        jsonArry_endChild.add(jsonObject_EndCode);
				
		            String updatesql = "update HASYS_DM_BIZOUTBOUNDSETTING set XML='"+jsonObject.toString()+"' where BusinessId="+bizid+"";
		            stmt = conn.prepareStatement(updatesql);
		            stmt.executeUpdate();
				
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} 
		finally {
			DbUtil.DbCloseConnection(conn);
			DbUtil.DbCloseExecute(stmt);
		}
		
		
		
	}
	
	//删除结束码信息
	public boolean dmDeleteBizEndCode(DMEndCode dmEndCode,StringBuffer err) throws SQLException
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn =this.getDbConnection();
			//判断是否有正在活动的共享批次
			String selectsql=String.format("select count(*) from HASYS_DM_SID where BusinessID="+dmEndCode.getBizId()+" and State='启动'");
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
			//删除结束码表信息
			String szSql = String.format("delete from HASYS_DM_BIZENDCODE where BusinessId="+dmEndCode.getBizId()+" and CodeType='"+dmEndCode.getEndCodeType() +"' and Code='"+dmEndCode.getEndCode()+"'");
			stmt = conn.prepareStatement(szSql);
			stmt.executeUpdate();
			stmt.close();
			//删除字段表信息
			String updatecode=String.format("delete HASYS_DIC_INDEX  where NAME='"+dmEndCode.getEndCode()+"' and classid=(select classid from HASYS_DIC_CLASS where classname='"+dmEndCode.getBizId()+dmEndCode.getEndCodeType()+"')");
			stmt = conn.prepareStatement(updatecode);
			stmt.executeUpdate();
			
			String deletedadd="select xml from HASYS_DM_BIZOUTBOUNDSETTING where businessid="+dmEndCode.getBizId()+"";
			stmt = conn.prepareStatement(deletedadd);
			rs = stmt.executeQuery();
			String xml="";
			while(rs.next())
			{
				xml=rs.getString(1);
			}
			
			JsonObject jsonObject= new JsonParser().parse(xml).getAsJsonObject();
			JsonArray jsonArray=jsonObject.get("EndCodeRedialStrategy").getAsJsonArray();
			JsonObject jsonObject_endChild=jsonArray.get(0).getAsJsonObject();
			JsonArray jsonArry_endChild=jsonObject_endChild.get("dataInfo").getAsJsonArray();
			for(int i=0;i<jsonArry_endChild.size();i++)
			{
				JsonObject jsonObject_endcode=jsonArry_endChild.get(i).getAsJsonObject();
				if (jsonObject_endcode.get(dmEndCode.getEndCodeType()).getAsString().equals(dmEndCode.getEndCodeType())||jsonObject_endcode.get(dmEndCode.getEndCode()).getAsString().equals(dmEndCode.getEndCode())) {
					jsonArray.remove(i);
				}
			}
			
			String updateadd="update HASYS_DM_BIZOUTBOUNDSETTING set xml='"+jsonObject.toString()+"' where businessid="+dmEndCode.getBizId()+"";
			stmt = conn.prepareStatement(updateadd);
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
	
	
	public List<DMEndCode> dmGetAllBizEndCode(String bizid,String Code)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<DMEndCode> listDmEndCodes=new ArrayList<DMEndCode>();
		try {
			conn =this.getDbConnection();
			String sql="select * from HASYS_DM_BIZENDCODE where BUSINESSID="+bizid+"";
			if(!Code.equals(""))
			{
				sql=sql+" and Code like'%"+Code+"%'";
			}
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while(rs.next())
			{
				
				DMEndCode dmEndCode=new DMEndCode();
				dmEndCode.setBizId(rs.getInt("BUSINESSID"));
				dmEndCode.setEndCodeType(rs.getString("CODETYPE"));
				dmEndCode.setEndCode(rs.getString("CODE"));
				dmEndCode.setDesc(rs.getString("DESCRIPTION"));
				listDmEndCodes.add(dmEndCode);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();	
			
		} finally {
			DbUtil.DbCloseConnection(conn);
			DbUtil.DbCloseExecute(stmt);
		}
		
		return listDmEndCodes;
	}
}
