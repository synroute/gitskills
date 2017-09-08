package hiapp.modules.dmmanager.data;

import hiapp.modules.dmmanager.ImportDataMessage;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.Data;
     //数据共享db
public class DMBizDataImport extends BaseRepository{
    ////根据时间筛选导入批次号查询出没有被共享的客户批次数据
	public List<ImportDataMessage> getNotShareDataByTime(Date startTime, Date endTime,
			String businessId, List<ImportDataMessage> listDictClassAll) {
		 String szSql="";
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 Connection dbConn=null;
		 try {
			 dbConn=this.getDbConnection();
			 szSql="select a.* "
			 	 + "from HASYS-DM-IID a,HAU_DM_BIC_POOL b "
			 	 + "where a.IID=b.IID AND b.AREACUR='DA'"
			 	 + "AND a.BUSINESSID="+businessId+""
			 	 + "OR a.IMPORTTIME AND"
			 	 + "BETEEN to_data("+startTime+",'yyyy-MM-dd') AND to_data("+endTime+",'yyyy-MM-dd')";
			 stmt = dbConn.prepareStatement(szSql);
			 rs = stmt.executeQuery();
				while(rs.next()){
				ImportDataMessage importDataMessage=new ImportDataMessage();
				importDataMessage.setId(rs.getInt(1));
				importDataMessage.setIid(rs.getString(2));
				importDataMessage.setBusinessId(rs.getInt(3));
				importDataMessage.setImportTime((Data) rs.getDate(4));
				importDataMessage.setUserId(rs.getString(5));
				importDataMessage.setName(rs.getString(6));
				importDataMessage.setDescription(rs.getString(7));
				importDataMessage.setImportType(rs.getString(8));
				listDictClassAll.add(importDataMessage);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return listDictClassAll;
	}

	
}
