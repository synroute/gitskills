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
	 * 获取所有导出模板w
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
			if(columns==null){
				return null;
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
	@SuppressWarnings({ "resource", "unchecked", "unused", "rawtypes" })
	public Map<String,Object> getOutputDataByTime(String startTime,String endTime,Integer templateId,Integer bizId,Integer num,Integer pageSize) throws IOException{
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> workSheetNameList=new ArrayList<String>();
		//导入数据的集合
		List<Map<String,Object>> outDataList=new ArrayList<Map<String,Object>>();
		Map<String,Object> resultMap=new HashMap<String, Object>();
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		String dataPoolName="HAU_DM_B"+bizId+"C_POOL";
		String importTableName="HAU_DM_B"+bizId+"C_IMPORT";
		String resultTableName="HAU_DM_B"+bizId+"C_Result";
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
				String workSheetId=null;
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetName").isJsonNull()){
				 workSheetName=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
				 if(workSheetName!=null&&!"".equals(workSheetName)){
						workSheetNameList.add(workSheetName);
					}
					
				}
				
			}
			
			//对workSheetNameList去重
			List<String> newList=new ArrayList<String>(new HashSet(workSheetNameList));
			//查询数据Sql
			String getOutDataSql="select b.IID,b.CID,";
			String getOutDataSql1="select IID,CID,";
			String getOutDataSql2="";
			for (int i = 0; i < dataArray.size(); i++) {
				String workSheetName1=null;
				String workSheetId=null;
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetName").isJsonNull()){
					 workSheetName1=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
					}
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetId").isJsonNull()){
					workSheetId=dataArray.get(i).getAsJsonObject().get("WorkSheetId").getAsString();
					
				}
				String suffix=workSheetName1.substring(workSheetName1.lastIndexOf("_")+1);
				for (int j = 0; j <newList.size(); j++) {
					if(newList.get(j).equals(workSheetName1)){
						String asName="a"+j+".";//别名
						if("pool".equals(suffix.toLowerCase())){
							asName="b.";
						}
						String column=dataArray.get(i).getAsJsonObject().get("WorkSheetColName").getAsString();
						if(!"IID".equals(column.toUpperCase())&&!"CID".equals(column.toUpperCase())){
							getOutDataSql+=getDataType(column,workSheetId,asName)+",";
							getOutDataSql1+=column+",";
							break;
						}
						
					}
				}
			}
			getOutDataSql=getOutDataSql+"rownum rn from "+dataPoolName+" b left join ";
			for (int i = 0; i < newList.size(); i++) {
				String asName="a"+i;
				String workSheetName=newList.get(i);
				String suffix=workSheetName.substring(workSheetName.lastIndexOf("_")+1);
				if("pool".equals(suffix.toLowerCase())){
					continue;
				}
				if(newList.contains(importTableName)&&!newList.contains(resultTableName)){
					if("import".equals(suffix.toLowerCase())){
						getOutDataSql2=" and "+asName+".Modifylast=1";
					}
				}
				if(!newList.contains(importTableName)&&newList.contains(resultTableName)){
					if("result".equals(suffix.toLowerCase())){
						getOutDataSql2=" and "+asName+".Modifylast=1";
					}
				}
				if(newList.contains(importTableName)&&newList.contains(resultTableName)){
					if("import".equals(suffix.toLowerCase())){
						getOutDataSql2=" and "+asName+".Modifylast=1";
					}
				}
			
				getOutDataSql+=newList.get(i)+ " "+asName+" on b.IID="+asName+".IID and b.CID="+asName+".CID left join ";
			}
			getOutDataSql=getOutDataSql.substring(0,getOutDataSql.lastIndexOf("left join"))+" where ";
			getOutDataSql+="b.IID in(select IID from HASYS_DM_IID where IMPORTTIME>to_date(?,'yyyy-mm-dd hh24:mi:ss') and IMPORTTIME<to_date(?,'yyyy-mm-dd hh24:mi:ss') and BUSINESSID=?)";
			getOutDataSql1=getOutDataSql1.substring(0,getOutDataSql1.length()-1)+" from ("+getOutDataSql+getOutDataSql2+" and rownum<?) t where rn>=?";
			pst=conn.prepareStatement(getOutDataSql1);
			pst.setString(1,startTime);
			pst.setString(2, endTime);
			pst.setInt(3,bizId);
			pst.setInt(4, endNum);
			pst.setInt(5,startNum);
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("IID",rs.getObject(1));
				map.put("CID",rs.getObject(2));
				for (int i = 0; i < dataArray.size(); i++) {
					String value=null;
					if(!dataArray.get(i).getAsJsonObject().get("WorkSheetColName").isJsonNull()){
						value=dataArray.get(i).getAsJsonObject().get("WorkSheetColName").getAsString();
					}
					String key=dataArray.get(i).getAsJsonObject().get("ExcelHeader").getAsString();
					if(value!=null&&!"".equals(value)&&!"IID".equals(key.toUpperCase())&&!"CID".equals(key.toUpperCase())){
						map.put(key,rs.getObject(value));
					}else{
						map.put(key,"");
					}
				}
				outDataList.add(map);
			}
			
			String getCountSql="select count(1) from ("+getOutDataSql+getOutDataSql2+") t";
			pst=conn.prepareStatement(getCountSql);
			pst.setString(1, startTime);
			pst.setString(2, endTime);
			pst.setInt(3,bizId);
			rs=pst.executeQuery();
			Integer total=0;
			while(rs.next()){
				total=rs.getInt(1);
			}
			resultMap.put("total", total);
			resultMap.put("rows",outDataList);
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
		String dataPoolName="HAU_DM_B"+bizId+"C_POOL";
		String importTableName="HAU_DM_B"+bizId+"C_IMPORT";
		String resultTableName="HAU_DM_B"+bizId+"C_Result";
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
			String getOutDataSql="select b.IID,";
			String getOutDataSql2="";
			for (int i = 0; i < dataArray.size(); i++) {
				String workSheetName1=null;
				String workSheetId=null;
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetName").isJsonNull()){
					 workSheetName1=dataArray.get(i).getAsJsonObject().get("WorkSheetName").getAsString();
					}
				if(!dataArray.get(i).getAsJsonObject().get("WorkSheetId").isJsonNull()){
					workSheetId=dataArray.get(i).getAsJsonObject().get("WorkSheetId").getAsString();
					
				}
				String suffix=workSheetName1.substring(workSheetName1.lastIndexOf("_")+1);
				for (int j = 0; j <newList.size(); j++) {
					if(newList.get(j).equals(workSheetName1)){
						String asName="a"+j+".";//别名
						if("pool".equals(suffix.toLowerCase())){
							asName="b.";
						}
						String column=dataArray.get(i).getAsJsonObject().get("WorkSheetColName").getAsString();
						if(!"IID".equals(column.toUpperCase())&&!"CID".equals(column.toUpperCase())){
							getOutDataSql+=getDataType(column,workSheetId,asName)+",";
							break;
						}
						
					}
				}
			}
			getOutDataSql=getOutDataSql+"rownum rn from "+dataPoolName+" b left join ";
			for (int i = 0; i < newList.size(); i++) {
				String asName="a"+i;
				String workSheetName=newList.get(i);
				String suffix=workSheetName.substring(workSheetName.lastIndexOf("_")+1);
				if("pool".equals(suffix.toLowerCase())){
					continue;
				}
				if(newList.contains(importTableName)&&!newList.contains(resultTableName)){
					if("import".equals(suffix.toLowerCase())){
						getOutDataSql2=" and "+asName+".Modifylast=1";
					}
				}
				if(!newList.contains(importTableName)&&newList.contains(resultTableName)){
					if("result".equals(suffix.toLowerCase())){
						getOutDataSql2=" and "+asName+".Modifylast=1";
					}
				}
				if(newList.contains(importTableName)&&newList.contains(resultTableName)){
					if("import".equals(suffix.toLowerCase())){
						getOutDataSql2=" and "+asName+".Modifylast=1";
					}
				}
			
				getOutDataSql+=newList.get(i)+ " "+asName+" on b.IID="+asName+".IID and b.CID="+asName+".CID left join ";
			}
			getOutDataSql=getOutDataSql.substring(0,getOutDataSql.lastIndexOf("left join"))+" where ";
			getOutDataSql+="b.IID in(select IID from HASYS_DM_IID where IMPORTTIME>to_date(?,'yyyy-mm-dd hh24:mi:ss') and IMPORTTIME<to_date(?,'yyyy-mm-dd hh24:mi:ss') and BUSINESSID=?)"+getOutDataSql2;
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
					if(value!=null&&!"".equals(value)&&!"IID".equals(key.toUpperCase())&&!"CID".equals(key.toUpperCase())){
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
    

	/**
	 * 获取字段类型
	 * @param columnName
	 * @param workSheetId
	 * @param asName
	 * @return
	 */
	public String getDataType(String columnName,String workSheetId,String asName){
		Connection conn=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String column=null;
		try {
			conn=this.getDbConnection();
			String sql="select dataType from Hasys_Worksheetcolumn where WORKSHEETID=? and COLUMNNAME=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,workSheetId);
			pst.setString(2, columnName);
			rs=pst.executeQuery();
			String dataType=null;
			while(rs.next()){
				dataType=rs.getString(1);
			}
			
			if("varchar".equals(dataType.toLowerCase())||"int".equals(dataType.toLowerCase())){
				column=asName+columnName;
			}else if("datetime".equals(dataType.toLowerCase())){
				column="to_char("+asName+columnName+",'yyyy-mm-dd hh24:mi:ss') "+columnName;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return column;
	}
	
	
}
