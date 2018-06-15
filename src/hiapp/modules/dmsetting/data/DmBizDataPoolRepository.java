package hiapp.modules.dmsetting.data;

import java.nio.channels.SelectableChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntToDoubleFunction;

import javax.swing.Spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dmsetting.DMDataPool;
import hiapp.modules.dmsetting.result.DMBizDatePoolGetUserId;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.data.UgrRepository;
import hiapp.system.buinfo.data.UserRepository;
import hiapp.system.buinfo.srv.result.GroupTreeBranch;
import hiapp.system.buinfo.srv.result.UserView;
import hiapp.system.notice.bean.Notice;
import hiapp.system.notice.bean.NoticeState;
import hiapp.system.notice.service.NoticeService;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

@Repository
public class DmBizDataPoolRepository  extends BaseRepository {
	@Autowired
    private NoticeService noticeService;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UgrRepository ugrRepository;
	//创建普通数据池
	public boolean dmCreateBizDataPool(DMDataPool dataPool,StringBuffer err)
	{
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn =this.getDbConnection();
			int poolTopLimit=0;
			if(dataPool.getPoolTopLimit()!=0)
			{
				poolTopLimit=dataPool.getPoolTopLimit();
			}
			
			//判断是否有活动的共享批次
			String selectsql=String.format("select count(*) from HASYS_DM_DATAPOOL where DataPoolName='"+dataPool.getDataPoolName()+"' and BusinessID="+dataPool.getBizId()+"");
			stmt = dbConn.prepareStatement(selectsql);
			rs = stmt.executeQuery();
			int count=0;
			while(rs.next())
			{
				count=rs.getInt(1);
			}
			if (count>0) {
				
				err.append("数据池名称重复！");
				return false;
			}
			stmt.close();
			String poolsql=String.format("select PoolTopLimit from HASYS_DM_DATAPOOL where PID="+dataPool.getpId()+" and BusinessID="+dataPool.getBizId()+"");
			stmt = dbConn.prepareStatement(poolsql);
			rs = stmt.executeQuery();
			
			while(rs.next())
			{
				poolTopLimit+=rs.getInt(1);
			}
			stmt.close();
			String poolpid=String.format("select PoolTopLimit from HASYS_DM_DATAPOOL where ID="+dataPool.getpId()+" and BusinessID="+dataPool.getBizId()+"");
			stmt = dbConn.prepareStatement(poolpid);
			rs = stmt.executeQuery();
			
			int poolPid=0;
			while(rs.next())
			{
				poolPid=rs.getInt(1);
			}
			stmt.close();
			if (poolTopLimit>poolPid) {
				
				err.append("数据池上限已超过！");
				return false;
			}
			
			
			String szSql = String.format("insert into HASYS_DM_DATAPOOL(ID,BusinessID,DataPoolName,DataPoolType,DataPoolDes,PID,AreaType,PoolTopLimit,isDelete)"+
			" values(S_HASYS_DM_DATAPOOL.nextval,%s,'%s',2,'%s',%s,0,%s,0)",dataPool.getBizId(),dataPool.getDataPoolName(),dataPool.getDataPoolDesc(),dataPool.getpId(),dataPool.getPoolTopLimit());
			stmt = dbConn.prepareStatement(szSql);
			stmt.executeUpdate();
			
			String select =String.format("select ID from (select ID from HASYS_DM_DATAPOOL order by ID desc)  WHERE ROWNUM <=1 ");
			stmt = dbConn.prepareStatement(select);
			rs = stmt.executeQuery();
			while(rs.next()){
				dataPool.setPoolId(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}
	//修改数据池
	public boolean dmModifyBizDataPool(DMDataPool dataPool,StringBuffer err)
	{
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn =this.getDbConnection();
			int poolLimit=0;
			if(dataPool.getPoolTopLimit()!=0)
			{
				poolLimit=dataPool.getPoolTopLimit();
			}
			String poolsql=String.format("select PoolTopLimit from HASYS_DM_DATAPOOL where PID=(select ID from HASYS_DM_DATAPOOL where ID="+dataPool.getPoolId()+")");
			stmt = dbConn.prepareStatement(poolsql);
			rs = stmt.executeQuery();
			
			while(rs.next())
			{
				poolLimit+=rs.getInt(1);
			}
			stmt.close();
			String poolpid=String.format("select PoolTopLimit from HASYS_DM_DATAPOOL where ID=(select PID from HASYS_DM_DATAPOOL where ID="+dataPool.getPoolId()+")");
			stmt = dbConn.prepareStatement(poolpid);
			rs = stmt.executeQuery();
			
			int poolPid=0;
			while(rs.next())
			{
				poolPid=rs.getInt(1);
			}
			stmt.close();
			if (poolLimit>poolPid) {
				
				err.append("数据池上限已超过！");
				return false;
			}
			
			
			
			String szSql = String.format("update HASYS_DM_DATAPOOL set DataPoolName='%s',DataPoolDes='%s',PoolTopLimit='%s' where ID=%s",dataPool.getDataPoolName(),dataPool.getDataPoolDesc(),dataPool.getPoolTopLimit(),dataPool.getPoolId());
			stmt = dbConn.prepareStatement(szSql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return true;
	}
	
	//删除数据池
	
		public boolean dmDeleteBizDataPool(int poolId)
		{
			Connection dbConn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				dbConn =this.getDbConnection();
				String selecttype=String.format("select DataPoolType from HASYS_DM_DATAPOOL where ID=%s",poolId);
				stmt = dbConn.prepareStatement(selecttype);
				rs = stmt.executeQuery();
				int type=0;
				while(rs.next()){
					type=rs.getInt(1);
				}
				stmt.close();
				if(type==3)
				{
					String deleteslq=String.format("delete from Hasys_DM_SIDUserPool where DataPoolID=%s",poolId);
					stmt = dbConn.prepareStatement(deleteslq);
					stmt.executeUpdate();
					stmt.close();
				}
				String szSql = String.format("delete from HASYS_DM_DATAPOOL where ID=%s",poolId);
				stmt = dbConn.prepareStatement(szSql);
				stmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			} 
			finally {
				DbUtil.DbCloseConnection(dbConn);
				DbUtil.DbCloseExecute(stmt);
			}
			return true;
		}
		
	
	
	//创建坐席数据池
		public boolean dmCreateBizUserDataPool(DMDataPool dataPool,String userId ,StringBuffer err)
		{
			Connection dbConn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				
				String[] userids=userId.split(",");
				dbConn =this.getDbConnection();
				String poolsql=String.format("select PoolTopLimit from HASYS_DM_DATAPOOL where ID="+dataPool.getpId()+"");
				stmt = dbConn.prepareStatement(poolsql);
				rs = stmt.executeQuery();
				int poolLimit=0;
				while(rs.next())
				{
					poolLimit=rs.getInt(1);
				}
				int userpool=0;
				stmt.close();
				for(int row=0;row<userids.length;row++){
					
					//判断是否有活动的共享批次
					String selectsql=String.format("select count(*) from HASYS_DM_DATAPOOL where DataPoolName='"+userids[row]+"' and BusinessID="+dataPool.getBizId()+"");
					stmt = dbConn.prepareStatement(selectsql);
					rs = stmt.executeQuery();
					int count=0;
					while(rs.next())
					{
						count=rs.getInt(1);
					}
					if (count>0) {
						
						err.append("数据池名称重复！"+userids[row]);
						return false;
					}
					
					String szSql="";
					userpool=userpool+1000;
					if(userpool<=poolLimit)
					{
						szSql = String.format("insert into HASYS_DM_DATAPOOL(ID,BusinessID,DataPoolName,DataPoolType,PID,AreaType,isDelete,POOLTOPLIMIT)"+
								" values(S_HASYS_DM_DATAPOOL.nextval,%s,'%s',3,%s,0,0,1000)",dataPool.getBizId(),userids[row],dataPool.getpId());
					}else {
						szSql = String.format("insert into HASYS_DM_DATAPOOL(ID,BusinessID,DataPoolName,DataPoolType,PID,AreaType,isDelete,POOLTOPLIMIT)"+
								" values(S_HASYS_DM_DATAPOOL.nextval,%s,'%s',3,%s,0,0,0)",dataPool.getBizId(),userids[row],dataPool.getpId());
					}
					
					
					stmt = dbConn.prepareStatement(szSql);
					stmt.executeUpdate();
					stmt.close();
					
				}
				
				String updateSql=String.format("update HASYS_DM_DATAPOOL set AreaType=2 where ID="+dataPool.getpId()+"");
				stmt = dbConn.prepareStatement(updateSql);
				stmt.executeUpdate();
				stmt.close();
				
			} catch (SQLException e) {
				err.append("失败！");
				e.printStackTrace();
				return false;
			} 
			finally {
				DbUtil.DbCloseConnection(dbConn);
				DbUtil.DbCloseExecute(stmt);
			}
			return true;
		}
		
		//获取数据池详细信息
		public List<DMDataPool> dmGetBizDataPool(int poolid,String type )
		{
			Connection dbConn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			List<DMDataPool> listDmDataPool=new ArrayList<DMDataPool>();
			try {
				dbConn =this.getDbConnection();
				String szSql="";
				if(type.equals("数据池"))
				{
					szSql = String.format("select ID,DataPoolName,DataPoolDes,PoolTopLimit,DataPoolType from HASYS_DM_DATAPOOL where ID=%s",poolid);
				}else {
					szSql = String.format("select ID,DataPoolName,DataPoolDes,PoolTopLimit,DataPoolType from HASYS_DM_DATAPOOL where DataPoolType=3 and  ID in (select DataPoolID from HASYS_DM_PER_MAP_POOL where PermissionID=%s and DataPoolID in (select Id from  HASYS_DM_DATAPOOL where isDelete=0))",poolid);
				}
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				while(rs.next()){
					DMDataPool dmDataPool=new DMDataPool();
					dmDataPool.setPoolId(rs.getInt(1));
					dmDataPool.setDataPoolName(rs.getString(2));
					dmDataPool.setDataPoolDesc(rs.getString(3));
					dmDataPool.setPoolTopLimit(rs.getInt(4));
					dmDataPool.setDataPoolType(rs.getString(5));
					listDmDataPool.add(dmDataPool);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			finally {
				DbUtil.DbCloseConnection(dbConn);
				DbUtil.DbCloseExecute(stmt);
			}
			return listDmDataPool;
		}
		
		//获取业务下所有数据池
				public List<DMDataPool> dmGetAllBizDataPool(int bizId )
				{
					Connection dbConn = null;
					PreparedStatement stmt = null;
					ResultSet rs = null;
					List<DMDataPool> listDmDataPool=new ArrayList<DMDataPool>();
					try {
						dbConn =this.getDbConnection();
						String szSql = String.format("select ID,DataPoolName,PID,DATAPOOLTYPE from HASYS_DM_DATAPOOL where BusinessID=%s and isDelete=0",bizId);
						stmt = dbConn.prepareStatement(szSql);
						rs = stmt.executeQuery();
						while(rs.next()){
							DMDataPool dmDataPool=new DMDataPool();
							dmDataPool.setPoolId(rs.getInt(1));
							dmDataPool.setDataPoolName(rs.getString(2));
							dmDataPool.setpId(rs.getInt(3));
							dmDataPool.setDataPoolType(rs.getString(4));
							listDmDataPool.add(dmDataPool);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} 
					finally {
						DbUtil.DbCloseConnection(dbConn);
						DbUtil.DbCloseExecute(stmt);
					}
					return listDmDataPool;
				}
					//获取该业务下所有可选用户
				public List<DMBizDatePoolGetUserId> dmGetBizUser(int bizId )
				{
					Connection dbConn = null;
					PreparedStatement stmt = null;
					ResultSet rs = null;
					String groupId=null;
					List<DMBizDatePoolGetUserId> listDmBizDatePoolGetUserId = new ArrayList<DMBizDatePoolGetUserId>();
					try {
						dbConn =this.getDbConnection();
						String szSql = String.format("select OwnerGROUPId from HASYS_DM_Business where BusinessID=%s",bizId);
						stmt = dbConn.prepareStatement(szSql);
						rs = stmt.executeQuery();
						while(rs.next()){
							groupId=rs.getString(1);
						}
						
						String[] groupids=groupId.split(",");
						List<GroupTreeBranch> listGroups = new ArrayList<GroupTreeBranch>();
						for (int g = 0; g < groupids.length; g++) {
							ugrRepository.getGroupAndAllChildGroup(listGroups,groupids[g]);
							//通过组获取组下用户信息
							List<User> listUser = new ArrayList<User>();
							ugrRepository.getAllUsersByGroup(listUser,listGroups);
							//拼接信息
							
								GroupTreeBranch groupTreeBranch=listGroups.get(listGroups.size()-1);
								DMBizDatePoolGetUserId dmBizDatePoolGetUserId=new DMBizDatePoolGetUserId();
								dmBizDatePoolGetUserId.setUserId(groupids[g]);
								dmBizDatePoolGetUserId.setUserName(groupTreeBranch.getGroupName());
								dmBizDatePoolGetUserId.setGroupId(0);
								listDmBizDatePoolGetUserId.add(dmBizDatePoolGetUserId);
							
							for (int i = 0; i < listUser.size(); i++) {
								UserView userView = new UserView();
								User user = listUser.get(i);
								DMBizDatePoolGetUserId dmBizDatePoolGetUserIds=new DMBizDatePoolGetUserId();
								dmBizDatePoolGetUserIds.setUserId(user.getId());
								dmBizDatePoolGetUserIds.setUserName(user.getName());
								dmBizDatePoolGetUserIds.setGroupId(Integer.parseInt(groupids[g]));
								
								listDmBizDatePoolGetUserId.add(dmBizDatePoolGetUserIds);
							}
						}
						
					} catch (SQLException e) {
						e.printStackTrace();
					} 
					finally {
						DbUtil.DbCloseQuery(rs, stmt);
						DbUtil.DbCloseConnection(dbConn);
						
					}
					
					
					return listDmBizDatePoolGetUserId;
				}

				public void getDataPool(List<CustomerBasic> list,String messageString,int type){
					Connection conn=null;
					PreparedStatement pst=null;
					ResultSet rs=null;
					
					try {
						conn=this.getDbConnection();
						String poolidString="";
						for(int i=0;i<list.size();i++)
						{
							CustomerBasic customerBasic =list.get(i);
							
							if(type==1)
							{
								String sql="select DataPoolIDCur from HAU_DM_B"+customerBasic.getBizId()+"C_POOL where SourceID='"+customerBasic.getSourceId()+"' and IID='"+customerBasic.getImportBatchId()+"' and CID='"+customerBasic.getCustomerId()+"'";
								pst=conn.prepareStatement(sql);	
								
								rs=pst.executeQuery();
								while(rs.next()){
									poolidString=poolidString+rs.getString(1)+",";
								}
							}else if(type==2){
								String sql="select DataPoolIDLast from HAU_DM_B"+customerBasic.getBizId()+"C_POOL where SourceID='"+customerBasic.getSourceId()+"' and IID='"+customerBasic.getImportBatchId()+"' and CID='"+customerBasic.getCustomerId()+"'";
								pst=conn.prepareStatement(sql);	
								
								rs=pst.executeQuery();
								while(rs.next()){
									poolidString=poolidString+rs.getString(1)+",";
								}
							}else if(type==2){
								String allsqlString="select DataPoolIDCur,DataPoolIDLast from HAU_DM_B"+customerBasic.getBizId()+"C_POOL where SourceID='"+customerBasic.getSourceId()+"' and IID='"+customerBasic.getImportBatchId()+"' and CID='"+customerBasic.getCustomerId()+"'";
								pst=conn.prepareStatement(allsqlString);	
								
								rs=pst.executeQuery();
								while(rs.next()){
									poolidString=poolidString+rs.getString(1)+","+rs.getString(2)+",";
								}
								
								
							}
							
						}
						poolidString=poolidString.substring(0, poolidString.length()-1);
						
						String getDataPoolSql="select DISTINCT userid from BU_MAP_USERORGROLE where ROLEID in (select  ROLEID from BU_MAP_PERMORGROLE where permissionid in (SELECT PermissionID FROM	HASYS_DM_PER_MAP_POOL	WHERE	DataPoolID IN (SELECT	ID	FROM	HASYS_DM_DATAPOOL	WHERE		ID in("+poolidString+") )))";
						pst=conn.prepareStatement(getDataPoolSql);	
						
						rs=pst.executeQuery();
						String userIds="";
						while(rs.next()){
							userIds=userIds+rs.getString(1)+",";
						}
						userIds=userIds.substring(0, userIds.length()-1);
						Notice notice=new Notice();
						notice.setType("2");
						notice.setState(NoticeState.ACTIVED);
						notice.setSender("1001");
						notice.setRecever(userIds);
						notice.setRelatedId("");
						notice.setData(messageString);
						noticeService.sendNotice(notice);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						DbUtil.DbCloseQuery(rs,pst);
						DbUtil.DbCloseConnection(conn);
					}
					
				}
				
		
}
