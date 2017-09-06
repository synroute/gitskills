package hiapp.modules.dmsetting.data;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dm.setting.dbLayer.DMBizWorkSheets;
import dm.setting.dbLayer.WorkSheetTypeDm;
import hiapp.utils.base.DatabaseType;
import hiapp.system.worksheet.dblayer.WorkSheet;
import hiapp.system.worksheet.dblayer.WorkSheetColumn;
import hiapp.system.worksheet.dblayer.WorkSheetManager;

public class DMBizTemplateExport {
	private int templateID;
	private String name;
	private String Description;
	private int isDefault;
	public int getTemplateID() {
		return templateID;
	}
	public void setTemplateID(int templateID) {
		this.templateID = templateID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public int getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}
	
	
	public static int getxml(Connection dbConn,int bizId ,int templateId) throws SQLException
		{
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String szXml="";
			String szSql =String.format("SELECT Xml from HASYS_DM_BIZTEMPLATEEXPORT where BusinessId=%d AND TemplateID=%d ",bizId,templateId);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if(rs.next()){
				szXml=rs.getString(1);
			}
			if (szXml.equals("")) {
				return 0;
			}else {
				return 1;
			}
			
		}
	
	
	public static boolean getMapColumns(Connection dbConn,DatabaseType dataBaseType,int bizId ,int templateId,List<ImportMapColumn> listMapColumn) throws SQLException{
		//int workSheetId=DMBizWorkSheets.getWorkSheetId(dbConn,bizId, WorkSheetTypeDm.WSTDM_CUSTOMERIMPORT.getType());
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String worksql="select ID,NAME,NAMECH from HASYS_WORKSHEET where ID in (select WORKSHEETID from HASYS_DM_BIZWORKSHEET where BIZID="+bizId+")";
		stmt = dbConn.prepareStatement(worksql);
		rs = stmt.executeQuery();
		while(rs.next())
		{
			WorkSheet workSheet=null;
			WorkSheetManager.getWorkSheet(dbConn, dataBaseType, rs.getInt("ID"), workSheet);
			List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
			WorkSheet.getColumns(dbConn, rs.getInt("ID"),listColumns);
			for(int ii=0;ii<listColumns.size();ii++){
				WorkSheetColumn workSheetColumn=listColumns.get(ii);	
				ImportMapColumn importMapColumn=new ImportMapColumn();
				importMapColumn.setName(workSheetColumn.getName());
				importMapColumn.setNameCh(workSheetColumn.getNameCh());
				importMapColumn.setDescription(workSheetColumn.getDescription());
				importMapColumn.setWorksheetid(rs.getString("ID"));
				importMapColumn.setWorksheetName(rs.getString("NAME"));
				importMapColumn.setWorksheetNameCh(rs.getString("NAMECH"));
				listMapColumn.add(importMapColumn);
			}
		}
		String szXml=null;
        Node xmlNodeFieldMaps=null;
        	
		try {
			String szSql =String.format("SELECT Xml from HASYS_DM_BIZTEMPLATEEXPORT where BusinessId=%d AND TemplateID=%d ",bizId,templateId);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if(rs.next()){
				szXml=rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
		if(szXml==null){
			return true;
		}
		
        try {
			StringReader sr = new StringReader(szXml);
			InputSource is = new InputSource(sr);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			Element eRoot = doc.getDocumentElement();
			xmlNodeFieldMaps=eRoot.getFirstChild();
            
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
            
		if(xmlNodeFieldMaps==null){
			return true;
		}
		for(int ii=0;ii<listMapColumn.size();ii++){
			ImportMapColumn importMapColumn=listMapColumn.get(ii);
			String name=importMapColumn.getName();
	        for(int jj=0;jj<xmlNodeFieldMaps.getChildNodes().getLength();jj++){
	        	Node xmlNodeFilterItem=xmlNodeFieldMaps.getChildNodes().item(jj);
	        	String name1=xmlGetAttrValue(xmlNodeFilterItem,"WorkSheetColName");
	        	if(name1.toUpperCase().equals(name)){
		        	importMapColumn.setCellAddr(xmlGetAttrValue(xmlNodeFilterItem,"CellAddr"));
		        	importMapColumn.setRowIndex(xmlGetAttrValue(xmlNodeFilterItem,"RowIndex"));
		        	importMapColumn.setColIndex(xmlGetAttrValue(xmlNodeFilterItem,"ColIndex"));
		        	importMapColumn.setExcelColumnName(xmlGetAttrValue(xmlNodeFilterItem,"ExcelHeader"));
		        	importMapColumn.setxmlWorksheetid(xmlGetAttrValue(xmlNodeFilterItem,"WorkSheetId"));
		        	importMapColumn.setxmlWorkSheetColName(xmlGetAttrValue(xmlNodeFilterItem,"WorkSheetColName"));
		        	importMapColumn.setxmlWorksheetName(xmlGetAttrValue(xmlNodeFilterItem,"WorkSheetName"));
		        	break;
	        	}
	        }
		}
        return true;
	}
	
	public static boolean modifiedMapColumns(Connection dbConn,int bizId ,int templateId,JsonArray jaMapColumns){
		String szXml = null;
		// 閸掓稑缂揇ocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
//          document.setXmlStandalone(true);
            Element eRoot = document.createElement("ExportExcelTemplate");
            Element eFieldMaps=document.createElement("FieldMaps");				
    		for(int ii=0;ii<jaMapColumns.size();ii++){
    			JsonObject jso=jaMapColumns.get(ii).getAsJsonObject();
    			
                Element eItem = document.createElement("Item");
    	        m_CreateColumnJson2XmlAttr("CellAddr",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("RowIndex",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("ColIndex",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("ExcelHeader",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("WorkSheetId",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("WorkSheetColName",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("WorkSheetName",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("FunctionName",jso,eItem);
                eFieldMaps.appendChild(eItem);
    		 }
            
            eRoot.appendChild(eFieldMaps);
            document.appendChild(eRoot);

            TransformerFactory transFactory = TransformerFactory.newInstance();  
            Transformer transFormer = transFactory.newTransformer();  
            transFormer.setOutputProperty(OutputKeys.ENCODING, "GB2312");  
            DOMSource domSource = new DOMSource(document);  
      
            StringWriter sw = new StringWriter();  
            StreamResult xmlResult = new StreamResult(sw);  
            transFormer.transform(domSource, xmlResult);
            szXml=sw.toString();
            
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String name="";
        String description="";
        int isDefault=-1;
        String EXCELFILEDATA="";
		PreparedStatement stmt = null;
		ResultSet rs= null;
		try {
			String szSql = "select name, description, isdefault,EXCELFILEDATA from HASYS_DM_BIZTEMPLATEEXPORT where BusinessId=? AND TemplateID=?";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1, bizId);
			stmt.setInt(2, templateId);
			rs=stmt.executeQuery();
			if(rs.next()){
				name=rs.getString(1);
				description=rs.getString(2);
				isDefault=rs.getInt(3);
				EXCELFILEDATA=rs.getString(4);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			
		}

		try {
			String szSql = "delete from HASYS_DM_BIZTEMPLATEEXPORT where BusinessId=? AND TemplateID=?";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1, bizId);
			stmt.setInt(2, templateId);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			
		}
		
		
		try {
			
			String szSql = "INSERT INTO HASYS_DM_BIZTEMPLATEEXPORT (ID,TemplateID,BusinessId,Name,Description,isdefault,Xml,EXCELFILEDATA) "+
							 "VALUES(SEQ_BIZADimport.nextval,?,?,?,?,?,?,?) ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1, templateId);
			stmt.setInt(2, bizId);
			stmt.setString(3, name);
			stmt.setString(4, description);
			stmt.setInt(5, isDefault);
	        StringReader reader = new StringReader(szXml);  
	        stmt.setCharacterStream(6, reader, szXml.length()); 
	        stmt.setString(7, EXCELFILEDATA);
	        stmt.executeUpdate();  
			

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			
		}
		
		return true;
	}
    private static void m_CreateColumnJson2XmlAttr(String key,JsonObject jsItem,Element xmlNodeItem){
   	 String value="";
   	 JsonElement je=jsItem.get(key);
   	 if(je!=null && !je.isJsonNull()){
       	 value=je.getAsString();
   	 }
   	 xmlNodeItem.setAttribute(key, value);
    }
 	private static String xmlGetAttrValue(Node node,String AttrName){
 		String AttrValue="";
 		Node node1=node.getAttributes().getNamedItem(AttrName);
 		if(node1==null) return AttrValue;
 		
 		AttrValue=node1.getNodeValue();
 		return AttrValue;
 	}
    
	
}
