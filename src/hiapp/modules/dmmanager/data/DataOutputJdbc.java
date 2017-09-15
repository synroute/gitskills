package hiapp.modules.dmmanager.data;



import hiapp.modules.dmsetting.DMBizExportTemplate;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Repository
public class DataOutputJdbc extends BaseRepository{
	/**
	 * 获取所有导出模板
	 * @param bizId
	 * @return
	 * @throws IOException
	 */
	public List<DMBizExportTemplate> getOutputTemplates(Integer bizId) throws IOException{
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<DMBizExportTemplate> templateList=new ArrayList<DMBizExportTemplate>();
		try {
			conn=this.getDbConnection();
			String sql="select TEMPLATEID,BUSINESSID,NAME,DESCRIPTION,ISDEFAULT,XML from HASYS_DM_BIZTEMPLATEEXPORT where BUSINESSID=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, bizId);
			rs=pst.executeQuery();
			while(rs.next()){
				DMBizExportTemplate exportTempplate=new DMBizExportTemplate();
				exportTempplate.setTemplateId(rs.getInt(1));
				exportTempplate.setBizId(String.valueOf(bizId));
				exportTempplate.setTemplateName(rs.getString(3));
				exportTempplate.setDesc(rs.getString(4));
				exportTempplate.setIsDefault(rs.getInt(5));
				exportTempplate.setConfigJson(ClobToString(rs.getClob(6)));
				templateList.add(exportTempplate);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return templateList;
	}
	/**
	 * 获取导出数据在前台展示的列
	 * @param templateId
	 * @return
	 * @throws IOException
	 */
	public Map<String,List<String>>  getOutDataColumns(Integer templateId) throws IOException{
		List<String> excelHeaderList=new ArrayList<String>();
		List<String> columnList=new ArrayList<String>();
		Map<String,List<String>> dataMap=new HashMap<String, List<String>>(); 
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn=this.getDbConnection();
			String sql="select xml from HASYS_DM_BIZTEMPLATEEXPORT where TEMPLATEID=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, templateId);
			rs=pst.executeQuery();
			String columns=null;
			while(rs.next()){
				columns=ClobToString(rs.getClob(1));
			}
			JsonObject jsonObject= new JsonParser().parse(columns).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			for (int i = 0; i < dataArray.size(); i++) {
				String excelHeader=dataArray.get(i).getAsJsonObject().get("ExcelHeader").getAsString();
				String column=dataArray.get(i).getAsJsonObject().get("WorkSheetColName").getAsString();
				excelHeaderList.add(column);
				columnList.add(column);
			}
			dataMap.put("excelHeader", excelHeaderList);
			dataMap.put("column", columnList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return dataMap;
	}
	
	/**
	 * 根据时间获得要导出的数据 
	 * @param startTime
	 * @param endTime
	 * @param templateId
	 * @return
	 * @throws IOException
	 */
	public List<Map<String,Object>> getOutputDataByTime(String startTime,String endTime,Integer templateId) throws IOException{
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> workSheetNameList=new ArrayList<String>();
		//导入数据的集合
		List<Map<String,Object>> outDataList=new ArrayList<Map<String,Object>>();
		try {
			conn=this.getDbConnection();
			String getOutputXmlSql="select xml from HASYS_DM_BIZTEMPLATEEXPORT where TEMPLATEID=?";
			pst=conn.prepareStatement(getOutputXmlSql);
			pst.setInt(1,templateId );
		
			String workSheets=null;
			while(rs.next()){
				workSheets=ClobToString(rs.getClob(1));
			}
			JsonObject jsonObject= new JsonParser().parse(workSheets).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			for (int i = 0; i < dataArray.size(); i++) {
				String workSheetName=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
				workSheetNameList.add(workSheetName);
			}
			//对workSheetNameList去重
			HashSet set=new HashSet();
			workSheetNameList.clear();
			workSheetNameList.addAll(set);
			//查询数据Sql
			String getOutDataSql="select ";
			for (int i = 0; i < dataArray.size(); i++) {
				String workSheetName1=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
				for (int j = 0; j <workSheetNameList.size(); j++) {
					if(workSheetNameList.get(j).equals(workSheetName1)){
						String asName="a"+j+".";//别名
						getOutDataSql+=asName+dataArray.get(i).getAsJsonObject().get("WorkSheetColName").getAsString()+",";
						break;
					}
				}
			}
			getOutDataSql=getOutDataSql.substring(getOutDataSql.length()-1)+" from ";
			for(int k=0;k<workSheetNameList.size();k++){
				String asName="a"+k;//别名
				getOutDataSql+=workSheetNameList.get(k)+" "+asName+",";
			}
			
			getOutDataSql=getOutDataSql.substring(getOutDataSql.length()-1)+" where ";
			for (int h = 1; h < workSheetNameList.size(); h++) {
				String asName="a"+h+".";
				getOutDataSql+="a0.IID="+asName+"IID and ";
			}
			getOutDataSql+="a0.IID in(select IID from HASYS_DM_IID where IMPORTTIME>to_date(?,'yyyy-mm-dd') and IMPORTTIME<to_date(?,'yyyy-mm-dd'))";
			pst=conn.prepareStatement(getOutDataSql);
			pst.setString(1,startTime);
			pst.setString(2, endTime);
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				for (int i = 0; i < dataArray.size(); i++) {
					String key=dataArray.get(i).getAsJsonObject().get("WorkSheetColName").getAsString();
					map.put(key,rs.getObject(i+1));
				}
				outDataList.add(map);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return outDataList;
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
    

}
