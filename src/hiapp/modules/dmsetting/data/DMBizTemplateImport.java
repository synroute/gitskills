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
import hiapp.utils.UtilServlet;

public class DMBizTemplateImport {
	private int templateId;
	private String name;
	private String description;
	private boolean isDefault;
	private String sourceType;
	public int getTemplateId() {
		return templateId;
	}
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public static boolean getMapColumns(Connection dbConn,DatabaseType dataBaseType,int bizId ,int templateId,List<ImportMapColumn> listMapColumn){
		int workSheetId=DMBizWorkSheets.getWorkSheetId(dbConn,bizId, WorkSheetTypeDm.WSTDM_CUSTOMERIMPORT.getType());
		WorkSheet workSheet=null;
		WorkSheetManager.getWorkSheet(dbConn, dataBaseType, workSheetId, workSheet);
		List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
		WorkSheet.getColumns(dbConn, workSheetId,listColumns);
		for(int ii=0;ii<listColumns.size();ii++){
			WorkSheetColumn workSheetColumn=listColumns.get(ii);
			ImportMapColumn importMapColumn=new ImportMapColumn();
			importMapColumn.setName(workSheetColumn.getName());
			importMapColumn.setNameCh(workSheetColumn.getNameCh());
			importMapColumn.setDescription(workSheetColumn.getDescription());
			listMapColumn.add(importMapColumn);
		}
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String szXml=null;
        Node xmlNodeFieldMaps=null;

		try {
			String szSql =String.format("SELECT Xml from HASYS_DM_BIZTEMPLATEIMPORT where BusinessId=%d AND TemplateID=%d ",bizId,templateId);
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
			int x=xmlNodeFieldMaps.getChildNodes().getLength();
	        for(int jj=0;jj<xmlNodeFieldMaps.getChildNodes().getLength();jj++){
	        	Node xmlNodeFilterItem=xmlNodeFieldMaps.getChildNodes().item(jj);
	        	String name1=xmlGetAttrValue(xmlNodeFilterItem,"Name");
	        	if(name1.toUpperCase().equals(name)){
		        	importMapColumn.setExcelRowNumber(xmlGetAttrValue(xmlNodeFilterItem,"ExcelRowNumber"));
		        	importMapColumn.setExcelColumnName(xmlGetAttrValue(xmlNodeFilterItem,"ExcelColumnName"));
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
            Element eRoot = document.createElement("ImportExcelTemplate");
            Element eFieldMaps=document.createElement("FieldMaps");
    		for(int ii=0;ii<jaMapColumns.size();ii++){
    			JsonObject jso=jaMapColumns.get(ii).getAsJsonObject();
    			
                Element eItem = document.createElement("Item");
    	        m_CreateColumnJson2XmlAttr("Name",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("NameCh",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("Description",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("ExcelRowNumber",jso,eItem);
    	        m_CreateColumnJson2XmlAttr("ExcelColumnName",jso,eItem);
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
        String sourceType="";
		PreparedStatement stmt = null;
		ResultSet rs= null;
		try {
			String szSql = "select name, description, isdefault, sourcetype from hasys_dm_biztemplateimport where BusinessId=? AND TemplateID=?";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1, bizId);
			stmt.setInt(2, templateId);
			rs=stmt.executeQuery();
			if(rs.next()){
				name=rs.getString(1);
				description=rs.getString(2);
				isDefault=rs.getInt(3);
				sourceType=rs.getString(4);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			
		}

		try {
			String szSql = "delete from hasys_dm_biztemplateimport where BusinessId=? AND TemplateID=?";
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
			
			String szSql = "INSERT INTO hasys_dm_biztemplateimport (ID,TemplateID,BusinessId,Name,Description,isdefault,sourcetype,Xml) "+
							 "VALUES(SEQ_BIZADimport.nextval,?,?,?,?,?,?,?) ";
			stmt = dbConn.prepareStatement(szSql);
			stmt.setInt(1, templateId);
			stmt.setInt(2, bizId);
			stmt.setString(3, name);
			stmt.setString(4, description);
			stmt.setInt(5, isDefault);
			stmt.setString(6, sourceType);
	        StringReader reader = new StringReader(szXml);  
	        stmt.setCharacterStream(7, reader, szXml.length());  
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
