package hiapp.modules.dmmanager.data;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dmmanager.DataPool;
import hiapp.modules.dmmanager.ShareBatchItemS;
import hiapp.modules.dmmanager.TreePool;
import hiapp.modules.dmmanager.UserItem;
import hiapp.system.buinfo.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Repository;

@Repository
public class DMBizMangeShare extends BaseRepository{

	//根据userid的权限 获取到所有的共享批次数据
		public List<ShareBatchItem> getUserShareBatch(List<ShareBatchItem> shareBatchItem, User user) {
			String sql = "";
			PreparedStatement stmt = null;
			Connection dbConn = null;
			ResultSet rs = null;
			try {
				dbConn = this.getDbConnection();
				// 查询 共享批次信息表 里面所有共享批次   来自共享批次信息表 a，座席池所属共享批次信息表 b,条件 B.数据池名称=user.getname and a.共享批次id=b.共享批次id                                        HASYS_DM_SID
				//SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTIME,A.ENDTIME FROM HASYS_DM_SID A,HASYS_DM_SIDUSERPOOL B WHERE B.DATAPOOlNAME='%s' AND B.SHAREID=A.SHAREID;
				                 //SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME FROM HASYS_DM_SID A,HASYS_DM_SIDUSERPOOL B WHERE B.DATAPOOlNAME='管' AND B.SHAREID=A.SHAREID
				//sql=String.format("SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME FROM HASYS_DM_SID A,HASYS_DM_SIDUSERPOOL B WHERE B.DATAPOOlNAME='%s' AND B.SHAREID=A.SHAREID",user.getName());
				sql=String.format("SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME FROM HASYS_DM_SID A WHERE NOT EXISTS(SELECT 1 FROM HASYS_DM_SID WHERE SHAREID=A.SHAREID AND ID>A.ID) ORDER BY A.CREATETIME");
				stmt = dbConn.prepareStatement(sql);
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
				public List<ShareBatchItemS> getUserShareBatchByTime(String businessID,
						String startTime, String endTime,List<ShareBatchItemS> shareBatchItem) {
					String sql = "";
					PreparedStatement stmt = null;
					Connection dbConn = null;
					ResultSet rs = null;
					try {
						dbConn = this.getDbConnection();
						   //SELECT DISTINCT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME,B.ABC FROM HASYS_DM_SID A ,(SELECT SHAREID,BUSINESSID,COUNT(1) AS ABC FROM HASYS_DM_B1C_DATAM3 GROUP BY SHAREID,BUSINESSID )B WHERE A.SHAREID=B.SHAREID AND A.BUSINESSID=B.BUSINESSID AND A.CREATETIME >to_date('09/02/2017','mm/dd/yyyy') AND A.CREATETIME < to_date('09/20/2017','mm/dd/yyyy') AND A.BUSINESSID=2
						//sql="SELECT DISTINCT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME,B.ABC FROM HASYS_DM_SID A LEFT JOIN (SELECT SHAREID,BUSINESSID,COUNT(1) AS ABC FROM HASYS_DM_B1C_DATAM3 GROUP BY SHAREID,BUSINESSID )B ON A.SHAREID=B.SHAREID AND A.BUSINESSID=B.BUSINESSID AND A.BUSINESSID="+businessID+" AND A.CREATETIME BETWEEN to_date('"+startTime+"','mm/dd/yyyy') AND to_date('"+endTime+"','mm/dd/yyyy')";
						//  sql="SELECT DISTINCT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME,B.ABC FROM HASYS_DM_SID A ,(SELECT SHAREID,BUSINESSID,COUNT(1) AS ABC FROM HASYS_DM_B1C_DATAM3 GROUP BY SHAREID,BUSINESSID )B WHERE A.SHAREID=B.SHAREID AND A.BUSINESSID=B.BUSINESSID AND A.CREATETIME >to_date('"+startTime+"','mm/dd/yyyy') AND A.CREATETIME < to_date('"+endTime+"','mm/dd/yyyy') AND A.BUSINESSID="+businessID+"";
						sql="SELECT DISTINCT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME,B.ABC FROM HASYS_DM_SID A ,(SELECT SHAREID,BUSINESSID,COUNT(1) AS ABC FROM HASYS_DM_B1C_DATAM3 GROUP BY SHAREID,BUSINESSID )B WHERE  A.SHAREID=B.SHAREID AND A.BUSINESSID=B.BUSINESSID AND A.CREATETIME >to_date('"+startTime+"','mm/dd/yyyy') AND A.CREATETIME < to_date('"+endTime+"','mm/dd/yyyy') AND A.BUSINESSID="+businessID+" AND NOT EXISTS(SELECT 1 FROM HASYS_DM_SID WHERE SHAREID=A.SHAREID AND ID>A.ID) ORDER BY A.CREATETIME";
						stmt = dbConn.prepareStatement(sql);
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
								shareBatchStateEnum ="未操作";
							}
							shareBatchItems.setState(shareBatchStateEnum);
							shareBatchItems.setStartTime(rs.getDate(9));
							shareBatchItems.setEndTime(rs.getDate(10));
							shareBatchItems.setAbc(rs.getInt(11));
							shareBatchItem.add(shareBatchItems);
						}//select count(SHAREID)　from HASYS_DM_B1C_DATAM3   WHERE SHAREID='DM_SID_20170917_601' 
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						DbUtil.DbCloseQuery(rs,stmt);
						DbUtil.DbCloseConnection(dbConn);
					}
					return shareBatchItem;
				}
		//接收一个共享批次号 设置共享批次的启动时间和结束时间  
		public ServiceResultCode setShareDataTime(String[] shareID, String startTime,
				String endTime, User user) {
			String sql =null; //"UPDATA HASYS_DM_SID SET CREATEUSERID="+user.getId()+",STARTTIME=to_date('"+startTime+"','mm/dd/2017'),ENDTIME=to_date('"+endTime+"','mm/dd/2017') WHERE SHAREID in "+s+"";
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
				//sql=String.format("UPDATA HASYS_DM_SID SET CREATEUSERID='%s',STARTTIME=to_date('%s','mm/dd/2017'),ENDTIME=to_date('%s','mm/dd/2017') WHERE SHAREID='%s'",user.getId(),startTime,endTime,shareID);
				sql = "UPDATE HASYS_DM_SID SET CREATEUSERID='"+user.getId()+"',STARTTIME=to_date('"+startTime+"','mm/dd/yyyy'),ENDTIME=to_date('"+endTime+"','mm/dd/yyyy') WHERE SHAREID in ('"+sb+"')";
				
				stmt = dbConn.prepareStatement(sql);
				stmt.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ServiceResultCode.SUCCESS;
		}
		//查询本批次创建人权限下共享区内所属座席池
		public List<User> selectShareCustomer(User user) {
			String sql = "";
			PreparedStatement stmt = null;
			Connection dbConn = null;
			ResultSet rs = null;
			List<User> listUser = null;
			try {
				dbConn = this.getDbConnection();
				sql = String.format("SELECT USERNAME FROM BU_INF_USER WHERE USERID='%s'",user.getId());
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				listUser = new ArrayList<User>();
				while (rs.next()) {
					User User = new User();
					User.setId(rs.getString(1));
					User.setName(rs.getString(2));
					User.setAgentId(rs.getString(3));
					listUser.add(User);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.DbCloseQuery(rs, stmt);
				DbUtil.DbCloseConnection(dbConn);
			}
			return listUser;
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
				sql=String.format("SELECT DATAPOOLNAME FROM HASYS_DM_DATAPOOL WHERE ID='%s'",s);
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
			StringBuilder sb=new StringBuilder();
			try {
				for (int i = 0; i < shareID.length; i++) {
					String shareid = shareID[i];
					sb.append(shareid);
					sb.deleteCharAt(sb.length()-1);
					dbConn=this.getDbConnection();
					insertsql=String.format("INSERT INTO HASYS_DM_SIDUSERPOOl (ID,BUSINESSID,SHAREID,DATAPOOLNAME,DATAPOOLID) VALUES (S_HASYS_DM_SIDUSERPOOl.NEXTVAL,%s,'%s','%s',%s)",businessID,sb,DataPoolName,datapoolid);
					stmt = dbConn.prepareStatement(insertsql);
				}
				
				stmt.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.DbCloseConnection(dbConn);
			}	
		}
		
		public DataPool selectShareCustomer(int permissionId) {
			String sql = "";
			PreparedStatement stmt = null;
			Connection dbConn = null;
			ResultSet rs = null;
			DataPool dataPool=new DataPool();
			List<DataPool> list=new ArrayList<DataPool>();
			int a=0;
			int b=0;
			try {
				dbConn=this.getDbConnection();
				sql=" select b.ID,b.DataPoolName from HASYS_DM_PER_MAP_POOL a,HASYS_DM_DATAPOOL b where a.DATAPOOLID=b.ID and a.PERMISSIONID="+permissionId+"";
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					a=rs.getInt(1);
					dataPool.setId(a);
					dataPool.setText(rs.getString(2));
				}
				
				String childrenSql="select b.ID,b.DataPoolName from HASYS_DM_DATAPOOL b where b.pid=? and b.DataPoolType=3";
				stmt = dbConn.prepareStatement(childrenSql);
				stmt.setInt(1,a);
				rs = stmt.executeQuery();
				while (rs.next()) {
					DataPool dataPoolChildren=new DataPool();
					dataPoolChildren.setId(rs.getInt(1));
					dataPoolChildren.setText(rs.getString(2));
					list.add(dataPoolChildren);
				}
				dataPool.setChildren(list);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.DbCloseQuery(rs,stmt);
				DbUtil.DbCloseConnection(dbConn);
			}
			
			return dataPool;	
		}	
		
