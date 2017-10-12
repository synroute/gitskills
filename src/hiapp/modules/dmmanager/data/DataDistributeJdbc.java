package hiapp.modules.dmmanager.data;

import hiapp.modules.dm.singlenumbermode.SingleNumberOutboundDataManage;
import hiapp.modules.dmmanager.TreePool;
import hiapp.modules.dmmanager.UserItem;
import hiapp.modules.dmmanager.bean.DistributeTemplate;
import hiapp.modules.dmmanager.bean.OutputFirstRow;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.idfactory.IdFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Repository
public class DataDistributeJdbc extends BaseRepository{
	@Autowired
	private IdFactory idfactory;
	@Autowired
	private SingleNumberOutboundDataManage singleNumberOutboundDataManage;
	/**
	 * 获取所有分配模板
	 * @param bizId
	 * @return
	 */
	public List<DistributeTemplate> getAllDisTemplate(Integer bizId){
		List<DistributeTemplate> disTempateList=new ArrayList<DistributeTemplate>();
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn=this.getDbConnection();
			String sql="select id,TEMPLATEID,BUSINESSID,NAME,DESCRIPTION from hasys_dm_biztemplatedistview where BUSINESSID=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,bizId);
			rs=pst.executeQuery();
			while(rs.next()){
				DistributeTemplate disTemplate=new DistributeTemplate();
				disTemplate.setId(rs.getInt(1));
				disTemplate.setTemplateId(rs.getInt(2));
				disTemplate.setBizId(rs.getInt(3));
				disTemplate.setName(rs.getString(4));
				disTemplate.setDescription(rs.getString(5));
				disTempateList.add(disTemplate);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return disTempateList;
		
	}
	/**
	 * 获取前台展示的字段
	 * @param bizId
	 * @param templateId
	 * @return
	 */
	public List<OutputFirstRow> getAllColumn(Integer bizId,Integer templateId){
		List<OutputFirstRow> columns=new ArrayList<OutputFirstRow>();
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn=this.getDbConnection();
			String configJson=getConfigJson(bizId, templateId);
			JsonObject jsonObject= new JsonParser().parse(configJson).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("Template").getAsJsonArray();
			for (int i = 0; i < dataArray.size(); i++) {
				OutputFirstRow firstRow=new OutputFirstRow();
				String title=null; 
				String filed=null;
				if(!dataArray.get(i).getAsJsonObject().get("Title").isJsonNull()){
					title=dataArray.get(i).getAsJsonObject().get("Title").getAsString();
					if(!"".equals(title)){
						firstRow.setTitle(title);
					}
				}
				if(!dataArray.get(i).getAsJsonObject().get("ColumnName").isJsonNull()){
					filed=dataArray.get(i).getAsJsonObject().get("ColumnName").getAsString();
					if(!"".equals(filed)){
						firstRow.setField(filed);
					}
				}
				columns.add(firstRow);		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return columns;
	}
	
	/**
	 * 根据时间获取未分配数据并保存到临时表中
	 * @param userId
	 * @param bizId
	 * @param templateId
	 * @param startTime
	 * @param endTime
	 * @param num
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings({ "unused", "resource", "unchecked" })
	public void getNotDisDatByTime(String userId,Integer bizId,Integer templateId,String startTime,String endTime){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		List<String> workSheetNameList=new ArrayList<String>();
		String dataPoolName="HAU_DM_B"+bizId+"C_POOL";
		String tempTableName="HAU_DM_"+bizId+"_"+userId;
		try {
			conn=this.getDbConnection();
			String selectTable="select table_name from user_tables where table_name=?";
			pst=conn.prepareStatement(selectTable);
			pst.setString(1,tempTableName);
			rs=pst.executeQuery();
			String dbTableName=null;
			while(rs.next()){
				dbTableName=rs.getString(1);
			}
			String configJson=getConfigJson(bizId, templateId);
			JsonObject jsonObject= new JsonParser().parse(configJson).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("Template").getAsJsonArray();
			String getDataSql2="b.id,0,b.IID,b.CID,b.DataPoolIDCur,b.AreaCur,";
			String insertSql="insert into "+tempTableName+"(TEMPID,IID,CID,DATAPOOLIDCUR,AREACUR, ";
			String createTableSql="create table "+tempTableName+"(TEMPID NUMBER,IFCHECKD NUMBER,IID VARCHAR2(50),CID VARCHAR2(50),DATAPOOLIDCUR NUMBER,AREACUR BUMBER,";
			
			for (int i = 0; i < dataArray.size(); i++) {
				String workSheetName=null ;
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetName").isJsonNull()){
					workSheetName=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
					if(!"".equals(workSheetName)){
						workSheetNameList.add(workSheetName);
					}
					
				}
				
			}
			List<String> newList=new ArrayList<String>(new HashSet(workSheetNameList));
			for (int i = 0; i < dataArray.size(); i++) {
				String columnName=null ;
				String workSheetName=null;
				String workSheetId=null;
				if(!dataArray.get(i).getAsJsonObject().get("ColumnName").isJsonNull()){
					columnName=dataArray.get(i).getAsJsonObject().get("ColumnName").getAsString();
				}
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetName").isJsonNull()){
					workSheetName=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
				}
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetId").isJsonNull()){
					workSheetId=dataArray.get(i).getAsJsonObject().get("WorkSheetId").getAsString();
				}
				String suffix=workSheetName.substring(workSheetName.lastIndexOf("_")+1);
				if("pool".equals(suffix.toLowerCase())){
					continue;
				}
				for (int j = 0; j < newList.size(); j++) {
					String asName="a"+j+".";
					if(newList.get(j).equals(workSheetName)){
						if(!"ID".equals(columnName.toUpperCase())&&!"IID".equals(columnName.toUpperCase())&&!"CID".equals(columnName.toUpperCase())&&!"DATAPOOLIDCUR".equals(columnName.toUpperCase())&&!"AREACUR".equals(columnName.toUpperCase())){
							getDataSql2+=asName+columnName+",";
							insertSql+=columnName+",";
							createTableSql+=getTempColumnType(columnName,workSheetId);
							break;
						}
						
					}
				}
				
			}
			getDataSql2=getDataSql2+" from "+dataPoolName+" t left join ";
			
			for (int i = 0; i < newList.size(); i++) {
				String workSheetName=newList.get(i);
				String suffix=workSheetName.substring(workSheetName.lastIndexOf("_")+1);
				if("pool".equals(suffix.toLowerCase())){
					continue;
				}
				String asName="a"+i;
				getDataSql2+=newList.get(i)+" asName"+" on t.IID="+asName+".IID and t.CID="+asName+".CID left join ";
			}
			getDataSql2=getDataSql2.substring(0,getDataSql2.lastIndexOf("left join"))+" where ";
			getDataSql2=getDataSql2+" t.CID in(select b.CID from HASYS_DM_DID a,"+dataPoolName+" b where a.DID=b.SourceID and a.BUSINESSID=? and a.ModifyTime>(?,'yyyy-mm-dd') and a.ModifyTime<(?,'yyyy-mm-dd') and b.AreaCur=0 and b.DataPoolIDCur=(select m.id from HASYS_DM_DATAPOOL m where m.BusinessID=? and m.DataPoolName=?)  )";
			createTableSql=createTableSql.substring(0,createTableSql.length()-1);
			insertSql=insertSql.substring(0,insertSql.length()-1)+")"+getDataSql2;
			if(dbTableName==null){
				pst=conn.prepareStatement(createTableSql);
				pst.executeUpdate();
			}
			//删除数据
			String delteSql="delete from "+tempTableName;
			pst=conn.prepareStatement(delteSql);
			pst.executeUpdate();
			//向临时表添加数据
			pst=conn.prepareStatement(insertSql);
			pst.setInt(1,bizId);
			pst.setString(2,startTime);
			pst.setString(3,endTime);
			pst.setInt(4,bizId);
			pst.setString(5,userId);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
	}
	/**
	 * 从临时表里面查询出要展示的数据
	 * @param bizId
	 * @param templateId
	 * @param userId
	 * @param num
	 * @param pageSize
	 */
	@SuppressWarnings("resource")
	public Map<String,Object> getTempNotDisData(Integer bizId,Integer templateId ,String userId,Integer num,Integer pageSize){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String tempTableName="HAU_DM_"+bizId+"_"+userId;
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		List<String> workSheetNameList=new ArrayList<String>();
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
		try {
			conn=this.getDbConnection();
			String configJson=getConfigJson(bizId, templateId);
			JsonObject jsonObject= new JsonParser().parse(configJson).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("Template").getAsJsonArray();
			String getDataSql1="select TEMPID,IID,CID,";
			String getDataSql3="select TEMPID,IID,CID,";
			for (int i = 0; i < dataArray.size(); i++) {
				String workSheetName=null ;
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetName").isJsonNull()){
					workSheetName=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
					if(!"".equals(workSheetName)){
						workSheetNameList.add(workSheetName);
					}
					
				}
				
			}
			List<String> newList=new ArrayList<String>(new HashSet(workSheetNameList));
			for (int i = 0; i < dataArray.size(); i++) {
				String columnName=null ;
				String workSheetName=null;
				String workSheetId=null;
				if(!dataArray.get(i).getAsJsonObject().get("ColumnName").isJsonNull()){
					columnName=dataArray.get(i).getAsJsonObject().get("ColumnName").getAsString();
				}
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetName").isJsonNull()){
					workSheetName=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
				}
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetId").isJsonNull()){
					workSheetId=dataArray.get(i).getAsJsonObject().get("WorkSheetId").getAsString();
				}
				String suffix=workSheetName.substring(workSheetName.lastIndexOf("_")+1);
				if("pool".equals(suffix.toLowerCase())){
					continue;
				}
				for (int j = 0; j < newList.size(); j++) {
					String asName="a"+j+".";
					if(newList.get(j).equals(workSheetName)){
						if(!"IID".equals(columnName.toUpperCase())&&!"CID".equals(columnName.toUpperCase())){
							getDataSql1+=getDataType(columnName,workSheetId,asName)+",";
							getDataSql3+=getDataType(columnName,workSheetId,asName)+",";
							break;
						}
						
					}
				}
				
			}
			getDataSql1=getDataSql1.substring(0,getDataSql1.length()-1)+" from ("+getDataSql3+" rownum rn from "+tempTableName+" where rownum<?)  where rn>=?";
			//查询出数据
			pst=conn.prepareStatement(getDataSql1);
			pst.setInt(1,endNum);
			pst.setInt(2,startNum);
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("tempId",rs.getObject(1));
				map.put("IID",rs.getObject(2));
				map.put("CID",rs.getObject(3));
				for (int i = 0; i < dataArray.size(); i++) {
					String key=null;
					if(!dataArray.get(i).getAsJsonObject().get("ColumnName").isJsonNull()){
						key=dataArray.get(i).getAsJsonObject().get("ColumnName").getAsString();
						if(!"".equals(key)){
							map.put(key,rs.getObject(key));
						}
					}
				}
				
				dataList.add(map);
			}
			//查询临时表数据总数
			String getCountSql="select count(*) from "+tempTableName;
			pst=conn.prepareStatement(getCountSql);
			rs=pst.executeQuery();
			Integer total=null;
			while(rs.next()){
				total=rs.getInt(1);
			}
			resultMap.put("total",total);
			resultMap.put("rows", dataList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	/**
	 * 得到根目录
	 * @param userId
	 * @param bizId
	 * @return
	 */
	public TreePool getRootPool(String userId,Integer bizId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		TreePool treePool=new TreePool();
		try {
			conn=this.getDbConnection();
			String sql="select ID,DATAPOOLNAME,PID from HASYS_DM_DATAPOOL where BusinessID=? and DATAPOOLNAME=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, bizId);
			pst.setString(2, userId);
			rs=pst.executeQuery();
			while(rs.next()){
				treePool.setId(rs.getInt(1));
				treePool.setDataPoolName(rs.getString(2));
				treePool.setPid(rs.getInt(3));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return treePool;
	}
	
	public List<UserItem> getChildrenPool(Integer bizId,Integer pid){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<UserItem>	userItems=new ArrayList<UserItem>();
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		try {
			conn=this.getDbConnection();
			String sql="select a.ID,a.DATAPOOLNAME,a.POOLTOPLIMIT-(select nvl(sum(case when b.datapoolidcur = a.id then 1 else 0 end),0) from "+poolName+" b) topLimit from HASYS_DM_DATAPOOL a where a.BusinessID=? and a.pid=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,bizId);
			pst.setInt(2, pid);
			rs=pst.executeQuery();
			while(rs.next()){
				UserItem userItem=new UserItem();
				userItem.setId(String.valueOf(rs.getInt(1)));
				userItem.setText(rs.getString(2));;
				userItem.setTopLimit(rs.getInt(3));
				userItems.add(userItem);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return userItems;
	}
	/**
	 * 获取前台tree
	 * @param bizId
	 * @param userId
	 * @return
	 */
	public UserItem getTreePoolByBizId(Integer bizId,String userId){
		TreePool treePool=getRootPool(userId, bizId);
		UserItem userItem=new UserItem();
		userItem.setId(String.valueOf(treePool.getId()));
		userItem.setText(treePool.getDataPoolName());
		List<UserItem>  userItemList=getChildrenPool(bizId, treePool.getId());
		userItem.setChildren(userItemList);
		return userItem;
	}
	
	/**
	 * 递归方法
	 * @param userTreeBranch
	 * @param bizId
	 */
	/*public void addChildren(UserItem userTreeBranch,Integer bizId) {
		    List<UserItem> listChildrenBranchs = new ArrayList<UserItem>();
		    Integer pid=Integer.valueOf(userTreeBranch.getId());
		    List<TreePool>  treePoolList=getChildrenPool(bizId, pid);
		    for (int ii = 0; ii < treePoolList.size(); ii++) {
				TreePool TreePoolBranch = treePoolList.get(ii);
				UserItem treeBranch = new UserItem();
				treeBranch.setId(String.valueOf(TreePoolBranch.getId()));
				treeBranch.setText(TreePoolBranch.getDataPoolName());
				treeBranch.setState("");
				treeBranch.setDicId(TreePoolBranch.getId());
				treeBranch.setItemText(TreePoolBranch.getDataPoolName());
				treeBranch.setTopLimit(TreePoolBranch.getTopLimit());
				if(treeBranch.getId()==null){
					continue;
				}
				addChildren(treeBranch,bizId);
				listChildrenBranchs.add(treeBranch);
				
			}
		    userTreeBranch.setChildren(listChildrenBranchs);
		}*/
	/**
	 * 保存数据到正式表中
	 * @param bizId
	 * @param userId
	 * @param disName
	 * @param description
	 * @param dataPoolList
	 * @return
	 */
	@SuppressWarnings({ "unused","resource" })
	public Map<String,Object> saveDistributeDataToDB(Integer bizId,String userId,String disName,String description,List<Map<String,Object>>  dataPoolList){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
		String disBatchId=idfactory.newId("DM_DID");//分配号
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		String orePoolName="HAU_DM_B"+bizId+"C_POOL_ORE";
		String tempTableName="HAU_DM_"+bizId+"_"+userId;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			for(int i = 0; i < dataPoolList.size(); i++) {
				Integer dataPoolId=(Integer) dataPoolList.get(i).get("dataPoolId");
				Integer disNum=(Integer) dataPoolList.get(i).get("disNum");
				String updatePoolSql="update "+poolName+" a set (sourceID,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ModifyUserID,ModifyTime)"
						+ " = (select '"+disBatchId+"',DATAPOOLIDCUR,'"+dataPoolId+"',AreaCur,0,'"+userId+"',sysdate from "+tempTableName+" b where a.IID=b.IID AND a.CID=b.CID and b.ifchecked=1 and rownum<="+disNum+" ORDER BY b.TEMPID ASC)";
				pst=conn.prepareStatement(updatePoolSql);
				pst.executeUpdate();
				String insertOrePoolSql="insert into "+orePoolName+" a(id,SourceID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime)"+
										" select tempId,'"+disBatchId+"',IID,CID,'分配',DataPoolIDCur,'"+dataPoolId+"',AreaCur,0,0,'"+userId+"',sysdate from "+tempTableName+" b where b.ifchecked=1 and rownum<="+disNum+" ORDER BY b.TEMPID ASC";
				pst=conn.prepareStatement(insertOrePoolSql);
				pst.executeUpdate();
				String deleteSql=" delete from "+tempTableName+" a where a.tempId in(select b.tempId from "+tempTableName+" b where b.ifchecked=1 and rownum<="+disNum+" order by b.tempId asc)";
				pst=conn.prepareStatement(deleteSql);
				pst.executeUpdate();
				
			}
			String insertDisBatchSql="insert into HASYS_DM_DID(id,BusinessID,DID,DistributionName,ModifyUserID,ModifyTime,Description) values(S_HASYS_DM_DID.nextval,?,?,?,?,sysdate,?)";
			pst=conn.prepareStatement(insertDisBatchSql);
			pst.setInt(1,bizId);
			pst.setString(2,disBatchId);
			pst.setString(3,disName);
			pst.setString(4,userId);
			pst.setString(5,description);
			pst.executeUpdate();
			resultMap.put("result",true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("result",false);
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return resultMap;
	}
	/**
	 * 保存共享数据
	 * @param bizId
	 * @param userId
	 * @param shareName
	 * @param description
	 * @param startTime
	 * @param endTime
	 * @param dataPoolIds
	 * @param dataPoolNames
	 */
	@SuppressWarnings("resource")
	public Map<String,Object> saveShareDataToDB(Integer bizId,String userId,String shareName,String description,String startTime,String endTime,String dataPoolIds,String dataPoolNames,Integer model,Integer ifAppend ){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		String orePoolName="HAU_DM_B"+bizId+"C_POOL_ORE";
		String tempTableName="HAU_DM_"+bizId+"_"+userId;
		//单号码重拨表
		String datamTableName="HAU_DM_B"+bizId+"C_DATAM3";
		//
		String hisTableName= "HAU_DM_B"+bizId+"C_DATAM3_HIS";
		//共享批次
		String shareId = idfactory.newId("DM_SID");
		String[] arrDataPoolId=dataPoolIds.split(",");
		String[] arrDataPoolName=dataPoolNames.split(",");
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String getDataSourceSql="select a.id from HASYS_DM_DATAPOOL a where a.BusinessID=? and DataPoolName =?";
			pst=conn.prepareStatement(getDataSourceSql);
			pst.setInt(1,bizId);
			pst.setString(2,userId);
			rs=pst.executeQuery();
			Integer dataPoolId=null;
			while(rs.next()){
				dataPoolId=rs.getInt(1);
			}
			String updatePoolSql="update "+poolName+" a set (sourceID,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ModifyUserID,ModifyTime)"
					+ " = (select '"+shareId+"',DATAPOOLIDCUR,'"+dataPoolId+"',AreaCur,1,'"+userId+"',sysdate from "+tempTableName+" b where a.IID=b.IID AND a.CID=b.CID and b.ifchecked=1 ORDER BY b.TEMPID ASC)";
			pst=conn.prepareStatement(updatePoolSql);
			pst.executeUpdate();
			String insertOrePoolSql="insert into "+orePoolName+" a(id,SourceID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime)"+
									" select tempId,'"+shareId+"',IID,CID,'共享',DataPoolIDCur,'"+dataPoolId+"',AreaCur,1,0,'"+userId+"',sysdate from "+tempTableName+" b where b.ifchecked=1  ORDER BY b.TEMPID ASC";
			pst=conn.prepareStatement(insertOrePoolSql);
			pst.executeUpdate();
			if(model==3){
				String insertDatamSql="insert into "+datamTableName+"a(ID,BUSINESSID,SHAREID,IID,CID,STATE,MODIFYID,MODIFYUSERID,MODIFYTIME) select tempId,"+bizId+",'"+shareId+"'IID,CID,'共享创建',0,sysdate from "+tempTableName+"b "+
									  "where b.ifchecked=1  ORDER BY b.TEMPID ASC";
				pst=conn.prepareStatement(insertDatamSql);
				pst.executeUpdate();
				String insertHisDatamSql="insert into "+hisTableName+"a(ID,BUSINESSID,SHAREID,IID,CID,STATE,MODIFYID,MODIFYUSERID,MODIFYTIME) select tempId,"+bizId+",'"+shareId+"'IID,CID,'共享创建',0,sysdate from "+tempTableName+"b "+
										 "where b.ifchecked=1  ORDER BY b.TEMPID ASC";
				pst=conn.prepareStatement(insertHisDatamSql);
				pst.executeUpdate();
			}
			String deleteTempSql=" delete from "+tempTableName+" a where b.ifchecked=1";
			pst=conn.prepareStatement(deleteTempSql);
			pst.executeUpdate();
			String insertShareSql=null;
			if(startTime==null||"".equals(startTime)){
				if(ifAppend==0){
					insertShareSql="INSERT INTO HASYS_DM_SID (ID,BUSINESSID,SHAREID,SHARENAME,CREATEUSERID,CREATETIME,DESCRIPTION) values(S_HASYS_DM_SID.NEXTVAL,?,?,?,?,sysdate,?)";
					pst=conn.prepareStatement(insertShareSql);
					pst.setInt(1,bizId);
					pst.setString(2,shareId);
					pst.setString(3,shareName);
					pst.setString(4,userId);
					pst.setString(5,description);
					pst.executeUpdate();
				}else{
					String appendId=idfactory.newId("DM_AID");
					insertShareSql="INSERT INTO HASYS_DM_AID (ID,BUSINESSID,SHAREID,AdditionalID,AdditionalName,CreatUserID,CreateTime,Description,State) VALUES(S_HASYS_DM_AID.nextval,?,?,?,?,?,sysdate,?,?)";
					pst=conn.prepareStatement(insertShareSql);
					pst.setInt(1,bizId);
					pst.setString(2,shareId);
					pst.setString(3,appendId);
					pst.setString(4, userId);
					pst.setString(5,description);
					pst.setString(6,"入库成功");
					pst.executeUpdate();
					List<String> shareIds=new ArrayList<String>();
					shareIds.add(shareId);
					singleNumberOutboundDataManage.appendCustomersToShareBatch(bizId, shareIds);
				}
			
			}else{
				insertShareSql="INSERT INTO HASYS_DM_SID (ID,BUSINESSID,SHAREID,SHARENAME,CREATEUSERID,CREATETIME,DESCRIPTION,StartTime,EndTime)values(S_HASYS_DM_SID.NEXTVAL,?,?,?,?,sysdate,?,to_date(?,'yyyy-mm-dd'),to_date(?,'yyyy-mm-dd')) ";
				pst=conn.prepareStatement(insertShareSql);
				pst.setInt(1,bizId);
				pst.setString(2,shareId);
				pst.setString(3,shareName);
				pst.setString(4,userId);
				pst.setString(5,description);
				pst.setString(6,startTime);
				pst.setString(7,endTime);
				pst.executeUpdate();
				String deleteSql="delete from HASYS_DM_SIDUSERPOOl where SHAREID=? and BUSINESSID=?";
				pst=conn.prepareStatement(deleteSql);
				pst.setString(1,shareId);
				pst.setInt(2,bizId);
				pst.executeUpdate();
				
				for (int i = 0; i < arrDataPoolName.length; i++) {
					String dataPoolName=arrDataPoolName[i];
					String dataPoolId1=arrDataPoolId[i];
					if(dataPoolName==null||"".equals(dataPoolName)){
						continue;
					}
					if(dataPoolId1==null||"".equals(dataPoolId1)){
						continue;
					}
					String insertSql="INSERT INTO HASYS_DM_SIDUSERPOOl (ID,BUSINESSID,SHAREID,DATAPOOLNAME,DATAPOOLID) VALUES(S_HASYS_DM_SIDUSERPOOl.NEXTVAL,?,?,?,?)";
					pst=conn.prepareStatement(insertSql);
					pst.setInt(1,bizId);
					pst.setString(2,shareId);
					pst.setString(3,dataPoolName);
					pst.setInt(4,Integer.valueOf(dataPoolId1));
					pst.executeUpdate();
				}
			}
			resultMap.put("result",true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("result",false);
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	/**
	 * 判断当前数据池下面是否有坐席池
	 * @param bizId
	 * @param userId
	 * @return
	 */
	public Map<String,Object> ifCurPoolChildrens(Integer bizId,String userId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Integer> dataPoolTypeList=new ArrayList<Integer>();
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String sql="select DataPoolType from HASYS_DM_DATAPOOL where pid=(select id from HASYS_DM_DATAPOOL where BusinessID=? and DataPoolName=?)";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,bizId);
			pst.setString(2,userId);
			rs=pst.executeQuery();
			while(rs.next()){
				Integer dataPoolType=rs.getInt(1);
				dataPoolTypeList.add(dataPoolType);
			}
			if(dataPoolTypeList.contains(3)){
				resultMap.put("result",0);
			}else{
				resultMap.put("result",1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	/**
	 * 修改临时表
	 * @param bizId
	 * @param userId
	 * @param tempIds
	 * @param action
	 */
	public void updateTempData(Integer bizId,String userId,String tempIds,Integer action){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String[] arrTempId=tempIds.split(",");
		String tableName="HAU_DM_"+bizId+"_"+userId;
		try {
			conn=this.getDbConnection();
			String sql=null;
			if(action==0){
				sql="update "+tableName+" set IFCHECKED=1";
			}else{
				sql="update "+tableName+" set IFCHECKED=1 where tempId in(";
				for (int i = 0; i < arrTempId.length; i++) {
					String tempId=arrTempId[i];
					if(tempId==null||"".equals(tempId)){
						continue;
					}
					sql+=Integer.valueOf(tempId)+",";
				}
				sql=sql.substring(0,sql.length()-1)+")";
			}
			pst=conn.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
	}
	
	
	/**
	 * 根据业务ID和模板Id获取JSON字符串
	 * @param bizId
	 * @param templateId
	 * @return
	 */
	public String getConfigJson(Integer bizId,Integer templateId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String configJson=null;
		try {
			conn=this.getDbConnection();
			String getJsonSql="select xml from hasys_dm_biztemplatedistview where BUSINESSID=? and templateId=?";
			pst=conn.prepareStatement(getJsonSql);
			pst.setInt(1,bizId);
			pst.setInt(2,templateId);
			rs=pst.executeQuery();
			while(rs.next()){
				configJson=rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return configJson;
	}
	
	
	
	/**
	 * 获取字段类型
	 * @param columnName
	 * @param workSheetId
	 * @param asName
	 * @return
	 */
	public String getDataType(String columnName,String workSheetId,String asName){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String column=null;
		try {
			conn=this.getDbConnection();
			String sql="select dataType from Hasys_Worksheetcolumn where WORKSHEETID=? and COLUMNNAME=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,workSheetId);
			pst.setString(2, columnName);
			rs=pst.executeQuery();
			String dataType=null;
			while(rs.next()){
				dataType=rs.getString(1);
			}
			
			if("varchar".equals(dataType.toLowerCase())||"int".equals(dataType.toLowerCase())){
				column=asName+columnName;
			}else if("datetime".equals(dataType.toLowerCase())){
				column="to_char("+asName+columnName+",'yyyy-mm-dd') columnName";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return column;
	}
	
	public String getTempColumnType(String columnName,String workSheetId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String tempColumnType=null;
		try {
			conn=this.getDbConnection();
			String sql="select dataType,length from Hasys_Worksheetcolumn where WORKSHEETID=? and COLUMNNAME=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,workSheetId);
			pst.setString(2, columnName);
			rs=pst.executeQuery();
			String dataType=null;
			Integer length=null;
			while(rs.next()){
				dataType=rs.getString(1);
				length=rs.getInt(2);
			}
			if("varchar".equals(dataType.toLowerCase())){
				tempColumnType=columnName+"  VARCHAR2("+length+"),";
			}else if("int".equals(dataType.toLowerCase())){
				tempColumnType=columnName+"  NUMBER,";
			}else if("datetime".equals(dataType.toLowerCase())){
				tempColumnType=columnName+"  DATE,";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return tempColumnType;
	}
	
	

}
