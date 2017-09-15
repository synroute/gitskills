package hiapp.modules.dmmanager.data;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import hiapp.system.buinfo.User;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
				sql=String.format("SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTIME,A.ENDTIME FROM HASYS_DM_SID A,HASYS_DM_SIDUSERPOOL B WHERE B.DATAPOOlNAME='%s' AND B.SHAREID=A.SHAREID",user.getName());
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
					shareBatchItems.setState((ShareBatchStateEnum) rs.getObject(8));
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
						String startTime, String endTime, User user, List<ShareBatchItem> shareBatchItem) {
					String sql = "";
					PreparedStatement stmt = null;
					Connection dbConn = null;
					ResultSet rs = null;
					try {
						dbConn = this.getDbConnection();
						sql=String.format("SELECT A.ID,A.BUSINESSID,A.SHAREID,A.SHARENAME,A.CREATEUSERID,A.CREATETIME,A.DESCRIPTION,A.STATE,A.STARTIME,A.ENDTIME FROM HASYS_DM_SID A,HASYS_DM_SIDUSERPOOL B WHERE B.DATAPOOlNAME='%s' AND B.SHAREID=A.SHAREID OR A.BUSINESSID='%s' AND A.CREATETIME BEWEEN to_date('%s','yy-mm-dd hh24:mi:ss') AND to_date('%s','yy-mm-dd hh24:mi:ss')",user.getName(),businessID,startTime,endTime);
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
							shareBatchItems.setState((ShareBatchStateEnum) rs.getObject(8));
							shareBatchItems.setStartTime(rs.getDate(9));
							shareBatchItems.setEndTime(rs.getDate(10));
							shareBatchItem.add(shareBatchItems);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return shareBatchItem;
				}
		//接收一个共享批次号 设置共享批次的启动时间和结束时间  
		public ServiceResultCode setShareDataTime(String shareID, String startTime,
				String endTime, User user, String userId) {
			String sql = "";
			PreparedStatement stmt = null;
			Connection dbConn = null;
			try {
				dbConn=this.getDbConnection();
				sql=String.format("UPDATA HASYS_DM_SID SET CREATEUSERID='%s',STARTTIME=to_date('%s','yyyy/mm/dd hh24:mi'),ENDTIME=to_date('%s','yyyy/mm/dd hh24:mi') WHERE SHAREID='%s'",user.getId(),startTime,endTime,shareID);
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
		public ServiceResultCode modifyShareState(String shareID, String flag) {
			String sql = "";
			PreparedStatement stmt = null;
			Connection dbConn = null;
			try {
				dbConn=this.getDbConnection();
				sql=String.format("UPDATA HASYS_DM_SID SET STATE='%s' WHERE SHAREID='%s'",flag,shareID);
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
			String poolId = null;
			try {
				dbConn=this.getDbConnection();
				sql=String.format("SELECT ID FROM HASYS_DM_DATAPOOL WHERE BUSINESSID='%s' AND DATAPOOLNAME='%s'",businessID,s);
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
			    poolId = rs.getString(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return poolId;
		}
        // 将 获得共享数据的用户  填入 座席池所属共享批次信息表
		public void addShareCustomerfByUserIds(String poolId, String shareID,
				String businessID, String s) {
			String insertsql = "";
			PreparedStatement stmt = null;
			Connection dbConn = null;
			try {
				dbConn=this.getDbConnection();
				insertsql=String.format("INTSERT INTO HASYS_DM_SIDUSERPOOl VALUES(SEQ_HASYS_DM_SIDUSERPOOL.NEXTVAL,'%s','%s','%s','%s')",businessID,shareID,s,poolId);
				stmt = dbConn.prepareStatement(insertsql);
				stmt.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		
}
