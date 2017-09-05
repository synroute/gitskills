package hiapp.modules.dmmanager.dbLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysql.cj.api.mysqla.result.Resultset;

import dm.setting.dbLayer.DMBizWorkSheets;
import hiapp.WorkSheetManager.WorkSheet;
import hiapp.WorkSheetManager.WorkSheetColumn;
import hiapp.WorkSheetManager.WorkSheetManager;
import hiapp.utils.UtilServlet;

public class DMBizTaskCreate {
	public static boolean queryImportData(UtilServlet utilServlet,int bizId,String szdateStart,String szdateEnd,List<WorkSheetColumn> listColumn,ResultSet rs) throws Exception{

		PreparedStatement stmt = null;
		try {
			
			int workSheetId=DMBizWorkSheets.getWorkSheetId(utilServlet,bizId, "客户导入工作表");
			String szTableNameImport=WorkSheetManager.getWorkSheetName(utilServlet,workSheetId);
			WorkSheet.getColumns(utilServlet, workSheetId, listColumn);
			
			
			String szTimeStart=String.format("%s 00:00:00",szdateStart);
			String szTimeEnd=String.format("%s 23:59:59",szdateEnd);
			String szSql;
			String szSqlPart1="Select ";
			String szSqlPart2="";
			for(int nCol=0;nCol<listColumn.size();nCol++){
				WorkSheetColumn workSheetColumn=listColumn.get(nCol);
				String szItem=String.format("%s.%s",szTableNameImport,workSheetColumn.getName());
				szSqlPart2+=szItem;
				if(nCol!=listColumn.size()-1){
					szSqlPart2+=",";
				}
			}
			String szSqlPart3=String.format("FROM HASYS_DM_IMPORTINFO INNER JOIN %s ON HASYS_DM_IMPORTINFO.IID = %s.IID AND HASYS_DM_IMPORTINFO.BusinessID=%d", 
												szTableNameImport,								
												szTableNameImport,bizId);

			szSql=String.format("%s\r\n%s\r\n%s\r\n where HASYS_DM_IMPORTINFO.ImportTime >=to_date('%s','yyyy-MM-dd HH24:MI:SS') and HASYS_DM_IMPORTINFO.ImportTime<=to_date('%s','yyyy-MM-dd HH24:MI:SS')  ORDER BY %s.ID",
									szSqlPart1,szSqlPart2,szSqlPart3,szTimeStart,szTimeEnd,szTableNameImport);
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			utilServlet.DbCloseExecute(stmt);
			return false;
		} finally {
		}
		return true;
	}
	public static boolean taskIdCreate(UtilServlet utilServlet,int bizId,StringBuffer taskId,StringBuffer taskIndex){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			int nTaskIdLast=0;
			String szSql =String.format("SELECT TaskIdLast ,TaskDateLast FROM HASYS_DM_SEQIDTASK	where "
										+ "trunc(TaskDateLast) = trunc(sysdate) and BusinessId=%d ",bizId);
			
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if(rs.next()){
				nTaskIdLast=rs.getInt(1);
			}
			rs.close();
	        Calendar now = Calendar.getInstance();  
			String szTaskId=String.format("%04d%02d%02dT_%04d", now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH),nTaskIdLast+1);
			taskId.append(szTaskId);
			String szTaskIndex=String.format("%d", nTaskIdLast+1);
			taskIndex.append(szTaskIndex);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			utilServlet.DbCloseQuery(rs, stmt);
		}
		return true;
	}
	public static boolean save2Db(UtilServlet utilServlet,int bizId,String taskId,String taskName,String taskDescription,int taskIndex, JsonArray jaTaskData){
		if(!m_save2DbInsertCustInTask(utilServlet, bizId, taskId, jaTaskData)){
			return false;
		}
		if(!m_save2DbInsertTaskInfo(utilServlet, bizId, taskId, taskName, taskDescription)){
			return false;
		}
		if(!m_save2DbUpdateTaskId(utilServlet, bizId, taskIndex)){
			return false;
		}
		return true;
	}
	public static boolean m_save2DbInsertCustInTask(UtilServlet utilServlet,int bizId,String taskId,JsonArray jaTaskData){
		String userId=utilServlet.getUserId();
		PreparedStatement stmt = null;
		int workSheetId=DMBizWorkSheets.getWorkSheetId(utilServlet,bizId, "客户任务工作表");
		String szTableNameTask=WorkSheetManager.getWorkSheetName(utilServlet, workSheetId);
		String szSeqNameTask=WorkSheetManager.getWorkSheetSeqName(utilServlet, workSheetId);
		List<WorkSheetColumn> lsWorkSheetColumns=new ArrayList<WorkSheetColumn>();
		WorkSheet.getColumns(utilServlet, workSheetId, lsWorkSheetColumns);
			
		String szSqlHeader = String.format("INSERT INTO %s ",szTableNameTask);
		for(int ii=0;ii<jaTaskData.size();ii++){
			String szSql="";
			String szFields="(";
			String szValues="VALUES(";
			JsonObject jsRow=jaTaskData.get(ii).getAsJsonObject();
			for(int jj=0;jj<lsWorkSheetColumns.size();jj++){
				WorkSheetColumn workSheetColumn=lsWorkSheetColumns.get(jj);
				String field=workSheetColumn.getName();
				if(field.equals("ID")){
					szFields+=field;
					szFields+=",";
					szValues+=String.format("%s.nextval",szSeqNameTask);
					szValues+=",";
				}
				else if(field.equals("TASKID")){
					szFields+=field;
					szFields+=",";
					szValues+="'"+taskId+"'";
					szValues+=",";
				}
				else if(field.equals("MODIFYID")){
					szFields+=field;
					szFields+=",";
					szValues+="0";
					szValues+=",";
				}
				else if(field.equals("MODIFYUSERID")){
					szFields+=field;
					szFields+=",";
					szValues+="'"+userId+"'";
					szValues+=",";
				}
				else if(field.equals("MODIFYTIME")){
					szFields+=field;
					szFields+=",";
					szValues+="sysdate";
					szValues+=",";
				}
				else{
					szFields+=field;
					if(jj==lsWorkSheetColumns.size()-1){
						szFields+=")";
					}
					else{
						szFields+=",";
					}
					JsonElement jElement=jsRow.get(field);
					String value="";
					if(jElement!=null && !jElement.isJsonNull()){
						value=jElement.getAsString();
					}
					szValues+="'"+value+"'";
					if(jj==lsWorkSheetColumns.size()-1){
						szValues+=")";
					}
					else{
						szValues+=",";
					}
				}
			}//for(int jj=0;jj<lsWorkSheetColumns.size();jj++){
			szSql=String.format("%s %s %s", szSqlHeader,szFields,szValues);
			try {
				stmt = utilServlet.dbConn.prepareStatement(szSql);
				stmt.execute();
			} catch (Exception e) {
				utilServlet.setResultCode(2);
				utilServlet.setResultMessage("插入记录错误！");
				return false;
			} finally {
				utilServlet.DbCloseExecute(stmt);
			}
				
		}//for(int ii=0;ii<jaTaskData.size();ii++){
		
		return true;
	}
	public static boolean m_save2DbInsertTaskInfo(UtilServlet utilServlet,int bizId,String taskId,String taskName,String taskDescription){
		String userId=utilServlet.getUserId();
		String szSql=String.format("INSERT INTO HASYS_DM_TASK "
									+"(TID,BusinessId,TaskTypeId,Name,CreateUserID,CreateTime,Description,State,StartTime,EndTime)"
									+"VALUES('%s',%d,%d,'%s','%s',sysdate,'%s','',to_date('2000-01-01 00:00:00','yyyy-mm-dd HH24:MI:SS'),to_date('2000-01-01 00:00:00','yyyy-mm-dd HH24:MI:SS') )",
									taskId,bizId,0,taskName,userId,taskDescription);
		PreparedStatement stmt = null;
		try {
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (Exception e) {
			utilServlet.setResultCode(2);
			utilServlet.setResultMessage("插入记录错误！");
			return false;
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}
		return true;
	}
	public static boolean m_save2DbUpdateTaskId(UtilServlet utilServlet,int bizId,int taskIndex){
		String userId=utilServlet.getUserId();
		String szSql=String.format("UPDATE HASYS_DM_SEQIDTASK SET TaskIdLast = %d ,TaskDateLast = sysdate	WHERE	BusinessId=%s",
									taskIndex,bizId);
		PreparedStatement stmt = null;
		try {
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (Exception e) {
			utilServlet.setResultCode(3);
			utilServlet.setResultMessage("更新序列错误！");
			return false;
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}
		return true;
	}

}
