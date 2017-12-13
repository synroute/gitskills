package hiapp.modules.dmmanager.data;

import hiapp.modules.dmmanager.bean.MonitorData;
import hiapp.modules.dmmanager.bean.OutputFirstRow;
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
			if(importId!=null&&!"".equals(importId)){
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
			DbUtil.DbCloseQuery(rs, pst);
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
			if(importId!=null&&!"".equals(importId)){
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
	
	
	public List<OutputFirstRow> getAllSheetColumn(String importWorkSheetId,String resultWorkSheetId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<OutputFirstRow> list=new ArrayList<OutputFirstRow>();
		try {
			conn=this.getDbConnection();
			String sql="select ColumnName,ColumnNameCh,DataType,WorkSheetId from HASYS_WORKSHEETCOLUMN WHERE WorkSheetId in(?,?)";
			pst=conn.prepareStatement(sql);
			pst.setString(1,importWorkSheetId );
			pst.setString(2,resultWorkSheetId);
			rs=pst.executeQuery();
			while(rs.next()){
				OutputFirstRow firstRow=new OutputFirstRow();
				firstRow.setField(rs.getString(1));
				firstRow.setTitle(rs.getString(2));
				firstRow.setDataType(rs.getString(3));
				firstRow.setWorkSheetId(rs.getString(4));
				list.add(firstRow);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return list;
	}
	
	public List<Map<String,Object>> getAllDataByTime(String importWorkSheetId,String resultWorkSheetId,String startTime,String endTime,List<OutputFirstRow> titleList,Integer ifDial,Integer bizId,Integer num,Integer pageSize){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String importTableName="HAU_DM_B"+bizId+"C_IMPORT";
		String resultTableName="HAU_DM_B"+bizId+"C_RESULT";
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		try {
			conn=this.getDbConnection();
			String sql="select ";
			String sql1="select ";
			for (int i = 0; i < titleList.size(); i++) {
				String columName=titleList.get(i).getField();
				String dataType=titleList.get(i).getDataType();
				String workSheetId=titleList.get(i).getWorkSheetId();
				String asName=null;
				if(workSheetId.equals(importWorkSheetId)){
					asName="a.";
					sql1+=getDataType(dataType,columName,asName);
					sql+=asName+columName+",";
				}
				if(workSheetId.equals(resultWorkSheetId)){
					asName="b.";
					sql1+=getDataType(dataType,columName,asName);
					sql+=asName+columName+",";
				}
			}
			sql1=sql1+"rownum rn from "+importTableName+" a left join "+resultTableName+" b on a.IID=b.IID where a.MODIFYLAST=1 and b.MODIFYLAST=1 and "
					+"exists(select * from hasys_dm_iid c where a.CID=c.CID and a.IID=c.CID and c.IMPORTTIME>to_date(?,'yyyy-mm-dd hh24:mi:ss') and c.IMPORTTIME<to_date(?,'yyyy-mm-dd hh24:mi:ss')) and rownum<?";
			if(ifDial==0){
				sql1+=" and exists(select * from "+resultTableName+" d where a.iid=d.iid and a.cid=d.cid)";
			}else if(ifDial==1){
				sql1+=" and not exists(select * from "+resultTableName+" d where a.iid=d.iid and a.cid=d.cid)";
			}
			sql=sql.substring(0,sql.length()-1)+"("+sql1+") t where rn>=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,startTime);
			pst.setString(2,endTime);
			pst.setInt(3, endNum);
			pst.setInt(4,startNum);
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				for (int i = 0; i < titleList.size(); i++) {
					String columName=titleList.get(i).getField();
					map.put(columName, rs.getObject(columName));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return list;
	}
		
	public String getDataType(String dataType,String columName,String asName){
		String column=null;
		if("datetime".equals(dataType.toLowerCase())){
			column="to_char("+asName+columName+",'yyyy-mm-dd hh24:mi:ss') "+columName+",";
		}else{
			column=asName+columName+",";
		}
		return column;
	}
}
