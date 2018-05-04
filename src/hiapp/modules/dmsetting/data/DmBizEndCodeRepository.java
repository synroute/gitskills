package hiapp.modules.dmsetting.data;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.impl.jam.xml.TunnelledException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.modules.dmsetting.DMEndCode;

import hiapp.system.dictionary.Dict;
import hiapp.system.dictionary.DictTreeBranch;
import hiapp.system.dictionary.data.DictRepository;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
@Repository
public class DmBizEndCodeRepository extends BaseRepository {
	
	@Autowired
	 private DictRepository dictManager;
	//添加结束码
	public boolean dmAddBizEndCode(String mapColmns,int bizId,StringBuffer err)throws SQLException, IOException
	{
		Connection conn = null;
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
			
			
			String typesql=String.format("select OutboundMddeId from HASYS_DM_Business where BusinessID="+bizId+"");
			stmt = conn.prepareStatement(typesql);
			rs = stmt.executeQuery();
			int type=0;
			while(rs.next())
			{
				type=rs.getInt(1);
			}
			
			JsonArray jsonArray= new JsonParser().parse(mapColmns).getAsJsonArray();
			for(int i=0;i<jsonArray.size();i++)
			{
				JsonObject jsonObject=jsonArray.get(i).getAsJsonObject();
				String desc="";
				//插入结束码表信息
				if(jsonObject.has("desc"))
				{
					desc=jsonObject.get("desc").getAsString();
				}
				String szSql = String.format("INSERT INTO HASYS_DM_BIZENDCODE (BusinessId,CodeType,Code,Description) values("+bizId+",'"+jsonObject.get("endCodeType").getAsString()+"','"+jsonObject.get("endCode").getAsString()+"','"+desc+"')");
				stmt = conn.prepareStatement(szSql);
				stmt.executeUpdate();
				StringBuffer errMessage=new StringBuffer();
				//创建字典表
				List<DictTreeBranch> listDictTreeBranch=new ArrayList<DictTreeBranch>();
			    dictManager.getDictionaryClassBySearchKey("数据管理结束码",listDictTreeBranch);
			    if(listDictTreeBranch.size()==0)
			    {
			    	String dictionary=String.format("select CLASSID from (select CLASSID from HASYS_DIC_CLASS order by CLASSID desc)  WHERE ROWNUM <=1");
					stmt = conn.prepareStatement(dictionary);
					rs = stmt.executeQuery();
					Integer CLASSID=0;
					while(rs.next())
					{
						CLASSID=rs.getInt("CLASSID");
					}
					CLASSID=CLASSID+1;
					dictManager.newDictionaryClass(CLASSID.toString(), "数据管理结束码", "", errMessage);
			    }
				List<Dict> list=new ArrayList<Dict>();
				dictManager.queryDictionary("业务"+bizId+"结束码",list);
				Integer CLASSID=dictManager.getClassIdByName("数据管理结束码");
				if(list.size()==0)
				{
					
					
					Dict dict=new Dict();
					dict.setClassId(CLASSID);
					dict.setDescription("业务"+bizId+"结束码");
					dict.setLevelCount(2);
					dict.setName("业务"+bizId+"结束码");
					dictManager.newDictionay(dict);
					dictManager.queryDictionary("业务"+bizId+"结束码",list);
				}
				String itemIdsql="select ITEMID from (select ITEMID from HASYS_DIC_ITEM order by ITEMID desc) WHERE ROWNUM <=1";
				stmt = conn.prepareStatement(itemIdsql);
				rs = stmt.executeQuery();
				Integer itemId=0;
				while(rs.next())
				{
					itemId=rs.getInt(1);
				}
				Integer dicid=list.get(0).getId();
				stmt.close();
				String psql="select ITEMID from HASYS_DIC_ITEM where ITEMTEXT='"+jsonObject.get("endCodeType").getAsString()+"' and dicid="+dicid+" and ITEMPARENT=-1";
				stmt = conn.prepareStatement(psql);
				rs = stmt.executeQuery();
				String pid="";
				while(rs.next())
				{
					pid=rs.getString(1);
				}
				if(pid.equals(""))
				{
					itemId=itemId+1;
					dictManager.insertItemsText(
							dicid.toString(), itemId.toString(), jsonObject.get("endCodeType").getAsString(),"-1");
					
					Integer itemIds=itemId+2;
					dictManager.insertItemsText(
							dicid.toString(), itemIds.toString(), jsonObject.get("endCode").getAsString(),itemId.toString());
				}else{
					itemId=itemId+1;
					dictManager.insertItemsText(
							dicid.toString(), itemId.toString(), jsonObject.get("endCode").getAsString(),pid);
				}
				
				
				
				if(type==3)
				{
					add( bizId, jsonObject.get("endCodeType").getAsString(), jsonObject.get("endCode").getAsString(),desc);
				}else if(type==6||type==5||type==2)
				{
					addModel6( bizId, jsonObject.get("endCodeType").getAsString(), jsonObject.get("endCode").getAsString(),desc,type);
				}else if(type==4) {
					addModel4( bizId, jsonObject.get("endCodeType").getAsString(), jsonObject.get("endCode").getAsString(),desc,type);
				}
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
		Connection conn = null;
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
			String updatecode=String.format("update HASYS_DIC_ITEM set ITEMTEXT='"+dmEndCode.getEndCodeType()+"' where ITEMTEXT='"+codetype_old+"' and ITEMPARENT=-1  and DICID=(select ID from HASYS_DIC_INDEX where NAME='业务"+dmEndCode.getBizId()+"结束码')");
			stmt = conn.prepareStatement(updatecode);
			stmt.executeUpdate();
			stmt.close();
			String updateDictionary=String.format("update HASYS_DIC_ITEM set ITEMTEXT='"+dmEndCode.getEndCode()+"' where ITEMTEXT='"+code_old+"' and ITEMPARENT!=-1  and DICID=(select ID from HASYS_DIC_INDEX where NAME='业务"+dmEndCode.getBizId()+"结束码')");
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
		Connection conn = null;
		ResultSet rs = null;
		int count=0;
		String xml="";
		try {
			conn =this.getDbConnection();
			String szSql = String.format("select xml from HASYS_DM_BIZOUTBOUNDSETTING where BusinessID="+bizid+"");
			stmt = conn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				xml=rs.getString("xml");
				count+=1;
			}
			
			xml.replaceAll(codetype_old, EndCodeType);
			xml.replaceAll(code_old, EndCode);
			
		    String updatesql = "update HASYS_DM_BIZOUTBOUNDSETTING set XML='"+xml+"' where BusinessId="+bizid+"";
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
		Connection conn = null;
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
			
			DbUtil.DbCloseExecute(stmt);
		}
		
		
		
	}
	

