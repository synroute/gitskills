package hiapp.modules.dmsetting.data;

import hiapp.modules.dmsetting.DMBizAutomaticConfig;
import hiapp.modules.dmsetting.DMBizImportTemplate;
import hiapp.modules.dmsetting.result.DMBizAutomaticColumns;
import hiapp.system.worksheet.bean.WorkSheet;
import hiapp.system.worksheet.bean.WorkSheetColumn;
import hiapp.system.worksheet.data.WorkSheetRepository;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
@Repository
public class DmBizAutomaticRepository extends BaseRepository {
	
	@Autowired
	private WorkSheet workSheet;
	@Autowired
	private WorkSheetRepository workSheetRepository;
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
	
	//获取客户导入表列
	public List<DMBizAutomaticColumns> dmGetBizCustomerColumns(int bizId)
	{
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
		List<DMBizAutomaticColumns> listDmBizAutomaticColums=new ArrayList<DMBizAutomaticColumns>();
		try {
			//根据表名查询中文名称接worksheetid
			dbConn =this.getDbConnection();
			String szSelectSql="select ID,NameCh from HASYS_WORKSHEET where NAME='HAU_DM_B"+bizId+"C_IMPORT'";
			stmt = dbConn.prepareStatement(szSelectSql);
			rs = stmt.executeQuery();
			String workSheetId="";
			String worksheetName="";
			while(rs.next()){
				workSheetId=rs.getString(1);
				worksheetName=rs.getString(2);
			}
			
			//根据worksheetid获取该工作表下所有列信息
			workSheet.getColumns(dbConn,workSheetId, listColumns);
			//将列值绑定到列表中
			for (int i = 0; i < listColumns.size(); i++) {
				WorkSheetColumn workSheetColumn=listColumns.get(i);
				//剔除掉不需要显示的列信息
				
					DMBizAutomaticColumns dmBizAutomaticColumns=new DMBizAutomaticColumns();
					dmBizAutomaticColumns.setWorksheetId(workSheetId);
					dmBizAutomaticColumns.setWorksheetName("HAU_DM_B"+bizId+"C_IMPORT");
					dmBizAutomaticColumns.setWorksheetNameCh(worksheetName);
					dmBizAutomaticColumns.setColumnName(workSheetColumn.getColumnName());
					dmBizAutomaticColumns.setColumnNameCh(workSheetColumn.getColumnNameCh());
					listDmBizAutomaticColums.add(dmBizAutomaticColumns);
				
				
			}
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return listDmBizAutomaticColums;
	}
	//获取结果表列
	public boolean getResultColumns(
			List<DMBizAutomaticColumns> listDMBizAutomaticColumns, String bizId) {
		Connection dbConn = null;
		try {
			dbConn = this.getDbConnection();
			List<WorkSheetColumn> listColumns = new ArrayList<WorkSheetColumn>();
			//根据表名获取worksheetid
			String szWorkSheetName="HAU_DM_B"+bizId+"C_RESULT";
			String workSheetId = workSheetRepository.getWorksheetIdByName(szWorkSheetName);
			//根据worksheetid获取该表下所有列信息
			workSheet.getColumns(dbConn,workSheetId, listColumns);
			//将列绑定到列表中
			for (WorkSheetColumn workSheetColumn : listColumns) {
				//剔除掉不需要显示的列信息
				
					DMBizAutomaticColumns dmBizAutomaticColumns = new DMBizAutomaticColumns();
					dmBizAutomaticColumns.setWorksheetId(workSheetId);
					dmBizAutomaticColumns.setWorksheetName(szWorkSheetName);
					dmBizAutomaticColumns.setWorksheetNameCh(workSheetRepository.getWorkSheetNameCh(workSheetId));
					dmBizAutomaticColumns.setColumnName(workSheetColumn.getColumnName());
					dmBizAutomaticColumns.setColumnNameCh(workSheetColumn.getColumnNameCh());
					listDMBizAutomaticColumns.add(dmBizAutomaticColumns);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
		}
		return true;
	}
	//获取数据记录表待选列
	
	public boolean getPoolColumns(
			List<DMBizAutomaticColumns> listDMBizAutomaticColumns, String bizId) {
		Connection dbConn = null;
		try {
			dbConn = this.getDbConnection();
			List<WorkSheetColumn> listColumns = new ArrayList<WorkSheetColumn>();
			//根据表名获取worksheetid
			String szWorkSheetName="HAU_DM_B"+bizId+"C_POOL";
			String workSheetId = workSheetRepository.getWorksheetIdByName(szWorkSheetName);
			//根据worksheetid获取该表下所有列信息
			workSheet.getColumns(dbConn,workSheetId, listColumns);
			//将列绑定到列表中
			for (WorkSheetColumn workSheetColumn : listColumns) {
				//剔除掉不需要显示的列信息
				if (!workSheetColumn.getColumnName().equals("ID")&&!workSheetColumn.getColumnName().equals("IID")
						&&!workSheetColumn.getColumnName().equals("CID")&&!workSheetColumn.getColumnName().equals("MODIFYUSERID")&&!workSheetColumn.getColumnName().equals("MODIFYTIME")) {
					DMBizAutomaticColumns dmBizAutomaticColumns = new DMBizAutomaticColumns();
					dmBizAutomaticColumns.setWorksheetId(workSheetId);
					dmBizAutomaticColumns.setWorksheetName(szWorkSheetName);
					dmBizAutomaticColumns.setWorksheetNameCh(workSheetRepository.getWorkSheetNameCh(workSheetId));
					dmBizAutomaticColumns.setColumnName(workSheetColumn.getColumnName());
					dmBizAutomaticColumns.setColumnNameCh(workSheetColumn.getColumnNameCh());
					listDMBizAutomaticColumns.add(dmBizAutomaticColumns);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
		}
		return true;
	}
	
	//根据cid，iid获取客户信息
	public Map<String,String> dmGetBizCustomer(int bizId,String Cid,String IID,String columns)
	{
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Map<String,String> map=new HashMap<String, String>(); 
		JsonObject jsonObject=new JsonObject();
		try {
			dbConn =this.getDbConnection();
			//查询客户信息
			columns=columns.substring(0, columns.length()-1);
			String szSelectSql="select "+columns+" from HAU_DM_B"+bizId+"C_IMPORT where Cid='"+Cid+"' and IID='"+IID+"'";
			stmt = dbConn.prepareStatement(szSelectSql);
			rs = stmt.executeQuery();
			String[] column=columns.split(",");
			while(rs.next()){
				for(int i=0;i<column.length;i++)
				{
					map.put(column[i], rs.getString(column[i]));
					//jsonObject.addProperty(column[i], rs.getString(column[i]));
				}
			}
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		return map;
	}

	//根据cid查询前台所需信息
	public List<Map<String,String>> dmGetBizCustomerHis(int bizId,String Cid,String columns)
	{
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JsonObject jsonObject=new JsonObject();
		JsonArray jsonArray=new JsonArray();
		Map<String,String> map=new HashMap<String, String>(); 
		try {
			dbConn =this.getDbConnection();
			columns=columns.substring(0, columns.length()-1);
			//String szSelectSql="select "+columns+" from HAU_DM_B"+bizId+"C_IMPORT where Cid='"+Cid+"'";
			String szSelectSql="select "+columns+" from HAU_DM_B"+bizId+"C_IMPORT,HASYS_DM_B"+bizId+"C_PresetTime,HAU_DM_B"+bizId+"C_RESULT "
					+ " where HAU_DM_B"+bizId+"C_IMPORT.Cid=HAU_DM_B"+bizId+"C_RESULT.CID and HAU_DM_B"+bizId+"C_IMPORT.Cid=HASYS_DM_B"+bizId+"C_PresetTime.CID Cid='"+Cid+"'";
			stmt = dbConn.prepareStatement(szSelectSql);
			rs = stmt.executeQuery();
			String[] column=columns.split(",");
			while(rs.next()){
				JsonObject jsonObject_row=new JsonObject();
				for(int i=0;i<column.length;i++)
				{
					String columnString=column[i];
					String[] workColumn=columnString.split("\\.");
					map.put(column[i], rs.getString(workColumn[1]));
					
				}
				list.add(map);
				jsonArray.add(jsonObject_row);
			}
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseExecute(stmt);
		}
		jsonObject.add("rows", jsonArray);
		return list;
	}
	
	
	public boolean getPresetColumns(List<DMBizAutomaticColumns> listDMBizAutomaticColumns, String bizId) {
		Connection dbConn = null;
		try {
			dbConn = this.getDbConnection();
			List<WorkSheetColumn> listColumns = new ArrayList<WorkSheetColumn>();
			//拼接工作表名称
			String szWorkSheetName="HASYS_DM_B"+bizId+"C_PRESETTIME";
			//根据预约表名获取工作表id
			String workSheetId = workSheetRepository.getWorksheetIdByName(szWorkSheetName);
			//根据工作表id获取该工作表下面所有的列信息
			workSheet.getColumns(dbConn,workSheetId, listColumns);
			for (WorkSheetColumn workSheetColumn : listColumns) {
				//剔除掉不需要显示的列信息
				if (!workSheetColumn.getColumnName().equals("ID")
						&&!workSheetColumn.getColumnName().equals("SOURCEID")
						&&!workSheetColumn.getColumnName().equals("IID")
						&&!workSheetColumn.getColumnName().equals("CID")
						&&!workSheetColumn.getColumnName().equals("MODIFYID")
						&&!workSheetColumn.getColumnName().equals("MODIFYLAST")
						&&!workSheetColumn.getColumnName().equals("MODIFYUSERID")
						&&!workSheetColumn.getColumnName().equals("MODIFYTIME")) {
					DMBizAutomaticColumns dmBizAutomaticColumns = new DMBizAutomaticColumns();
					dmBizAutomaticColumns.setWorksheetName(szWorkSheetName);
					dmBizAutomaticColumns.setWorksheetNameCh(workSheetRepository.getWorkSheetNameCh(workSheetId));
					dmBizAutomaticColumns.setColumnName(workSheetColumn.getColumnName());
					dmBizAutomaticColumns.setColumnNameCh(workSheetColumn.getColumnNameCh());
					listDMBizAutomaticColumns.add(dmBizAutomaticColumns);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
		}
		return true;
	}
	//获取该业务下所有工作表列
	public List<DMBizAutomaticColumns> getAllBizColumns(String bizId,String type) {

		//获取导入表列信息
		List<DMBizAutomaticColumns> listDMBizAutomaticColumns=new ArrayList<DMBizAutomaticColumns>();
		List<DMBizAutomaticColumns> listImportColumns = this.dmGetBizCustomerColumns(Integer.parseInt(bizId));
		
		List<DMBizAutomaticColumns> listResultColumns = new ArrayList<DMBizAutomaticColumns>();
		
		//获取结果表列信息
		this.getResultColumns(listResultColumns,bizId);
		if (type.equals("分配")) {
			//循环获取导入表列
			for (DMBizAutomaticColumns dmBizAutomaticColumns : listImportColumns) {
				if (!dmBizAutomaticColumns.getColumnName().equals("ID")&&!dmBizAutomaticColumns.getColumnName().equals("MODIFYCLASS")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYID")) {
					listDMBizAutomaticColumns.add(dmBizAutomaticColumns);
				}
			}
			//循环获取结果表列
			for (DMBizAutomaticColumns dmBizAutomaticColumns : listResultColumns) {
				if (!dmBizAutomaticColumns.getColumnName().equals("IID")&&
						!dmBizAutomaticColumns.getColumnName().equals("CID")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYTIME")&&
						!dmBizAutomaticColumns.getColumnName().equals("ID")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYID")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYLAST")&&
						!dmBizAutomaticColumns.getColumnName().equals("ID")&&
						!dmBizAutomaticColumns.getColumnName().equals("SOURCEID")) {
					listDMBizAutomaticColumns.add(dmBizAutomaticColumns);
				}
			}
			
			List<DMBizAutomaticColumns> listPoolColumns = new ArrayList<DMBizAutomaticColumns>();
			//获取数据池记录表列信息
			this.getPoolColumns(listPoolColumns,bizId);
			listDMBizAutomaticColumns.addAll(listPoolColumns);
			
		}else if (type.equals("回收")) {
			//循环获取导入表列
			for (DMBizAutomaticColumns dmBizAutomaticColumns : listImportColumns) {
				if (!dmBizAutomaticColumns.getColumnName().equals("ID")&&!dmBizAutomaticColumns.getColumnName().equals("MODIFYCLASS")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYID")) {
					listDMBizAutomaticColumns.add(dmBizAutomaticColumns);
				}
			}
			//循环获取结果表列
			for (DMBizAutomaticColumns dmBizAutomaticColumns : listResultColumns) {
				if (!dmBizAutomaticColumns.getColumnName().equals("IID")&&
						!dmBizAutomaticColumns.getColumnName().equals("CID")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYTIME")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYID")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYLAST")&&
						!dmBizAutomaticColumns.getColumnName().equals("ID")&&
						!dmBizAutomaticColumns.getColumnName().equals("SOURCEID")) {
					listDMBizAutomaticColumns.add(dmBizAutomaticColumns);
				}
			}
			
			List<DMBizAutomaticColumns> listPoolColumns = new ArrayList<DMBizAutomaticColumns>();
			//获取数据池记录表列信息
			this.getPoolColumns(listPoolColumns,bizId);
			listDMBizAutomaticColumns.addAll(listPoolColumns);
			//获取预约表列信息
			List<DMBizAutomaticColumns> listPresetColumns = new ArrayList<DMBizAutomaticColumns>();
			
			this.getPresetColumns(listPresetColumns,bizId);
			listDMBizAutomaticColumns.addAll(listPresetColumns);
		} 
			else if(type.equals("导出")){
			//循环获取导入表列
			for (DMBizAutomaticColumns dmBizAutomaticColumns : listImportColumns) {
				if (!dmBizAutomaticColumns.getColumnName().equals("ID")&&!dmBizAutomaticColumns.getColumnName().equals("MODIFYCLASS")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYID")&&!dmBizAutomaticColumns.getColumnName().equals("MODIFYUSERID")) {
					listDMBizAutomaticColumns.add(dmBizAutomaticColumns);
				}
			}
			//循环获取结果表列
			for (DMBizAutomaticColumns dmBizAutomaticColumns : listResultColumns) {
				if (!dmBizAutomaticColumns.getColumnName().equals("IID")&&
						!dmBizAutomaticColumns.getColumnName().equals("CID")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYTIME")&&
						!dmBizAutomaticColumns.getColumnName().equals("ID")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYID")&&
						!dmBizAutomaticColumns.getColumnName().equals("MODIFYLAST")) {
					listDMBizAutomaticColumns.add(dmBizAutomaticColumns);
				}
			}
			
			
			List<DMBizAutomaticColumns> listPresetColumns = new ArrayList<DMBizAutomaticColumns>();
			//获取预约表列信息
			this.getPresetColumns(listPresetColumns,bizId);
			listDMBizAutomaticColumns.addAll(listPresetColumns);
		}
		
		
		
		return listDMBizAutomaticColumns;
	}
	
	//获取待选表对象
	public void worksheetAddList(List<WorkSheet> workSheet,String worksheetName){
		WorkSheet WorkSheet = new WorkSheet();
		WorkSheet.setWorksheetName(worksheetName);
		WorkSheet.setWorksheetId(workSheetRepository.getWorksheetIdByName(worksheetName));
		WorkSheet.setWorksheetNameCh(workSheetRepository.getWorkSheetNameCh(WorkSheet.getWorksheetId()));
		workSheet.add(WorkSheet);
	}
	
	//根据业务号获取url
			public Map<String,String> dmGetAutomaticPageUrl(int sourceId,String sourceModular,String pageName) {
				Map<String,String> map=new HashMap<String, String>(); 
				Connection dbConn = null;
				String url="";
				PreparedStatement stmt = null;
				ResultSet rs = null;	
				try {
					dbConn = this.getDbConnection();
					String szSql =String.format("select pageurl,PAGEPARAMETER from HASYS_DM_PAGE_MAP_PER where SOURCEID=%s and SOURCEMODULAR='%s' and PageName='%s'", sourceId,sourceModular,pageName) ;
					stmt = dbConn.prepareStatement(szSql);
					rs = stmt.executeQuery();
					if (rs.next()) {
						map.put("pageUrl", rs.getString(1));
						map.put("pageParameter", rs.getString(2));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					DbUtil.DbCloseConnection(dbConn);
					DbUtil.DbCloseQuery(rs, stmt);
				}
				return map;
			}
	
		//根据业务号获取url
				public boolean dmCreateAutomaticPageUrl(int bizId,String pageName,String pageUrl,String pageParameter,String sourceModular) {
					 
					Connection dbConn = null;
					String url="";
					PreparedStatement stmt = null;	
					ResultSet rs = null;	
					try {
						dbConn = this.getDbConnection();
						dbConn = this.getDbConnection();
						String selectSql =String.format("select count(*) from HASYS_DM_PAGE_MAP_PER where SourceID=%s and SOURCEMODULAR='%s' and PAGENAME='%s' ",bizId,sourceModular,pageName) ;
						
						stmt = dbConn.prepareStatement(selectSql);
						rs = stmt.executeQuery();
						int ishas=0;
						while (rs.next()) {
							ishas = rs.getInt(1);
						}
						String szSql="";
						if(ishas>0)
						{
							szSql=String.format("update HASYS_DM_PAGE_MAP_PER set PAGEURL='"+pageUrl+"' where SourceID=%s and SOURCEMODULAR='%s' and PAGENAME='%s' ",bizId,sourceModular,pageName) ;
						}else{
							szSql =String.format("insert into HASYS_DM_PAGE_MAP_PER(ID,SOURCEID,PAGENAME,PAGEURL,PAGEPARAMETER,sourceModular) VALUES(S_HASYS_DM_PAGE_MAP_PER.nextval,%s,'%s','%s','%s','%s') ", bizId,pageName,pageUrl,pageParameter,sourceModular);
						}
						stmt = dbConn.prepareStatement(szSql);
						stmt.execute();
						
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					} finally{
						DbUtil.DbCloseConnection(dbConn);
						DbUtil.DbCloseQuery(rs, stmt);
					}
					return true;
				}
				//创建自动生成页面配置信息
				public boolean dmCreateAutomaticConfig(int sourceId,String sourceModular,String pageName,String panleName,JsonArray jsonArray,String state,String displayType,String userId,int isDelete,String pageId) {
					 
					Connection dbConn = null;
					
					PreparedStatement stmt = null;	
					ResultSet rs = null;	
					try {
						dbConn = this.getDbConnection();
						String selectSql =String.format("select count(*) from HASYS_DM_AUTOMATICCONFIG where SourceID=%s and SOURCEMODULAR='%s' and PAGENAME='%s' and PANLENAME='%s'",sourceId,sourceModular,pageName,panleName) ;
						
						stmt = dbConn.prepareStatement(selectSql);
						rs = stmt.executeQuery();
						int ishas=0;
						while (rs.next()) {
							ishas = rs.getInt(1);
						}
						String szSql="";
						if(ishas>0)
						{
							szSql=String.format("update HASYS_DM_AUTOMATICCONFIG set Config='"+jsonArray.toString()+"' where SourceID=%s and SOURCEMODULAR='%s' and PAGENAME='%s' and PANLENAME='%s'",sourceId,sourceModular,pageName,panleName) ;
							PreparedStatement stat=dbConn.prepareStatement("update HASYS_DM_AUTOMATICCONFIG set Config=? where SourceID="+sourceId+" and SOURCEMODULAR='"+sourceModular+"' and PAGENAME='"+pageName+"' and PANLENAME='"+panleName+"'");
							 String clobContent = jsonArray.toString();  
						     StringReader reader = new StringReader(clobContent);  
						     stat.setCharacterStream(1, reader, clobContent.length());
						     stat.executeUpdate();
						}else{
							PreparedStatement stat=dbConn.prepareStatement("insert into HASYS_DM_AUTOMATICCONFIG(ID,SourceID,SourceModular,PageName,PanleName,Config,CREATER,STATE,ISDELETE,DISPLAYTYPE,PAGEID,CREATETIME) "
									+ "values(S_HASYS_DM_AUTOMATICCONFIG.nextval,'"+sourceId+"','"+sourceModular+"','"+pageName+"','"+panleName+"',?,'"+userId+"','"+state+"',"+isDelete+",'"+displayType+"','"+pageId+"',TO_DATE('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ "','yyyy-mm-dd hh24:mi:ss'))");
							 String clobContent = jsonArray.toString();  
						     StringReader reader = new StringReader(clobContent);  
						     stat.setCharacterStream(1, reader, clobContent.length());
						     stat.executeUpdate();
						}
						
						/*stmt = dbConn.prepareStatement(szSql);
						
						dbConn.commit();*/
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					} finally{
						DbUtil.DbCloseConnection(dbConn);
						DbUtil.DbCloseQuery(rs, stmt);
					}
					return true;
				}
				
				
				//根据业务号获取面板配置信息
				public List<DMBizAutomaticConfig> dmGetAutomaticConfig(int sourceId,String sourceModular,String pageName) {
					List<DMBizAutomaticConfig> listDMBizAutomaticConfig=new ArrayList<DMBizAutomaticConfig>();
					Connection dbConn = null;
					String url="";
					PreparedStatement stmt = null;	
					ResultSet rs = null;	
					try {
						dbConn = this.getDbConnection();
						String szSql =String.format("select ID,SourceID,SOURCEMODULAR,PAGENAME,PANLENAME,CONFIG,CREATER,STATE,ISDELETE,DISPLAYTYPE,PAGEID,CREATETIME from HASYS_DM_AUTOMATICCONFIG where SourceID=%s and SOURCEMODULAR='%s' and PAGENAME='%s'",sourceId,sourceModular,pageName) ;
						
						stmt = dbConn.prepareStatement(szSql);
						rs = stmt.executeQuery();
						while (rs.next()) {
							DMBizAutomaticConfig dmBizAutomaticConfig=new DMBizAutomaticConfig();
							dmBizAutomaticConfig.setId(rs.getString(1));
							dmBizAutomaticConfig.setSourceId(rs.getString(2));
							dmBizAutomaticConfig.setSourceModulear(rs.getString(3));
							dmBizAutomaticConfig.setPageName(rs.getString(4));
							dmBizAutomaticConfig.setPanleName(rs.getString(5));
							dmBizAutomaticConfig.setConfig(rs.getString(6));
							dmBizAutomaticConfig.setCreater(rs.getString(7));
							dmBizAutomaticConfig.setState(rs.getString(8));
							dmBizAutomaticConfig.setIsDelete(rs.getInt(9));
							dmBizAutomaticConfig.setDisplayType(rs.getString(10));
							dmBizAutomaticConfig.setPageId(rs.getString(11));
							dmBizAutomaticConfig.setCreateTime(rs.getString(12));
							listDMBizAutomaticConfig.add(dmBizAutomaticConfig);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						DbUtil.DbCloseConnection(dbConn);
						DbUtil.DbCloseQuery(rs, stmt);
					}
					return listDMBizAutomaticConfig;
				}
				
				//
				public   boolean dmSubmitResult(DMBizImportTemplate dmBizImportTemplate,StringBuffer errMessage){
					PreparedStatement stmt = null;
					ResultSet rs = null;
					Connection dbConn = null;
					try {
						dbConn =this.getDbConnection();
						try {
							
							int count = 0;
							String szSql = String.format("select COUNT(*) from HASYS_DM_BIZTEMPLATEIMPORT where BUSINESSID='%s' and TemplateID='%s'",dmBizImportTemplate.getBizId(),dmBizImportTemplate.getTemplateId());
							stmt = dbConn.prepareStatement(szSql);
							rs = stmt.executeQuery();
							if (rs.next()) {
								count = rs.getInt(1);
							}
							if (count > 0) {
								errMessage.append("模板ID冲突！");
								return false;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							
							int count = 0;
							 String szSql = String.format("select COUNT(*) from HASYS_DM_BIZTEMPLATEIMPORT where BUSINESSID='%s' and NAME='%s'",dmBizImportTemplate.getBizId(),dmBizImportTemplate.getTemplateName());
							stmt = dbConn.prepareStatement(szSql);
							rs = stmt.executeQuery();
							if (rs.next()) {
								count = rs.getInt(1);
							}
							if (count > 0) {
								errMessage.append("模板名称冲突！");
								return false;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						String szSql = "INSERT INTO HASYS_DM_BIZTEMPLATEIMPORT (ID,TemplateID,BusinessId,Name,Description,IsDefault,SourceType,Xml) VALUES(S_HASYS_DM_BIZTEMPLATEIMPORT.nextval,?,?,?,?,?,?,'') ";
						stmt = dbConn.prepareStatement(szSql);
						
						stmt.execute();

					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						DbUtil.DbCloseConnection(dbConn);
						DbUtil.DbCloseExecute(stmt);
					}
					return true;
				}
}
