package hiapp.modules.dmmanager.data;

import hiapp.modules.dm.DMService;
import hiapp.modules.dm.singlenumbermode.SingleNumberOutboundDataManage;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import hiapp.modules.dmmanager.OperationNameEnum;
import hiapp.modules.dmmanager.TreePool;
import hiapp.modules.dmmanager.UserItem;
import hiapp.modules.dmmanager.bean.DistributeTemplate;
import hiapp.modules.dmmanager.bean.OutputFirstRow;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.idfactory.IdFactory;

import java.sql.CallableStatement;
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
import com.google.gson.JsonParser;

@Repository
public class DataDistributeJdbc extends BaseRepository{
	@Autowired
	private IdFactory idfactory;
	@Autowired
	private SingleNumberOutboundDataManage singleNumberOutboundDataManage;
	@Autowired
	private DMService dMService;

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
			String sql="select id,TEMPLATEID,BUSINESSID,NAME,DESCRIPTION,ISDEFAULT from hasys_dm_biztemplatedistview where BUSINESSID=?";
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
				disTemplate.setIsDefault(rs.getInt(6));
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
			if(configJson==null){
				return null;
			}
			JsonArray dataArray= new JsonParser().parse(configJson).getAsJsonArray();
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
	@SuppressWarnings({ "unused","unchecked", "rawtypes" })
	public void getNotDisDatByTime(String userId,Integer bizId,Integer templateId,String startTime,String endTime,int permissionId){
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
			DbUtil.DbCloseQuery(rs,pst);
			String configJson=getConfigJson(bizId, templateId);
			JsonArray dataArray= new JsonParser().parse(configJson).getAsJsonArray();
			String getDataSql2="select S_HAU_DM_B101C_IMPORT.nextval,b.IID,b.CID,b.DataPoolIDCur,b.AreaCur,";
			String insertSql="insert into "+tempTableName+"(TEMPID,IID,CID,DATAPOOLIDCUR,AREACUR, ";
			String createTableSql="create table "+tempTableName+"(TEMPID NUMBER,IFCHECKED NUMBER,IID VARCHAR2(50),CID VARCHAR2(50),DATAPOOLIDCUR NUMBER,AREACUR NUMBER,";
			
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
				for (int j = 0; j < newList.size(); j++) {
					String asName="a"+j+".";
					if(newList.get(j).equals(workSheetName)){
						if("pool".equals(suffix.toLowerCase())){
							asName="b.";
						}
						if(!"IID".equals(columnName.toUpperCase())&&!"CID".equals(columnName.toUpperCase())&&!"DATAPOOLIDCUR".equals(columnName.toUpperCase())&&!"AREACUR".equals(columnName.toUpperCase())){
							getDataSql2+=asName+columnName+",";
							insertSql+=columnName+",";
							createTableSql+=getTempColumnType(columnName,workSheetId);
							break;
						}
						
					}
				}
				
			}
			getDataSql2=getDataSql2.substring(0,getDataSql2.length()-1)+" from "+dataPoolName+" b left join ";
			
