package hiapp.modules.dmsetting.data;

import java.nio.channels.SelectableChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import hiapp.modules.dmsetting.DMDataPool;
import hiapp.modules.dmsetting.result.DMBizDatePoolGetUserId;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.data.UgrRepository;
import hiapp.system.buinfo.data.UserRepository;
import hiapp.system.buinfo.srv.result.GroupTreeBranch;
import hiapp.system.buinfo.srv.result.UserView;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

@Repository
public class DmBizDataPoolRepository  extends BaseRepository {
	Connection dbConn = null;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UgrRepository ugrRepository;
	//创建普通数据池
	public boolean dmCreateBizDataPool(DMDataPool dataPool )
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn =this.getDbConnection();
			String szSql = String.format("insert into HASYS_DM_DATAPOOL(ID,BusinessID,DataPoolName,DataPoolType,DataPoolDes,PID,AreaType,PoolTopLimit)"+
			" values(S_HASYS_DM_DATAPOOL.nextval,%s,'%s',2,'%s',%s,0,%s)",dataPool.getBizId(),dataPool.getDataPoolName(),dataPool.getDataPoolDesc(),dataPool.getpId(),dataPool.getPoolTopLimit());
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
	//修改数据池
	public boolean dmModifyBizDataPool(DMDataPool dataPool )
	{
		PreparedStatement stmt = null;
		try {
			dbConn =this.getDbConnection();
			String szSql = String.format("update HASYS_DM_DATAPOOL set DataPoolName='%s',DataPoolDes='%s,PoolTopLimit'%s' where ID=%s",dataPool.getDataPoolName(),dataPool.getDataPoolDesc(),dataPool.getPoolTopLimit(),dataPool.getPoolId());
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
		public boolean dmCreateBizUserDataPool(DMDataPool dataPool,JsonArray jArray )
		{
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				
				for(int row=0;row<jArray.size();row++){
					String userId = jArray.get(row).getAsJsonObject().get("userId").getAsString();
					
					dbConn =this.getDbConnection();
					String szSql = String.format("insert into HASYS_DM_DATAPOOL(ID,BusinessID,DataPoolName,DataPoolType,DataPoolDes,PID,AreaType)"+
					" values(HASYS_DM_DATAPOOL.nextval,%s,'%s',2,%s,0)",dataPool.getBizId(),userId,dataPool.getpId());
					stmt = dbConn.prepareStatement(szSql);
					stmt.executeUpdate();
					stmt.close();
					String updateSql=String.format("update HASYS_DM_DATAPOOL set AreaType=2 where ID="+dataPool.getpId()+"");
					stmt = dbConn.prepareStatement(updateSql);
					stmt.executeUpdate();
					stmt.close();
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
		
		//获取数据池详细信息
		public List<DMDataPool> dmGetBizDataPool(int poolid )
		{
			PreparedStatement stmt = null;
			ResultSet rs = null;
			List<DMDataPool> listDmDataPool=new ArrayList<DMDataPool>();
			try {
				dbConn =this.getDbConnection();
				String szSql = String.format("select ID,DataPoolName,DataPoolDes,PoolTopLimit,DataPoolType from HASYS_DM_DATAPOOL where ID=%s",poolid);
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
		
		//获取数据池详细信息
				public List<DMDataPool> dmGetAllBizDataPool(int bizId )
				{
					PreparedStatement stmt = null;
					ResultSet rs = null;
					List<DMDataPool> listDmDataPool=new ArrayList<DMDataPool>();
					try {
						dbConn =this.getDbConnection();
						String szSql = String.format("select ID,DataPoolName,PID from HASYS_DM_DATAPOOL where BusinessID=%s",bizId);
						stmt = dbConn.prepareStatement(szSql);
						rs = stmt.executeQuery();
						while(rs.next()){
							DMDataPool dmDataPool=new DMDataPool();
							dmDataPool.setPoolId(rs.getInt(1));
							dmDataPool.setDataPoolName(rs.getString(2));
							dmDataPool.setpId(rs.getInt(3));
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
					PreparedStatement stmt = null;
					ResultSet rs = null;
					String groupId="";
					try {
						dbConn =this.getDbConnection();
						String szSql = String.format("select OwnerGROUPId from HASYS_DM_Business where BusinessID=%s",bizId);
						stmt = dbConn.prepareStatement(szSql);
						rs = stmt.executeQuery();
						while(rs.next()){
							groupId=rs.getString(1);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} 
					finally {
						DbUtil.DbCloseConnection(dbConn);
						DbUtil.DbCloseExecute(stmt);
					}
					List<DMBizDatePoolGetUserId> listDmBizDatePoolGetUserId = new ArrayList<DMBizDatePoolGetUserId>();
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
					
					
					return listDmBizDatePoolGetUserId;
				}

		
		
}
