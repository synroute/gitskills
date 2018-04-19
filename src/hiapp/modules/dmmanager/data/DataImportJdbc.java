package hiapp.modules.dmmanager.data;

import hiapp.modules.dmmanager.bean.Business;
import hiapp.modules.dmmanager.bean.ExcelUtils;
import hiapp.modules.dmmanager.bean.ImportTemplate;
import hiapp.modules.dmmanager.bean.WorkSheet;
import hiapp.modules.dmmanager.bean.WorkSheetColumn;
import hiapp.modules.dmsetting.DMWorkSheetTypeEnum;
import hiapp.modules.dmsetting.data.DmWorkSheetRepository;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.idfactory.IdFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
@Repository
public class DataImportJdbc extends BaseRepository{
	@Autowired
	private IdFactory idfactory;
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
	
	/**
	 * 获取所有业务
	 * @param userId
	 * @return
	 * @throws IOException 
	 */
	public List<Business> getBusinessData(int pemissId,Integer bizid) throws IOException{
		List<Business> businessList=new ArrayList<Business>();
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn= this.getDbConnection();
			String getOrgnizeSql="";
			if(bizid==-1)
			{
				getOrgnizeSql="select distinct b.businessId,b.name,b.DESCRIPTION,b.OWNERGROUPID,b.outboundmddeId from HASYS_DM_PER_MAP_POOL a,HASYS_DM_Business b  where a.businessid=b.businessid and a.permissionid=? ";
				pst=conn.prepareStatement(getOrgnizeSql);
				pst.setInt(1,pemissId);
				rs = pst.executeQuery();
				while (rs.next()) {
					Business bus=new Business();
					bus.setId(rs.getInt(1));
					bus.setName(rs.getString(2));
					bus.setDescription(rs.getString(3));
					bus.setOwnergroupId(rs.getString(4));
					bus.setOutboundmddeId(rs.getInt(5));
					businessList.add(bus);
				}
			}else {
				getOrgnizeSql="select distinct b.businessId,b.name,b.DESCRIPTION,b.OWNERGROUPID,b.outboundmddeId from HASYS_DM_PER_MAP_POOL a,HASYS_DM_Business b  where a.businessid=b.businessid and a.permissionid=? and a.itemname ='数据管理' and b.businessid="+bizid+"";
				pst=conn.prepareStatement(getOrgnizeSql);
				pst.setInt(1,pemissId);
				rs = pst.executeQuery();
				while (rs.next()) {
					Business bus=new Business();
					bus.setId(rs.getInt(1));
					bus.setName(rs.getString(2));
					bus.setDescription(rs.getString(3));
					bus.setOwnergroupId(rs.getString(4));
					bus.setOutboundmddeId(rs.getInt(5));
					businessList.add(bus);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return businessList;
	}
	/**
	 * 获取所有导入模板
	 * @param businessId
	 * @return
	 */
	public List<ImportTemplate> getAllTemplates(Integer businessId){
		List<ImportTemplate> temList=new ArrayList<ImportTemplate>();
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn= this.getDbConnection();
			String sql="select id,TEMPLATEID,BUSINESSID,NAME,DESCRIPTION,ISDEFAULT,SOURCETYPE,XML from HASYS_DM_BIZTEMPLATEIMPORT where BUSINESSID=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,businessId);
			rs = pst.executeQuery();
			while (rs.next()) {
				ImportTemplate temPlate=new ImportTemplate();
				temPlate.setId(rs.getInt(1));
				temPlate.setTemPlateId(rs.getInt(2));
				temPlate.setBussinesID(rs.getInt(3));
				temPlate.setName(rs.getString(4));
				temPlate.setDescription(rs.getString(5));
				temPlate.setIsDefault(rs.getInt(6));
				temPlate.setSourceType(rs.getString(7));
				temPlate.setXml(rs.getString(8));
				temList.add(temPlate);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return temList;
	}
	/**
	 * 获取workSheetId
	 * @param bizId
	 * @return
	 */
	public String getWookSeetId(Integer bizId){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String wookSheetId=null;
		String name="HAU_DM_B"+bizId+"C_IMPORT";
		try {
			conn= this.getDbConnection();
			String sql="select id from hasys_worksheet  where name=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, name);
			rs = pst.executeQuery();
			
			while(rs.next()){
				wookSheetId=rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return wookSheetId;
	}
	/**
	 * 根据WORKSHEETID获取表信息
	 * @param workSheetId
	 * @return
	 */
	public WorkSheet getWorkSheet(String workSheetId){
		WorkSheet workSheet=new WorkSheet();
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn= this.getDbConnection();
			String sql="SELECT id,name,namech,description,isowner FROM HASYS_WORKSHEET where id=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, workSheetId);
			rs = pst.executeQuery();
			while(rs.next()){
				workSheet.setId(rs.getString(1));
				workSheet.setName(rs.getString(2));
				workSheet.setNameCh(rs.getString(3));
				workSheet.setDescription(rs.getString(4));
				workSheet.setIsOwner(rs.getInt(5));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return workSheet;
	}
	/**
	 * 获取导入表字段
	 * @param workSheetId
	 * @return
	 */
	public List<WorkSheetColumn> getWorkSeetColumnList(String workSheetId){
		 List<WorkSheetColumn>  columnList=new ArrayList<WorkSheetColumn>();
		 Connection conn=null;
		 PreparedStatement pst = null;
		 ResultSet rs = null;
		 try {
			conn= this.getDbConnection();
			String sql="SELECT id,ColumnName,ColumnNameCh,ColumnDescription,DataType,Length,DictionaryName,DictionaryLevel,IsSysColumn,IsIdentitySquence,workSheetId FROM HASYS_WORKSHEETCOLUMN	 WHERE WorkSheetId=? ORDER BY ID";
			pst=conn.prepareStatement(sql);
			pst.setString(1, workSheetId);
			rs = pst.executeQuery();
			while(rs.next()){
				WorkSheetColumn sheetColumn=new WorkSheetColumn();
				sheetColumn.setId(rs.getInt(1));
				sheetColumn.setField(rs.getString(2));
				sheetColumn.setTitle(rs.getString(3));
				sheetColumn.setDescription(rs.getString(4));
				sheetColumn.setDataType(rs.getString(5));
				sheetColumn.setLength(rs.getInt(6));
				sheetColumn.setDicName(rs.getString(7));
				sheetColumn.setDicLevel(rs.getInt(8));
				sheetColumn.setIsSysColumn(rs.getInt(9));
				sheetColumn.setIsIdentitySquence(rs.getInt(10));
				sheetColumn.setWorkSheetId(rs.getString(11));
				columnList.add(sheetColumn);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		 return columnList;
	}
	
	
	public Map<String,Object> getExcelData(Integer temPlateId,Integer bizId) throws IOException{
		Map<String,String> map1=new HashMap<String, String>();
		Map<String,Object> map=new HashMap<String, Object>();
		List<String> excelList=new ArrayList<String>();		
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String jsonData=null;
		try {
			conn= this.getDbConnection();
			String getXmlSql="select xml from HASYS_DM_BIZTEMPLATEIMPORT where TEMPLATEID=? and BUSINESSID=?";
			pst=conn.prepareStatement(getXmlSql);
			pst.setInt(1, temPlateId);
			pst.setInt(2,bizId);
			rs = pst.executeQuery();
			while(rs.next()){
				jsonData=ClobToString(rs.getClob(1));	
			}
			JsonObject jsonObject= new JsonParser().parse(jsonData).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			for (int i = 0; i < dataArray.size(); i++) {
				String key=dataArray.get(i).getAsJsonObject().get("DbFieldName").getAsString();
				String value=dataArray.get(i).getAsJsonObject().get("ExcelHeader").getAsString();
				if(value!=null&&!"".equals(value)){
					map1.put(key,value );
					excelList.add(value);

				}
			}
			
			map.put("exMap", map1);
			map.put("exList",excelList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 获取要导入的数据
	 * @param temPlateId
	 * @param bizId
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked","rawtypes" })
	public Map<String,Object> getDbData(Integer temPlateId,Integer bizId,Integer num,Integer pageSize) throws IOException{
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String jsonData=null;
		String getDbDataSql="select ";
		String getDbDataSql1="select ";
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
		List<String> sourceColumns=new ArrayList<String>();
		Map<String,Object> resultMap=new HashMap<String, Object>();
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		try {
			conn= this.getDbConnection();
			String getXmlSql="select xml from HASYS_DM_BIZTEMPLATEIMPORT where TEMPLATEID=? and BUSINESSID=?";
			pst=conn.prepareStatement(getXmlSql);
			pst.setInt(1, temPlateId);
			pst.setInt(2,bizId);
			rs = pst.executeQuery();
			while(rs.next()){
				jsonData=ClobToString(rs.getClob(1));	
			}
			DbUtil.DbCloseQuery(rs, pst);
			
			JsonObject jsonObject= new JsonParser().parse(jsonData).getAsJsonObject();
			JsonArray excelTemplateArray=jsonObject.get("ImportExcelTemplate").getAsJsonArray();
			JsonObject excelTemplate=excelTemplateArray.get(0).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			String sourceTableName=excelTemplate.get("SourceTableName").getAsString();
			for (int i = 0; i < dataArray.size(); i++) {
				String value=dataArray.get(i).getAsJsonObject().get("FieldNameSource").getAsString();
				if(value!=null&&!"".equals(value)){
					sourceColumns.add(value);
				}
			 
			}
			List<String> newList=new ArrayList<String>(new HashSet(sourceColumns));
			for (int i = 0; i < newList.size(); i++) {
				getDbDataSql+=newList.get(i)+",";
				getDbDataSql1+=newList.get(i)+",";
			}
			getDbDataSql=getDbDataSql.substring(0, getDbDataSql.length()-1)+" from ("+getDbDataSql1+"rownum rn from "+sourceTableName+" where rownum<?) a where rn>=?";
			pst=conn.prepareStatement(getDbDataSql);
			pst.setInt(1, endNum);
			pst.setInt(2,startNum);
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				for (int i = 0; i < dataArray.size(); i++) {
					String sourceName=dataArray.get(i).getAsJsonObject().get("FieldNameSource").getAsString();
					String key=dataArray.get(i).getAsJsonObject().get("FieldName").getAsString();
					for (int j= 0; j < newList.size(); j++) {
						if(sourceName.equals(newList.get(j))){
							map.put(key,rs.getObject(j+1));
						}
						
					}
				}
			
				
				dataList.add(map);
			}
			String  countSql="select count(*) from "+sourceTableName;
			pst=conn.prepareStatement(countSql);
			rs=pst.executeQuery();
			Integer total=null;
			while(rs.next()){
				total=rs.getInt(1);
			}
			
			resultMap.put("rows",dataList);
			resultMap.put("total",total);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	/**
	 * 获取要导入的数据
	 * @param temPlateId
	 * @param bizId
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({"unchecked","rawtypes" })
	public List<Map<String,Object>> insertDataByDb(Integer templateId,Integer bizId,String contextPath,String userId,String operationName) throws IOException{
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String jsonData=null;
		String getDbDataSql1="select ";
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
		List<String> sourceColumns=new ArrayList<String>();
		String workSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());;
		String tableName="HAU_DM_B"+bizId+"C_IMPORT";
		String importBatchId=idfactory.newId("DM_IID");//饶茹批次号
		String disBatchId=idfactory.newId("DM_DID");//分配号
		try {
			conn= this.getDbConnection();
			jsonData=getJsonData(bizId, templateId);
			JsonObject jsonObject= new JsonParser().parse(jsonData).getAsJsonObject();
			JsonArray excelTemplateArray=jsonObject.get("ImportExcelTemplate").getAsJsonArray();
			JsonObject excelTemplate=excelTemplateArray.get(0).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			JsonObject importConfig= jsonObject.get("ImportConfig").getAsJsonObject();
			String icrement=importConfig.get("IncrementalIdentifier").getAsString();
			String sourceTableName=excelTemplate.get("SourceTableName").getAsString();
			String importTime = getImportTime(bizId, templateId);
			for (int i = 0; i < dataArray.size(); i++) {
				String value=dataArray.get(i).getAsJsonObject().get("FieldNameSource").getAsString();
				if(value!=null&&!"".equals(value)){
					sourceColumns.add(value);
				}
			 
			}
			List<String> newList=new ArrayList<String>(new HashSet(sourceColumns));
			for (int i = 0; i < newList.size(); i++) {
				getDbDataSql1+=newList.get(i)+",";
			}
			getDbDataSql1=getDbDataSql1.substring(0, getDbDataSql1.length()-1)+" from "+sourceTableName;
			if(importTime!=null){
				getDbDataSql1+=" where "+icrement+">to_date('"+importTime+"','yyyy-mm-dd hh24:mi:ss')";
			}
			pst=conn.prepareStatement(getDbDataSql1);
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				for (int i = 0; i < dataArray.size(); i++) {
					String sourceName=dataArray.get(i).getAsJsonObject().get("FieldNameSource").getAsString();
					String key=dataArray.get(i).getAsJsonObject().get("FieldName").getAsString();
					for (int j= 0; j < newList.size(); j++) {
						if(sourceName.equals(newList.get(j))){
							map.put(key,rs.getObject(j+1));
						}
						
					}
				}
			
				
				dataList.add(map);
			}
			DbUtil.DbCloseQuery(rs,pst);
			String sql="select to_char(max(t."+icrement+"),'yyyy-mm-dd hh24:mi:ss') importTime from "+sourceTableName+" t";
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			String maxTime=null;
			while(rs.next()){
				maxTime=rs.getString(1);
			}
			DbUtil.DbCloseQuery(rs,pst);
			updateImportTime(bizId, templateId, importTime, maxTime);
			String getDataSourceSql="select a.id from HASYS_DM_DATAPOOL a where a.BusinessID=? and a.DataPoolType =1";
			pst=conn.prepareStatement(getDataSourceSql);
			pst.setInt(1,bizId);
			rs=pst.executeQuery();
			Integer dataPoolNumber=null;
			while(rs.next()){
				dataPoolNumber=rs.getInt(1);
			}
			//保存数据
			insertDbData(bizId,jsonData, workSheetId, tableName, dataList,userId,importBatchId,dataPoolNumber, operationName,disBatchId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return dataList;
	}
	/**
	 * 保存导入数据
	 * @param tempId
	 * @param bizId
	 * @param sheetColumnList
	 * @param isnertData
	 * @param tableName
	 * @param userId
	 * @throws IOException
	 */
	@SuppressWarnings({ "unused" })
	public Map<String,Object> insertImportData(Integer tempId,Integer bizId,String workSheetId,List<Map<String,Object>> isnertData,String tableName,String userId,String operationName) throws IOException{
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String jsonData=null;
		Statement statement=null;
		Map<String,Object> resultMap=null;
		List<String> stringList=new ArrayList<String>();
		try {
			conn= this.getDbConnection();
			jsonData=getJsonData(bizId, tempId);
			String importBatchId=idfactory.newId("DM_IID");//饶茹批次号
			String disBatchId=idfactory.newId("DM_DID");//分配号
			String getDataSourceSql="select a.id from HASYS_DM_DATAPOOL a where a.BusinessID=? and a.DataPoolType =1";
			pst=conn.prepareStatement(getDataSourceSql);
			pst.setInt(1,bizId);
			rs=pst.executeQuery();
			Integer dataPoolNumber=null;
			while(rs.next()){
				dataPoolNumber=rs.getInt(1);
			}
			resultMap=insertExcelData(jsonData,workSheetId,tableName,bizId,userId,importBatchId,dataPoolNumber,operationName,disBatchId);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	

    /**
     * 导入Excel数据
     * @param jsonData
     * @param workSheetId
     * @param tableName
     * @param isnertData
     * @param bizId
     * @param uId
     */
	public Map<String,Object> insertExcelData(String jsonData,String workSheetId,String tableName,Integer bizId,String userId,String importBatchId,Integer dataPoolNumber,String operationName,String disBatchId){
    	Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Object> resultMap=new HashMap<String, Object>();//返回结果集 
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		String orePoolName="HAU_DM_B"+bizId+"C_POOL_ORE";
		String tempTableName="HAU_DM_B"+bizId+"C_IMPORT_"+userId;
		String resultTableName="HAU_DM_B"+bizId+"C_Result";//结果表
		List<Map<String,String>> tableHeaders=new ArrayList<Map<String,String>>();
 		try {
			conn=this.getDbConnection();
	    	//解析JSON RepetitionExcludeType
			JsonObject jsonObject= new JsonParser().parse(jsonData).getAsJsonObject();
			JsonArray excelTemplateArray=jsonObject.get("ImportExcelTemplate").getAsJsonArray();
			JsonObject excelTemplate=excelTemplateArray.get(0).getAsJsonObject();
			String repetitionExcludeType=excelTemplate.get("RepetitionExcludeType").getAsString();
			String RepetitionColumn=excelTemplate.get("RepetitionExcludeWorkSheetColumn").getAsString();
		    Integer RepetitionCount=Integer.valueOf(excelTemplate.get("RepetitionExcludeDayCount").getAsString());
		    String dintincColumn="";
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			String distinctSql=null;
			//查询数据
			if("根据导入时间排重".equals(repetitionExcludeType)){
				distinctSql="select "+RepetitionColumn+" from "+tableName+" where modifytime <sysdate and modifytime>sysdate-"+RepetitionCount;
			}else{
				distinctSql="select a."+RepetitionColumn+" from "+tableName+" a,"+resultTableName+" b where a.iid=b.iid and a.cid=b.cid and b.DialTime <sysdate and b.DialTime>sysdate-"+RepetitionCount;
			}
			String sql=" select "+RepetitionColumn+" from "+tempTableName+" where ifchecked=1 and "+RepetitionColumn+" in("+distinctSql+")";
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			resultMap.put("ifRepeat",0);
			while(rs.next()){
				resultMap.put("ifRepeat",1);
				break;
			}
			DbUtil.DbCloseQuery(rs,pst);
			if(!"ID".equals(RepetitionColumn.toUpperCase())){
		    	dintincColumn=" and "+RepetitionColumn +" not in("+distinctSql+")";
		    	for (int i = 0; i < dataArray.size(); i++) {
					String cName=dataArray.get(i).getAsJsonObject().get("DbFieldName").getAsString();
					String excelName=dataArray.get(i).getAsJsonObject().get("ExcelHeader").getAsString();
					if(excelName!=null&&!"".equals(excelName)){
						Map<String,String> map=new HashMap<String, String>();
						map.put("field", cName);
						map.put("title",excelName);
						tableHeaders.add(map);
					}
				}
		    	
		    }
			
			//不自动提交数据
			conn.setAutoCommit(false);
			//导入表里面添加数据
			String insertImportDataSql="insert into "+tableName+"(ID,IID,CID,modifylast,modifyid,modifyuserid,modifytime,";
			String selectSql="select S_"+tableName+".nextval,'"+importBatchId+"',CUSTOMERID,1,0,'"+userId+"',sysdate,";
			//数据池记录表里面插数据
			String isnertDataPoolSql="insert into "+poolName+"(ID,SourceID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime) "
									+" select S_"+poolName+".nextval,'"+disBatchId+"','"+importBatchId+"',CUSTOMERID,'"+operationName+"',"+dataPoolNumber+","+dataPoolNumber+",0,0,0,'"+userId+"',sysdate from "+tempTableName+" where ifchecked=1"+dintincColumn;
			pst=conn.prepareStatement(isnertDataPoolSql);
			pst.execute();
			DbUtil.DbCloseExecute(pst);
			//数据池操作记录表里面插数据
			String dataPoolOperationSql="insert into "+orePoolName+"(ID,SourceID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime)"
										+" select S_"+orePoolName+".nextval,'"+disBatchId+"','"+importBatchId+"',CUSTOMERID,'"+operationName+"',"+dataPoolNumber+","+dataPoolNumber+",0,0,0,'"+userId+"',sysdate from "+tempTableName+" where ifchecked=1"+dintincColumn;
			pst=conn.prepareStatement(dataPoolOperationSql);
			pst.execute();
			DbUtil.DbCloseExecute(pst);
			for (int k = 0; k < dataArray.size(); k++) {
				String excelHeader=dataArray.get(k).getAsJsonObject().get("ExcelHeader").getAsString();
				if(excelHeader!=null&&!"".equals(excelHeader)){
					insertImportDataSql+=dataArray.get(k).getAsJsonObject().get("DbFieldName").getAsString()+",";
					selectSql+=dataArray.get(k).getAsJsonObject().get("DbFieldName").getAsString()+",";
				}
			}
			selectSql=selectSql.substring(0,selectSql.length()-1)+" from "+tempTableName+"  where ifchecked=1"+dintincColumn;
			insertImportDataSql=insertImportDataSql.substring(0,insertImportDataSql.length()-1)+") "+selectSql;
			pst=conn.prepareStatement(insertImportDataSql);
			pst.execute();
			DbUtil.DbCloseExecute(pst);

			//从临时表中删除数据
			String deleteTempSql="delete from "+tempTableName+" b where ifChecked=1"+dintincColumn;	
			pst=conn.prepareStatement(deleteTempSql);	
			pst.execute();
			DbUtil.DbCloseExecute(pst);

			//导入批次表里面插数据
			String insertImportBatchSql="insert into HASYS_DM_IID(id,iid,BusinessId,ImportTime,UserID,Name,Description,ImportType) values(SEQ_HASYS_DM_IID.nextval,?,?,sysdate,?,?,?,?)";
			pst=conn.prepareStatement(insertImportBatchSql);
			pst.setString(1, importBatchId);
			pst.setInt(2, bizId);
			pst.setString(3, userId);
			pst.setString(4, "导入批次");
			pst.setString(5, "导入批次");
			pst.setString(6, operationName);
			pst.execute();
			DbUtil.DbCloseExecute(pst);
			String insertDisBatchSql="insert into HASYS_DM_DID(id,BusinessID,DID,DistributionName,ModifyUserID,ModifyTime,Description) values(S_HASYS_DM_DID.nextval,?,?,?,?,sysdate,?)";
			pst=conn.prepareStatement(insertDisBatchSql);
			pst.setInt(1,bizId);
			pst.setString(2, disBatchId);
			pst.setString(3,"分配批次");
			pst.setString(4,userId);
			pst.setString(5,"分配批次");
			pst.execute();
			//提交
			conn.commit();
			resultMap.put("tableHeaders", tableHeaders);
			resultMap.put("flag", 1);
			resultMap.put("result",true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("result",false);
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
    	return resultMap;
    }
    
	public Map<String,Object>  getRepeatData(Integer tempId,Integer bizId,String workSheetId,String userId,Integer num,Integer pageSize){
    	Connection conn=null;
    	PreparedStatement pst=null;
    	ResultSet rs=null;
    	String tempTableName="HAU_DM_B"+bizId+"C_IMPORT_"+userId;
    	Map<String,String> dataTypeList=new HashMap<String,String>();
		List<Map<String,Object>> repeatColumns=new ArrayList<Map<String,Object>>();//重复字段的集合
		Map<String,Object> resultMap=new HashMap<String, Object>();
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
     	try {
			conn=this.getDbConnection();
			String jsonData=getJsonData(bizId, tempId);
			JsonObject jsonObject= new JsonParser().parse(jsonData).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			JsonArray excelTemplateArray=jsonObject.get("ImportExcelTemplate").getAsJsonArray();
			JsonObject excelTemplate=excelTemplateArray.get(0).getAsJsonObject();
			String RepetitionColumn=excelTemplate.get("RepetitionExcludeWorkSheetColumn").getAsString();
			if(!"ID".equals(RepetitionColumn.toUpperCase())){
				getDataType(dataTypeList, dataArray, workSheetId, 1);
				String selectRepColumnSql1="select ";
				String selectRepColumnSql2="select ";
				String selectRepColumnSql="select ";
				for (int i = 0; i < dataArray.size(); i++) {
					String cName=dataArray.get(i).getAsJsonObject().get("DbFieldName").getAsString();
					if(dataTypeList.keySet().contains(cName)){
						String type=dataTypeList.get(cName);
						if("datetime".equals(type.toLowerCase())){
							selectRepColumnSql+="to_char("+cName+",'yyyy-mm-dd') "+cName+",";
						}else{
							selectRepColumnSql+=cName+",";
						}
						selectRepColumnSql1+=cName+",";
						selectRepColumnSql2+=cName+",";
					}
					
				}
				selectRepColumnSql=selectRepColumnSql.substring(0,selectRepColumnSql.length()-1)+" from "+tempTableName+" where ifchecked=1";
				selectRepColumnSql1=selectRepColumnSql1+"rownum rn from ("+selectRepColumnSql+") where rownum<?";
				selectRepColumnSql2=selectRepColumnSql2.substring(0,selectRepColumnSql2.length()-1)+" from ("+selectRepColumnSql1+") where rn>=?";
				pst=conn.prepareStatement(selectRepColumnSql2);
				pst.setInt(1,endNum);
				pst.setInt(2,startNum);
				rs=pst.executeQuery();
				while(rs.next()){
					Map<String,Object> map=new HashMap<String, Object>();
					 for(Map.Entry<String, String> entry : dataTypeList.entrySet()) {
						 String key=entry.getKey();
						 map.put(key,rs.getObject(key));
					 }
					 repeatColumns.add(map);
				}
			}
			DbUtil.DbCloseQuery(rs,pst);
			String getCountSql="select count(*)  from "+tempTableName+" where ifchecked=1";
			pst=conn.prepareStatement(getCountSql);
			rs=pst.executeQuery();
			Integer total=null;
			while(rs.next()){
				total=rs.getInt(1);
			}
			resultMap.put("total",total);
			resultMap.put("rows",repeatColumns);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
    	return resultMap;
    }
    
    /**
     * 导出重复数据
     * @param bizId
     * @param userId
     * @param tempId
     * @param workSheetId
     * @param request
     * @param response
     */
	public void exportRepeatData(Integer bizId,String userId,Integer tempId,String workSheetId,HttpServletRequest request, HttpServletResponse response){
    	Connection conn=null;
    	PreparedStatement pst=null;
    	ResultSet rs=null;
    	String tempTableName="HAU_DM_B"+bizId+"C_IMPORT_"+userId;
    	Map<String,String> dataTypeList=new HashMap<String,String>();
		List<Map<String,Object>> repeatColumns=new ArrayList<Map<String,Object>>();//重复字段的集合
		List<String> excelHeaders=new ArrayList<String>();
		List<String> sheetColumns=new ArrayList<String>();
		try {
			conn=this.getDbConnection();
			String jsonData=getJsonData(bizId, tempId);
			JsonObject jsonObject= new JsonParser().parse(jsonData).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			JsonArray excelTemplateArray=jsonObject.get("ImportExcelTemplate").getAsJsonArray();
			JsonObject excelTemplate=excelTemplateArray.get(0).getAsJsonObject();
			String RepetitionColumn=excelTemplate.get("RepetitionExcludeWorkSheetColumn").getAsString();
			if(!"ID".equals(RepetitionColumn.toUpperCase())){
				getDataType(dataTypeList, dataArray, workSheetId, 1);
				String selectRepColumnSql="select ";
				for (int i = 0; i < dataArray.size(); i++) {
					String cName=dataArray.get(i).getAsJsonObject().get("DbFieldName").getAsString();
					String excelName=dataArray.get(i).getAsJsonObject().get("ExcelHeader").getAsString();
					if(dataTypeList.keySet().contains(cName)){
						String type=dataTypeList.get(cName);
						if("datetime".equals(type.toLowerCase())){
							selectRepColumnSql+="to_char("+cName+",'yyyy-mm-dd') "+cName+",";
						}else{
							selectRepColumnSql+=cName+",";
						}
						excelHeaders.add(excelName);
						sheetColumns.add(cName);
					}
					
				}
				selectRepColumnSql=selectRepColumnSql.substring(0,selectRepColumnSql.length()-1)+" from "+tempTableName+" where ifchecked=1";
				pst=conn.prepareStatement(selectRepColumnSql);
				rs=pst.executeQuery();
				while(rs.next()){
					Map<String,Object> map=new HashMap<String, Object>();
					 for(Map.Entry<String, String> entry : dataTypeList.entrySet()) {
						 String key=entry.getKey();
						 map.put(key,rs.getObject(key));
					 }
					 repeatColumns.add(map);
				}
			}
			
			ExcelUtils excelUtils=new ExcelUtils();
			excelUtils.exportExcel(excelHeaders, repeatColumns, sheetColumns, request, response);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		
    }
    /**
     * 将数据库来源数据插入到导入表中
     */
	public Map<String,Object> insertDbData(Integer bizId,String jsonData,String workSheetId,String tableName,List<Map<String,Object>> isnertData,String userId,String importBatchId,Integer dataPoolNumber,String operationName,String disBatchId){
    	Connection conn=null;
		Statement statement=null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Map<String,String> dataTypeList=new HashMap<String,String>();
		Map<String,Object> resultMap=new HashMap<String, Object>();//返回结果集 
		String poolName="HAU_DM_B"+bizId+"C_POOL";
		String orePoolName="HAU_DM_B"+bizId+"C_POOL_ORE";
		List<String> customerBatchIds=idfactory.newIds("DM_CID", isnertData.size());
		try {
			conn=this.getDbConnection();
			//不自动提交数据
			conn.setAutoCommit(false);
			JsonObject jsonObject= new JsonParser().parse(jsonData).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			//获取导入表字段所属类型
			getDataType(dataTypeList,dataArray,workSheetId,2);
			//数据池记录表里面插数据
			String isnertDataPoolSql="insert into "+poolName+"(ID,SourceID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime) "
									+" values(S_"+poolName+".nextval,?,?,?,?,?,?,?,?,?,?,sysdate)";
			pst=conn.prepareStatement(isnertDataPoolSql);
			//数据池操作记录表里面插数据
			String dataPoolOperationSql="insert into "+orePoolName+"(ID,SourceID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime)"
										+" values(S_"+orePoolName+".nextval,?,?,?,?,?,?,?,?,?,?,sysdate)";
			pst1=conn.prepareStatement(dataPoolOperationSql);
			//向导入表插数据
			statement=conn.createStatement();
			for (int i = 0; i < isnertData.size(); i++) {
				String customerBatchId=customerBatchIds.get(i);//客户号
				String insertImportDataSql="insert into "+tableName+"(ID,IID,CID,modifylast,modifyid,modifyuserid,modifytime,";
				
				pst.setString(1,disBatchId);
				pst.setString(2, importBatchId);
				pst.setString(3,customerBatchId);
				pst.setString(4,operationName);
				pst.setInt(5, dataPoolNumber);
				pst.setInt(6, dataPoolNumber);
				pst.setInt(7, 0);
				pst.setInt(8, 0);
				pst.setInt(9, 0);
				pst.setString(10, userId);
			
				pst1.setString(1,disBatchId);
				pst1.setString(2, importBatchId);
				pst1.setString(3,customerBatchId);
				pst1.setString(4,operationName);
				pst1.setInt(5, dataPoolNumber);
				pst1.setInt(6, dataPoolNumber);
				pst1.setInt(7, 0);
				pst1.setInt(8, 0);
				pst1.setInt(9, 0);
				pst1.setString(10, userId);
				for (int k = 0; k < dataArray.size(); k++) {
					String sourceName=dataArray.get(k).getAsJsonObject().get("FieldNameSource").getAsString();
					if(sourceName!=null&&!"".equals(sourceName)){
						insertImportDataSql+=dataArray.get(k).getAsJsonObject().get("FieldName").getAsString()+",";

					}
				}
				insertImportDataSql=insertImportDataSql.substring(0,insertImportDataSql.length()-1)+") values(S_"+tableName+".nextval,'"+importBatchId+"','"+customerBatchId+"',1,0,'"+userId+"',sysdate,";
				for (int j = 0; j < dataArray.size(); j++) {
					String cName=dataArray.get(j).getAsJsonObject().get("FieldName").getAsString();
						if(dataTypeList.keySet().contains(cName)){
							String type=dataTypeList.get(cName);
							if(type.toLowerCase().startsWith("varchar")){
								insertImportDataSql+="'"+isnertData.get(i).get(cName)+"',";
							}else if(type.toLowerCase().startsWith("int")){
								insertImportDataSql+=isnertData.get(i).get(cName)+",";
							}else if(type.toLowerCase().startsWith("datetime")){
								insertImportDataSql+="to_date('"+isnertData.get(i).get(cName)+"','yyyy-mm-dd'),";
							}
						}
					
				
				}
				insertImportDataSql=insertImportDataSql.substring(0,insertImportDataSql.length()-1)+")";
				statement.addBatch(insertImportDataSql);
				pst.addBatch();
				pst1.addBatch();
			}
			pst.executeBatch();
			pst1.executeBatch();
			statement.executeBatch();
			DbUtil.DbCloseExecute(pst);
			//导入批次表里面插数据
			String insertImportBatchSql="insert into HASYS_DM_IID(id,iid,BusinessId,ImportTime,UserID,Name,Description,ImportType) values(SEQ_HASYS_DM_IID.nextval,?,?,sysdate,?,?,?,?)";
			pst=conn.prepareStatement(insertImportBatchSql);
			pst.setString(1, importBatchId);
			pst.setInt(2, bizId);
			pst.setString(3, userId);
			pst.setString(4, "导入批次");
			pst.setString(5, "导入批次");
			pst.setString(6, operationName);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			String insertDisBatchSql="insert into HASYS_DM_DID(id,BusinessID,DID,DistributionName,ModifyUserID,ModifyTime,Description) values(S_HASYS_DM_DID.nextval,?,?,?,?,sysdate,?)";
			pst=conn.prepareStatement(insertDisBatchSql);
			pst.setInt(1,bizId);
			pst.setString(2, disBatchId);
			pst.setString(3,"分配批次");
			pst.setString(4,userId);
			pst.setString(5,"分配批次");
			pst.executeUpdate();
			conn.commit();
			resultMap.put("flag", 2);
			resultMap.put("result",true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("result",false);
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			try {
				if(pst1!=null){
					pst1.close();
				}
				if(statement!=null){
					statement.close();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
    }

    /**
     * 获取字段类型
     * @param dataTypeList
     * @param columList
     * @param dataArray
     * @param workSheetId
     * @param action
     */
    public void getDataType(Map<String,String> dataTypeList,JsonArray dataArray,String workSheetId,Integer action){
    	Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String actionName=null;
		String sourceName=null;
		try {
			conn=this.getDbConnection();
			//获取导入表字段所属类型和
			String getDataTypeSql="select datatype,columnname from hasys_worksheetcolumn  where workSheetId=? and columnname in(";
			if(action==1){
				actionName="DbFieldName";
				sourceName="ExcelHeader";
			}else{
				actionName="FieldName";
				sourceName="FieldNameSource";
			}
			for (int i = 0; i < dataArray.size(); i++) {
				String name=dataArray.get(i).getAsJsonObject().get(actionName).getAsString();
				String exname=dataArray.get(i).getAsJsonObject().get(sourceName).getAsString();
				if(exname!=null&&!"".equals(exname)){
					getDataTypeSql+="'"+name+"',";
				}
			}
			getDataTypeSql=getDataTypeSql.substring(0,getDataTypeSql.length()-1)+")";
			pst=conn.prepareStatement(getDataTypeSql);
			pst.setString(1, workSheetId);;
			rs=pst.executeQuery();
			while(rs.next()){
				dataTypeList.put(rs.getString(2),rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
    	
    }
    /**
     * 创建中间表并保存数据
     */
	public  Map<String,Object> createTepporaryImportTable(List<WorkSheetColumn> sheetColumnList,List<Map<String,Object>> dataList,Integer bizId,String userId){
    	Connection conn=null;
		Statement statement=null;
		Map<String,Object>  resultMap=new HashMap<String, Object>();
		String tableName="HAU_DM_B"+bizId+"C_IMPORT_"+userId;
		try {
			conn=this.getDbConnection();
			//创建表
			createTable(tableName, sheetColumnList);
			conn.setAutoCommit(false);
			//临时表插数据 
			statement=conn.createStatement();
			List<String> customerBatchIds=idfactory.newIds("DM_CID", dataList.size());
			for(int i=0;i<dataList.size();i++){
				String customerBatchId=customerBatchIds.get(i);//客户号
				String insertDataSql="insert into "+tableName+"(TEMPID,CUSTOMERID,";
				String valuesSql=" values(S_HAU_DM_B101C_IMPORT.nextval,'"+customerBatchId+"',";
				for (int j = 0; j < sheetColumnList.size(); j++) {
					String columnName=sheetColumnList.get(j).getField();
					String type=sheetColumnList.get(j).getDataType();
					if(dataList.get(i).keySet().contains(columnName)){
						insertDataSql+=columnName+",";
						if(type.toLowerCase().startsWith("varchar")){
							valuesSql+="'"+dataList.get(i).get(columnName)+"',";
						}else if(type.toLowerCase().startsWith("int")){
							valuesSql+=dataList.get(i).get(columnName)+",";
						}else if(type.toLowerCase().startsWith("datetime")){
							valuesSql+="to_date('"+dataList.get(i).get(columnName)+"','yyyy-mm-dd'),";
						}
					}
				}
				insertDataSql=insertDataSql.substring(0,insertDataSql.length()-1)+")"+valuesSql.substring(0,valuesSql.length()-1)+")";
				statement.addBatch(insertDataSql);
				if((i+1)%1000==0){
					statement.executeBatch();
				}
			}
			statement.executeBatch();
			conn.commit();
			resultMap.put("result", true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("result", false);
		}finally{
			try {
				if(statement!=null){
					statement.close();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DbUtil.DbCloseConnection(conn);
		}
    	return resultMap;
    }
    /**
     * 获取导入表数据
     */
    public Map<String,Object> getImportExcelData(Integer bizId,List<WorkSheetColumn> sheetColumnList,Integer num,Integer pageSize,String userId){
    	List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
    	Map<String,Object> resultMap=new HashMap<String, Object>();
    	Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		String tableName="HAU_DM_B"+bizId+"C_IMPORT_"+userId;
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		try {
			conn=this.getDbConnection();
			//查询出要展示的数据
			String getDataSql1="select tempId,";
			String getDataSql2=" select tempId,";
			for(int i = 0; i < sheetColumnList.size(); i++) {
				String column=sheetColumnList.get(i).getField();
				getDataSql1+=column+",";
				getDataSql2+=column+",";
			}
			getDataSql2="("+getDataSql2+"rownum rn from "+tableName+" where rownum<?) a";
			getDataSql1=getDataSql1.substring(0,getDataSql1.length()-1)+" from"+getDataSql2+" where rn>=?";
			pst=conn.prepareStatement(getDataSql1);
			pst.setInt(1, endNum);
			pst.setInt(2,startNum);
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("tempId",rs.getObject("tempId"));
				for (int i = 0; i < sheetColumnList.size(); i++) {
					String column=sheetColumnList.get(i).getField();
					if(rs.getObject(column)!=null){
						map.put(column, rs.getObject(column));
					}
				}
				dataList.add(map);
			}
			DbUtil.DbCloseQuery(rs,pst);
			String  countSql="select count(*) from "+tableName;
			pst=conn.prepareStatement(countSql);
			rs=pst.executeQuery();
			Integer total=null;
			while(rs.next()){
				total=rs.getInt(1);
			}
			resultMap.put("rows",dataList);
			resultMap.put("total",total);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return resultMap;
    }
   
    /**
     * 插一条数据到导入表
     * @param bizId
     * @param importBatchId
     * @param customerId
     * @param userId
     * @param customerInfo
     */
  @SuppressWarnings({ "unchecked", "unused"})
public void insertDataToImPortTable(Integer bizId,String importBatchId,String customerId,String userId,String customerInfo,Integer Modifyid){
	  Connection conn=null;
	  PreparedStatement pst = null;
	  String tableName="HAU_DM_B"+bizId+"C_IMPORT";
	  Map<String,Object> columnMap=new Gson().fromJson(customerInfo, Map.class);
	  String workSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());
	  try {
		conn=this.getDbConnection();
		String updateSql="update "+tableName+" set MODIFYLAST=0 where IID='"+importBatchId+"' and CID='"+customerId+"'";
		pst=conn.prepareStatement(updateSql);
		pst.executeUpdate();
		DbUtil.DbCloseExecute(pst);
		String columnSql="insert into "+tableName+"(ID,IID,CID,Modifyuserid,Modifylast,Modifyid,Modifytime,";
		String valueSql=" values(S_"+tableName+".nextval,'"+importBatchId+"','"+customerId+"','"+userId+"',1,"+Modifyid+",sysdate,";
		Iterator<Entry<String, Object>> it = columnMap.entrySet().iterator();
		 for(Map.Entry<String, Object> entry : columnMap.entrySet()) {
			 	String column=entry.getKey();
			 	if("ID".equals(column.toUpperCase())||"CID".equals(column.toUpperCase())||"IID".equals(column.toUpperCase())||"MODIFYUSERID".equals(column.toUpperCase())||"MODIFYLAST".equals(column.toUpperCase())||"MODIFYTIME".equals(column.toUpperCase())||"MODIFYID".equals(column.toUpperCase())){
			 		continue;
			 	}
			   columnSql+=entry.getKey()+",";
			   String type=getDataType(workSheetId, entry.getKey());
			   if("datetime".equals(type.toLowerCase())){
				   valueSql+="to_date('"+entry.getValue()+"','yyyy-mm-dd hh24:mi:ss'),";
			   }else if("int".equals(type.toLowerCase())){
				   valueSql+=entry.getValue()+",";
			   }else{
				   valueSql+="'"+entry.getValue()+"',";
			   }
		  
		 }
		 columnSql=columnSql.substring(0,columnSql.length()-1)+")"+valueSql.substring(0,valueSql.length()-1)+")";
		 pst=conn.prepareStatement(columnSql);
		 pst.executeUpdate();
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
		try {
			if(pst!=null){
				pst.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DbUtil.DbCloseConnection(conn);
	}
	  
  }
  /**
   * 向结果表插一条数据
   * @param bizId
   * @param sourceID
   * @param importBatchId
   * @param customerId
   * @param userId
   * @param modifyid
   * @param dialType
   * @param dialTime
   * @param customerCallId
   * @param endcodeType
   * @param endCode
   */
public void insertDataToResultTable(Integer bizId,String sourceID,String importBatchId,String customerId,String userId,Integer modifyid,String dialType,Date dialTime,String customerCallId,String endcodeType,String endCode ){
	  Connection conn=null;
	  PreparedStatement pst = null;
	  String tableName="HAU_DM_Result";
	  String dataTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dialTime);
	  try {
		conn=this.getDbConnection();
		String updateSql="update "+tableName+" set Modifylast=0";
		pst=conn.prepareStatement(updateSql);
		pst.executeUpdate();
		DbUtil.DbCloseExecute(pst);
		String columnSql="insert into "+tableName+"(id,BUSINESSID,sourceId,iid,cid,Modifylast,Modifyid,Modifyuserid,Modifytime,DialType,dialTime,CUSTOMERCALLID,ENDCODETYPE,ENDCODE)";
		String valueSql=" values(S_HAU_DM_RESULT.nextval,'"+bizId+"','"+sourceID+"','"+importBatchId+"','"+customerId+"',1,"+modifyid+",'"+userId+"',sysdate,'"+dialType+"',to_date('"+dataTime+"','yyyy-mm-dd hh24:mi:ss'),'"+customerCallId+"','"+endcodeType+"','"+endCode+"')";
		columnSql=columnSql+valueSql;
		pst=conn.prepareStatement(columnSql);
		pst.executeUpdate();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
		DbUtil.DbCloseExecute(pst);
		DbUtil.DbCloseConnection(conn);
	}
	  
 }
  
  /**
	 * 修改临时表
	 * @param bizId
	 * @param userId
	 * @param tempIds
	 * @param action
	 */
	public void updateTempData(Integer bizId,String userId,String tempIds,Integer action){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String[] arrTempId=tempIds.split(",");
		String tableName="HAU_DM_B"+bizId+"C_IMPORT_"+userId;
		try {
			conn=this.getDbConnection();
			String sql=null;
			if(action==0){
				sql="update "+tableName+" set IFCHECKED=1";
			}else{
				sql="update "+tableName+" set IFCHECKED=1 where tempId in(";
				for (int i = 0; i < arrTempId.length; i++) {
					String tempId=arrTempId[i];
					if(tempId==null||"".equals(tempId)){
						continue;
					}
					sql+=Integer.valueOf(tempId)+",";
				}
				sql=sql.substring(0,sql.length()-1)+")";
			}
			pst=conn.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
	}
	
	public  String getDataType(String workSheetId,String columName){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String dataType=null;
		try {
			conn=this.getDbConnection();
			String getTypeSql="select dataType from HASYS_WORKSHEETCOLUMN	where upper(COLUMNNAME)=? and  workSheetId=?";
			pst=conn.prepareStatement(getTypeSql);
			pst.setString(1, columName.toUpperCase());
			pst.setString(2, workSheetId);
			rs=pst.executeQuery();
			while(rs.next()){
				dataType=rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return dataType;
	}
	
	public void createTable(String tableName,List<WorkSheetColumn> sheetColumnList){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn=this.getDbConnection();
			String selectTable="select table_name from user_tables where table_name=?";
			pst=conn.prepareStatement(selectTable);
			pst.setString(1,tableName);
			rs=pst.executeQuery();
			String dbTableName=null;
			while(rs.next()){
				dbTableName=rs.getString(1);
			}
			DbUtil.DbCloseQuery(rs, pst);
			//创建临时表sql
			String createTableSql="create table "+tableName+"(TEMPID NUMBER,IFCHECKED NUMBER,CUSTOMERID VARCHAR2(50),";
			for (int i = 0; i < sheetColumnList.size(); i++) {
				String type=sheetColumnList.get(i).getDataType();
				String columnName=sheetColumnList.get(i).getField();
				if("CUSTOMERID".equals(columnName.toUpperCase())){
					continue;
				}
				if("varchar".equals(type.toLowerCase())){
					createTableSql+=columnName+"  VARCHAR2("+sheetColumnList.get(i).getLength()+"),";
				}else if("int".equals(type.toLowerCase())){
					createTableSql+=columnName+"  NUMBER,";
				}else if("datetime".equals(type.toLowerCase())){
					createTableSql+=columnName+"  DATE,";
				}
			}
			
			createTableSql=createTableSql.substring(0, createTableSql.length()-1)+")";
			if(dbTableName==null){
				pst=conn.prepareStatement(createTableSql);
				pst.executeUpdate();
			}else{
				//删除数据
				String delteSql="delete from "+tableName;
				pst=conn.prepareStatement(delteSql);
				pst.executeUpdate();
				DbUtil.DbCloseExecute(pst);
				//删除表
				String dropTableSql="drop table "+tableName;
				pst=conn.prepareStatement(dropTableSql);
				pst.executeUpdate();
				DbUtil.DbCloseExecute(pst);
				//创建表
				pst=conn.prepareStatement(createTableSql);
				pst.executeUpdate();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		
	}
    
	/**
	 * 根据业务Id和模板Id获取JSON数据
	 * @param bizId
	 * @param templateId
	 * @return
	 */
	public String getJsonData(Integer bizId,Integer templateId){
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		String jsonData=null;
		try {
			conn=this.getDbConnection();
			String getXmlSql="select xml from HASYS_DM_BIZTEMPLATEIMPORT where TEMPLATEID=? and BUSINESSID=?";
			pst=conn.prepareStatement(getXmlSql);
			pst.setInt(1, templateId);
			pst.setInt(2,bizId);
			rs = pst.executeQuery();
			while(rs.next()){
				jsonData=ClobToString(rs.getClob(1));	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e){
			
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return jsonData;
	}
	
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
    
    public String getImportTime(Integer bizId,Integer templateId){
    	Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		String importTime=null;
		try {
			conn=this.getDbConnection();
			String sql="select to_char(importTime,'yyyy-mm-dd hh24:mi:ss') from HASYS_DM_IMPORTTIME where BUSINESSID=? and TEMPLATEID=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,bizId);
			pst.setInt(2,templateId);
			rs=pst.executeQuery();
			while(rs.next()){
				importTime=rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return importTime;
    }
    
    public void updateImportTime(Integer bizId,Integer templateId,String importTime,String maxTime){
       	Connection conn=null;
		PreparedStatement pst=null;
		try {
			conn=this.getDbConnection();
			String sql=null;
			if(importTime==null){
				sql="insert into HASYS_DM_IMPORTTIME(BUSINESSID,TEMPLATEID,importTime) values("+bizId+","+templateId+","+"to_date('"+maxTime+"','yyyy-mm-dd hh24:mi:ss')"+")";
			}else{
				sql="update HASYS_DM_IMPORTTIME set importTime="+"to_date('"+maxTime+"','yyyy-mm-dd hh24:mi:ss')"+" where BUSINESSID="+bizId+" and TEMPLATEID="+templateId;
			}
			pst=conn.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
    }
}
