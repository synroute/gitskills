package hiapp.modules.dmmanager.data;

import hiapp.modules.dmmanager.bean.Business;
import hiapp.modules.dmmanager.bean.ImportTemplate;
import hiapp.modules.dmmanager.bean.WorkSheet;
import hiapp.modules.dmmanager.bean.WorkSheetColumn;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
@Repository
public class DataImportJdbc extends BaseRepository{
	@Autowired
	private IdFactory idfactory;
	/**
	 * 获取所有业务
	 * @param userId
	 * @return
	 */
	public List<Business> getBusinessData(String userId){
		List<Business> businessList=new ArrayList<Business>();
		List<Integer> ornizeIdList=new ArrayList<Integer>();
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn= this.getDbConnection();
			String getOrgnizeSql="select b.id,b.name,b.DESCRIPTION,b.OWNERGROUPID,b.DETAILSETTINGXML,b.MODEID,b.SUBMODEID from BU_MAP_USERORGROLE a,HASYS_DM_Business b  where a.GROUPID=b.OWNERGROUPID and a.USERID=?";
			pst=conn.prepareStatement(getOrgnizeSql);
			pst.setString(1,userId);
			rs = pst.executeQuery();
			while (rs.next()) {
				Business bus=new Business();
				bus.setId(rs.getInt(1));
				bus.setName(rs.getString(2));
				bus.setDescription(rs.getString(3));
				bus.setOwnergroup(rs.getInt(4));
				bus.setDetailSettingXml(rs.getString(5));
				bus.setModeId(rs.getInt(6));
				bus.setSubmodeId(rs.getInt(7));
				businessList.add(bus);
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
			//ִ�и���
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
		try {
			conn= this.getDbConnection();
			String sql="select WORKSHEETID from Hasys_Dm_Bizworksheet where BIZID=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, bizId);
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
			String sql="SELECT id,name,namech,description,isowner  FROM HASYS_WORKSHEET where id=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, workSheetId);
			rs = pst.executeQuery();
			while(rs.next()){
				workSheet.setId(rs.getInt(1));
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
				sheetColumn.setName(rs.getString(2));
				sheetColumn.setNameCh(rs.getString(3));
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
	/**
	 * 获取要导入的数据
	 * @param temPlateId
	 * @param bizId
	 * @return
	 * @throws IOException
	 */
	public List<Map<String,Object>> getDbData(Integer temPlateId,Integer bizId) throws IOException{
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String jsonData=null;
		String getDbDataSql="select ";
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
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
			JsonObject jsonObject= new JsonParser().parse("jsonData").getAsJsonObject();
			JsonObject excelTemplate=jsonObject.get("ImportExcelTemplate").getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			String sourceTableName=excelTemplate.get("SourceTableName").getAsString();
			for (int i = 0; i < dataArray.size(); i++) {
				getDbDataSql+=dataArray.get(i).getAsJsonObject().get("FieldNameSource").getAsString()+",";
			}
			getDbDataSql=getDbDataSql.substring(getDbDataSql.length()-1)+" from "+sourceTableName;
			pst=conn.prepareStatement(getDbDataSql);
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				for (int i = 0; i < dataArray.size(); i++) {
					String key=dataArray.get(i).getAsJsonObject().get("FieldName").getAsString();
					map.put(key,rs.getObject(i+1));
				}
				
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
	public Boolean insertImportData(Integer tempId,Integer bizId,String workSheetId,List<WorkSheetColumn> sheetColumnList,List<Map<String,Object>> isnertData,String tableName,String userId,String operationName) throws IOException{
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String jsonData=null;
		Statement statement=null;
		Integer uId=Integer.valueOf(userId);
		List<String> stringList=new ArrayList<String>();
		try {
			conn= this.getDbConnection();
			String getXmlSql="select xml from HASYS_DM_BIZTEMPLATEIMPORT where TEMPLATEID=? and BUSINESSID=?";
			pst=conn.prepareStatement(getXmlSql);
			pst.setInt(1, tempId);
			pst.setInt(2,bizId);
			rs = pst.executeQuery();
			while(rs.next()){
				jsonData=ClobToString(rs.getClob(1));	
			}
			//解析JSON RepetitionExcludeType
			JsonObject jsonObject= new JsonParser().parse("jsonData").getAsJsonObject();
			JsonObject excelTemplate=jsonObject.get("ImportExcelTemplate").getAsJsonObject();
			String repetitionExcludeType=excelTemplate.get("RepetitionExcludeType").getAsString();
			String RepetitionColumn=excelTemplate.get("RepetitionExcludeWorkSheetColumn").getAsString();
		    Integer RepetitionCount=Integer.valueOf(excelTemplate.get("RepetitionExcludeDayCount").getAsString());
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			String importBatchId=idfactory.newId("DM_IID");//饶茹批次号
			String disBatchId=idfactory.newId("DM_DID");//分配号
			String customerBatchId=idfactory.newId("DM_CID");//客户号
			//获取排重字段类型
			String getTypeSql="select dataType from HASYS_WORKSHEETCOLUMN	where COLUMNNAME=? and  workSheetId=?";
			pst=conn.prepareStatement(getTypeSql);
			pst.setString(1, RepetitionColumn);
			pst.setString(2, workSheetId);
			String type=null;
			while(rs.next()){
				type=rs.getString(1);
			}
			String distinctSql=null;
			String resultTableName="HAU_DM_B"+bizId+"C_Result";
			//查询数据
			if("按导入时间排重".equals(repetitionExcludeType)){
				if(type.startsWith("datetime")){
					 distinctSql="select to_char("+RepetitionColumn+",'yyyy-mm-dd') from "+tableName+" where modifytime <sysdate and modifytime>sysdate-"+RepetitionCount;
				}else{
					 distinctSql="select"+RepetitionColumn+" from "+tableName+" where modifytime <sysdate and modifytime>sysdate-"+RepetitionCount;
				}
			}else{
				if(type.startsWith("datetime")){
					 distinctSql="select to_char(a."+RepetitionColumn+",'yyyy-mm-dd') from "+tableName+" a,"+resultTableName+" b where b.DialTime <sysdate and b.DialTime>sysdate-"+RepetitionCount;
				}else{
					 distinctSql="select a."+RepetitionColumn+" from "+tableName+" a,"+resultTableName+" b where b.DialTime <sysdate and b.DialTime>sysdate-"+RepetitionCount;
				}
			}
			
			pst=conn.prepareStatement(distinctSql);
			rs=pst.executeQuery();
			while(rs.next()){
				if(type.startsWith("Varchar")||type.startsWith("datetime")){
					stringList.add(rs.getString(1));
				}else if(type.startsWith("int")){
					stringList.add(String.valueOf(rs.getInt(1)));
				}
			}
			//向导入表插数据
			statement=conn.createStatement();
			Boolean ifRepeat=true;
			for (int i = 0; i < isnertData.size(); i++) {
				String data=(String) isnertData.get(i).get(RepetitionColumn);
				for(int h=0;h<stringList.size();h++){
					if(data.equals(stringList.get(h))){
						ifRepeat=false;
						break;
					}
				}
				if(ifRepeat){
					String insertImportDataSql="insert into "+tableName+"(ID,IID,CID,modifylast,modifyid,modifyuserid,modifytime,";
					for (int k = 0; k < dataArray.size(); k++) {
						insertImportDataSql+=dataArray.get(k).getAsJsonObject().get("FieldName").getAsString()+",";
					}
					insertImportDataSql=insertImportDataSql.substring(insertImportDataSql.length()-1)+") values(HAU_DM_B101C_IMPORT.nextval,"+importBatchId+","+customerBatchId+",1,0,"+uId+",sysdate,";
					for (int j = 0; j < dataArray.size(); j++) {
						insertImportDataSql+=isnertData.get(i).get(dataArray.get(j).getAsJsonObject().get("FieldName").getAsString())+",";
					}
					insertImportDataSql=insertImportDataSql.substring(insertImportDataSql.length()-1)+")";
					statement.addBatch(insertImportDataSql);
				}
				
			}
			statement.executeBatch();
			
			//导入批次表里面插数据
			String insertImportBatchSql="isnert into HASYS_DM_IID(id,iid,BusinessId,ImportTime,UserID,Name,Description,ImportType) values(SEQ_HASYS_DM_IID.nextval,?,?,sysdate,?,?,?,?)";
			pst=conn.prepareStatement(insertImportBatchSql);
			pst.setString(1, importBatchId);
			pst.setInt(2, bizId);
			pst.setString(3, userId);
			pst.setString(4, "导入批次");
			pst.setString(5, "导入批次");
			pst.setString(6, "excel");
			pst.executeUpdate();
			String getDataSourceSql="select id from HASYS_DM_DATAPOOL where DataPoolName ='数据源池'";
			pst=conn.prepareStatement(getDataSourceSql);
			rs=pst.executeQuery();
			Integer dataPoolNumber=null;
			while(rs.next()){
				dataPoolNumber=rs.getInt(1);
			}
			//数据池记录表里面插数据
			String isnertDataPoolSql="isnert into HAU_DM_B1C_POOL(ID,DID,IID,CID,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime) "
									+" values(SEQ_HAU_DM_B1C_POOL.nextval,?,?,?,?,?,?,?,?,?,sysdate)";
			pst.setString(1,disBatchId);
			pst.setString(2, importBatchId);
			pst.setString(3,customerBatchId);
			pst.setInt(4, dataPoolNumber);
			pst.setInt(5, dataPoolNumber);
			pst.setInt(6, 0);
			pst.setInt(7, 0);
			pst.setInt(8, 0);
			pst.setString(9, userId);
			pst.executeUpdate();
			//数据池操作记录表里面插数据
			String dataPoolOperationSql="insert into HAU_DM_B1C_POOL_ORE(ID,DID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime)"
										+" values(SEQ_HAU_DM_B1C_POOL_ORE.nextval,?,?,?,?,?,?,?,?,?,?,sysdate)";
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
			pst.executeUpdate();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
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
    
public static void main(String[] args) {
	JsonObject json= new JsonParser().parse("111").getAsJsonObject();
	JsonArray  arr=new JsonParser().parse("123").getAsJsonArray();
	json.get("").getAsJsonObject();
	for (int i = 0; i < arr.size(); i++) {
		arr.get(i).getAsJsonObject().get("11").getAsString();
	}
}
}
