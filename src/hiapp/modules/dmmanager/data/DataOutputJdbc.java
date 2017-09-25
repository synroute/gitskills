package hiapp.modules.dmmanager.data;



import hiapp.modules.dmmanager.bean.OutputFirstRow;
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
			String sql="select TEMPLATEID,BUSINESSID,NAME,DESCRIPTION,ISDEFAULT,configJson from HASYS_DM_BIZTEMPLATEEXPORT where BUSINESSID=?";
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
				exportTempplate.setConfigJson(rs.getString(6));
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
	public List<OutputFirstRow>  getOutDataColumns(Integer bizId,Integer templateId) throws IOException{
		List<OutputFirstRow> columnList=new ArrayList<OutputFirstRow>();
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn=this.getDbConnection();
			String sql="select configJson from HASYS_DM_BIZTEMPLATEEXPORT where TEMPLATEID=? and  BUSINESSID=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, templateId);
			pst.setInt(2,bizId);
			rs=pst.executeQuery();
			String columns=null;
			while(rs.next()){
				if(rs.getClob(1)!=null&&!"".equals(rs.getClob(1))){
					columns=ClobToString(rs.getClob(1));	
				}
				
			}
			JsonObject jsonObject= new JsonParser().parse(columns).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			for (int i = 0; i < dataArray.size(); i++) {
				OutputFirstRow output=new OutputFirstRow();
				String excelHeader=dataArray.get(i).getAsJsonObject().get("ExcelHeader").getAsString();
				output.setField(excelHeader);
				output.setTitle(excelHeader);
				output.setExcelHeader(excelHeader);
				columnList.add(output);
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
	 * 根据时间获得要导出的数据 
	 * @param startTime
	 * @param endTime
	 * @param templateId
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "resource", "unchecked" })
	public List<Map<String,Object>> getOutputDataByTime(String startTime,String endTime,Integer templateId,Integer bizId) throws IOException{
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> workSheetNameList=new ArrayList<String>();
		//导入数据的集合
		List<Map<String,Object>> outDataList=new ArrayList<Map<String,Object>>();
		try {
			conn=this.getDbConnection();
			String getOutputXmlSql="select configJson from HASYS_DM_BIZTEMPLATEEXPORT where TEMPLATEID=? and BUSINESSID=?";
			pst=conn.prepareStatement(getOutputXmlSql);
			pst.setInt(1,templateId );
			pst.setInt(2,bizId);
			rs=pst.executeQuery();
			String workSheets=null;
			while(rs.next()){
				workSheets=ClobToString(rs.getClob(1));
			}
			JsonObject jsonObject= new JsonParser().parse(workSheets).getAsJsonObject();
			JsonArray dataArray=jsonObject.get("FieldMaps").getAsJsonArray();
			for (int i = 0; i < dataArray.size(); i++) {
				String  workSheetName=null;
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetName").isJsonNull()){
				 workSheetName=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
				}
				if(workSheetName!=null&&!"".equals(workSheetName)){
					workSheetNameList.add(workSheetName);
				}
				
			}
			
			//对workSheetNameList去重
			List<String> newList=new ArrayList<String>(new HashSet(workSheetNameList));
			//查询数据Sql
			String getOutDataSql="select ";
			for (int i = 0; i < dataArray.size(); i++) {
				String workSheetName1=null;
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetName").isJsonNull()){
					 workSheetName1=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
					}
				for (int j = 0; j <newList.size(); j++) {
					if(newList.get(j).equals(workSheetName1)){
						String asName="a"+j+".";//别名
						String column=dataArray.get(i).getAsJsonObject().get("WorkSheetColName").getAsString();
						getOutDataSql+=asName+dataArray.get(i).getAsJsonObject().get("WorkSheetColName").getAsString()+",";
						break;
					}
				}
			}
			getOutDataSql=getOutDataSql.substring(0,getOutDataSql.length()-1)+" from ";
			for(int k=0;k<newList.size();k++){
					String asName="a"+k;//别名
					getOutDataSql+=newList.get(k)+" "+asName+",";
			
			}
			
			getOutDataSql=getOutDataSql.substring(0,getOutDataSql.length()-1)+" where ";
			for (int h = 1; h < newList.size(); h++) {
				String asName="a"+h+".";
				getOutDataSql+="a0.IID="+asName+"IID and ";
			}
			for (int h = 1; h < newList.size(); h++) {
				String asName="a"+h+".";
				getOutDataSql+="a0.CID="+asName+"CID and ";
			}
			getOutDataSql+="a0.IID in(select IID from HASYS_DM_IID where IMPORTTIME>to_date(?,'yyyy-mm-dd hh24:mi:ss') and IMPORTTIME<to_date(?,'yyyy-mm-dd hh24:mi:ss') and BUSINESSID=?)";
			pst=conn.prepareStatement(getOutDataSql);
			pst.setString(1,startTime);
			pst.setString(2, endTime);
			pst.setInt(3,bizId);
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				for (int i = 0; i < dataArray.size(); i++) {
					String value=null;
					if(!dataArray.get(i).getAsJsonObject().get("WorkSheetColName").isJsonNull()){
						value=dataArray.get(i).getAsJsonObject().get("WorkSheetColName").getAsString();
					}
					String key=dataArray.get(i).getAsJsonObject().get("ExcelHeader").getAsString();
					if(value!=null&&!"".equals(value)){
						map.put(key,rs.getObject(value));
					}else{
						map.put(key,"");
					}
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