			for (int i = 0; i < newList.size(); i++) {
				String workSheetName=newList.get(i);
				String suffix=workSheetName.substring(workSheetName.lastIndexOf("_")+1);
				if("pool".equals(suffix.toLowerCase())){
					continue;
				}
				String asName="a"+i;
				getDataSql2+=newList.get(i)+ " "+asName+" on b.IID="+asName+".IID and b.CID="+asName+".CID left join ";
			}
			getDataSql2=getDataSql2.substring(0,getDataSql2.lastIndexOf("left join"))+" where ";
			getDataSql2=getDataSql2+" b.CID in(select b.CID from HASYS_DM_DID a,"+dataPoolName+" b where a.DID=b.SourceID and a.BUSINESSID=? and a.ModifyTime>to_date(?,'yyyy-mm-dd hh24:mi:ss') and a.ModifyTime<to_date(?,'yyyy-mm-dd hh24:mi:ss') and b.AreaCur=0 and b.DataPoolIDCur=(select DataPoolID from HASYS_DM_PER_MAP_POOL b where b.BusinessID=? and b.PermissionID=? and b.DataPoolID is not null)  )";
			createTableSql=createTableSql.substring(0,createTableSql.length()-1)+")";
			insertSql=insertSql.substring(0,insertSql.length()-1)+")"+getDataSql2;
			if(dbTableName==null){
				pst=conn.prepareStatement(createTableSql);
				pst.executeUpdate();
				DbUtil.DbCloseExecute(pst);
			}else{
				//删除数据
				String delteSql="delete from "+tempTableName;
				pst=conn.prepareStatement(delteSql);
				pst.executeUpdate();
				DbUtil.DbCloseExecute(pst);
				String dropTableSql="drop table "+tempTableName;
				pst=conn.prepareStatement(dropTableSql);
				pst.executeUpdate();
				DbUtil.DbCloseExecute(pst);
				pst=conn.prepareStatement(createTableSql);
				pst.executeUpdate();
				DbUtil.DbCloseExecute(pst);
			}
			//向临时表添加数据
			pst=conn.prepareStatement(insertSql);
			pst.setInt(1,bizId);
			pst.setString(2,startTime);
			pst.setString(3,endTime);
			pst.setInt(4,bizId);
			pst.setInt(5,permissionId);
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String,Object> getTempNotDisData(Integer bizId,Integer templateId ,String userId,Integer num,Integer pageSize,String tempTableName){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		List<String> workSheetNameList=new ArrayList<String>();
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
		try {
			conn=this.getDbConnection();
			String configJson=getConfigJson(bizId, templateId);
			JsonArray dataArray= new JsonParser().parse(configJson).getAsJsonArray();
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
				for (int j = 0; j < newList.size(); j++) {
					if(newList.get(j).equals(workSheetName)){
						if(!"IID".equals(columnName.toUpperCase())&&!"CID".equals(columnName.toUpperCase())){
							getDataSql1+=columnName+",";
							getDataSql3+=getDataType(columnName,workSheetId)+",";
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
						if(!"".equals(key)&&!"IID".equals(key.toUpperCase())&&!"CID".equals(key.toUpperCase())){
							map.put(key,rs.getObject(key));
						}
					}
				}
				
				dataList.add(map);
			}
			DbUtil.DbCloseQuery(rs, pst);
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
	public TreePool getRootPool(String userId,Integer bizId,int permissionId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		TreePool treePool=new TreePool();
		try {
			conn=this.getDbConnection();
			String sql="select a.ID,a.DATAPOOLNAME,a.PID from HASYS_DM_DATAPOOL a where a.id=(select DataPoolID from HASYS_DM_PER_MAP_POOL b where b.BusinessID=? and b.PermissionID=? and b.DataPoolID is not null)";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, bizId);
			pst.setInt(2,permissionId);
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
			String sql="select a.ID,a.DATAPOOLNAME,a.POOLTOPLIMIT-(select nvl(sum(case when b.datapoolidcur = a.id then 1 else 0 end),0) from "+poolName+" b) topLimit,a.dataPoolType from HASYS_DM_DATAPOOL a where a.BusinessID=? and a.pid=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,bizId);
			pst.setInt(2, pid);
			rs=pst.executeQuery();
			while(rs.next()){
				UserItem userItem=new UserItem();
				userItem.setId(String.valueOf(rs.getInt(1)));
				userItem.setText(rs.getString(2));;
				userItem.setTopLimit(rs.getInt(3));
				userItem.setDataPoolType(rs.getInt(4));
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
	public UserItem getTreePoolByBizId(Integer bizId,String userId,int permissionId){
		TreePool treePool=getRootPool(userId, bizId,permissionId);
		UserItem userItem=new UserItem();
		userItem.setId(String.valueOf(treePool.getId()));
		userItem.setText(treePool.getDataPoolName());
		List<UserItem>  userItemList=getChildrenPool(bizId, treePool.getId());
		userItem.setChildren(userItemList);
		return userItem;
	}
	
	
	/**
	 * 保存数据到正式表中
	 * @param bizId
	 * @param userId
	 * @param disName
	 * @param description
	 * @param dataPoolList
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked"})
	public Map<String,Object> saveDistributeDataToDB(Integer bizId,String userId,String disName,String description,List<Map<String,Object>>  dataPoolList){
		Connection conn=null;
		PreparedStatement pst = null;
		CallableStatement callStmt = null; 
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
		String disBatchId=idfactory.newId("DM_DID");//分配号
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		String orePoolName="HAU_DM_B"+bizId+"C_POOL_ORE";
		String tempTableName="HAU_DM_"+bizId+"_"+userId;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			resultMap=getDataPool(dataPoolList, bizId);
			List<String> nameList=(List<String>) resultMap.get("poolName");
			if(nameList==null||nameList.size()>0){
				return resultMap;
			}
			conn=this.getDbConnection();
			//不自动提交数据
			conn.setAutoCommit(false);
			for(int i = 0; i < dataPoolList.size(); i++) {
				Integer dataPoolId=Integer.valueOf((String)dataPoolList.get(i).get("dataPoolId"));
				Double dataPoolType=(Double)dataPoolList.get(i).get("dataPoolType");
				Double disNum=(Double)dataPoolList.get(i).get("disNum");
				callStmt = conn.prepareCall("{call HASYS_DM_DISTRIBUTION_BATCH(?,?,?,?,?,?,?)}");
				callStmt.setString(1,tempTableName);
				callStmt.setDouble(2,dataPoolType);
				callStmt.setInt(3,bizId);
				callStmt.setString(4,disBatchId);
				callStmt.setDouble(5, disNum);
				callStmt.setInt(6,dataPoolId);
				callStmt.setString(7,userId);
				callStmt.execute();  
				if(callStmt!=null){
					callStmt.close();
				}
			}
			String insertDisBatchSql="insert into HASYS_DM_DID(id,BusinessID,DID,DistributionName,ModifyUserID,ModifyTime,Description) values(S_HASYS_DM_DID.nextval,?,?,?,?,sysdate,?)";
			pst=conn.prepareStatement(insertDisBatchSql);
			pst.setInt(1,bizId);
			pst.setString(2,disBatchId);
			pst.setString(3,disName);
			pst.setString(4,userId);
			pst.setString(5,description);
			pst.execute();
			conn.commit();
			resultMap.put("result",true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("result",false);
		}finally{
			try {
				if(pst!=null){
					pst.close();
				}
				if(callStmt!=null){
					callStmt.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	public Map<String,Object> saveShareDataToDB(Integer bizId,String userId,String shareName,String description,String startTime,String endTime,String dataPoolIds,String dataPoolNames,Integer model,String appendId,int permissionId ){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		String orePoolName="HAU_DM_B"+bizId+"C_POOL_ORE";	
		String tempTableName="HAU_DM_"+bizId+"_"+userId;
		//单号码重拨表
		String datamTableName="HAU_DM_B"+bizId+"C_DATAM3";
		//单号码重拨历史表
		String hisTableName= "HAU_DM_B"+bizId+"C_DATAM3_HIS";
		//多号码重拨表
		String datamTableName6="HAU_DM_B"+bizId+"C_DATAM6";
		//多号码重拨历史表
		String hisTableName6= "HAU_DM_B"+bizId+"C_DATAM6_HIS";
		//导入表
		String importTableName="HAU_DM_B"+bizId+"C_IMPORT";
		//共享批次
		String shareId = idfactory.newId("DM_SID");
		String appendIds=null;
		String state=null;
		String state1=null;
		Integer ifAppend=0;
		if(appendId==null||"".equals(appendId)){
			ifAppend=0;
			state=SingleNumberModeShareCustomerStateEnum.CREATED.getName();
			state1=OperationNameEnum.Sharing.getName();
		}else{
			shareId=appendId;
			state=SingleNumberModeShareCustomerStateEnum.APPENDED.getName();
			state1=OperationNameEnum.APPERND.getName();
			ifAppend=1;
		}
		String[] arrDataPoolId=dataPoolIds.split(",");
		String[] arrDataPoolName=dataPoolNames.split(",");
		Map<String,Object> resultMap=new HashMap<String, Object>();
		Boolean result=null;
		try {
			conn=this.getDbConnection();
			String getDataSourceSql="select DataPoolID from HASYS_DM_PER_MAP_POOL b where b.BusinessID=? and b.PermissionID=? and b.DataPoolID is not null";
			pst=conn.prepareStatement(getDataSourceSql);
			pst.setInt(1,bizId);
			pst.setInt(2,permissionId);
			rs=pst.executeQuery();
			Integer dataPoolId=null;
			while(rs.next()){
				dataPoolId=rs.getInt(1);
			}
			DbUtil.DbCloseQuery(rs,pst);
			//不自动提交数据
			conn.setAutoCommit(false);
			String updatePoolSql="update "+poolName+" a set (sourceID,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ModifyUserID,ModifyTime,OperationName)"
					+ " = (select '"+shareId+"',DATAPOOLIDCUR,'"+dataPoolId+"',AreaCur,1,'"+userId+"',sysdate,'"+state1+"' from "+tempTableName+" b where a.IID=b.IID AND a.CID=b.CID and b.ifchecked=1)"
					+ " where exists(select 1 from "+tempTableName+" b where a.IID = b.IID AND a.CID = b.CID and b.ifchecked=1)";;
			pst=conn.prepareStatement(updatePoolSql);
			pst.execute();
			DbUtil.DbCloseExecute(pst);
			String insertOrePoolSql="insert into "+orePoolName+" a(id,SourceID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime)"+
									" select S_"+orePoolName+".nextval,'"+shareId+"',IID,CID,'"+state1+"',DataPoolIDCur,'"+dataPoolId+"',AreaCur,1,0,'"+userId+"',sysdate from "+tempTableName+" b where b.ifchecked=1 ";
			pst=conn.prepareStatement(insertOrePoolSql);
			pst.execute();
			DbUtil.DbCloseExecute(pst);
			if(model==3){
				
				String insertDatamSql="insert into "+datamTableName+" a (ID,BUSINESSID,SHAREID,IID,CID,STATE,MODIFYID,MODIFYUSERID,MODIFYTIME) select S_"+datamTableName+".NEXTVAL,"+bizId+",'"+shareId+"',IID,CID,'"+state+"',0,'"+userId+"',sysdate from "+tempTableName+" b "+
									  "where b.ifchecked=1 ";
				pst=conn.prepareStatement(insertDatamSql);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
				String insertHisDatamSql="insert into "+hisTableName+" a (ID,BUSINESSID,SHAREID,IID,CID,STATE,MODIFYID,MODIFYUSERID,MODIFYTIME) select S_"+hisTableName+".NEXTVAL,"+bizId+",'"+shareId+"',IID,CID,'"+state+"',0,'"+userId+"',sysdate from "+tempTableName+" b "+
										 "where b.ifchecked=1 ";
				pst=conn.prepareStatement(insertHisDatamSql);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
			}else if(model==6){
				Map<Integer,String> phoneNumMap=getPhoneNum(bizId);
				String insertDatamSql6="insert into "+datamTableName6+" a (ID,BusinessID,SHAREID,IID,CID,State,ModifyID,ModifyUserID,ModifyTime,IsAppend,Pt1_PhoneNumber,Pt2_PhoneNumber,Pt3_PhoneNumber,Pt4_PhoneNumber,Pt5_PhoneNumber,Pt6_PhoneNumber,Pt7_PhoneNumber,Pt8_PhoneNumber,Pt9_PhoneNumber,Pt10_PhoneNumber)"+
									   " select S_"+datamTableName6+".nextval,"+bizId+",'"+shareId+"',m.IID,m.CID,'"+state+"',0,'"+userId+"',sysdate,"+ifAppend+","+getColumnName(1,phoneNumMap)+","+getColumnName(2,phoneNumMap)+","+getColumnName(3,phoneNumMap)+","+getColumnName(4,phoneNumMap)+","+getColumnName(5,phoneNumMap)+
									   ","+getColumnName(6,phoneNumMap)+","+getColumnName(7,phoneNumMap)+","+getColumnName(8,phoneNumMap)+","+getColumnName(9,phoneNumMap)+","+getColumnName(10,phoneNumMap)+" from "+tempTableName+" m left join "+importTableName+" n on m.IID=n.IID and m.CID=n.CID where m.ifchecked=1";
				pst=conn.prepareStatement(insertDatamSql6);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
				String insertHisDatamSql6="insert into "+hisTableName6+" a (ID,BusinessID,SHAREID,IID,CID,State,ModifyID,ModifyUserID,ModifyTime,IsAppend,Pt1_PhoneNumber,Pt2_PhoneNumber,Pt3_PhoneNumber,Pt4_PhoneNumber,Pt5_PhoneNumber,Pt6_PhoneNumber,Pt7_PhoneNumber,Pt8_PhoneNumber,Pt9_PhoneNumber,Pt10_PhoneNumber)"+
						   " select S_"+hisTableName6+".nextval,"+bizId+",'"+shareId+"',m.IID,m.CID,'"+state+"',0,'"+userId+"',sysdate,"+ifAppend+","+getColumnName(1,phoneNumMap)+","+getColumnName(2,phoneNumMap)+","+getColumnName(3,phoneNumMap)+","+getColumnName(4,phoneNumMap)+","+getColumnName(5,phoneNumMap)+
						   ","+getColumnName(6,phoneNumMap)+","+getColumnName(7,phoneNumMap)+","+getColumnName(8,phoneNumMap)+","+getColumnName(9,phoneNumMap)+","+getColumnName(10,phoneNumMap)+" from "+tempTableName+" m left join "+importTableName+" n on m.IID=n.IID and m.CID=n.CID where m.ifchecked=1";
				pst=conn.prepareStatement(insertHisDatamSql6);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
			}else if(model==4){
				Map<Integer,String> phoneNumMap=getPhoneNum(bizId);
				String dataTableName4="HAU_DM_B"+bizId+"C_DATAM4";
				//多号码重拨历史表
				String hisTableName4= "HAU_DM_B"+bizId+"C_DATAM4_HIS";
				String insertDatamSql4="insert into "+dataTableName4+" a (ID,BusinessID,SHAREID,IID,CID,State,ModifyID,ModifyUserID,ModifyTime,IsAppend,Pt1_PhoneNumber,Pt2_PhoneNumber,Pt3_PhoneNumber,Pt4_PhoneNumber,Pt5_PhoneNumber,Pt6_PhoneNumber,Pt7_PhoneNumber,Pt8_PhoneNumber,Pt9_PhoneNumber,Pt10_PhoneNumber)"+
									   " select S_"+dataTableName4+".nextval,"+bizId+",'"+shareId+"',m.IID,m.CID,'"+state+"',0,'"+userId+"',sysdate,"+ifAppend+","+getColumnName(1,phoneNumMap)+","+getColumnName(2,phoneNumMap)+","+getColumnName(3,phoneNumMap)+","+getColumnName(4,phoneNumMap)+","+getColumnName(5,phoneNumMap)+
									   ","+getColumnName(6,phoneNumMap)+","+getColumnName(7,phoneNumMap)+","+getColumnName(8,phoneNumMap)+","+getColumnName(9,phoneNumMap)+","+getColumnName(10,phoneNumMap)+" from "+tempTableName+" m left join "+importTableName+" n on m.IID=n.IID and m.CID=n.CID where m.ifchecked=1";
				pst=conn.prepareStatement(insertDatamSql4);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
				String insertHisDatamSql4="insert into "+hisTableName4+" a (ID,BusinessID,SHAREID,IID,CID,State,ModifyID,ModifyUserID,ModifyTime,IsAppend,Pt1_PhoneNumber,Pt2_PhoneNumber,Pt3_PhoneNumber,Pt4_PhoneNumber,Pt5_PhoneNumber,Pt6_PhoneNumber,Pt7_PhoneNumber,Pt8_PhoneNumber,Pt9_PhoneNumber,Pt10_PhoneNumber)"+
						   " select S_"+hisTableName4+".nextval,"+bizId+",'"+shareId+"',m.IID,m.CID,'"+state+"',0,'"+userId+"',sysdate,"+ifAppend+","+getColumnName(1,phoneNumMap)+","+getColumnName(2,phoneNumMap)+","+getColumnName(3,phoneNumMap)+","+getColumnName(4,phoneNumMap)+","+getColumnName(5,phoneNumMap)+
						   ","+getColumnName(6,phoneNumMap)+","+getColumnName(7,phoneNumMap)+","+getColumnName(8,phoneNumMap)+","+getColumnName(9,phoneNumMap)+","+getColumnName(10,phoneNumMap)+" from "+tempTableName+" m left join "+importTableName+" n on m.IID=n.IID and m.CID=n.CID where m.ifchecked=1";
				pst=conn.prepareStatement(insertHisDatamSql4);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
			}else if(model==5){
				String datamTableName5="HAU_DM_B"+bizId+"C_DATAM5";
				String hisTableName5="HAU_DM_B"+bizId+"C_DATAM5_HIS";
				String insertDatamSql="insert into "+datamTableName5+" a (ID,BUSINESSID,SHAREID,IID,CID,STATE,MODIFYID,MODIFYUSERID,MODIFYTIME) select S_"+datamTableName5+".NEXTVAL,"+bizId+",'"+shareId+"',IID,CID,'"+state+"',0,'"+userId+"',sysdate from "+tempTableName+" b "+
						  "where b.ifchecked=1 ";
				pst=conn.prepareStatement(insertDatamSql);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
				String insertHisDatamSql="insert into "+hisTableName5+" a (ID,BUSINESSID,SHAREID,IID,CID,STATE,MODIFYID,MODIFYUSERID,MODIFYTIME) select S_"+hisTableName5+".NEXTVAL,"+bizId+",'"+shareId+"',IID,CID,'"+state+"',0,'"+userId+"',sysdate from "+tempTableName+" b "+
										 "where b.ifchecked=1 ";
				pst=conn.prepareStatement(insertHisDatamSql);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
			}else if(model==2){
				String datamTableName2="HAU_DM_B"+bizId+"C_DATAM2";
				String hisTableName2="HAU_DM_B"+bizId+"C_DATAM2_HIS";
				String phoneColumn=getHidPhoneNum(bizId);
				String insertDatamSql="insert into "+datamTableName2+" a (ID,BUSINESSID,SHAREID,IID,CID,STATE,MODIFYID,MODIFYUSERID,MODIFYTIME,PhoneNumber) select S_"+datamTableName2+".NEXTVAL,"+bizId+",'"+shareId+"',m.IID,m.CID,'"+state+"',0,'"+userId+"',sysdate,"+phoneColumn+" from "+tempTableName+" m "+
									  "left join "+importTableName+" n on m.IID=n.IID and m.CID=n.CID where m.ifchecked=1";
				pst=conn.prepareStatement(insertDatamSql);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
				String insertHisDatamSql="insert into "+hisTableName2+" a (ID,BUSINESSID,SHAREID,IID,CID,STATE,MODIFYID,MODIFYUSERID,MODIFYTIME,PhoneNumber) select S_"+hisTableName2+".NEXTVAL,"+bizId+",'"+shareId+"',m.IID,m.CID,'"+state+"',0,'"+userId+"',sysdate,"+phoneColumn+" from "+tempTableName+" m "+
										 "left join "+importTableName+" n on m.IID=n.IID and m.CID=n.CID where m.ifchecked=1";
				pst=conn.prepareStatement(insertHisDatamSql);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
			}
			String deleteTempSql=" delete from "+tempTableName+" a where a.ifchecked=1";
			pst=conn.prepareStatement(deleteTempSql);
			pst.execute();
			DbUtil.DbCloseExecute(pst);
			String insertShareSql=null;
			if(startTime==null||"".equals(startTime)){
				if(appendId==null||"".equals(appendId)){
					insertShareSql="INSERT INTO HASYS_DM_SID (ID,BUSINESSID,SHAREID,SHARENAME,CREATEUSERID,CREATETIME,DESCRIPTION) values(S_HASYS_DM_SID.NEXTVAL,?,?,?,?,sysdate,?)";
					pst=conn.prepareStatement(insertShareSql);
					pst.setInt(1,bizId);
					pst.setString(2,shareId);
					pst.setString(3,shareName);
					pst.setString(4,userId);
					pst.setString(5,description);
					pst.execute();
					DbUtil.DbCloseExecute(pst);
				}else{
					appendIds=idfactory.newId("DM_AID");
					insertShareSql="INSERT INTO HASYS_DM_AID (ID,BUSINESSID,SHAREID,AdditionalID,AdditionalName,CreatUserID,CreateTime,Description,State) VALUES(S_HASYS_DM_AID.nextval,?,?,?,?,?,sysdate,?,?)";
					pst=conn.prepareStatement(insertShareSql);
					pst.setInt(1,bizId);
					pst.setString(2,shareId);
					pst.setString(3,appendIds);
					pst.setString(4,shareName);
					pst.setString(5, userId);
					pst.setString(6,description);
					pst.setString(7,"入库成功");
					pst.execute();
					DbUtil.DbCloseExecute(pst);
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
				pst.execute();
				DbUtil.DbCloseExecute(pst);
				String deleteSql="delete from HASYS_DM_SIDUSERPOOl where SHAREID=? and BUSINESSID=?";
				pst=conn.prepareStatement(deleteSql);
				pst.setString(1,shareId);
				pst.setInt(2,bizId);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
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
					pst.execute();
					DbUtil.DbCloseExecute(pst);
				}
			}
			conn.commit();
			if(startTime==null||"".equals(startTime)){
				if(appendId!=null&&!"".equals(appendId)){
					List<String> shareIds=new ArrayList<String>();
					shareIds.add(shareId);
					result= dMService.appendCustomersToShareBatch(bizId, shareIds);
					if(result){
						String updateSql="update HASYS_DM_AID a set a.state='通知分配器成功' where a.BusinessID="+bizId+" and a.ShareID='"+shareId+"' and a.AdditionalID='"+appendIds+"'";
						pst=conn.prepareStatement(updateSql);
						pst.execute();
						DbUtil.DbCloseExecute(pst);
						conn.commit();
					}
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
	public Map<String,Object> ifCurPoolChildrens(Integer bizId,int permissionId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Integer> dataPoolTypeList=new ArrayList<Integer>();
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String sql="select DataPoolType from HASYS_DM_DATAPOOL where pid=(select DataPoolID from HASYS_DM_PER_MAP_POOL b where b.BusinessID=? and b.PermissionID=? and b.DataPoolID is not null)";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,bizId);
			pst.setInt(2,permissionId);
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
	public void updateTempData(Integer bizId,String userId,String tempIds,Integer action,String tableName){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String[] arrTempId=tempIds.split(",");
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
			String getJsonSql="select CONFIGJSON from hasys_dm_biztemplatedistview where BUSINESSID=? and templateId=?";
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
	public String getDataType(String columnName,String workSheetId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String column=null;
		try {
			conn=this.getDbConnection();
			String sql="select dataType from Hasys_Worksheetcolumn where WORKSHEETID=? and upper(COLUMNNAME)=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,workSheetId);
			pst.setString(2, columnName.toUpperCase());
			rs=pst.executeQuery();
			String dataType=null;
			while(rs.next()){
				dataType=rs.getString(1);
			}
			
			if("varchar".equals(dataType.toLowerCase())||"int".equals(dataType.toLowerCase())){
				column=columnName;
			}else if("datetime".equals(dataType.toLowerCase())){
				column="to_char("+columnName+",'yyyy-mm-dd hh24:mi:ss') "+columnName;
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
			String sql="select dataType,length from Hasys_Worksheetcolumn where WORKSHEETID=? and upper(COLUMNNAME)=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,workSheetId);
			pst.setString(2, columnName.toUpperCase());
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
	
	public Map<Integer,String> getPhoneNum(Integer bizId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<Integer,String> resultMap=new HashMap<Integer, String>();
		try {
			conn=this.getDbConnection();
			String sql="select DialType,CustomerColumnMap from hasys_DM_BizPhoneType where BusinessId=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,bizId);
			rs=pst.executeQuery();
			while(rs.next()){
				Integer phoneNum=rs.getInt(1);
				String column=rs.getString(2);
				resultMap.put(phoneNum, column);
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
	
	public String getHidPhoneNum(Integer bizId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String phoneNum=null;
		try {
			conn=this.getDbConnection();
			String sql="select CustomerColumnMap from hasys_DM_BizPhoneType where BusinessId=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,bizId);
			rs=pst.executeQuery();
			while(rs.next()){
				phoneNum="n."+rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return phoneNum;
	}
	public String getColumnName(Integer num,Map<Integer,String> phoneNumMap){
		String columName=null;
		if(phoneNumMap.keySet().contains(num)){
			columName="n."+phoneNumMap.get(num);
		}
		return columName;
	}
	
	public Map<String,Object> getDataPool(List<Map<String,Object>>  dataPoolList,Integer bizId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		List<UserItem>	userItems=new ArrayList<UserItem>();
		List<String> poolNameList=new ArrayList<String>();
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String sql="select a.ID,a.DATAPOOLNAME,a.POOLTOPLIMIT-(select nvl(sum(case when b.datapoolidcur = a.id then 1 else 0 end),0) from "+poolName+" b) topLimit,a.dataPoolType from HASYS_DM_DATAPOOL a where a.BusinessID=? and a.id in(";
			for (int i = 0; i < dataPoolList.size(); i++) {
				Integer dataPoolId=Integer.valueOf((String)dataPoolList.get(i).get("dataPoolId"));
				sql+=dataPoolId+",";
			}
			sql=sql.substring(0,sql.length()-1)+")";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,bizId);
			rs=pst.executeQuery();
			while(rs.next()){
				UserItem userItem=new UserItem();
				userItem.setItemId(rs.getInt(1));
				userItem.setItemText(rs.getString(2));
				userItem.setTopLimit(rs.getInt(3));
				userItems.add(userItem);
			}
			
			for (int i = 0; i < dataPoolList.size(); i++) {
				String dataPoolName=(String)dataPoolList.get(i).get("poolName");
				Double disNum=(Double)dataPoolList.get(i).get("disNum");
				for (int j = 0; j < userItems.size(); j++) {
					if(dataPoolName.equals(userItems.get(j).getItemText())){
						if(userItems.get(j).getTopLimit()<disNum){
							poolNameList.add(dataPoolName);
							break;
						}
					}
				}

			}
			resultMap.put("result",false);
			resultMap.put("poolName",poolNameList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("result",false);
		}
		
		return resultMap;
	}
}
