package hiapp.modules.dmmanager.data;

import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import hiapp.modules.dmmanager.AreaCurEnum;
import hiapp.modules.dmmanager.AreaLastEnum;
import hiapp.modules.dmmanager.ImportBatchMassage;
import hiapp.modules.dmmanager.IsRecoverEnum;
import hiapp.modules.dmmanager.OperationNameEnum;
import hiapp.system.buinfo.User;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*import net.sf.json.JSONArray;
import net.sf.json.JSONObject;*/
import org.springframework.stereotype.Repository;
//数据共享db
@Repository
public class DMBizDataShare extends BaseRepository {

	/*// //根据时间筛选导入批次号查询出没有被共享的客户批次数据
	public List<ImportBatchMassage> getNotShareDataByTime(String startTime,
			String endTime, String businessId,
			List<ImportBatchMassage> listDictClassAll) {
		String szSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection dbConn = null;
		try {
			dbConn = this.getDbConnection();
			 查询所有 （导入批次信息表 A） 里面的数据   
			          来自           导入批次信息表 A，数据池记录表 B  
			          条件    A.导入批次id=B.导入批次id AND B.当前所在数据池分区=DA AND A.业务id= 业务id  OR A.导入时间   BEWEEN (开始时间,结束时间 )
			       
			szSql = "select a.* from HASYS-DM-IID a,HAU_DM_B1C_POOL b "
					+ "where a.IID=b.IID AND b.AREACUR='DA'"
					+ "AND a.BUSINESSID=" + businessId + ""
					+ "OR a.IMPORTTIME BETWEEN to_date(" + startTime
					+ ",'yyyy/mm/dd') AND to_date(" + endTime
					+ ",'yyyy/mm/dd')";
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				ImportBatchMassage importDataMessage = new ImportBatchMassage();
				importDataMessage.setId(rs.getInt(1));
				importDataMessage.setiId(rs.getString(2));
				importDataMessage.setBusinessId(rs.getInt(3));
				importDataMessage.setImportTime(rs.getDate(4));
				importDataMessage.setUserId(rs.getString(5));
				importDataMessage.setName(rs.getString(6));
				importDataMessage.setDescription(rs.getString(7));
				importDataMessage.setImportType(rs.getString(8));
				listDictClassAll.add(importDataMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return listDictClassAll;
	}*/
	//根据时间筛选导入批次号查询出没有被共享的客户批次数据
	public List<Map<String, Object>> getNotShareDataByTimes(
			String StartTime,String EndTime,String businessId, String templateId) {
		String getXmlSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection dbConn = null;
		String jsonData=null;
		String ImportTableName=null;
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
		try {
			dbConn = this.getDbConnection();
			getXmlSql=String.format("SELECT XML FROM HASYS_DM_BIZTEMPLATEIMPORT WHERE TEMPLATEID='%s' AND BUSINESSID='%s'",templateId,businessId);
			stmt=dbConn.prepareStatement(getXmlSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				//循环获取xml里面的数据
				jsonData=ClobToString(rs.getClob(1));	
			}
			JSONObject jsonObject=JSONObject.fromObject(jsonData);
			//解析出是哪个Excel文件
			JSONObject excelTemplate=jsonObject.getJSONObject("ImportExcelTemplate");
			//获取表名
			ImportTableName=excelTemplate.getString("ImportTableName");
			//表里面有多少字段
			JSONArray dataArray=jsonObject.getJSONArray("FieldMaps");
			//循环便利传过来的批次号，根据批次号的多少 循环多少次
			String sql="select";
			for (int i = 0; i < dataArray.size(); i++) {
				sql+=dataArray.getJSONObject(i).getString("FieldName")+",";
			}
			sql.substring(sql.length()-1);
			sql=sql+"from "+ImportTableName+" where IID IN (select a.IID from HASYS-DM-IID a,HAU_DM_B1C_POOL b where a.IID=b.IID AND b.AREACUR='DA' AND a.BUSINESSID=" + businessId + ""+ "OR a.IMPORTTIME BETWEEN to_date(" + StartTime+ ",'yyyy/mm/dd') AND to_date(" + EndTime+ ",'yyyy/mm/dd'))";
			stmt=dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				//列名不确定 需要循环 录入到 map集合中
				for (int i = 0; i < dataArray.size(); i++) {
					// 将循环出来的列名作为key
					String key=dataArray.getJSONObject(i).getString("FieldName");
					map.put(key,rs.getObject(i+1));
				}
				dataList.add(map);
			}
			/*for (ImportBatchMassage importBatchMassage : importDataMessage) {
				//查询批次号对应表的数据 
				String Sql=String.format("SELECT * FROM '%s' WHERE IID='%s'",ImportTableName,importBatchMassage.getiId());
				stmt=dbConn.prepareStatement(Sql);
				rs = stmt.executeQuery();
				//查询出来的表数据 
				while(rs.next()){
					Map<String,Object> map=new HashMap<String, Object>();
					//列名不确定 需要循环 录入到 map集合中
					for (int i = 0; i < dataArray.size(); i++) {
						// 将循环出来的列名作为key
						String key=dataArray.getJSONObject(i).getString("FieldName");
						map.put(key,rs.getObject(i+1));
					}
					dataList.add(map);
					
				}	
				}*/	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,stmt);
			DbUtil.DbCloseConnection(dbConn);
		}
		return dataList;
		
	}
			/*//解析导入模板设置表
			*/
		
	 // CLOB转换成String
    public String ClobToString(Clob sc) throws SQLException, IOException {  
        String reString = "";  
        Reader is = sc.getCharacterStream();
        BufferedReader br = new BufferedReader(is);  
        String s = br.readLine();  
        StringBuffer sb = new StringBuffer();  
        while (s != null) {
            sb.append(s);  
            s = br.readLine();  
        }  
        reString = sb.toString();  
        return reString;  
    }  
	
	

	// 2.1将共享的数据填入单号码重拨一份
	@SuppressWarnings("all")
	public String confirmShareData(String iId,String businessId,
			User user, String newId) {
		String insertsql = "";
		PreparedStatement stmt = null;
		Connection dbConn = null;
		try {
			dbConn = this.getDbConnection();
			insertsql =String.format("INTSERT INTO HASYS_DM_B1C_DATAM3 VALUES(HASYS_DM_B1C_DATAM3_ID.NEXTVAL,'%s','%s','%s','%s','%s')",businessId,newId,iId,user.getId(),SingleNumberModeShareCustomerStateEnum.CREATED) ;
			stmt = dbConn.prepareStatement(insertsql);
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newId;
	}

	// 2.2将共享的数据填入单号码重拨共享历史表一份
	public void confirmShareDataOne(String iId, String bizid, User user, String newId) {
		String insertsql = "";
		PreparedStatement stmt = null;
		Connection dbConn = null;
		try {
			dbConn = this.getDbConnection();
			insertsql =String.format("INTSERT INTO HASYS_DM_B1C_DATAM3_HIS VALUES(SEQ_HASYS_DM_B1C_DATAM3.NEXTVAL,'%s','%s','%s','%s','%s')",bizid,newId,iId,user.getId(),SingleNumberModeShareCustomerStateEnum.CREATED);
			stmt = dbConn.prepareStatement(insertsql);
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 查询当前的业务的数据池
	public String confirmShareDataTwo(String businessId) {
		String sql = "";
		PreparedStatement stmt = null;
		Connection dbConn = null;
		ResultSet rs = null;
		String s = null;
		try {
			dbConn = this.getDbConnection();
			sql =String.format("SELECT DATAPOOLNAME FROM HAU_DM_DATAPOOL WHERE BUSINESSID='%s'", businessId) ;
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			s = rs.getString(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	// 更改数据池记录表数据
	public void confirmShareDataThree(String iId,
			String dataPool, User user) {
		String updatesql = "";
		PreparedStatement stmt = null;
		Connection dbConn=null;
        try {
        	dbConn=this.getDbConnection();
        	updatesql=String.format("UPDATE HAU_DM_B1C_POOL SET ID='%s',IID='%s',CID='%s',DATAPOOLIDLASt='%s',DATAPOOLIDCUR='%s',AREALAST='%s',AREACUR='$s'",iId,iId,user,dataPool,dataPool,AreaLastEnum.DA,AreaCurEnum.SA);
        	stmt = dbConn.prepareStatement(updatesql);
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//向数据池操作记录表添加数据
	public void confirmShareDataFree(String iId,
			User user, String dataPool) {
		String insertsql = "";
		PreparedStatement stmt = null;
		Connection dbConn = null;
		try {
			dbConn=this.getDbConnection();
			insertsql=String.format("INTSERT INTO HAU_DM_B1C_POOL_ORE values(SEQ_HAU_DM_B1C_POOL_ORE.NEXTVAL,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',to_date(sysdate,'yyyy/mm/dd hh24:mi'))",null,iId,user.getId(),OperationNameEnum.Sharing,dataPool,dataPool,AreaLastEnum.DA,AreaCurEnum.SA,IsRecoverEnum.NO,user.getId());
			stmt = dbConn.prepareStatement(insertsql);
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//向共享批次信息表添加数据
	public ServiceResultCode confirmShareDataFive(String businessId,
			String batherId, String shareName, String description) {
		String insertsql = "";
		PreparedStatement stmt = null;
		Connection dbConn = null;
		try {
			dbConn=this.getDbConnection();
			insertsql = String.format("INTSERT INTO HASYS_DM_SID VALUES(SEQ_HASYS_DM_SID.NEXTVAL,'%s','%s','%s','%s','%s','%s')",businessId,batherId,shareName,0,null,description);
			stmt = dbConn.prepareStatement(insertsql);
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ServiceResultCode.SUCCESS;
	}
}
