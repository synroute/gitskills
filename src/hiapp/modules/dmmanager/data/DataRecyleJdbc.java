package hiapp.modules.dmmanager.data;

import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dmmanager.ShareBatchItemS;
import hiapp.modules.dmmanager.bean.OutputFirstRow;
import hiapp.modules.dmmanager.bean.RecyleTemplate;
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
import com.google.gson.JsonParser;

@Repository
public class DataRecyleJdbc extends BaseRepository{
	@Autowired
	private DataDistributeJdbc dataDistributeJdbc;
	@Autowired
	private IdFactory idfactory;
	/**
	 * 获取所有回收模板
	 * @param bizId
	 * @return
	 */
	public List<RecyleTemplate> getAllRecyleTemplate(Integer bizId){
		List<RecyleTemplate> templateList=new ArrayList<RecyleTemplate>();
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {	
			conn=this.getDbConnection();
			String sql="select id,TEMPLATEID,BUSINESSID,NAME,DESCRIPTION,ISDEFAULT from HASYS_DM_BIZTEMPLATERECOVER where BUSINESSID=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,bizId);
			rs=pst.executeQuery();
			while(rs.next()){
				RecyleTemplate recyleTemplate=new RecyleTemplate();
				recyleTemplate.setId(rs.getInt(1));
				recyleTemplate.setTemplateId(rs.getInt(2));
				recyleTemplate.setBizId(rs.getInt(3));
				recyleTemplate.setName(rs.getString(4));
				recyleTemplate.setDescription(rs.getString(5));
				recyleTemplate.setIsDefault(rs.getInt(6));
				templateList.add(recyleTemplate);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return templateList;
	}
	/**
	 * 获取前台展示列
	 * @param bizId
	 * @param templateId
	 */
	public List<OutputFirstRow> getTemplateColums(Integer bizId,Integer templateId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<OutputFirstRow> columns=new ArrayList<OutputFirstRow>();
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
	 * 根据时间获取已经分配的数据并保存到临时表中
	 * @param bizId
	 * @param userId
	 * @param templateId
	 * @param startTime
	 * @param endTime
	 */
	@SuppressWarnings({"unchecked", "rawtypes" })
	public void getDistributeDataByTime(Integer bizId,String userId,Integer templateId,String startTime,String endTime,int permissionId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String tempTableName="HAU_DM_H"+bizId+"S_"+userId;
		String dataPoolName="HAU_DM_B"+bizId+"C_POOL";
		List<String> workSheetNameList=new ArrayList<String>();
		Integer areaCur=0;
		try {
			conn=this.getDbConnection();
			Map<String, Object> ifCurPoolChildrens = dataDistributeJdbc.ifCurPoolChildrens(bizId, permissionId);
			Integer result=(Integer) ifCurPoolChildrens.get("result");
			if(result==0){
				areaCur=2;
			}else{
				areaCur=0;
			}
			
			String dbTableName=getTableName(tempTableName);
			String configJson=getConfigJson(bizId, templateId);
			JsonArray dataArray= new JsonParser().parse(configJson).getAsJsonArray();
			String insertSql="insert into "+tempTableName+"(TEMPID,IID,CID,DATAPOOLIDCUR,AREACUR,";
			String createTableSql="create table "+tempTableName+" (TEMPID NUMBER,IFCHECKED NUMBER,IID VARCHAR2(50),CID VARCHAR2(50),DATAPOOLIDCUR NUMBER,AREACUR NUMBER,";
			String getDataSql2="select S_HAU_DM_B101C_IMPORT.nextval,b.IID,b.CID,b.DataPoolIDCur,b.AreaCur,";
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
			for(int i = 0; i < dataArray.size(); i++) {
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
							createTableSql+=dataDistributeJdbc.getTempColumnType(columnName,workSheetId);
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
				//getDataSql2+=newList.get(i)+" "+asName+" on b.IID="+asName+".IID and b.CID="+asName+".CID left join ";
                //getDataSql2+=newList.get(i)+" "+asName;
				getDataSql2+=" (select * from "+ newList.get(i)+" where modifylast=1)"+asName;
				getDataSql2+=" on b.IID="+asName+".IID and b.CID="+asName+".CID left join ";
			}
			getDataSql2=getDataSql2.substring(0,getDataSql2.lastIndexOf("left join"))+" where ";
			getDataSql2=getDataSql2+" b.CID in(select b.CID from HASYS_DM_DID a,"+dataPoolName+" b where a.DID=b.SourceID and a.BUSINESSID=? and a.ModifyTime>to_date(?,'yyyy-mm-dd hh24:mi:ss') and a.ModifyTime<to_date(?,'yyyy-mm-dd hh24:mi:ss') "
					+ " and b.AreaCur="+areaCur+" and b.ISRecover=0 and b.DataPoolIDCur in(select id from HASYS_DM_DATAPOOL p where p.pid=(select DataPoolID from HASYS_DM_PER_MAP_POOL b where b.BusinessID=? and b.PermissionID=? and b.DataPoolID is not null) and p.BusinessID=?) )";
			createTableSql=createTableSql.substring(0,createTableSql.length()-1)+")";
			insertSql=insertSql.substring(0,insertSql.length()-1)+") "+getDataSql2;
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

				//删除表
				String dropTableSql="drop table "+tempTableName;
				pst=conn.prepareStatement(dropTableSql);
				pst.executeUpdate();
				DbUtil.DbCloseExecute(pst);
				//创建表
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
			pst.setInt(5, permissionId);
			pst.setInt(6, bizId);;
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
							getDataSql3+=dataDistributeJdbc.getDataType(columnName,workSheetId)+",";
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
			DbUtil.DbCloseQuery(rs,pst);
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
	 * 根据时间获得停止或者过期的已共享数据
	 * @param bizId
	 * @param userId
	 * @param templateId
	 * @param startTime
	 * @param endTime
	 * @param num
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> getShareDataByTime(Integer bizId,String userId,String startTime,String endTime,Integer num,Integer pageSize){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String dataPoolName="HAU_DM_B"+bizId+"C_POOL";
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		List<ShareBatchItemS> shareBatchItemList=new ArrayList<ShareBatchItemS>();
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String sql = "";
			String sql1="";
			sql="SELECT DISTINCT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,to_char(A.CREATETIME,'yyyy-mm-dd hh24:mi:ss') CREATETIME,A.DESCRIPTION,A.STATE,to_char(A.STARTTIME,'yyyy-mm-dd') STARTTIME,to_char(A.ENDTIME,'yyyy-mm-dd') ENDTIME,B.ABC,rownum rn FROM HASYS_DM_SID A ,(SELECT SourceID,COUNT(1) AS ABC FROM "+dataPoolName+" GROUP BY SourceID ) B WHERE A.SHAREID=B.SourceID AND A.CREATETIME >to_date(?,'yyyy-mm-dd hh24:mi:ss') AND A.CREATETIME < to_date(?,'yyyy-mm-dd hh24:mi:ss') AND A.BUSINESSID=? AND (A.STATE='stop' or A.STATE='expired') AND NOT EXISTS(SELECT 1 FROM HASYS_DM_SID WHERE SHAREID=A.SHAREID AND ID>A.ID) and rownum<? ORDER BY CREATETIME";
			sql1="SELECT DISTINCT ID,BUSINESSID,SHAREID,SHARENAME,CREATEUSERID,CREATETIME,DESCRIPTION,STATE,STARTTIME,ENDTIME,ABC from (";
			sql=sql1+sql+") m where rn>=?";			
			pst= conn.prepareStatement(sql);
			pst.setString(1,startTime);
			pst.setString(2,endTime);
			pst.setInt(3,bizId);
			pst.setInt(4,endNum);
			pst.setInt(5,startNum);
			rs = pst.executeQuery();
			while (rs.next()) {
				ShareBatchItemS shareBatchItems = new ShareBatchItemS();
				shareBatchItems.setId(rs.getInt(1));
				shareBatchItems.setBizId(rs.getInt(2));
				shareBatchItems.setShareBatchId(rs.getString(3));
				shareBatchItems.setShareBatchName(rs.getString(4));
				shareBatchItems.setCreateUserId(rs.getString(5));
				shareBatchItems.setCreateTime(rs.getString(6));
				shareBatchItems.setDescription(rs.getString(7));
				String state = (String)rs.getObject(8);
				String shareBatchStateEnum = null;
				if("stop".equals(state)){
					shareBatchStateEnum ="停止";
				}else if("expired".equals(state)){
					shareBatchStateEnum ="过期";
				}else if(state==null||"".equals(state)){
					shareBatchStateEnum ="";
				}
				shareBatchItems.setState(shareBatchStateEnum);
				shareBatchItems.setStartTime(rs.getString(9));
				shareBatchItems.setEndTime(rs.getString(10));
				shareBatchItems.setAbc(rs.getInt(11));
				shareBatchItemList.add(shareBatchItems);
			}
			DbUtil.DbCloseQuery(rs, pst);
			String getCountSql="select count(*) from (SELECT DISTINCT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME,B.ABC FROM HASYS_DM_SID A ,(SELECT SourceID,COUNT(1) AS ABC FROM "+dataPoolName+" GROUP BY SourceID ) B where  A.SHAREID=B.SourceID  AND A.CREATETIME >to_date(?,'yyyy-MM-dd hh24:mi:ss') AND A.CREATETIME < to_date(?,'yyyy-MM-dd hh24:mi:ss') AND A.BUSINESSID=? AND (A.STATE='stop'or A.STATE='expired') AND NOT EXISTS(SELECT 1 FROM HASYS_DM_SID WHERE SHAREID=A.SHAREID AND ID>A.ID) ORDER BY A.CREATETIME) t";
			pst=conn.prepareStatement(getCountSql);
			pst.setString(1,startTime);
			pst.setString(2,endTime);
			pst.setInt(3,bizId);
			rs=pst.executeQuery();
			Integer total=null;
			while(rs.next()){
				total=rs.getInt(1);
			}
			resultMap.put("total", total);
			resultMap.put("rows",shareBatchItemList);
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
	 * 分配数据进行回收
	 * @param bizId
	 * @param userId
	 */
	public Map<String,Object> recyleDisData(Integer bizId,String userId,int permissionId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String disBatchId=idfactory.newId("DM_DID");//分配号
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		String orePoolName="HAU_DM_B"+bizId+"C_POOL_ORE";
		String tempTableName="HAU_DM_H"+bizId+"S_"+userId;
		Map<String,Object> resultMap=new HashMap<String, Object>();
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
			String updatePoolSql="update "+poolName+" a set (sourceID,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ModifyUserID,ModifyTime,ISRecover,OperationName)"
					+ " = (select '"+disBatchId+"',DATAPOOLIDCUR,'"+dataPoolId+"',AreaCur,0,'"+userId+"',sysdate,1,'回收' from "+tempTableName+" b where a.IID=b.IID AND a.CID=b.CID and b.ifchecked=1) "
							+ " where exists(select 1 from "+tempTableName+" b where a.IID = b.IID AND a.CID = b.CID and b.ifchecked=1)";
			pst=conn.prepareStatement(updatePoolSql);
			pst.execute();
			DbUtil.DbCloseExecute(pst);
			String insertOrePoolSql="insert into "+orePoolName+" a(id,SourceID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime)"+
					" select S_"+orePoolName+".nextval,'"+disBatchId+"',IID,CID,'回收',DataPoolIDCur,'"+dataPoolId+"',AreaCur,0,1,'"+userId+"',sysdate from "+tempTableName+" b where b.ifchecked=1";
			pst=conn.prepareStatement(insertOrePoolSql);
			pst.execute();
			DbUtil.DbCloseExecute(pst);
			String deleteSql=" delete from "+tempTableName+" a where a.ifchecked=1";
			pst=conn.prepareStatement(deleteSql);
			pst.execute();
			DbUtil.DbCloseExecute(pst);
			String insertDisBatchSql="insert into HASYS_DM_DID(id,BusinessID,DID,ModifyUserID,ModifyTime) values(S_HASYS_DM_DID.nextval,"+bizId+",'"+disBatchId+"','"+userId+"',sysdate)";
			pst=conn.prepareStatement(insertDisBatchSql);
			pst.execute();
			//提交
			conn.commit();
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
	 * 回收共享数据
	 * @param bizId
	 * @param userId
	 * @param shareIds
	 */
	public Map<String,Object> recyleShareData(Integer bizId,String userId,String shareIds,int permissionId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String[] shareIdArr=shareIds.split(",");
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		String orePoolName="HAU_DM_B"+bizId+"C_POOL_ORE";
		String disBatchId=idfactory.newId("DM_DID");//分配号
		Map<String,Object> resultMap=new HashMap<String, Object>();
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
			DbUtil.DbCloseQuery(rs, pst);
			//不自动提交数据
			conn.setAutoCommit(false);
			for (int i = 0; i < shareIdArr.length; i++) {
				String shareId=shareIdArr[i];
				String updatePoolSql="update "+poolName+" set SourceID=?,DataPoolIDLast=DataPoolIDCur,DataPoolIDCur=?,AreaLast=AreaCur,AreaCur=0,ISRecover=1,ModifyUserID=?,ModifyTime=sysdate,OperationName='回收' where SourceID=?";
				pst=conn.prepareStatement(updatePoolSql);
				pst.setString(1,disBatchId);
				pst.setInt(2, dataPoolId);
				pst.setString(3,userId);
				pst.setString(4,shareId);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
				String insertOrePoolSql="insert into "+orePoolName+" a(id,SourceID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime)"+
										" select S_"+orePoolName+".nextval,'"+disBatchId+"',IID,CID,'回收',DataPoolIDCur,'"+dataPoolId+"',AreaCur,0,1,'"+userId+"',sysdate from "+orePoolName+" b where b.SourceID=?";
				pst=conn.prepareStatement(insertOrePoolSql);
				pst.setString(1, shareId);
				pst.execute();
				DbUtil.DbCloseExecute(pst);
				String updateShareIdSql="update HASYS_DM_SID set state ='"+ShareBatchStateEnum.RECOVER.getName()+"' where BusinessID="+bizId+" and ShareID='"+shareId+"'";
				pst=conn.prepareStatement(updateShareIdSql);
				pst.execute();
				DbUtil.DbCloseExecute(pst);

			}
			String insertDisBatchSql="insert into HASYS_DM_DID(id,BusinessID,DID,ModifyUserID,ModifyTime) values(S_HASYS_DM_DID.nextval,"+bizId+",'"+disBatchId+"','"+userId+"',sysdate)";
			pst=conn.prepareStatement(insertDisBatchSql);
			pst.execute();
			conn.commit();
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
			String getJsonSql="select CONFIGJSON from HASYS_DM_BIZTEMPLATERECOVER where BUSINESSID=? and templateId=?";
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
	
	public String getTableName(String tempTableName){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String dbTableName=null;
		try {
			conn=this.getDbConnection();
			String selectTable="select table_name from user_tables where table_name=?";
			pst=conn.prepareStatement(selectTable);
			pst.setString(1,tempTableName);
			rs=pst.executeQuery();
			while(rs.next()){
				dbTableName=rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return dbTableName;
	}
	
}
