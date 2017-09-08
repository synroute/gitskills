package hiapp.modules.dmsetting.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;












import hiapp.modules.dmsetting.beanOld.DMBusiness;
import hiapp.modules.dmsetting.beanOld.DMModeEnum;
import hiapp.modules.dmsetting.beanOld.DMSubModeEnum;
import hiapp.modules.dmsetting.beanOld.DMWorkSheet;
import hiapp.modules.dmsetting.beanOld.DMWorkSheetType;
import hiapp.system.worksheet.bean.CreationInfoWorkSheet;
import hiapp.system.worksheet.bean.WorkSheet;
import hiapp.system.worksheet.bean.WorkSheetDataType;
import hiapp.system.worksheet.data.WorkSheetRepository;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.database.DatabaseType;
import hiapp.utils.serviceresult.ServiceResultCode;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月8日 上午10:02:56 
 * 类说明 
 */
public class DmWorkSheetRepository extends BaseRepository {
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
	@Autowired
	private WorkSheetRepository workSheetRepository;
	
	public ServiceResultCode newDMBizWorkSheetsSystem(DMBusiness dmBusiness,StringBuffer errMessage) {
		Connection dbConn = null;
		try {
			dbConn = this.getDbConnection();
			ServiceResultCode sResultCode=null;
			DMModeEnum dmMode=DMModeEnum.get(dmBusiness.getModeId());
			DMSubModeEnum dmSubMode=DMSubModeEnum.get(dmBusiness.getSubModeId());
			if(dmMode==DMModeEnum.MODE1) {
				if(dmSubMode==DMSubModeEnum.SUBMODE1){
					sResultCode=m_newWorkSheetCustImport(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
					sResultCode=m_newWorkSheetCustTask(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
					sResultCode=m_newWorkSheetPresetTime(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
					sResultCode=m_newWorkSheetPresetTime_His(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
					sResultCode=m_newWorkSheetTaskDist(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
					sResultCode=m_newWorkSheetTaskDist_His(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				}
			}
			else if(dmMode==DMModeEnum.MODE2) {
				if(dmSubMode==DMSubModeEnum.SUBMODE2){
					
				}
				else if(dmSubMode==DMSubModeEnum.SUBMODE3){
					
				}
				else if(dmSubMode==DMSubModeEnum.SUBMODE4){
					
				}
			}
			else if(dmMode==DMModeEnum.MODE3) {
				if(dmSubMode==DMSubModeEnum.SUBMODE5){
					
				}
				else if(dmSubMode==DMSubModeEnum.SUBMODE6){
					
				}
				else if(dmSubMode==DMSubModeEnum.SUBMODE7){
					
				}
				else if(dmSubMode==DMSubModeEnum.SUBMODE8){
					
				}
				else if(dmSubMode==DMSubModeEnum.SUBMODE9){
					
				}
			}	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
		}
		return ServiceResultCode.SUCCESS;
	}
	private ServiceResultCode m_newWorkSheetCustImport(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAUDM_B%d_CUSTIMPORT", dmBusiness.getId());
		String szWorkSheetNameCh=String.format("外拨业务%d客户导入工作表", dmBusiness.getId());
		String szWorkSheetDescription=String.format("业务%d客户导入工作表，客户从Excel或外部数据导入到系统中，首先存入此工作表",dmBusiness.getId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		return m_newWs(dbConn,dmBusiness.getId(),creationInfoWorkSheet,DMWorkSheetType.WSTDM_CUSTOMERIMPORT,errMessage);
	}
	private ServiceResultCode m_newWorkSheetCustTask(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAUDM_B%d_CUSTTASK", dmBusiness.getId());
		String szWorkSheetNameCh=String.format("外拨业务%d客户任务工作表", dmBusiness.getId());
		String szWorkSheetDescription=String.format("外拨业务%d客户任务工作表，创建后，客户数据存入此工作表",dmBusiness.getId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("任务ID", "TaskId", "任务唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改ID", "ModifyID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		return m_newWs(dbConn,dmBusiness.getId(),creationInfoWorkSheet,DMWorkSheetType.WSTDM_CUSTOMERTASK,errMessage);
	}
	private ServiceResultCode m_newWorkSheetTaskDist(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAUDM_B%d_TASKDIST", dmBusiness.getId());
		String szWorkSheetNameCh=String.format("外拨业务%d客户分配工作表", dmBusiness.getId());
		String szWorkSheetDescription=String.format("外拨业务%d客户分配工作表，客户分配到坐席的数据存入此工作表",dmBusiness.getId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("任务ID", "TaskId", "任务唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改ID", "ModifyID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("分配者用户ID", "distuserid", "分配者用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("分配时间", "disttime", "分配时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("被分配用户ID", "userid", "被分配用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("被分配用户名", "username", "被分配用户名", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("系统状态", "sysstate", "系统状态", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("结束码类型", "endcodetype", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("结束码", "endcode", "结束码", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("用户状态类型", "userstatetype", "用户状态类型", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("用户状态类型", "userstate", "用户状态", WorkSheetDataType.TEXT, 50, false, true);

		return m_newWs(dbConn,dmBusiness.getId(),creationInfoWorkSheet,DMWorkSheetType.WSTDM_CUSTDIST,errMessage);
	}
	private ServiceResultCode m_newWorkSheetTaskDist_His(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAUDM_B%d_TASKDIST_His", dmBusiness.getId());
		String szWorkSheetNameCh=String.format("外拨业务%d客户分配历史工作表", dmBusiness.getId());
		String szWorkSheetDescription=String.format("外拨业务%d客户分配历史工作表，客户分配到坐席的数据存入此工作表",dmBusiness.getId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("任务ID", "TaskId", "任务唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改ID", "ModifyID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("分配者用户ID", "distuserid", "分配者用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("分配时间", "disttime", "分配时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("被分配用户ID", "userid", "被分配用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("被分配用户名", "username", "被分配用户名", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("系统状态", "sysstate", "系统状态", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("结束码类型", "endcodetype", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("结束码", "endcode", "结束码", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("用户状态类型", "userstatetype", "用户状态类型", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("用户状态类型", "userstate", "用户状态", WorkSheetDataType.TEXT, 50, false, true);

		return m_newWs(dbConn,dmBusiness.getId(),creationInfoWorkSheet,DMWorkSheetType.WSTDM_CUSTDIST_HIS,errMessage);
	}
	private ServiceResultCode m_newWorkSheetPresetTime(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAUDM_B%d_PresetTime", dmBusiness.getId());
		String szWorkSheetNameCh=String.format("外拨业务%d预约工作表", dmBusiness.getId());
		String szWorkSheetDescription=String.format("外拨业务%d预约工作表，坐席预约的数据存入此工作表，只保留最后预约的数据",dmBusiness.getId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("任务ID", "TaskId", "任务唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		
		creationInfoWorkSheet.addColumn("预约日期时间", "presettime", "预约日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("状态", "state", "状态", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("预约说明", "presetcomment", "预约说明", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("号码类型", "phonetype", "号码类型", WorkSheetDataType.TEXT, 50, false, true);
		
		creationInfoWorkSheet.addColumn("修改ID", "ModifyID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		
		return m_newWs(dbConn,dmBusiness.getId(),creationInfoWorkSheet,DMWorkSheetType.WSTDM_PRESETTIME,errMessage);
	}
	private ServiceResultCode m_newWorkSheetPresetTime_His(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAUDM_B%d_PresetTime_His", dmBusiness.getId());
		String szWorkSheetNameCh=String.format("外拨业务%d预约历史工作表", dmBusiness.getId());
		String szWorkSheetDescription=String.format("外拨业务%d预约历史工作表，坐席预约的数据存入此工作表",dmBusiness.getId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("任务ID", "TaskId", "任务唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		
		creationInfoWorkSheet.addColumn("预约日期时间", "presettime", "预约日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("状态", "state", "状态", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("预约说明", "presetcomment", "预约说明", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("号码类型", "phonetype", "号码类型", WorkSheetDataType.TEXT, 50, false, true);
		
		creationInfoWorkSheet.addColumn("修改ID", "ModifyID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		
		return m_newWs(dbConn,dmBusiness.getId(),creationInfoWorkSheet,DMWorkSheetType.WSTDM_PRESETTIME_HIS,errMessage);
	}
	
	private ServiceResultCode m_newWs(Connection dbConn,int bizId,CreationInfoWorkSheet creationInfoWorkSheet,DMWorkSheetType dmWsType,StringBuffer errMessage){
		String szSql;
		PreparedStatement stmt = null;
		try {
			if(!workSheetRepository.newWorkSheet(DatabaseType.ORACLE,creationInfoWorkSheet)){
				return ServiceResultCode.EXECUTE_SQL_FAIL;
			}
		} catch (Exception e) {
		}
		try {
			szSql="INSERT INTO HASYS_DM_BIZWORKSHEET (ID,BIZID,WORKSHEETID,TYPE) VALUES (HASYS_DM_BIZWORKSHEET_ID.nextval,?,?,?) ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1, bizId);
			stmt.setInt(2, creationInfoWorkSheet.getId());
			stmt.setString(3, dmWsType.getType());
			stmt.execute();
		} catch (Exception e) {
			return ServiceResultCode.EXECUTE_SQL_FAIL;
		}
		finally {
			DbUtil.DbCloseExecute(stmt);
		}
		return ServiceResultCode.SUCCESS;
	}
	public ServiceResultCode modify(Connection dbConn,int bizId,String id,String nameCh,String name,String description,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		creationInfoWorkSheet.setName(name);
		creationInfoWorkSheet.setNameCh(nameCh);
		creationInfoWorkSheet.setDescription(description);
		workSheetRepository.modifyWorkSheet(DatabaseType.ORACLE, creationInfoWorkSheet);
		return ServiceResultCode.SUCCESS;
	}
	public ServiceResultCode destroy(int bizId,int id,StringBuffer errMessage){
		try {
			workSheetRepository.destroyWorkSheet(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceResultCode.SUCCESS;
	}
	public ServiceResultCode destroyWorkSheet(int workSheetId) throws Exception{
		Connection dbConn = null;
		String szSql;
		PreparedStatement stmt = null;
		try {
			dbConn = this.getDbConnection();
			szSql="DELETE FROM HASYS_DM_BIZWORKSHEET WHERE WORKSHEETID=? ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1, workSheetId);
			stmt.execute();
		} catch (Exception e) {
			return ServiceResultCode.EXECUTE_SQL_FAIL;
		}
		finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		workSheetRepository.destroyWorkSheet(workSheetId);
		return ServiceResultCode.SUCCESS;
	}
	
	public ServiceResultCode destroyWorkSheetAll(int bizId, StringBuffer errMessage) throws Exception{
		List<DMWorkSheet> listworksheet=new ArrayList<DMWorkSheet>();
		dmWorkSheetRepository.getWorkSheetAll(bizId,listworksheet);
        for(int ii=0;ii<listworksheet.size();ii++){
        	WorkSheet workSheet=listworksheet.get(ii);
        	destroyWorkSheet(workSheet.getId());
        }	
		return ServiceResultCode.SUCCESS;
	}
	
	public boolean getWorkSheetAll(int bizId,List<DMWorkSheet> listWorkSheet){
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn = this.getDbConnection();
			String szSql = "select w.id, w.name, w.namech, w.description, wdm.type from hasys_worksheet w "+
							"left join  hasys_dm_bizworksheet wdm on w.id= wdm.worksheetid "+
							"where wdm.bizid=? order by w.id";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1,bizId);	
			rs = stmt.executeQuery();
			while(rs.next()){
				DMWorkSheet dmWorkSheet=new DMWorkSheet();
				dmWorkSheet.setId(rs.getInt(1));
				dmWorkSheet.setName(rs.getString(2));
				dmWorkSheet.setNameCh(rs.getString(3));
				dmWorkSheet.setDescription(rs.getString(4));
				dmWorkSheet.setType(rs.getString(5));
				dmWorkSheet.setIdNameCh(String.format("%d:%s", dmWorkSheet.getId(),dmWorkSheet.getNameCh()));
				listWorkSheet.add(dmWorkSheet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return true;
	}
}