	//添加addsettingjson字符串
		public void addModel4(int bizid,String EndCodeType,String EndCode,String Description,int type) throws SQLException, IOException
		{
			Connection conn = null;
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
			        JsonObject jsonObject=new JsonObject();
			        
			        JsonArray jsonArray_EndCode= new JsonArray();
			        
			        JsonObject jsonObject_EndCode=new JsonObject();
			        
			        jsonObject_EndCode.addProperty("EndCodeType", EndCodeType);
			        jsonObject_EndCode.addProperty("EndCode", EndCode);
			        jsonObject_EndCode.addProperty("IsCustStop", "");
			        jsonObject_EndCode.addProperty("IsPhoneStop", "");
			        jsonObject_EndCode.addProperty("isPresetDial", "");
			        jsonArray_EndCode.add(jsonObject_EndCode);
			        jsonObject.add("EndCodeRedialStrategy", jsonArray_EndCode);
			        	
			        JsonObject jsonObject_MultiNumberDetail=new JsonObject();
			        JsonObject jsonObject_Attribute=new JsonObject();
			        jsonObject_Attribute.addProperty("StageCount", "");
			        jsonObject_Attribute.addProperty("LoopType", "");
			        jsonObject_Attribute.addProperty("StageDelayDays", "");
			        
			        jsonObject_MultiNumberDetail.add("Attribute", jsonObject_Attribute);
			        
			        JsonArray jsonArray_DayOrder=new JsonArray();
			        JsonObject jsonObject_DayOrder=new JsonObject();
			        jsonObject_DayOrder.addProperty("Index", "");
			        jsonObject_DayOrder.addProperty("PhoneName", "");
			        jsonObject_DayOrder.addProperty("DialCount", "");
			        jsonArray_DayOrder.add(jsonObject_DayOrder);
			        jsonObject_MultiNumberDetail.add("DayOrder", jsonArray_DayOrder);
			        
			        jsonObject.add("MultiNumberDetail", jsonObject_MultiNumberDetail);
			        
			        
			        PreparedStatement stat=conn.prepareStatement("INSERT INTO HASYS_DM_BIZOUTBOUNDSETTING (ID,BusinessId,XML) values(S_HASYS_DM_BIZOUTBOUNDSETTING.nextval,"+bizid+",?)");

						String clobContent = jsonObject.toString();  
					     StringReader reader = new StringReader(clobContent);  
					     stat.setCharacterStream(1, reader, clobContent.length());
					     stat.executeUpdate();
			            
			            
				}else
				{
					
					
					JsonObject jsonObject=new JsonParser().parse(xml).getAsJsonObject();
					
					JsonArray jsonArray=jsonObject.get("EndCodeRedialStrategy").getAsJsonArray();
					JsonObject jsonObject_EndCode=new JsonObject();
					
					 	jsonObject_EndCode.addProperty("EndCodeType", EndCodeType);
				        jsonObject_EndCode.addProperty("EndCode", EndCode);
				        jsonObject_EndCode.addProperty("IsCustStop", "");
				        jsonObject_EndCode.addProperty("IsPhoneStop", "");
				        jsonObject_EndCode.addProperty("isPresetDial", "");
				       
				        jsonArray.add(jsonObject_EndCode);
					
				        PreparedStatement stat=conn.prepareStatement("update HASYS_DM_BIZOUTBOUNDSETTING set XML=? where BusinessId="+bizid+"");
			            String clobContent = jsonObject.toString();  
					     StringReader reader = new StringReader(clobContent);  
					     stat.setCharacterStream(1, reader, clobContent.length());
					     stat.executeUpdate();
					
				}
				
				
			} catch (SQLException e) {
				e.printStackTrace();
				
			} 
			finally {
				
				DbUtil.DbCloseExecute(stmt);
			}
			
			
			
		}
	
	
	
	//添加addsettingjson字符串
		public void addModel6(int bizid,String EndCodeType,String EndCode,String Description,int type) throws SQLException, IOException
		{
			Connection conn = null;
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
			        
			        
			        JsonArray jsonArray_EndCode= new JsonArray();
			        
			        JsonObject jsonObject_EndCode=new JsonObject();
			        
			        jsonObject_EndCode.addProperty("EndCodeType", EndCodeType);
			        jsonObject_EndCode.addProperty("EndCode", EndCode);
			        jsonObject_EndCode.addProperty("IsCustStop", "");
			        if (type==6) {
				        jsonObject_EndCode.addProperty("isPresetDial", "");
				        jsonObject_EndCode.addProperty("IsPhoneStop", "");
					}
			        
			        jsonObject_EndCode.addProperty("RedialMinutes", "");
			        jsonObject_EndCode.addProperty("RedialCount", "");
			        
			        jsonArray_EndCode.add(jsonObject_EndCode);
			        
			        	
			        PreparedStatement stat=conn.prepareStatement("INSERT INTO HASYS_DM_BIZOUTBOUNDSETTING (ID,BusinessId,XML) values(S_HASYS_DM_BIZOUTBOUNDSETTING.nextval,"+bizid+",?)");
			           
			            String clobContent =jsonArray_EndCode.toString();  
					     StringReader reader = new StringReader(clobContent);  
					     stat.setCharacterStream(1, reader, clobContent.length());
					     stat.executeUpdate();
			        
				}else
				{
					
					
					JsonArray jsonArray=new JsonParser().parse(xml).getAsJsonArray();
					
					JsonObject jsonObject_EndCode=new  JsonObject();
					
					
					 jsonObject_EndCode.addProperty("EndCodeType", EndCodeType);
				        jsonObject_EndCode.addProperty("EndCode", EndCode);
				        jsonObject_EndCode.addProperty("IsCustStop", "");
				        if (type==6) {
					        jsonObject_EndCode.addProperty("isPresetDial", "");
					        jsonObject_EndCode.addProperty("IsPhoneStop", "");
						}
				        jsonObject_EndCode.addProperty("RedialMinutes", "");
				        jsonObject_EndCode.addProperty("RedialCount", "");
			        jsonArray.add(jsonObject_EndCode);
					
			        PreparedStatement stat=conn.prepareStatement("update HASYS_DM_BIZOUTBOUNDSETTING set XML=? where BusinessId="+bizid+"");
			            String clobContent =jsonArray.toString();  
					     StringReader reader = new StringReader(clobContent);  
					     stat.setCharacterStream(1, reader, clobContent.length());
					     stat.executeUpdate();
					
				}
				
				
			} catch (SQLException e) {
				e.printStackTrace();
				
			} 
			finally {
				
				DbUtil.DbCloseExecute(stmt);
			}
			
			
			
		}
	
	
	//删除结束码信息
	public boolean dmDeleteBizEndCode(DMEndCode dmEndCode,StringBuffer err) throws SQLException
	{
		Connection conn = null;
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

			String typesql=String.format("select OutboundMddeId from HASYS_DM_Business where BusinessID="+dmEndCode.getBizId()+"");
			stmt = conn.prepareStatement(typesql);
			rs = stmt.executeQuery();
			int type=0;
			while(rs.next())
			{
				type=rs.getInt(1);
			}
			
			//删除结束码表信息
			String szSql = String.format("delete from HASYS_DM_BIZENDCODE where BusinessId="+dmEndCode.getBizId()+" and CodeType='"+dmEndCode.getEndCodeType() +"' and Code='"+dmEndCode.getEndCode()+"'");
			stmt = conn.prepareStatement(szSql);
			stmt.executeUpdate();
			stmt.close();
			//删除字段表信息
			String updatecode=String.format("delete HASYS_DIC_ITEM  where ITEMTEXT='"+dmEndCode.getEndCode()+"' and ITEMPARENT!=-1  and DICID=(select ID from HASYS_DIC_INDEX where NAME='业务"+dmEndCode.getBizId()+"结束码')");
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
			
			String updateadd="";
			if(type==3)
			{
				JsonObject jsonObject= new JsonParser().parse(xml).getAsJsonObject();
				JsonArray jsonArray=jsonObject.get("EndCodeRedialStrategy").getAsJsonArray();
				JsonObject jsonObject_endChild=jsonArray.get(0).getAsJsonObject();
				JsonArray jsonArry_endChild=jsonObject_endChild.get("dataInfo").getAsJsonArray();
				for(int i=0;i<jsonArry_endChild.size();i++)
				{
					JsonObject jsonObject_endcode=jsonArry_endChild.get(i).getAsJsonObject();
					if (jsonObject_endcode.get("endCodeType").getAsString().equals(dmEndCode.getEndCodeType())||jsonObject_endcode.get("endCode").getAsString().equals(dmEndCode.getEndCode())) {
						jsonArry_endChild.remove(i);
					}
				}
				
				//updateadd="update HASYS_DM_BIZOUTBOUNDSETTING set xml='"+jsonObject.toString()+"' where businessid="+dmEndCode.getBizId()+"";
				
				PreparedStatement stat=conn.prepareStatement("update HASYS_DM_BIZOUTBOUNDSETTING set xml=? where businessid="+dmEndCode.getBizId()+"");
				
				String clobContent = jsonObject.toString();  
			     StringReader reader = new StringReader(clobContent);  
			     stat.setCharacterStream(1, reader, clobContent.length());
			     stat.executeUpdate();
			}else if(type==6||type==5)
			{
				
				JsonArray jsonArray=new JsonParser().parse(xml).getAsJsonArray();
				for(int i=0;i<jsonArray.size();i++)
				{
					JsonObject jsonObject_endcode=jsonArray.get(i).getAsJsonObject();
					if (jsonObject_endcode.get("EndCodeType").getAsString().equals(dmEndCode.getEndCodeType())||jsonObject_endcode.get("EndCode").getAsString().equals(dmEndCode.getEndCode())) {
						jsonArray.remove(i);
					}
				}
				
				//updateadd="update HASYS_DM_BIZOUTBOUNDSETTING set xml='"+jsonArray.toString()+"' where businessid="+dmEndCode.getBizId()+"";

				PreparedStatement stat=conn.prepareStatement("update HASYS_DM_BIZOUTBOUNDSETTING set xml=? where businessid="+dmEndCode.getBizId()+"");
				
				String clobContent = jsonArray.toString();  
			     StringReader reader = new StringReader(clobContent);  
			     stat.setCharacterStream(1, reader, clobContent.length());
			     stat.executeUpdate();
			}else if(type==4)
			{
				JsonObject jsonObject= new JsonParser().parse(xml).getAsJsonObject();
				JsonArray jsonArray=jsonObject.get("EndCodeRedialStrategy").getAsJsonArray();
				
				for(int i=0;i<jsonArray.size();i++)
				{
					JsonObject jsonObject_endcode=jsonArray.get(i).getAsJsonObject();
					if (jsonObject_endcode.get("EndCodeType").getAsString().equals(dmEndCode.getEndCodeType())||jsonObject_endcode.get("EndCode").getAsString().equals(dmEndCode.getEndCode())) {
						jsonArray.remove(i);
					}
				}
				
				//updateadd="update HASYS_DM_BIZOUTBOUNDSETTING set xml='"+jsonObject.toString()+"' where businessid="+dmEndCode.getBizId()+"";
				
				PreparedStatement stat=conn.prepareStatement("update HASYS_DM_BIZOUTBOUNDSETTING set xml=? where businessid="+dmEndCode.getBizId()+"");
				
				String clobContent = jsonObject.toString();  
			     StringReader reader = new StringReader(clobContent);  
			     stat.setCharacterStream(1, reader, clobContent.length());
			     stat.executeUpdate();
			}
			
			
			
			/*stmt = conn.prepareStatement(updateadd);
			stmt.executeUpdate();*/
			
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
		Connection conn = null;
		
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
	
	
	//删除结束码信息
		public boolean dmGetPersetByEndCode(DMEndCode dmEndCode,StringBuffer err) throws SQLException
		{
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			boolean isperset=false;
			try {
				conn =this.getDbConnection();
				

				String typesql=String.format("select OutboundMddeId from HASYS_DM_Business where BusinessID="+dmEndCode.getBizId()+"");
				stmt = conn.prepareStatement(typesql);
				rs = stmt.executeQuery();
				int type=0;
				while(rs.next())
				{
					type=rs.getInt(1);
				}
				
				
				String deletedadd="select xml from HASYS_DM_BIZOUTBOUNDSETTING where businessid="+dmEndCode.getBizId()+"";
				stmt = conn.prepareStatement(deletedadd);
				rs = stmt.executeQuery();
				String xml="";
				while(rs.next())
				{
					xml=rs.getString(1);
				}
				
				
				if(type==3)
				{
					JsonObject jsonObject= new JsonParser().parse(xml).getAsJsonObject();
					JsonArray jsonArray=jsonObject.get("EndCodeRedialStrategy").getAsJsonArray();
					JsonObject jsonObject_endChild=jsonArray.get(0).getAsJsonObject();
					JsonArray jsonArry_endChild=jsonObject_endChild.get("dataInfo").getAsJsonArray();
					for(int i=0;i<jsonArry_endChild.size();i++)
					{
						JsonObject jsonObject_endcode=jsonArry_endChild.get(i).getAsJsonObject();
						if (jsonObject_endcode.get("endCodeType").getAsString().equals(dmEndCode.getEndCodeType())||jsonObject_endcode.get("endCode").getAsString().equals(dmEndCode.getEndCode())) {
							if(jsonObject_endcode.get("redialStateName").getAsString().equals("预约"))
							{
								isperset=true;
							}
						}
					}
					
				}else if(type==6||type==5||type==2)
				{
					
					JsonArray jsonArray=new JsonParser().parse(xml).getAsJsonArray();
					for(int i=0;i<jsonArray.size();i++)
					{
						JsonObject jsonObject_endcode=jsonArray.get(i).getAsJsonObject();
						if (jsonObject_endcode.get("EndCodeType").getAsString().equals(dmEndCode.getEndCodeType())||jsonObject_endcode.get("EndCode").getAsString().equals(dmEndCode.getEndCode())) {
							if(jsonObject_endcode.get("isPresetDial").getAsString().equals("true"))
							{
								isperset=true;
							}
						}
					}
					
					
				}else if(type==4)
				{
					JsonObject jsonObject= new JsonParser().parse(xml).getAsJsonObject();
					JsonArray jsonArray=jsonObject.get("EndCodeRedialStrategy").getAsJsonArray();
					
					for(int i=0;i<jsonArray.size();i++)
					{
						JsonObject jsonObject_endcode=jsonArray.get(i).getAsJsonObject();
						if (jsonObject_endcode.get("EndCodeType").getAsString().equals(dmEndCode.getEndCodeType())||jsonObject_endcode.get("EndCode").getAsString().equals(dmEndCode.getEndCode())) {
							if(jsonObject_endcode.get("isPresetDial").getAsString().equals("true"))
							{
								isperset=true;
							}
						}
					}
					
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
			
			
			return isperset;
			
			
			
		}
	
	
	
}
