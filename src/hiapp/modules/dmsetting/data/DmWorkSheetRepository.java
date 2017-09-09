package hiapp.modules.dmsetting.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.DMWorkSheet;
import hiapp.modules.dmsetting.DMWorkSheetTypeEnum;
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
			if (dmBusiness.getOutboundModeId()==1) {
				sResultCode=m_newWorkSheetCustImport(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetPresetTime(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
			} else if (dmBusiness.getOutboundModeId()==2) {
				
			} else if (dmBusiness.getOutboundModeId()==3) {
				sResultCode=m_newWorkSheetCustImport(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetPresetTime(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetCustResult(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPool(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPoolORE(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataM3(dbConn,dmBusiness,errMessage);			if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataM3_his(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
			} else if (dmBusiness.getOutboundModeId()==4) {
				
			} else if (dmBusiness.getOutboundModeId()==5) {
				
			} else if (dmBusiness.getOutboundModeId()==6) {
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
		}
		return ServiceResultCode.SUCCESS;
	}
	//导入表
	private ServiceResultCode m_newWorkSheetCustImport(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAU_DM_B%d_CUSTIMPORT", dmBusiness.getBizId());
		String szWorkSheetNameCh=String.format("外拨业务%d客户导入工作表", dmBusiness.getBizId());
		String szWorkSheetDescription=String.format("业务%d客户导入工作表，客户从Excel或外部数据导入到系统中，首先存入此工作表",dmBusiness.getBizId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("是否最后一次修改", "ModifyLast", "任务唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改ID", "ModifyID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_CUSTOMERIMPORT,errMessage);
	}
	//结果表
	private ServiceResultCode m_newWorkSheetCustResult(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAU_DM_B%d_CUSTTASK", dmBusiness.getBizId());
		String szWorkSheetNameCh=String.format("外拨业务%d用户拨打结果表", dmBusiness.getBizId());
		String szWorkSheetDescription=String.format("外拨业务%d结果表，创建后，拨打结果存入此工作表",dmBusiness.getBizId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("来源编号", "SourceID", "来源编号", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改ID", "ModifyID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("是否最后一次修改", "ModifyLast", "任务唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("拨打类型", "DailType", "拨打类型", WorkSheetDataType.TEXT, 10, false, true);
		creationInfoWorkSheet.addColumn("拨打时间", "DailTime", "拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("呼叫流水号", "CustomerCallId", "呼叫流水号", WorkSheetDataType.INT, -1, false, true);
		
		return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_RESULT,errMessage);
	}
	
	//预约时间表
	private ServiceResultCode m_newWorkSheetPresetTime(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HASYS_DM_B%d_PresetTime", dmBusiness.getBizId());
		String szWorkSheetNameCh=String.format("外拨业务%d预约工作表", dmBusiness.getBizId());
		String szWorkSheetDescription=String.format("外拨业务%d预约工作表，坐席预约的数据存入此工作表，只保留最后预约的数据",dmBusiness.getBizId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("来源编号", "SourceID", "来源编号", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("预约日期时间", "presettime", "预约日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("预约状态", "state", "预约状态", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("预约备注", "presetcomment", "预约备注", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改ID", "ModifyID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("修改描述", "ModifyDescription", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
		creationInfoWorkSheet.addColumn("号码类型", "phonetype", "号码类型", WorkSheetDataType.TEXT, 50, false, true);
		
		return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_PRESETTIME,errMessage);
	}
	//数据池记录表
	private ServiceResultCode m_newWorkSheetDataPool(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAU_DM_B%d_POOL", dmBusiness.getBizId());
		String szWorkSheetNameCh=String.format("外拨业务%ds数据池记录表", dmBusiness.getBizId());
		String szWorkSheetDescription=String.format("外拨业务%d数据池记录表，数据池记录数据存入此工作表",dmBusiness.getBizId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("分配批次ID", "DID", "分配批次号", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("上次所在数据池ID", "DATAPOOlIDLAST", "上次所在数据池ID", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("当前所在数据池ID", "DATAPOOlIDCUR", "当前所在数据池ID", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("上次所在数据池分区", "AREALAST", "上次所在数据池分区", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("当前所在数据池分区", "AREACUR", "当前所在数据池分区", WorkSheetDataType.INT, -1, false, true);	
		creationInfoWorkSheet.addColumn("是否被回收", "ISRECOVER", "是否被回收", WorkSheetDataType.INT, -1, false, true);	
		creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		
		return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_DATAPOOL,errMessage);
	}
	
	//数据池记录操作表
		private ServiceResultCode m_newWorkSheetDataPoolORE(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%d_POOL_ORE", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%ds数据池记录操作表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%d数据池记录操作表，数据池记录操作数据存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("分配批次ID", "DID", "分配批次号", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("本次操作", "OPERATIONNAME", "本次操作", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("上次所在数据池ID", "DATAPOOlIDLAST", "上次所在数据池ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("当前所在数据池ID", "DATAPOOlIDCUR", "当前所在数据池ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("上次所在数据池分区", "AREALAST", "上次所在数据池分区", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("当前所在数据池分区", "AREACUR", "当前所在数据池分区", WorkSheetDataType.INT, -1, false, true);	
			creationInfoWorkSheet.addColumn("是否被回收", "ISRECOVER", "是否被回收", WorkSheetDataType.INT, -1, false, true);	
			creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_DATAPOOLORE,errMessage);
		}
		//单号码重拨模式共享数据状态表
		private ServiceResultCode m_newWorkSheetDataM3(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%d_DATAM3", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%ds单号码重拨模式共享数据状态表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%d单号码重拨模式共享数据状态表，单号码重拨模式共享数据状态数据存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("坐席使用状态", "USERUSESTATE", "坐席使用状态", WorkSheetDataType.TEXT, 20, false, true);
			creationInfoWorkSheet.addColumn("是否内存加载", "ISMEMORYLOADIN", "是否内存加载", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改ID", "ModifyID", "修改ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("修改描述", "ModifyDesc", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
			creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("最近一次拨打时间", "LASTDIALTIME", "最近一次拨打时间", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("下次拨打时间", "NEXTDIALTIME", "下次拨打时间", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("当天已拨打次数", "THISDAYDIALEDCOUNT", "当天已拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("当前重拨阶段数", "CURREDIALSTAGECOUNT", "当前重拨阶段数", WorkSheetDataType.INT, -1, false, true);
			
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_DATAM3,errMessage);
		}
	
		private ServiceResultCode m_newWorkSheetDataM3_his(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%d_DATAM3_HIS", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%ds单号码重拨模式共享数据状态历史表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%d单号码重拨模式共享数据状态历史表，单号码重拨模式共享数据状态历史数据存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("坐席使用状态", "USERUSESTATE", "坐席使用状态", WorkSheetDataType.TEXT, 20, false, true);
			creationInfoWorkSheet.addColumn("是否内存加载", "ISMEMORYLOADIN", "是否内存加载", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改ID", "ModifyID", "修改ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改用户ID", "ModifyUserID", "修改用户ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "ModifyTime", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("修改描述", "ModifyDesc", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
			creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("最近一次拨打时间", "LASTDIALTIME", "最近一次拨打时间", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("下次拨打时间", "NEXTDIALTIME", "下次拨打时间", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("当天已拨打次数", "THISDAYDIALEDCOUNT", "当天已拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("当前重拨阶段数", "CURREDIALSTAGECOUNT", "当前重拨阶段数", WorkSheetDataType.INT, -1, false, true);
			
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_DATAM3_HIS,errMessage);
		}
	
	private ServiceResultCode m_newWs(Connection dbConn,int bizId,CreationInfoWorkSheet creationInfoWorkSheet,DMWorkSheetTypeEnum dmWsType,StringBuffer errMessage){
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
