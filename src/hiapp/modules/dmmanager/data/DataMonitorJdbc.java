package hiapp.modules.dmmanager.data;

import hiapp.modules.dmmanager.bean.MonitorData;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class DataMonitorJdbc extends BaseRepository{
	
	@SuppressWarnings("resource")
	public Map<String,Object> getMonitorData(Integer bizId,String startTime,String endTime,String importId,Integer num,Integer pageSize){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		List<MonitorData> dataList=new ArrayList<MonitorData>();
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String getDataSql="select IID, total, sl1,sl2, sl3 from( ";
			String getDataSql1="select a.IID, c.total, t.sl1, t.sl2, t.sl3,rownum rn from HASYS_DM_IID a,"+
					   "(select b.IId,count(1) total from "+poolName+" b group by b.iid) c,"+
					   "(select b.iid,sum(case when e.datapooltype = 1 then 1 else 0 end) sl1,sum(case when e.datapooltype = 2 then 1 else 0 end) sl2,"+
					   "sum(case when e.datapooltype = 3 then 1 else 0 end) sl3 from HASYS_DM_DATAPOOL e, HAU_DM_B"+bizId+"C_POOL b "+
					   "where e.id = b.datapoolidcur and e.businessid=? group by b.iid) t where a.iid = c.iid and t.iid = a.iid and a.businessid=? and a.importtime>to_date(?,'yyyy-mm-dd') and a.importtime<to_date(?,'yyyy-mm-dd')";
			if(importId==null&&!"".equals(importId)){
				getDataSql1+=" and a.iid='"+importId+"'";
			}
			getDataSql=getDataSql+getDataSql1+" and rownum<?) where rn>=?";
			pst=conn.prepareStatement(getDataSql);
			pst.setInt(1,bizId);
			pst.setInt(2, bizId);
			pst.setString(3, startTime);
			pst.setString(4,endTime);
			pst.setInt(5,endNum);
			pst.setInt(6,startNum);
			rs=pst.executeQuery();
			while(rs.next()){
				MonitorData monitorData=new MonitorData();
				monitorData.setImportId(rs.getString(1));
				monitorData.setTotalNum(rs.getInt(2));
				monitorData.setSourceNum(rs.getInt(3));
				monitorData.setMidNum(rs.getInt(4));
				monitorData.setZxNum(rs.getInt(5));
				dataList.add(monitorData);
			}
			
			String getCountSql="select count(1) from ("+getDataSql1+") t";
			pst=conn.prepareStatement(getCountSql);
			pst.setInt(1,bizId);
			pst.setInt(2, bizId);
			pst.setString(3, startTime);
			pst.setString(4,endTime);
			rs=pst.executeQuery();
			Integer total=0;
			while(rs.next()){
				total=rs.getInt(1);
			}
			resultMap.put("total", total);
			resultMap.put("rows",dataList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	public List<Map<String,Object>> getExportData(Integer bizId,String startTime,String endTime,String importId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		try {
			conn=this.getDbConnection();
			String getDataSql="select a.IID, c.total, t.sl1, t.sl2, t.sl3 from HASYS_DM_IID a,"+
					   "(select b.IId,count(1) total from "+poolName+" b group by b.iid) c,"+
					   "(select b.iid,sum(case when e.datapooltype = 1 then 1 else 0 end) sl1,sum(case when e.datapooltype = 2 then 1 else 0 end) sl2,"+
					   "sum(case when e.datapooltype = 3 then 1 else 0 end) sl3 from HASYS_DM_DATAPOOL e, HAU_DM_B"+bizId+"C_POOL b "+
					   "where e.id = b.datapoolidcur and e.businessid=? group by b.iid) t where a.iid = c.iid and t.iid = a.iid and a.businessid=? and a.importtime>to_date(?,'yyyy-mm-dd') and a.importtime<to_date(?,'yyyy-mm-dd')";
			if(importId==null&&!"".equals(importId)){
				getDataSql+=" and a.iid='"+importId+"'";
			}
			pst=conn.prepareStatement(getDataSql);
			pst.setInt(1,bizId);
			pst.setInt(2, bizId);
			pst.setString(3, startTime);
			pst.setString(4,endTime);
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("importId", rs.getString(1));
				map.put("totalNum",rs.getInt(2));
				map.put("sourceNum",rs.getInt(3));
				map.put("midNum",rs.getInt(4));
				map.put("zxNum",rs.getInt(5));
				dataList.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return dataList;
	}
}
