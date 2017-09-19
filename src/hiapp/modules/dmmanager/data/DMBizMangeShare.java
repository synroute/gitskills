package hiapp.modules.dmmanager.data;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dmmanager.DataPool;
import hiapp.system.buinfo.User;
import hiapp.system.dictionary.Dict;
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
				// 查询 共享批次信息表 里面所有共享批次   来自共享批次信息表 a，座席池所属共享批次信息表 b,条件 B.数据池名称=user.getname and a.共享批次id=b.共享批次id
				//SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTIME,A.ENDTIME FROM HASYS_DM_SID A,HASYS_DM_SIDUSERPOOL B WHERE B.DATAPOOlNAME='%s' AND B.SHAREID=A.SHAREID;
				                 //SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME FROM HASYS_DM_SID A,HASYS_DM_SIDUSERPOOL B WHERE B.DATAPOOlNAME='管' AND B.SHAREID=A.SHAREID
				//sql=String.format("SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME FROM HASYS_DM_SID A,HASYS_DM_SIDUSERPOOL B WHERE B.DATAPOOlNAME='%s' AND B.SHAREID=A.SHAREID",user.getName());
				sql=String.format("SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME FROM HASYS_DM_SID A");
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
				public List<ShareBatchItem> getUserShareBatchByTime(String businessID,
						String startTime, String endTime,List<ShareBatchItem> shareBatchItem) {
					String sql = "";
					PreparedStatement stmt = null;
					Connection dbConn = null;
					ResultSet rs = null;
					try {
						dbConn = this.getDbConnection();
						sql=String.format("SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTTIME,A.ENDTIME FROM HASYS_DM_SID A WHERE AND A.CREATETIME BETWEEN to_date('%s','mm/dd/yyyy') AND to_date('%s','mm/dd/yyyy')",startTime,endTime);
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
						}//select count(SHAREID)　from HASYS_DM_B1C_DATAM3   WHERE SHAREID='DM_SID_20170917_601' 
					} catch (Exception e) {
						e.printStackTrace();
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
			for (int i = 0; i < shareID.length; i++) {
			sb.append(shareID[i]+",");
			}
			sb.toString();
			sb.deleteCharAt(sb.length()-1);
			try {
				dbConn=this.getDbConnection();
				sql=String.format("UPDATE HASYS_DM_SID SET STATE=%s WHERE SHAREID IN ('"+sb+"')",flag);
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
				sql=String.format("SELECT DATAPOOLNAME FROM HASYS_DM_DATAPOOL WHERE BUSINESSID=%s AND ID='%s'",bizID,s);
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
				DataPoolName = rs.getString(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
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
					//list.add(dataPool);
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
			}
			
			return dataPool;	
		}	
		
}
