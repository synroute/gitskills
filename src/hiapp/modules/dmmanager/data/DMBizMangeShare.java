package hiapp.modules.dmmanager.data;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dmmanager.DataPool;
import hiapp.modules.dmmanager.ShareBatchItemS;
import hiapp.modules.dmmanager.TreePool;
import hiapp.modules.dmmanager.UserItem;
import hiapp.system.buinfo.Permission;
import hiapp.system.buinfo.RoleInGroupSet;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.data.PermissionRepository;
import hiapp.system.buinfo.data.UserRepository;
import hiapp.system.dictionary.Dict;
import hiapp.system.dictionary.DictItem;
import hiapp.system.dictionary.dicItemsTreeBranch;
import hiapp.system.dictionary.dictTreeBranch;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.serviceresult.ServiceResultCode;
import hiapp.utils.serviceresult.TreeNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DMBizMangeShare extends BaseRepository{
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private UserRepository userRepository;
	    //根据用户的权限 获取到所有的共享批次数据
		public List<ShareBatchItem> getUserShareBatch(String id,int businessID) {
			String sql = "";
			PreparedStatement stmt = null;
			Connection dbConn = null;
			ResultSet rs = null;
			String  HAUDMBCDATAM3="HAU_DM_B"+businessID+"C_DATAM3";
			List<ShareBatchItem> shareBatchItem= new ArrayList<ShareBatchItem>();
			RoleInGroupSet roleInGroupSet=userRepository.getRoleInGroupSetByUserId(id);
			Permission permission = permissionRepository.getPermission(roleInGroupSet);
			int permissionId = permission.getId();
			try {
				dbConn = this.getDbConnection();
				sql="SELECT DISTINCT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME,B.ABC FROM HASYS_DM_SID A ,(SELECT SHAREID,BUSINESSID,COUNT(1) AS ABC FROM "+HAUDMBCDATAM3+" GROUP BY SHAREID,BUSINESSID ) B,(SELECT DATAPOOLTYPE from HASYS_DM_DATAPOOL where BUSINESSID=?) C WHERE C.DATAPOOLTYPE=? AND A.SHAREID=B.SHAREID AND A.BUSINESSID=B.BUSINESSID AND A.BUSINESSID=? AND NOT EXISTS(SELECT 1 FROM HASYS_DM_SID WHERE SHAREID=A.SHAREID AND ID>A.ID) ORDER BY A.CREATETIME";
				stmt = dbConn.prepareStatement(sql);
				stmt.setInt(1,businessID);
				stmt.setInt(2,permissionId);
				stmt.setInt(3,businessID); 
				rs = stmt.executeQuery();
				while (rs.next()) {
					ShareBatchItem shareBatchItems = new ShareBatchItem();
					shareBatchItems.setId(rs.getInt(1));
					shareBatchItems.setBizId(rs.getInt(2));
					shareBatchItems.setShareBatchId(rs.getString(3));
					shareBatchItems.setShareBatchName(rs.getString(4));
					shareBatchItems.setCreateUserId(rs.getString(5));
					shareBatchItems.setCreateTime(rs.getTime(6));
					shareBatchItems.setDescription(rs.getString(7));
					String state = (String)rs.getObject(8);
					ShareBatchStateEnum shareBatchStateEnum = null;
					if("enable".equals(state)){
						shareBatchStateEnum = ShareBatchStateEnum.ENABLE;
					}else if("active".equals(state)){
						shareBatchStateEnum = ShareBatchStateEnum.ACTIVE;
					}else if("pause".equals(state)){
						shareBatchStateEnum = ShareBatchStateEnum.PAUSE;
					}else if("stop".equals(state)){
						shareBatchStateEnum = ShareBatchStateEnum.STOP;
					}else if("expired".equals(state)){
						shareBatchStateEnum = ShareBatchStateEnum.EXPIRED;
					}
					shareBatchItems.setState(shareBatchStateEnum);
					shareBatchItems.setStartTime(rs.getDate(9));
					shareBatchItems.setEndTime(rs.getDate(10));
					shareBatchItem.add(shareBatchItems);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return shareBatchItem;
		}
		//根据userid的权限 获取到规定时间内的共享批次数据，通过业务id
				@SuppressWarnings("resource")
				public Map<String,Object> getUserShareBatchByTime(String businessID,
						String startTime, String endTime,List<ShareBatchItemS> shareBatchItem,int permissionId,Integer num,Integer pageSize) {
					String sql = "";
					String sql1="";
					PreparedStatement stmt = null;
					Connection dbConn = null;
					ResultSet rs = null;
					int bizid=Integer.valueOf(businessID);
					Integer startNum=(num-1)*pageSize+1;
					Integer endNum=num*pageSize+1;
					String HAUDMBCDATAM3="HAU_DM_B"+bizid+"C_DATAM3";
					Map<String,Object> resultMap=new HashMap<String, Object>();
					try {
						dbConn = this.getDbConnection();
						sql="SELECT DISTINCT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME,B.ABC,rownum rn FROM HASYS_DM_SID A ,(SELECT SHAREID,BUSINESSID,COUNT(1) AS ABC FROM "+HAUDMBCDATAM3+" GROUP BY SHAREID,BUSINESSID ) B,(SELECT DATAPOOLTYPE from HASYS_DM_DATAPOOL where BUSINESSID=?) C WHERE C.DATAPOOLTYPE=? AND A.SHAREID=B.SHAREID AND A.BUSINESSID=B.BUSINESSID AND A.CREATETIME >to_date(?,'yyyy-mm-dd hh24:mi:ss') AND A.CREATETIME < to_date(?,'yyyy-mm-dd hh24:mi:ss') AND A.BUSINESSID=? AND NOT EXISTS(SELECT 1 FROM HASYS_DM_SID WHERE SHAREID=A.SHAREID AND ID>A.ID) and rownum<? ORDER BY A.CREATETIME";
						sql1="SELECT DISTINCT ID,BUSINESSID,SHAREID,SHARENAME,CREATEUSERID,CREATETIME,DESCRIPTION,STATE,STARTTIME,ENDTIME,ABC from (";
						sql=sql1+sql+") m where rn>=?";
						stmt = dbConn.prepareStatement(sql);
						stmt.setInt(1,bizid);
						stmt.setInt(2,permissionId);
						stmt.setString(3,startTime);
						stmt.setString(4,endTime);
						stmt.setInt(5,bizid);
						stmt.setInt(6,endNum);
						stmt.setInt(7,startNum);
						rs = stmt.executeQuery();
						while (rs.next()) {
							ShareBatchItemS shareBatchItems = new ShareBatchItemS();
							shareBatchItems.setId(rs.getInt(1));
							shareBatchItems.setBizId(rs.getInt(2));
							shareBatchItems.setShareBatchId(rs.getString(3));
							shareBatchItems.setShareBatchName(rs.getString(4));
							shareBatchItems.setCreateUserId(rs.getString(5));
							shareBatchItems.setCreateTime(rs.getTime(6));
							shareBatchItems.setDescription(rs.getString(7));
							String state = (String)rs.getObject(8);
							String shareBatchStateEnum = null;
							if("enable".equals(state)){
								shareBatchStateEnum ="启用";
							}else if("active".equals(state)){
								shareBatchStateEnum ="激活";
							}else if("pause".equals(state)){
								shareBatchStateEnum ="暂停";
							}else if("stop".equals(state)){
								shareBatchStateEnum ="停止";
							}else if("expired".equals(state)){
								shareBatchStateEnum ="过期";
							}else{
								shareBatchStateEnum ="";
							}
							shareBatchItems.setState(shareBatchStateEnum);
							shareBatchItems.setStartTime(rs.getDate(9));
							shareBatchItems.setEndTime(rs.getDate(10));
							shareBatchItems.setAbc(rs.getInt(11));
							shareBatchItem.add(shareBatchItems);
						}
						
						String getCountSql="select count(*) from (SELECT DISTINCT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME,B.ABC FROM HASYS_DM_SID A ,(SELECT SHAREID,BUSINESSID,COUNT(1) AS ABC FROM "+HAUDMBCDATAM3+" GROUP BY SHAREID,BUSINESSID ) B,(SELECT DATAPOOLTYPE from HASYS_DM_DATAPOOL where BUSINESSID=?) C WHERE C.DATAPOOLTYPE=? AND A.SHAREID=B.SHAREID AND A.BUSINESSID=B.BUSINESSID AND A.CREATETIME >to_date(?,'yyyy-MM-dd hh24:mi:ss') AND A.CREATETIME < to_date(?,'yyyy-MM-dd hh24:mi:ss') AND A.BUSINESSID=? AND NOT EXISTS(SELECT 1 FROM HASYS_DM_SID WHERE SHAREID=A.SHAREID AND ID>A.ID) ORDER BY A.CREATETIME)";
						stmt=dbConn.prepareStatement(getCountSql);
						stmt.setInt(1,bizid);
						stmt.setInt(2,permissionId);
						stmt.setString(3,startTime);
						stmt.setString(4,endTime);
						stmt.setInt(5,bizid);
						rs=stmt.executeQuery();
						Integer total=null;
						while(rs.next()){
							total=rs.getInt(1);
						}
						resultMap.put("total", total);
						resultMap.put("rows",shareBatchItem);
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						DbUtil.DbCloseQuery(rs,stmt);
						DbUtil.DbCloseConnection(dbConn);
					}
					return resultMap;
				}
		//接收一个共享批次号 设置共享批次的启动时间和结束时间  
		public ServiceResultCode setShareDataTime(String[] shareID, String startTime,
				String endTime, User user) throws Exception {
			String sql =null; 
			PreparedStatement stmt = null;
			Connection dbConn = null;
			StringBuilder sb=new StringBuilder();
			for (int i = 0; i < shareID.length; i++) {
			sb.append(shareID[i]+",");
			}
			sb.toString();
			sb.deleteCharAt(sb.length()-1);
			try {
				dbConn=this.getDbConnection();
				sql = "UPDATE HASYS_DM_SID SET CREATEUSERID=?,STARTTIME=to_date(?,'yyyy-MM-dd hh24:mi:ss'),ENDTIME=to_date(?,'yyyy-MM-dd hh24:mi:ss') WHERE SHAREID in ('"+sb+"')";
				stmt = dbConn.prepareStatement(sql);
				stmt.setString(1,user.getId()); 
				stmt.setString(2,startTime);
				stmt.setString(3,endTime);
				stmt.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ServiceResultCode.SUCCESS;
		}
		//设置共享数据是启动还是停止还是暂停ShareID
		public ServiceResultCode modifyShareState(String[] shareID, Integer flag) {
			String sql = "";
			PreparedStatement stmt = null;
			Connection dbConn = null;
			StringBuilder sb=new StringBuilder();
			String shareBatchStateEnum = null;
			if("1".equals(String.valueOf(flag))){
				shareBatchStateEnum ="enable";
			}else if("2".equals(String.valueOf(flag))){
				shareBatchStateEnum ="pause";
			}else if("3".equals(String.valueOf(flag))){
				shareBatchStateEnum ="stop";
			}
			for (int i = 0; i < shareID.length; i++) {
			sb.append(shareID[i]+",");
			}
			sb.toString();
			sb.deleteCharAt(sb.length()-1);
			try {
				dbConn=this.getDbConnection();
				sql=String.format("UPDATE HASYS_DM_SID SET STATE='%s' WHERE SHAREID IN ('"+sb+"')",shareBatchStateEnum);
				stmt = dbConn.prepareStatement(sql);
				stmt.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ServiceResultCode.SUCCESS;
		}
        //通过业务id 和用户id获取所在数据池的id
		public String addShareCustomerfByUserId(
				String businessID, String s) {
			String sql = "";
			PreparedStatement stmt = null;
			Connection dbConn = null;
			ResultSet rs = null;
			String DataPoolName = null;
			int bizID=Integer.valueOf(businessID);
			try {
				dbConn=this.getDbConnection();
				sql=String.format("SELECT DATAPOOLNAME FROM HASYS_DM_DATAPOOL WHERE ID='%s' AND BUSINESSID=%s",s,bizID);
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
				DataPoolName = rs.getString(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.DbCloseQuery(rs,stmt);
				DbUtil.DbCloseConnection(dbConn);
			}
			return DataPoolName;
		}
        // 将 获得共享数据的用户  填入 座席池所属共享批次信息表
		public void addShareCustomerfByUserIds(String uid, String[] shareID,
				String businessID, String DataPoolName) {
			String insertsql = "";
			PreparedStatement stmt = null;
			Connection dbConn = null;
			int datapoolid=Integer.valueOf(uid);
			//auto
			Integer bizid = Integer.valueOf(businessID);
			try {
				dbConn=this.getDbConnection();
				String deleteSql="delete from HASYS_DM_SIDUSERPOOl where SHAREID in(";
				for (int i = 0; i < shareID.length; i++) {
					String shareid = shareID[i];
					if(shareid==null&&"".equals(shareid)){
						continue;
					}
					deleteSql+="'"+shareid+"',";
				}
				deleteSql=deleteSql.substring(0,deleteSql.length()-1)+")";
				stmt = dbConn.prepareStatement(deleteSql);
				stmt.executeUpdate();
				
				for (int i = 0; i < shareID.length; i++) {
					String shareid = shareID[i];
					if(shareid==null&&"".equals(shareid)){
						continue;
					}
					insertsql=String.format("INSERT INTO HASYS_DM_SIDUSERPOOl (ID,BUSINESSID,SHAREID,DATAPOOLNAME,DATAPOOLID) VALUES (S_HASYS_DM_SIDUSERPOOl.NEXTVAL,%s,'%s','%s',%s)",bizid,shareid,DataPoolName,datapoolid);
					stmt = dbConn.prepareStatement(insertsql);
				}
				stmt.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.DbCloseConnection(dbConn);
			}	
		}

		//根据业务id查询所有数据池名称及其子父
		public void getUserPoolTree(int permissionId, TreePool tree,Integer businessID) {
			PreparedStatement stmt = null;
			Connection dbConn = null;
			ResultSet rs = null;
			Integer biz = Integer.valueOf(businessID);
			try {
				dbConn=this.getDbConnection();
				String sql="select a.id,a.datapoolname,a.pid,c.groupid,c.groupname,d.username "+
							"from HASYS_DM_DATAPOOL a "+
							"left join BU_MAP_USERORGROLE b on a.datapoolname = b.userid "+
							"left join BU_INF_GROUP c on b.groupid = c.groupid "+
							"left join BU_INF_USER d on d.userid = b.userid "+
							"where a.businessid = 25 and a.id ="+
							"(select p.datapoolid from HASYS_DM_PER_MAP_POOL  p where p.businessid=? and p.permissionid=? and p.itemname='数据管理')";
				stmt = dbConn.prepareStatement(sql);
				stmt.setInt(1, biz);
				stmt.setInt(2,permissionId);
				rs = stmt.executeQuery();
				while (rs.next()) {
					tree.setId(rs.getInt(1));
					tree.setDataPoolName(rs.getString(2));
					tree.setPid(rs.getInt(3));
					tree.setGroupId(rs.getInt(4));
					tree.setGroupName(rs.getString(5));
					tree.setUserName(rs.getString(6));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.DbCloseQuery(rs,stmt);
				DbUtil.DbCloseConnection(dbConn);
			}
			}
		
		
		public  List<TreePool> getChildrenTreePoolByPid(Integer bizId,Integer pid){
			List<TreePool> treePools=new ArrayList<TreePool>();
			Connection conn = null;
			PreparedStatement pst= null;
			ResultSet rs=null;
			try {
				conn=this.getDbConnection();
				String sql="select a.id,a.datapoolname,a.pid,d.username from HASYS_DM_DATAPOOL a "+
						   "left join BU_MAP_USERORGROLE b on a.datapoolname = b.userid "+
						   "left join BU_INF_USER d on d.userid = b.userid "+
						   "where a.businessid=? and a.pid=?";
				pst=conn.prepareStatement(sql);
				pst.setInt(1,bizId);
				pst.setInt(2,pid);
				rs=pst.executeQuery();
				while(rs.next()){
					TreePool tree=new TreePool();
					tree.setId(rs.getInt(1));
					tree.setDataPoolName(rs.getString(2));
					tree.setPid(rs.getInt(3));
					tree.setUserName(rs.getString(4));
					treePools.add(tree);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return treePools;
		}
		
		public UserItem getUserPoolTreeByPermissionID(Integer bizId,
				TreePool pool) {
				UserItem userItem=new UserItem();
				List<UserItem> userItemList=new ArrayList<UserItem>();
				UserItem userItem1=new UserItem();
			try {
					userItem.setId(Integer.toString(pool.getId()));
					userItem.setText(pool.getGroupId()+" "+pool.getGroupName());
					userItem.setState("");
					userItem.setDicId(pool.getId());
					userItem.setItemText(pool.getDataPoolName());
					userItem.setItemId(pool.getPid());
					
					userItem1.setId(Integer.toString(pool.getId()));
					userItem1.setText(pool.getDataPoolName()+" "+pool.getUserName());
					userItem1.setState("");
					userItem1.setDicId(pool.getId());
					userItem1.setItemText(pool.getDataPoolName());
					userItem1.setItemId(pool.getPid());
					userItemList.add(userItem1);
					userItem.setChildren(userItemList);
					addChildren(userItem1,bizId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return userItem;
		}
		//递归
		public void addChildren(UserItem userTreeBranch,Integer bizId) {
			    List<UserItem> listChildrenBranchs = userTreeBranch.getChildren();
			    Integer pid=Integer.valueOf(userTreeBranch.getId());
			    List<TreePool>  treePoolList=getChildrenTreePoolByPid(bizId,pid);
			    for (int ii = 0; ii < treePoolList.size(); ii++) {
					TreePool TreePoolBranch = treePoolList.get(ii);
					UserItem treeBranch = new UserItem();
					treeBranch.setId(String.valueOf(TreePoolBranch.getId()));
					treeBranch.setText(TreePoolBranch.getDataPoolName()+" "+TreePoolBranch.getUserName());
					treeBranch.setState("");
					treeBranch.setDicId(TreePoolBranch.getId());
					treeBranch.setItemText(TreePoolBranch.getDataPoolName());
					treeBranch.setItemId(TreePoolBranch.getPid());
					listChildrenBranchs.add(treeBranch);
					addChildren(treeBranch,bizId);
				}
			}
		@SuppressWarnings("resource")
		public ServiceResultCode DeleteShareBatchDataByShareId(String[] shareId,int businessID) throws Exception {
			String deSidSql="";
			String deDataM3Sql="";
			String deDataM3HisSql="";
			String dePoolSql="";
			String dePoolOreSql="";
			String deUserPoolSql="";
			String shareid="";
			//单号码重拨共享表
			String HASYS_DM_BC_DATAM3="HAU_DM_B"+businessID+"C_DATAM3";
			//单号码重拨共享历史表
			String HAU_DM_BC_DATAM3_HIS= "HAU_DM_B"+businessID+"C_DATAM3_HIS";
			//数据池记录表
			String HAU_DM_BC_POOL="HAU_DM_B"+businessID+"C_POOL";
			//数据池操作记录表
			String HAU_DM_BC_POOL_ORE="HAU_DM_B"+businessID+"C_POOL_ORE";
			PreparedStatement stmt = null;
	        Connection dbConn=null;
	        try {
	        	dbConn=this.getDbConnection();
	        for (int i = 0; i < shareId.length; i++) {
			shareid = shareId[i];
				//不自动提交数据
				dbConn.setAutoCommit(false);
				//删除共享批次信息表里面的数据
				deSidSql=String.format("DELETE FROM HASYS_DM_SID WHERE SHAREID='%s'",shareid);
				stmt = dbConn.prepareStatement(deSidSql);
				stmt.execute();
				//单号码重播共享表
				deDataM3Sql=String.format("DELETE FROM "+HASYS_DM_BC_DATAM3+" WHERE SHAREID='%s'",shareid);
				stmt = dbConn.prepareStatement(deDataM3Sql);
				stmt.execute();
				//单号码重播共享表历史表dePoolSql
				deDataM3HisSql=String.format("DELETE FROM "+HAU_DM_BC_DATAM3_HIS+" WHERE SHAREID='%s'",shareid);
				stmt = dbConn.prepareStatement(deDataM3HisSql);
				stmt.execute();
				//更改数据池记录表数据
				dePoolSql=String.format("DELETE FROM "+HAU_DM_BC_POOL+" WHERE SourceID='%s'",shareid);
				stmt = dbConn.prepareStatement(dePoolSql);
				stmt.execute();
				//数据池操作记录表
				dePoolOreSql=String.format("DELETE FROM "+HAU_DM_BC_POOL_ORE+" WHERE SourceID='%s'",shareid);
				stmt = dbConn.prepareStatement(dePoolOreSql);
				stmt.execute();
				//座席池所属共享批次信息表Hasys_DM_SIDUserPool
				deUserPoolSql=String.format("DELETE FROM HASYS_DM_SIDUSERPOOL WHERE SHAREID='%s'",shareid);
				stmt = dbConn.prepareStatement(deUserPoolSql);
				stmt.execute();
				//无异常提交代码
				dbConn.commit();
	        }
			} catch (Exception e) {
				//有异常回滚
				dbConn.rollback();
				e.printStackTrace();
				return ServiceResultCode.INVALID_SESSION;
			}
			finally {
				DbUtil.DbCloseConnection(dbConn);
				DbUtil.DbCloseExecute(stmt);
			}
			return ServiceResultCode.SUCCESS;
		}	
		
		
		public List<String> getSidUserPool(Integer bizId,String userId){
			PreparedStatement pst = null;
	        Connection conn=null;
			ResultSet rs=null;
			List<String> dataList=new ArrayList<String>();
			try {
				conn=this.getDbConnection();
				String  sql="select ShareID from Hasys_DM_SIDUserPool where BusinessID=? and DataPoolName=?";
				pst=conn.prepareStatement(sql);
				pst.setInt(1,bizId);
				pst.setString(2,userId);
				rs=pst.executeQuery();
				while(rs.next()){
					dataList.add(rs.getString(1));
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return dataList;
			
		}
		
		
		
}