		public List<DataPool> selectShareCustomerById(String id,String text) {
			PreparedStatement stmt = null;
			Connection dbConn = null;
			ResultSet rs = null;
			List<DataPool> list=new ArrayList<DataPool>();
			try {
				dbConn=this.getDbConnection();
				String childrenSql="select b.ID,b.DataPoolName from HASYS_DM_DATAPOOL b where b.pid=? and b.DataPoolType=3";
				stmt = dbConn.prepareStatement(childrenSql);
				stmt.setInt(1,Integer.valueOf(id));
				rs = stmt.executeQuery();
				while (rs.next()) {
					DataPool dataPoolChildren=new DataPool();
					dataPoolChildren.setId(rs.getInt(1));
					dataPoolChildren.setText(rs.getString(2));
					list.add(dataPoolChildren);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.DbCloseQuery(rs, stmt);
				DbUtil.DbCloseConnection(dbConn);
			}
			
			return list;	
		}
		
		
		public void getUserPoolTree(int permissionId, List<TreePool> treePool) {
			PreparedStatement stmt = null;
			Connection dbConn = null;
			ResultSet rs = null;
			try {
				dbConn=this.getDbConnection();
				String sql="select id,DATAPOOLNAME,pid from HASYS_DM_DATAPOOL";
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					TreePool tree=new TreePool();
					tree.setId(rs.getInt(1));
					tree.setDataPoolName(rs.getString(2));
					tree.setPid(rs.getInt(3));
					treePool.add(tree);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.DbCloseQuery(rs,stmt);
				DbUtil.DbCloseConnection(dbConn);
			}
			}
		
		public List<UserItem> getUserPoolTreeByPermissionID(int permissionId,
				List<TreePool> treePool) {
			try {
				List<UserItem> listDictItem=new ArrayList<UserItem>();
				for (int i = 0; i < treePool.size(); i++) {
					TreePool pool = treePool.get(i);
					if(pool.getPid()==-1){
						UserItem userItem=new UserItem();
						userItem.setId(Integer.toString(pool.getId()));
						userItem.setText(pool.getDataPoolName());
						userItem.setState("");
						userItem.setDicId(pool.getId());
						userItem.setItemText(pool.getDataPoolName());
						userItem.setItemId(pool.getPid());
						listDictItem.add(userItem);
						addChildren(userItem,treePool,permissionId);
					}
				}
				return listDictItem;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		public static boolean addChildren(UserItem userTreeBranch,
				List<TreePool> treePool,Integer permissionId) {
			    TreeSet<TreeNode> listChildrenBranchs = userTreeBranch.getChildren();
			for (int ii = 0; ii < treePool.size(); ii++) {
				TreePool TreePoolBranch = treePool.get(ii);
				if (TreePoolBranch.getPid()== userTreeBranch.getDicId()) {
					UserItem treeBranch = new UserItem();
					treeBranch.setId(Integer.toString(TreePoolBranch.getId()));
					treeBranch.setText(TreePoolBranch.getDataPoolName());
					treeBranch.setState("");
					treeBranch.setDicId(TreePoolBranch.getId());
					treeBranch.setItemText(TreePoolBranch.getDataPoolName());
					treeBranch.setItemId(TreePoolBranch.getPid());
					listChildrenBranchs.add(treeBranch);
					addChildren(treeBranch,treePool,permissionId);
				}
			}
			return true;
		}	
		
		
		
		
		
}
