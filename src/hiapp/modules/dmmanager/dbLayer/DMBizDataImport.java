package hiapp.modules.dmmanager.dbLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dm.setting.dbLayer.DMBizWorkSheets;
import hiapp.utils.base.DatabaseType;
import hiapp.modules.dmsetting.DMBizTemplateImport;
import hiapp.modules.dmsetting.ImportMapColumn;
import hiapp.utils.ConstantResultType;
import hiapp.utils.UtilServlet;

public class DMBizDataImport {
	public static boolean ImportIdCreate(Connection dbConn,int bizId,StringBuffer importId,StringBuffer importIndex) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			int nImportIdLast=0;
			String szSql =String.format("SELECT ImportIdLast ,ImportDateLast FROM HASYS_DM_SEQIDIMPORT	where "
										+ "trunc(ImportDateLast) = trunc(sysdate) and BusinessId=%d ",bizId);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if(rs.next()){
				nImportIdLast=rs.getInt(1);
			}
			rs.close();
	        Calendar now = Calendar.getInstance();  
			String szImportId=String.format("%04d%02d%02dP_%04d", now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH),nImportIdLast+1);
			String szImportIndex=String.format("%d", nImportIdLast+1);
			importId.append(szImportId);
			importIndex.append(szImportIndex);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			//utilServlet.DbCloseQuery(rs, stmt);
		}
		return true;
		
	}
	public static boolean save2Db(Connection dbConn,int  bizId,String importId,int importIndex,int templateId,String tableNameExcel,String userid) throws SQLException{
		String userId=userid;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int workSheetId=DMBizWorkSheets.getWorkSheetId(dbConn, bizId, "客户导入工作表");
		String szTableNameDb=hiapp.system.worksheet.dblayer.WorkSheetManager.getWorkSheetName(dbConn, workSheetId);
		
		List<hiapp.system.worksheet.dblayer.WorkSheetColumn> listColumns =new ArrayList<hiapp.system.worksheet.dblayer.WorkSheetColumn>();
		hiapp.system.worksheet.dblayer.WorkSheet.getColumns(dbConn, workSheetId, listColumns);
		
		List<ImportMapColumn> lsImportMapColumns=new ArrayList<ImportMapColumn>();
		DMBizTemplateImport.getMapColumns(dbConn,DatabaseType.ORACLE, bizId, templateId, lsImportMapColumns);
		
		List<String> listSqlInsert=new ArrayList<String>();
		
		String iidsql="select ImportIdLast from HASYS_DM_SEQIDIMPORT where BusinessId="+bizId+"";
		stmt = dbConn.prepareStatement(iidsql);
		rs = stmt.executeQuery();
		int lastIid=0;
		
		while(rs.next())
		{
			lastIid=rs.getInt("ImportIdLast");
			
		}
		String yearMonth =new SimpleDateFormat("yyyyMMdd").format(new Date());
		String IID="";
		String insertsql="";
		if (lastIid==0) {
			IID=yearMonth+"P10001";
			insertsql="insert into HASYS_DM_SEQIDIMPORT values(HASYS_DM_SEQIDIMPORT_ID.nextval,"+bizId+",10001,TO_DATE('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ "','yyyy-mm-dd hh24:mi:ss'))";
		}else{
			lastIid=lastIid+1;
			IID=yearMonth+"P"+lastIid;
			insertsql="insert into HASYS_DM_SEQIDIMPORT values(HASYS_DM_SEQIDIMPORT_ID.nextval,"+bizId+","+lastIid+",TO_DATE('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ "','yyyy-mm-dd hh24:mi:ss'))";
		}
		stmt = dbConn.prepareStatement(insertsql);
		stmt.executeUpdate();
		
		try {
			String szSqlInsertFields="(IID,";
			String szSql =String.format("SELECT * from %s ",tableNameExcel);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			int nCols=rs.getMetaData().getColumnCount();
			if(rs.next()){
				List<String> listFieldName=new ArrayList<String>();
				for(int ii=0;ii<nCols;ii++){
					String szExcelColumnName=rs.getString(ii+1);
	    			for(int jj=0;jj<lsImportMapColumns.size();jj++){
	    				ImportMapColumn importMapColumn=lsImportMapColumns.get(jj);
	    				if(importMapColumn.getNameCh().equals(szExcelColumnName)){
	    					importMapColumn.setDbFieldIndex(ii);
	    					listFieldName.add(importMapColumn.getName());
	    				}
	    			}
				}
				for(int ii=0;ii<listFieldName.size();ii++){
					szSqlInsertFields+=listFieldName.get(ii); 	
					if(ii==listFieldName.size()-1){
						szSqlInsertFields+=",MODIFYTIME,MODIFYUSERID,MODIFYID,MODIFYLAST,DESIDS)";
					}
					else{
						szSqlInsertFields+=",";
					}
					
				}
			}
			while(rs.next()){
				String szSqlInsertHeader=String.format("INSERT INTO %s",szTableNameDb);
				String szSqlInsertValues="('"+IID+"',";
				for(int ii=0;ii<nCols;ii++){
	    			for(int jj=0;jj<lsImportMapColumns.size();jj++){
	    				ImportMapColumn importMapColumn=lsImportMapColumns.get(jj);
	    				if(importMapColumn.getDbFieldIndex()==ii){
	    					String szFieldValue=String.format("'%s'", rs.getString(ii+1));
	    					szSqlInsertValues+=szFieldValue;
	    					if(ii==nCols-1){
	    						szSqlInsertValues+="";
	    					}
	    					else{
	    						szSqlInsertValues+=",";
	    					}
	    				}
	    			}

				}
				String szSqlInsert=String.format("%s %s VALUES %s"+"TO_DATE('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ "','yyyy-mm-dd hh24:mi:ss'),'"+userid+"',0,0,0)", szSqlInsertHeader,szSqlInsertFields,szSqlInsertValues);
				listSqlInsert.add(szSqlInsert);
			}
			rs.close();
			for(int ii=0;ii<listSqlInsert.size();ii++){
				szSql =listSqlInsert.get(ii);
				stmt = dbConn.prepareStatement(szSql);
				stmt.execute();
				
			}
			szSql=String.format("INSERT INTO HASYS_DM_IMPORTINFO (ID,IID,BusinessID,ImportTime,UserID,ImportSource,ImportDescription)	VALUES	(HASYS_DM_IMPORTINFO_ID.nextval,'%s',%d,sysdate,'%s','','')",
					IID,bizId,userId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();

			szSql =String.format("delete from HASYS_DM_SEQIDIMPORT where BusinessId=%d", bizId);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();

			szSql =String.format("INSERT INTO HASYS_DM_SEQIDIMPORT (id,BusinessID,ImportIdLast,ImportDateLast) VALUES (HASYS_DM_SEQIDIMPORT_ID.nextval,%d,%d,sysdate) ", bizId,importIndex+1);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();

			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			
		}
		return true;
	}

}
