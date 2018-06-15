package hiapp.modules.dmsetting.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.DMWorkSheet;
import hiapp.modules.dmsetting.DMWorkSheetTypeEnum;
import hiapp.system.worksheet.bean.CreationInfoWorkSheet;
import hiapp.system.worksheet.bean.WorkSheet;
import hiapp.system.worksheet.bean.WorkSheetColumn;
import hiapp.system.worksheet.bean.WorkSheetDataType;
import hiapp.system.worksheet.data.WorkSheetRepository;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.database.DatabaseType;
import hiapp.utils.idfactory.IdFactory;
import hiapp.utils.serviceresult.ServiceResultCode;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月8日 上午10:02:56 
 * 类说明 DM工作表与数据层、与工作表模块交互方法
 */
@Repository
public class DmWorkSheetRepository extends BaseRepository {
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
	@Autowired
	private WorkSheetRepository workSheetRepository;
	@Autowired
	private IdFactory idFactory;
	@Autowired
	private Logger logger;
	
	public ServiceResultCode newDMBizWorkSheetsSystem(DMBusiness dmBusiness,StringBuffer errMessage) {
		Connection dbConn = null;
		try {
			dbConn = this.getDbConnection();
			ServiceResultCode sResultCode=null;
			if (dmBusiness.getModeId()==1) {
				sResultCode=m_newWorkSheetImport(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetPresetTime(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetResult(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPool(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPoolORE(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetWenjuan(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
			} else if (dmBusiness.getModeId()==2) {
				sResultCode=m_newWorkSheetImport(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetPresetTime(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetResult(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPool(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPoolORE(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataM2(dbConn,dmBusiness,errMessage);			if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataM2_his(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetWenjuan(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
			} else if (dmBusiness.getModeId()==3) {
				sResultCode=m_newWorkSheetImport(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetPresetTime(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetResult(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPool(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPoolORE(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataM3(dbConn,dmBusiness,errMessage);			if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataM3_his(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetWenjuan(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
			} else if (dmBusiness.getModeId()==4) {
				sResultCode=m_newWorkSheetImport(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetPresetTime(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetResult(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPool(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPoolORE(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDuoDataM4(dbConn,dmBusiness,errMessage);			if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDuoDataM4_His(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetWenjuan(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
			} else if (dmBusiness.getModeId()==5) {
				
				sResultCode=m_newWorkSheetImport(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetPresetTime(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetResult(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPool(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPoolORE(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataM5(dbConn,dmBusiness,errMessage);			if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataM5_his(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetWenjuan(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
			} else if (dmBusiness.getModeId()==6) {
				sResultCode=m_newWorkSheetImport(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetPresetTime(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetResult(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPool(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDataPoolORE(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDuoDataM3(dbConn,dmBusiness,errMessage);			if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetDuoDataM3_his(dbConn,dmBusiness,errMessage);		if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
				sResultCode=m_newWorkSheetWenjuan(dbConn,dmBusiness,errMessage);	if(sResultCode!=ServiceResultCode.SUCCESS)return sResultCode;
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
	private ServiceResultCode m_newWorkSheetImport(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAU_DM_B%dC_IMPORT", dmBusiness.getBizId());
		String szWorkSheetNameCh=String.format("外拨业务%d导入表", dmBusiness.getBizId());
		String szWorkSheetDescription=String.format("业务%d导入表，客户从Excel或外部数据导入到系统中，首先存入此工作表",dmBusiness.getBizId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("是否最后一次修改", "MODIFYLAST", "任务唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_IMPORT,errMessage);
	}
	//结果表
	private ServiceResultCode m_newWorkSheetResult(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAU_DM_B%dC_RESULT", dmBusiness.getBizId());
		String szWorkSheetNameCh=String.format("外拨业务%d结果表", dmBusiness.getBizId());
		String szWorkSheetDescription=String.format("外拨业务%d结果表，创建后，拨打结果存入此工作表",dmBusiness.getBizId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("来源编号", "SOURCEID", "来源编号", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("是否最后一次修改", "MODIFYLAST", "任务唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("拨打类型", "DIALTYPE", "拨打类型", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("拨打时间", "DIALTIME", "拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("呼叫流水号", "CUSTOMERCALLID", "呼叫流水号", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
		return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_RESULT,errMessage);
	}
	
	//预约时间表
	private ServiceResultCode m_newWorkSheetPresetTime(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HASYS_DM_B%dC_PRESETTIME", dmBusiness.getBizId());
		String szWorkSheetNameCh=String.format("外拨业务%d预约时间表", dmBusiness.getBizId());
		String szWorkSheetDescription=String.format("外拨业务%d预约时间表，坐席预约的数据存入此工作表，只保留最后预约的数据",dmBusiness.getBizId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("来源编号", "SOURCEID", "来源编号", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("预约日期时间", "PRESETTIME", "预约日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("预约状态", "STATE", "预约状态", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("预约备注", "PRESETCOMMENT", "预约备注", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("是否最后一次修改", "MODIFYLAST", "任务唯一标识", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("修改描述", "MODIFYDESC", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
		creationInfoWorkSheet.addColumn("号码类型", "PHONETYPE", "号码类型", WorkSheetDataType.TEXT, 50, false, true);
		
		return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_PRESET,errMessage);
	}
	//数据池记录表
	private ServiceResultCode m_newWorkSheetDataPool(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		String szWorkSheetName=String.format("HAU_DM_B%dC_POOL", dmBusiness.getBizId());
		String szWorkSheetNameCh=String.format("外拨业务%ds数据池记录表", dmBusiness.getBizId());
		String szWorkSheetDescription=String.format("外拨业务%d数据池记录表，数据池记录数据存入此工作表",dmBusiness.getBizId());
		creationInfoWorkSheet.setName(szWorkSheetName);
		creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
		creationInfoWorkSheet.setDescription(szWorkSheetDescription);
		creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
		creationInfoWorkSheet.addColumn("来源编号", "SOURCEID", "来源编号", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("上次所在数据池ID", "DATAPOOLIDLAST", "上次所在数据池ID", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("当前所在数据池ID", "DATAPOOLIDCUR", "当前所在数据池ID", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("上次所在数据池分区", "AREALAST", "上次所在数据池分区", WorkSheetDataType.INT, -1, false, true);
		creationInfoWorkSheet.addColumn("当前所在数据池分区", "AREACUR", "当前所在数据池分区", WorkSheetDataType.INT, -1, false, true);	
		creationInfoWorkSheet.addColumn("是否被回收", "ISRECOVER", "是否被回收", WorkSheetDataType.INT, -1, false, true);	
		creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
		creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
		creationInfoWorkSheet.addColumn("当前操作", "OPERATIONNAME", "当前操作", WorkSheetDataType.TEXT, 50, false, true);
		return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_POOL,errMessage);
	}
	
	//数据池操作记录表
		private ServiceResultCode m_newWorkSheetDataPoolORE(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%dC_POOL_ORE", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%ds数据池操作记录表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%d数据池记录操作表，数据池记录操作数据存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("来源编号", "SOURCEID", "来源编号", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("本次操作", "OPERATIONNAME", "本次操作", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("上次所在数据池ID", "DATAPOOLIDLAST", "上次所在数据池ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("当前所在数据池ID", "DATAPOOLIDCUR", "当前所在数据池ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("上次所在数据池分区", "AREALAST", "上次所在数据池分区", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("当前所在数据池分区", "AREACUR", "当前所在数据池分区", WorkSheetDataType.INT, -1, false, true);	
			creationInfoWorkSheet.addColumn("是否被回收", "ISRECOVER", "是否被回收", WorkSheetDataType.INT, -1, false, true);	
			creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_POOLORE,errMessage);
		}
		
		//hidialer预测外呼

		private ServiceResultCode m_newWorkSheetDataM2(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%dC_DATAM2", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%dhidialer预测外拨模式共享数据状态表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%dhidialer预测外拨模式共享数据状态表，单号码预测外拨模式共享数据状态数据存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("修改描述", "MODIFYDSP", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
			creationInfoWorkSheet.addColumn("是否追加", "ISAPPEND", "是否追加", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话号码", "PHONENUMBER", "电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("最后拨打时间", "LASTDIALDAY", "最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("下次拨打时间", "NEXTDIALTIME", "下次拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("呼损次数", "CALLLOSSCOUNT", "呼损次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("重拨次数", "REDIALCOUNT", "重拨次数", WorkSheetDataType.INT, -1, false, true);
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHARE,errMessage);
		}
		
		//hidialer预测外呼历史
		private ServiceResultCode m_newWorkSheetDataM2_his(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%dC_DATAM2_HIS", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%dhidialer预测外拨模式共享数据状态表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%dhidialer预测外拨模式共享数据状态表，单号码预测外拨模式共享数据状态数据存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("修改描述", "MODIFYDSP", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
			creationInfoWorkSheet.addColumn("是否追加", "ISAPPEND", "是否追加", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话号码", "PHONENUMBER", "电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("最后拨打时间", "LASTDIALDAY", "最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("下次拨打时间", "NEXTDIALTIME", "下次拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("呼损次数", "CALLLOSSCOUNT", "呼损次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("重拨次数", "REDIALCOUNT", "重拨次数", WorkSheetDataType.INT, -1, false, true);
			
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHARE,errMessage);
		}
		
		
		//单号码预测
		
				private ServiceResultCode m_newWorkSheetDataM5(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
					CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
					creationInfoWorkSheet.setOwner(true);
					String szWorkSheetName=String.format("HAU_DM_B%dC_DATAM5", dmBusiness.getBizId());
					String szWorkSheetNameCh=String.format("外拨业务%ds单号码预测外拨模式共享数据状态表", dmBusiness.getBizId());
					String szWorkSheetDescription=String.format("外拨业务%d单号码预测外拨模式共享数据状态表，单号码预测外拨模式共享数据状态数据存入此工作表",dmBusiness.getBizId());
					creationInfoWorkSheet.setName(szWorkSheetName);
					creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
					creationInfoWorkSheet.setDescription(szWorkSheetDescription);
					creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
					creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
					creationInfoWorkSheet.addColumn("坐席使用状态", "USERUSESTATE", "坐席使用状态", WorkSheetDataType.TEXT, 20, false, true);
					creationInfoWorkSheet.addColumn("是否内存加载", "ISMEMORYLOADIN", "是否内存加载", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改ID", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("修改描述", "MODIFYDSP", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
					creationInfoWorkSheet.addColumn("是否追加", "ISAPPEND", "是否追加", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("客户是否重拨", "CUSTSTOP", "客户是否重拨", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("重拨次数", "DIALCOUNT", "重拨次数", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("电话号码", "PHONENUMBER", "电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("最后拨打时间", "LASTDIALDAY", "最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("最多拨打次数", "LASTDAYDIALCOUNT", "最多拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("是否再次拨打", "PHONESTOP", "是否再次拨打", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("预约拨打时间", "CURPRESETDIALTIME", "预约拨打时间", WorkSheetDataType.TEXT, 50, false, true);
					
					return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHARE,errMessage);
				}
		//单号码预测历史
				private ServiceResultCode m_newWorkSheetDataM5_his(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
					CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
					creationInfoWorkSheet.setOwner(true);
					String szWorkSheetName=String.format("HAU_DM_B%dC_DATAM5_HIS", dmBusiness.getBizId());
					String szWorkSheetNameCh=String.format("外拨业务%ds单号码预测外拨模式共享数据状态表", dmBusiness.getBizId());
					String szWorkSheetDescription=String.format("外拨业务%d单号码预测外拨模式共享数据状态表，单号码预测外拨模式共享数据状态数据存入此工作表",dmBusiness.getBizId());
					creationInfoWorkSheet.setName(szWorkSheetName);
					creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
					creationInfoWorkSheet.setDescription(szWorkSheetDescription);
					creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
					creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
					creationInfoWorkSheet.addColumn("坐席使用状态", "USERUSESTATE", "坐席使用状态", WorkSheetDataType.TEXT, 20, false, true);
					creationInfoWorkSheet.addColumn("是否内存加载", "ISMEMORYLOADIN", "是否内存加载", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改ID", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("修改描述", "MODIFYDSP", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
					creationInfoWorkSheet.addColumn("是否追加", "ISAPPEND", "是否追加", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("客户是否重拨", "CUSTSTOP", "客户是否重拨", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("重拨次数", "DIALCOUNT", "重拨次数", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("电话号码", "PHONENUMBER", "电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("最后拨打时间", "LASTDIALDAY", "最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("最多拨打次数", "LASTDAYDIALCOUNT", "最多拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("是否再次拨打", "PHONESTOP", "是否再次拨打", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("预约拨打时间", "CURPRESETDIALTIME", "预约拨打时间", WorkSheetDataType.TEXT, 50, false, true);
					
					return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHARE,errMessage);
				}
		
		
		
		
		
		//单号码重拨模式共享数据状态表
		private ServiceResultCode m_newWorkSheetDataM3(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%dC_DATAM3", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%ds单号码重拨模式共享数据状态表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%d单号码重拨模式共享数据状态表，单号码重拨模式共享数据状态数据存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("坐席使用状态", "USERUSESTATE", "坐席使用状态", WorkSheetDataType.TEXT, 20, false, true);
			creationInfoWorkSheet.addColumn("是否内存加载", "ISMEMORYLOADIN", "是否内存加载", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("修改描述", "MODIFYDESC", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
			creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("最近一次拨打时间", "LASTDIALTIME", "最近一次拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("下次拨打时间", "NEXTDIALTIME", "下次拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("当天已拨打次数", "THISDAYDIALEDCOUNT", "当天已拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("当前重拨阶段数", "CURREDIALSTAGECOUNT", "当前重拨阶段数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("第一次未接通日期", "LOSTCALLFIRSTDAY", "第一次未接通日期", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("当前未接通日期", "LOSTCALLCURDAY", "当前未接通日期", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("未接通总次数", "LOSTCALLTOTALCOUNT", "未接通总次数", WorkSheetDataType.INT, -1, false, true);
			
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHARE,errMessage);
		}
		//单号码重拨模式共享数据状态历史表
		private ServiceResultCode m_newWorkSheetDataM3_his(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%dC_DATAM3_HIS", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%ds单号码重拨模式共享数据状态历史表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%d单号码重拨模式共享数据状态历史表，单号码重拨模式共享数据状态历史数据存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("坐席使用状态", "USERUSESTATE", "坐席使用状态", WorkSheetDataType.TEXT, 20, false, true);
			creationInfoWorkSheet.addColumn("是否内存加载", "ISMEMORYLOADIN", "是否内存加载", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("修改描述", "MODIFYDESC", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
			creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("最近一次拨打时间", "LASTDIALTIME", "最近一次拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("下次拨打时间", "NEXTDIALTIME", "下次拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("当天已拨打次数", "THISDAYDIALEDCOUNT", "当天已拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("当前重拨阶段数", "CURREDIALSTAGECOUNT", "当前重拨阶段数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("第一次未接通日期", "LOSTCALLFIRSTDAY", "第一次未接通日期", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("当前未接通日期", "LOSTCALLCURDAY", "当前未接通日期", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("未接通总次数", "LOSTCALLTOTALCOUNT", "未接通总次数", WorkSheetDataType.INT, -1, false, true);
			
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHAREHISTROY,errMessage);
		}
		
		//多号码重拨
		private ServiceResultCode m_newWorkSheetDuoDataM4(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%dC_DATAM4", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%d多号码重拨模式共享数据状态表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%d多号码重拨模式共享数据状态表，多号码重拨模式共享数据状态存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("座席使用状态", "USERUSESTATE", "座席使用状态", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("是否内存加载", "ISMEMORYLOADING", "是否内存加载", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("修改描述", "MODIFYDSP", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
			creationInfoWorkSheet.addColumn("是否追加", "ISAPPEND", "是否追加", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_电话号码", "PT1_PHONENUMBER", "电话类型1_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_最后拨打时间", "PT1_LASTDIALTIME", "电话类型1_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_产生预约拨打次数", "PT1_CAUSEPRESETDIALCOUNT", "电话类型1_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_拨打次数", "PT1_DIALCOUNT", "电话类型1_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_电话号码", "PT2_PHONENUMBER", "电话类型2_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_最后拨打时间", "PT2_LASTDIALTIME", "电话类型2_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_产生预约拨打次数", "PT2_CAUSEPRESETDIALCOUNT", "电话类型2_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_拨打次数", "PT2_DIALCOUNT", "电话类型2_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_电话号码", "PT3_PHONENUMBER", "电话类型3_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_最后拨打时间", "PT3_LASTDIALTIME", "电话类型3_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_产生预约拨打次数", "PT3_CAUSEPRESETDIALCOUNT", "电话类型3_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_拨打次数", "PT3_DIALCOUNT", "电话类型3_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_电话号码", "PT4_PHONENUMBER", "电话类型4_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_最后拨打时间", "PT4_LASTDIALTIME", "电话类型4_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_产生预约拨打次数", "PT4_CAUSEPRESETDIALCOUNT", "电话类型4_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_拨打次数", "PT4_DIALCOUNT", "电话类型4_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_电话号码", "PT5_PHONENUMBER", "电话类型5_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_最后拨打时间", "PT5_LASTDIALTIME", "电话类型5_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_产生预约拨打次数", "PT5_CAUSEPRESETDIALCOUNT", "电话类型5_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_拨打次数", "PT5_DIALCOUNT", "电话类型5_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_电话号码", "PT6_PHONENUMBER", "电话类型6_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_最后拨打时间", "PT6_LASTDIALTIME", "电话类型6_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_产生预约拨打次数", "PT6_CAUSEPRESETDIALCOUNT", "电话类型6_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_拨打次数", "PT6_DIALCOUNT", "电话类型6_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_电话号码", "PT7_PHONENUMBER", "电话类型7_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_最后拨打时间", "PT7_LASTDIALTIME", "电话类型7_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_产生预约拨打次数", "PT7_CAUSEPRESETDIALCOUNT", "电话类型7_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_拨打次数", "PT7_DIALCOUNT", "电话类型7_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_电话号码", "PT8_PHONENUMBER", "电话类型8_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_最后拨打时间", "PT8_LASTDIALTIME", "电话类型8_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_产生预约拨打次数", "PT8_CAUSEPRESETDIALCOUNT", "电话类型8_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_拨打次数", "PT8_DIALCOUNT", "电话类型8_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_电话号码", "PT9_PHONENUMBER", "电话类型9_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_最后拨打时间", "PT9_LASTDIALTIME", "电话类型9_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_产生预约拨打次数", "PT9_CAUSEPRESETDIALCOUNT", "电话类型9_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_拨打次数", "PT9_DIALCOUNT", "电话类型9_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_电话号码", "PT10_PHONENUMBER", "电话类型10_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_最后拨打时间", "PT10_LASTDIALTIME", "电话类型10_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_产生预约拨打次数", "PT10_CAUSEPRESETDIALCOUNT", "电话类型10_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_拨打次数", "PT10_DIALCOUNT", "电话类型10_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("当前已拨打号码", "CURDIALPHONE", "当前已拨打号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("本号码预约拨打时间", "CURPRESETDIALTIME", "本号码预约拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("当前已拨打号码类型", "CURDIALPHONETYPE", "当前已拨打号码类型", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("下次拨打号码类型", "NEXTDIALPHONETYPE", "下次拨打号码类型", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("当前阶段数", "CURSTAGENUM", "当前阶段数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("第一次拨打日期", "FIRSTDIALDATE", "第一次拨打日期", WorkSheetDataType.DATETIME, -1, false, true);

			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHARE,errMessage);
		}
		
		//多号码重拨历史
				private ServiceResultCode m_newWorkSheetDuoDataM4_His(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
					CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
					creationInfoWorkSheet.setOwner(true);
					String szWorkSheetName=String.format("HAU_DM_B%dC_DATAM4_HIS", dmBusiness.getBizId());
					String szWorkSheetNameCh=String.format("外拨业务%d多号码重拨模式共享数据状态表", dmBusiness.getBizId());
					String szWorkSheetDescription=String.format("外拨业务%d多号码重拨模式共享数据状态表，多号码重拨模式共享数据状态存入此工作表",dmBusiness.getBizId());
					creationInfoWorkSheet.setName(szWorkSheetName);
					creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
					creationInfoWorkSheet.setDescription(szWorkSheetDescription);
					creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
					creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
					creationInfoWorkSheet.addColumn("座席使用状态", "USERUSESTATE", "座席使用状态", WorkSheetDataType.TEXT, 50, false, true);	
					creationInfoWorkSheet.addColumn("是否内存加载", "ISMEMORYLOADING", "是否内存加载", WorkSheetDataType.TEXT, 50, false, true);	
					creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改ID", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("修改描述", "MODIFYDSP", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
					creationInfoWorkSheet.addColumn("是否追加", "ISAPPEND", "是否追加", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型1_电话号码", "PT1_PHONENUMBER", "电话类型1_电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型1_最后拨打时间", "PT1_LASTDIALTIME", "电话类型1_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("电话类型1_产生预约拨打次数", "PT1_CAUSEPRESETDIALCOUNT", "电话类型1_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型1_拨打次数", "PT1_DIALCOUNT", "电话类型1_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型2_电话号码", "PT2_PHONENUMBER", "电话类型2_电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型2_最后拨打时间", "PT2_LASTDIALTIME", "电话类型2_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("电话类型2_产生预约拨打次数", "PT2_CAUSEPRESETDIALCOUNT", "电话类型2_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型2_拨打次数", "PT2_DIALCOUNT", "电话类型2_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型3_电话号码", "PT3_PHONENUMBER", "电话类型3_电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型3_最后拨打时间", "PT3_LASTDIALTIME", "电话类型3_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("电话类型3_产生预约拨打次数", "PT3_CAUSEPRESETDIALCOUNT", "电话类型3_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型3_拨打次数", "PT3_DIALCOUNT", "电话类型3_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型4_电话号码", "PT4_PHONENUMBER", "电话类型4_电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型4_最后拨打时间", "PT4_LASTDIALTIME", "电话类型4_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("电话类型4_产生预约拨打次数", "PT4_CAUSEPRESETDIALCOUNT", "电话类型4_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型4_拨打次数", "PT4_DIALCOUNT", "电话类型4_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型5_电话号码", "PT5_PHONENUMBER", "电话类型5_电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型5_最后拨打时间", "PT5_LASTDIALTIME", "电话类型5_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("电话类型5_产生预约拨打次数", "PT5_CAUSEPRESETDIALCOUNT", "电话类型5_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型5_拨打次数", "PT5_DIALCOUNT", "电话类型5_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型6_电话号码", "PT6_PHONENUMBER", "电话类型6_电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型6_最后拨打时间", "PT6_LASTDIALTIME", "电话类型6_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("电话类型6_产生预约拨打次数", "PT6_CAUSEPRESETDIALCOUNT", "电话类型6_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型6_拨打次数", "PT6_DIALCOUNT", "电话类型6_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型7_电话号码", "PT7_PHONENUMBER", "电话类型7_电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型7_最后拨打时间", "PT7_LASTDIALTIME", "电话类型7_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("电话类型7_产生预约拨打次数", "PT7_CAUSEPRESETDIALCOUNT", "电话类型7_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型7_拨打次数", "PT7_DIALCOUNT", "电话类型7_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型8_电话号码", "PT8_PHONENUMBER", "电话类型8_电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型8_最后拨打时间", "PT8_LASTDIALTIME", "电话类型8_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("电话类型8_产生预约拨打次数", "PT8_CAUSEPRESETDIALCOUNT", "电话类型8_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型8_拨打次数", "PT8_DIALCOUNT", "电话类型8_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型9_电话号码", "PT9_PHONENUMBER", "电话类型9_电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型9_最后拨打时间", "PT9_LASTDIALTIME", "电话类型9_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("电话类型9_产生预约拨打次数", "PT9_CAUSEPRESETDIALCOUNT", "电话类型9_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型9_拨打次数", "PT9_DIALCOUNT", "电话类型9_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型10_电话号码", "PT10_PHONENUMBER", "电话类型10_电话号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型10_最后拨打时间", "PT10_LASTDIALTIME", "电话类型10_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("电话类型10_产生预约拨打次数", "PT10_CAUSEPRESETDIALCOUNT", "电话类型10_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
					creationInfoWorkSheet.addColumn("电话类型10_拨打次数", "PT10_DIALCOUNT", "电话类型10_拨打次数", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("当前已拨打号码", "CURDIALPHONE", "当前已拨打号码", WorkSheetDataType.TEXT, 50, false, true);
					creationInfoWorkSheet.addColumn("本号码预约拨打时间", "CURPRESETDIALTIME", "本号码预约拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
					creationInfoWorkSheet.addColumn("当前已拨打号码类型", "CURDIALPHONETYPE", "当前已拨打号码类型", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("下次拨打号码类型", "NEXTDIALPHONETYPE", "下次拨打号码类型", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("当前阶段数", "CURSTAGENUM", "当前阶段数", WorkSheetDataType.INT, -1, false, true);
					creationInfoWorkSheet.addColumn("第一次拨打日期", "FIRSTDIALDATE", "第一次拨打日期", WorkSheetDataType.DATETIME, -1, false, true);
					return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHARE,errMessage);
				}
		
		
		//多号码预测外呼
		private ServiceResultCode m_newWorkSheetDuoDataM3(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%dC_DATAM6", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%d多号码预测外拨模式共享数据状态表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%d多号码预测外拨模式共享数据状态表，多号码预测外拨模式共享数据状态存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("修改描述", "MODIFYDESC", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
			creationInfoWorkSheet.addColumn("是否追加", "ISAPPEND", "是否追加", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_电话号码", "PT1_PHONENUMBER", "电话类型1_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_最后拨打时间", "PT1_LASTDIALTIME", "电话类型1_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_产生预约拨打次数", "PT1_CAUSEPRESETDIALCOUNT", "电话类型1_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_拨打次数", "PT1_DIALCOUNT", "电话类型1_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_电话号码", "PT2_PHONENUMBER", "电话类型2_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_最后拨打时间", "PT2_LASTDIALTIME", "电话类型2_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_产生预约拨打次数", "PT2_CAUSEPRESETDIALCOUNT", "电话类型2_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_拨打次数", "PT2_DIALCOUNT", "电话类型2_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_电话号码", "PT3_PHONENUMBER", "电话类型3_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_最后拨打时间", "PT3_LASTDIALTIME", "电话类型3_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_产生预约拨打次数", "PT3_CAUSEPRESETDIALCOUNT", "电话类型3_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_拨打次数", "PT3_DIALCOUNT", "电话类型3_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_电话号码", "PT4_PHONENUMBER", "电话类型4_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_最后拨打时间", "PT4_LASTDIALTIME", "电话类型4_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_产生预约拨打次数", "PT4_CAUSEPRESETDIALCOUNT", "电话类型4_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_拨打次数", "PT4_DIALCOUNT", "电话类型4_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_电话号码", "PT5_PHONENUMBER", "电话类型5_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_最后拨打时间", "PT5_LASTDIALTIME", "电话类型5_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_产生预约拨打次数", "PT5_CAUSEPRESETDIALCOUNT", "电话类型5_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_拨打次数", "PT5_DIALCOUNT", "电话类型5_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_电话号码", "PT6_PHONENUMBER", "电话类型6_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_最后拨打时间", "PT6_LASTDIALTIME", "电话类型6_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_产生预约拨打次数", "PT6_CAUSEPRESETDIALCOUNT", "电话类型6_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_拨打次数", "PT6_DIALCOUNT", "电话类型6_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_电话号码", "PT7_PHONENUMBER", "电话类型7_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_最后拨打时间", "PT7_LASTDIALTIME", "电话类型7_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_产生预约拨打次数", "PT7_CAUSEPRESETDIALCOUNT", "电话类型7_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_拨打次数", "PT7_DIALCOUNT", "电话类型7_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_电话号码", "PT8_PHONENUMBER", "电话类型8_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_最后拨打时间", "PT8_LASTDIALTIME", "电话类型8_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_产生预约拨打次数", "PT8_CAUSEPRESETDIALCOUNT", "电话类型8_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_拨打次数", "PT8_DIALCOUNT", "电话类型8_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_电话号码", "PT9_PHONENUMBER", "电话类型9_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_最后拨打时间", "PT9_LASTDIALTIME", "电话类型9_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_产生预约拨打次数", "PT9_CAUSEPRESETDIALCOUNT", "电话类型9_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_拨打次数", "PT9_DIALCOUNT", "电话类型9_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_电话号码", "PT10_PHONENUMBER", "电话类型10_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_最后拨打时间", "PT10_LASTDIALTIME", "电话类型10_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_产生预约拨打次数", "PT10_CAUSEPRESETDIALCOUNT", "电话类型10_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_拨打次数", "PT10_DIALCOUNT", "电话类型10_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("当前已拨打号码", "CURDIALPHONE", "当前已拨打号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("本号码预约拨打时间", "CURPRESETDIALTIME", "本号码预约拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("当前已拨打号码类型", "CURDIALPHONETYPE", "当前已拨打号码类型", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("下次拨打号码类型", "NEXTDIALPHONETYPE", "下次拨打号码类型", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("呼损次数", "CALLLOSSCOUNT", "呼损次数", WorkSheetDataType.INT, -1, false, true);
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHARE,errMessage);
		}
		//多号码预测外呼历史表
		private ServiceResultCode m_newWorkSheetDuoDataM3_his(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%dC_DATAM6_HIS", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%d多号码预测外拨模式共享数据状态历史表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%d多号码预测外拨模式共享数据状态历史表，多号码预测外拨模式共享数据状态历史存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("业务ID", "BUSINESSID", "业务ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("共享ID", "SHAREID", "共享ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("状态", "STATE", "状态", WorkSheetDataType.TEXT, 50, false, true);	
			creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("修改描述", "MODIFYDESC", "修改描述", WorkSheetDataType.TEXT, 1024, false, true);
			creationInfoWorkSheet.addColumn("是否追加", "ISAPPEND", "是否追加", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("客户呼叫对象", "CUSTOMERCALLID", "客户呼叫对象", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码类型", "ENDCODETYPE", "结束码类型", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("结束码", "ENDCODE", "结束码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_电话号码", "PT1_PHONENUMBER", "电话类型1_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_最后拨打时间", "PT1_LASTDIALTIME", "电话类型1_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_产生预约拨打次数", "PT1_CAUSEPRESETDIALCOUNT", "电话类型1_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型1_拨打次数", "PT1_DIALCOUNT", "电话类型1_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_电话号码", "PT2_PHONENUMBER", "电话类型2_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_最后拨打时间", "PT2_LASTDIALTIME", "电话类型2_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_产生预约拨打次数", "PT2_CAUSEPRESETDIALCOUNT", "电话类型2_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型2_拨打次数", "PT2_DIALCOUNT", "电话类型2_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_电话号码", "PT3_PHONENUMBER", "电话类型3_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_最后拨打时间", "PT3_LASTDIALTIME", "电话类型3_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_产生预约拨打次数", "PT3_CAUSEPRESETDIALCOUNT", "电话类型3_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型3_拨打次数", "PT3_DIALCOUNT", "电话类型3_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_电话号码", "PT4_PHONENUMBER", "电话类型4_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_最后拨打时间", "PT4_LASTDIALTIME", "电话类型4_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_产生预约拨打次数", "PT4_CAUSEPRESETDIALCOUNT", "电话类型4_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型4_拨打次数", "PT4_DIALCOUNT", "电话类型4_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_电话号码", "PT5_PHONENUMBER", "电话类型5_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_最后拨打时间", "PT5_LASTDIALTIME", "电话类型5_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_产生预约拨打次数", "PT5_CAUSEPRESETDIALCOUNT", "电话类型5_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型5_拨打次数", "PT5_DIALCOUNT", "电话类型5_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_电话号码", "PT6_PHONENUMBER", "电话类型6_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_最后拨打时间", "PT6_LASTDIALTIME", "电话类型6_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_产生预约拨打次数", "PT6_CAUSEPRESETDIALCOUNT", "电话类型6_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型6_拨打次数", "PT6_DIALCOUNT", "电话类型6_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_电话号码", "PT7_PHONENUMBER", "电话类型7_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_最后拨打时间", "PT7_LASTDIALTIME", "电话类型7_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_产生预约拨打次数", "PT7_CAUSEPRESETDIALCOUNT", "电话类型7_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型7_拨打次数", "PT7_DIALCOUNT", "电话类型7_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_电话号码", "PT8_PHONENUMBER", "电话类型8_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_最后拨打时间", "PT8_LASTDIALTIME", "电话类型8_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_产生预约拨打次数", "PT8_CAUSEPRESETDIALCOUNT", "电话类型8_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型8_拨打次数", "PT8_DIALCOUNT", "电话类型8_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_电话号码", "PT9_PHONENUMBER", "电话类型9_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_最后拨打时间", "PT9_LASTDIALTIME", "电话类型9_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_产生预约拨打次数", "PT9_CAUSEPRESETDIALCOUNT", "电话类型9_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型9_拨打次数", "PT9_DIALCOUNT", "电话类型9_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_电话号码", "PT10_PHONENUMBER", "电话类型10_电话号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_最后拨打时间", "PT10_LASTDIALTIME", "电话类型10_最后拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_产生预约拨打次数", "PT10_CAUSEPRESETDIALCOUNT", "电话类型10_产生预约拨打次数", WorkSheetDataType.INT, 50, false, true);
			creationInfoWorkSheet.addColumn("电话类型10_拨打次数", "PT10_DIALCOUNT", "电话类型10_拨打次数", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("当前已拨打号码", "CURDIALPHONE", "当前已拨打号码", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("本号码预约拨打时间", "CURPRESETDIALTIME", "本号码预约拨打时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("当前已拨打号码类型", "CURDIALPHONETYPE", "当前已拨打号码类型", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("下次拨打号码类型", "NEXTDIALPHONETYPE", "下次拨打号码类型", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("呼损次数", "CALLLOSSCOUNT", "呼损次数", WorkSheetDataType.INT, -1, false, true);
			
			
			
			
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHARE,errMessage);
		}
		
		
		private ServiceResultCode m_newWorkSheetWenjuan(Connection dbConn,DMBusiness dmBusiness,StringBuffer errMessage){
			CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
			creationInfoWorkSheet.setOwner(true);
			String szWorkSheetName=String.format("HAU_DM_B%dC_QUERESULT", dmBusiness.getBizId());
			String szWorkSheetNameCh=String.format("外拨业务%d问卷表", dmBusiness.getBizId());
			String szWorkSheetDescription=String.format("外拨业务%d问卷表，问卷关联数据存入此工作表",dmBusiness.getBizId());
			creationInfoWorkSheet.setName(szWorkSheetName);
			creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
			creationInfoWorkSheet.setDescription(szWorkSheetDescription);
			creationInfoWorkSheet.addColumn("ID", "ID", "ID标识，自增", WorkSheetDataType.INT, -1, true, true);
			creationInfoWorkSheet.addColumn("来源编号", "SOURCEID", "来源编号", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("导入批次ID", "IID", "导入批次ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("客户ID", "CID", "客户唯一标识", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改ID", "MODIFYID", "修改唯一标识", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("修改用户ID", "MODIFYUSERID", "修改用户ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("修改日期时间", "MODIFYTIME", "修改日期时间", WorkSheetDataType.DATETIME, -1, false, true);
			creationInfoWorkSheet.addColumn("是否最后一次修改", "MODIFYLAST", "任务唯一标识", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("问卷ID", "QUS_ID", "问卷ID", WorkSheetDataType.TEXT, 50, false, true);
			creationInfoWorkSheet.addColumn("题目ID", "QUS_INDEX", "题目ID", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("题目类型", "QUS_TYPE", "题目类型", WorkSheetDataType.INT, -1, false, true);
			creationInfoWorkSheet.addColumn("题目内容", "ORG_CONTENT", "题目内容", WorkSheetDataType.TEXT, 500, false, true);
			creationInfoWorkSheet.addColumn("答案编号", "RES_INDEX", "答案编号", WorkSheetDataType.TEXT, 10, false, true);
			creationInfoWorkSheet.addColumn("题目答案", "ORG_RES", "题目答案", WorkSheetDataType.TEXT, 500, false, true);
			
			return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_SHAREHISTROY,errMessage);
		}
		
		
		
		
		//多号码预测外呼历史表
				public  ServiceResultCode m_newWorkSheets(DMBusiness dmBusiness,StringBuffer errMessage,String worksheetname,String desc,String worksheetnamech){
					CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
					creationInfoWorkSheet.setOwner(true);
					String szWorkSheetName=String.format("HAU_DM_B%dC_%s", dmBusiness.getBizId(),worksheetname);
					String szWorkSheetNameCh=String.format("外拨业务%s", dmBusiness.getBizId()+worksheetnamech);
					String szWorkSheetDescription=String.format("%s",desc);
					creationInfoWorkSheet.setName(szWorkSheetName);
					creationInfoWorkSheet.setNameCh(szWorkSheetNameCh);
					creationInfoWorkSheet.setDescription(szWorkSheetDescription);
					creationInfoWorkSheet.addColumn("编号", "ID", "编号", WorkSheetDataType.INT, -1, false, true);
					Connection dbConn = null;
					try {
						dbConn = this.getDbConnection();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return m_newWs(dbConn,dmBusiness.getBizId(),creationInfoWorkSheet,DMWorkSheetTypeEnum.WSTDM_CUSTOM,errMessage);
				}
		
		
	//设置工作表ID前缀，创建表
	private ServiceResultCode m_newWs(Connection dbConn,int bizId,CreationInfoWorkSheet creationInfoWorkSheet,DMWorkSheetTypeEnum dmWsType,StringBuffer errMessage){
		String szSql;
		PreparedStatement stmt = null;
		try {
			String idPrefix = "DM";
			//获取工作表ID
			String workSheetId = idFactory.newId(idPrefix);
			creationInfoWorkSheet.setId(workSheetId);
			if(!workSheetRepository.newWorkSheet(DatabaseType.ORACLE,creationInfoWorkSheet,idPrefix)){
				return ServiceResultCode.EXECUTE_SQL_FAIL;
			}
		} catch (Exception e) {
		}
		try {
			szSql="INSERT INTO HASYS_DM_BIZWORKSHEET (ID,BIZID,WORKSHEETID,TYPE) VALUES (HASYS_DM_BIZWORKSHEET_ID.nextval,?,?,?) ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1, bizId);
			stmt.setString(2, creationInfoWorkSheet.getId());
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

	//修改工作表信息
	public ServiceResultCode modify(Connection dbConn,int bizId,String id,String nameCh,String name,String description,StringBuffer errMessage){
		CreationInfoWorkSheet creationInfoWorkSheet=new CreationInfoWorkSheet();
		creationInfoWorkSheet.setOwner(true);
		creationInfoWorkSheet.setName(name);
		creationInfoWorkSheet.setNameCh(nameCh);
		creationInfoWorkSheet.setDescription(description);
		workSheetRepository.modifyWorkSheet(DatabaseType.ORACLE, creationInfoWorkSheet);
		return ServiceResultCode.SUCCESS;
	}
	//删除工作表
	public ServiceResultCode destroy(int bizId,String id,StringBuffer errMessage){
		try {
			workSheetRepository.destroyWorkSheet(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceResultCode.SUCCESS;
	}
	//删除HASYS_DM_BIZWORKSHEET里工作表记录，并同步删除工作表
	public ServiceResultCode destroyWorkSheet(String workSheetId) throws Exception{
		Connection dbConn = null;
		String szSql;
		PreparedStatement stmt = null;
		try {
			dbConn = this.getDbConnection();
			szSql="DELETE FROM HASYS_DM_BIZWORKSHEET WHERE WORKSHEETID=? ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setString(1, workSheetId);
			stmt.execute();
		} catch (Exception e) {
			return ServiceResultCode.EXECUTE_SQL_FAIL;
		}
		finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		//根据工作表ID删除工作表
		workSheetRepository.destroyWorkSheet(workSheetId);
		return ServiceResultCode.SUCCESS;
	}
	//删除业务下的全部工作表
	public ServiceResultCode destroyWorkSheetAll(int bizId, StringBuffer errMessage) throws Exception{
		List<DMWorkSheet> listworksheet=new ArrayList<DMWorkSheet>();
		dmWorkSheetRepository.getWorkSheetAll(bizId,listworksheet);
        for(int ii=0;ii<listworksheet.size();ii++){
        	WorkSheet workSheet=listworksheet.get(ii);
        	destroyWorkSheet(workSheet.getWorksheetId());
        }	
		return ServiceResultCode.SUCCESS;
	}
	//获取业务下所有工作表
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
				dmWorkSheet.setWorksheetId(rs.getString(1));
				dmWorkSheet.setWorksheetName(rs.getString(2));
				dmWorkSheet.setWorksheetNameCh(rs.getString(3));
				dmWorkSheet.setWorksheetDes(rs.getString(4));
				dmWorkSheet.setWorksheetType(rs.getString(5));
				dmWorkSheet.setIdNameCh(String.format("%s:%s", dmWorkSheet.getWorksheetId(),dmWorkSheet.getWorksheetNameCh()));
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
	public boolean getAllDMWorkSheetByBizId(List<DMWorkSheet> listDMWorkSheet,
			String bizId) {
		Connection dbConn = null;
		String szSql="";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn = this.getDbConnection();
			szSql=String.format("SELECT WORKSHEETID,TYPE FROM HASYS_DM_BIZWORKSHEET "
					+ "WHERE BIZID ='%s' "
					+ "ORDER BY WORKSHEETID",
					bizId);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				DMWorkSheet DMWorkSheet = new DMWorkSheet();
				DMWorkSheet.setWorksheetId(rs.getString(1));
				DMWorkSheet.setWorksheetType(rs.getString(2));
				try {
					String szSql2=String.format("SELECT NAME,NAMECH,DESCRIPTION,ISOWNER FROM HASYS_WORKSHEET "
							+ "WHERE ID ='%s'",
							DMWorkSheet.getWorksheetId());
					PreparedStatement stmt2 = dbConn.prepareStatement(szSql2);
					ResultSet rs2 = stmt2.executeQuery();
					while (rs2.next()) {
						DMWorkSheet.setWorksheetName(rs2.getString(1));
						DMWorkSheet.setWorksheetNameCh(rs2.getString(2));
						DMWorkSheet.setWorksheetDes(rs2.getString(3));
						DMWorkSheet.setIsFixed(rs2.getInt(4));
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				listDMWorkSheet.add(DMWorkSheet);
			}
		} 
		catch (Exception e) {
			return false;
		}
		finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return true;
	}
	//根据工作表ID获取工作表列信息
	public boolean getWorkSheetColumnByWorksheetId(
			List<WorkSheetColumn> listWorkSheetColumn, String worksheetId) {
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String szSql =null;
		try {
			dbConn = this.getDbConnection();
			szSql = "SELECT ColumnName,ColumnNameCh,ColumnDescription,DataTypeCh,Length,IsSysColumn,ISPHONECOLUMN FROM HASYS_WORKSHEETCOLUMN WHERE WorkSheetId=? ORDER BY ID ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setString(1,worksheetId);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				WorkSheetColumn workSheetColumn=new WorkSheetColumn();
				workSheetColumn.setColumnName(rs.getString(1));
				workSheetColumn.setColumnNameCh(rs.getString(2));
				workSheetColumn.setColumnDes(rs.getString(3));
				workSheetColumn.setColumnType(rs.getString(4));
				workSheetColumn.setColumnLength(rs.getInt(5));
				workSheetColumn.setFixedColumn(rs.getString(6));
				workSheetColumn.setIsPhoneColumn(rs.getInt(7));
				listWorkSheetColumn.add(workSheetColumn);	
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
	//修改工作表列中文名称
	public ServiceResultCode modifyColumnNameCh(String worksheetId, String columnName,
			String columnNameCh,String columnDes,String columnLength,String isPhoneColumn,StringBuffer errMessage) {
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String szSql =null;
		try {/////////////////////////////////////////////////////////////////
			dbConn = this.getDbConnection();
			int count=0;
			szSql =String.format("select COUNT(*) from hasys_worksheetcolumn where worksheetid='%s' and columnnamech='%s' and columnName!='%s'",worksheetId,columnNameCh,columnName);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count=rs.getInt(1);
			}
			if (count>0) {
				errMessage.append("列中文名冲突！");
				return ServiceResultCode.INVALID_PARAM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{	
			DbUtil.DbCloseQuery(rs, stmt);
		}
		try {
			if (!columnLength.equals("-1")) {
				String sql="select NAME from HASYS_WORKSHEET where ID='"+worksheetId+"'";
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				String workname="";
				if (rs.next()) {
					workname=rs.getString(1);
				}
				stmt.close();
				szSql=String.format("alter table "+workname+" modify("+columnName+" varchar2("+columnLength+"))");
				stmt = dbConn.prepareStatement(szSql);
				stmt.execute();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			errMessage.append("数据库文本长度不能设置为"+columnLength);
			return ServiceResultCode.INVALID_PARAM;
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
		}
		try {
			szSql=String.format("update hasys_worksheetcolumn set columnnamech = '%s',COLUMNDESCRIPTION = '%s',LENGTH="+columnLength+",ISPHONECOLUMN="+isPhoneColumn+" where worksheetid='%s' and columnname='%s' ",
					columnNameCh,columnDes,worksheetId,columnName);
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return ServiceResultCode.SUCCESS;
	}
	public ServiceResultCode newColumn(String worksheetId, String fixedColumn,
			String columnName, String columnNameCh, String columnType,
			String columnLength, String columnDes,String isPhoneColumn, StringBuffer errMessage) {
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String szSql =null;
		try {/////////////////////////////////////////////////////////////////
			dbConn = this.getDbConnection();
			int count=0;
			szSql =String.format("select COUNT(*) from hasys_worksheetcolumn where worksheetid='%s' and columnname='%s'",worksheetId,columnName);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count=rs.getInt(1);
			}
			if (count>0) {
				errMessage.append("列名冲突！");
				return ServiceResultCode.INVALID_PARAM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{	
			DbUtil.DbCloseQuery(rs, stmt);
		}
		try {/////////////////////////////////////////////////////////////////
			int count=0;
			szSql =String.format("select COUNT(*) from hasys_worksheetcolumn where worksheetid='%s' and columnnamech='%s'",worksheetId,columnNameCh);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count=rs.getInt(1);
			}
			if (count>0) {
				errMessage.append("列中文名冲突！");
				return ServiceResultCode.INVALID_PARAM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{	
			DbUtil.DbCloseQuery(rs, stmt);
		}
		try {
			int isSysColumn = 0;
			if (fixedColumn.equals("是")) {
				isSysColumn = 1;
			}
			String datatype = "";
			String type = "";
			if (columnType.equals("文本")) {
				datatype = "varchar";
				type ="VARCHAR2";
			}else if (columnType.equals("数值")) {
				datatype = "int";
				type ="NUMBER";
			}else if (columnType.equals("日期时间")) {
				datatype = "datetime";
				type ="DATE";
			}
			szSql = String.format("INSERT INTO hasys_worksheetcolumn(ID,WORKSHEETID,COLUMNNAME,COLUMNNAMECH,COLUMNDESCRIPTION,DATATYPE,LENGTH,ISSYSCOLUMN,DATATYPECH,ISPHONECOLUMN) VALUES (HASYS_WORKSHEETCOLUMN_ID.nextval,'%s','%s','%s','%s','%s','%s','%s','%s',%s)",
					worksheetId,columnName,columnNameCh,columnDes,datatype,columnLength,isSysColumn,columnType,isPhoneColumn);
			stmt = dbConn.prepareStatement(szSql);		
			stmt.execute();
			workSheetRepository.newWorkSheetColumn(worksheetId,columnName,type,columnLength);
		} catch (Exception e) {
			this.logger.info(String.format(e.getMessage()));
			e.printStackTrace();
			return ServiceResultCode.INVALID_PARAM;
		} finally {
			DbUtil.DbCloseExecute(stmt);
			DbUtil.DbCloseConnection(dbConn);
		}
		// TODO Auto-generated method stub
		return ServiceResultCode.SUCCESS;
	}
	
	public String getWorkSheetIdByType(int bizId,String worksheetType){
		String worksheetId = "";
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn = this.getDbConnection();
			String szSql = String.format("select WORKSHEETID from HASYS_DM_BIZWORKSHEET where BIZID='%S' and TYPE='%s' ",bizId,worksheetType);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				worksheetId = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		return worksheetId;
	}
}
