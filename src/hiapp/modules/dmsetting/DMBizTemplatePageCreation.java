package hiapp.modules.dmsetting;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dm.setting.dbLayer.DMBizWorkSheets;
import dm.setting.dbLayer.WorkSheetDm;
import dm.setting.dbLayer.WorkSheetTypeDm;
import hiapp.system.worksheet.dblayer.WorkSheet;
import hiapp.system.worksheet.dblayer.WorkSheetColumn;
import hiapp.utils.UtilServlet;


public class DMBizTemplatePageCreation {
	private int bizId;
	private int templateId;
	private String name;
	private String description;
	private String xml;
	private Document xmlDoc;
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
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public int getBizId() {
		return bizId;
	}
	public void setBizId(int bizId) {
		this.bizId = bizId;
	}
	public boolean loadXml(UtilServlet utilServlet) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String szXml=null;
		try {
			String szSql =String.format("SELECT name,description,Xml from HASYS_DM_BIZTEMPLATEPAGES where BusinessId=%d AND TemplateID=%d ",bizId,templateId);
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if(rs.next()){
				setName(rs.getString(1));
				setDescription(rs.getString(2));
				szXml=rs.getString(3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 	
		finally {
			utilServlet.DbCloseQuery(rs, stmt);
		}
		xmlDoc=null;
		if(szXml==null || szXml==""){
	        try {
	            DocumentBuilderFactory factory = DocumentBuilderFactory
	                    .newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            xmlDoc= builder.newDocument();
	            
	            Element root = xmlDoc.createElement("page");
	            root.setAttribute("pageType", "Dial");
	            String szBizId=String.format("%d", bizId);
	            root.setAttribute("bizId", szBizId);
	            xmlDoc.appendChild(root); 
	            m_LoadXmlNew_CustListPage();
	            m_LoadXmlNew_CustDetailPage(utilServlet);
	            m_LoadXmlNew_DialListPage();
	            saveXml(utilServlet, "d:\\JavaCreatedXml.xml");
	            
	        } catch (ParserConfigurationException e) {
	            System.out.println(e.getMessage());
	        }			
		}
		else{
	        try {
	        	StringReader sr = new StringReader(szXml); 
	        	InputSource is = new InputSource(sr); 
	        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
	        	DocumentBuilder builder=factory.newDocumentBuilder();
	        	xmlDoc= builder.parse(is);  	        
	        } catch (Exception e) {
	            e.printStackTrace();
				return false;
	        } 
		}
            
		return true;
	}
	private boolean m_LoadXmlNew_CustListPage(){
        String fileName=String.format("DmBizBiz%d_CustList", bizId);
        String title=String.format("业务%d_客户列表", bizId);
        String template="dial/dial_template1.temp";
        
        Element root=xmlDoc.getDocumentElement();
        Element xmlNodePage= xmlDoc.createElement("CustListPage");		
        root.appendChild(xmlNodePage); 
        xmlNodePage.setAttribute("PageFile", fileName);
        xmlNodePage.setAttribute("Title", title);
        xmlNodePage.setAttribute("Template", template);
        
        
        m_LoadXmlNew_Filter(xmlNodePage);
        m_LoadXmlNew_DataGrid(xmlNodePage);
		return true;
	}
	private boolean m_LoadXmlNew_CustDetailPage(UtilServlet utilServlet){
        String fileName=String.format("DmBiz%d_CustDetail", bizId);
        String title=String.format("业务%d_客户拨打", bizId);
        String template="dial/dial_template2.temp";
        
        Element root=xmlDoc.getDocumentElement();
        Element xmlNodePage= xmlDoc.createElement("CustDetailPage");		
        root.appendChild(xmlNodePage); 
        xmlNodePage.setAttribute("PageFile", fileName);
        xmlNodePage.setAttribute("Title", title);
        xmlNodePage.setAttribute("Template", template);
        
		List<WorkSheetDm> listWorkSheet=new ArrayList<WorkSheetDm>();
		DMBizWorkSheets.getWorkSheetAll(utilServlet, bizId, listWorkSheet);

        Element xmlNodeWorkSheetDetailList= xmlDoc.createElement("WorkSheetDetailList");
        xmlNodeWorkSheetDetailList.setAttribute("PageItemType","Panel");
        xmlNodePage.appendChild(xmlNodeWorkSheetDetailList); 
		
		for(int ii=0;ii<listWorkSheet.size();ii++){
			WorkSheetDm workSheet=listWorkSheet.get(ii);
			String type=workSheet.getType();
			if(type.equals("客户导入工作表")) continue;	
			if(type.equals("预约工作表")) continue;	
			if(type.equals("预约历史工作表")) continue;	
			if(type.equals("客户分配工作表")) continue;	
			if(type.equals("客户分配历史工作表")) continue;
			
	        Element xmlNodeWorkSheetDetail= xmlDoc.createElement("WorkSheetDetail");
	        xmlNodeWorkSheetDetail.setAttribute("PageItemType", "ControlPanel");
	        String workSheetId=String.format("%d", workSheet.getId());
	        xmlNodeWorkSheetDetail.setAttribute("WorkSheetId", workSheetId);
	        xmlNodeWorkSheetDetail.setAttribute("HasVisibleItem", "1");
	        xmlNodeWorkSheetDetail.setAttribute("GroupBoxTitle", workSheet.getNameCh());
	        xmlNodeWorkSheetDetail.setAttribute("GroupBoxName", workSheet.getNameCh());
	        xmlNodeWorkSheetDetailList.appendChild(xmlNodeWorkSheetDetail);

	        
	        m_LoadXmlNew_FlowLayoutSetting(xmlNodeWorkSheetDetail);
	        Element xmlNodeFlowLayoutItems= xmlDoc.createElement("FlowLayoutItems");
	        xmlNodeWorkSheetDetail.appendChild(xmlNodeFlowLayoutItems);
	        

	        List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
	        WorkSheet.getColumns(utilServlet.dbConn, workSheet.getId(), listColumns);//静态调用
	        for(int jj=0;jj<listColumns.size();jj++){
	        	WorkSheetColumn workSheetColumn=listColumns.get(jj);
		        Element xmlNodeItem= xmlDoc.createElement("Item");
		        xmlNodeItem.setAttribute("ColumnName", workSheetColumn.getName());
		        xmlNodeItem.setAttribute("WorkSheetColumnNameCh", workSheetColumn.getNameCh());
		        String controlNameOptr=String.format("%s_id",workSheetColumn.getName());
		        xmlNodeItem.setAttribute("ControlNameOptr", controlNameOptr);
		        
		        xmlNodeItem.setAttribute("IsVisible", "1");
		        xmlNodeItem.setAttribute("ControlType", "textbox");
		        xmlNodeItem.setAttribute("ComboboxOptions", "");
		        xmlNodeItem.setAttribute("IsIncludeDialButton", "0");
		        xmlNodeItem.setAttribute("PrefixText", workSheetColumn.getNameCh());
		        xmlNodeItem.setAttribute("PostfixText", "");
		        xmlNodeItem.setAttribute("IsMustFill", "0");
		        xmlNodeItem.setAttribute("Length", "-1");
		        xmlNodeItem.setAttribute("Mask", "0");
		        xmlNodeItem.setAttribute("IsReadOnly", "0");
		        xmlNodeItem.setAttribute("IsDisabled", "0");
		        xmlNodeItem.setAttribute("IsFromFirstCol", "0");
		        xmlNodeItem.setAttribute("OccupyColCount", "1");
		        xmlNodeItem.setAttribute("OccupyRowCount", "1");
		        xmlNodeFlowLayoutItems.appendChild(xmlNodeItem);
	        }
			
		}

		return true;
	}
	private boolean m_LoadXmlNew_DialListPage(){
        String fileName=String.format("DmBiz%d_DialList", bizId);
        String title=String.format("业务%d_拨打列表", bizId);
        String template="dial/dial_template3.temp";
        
        Element root=xmlDoc.getDocumentElement();
        Element xmlNodePage= xmlDoc.createElement("DialListPage");		
        root.appendChild(xmlNodePage); 
        xmlNodePage.setAttribute("PageFile", fileName);
        xmlNodePage.setAttribute("Title", title);
        xmlNodePage.setAttribute("Template", template);
        m_LoadXmlNew_Filter(xmlNodePage);
        m_LoadXmlNew_DataGrid(xmlNodePage);

		return true;
	}
	private boolean m_LoadXmlNew_Filter(Element xmlNodePage){
        Element xmlNodeFilter= xmlDoc.createElement("Filter");
        xmlNodeFilter.setAttribute("PageItemType", "ControlPanel");
        xmlNodeFilter.setAttribute("IsFilter", "1");
        xmlNodeFilter.setAttribute("DataTimeFileterColumn", "");
        xmlNodePage.appendChild(xmlNodeFilter);
        m_LoadXmlNew_FlowLayoutSetting(xmlNodeFilter);
        Element FlowLayoutItems= xmlDoc.createElement("FlowLayoutItems");
        xmlNodeFilter.appendChild(FlowLayoutItems);
        
		return true;
	}
	private boolean m_LoadXmlNew_FlowLayoutSetting(Element xmlNodeParent){
        Element xmlNodeFlowLayoutSetting= xmlDoc.createElement("FlowLayoutSetting");
        xmlNodeFlowLayoutSetting.setAttribute("PageItemType", "ControlPanel");
        xmlNodeFlowLayoutSetting.setAttribute("ColumnCount","4");
        xmlNodeFlowLayoutSetting.setAttribute("IsPreFixHasColon","1");
        xmlNodeFlowLayoutSetting.setAttribute("IsRatioCalculate","0");
        xmlNodeFlowLayoutSetting.setAttribute("RowHeight","27");
        xmlNodeFlowLayoutSetting.setAttribute("RowPedding","4");
        xmlNodeParent.appendChild(xmlNodeFlowLayoutSetting);
        for(int ii=0;ii<4;ii++){
            Element xmlNodeCol= xmlDoc.createElement("Col");
            xmlNodeCol.setAttribute("ColWidth", "246");
            xmlNodeCol.setAttribute("PostFixWidth", "0");
            xmlNodeCol.setAttribute("PreFixIndent", "80");
            xmlNodeFlowLayoutSetting.appendChild(xmlNodeCol);
        }
        
		return true;
	}
	public boolean m_LoadXmlNew_DataGrid(Element xmlNodeParent){
        Element xmlNodeDataGrid= xmlDoc.createElement("DataGrid");
        xmlNodeDataGrid.setAttribute("PageItemType", "GridControl");
        xmlNodeDataGrid.setAttribute("Title", "");
        xmlNodeParent.appendChild(xmlNodeDataGrid);
		return true;
	}
	
	public boolean getPageBaseInfo(UtilServlet utilServlet,String pageType,StringBuffer pageFile,StringBuffer title){
        Element root=xmlDoc.getDocumentElement();
		Node xmlNodePageSelect=m_getChildByNodeName(root,pageType);
		String value;
		value=xmlGetAttrValue(xmlNodePageSelect, "PageFile");
		pageFile.append(value);
		value=xmlGetAttrValue(xmlNodePageSelect, "Title");
		title.append(value);
		System.out.println(pageFile);
		System.out.println(title);
		return true;
	}
	public boolean updatePageBaseInfo(UtilServlet utilServlet,String pageType,String title){
        Element root=xmlDoc.getDocumentElement();
		Node xmlNodePageSelect=m_getChildByNodeName(root,pageType);
		xmlSetAttrValue(xmlNodePageSelect, "Title",title);
		return false;
	}
	public boolean getFilterColumnsSource(UtilServlet utilServlet,int bizId,int templateId,String pageType,List<FilterColumnSourceItem> listColumnsSource) throws Exception{
		List<WorkSheetDm> listWorkSheet=new ArrayList<WorkSheetDm>();
		if(!DMBizWorkSheets.getWorkSheetAll(utilServlet, bizId, listWorkSheet)){
			utilServlet.setResultCode(1);
			utilServlet.setResultMessage("获取所有工作表失败！");
			return false;
		}
		List<WorkSheet> listWorkSheetUse=new ArrayList<WorkSheet>();
		for(int ii=0;ii<listWorkSheet.size();ii++){
			WorkSheetDm workSheet=listWorkSheet.get(ii);
			String type=workSheet.getType();
			if(type.equals(WorkSheetTypeDm.WSTDM_CUSTOMERTASK.getType())){
				listWorkSheetUse.add(workSheet);
			}
			else if(type.equals(WorkSheetTypeDm.WSTDM_USERDEFINE.getType())){
				listWorkSheetUse.add(workSheet);
			}
		}
		for(int ii=0;ii<listWorkSheetUse.size();ii++){
			WorkSheet workSheet=listWorkSheetUse.get(ii);
			List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
			DMBizWorkSheets.getWorkSheetColumns(utilServlet, workSheet.getId(), listColumns);
			for(int jj=0;jj<listColumns.size();jj++){
				WorkSheetColumn workSheetColumn=listColumns.get(jj);
				FilterColumnSourceItem filterColumnSourceItem=new FilterColumnSourceItem();
				String workSheetColumnNameCh=String.format("%s.%s", workSheet.getNameCh(),workSheetColumn.getNameCh());
				filterColumnSourceItem.setWorkSheetColumnNameCh(workSheetColumnNameCh);
				filterColumnSourceItem.setWorkSheetNameCh(workSheet.getNameCh());
				filterColumnSourceItem.setWsColumnNameCh(workSheetColumn.getNameCh());
				filterColumnSourceItem.setColumnName(workSheetColumn.getName());
				listColumnsSource.add(filterColumnSourceItem);
			}
		}	
		return true;
	}
	public boolean getGridColumnsSource(UtilServlet utilServlet,int bizId,int templateId,String pageType,List<GridColumnSourceItem> listColumnsSource) throws Exception{
		List<WorkSheetDm> listWorkSheet=new ArrayList<WorkSheetDm>();
		if(!DMBizWorkSheets.getWorkSheetAll(utilServlet, bizId, listWorkSheet)){
			utilServlet.setResultCode(1);
			utilServlet.setResultMessage("获取所有工作表失败！");
			return false;
		}
		List<WorkSheet> listWorkSheetUse=new ArrayList<WorkSheet>();
		for(int ii=0;ii<listWorkSheet.size();ii++){
			WorkSheetDm workSheet=listWorkSheet.get(ii);
			String type=workSheet.getType();
			if(type.equals("客户任务工作表")){
				listWorkSheetUse.add(workSheet);
			}
			else if(type.equals("客户分配工作表")){
				listWorkSheetUse.add(workSheet);
			}
			else if(type.equals("用户自定义工作表")){
				listWorkSheetUse.add(workSheet);
			}
		}
		for(int ii=0;ii<listWorkSheetUse.size();ii++){
			WorkSheet workSheet=listWorkSheetUse.get(ii);
			List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
			DMBizWorkSheets.getWorkSheetColumns(utilServlet, workSheet.getId(), listColumns);
			for(int jj=0;jj<listColumns.size();jj++){
				WorkSheetColumn workSheetColumn=listColumns.get(jj);
				GridColumnSourceItem  gridColumnSourceItem=new GridColumnSourceItem();
				String workSheetColumnNameCh=String.format("%s.%s", workSheet.getNameCh(),workSheetColumn.getNameCh());
				gridColumnSourceItem.setWorkSheetColumnNameCh(workSheetColumnNameCh);
				gridColumnSourceItem.setWorkSheetNameCh(workSheet.getNameCh());
				gridColumnSourceItem.setWsColumnNameCh(workSheetColumn.getNameCh());
				gridColumnSourceItem.setColumnName(workSheetColumn.getName());
				listColumnsSource.add(gridColumnSourceItem);
			}
		}	
		return true;
	}
	public boolean getFilterItems(UtilServlet utilServlet,int bizId,int templateId,String pageType,List<FilterItem> listFilterItems) throws Exception{
        Element root=xmlDoc.getDocumentElement();
		Node xmlNodePageSelect=m_getChildByNodeName(root,pageType);
        Node xmlNodeFilter=m_getChildByNodeName(xmlNodePageSelect,"Filter");
        Node xmlNodeFlowLayoutItems=m_getChildByNodeName(xmlNodeFilter,"FlowLayoutItems");;
        for(int ii=0;ii<xmlNodeFlowLayoutItems.getChildNodes().getLength();ii++){
        	Node xmlNodeFilterItem=xmlNodeFlowLayoutItems.getChildNodes().item(ii);
            String nodeName=xmlNodeFilterItem.getNodeName();
            if(nodeName=="#text") continue;

        	FilterItem filterItem=new FilterItem();
        	filterItem.setWorkSheetNameCh(xmlGetAttrValue(xmlNodeFilterItem, "WorkSheetNameCh"));
        	filterItem.setWorkSheetColumnNameCh(xmlGetAttrValue(xmlNodeFilterItem, "WorkSheetColumnNameCh"));
        	filterItem.setColumnName(xmlGetAttrValue(xmlNodeFilterItem, "ColumnName"));
        	filterItem.setControlType(xmlGetAttrValue(xmlNodeFilterItem, "ControlType"));
        	filterItem.setComboboxOptions(xmlGetAttrValue(xmlNodeFilterItem, "ComboboxOptions"));
        	filterItem.setPrefixLabel(xmlGetAttrValue(xmlNodeFilterItem, "PrefixText"));
        	filterItem.setPostfixLabel(xmlGetAttrValue(xmlNodeFilterItem, "PostfixText"));
        	filterItem.setIsMustFill(xmlGetAttrValue(xmlNodeFilterItem, "IsMustFill"));
        	filterItem.setControlNameOptr(xmlGetAttrValue(xmlNodeFilterItem, "ControlNameOptr"));
        	listFilterItems.add(filterItem);
        }
		return true;
	}
	public boolean getGridColumns(UtilServlet utilServlet,int bizId,int templateId,String pageType,List<GridColumnItem> listGridColumnItems) throws Exception{
        Element root=xmlDoc.getDocumentElement();
		Node xmlNodePageSelect=m_getChildByNodeName(root,pageType);
        Node xmlNodeDataGrid=m_getChildByNodeName(xmlNodePageSelect,"DataGrid");
        for(int ii=0;ii<xmlNodeDataGrid.getChildNodes().getLength();ii++){
        	Node xmlNodeGridColumnItem=xmlNodeDataGrid.getChildNodes().item(ii);
            String nodeName=xmlNodeGridColumnItem.getNodeName();
            if(nodeName=="#text") continue;
        	
        	GridColumnItem gridColumnItem=new GridColumnItem();
        	gridColumnItem.setTitle(xmlGetAttrValue(xmlNodeGridColumnItem, "Title"));
        	gridColumnItem.setWidth(xmlGetAttrValue(xmlNodeGridColumnItem, "Width"));
        	gridColumnItem.setWorkSheetColumnName(xmlGetAttrValue(xmlNodeGridColumnItem, "WorkSheetColumnName"));
        	gridColumnItem.setOperation(xmlGetAttrValue(xmlNodeGridColumnItem, "Operation"));
        	gridColumnItem.setColumnName(xmlGetAttrValue(xmlNodeGridColumnItem, "ColumnName"));
        	listGridColumnItems.add(gridColumnItem);
        }
		return true;
	}
	public boolean getWorkSheetDetail(UtilServlet utilServlet,int bizId,int templateId,String pageType,int workSheetId,List<WorkSheetDetailItem> listWorkSheetDetialItems) throws Exception{
        Element root=xmlDoc.getDocumentElement();
		Node xmlNodePageSelect=m_getChildByNodeName(root,pageType);
        Node xmlNodeWorkSheetDetailList=m_getChildByNodeName(xmlNodePageSelect,"WorkSheetDetailList");
        Node xmlNodeWorkSheetDetailSelect=null;
        for(int ii=0;ii<xmlNodeWorkSheetDetailList.getChildNodes().getLength();ii++){
        	Node xmlNodeWorkSheetDetail=xmlNodeWorkSheetDetailList.getChildNodes().item(ii);
        	String workSheetIdString=xmlGetAttrValue(xmlNodeWorkSheetDetail, "WorkSheetId");
        	if(workSheetIdString!="" && Integer.parseInt(workSheetIdString)==workSheetId){
        		xmlNodeWorkSheetDetailSelect=xmlNodeWorkSheetDetail;
        		break;
        	}
        }
		Node xmlNodeFlowLayoutItems=m_getChildByNodeName(xmlNodeWorkSheetDetailSelect,"FlowLayoutItems");

        for(int ii=0;ii<xmlNodeFlowLayoutItems.getChildNodes().getLength();ii++){
            Node xmlNodeItem=xmlNodeFlowLayoutItems.getChildNodes().item(ii);
            String nodeName=xmlNodeItem.getNodeName();
            if(nodeName=="#text") continue;
            WorkSheetDetailItem workSheetDetailItem=new WorkSheetDetailItem();
            String xmlText=xmlNode2String(xmlNodeItem);
            workSheetDetailItem.setColumnName(xmlGetAttrValue(xmlNodeItem, "ColumnName"));
            workSheetDetailItem.setWorkSheetColumnNameCh(xmlGetAttrValue(xmlNodeItem, "WorkSheetColumnNameCh"));
            workSheetDetailItem.setControlNameOptr(xmlGetAttrValue(xmlNodeItem, "ControlNameOptr"));
            workSheetDetailItem.setIsVisible(xmlGetAttrValue(xmlNodeItem, "IsVisible"));
            workSheetDetailItem.setControlType(xmlGetAttrValue(xmlNodeItem, "ControlType"));
            workSheetDetailItem.setComboboxOptions(xmlGetAttrValue(xmlNodeItem, "ComboboxOptions"));
            workSheetDetailItem.setIsIncludeDialButton(xmlGetAttrValue(xmlNodeItem, "IsIncludeDialButton"));
            workSheetDetailItem.setPrefixText(xmlGetAttrValue(xmlNodeItem, "PrefixText"));
            workSheetDetailItem.setPostfixText(xmlGetAttrValue(xmlNodeItem, "PostfixText"));
            workSheetDetailItem.setIsMustFill(xmlGetAttrValue(xmlNodeItem, "IsMustFill"));
            workSheetDetailItem.setLength(xmlGetAttrValue(xmlNodeItem, "Length"));
            workSheetDetailItem.setMask(xmlGetAttrValue(xmlNodeItem, "Mask"));
            workSheetDetailItem.setIsReadOnly(xmlGetAttrValue(xmlNodeItem, "IsReadOnly"));
            workSheetDetailItem.setIsDisabled(xmlGetAttrValue(xmlNodeItem, "IsDisabled"));
            workSheetDetailItem.setIsFromFirstCol(xmlGetAttrValue(xmlNodeItem, "IsFromFirstCol"));
            workSheetDetailItem.setOccupyColCount(xmlGetAttrValue(xmlNodeItem, "OccupyColCount"));
            workSheetDetailItem.setOccupyRowCount(xmlGetAttrValue(xmlNodeItem, "OccupyRowCount"));
        	listWorkSheetDetialItems.add(workSheetDetailItem);
        	
        }
		return true;
	}
	
	public boolean updateFilterItems(UtilServlet utilServlet,String pageType,JsonArray jsonArray){
        Element root=xmlDoc.getDocumentElement();
		Node xmlNodePageSelect=m_getChildByNodeName(root,pageType);
        Node xmlNodeFilter=m_getChildByNodeName(xmlNodePageSelect,"Filter");
        Node xmlNodeFlowLayoutItems=m_getChildByNodeName(xmlNodeFilter,"FlowLayoutItems");;
		
        while (xmlNodeFlowLayoutItems.getChildNodes().getLength()>0) {
        	Node xmlItem=xmlNodeFlowLayoutItems.getFirstChild();
            xmlNodeFlowLayoutItems.removeChild(xmlItem);
		}
        
		for(int ii=0;ii<jsonArray.size();ii++){
			JsonObject jsItem=jsonArray.get(ii).getAsJsonObject();
	        Element xmlNodeItem=xmlDoc.createElement("FilterItem");
	        m_CreateColumnJson2XmlAttr("ColumnName",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("ControlNameOptr",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("WorkSheetNameCh",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("WorkSheetColumnNameCh",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("ControlType",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("ComboboxOptions",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("PrefixText",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("PostfixText",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("IsMustFill",jsItem,xmlNodeItem);
	        xmlNodeFlowLayoutItems.appendChild(xmlNodeItem);
		}	
		return true;
	}
	public boolean updateGridColumns(UtilServlet utilServlet,String pageType,JsonArray jsonArray){
        Element root=xmlDoc.getDocumentElement();
		Node xmlNodePageSelect=m_getChildByNodeName(root,pageType);
        Node xmlNodeDataGrid=m_getChildByNodeName(xmlNodePageSelect,"DataGrid");
        
        while(xmlNodeDataGrid.getChildNodes().getLength()>0)
        {
        	Node xmlNodeColumn=xmlNodeDataGrid.getFirstChild();
        	xmlNodeDataGrid.removeChild(xmlNodeColumn);
        }
		for(int ii=0;ii<jsonArray.size();ii++){
			JsonObject jsItem=jsonArray.get(ii).getAsJsonObject();
	        Element xmlNodeItem=xmlDoc.createElement("Item");
	        m_CreateColumnJson2XmlAttr("Title",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("Width",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("WorkSheetColumnName",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("ColumnName",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("Operation",jsItem,xmlNodeItem);
	        xmlNodeDataGrid.appendChild(xmlNodeItem);
		}	
        
		return true;
	}
	public boolean updateWorkSheetDetail(UtilServlet utilServlet,String pageType,int workSheetId, JsonArray jsonArray){
        Element root=xmlDoc.getDocumentElement();
		Node xmlNodePageSelect=m_getChildByNodeName(root,pageType);
        Node xmlNodeWorkSheetDetailList=m_getChildByNodeName(xmlNodePageSelect,"WorkSheetDetailList");
        Node xmlNodeWorkSheetDetailSelect=null;
        for(int ii=0;ii<xmlNodeWorkSheetDetailList.getChildNodes().getLength();ii++){
        	Node xmlNodeWorkSheetDetail=xmlNodeWorkSheetDetailList.getChildNodes().item(ii);
        	String workSheetIdString=xmlGetAttrValue(xmlNodeWorkSheetDetail, "WorkSheetId");
        	if(workSheetIdString!="" && Integer.parseInt(workSheetIdString)==workSheetId){
        		xmlNodeWorkSheetDetailSelect=xmlNodeWorkSheetDetail;
        		break;
        	}
        }
        
		Node xmlNodeFlowLayoutItems=m_getChildByNodeName(xmlNodeWorkSheetDetailSelect,"FlowLayoutItems");
        while(xmlNodeFlowLayoutItems.getChildNodes().getLength()>0){
        	Node xmlNodeItem=xmlNodeFlowLayoutItems.getFirstChild();
        	xmlNodeFlowLayoutItems.removeChild(xmlNodeItem);
        	
        }
		for(int ii=0;ii<jsonArray.size();ii++){
			JsonObject jsItem=jsonArray.get(ii).getAsJsonObject();
	        Element xmlNodeItem=xmlDoc.createElement("Item");
	        m_CreateColumnJson2XmlAttr("ColumnName",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("WorkSheetColumnNameCh",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("ControlNameOptr",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("IsVisible",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("ControlType",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("ComboboxOptions",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("IsIncludeDialButton",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("PrefixText",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("PostfixText",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("IsMustFill",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("Length",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("Mask",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("IsReadOnly",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("IsDisabled",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("IsFromFirstCol",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("OccupyColCount",jsItem,xmlNodeItem);
	        m_CreateColumnJson2XmlAttr("OccupyRowCount",jsItem,xmlNodeItem);
	        xmlNodeFlowLayoutItems.appendChild(xmlNodeItem);
		}	
        
		return true;
	}
     public static String xmlNode2String(Node node) { 
    	 if (node == null) {
    		 return "";
    	 } 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
			DocumentBuilder builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";

		}
		StringWriter sw = new StringWriter(); 
		try {
			transformer .transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";

		} 
		return sw.toString();
     }	
     private void m_CreateColumnJson2XmlAttr(String key,JsonObject jsItem,Element xmlNodeItem){
    	 String value="";
    	 JsonElement je=jsItem.get(key);
    	 if(je!=null){
        	 value=je.getAsString();
    	 }
    	 if(value.equals("文本框")) value="textbox";
    	 xmlNodeItem.setAttribute(key, value);
     }
 	public boolean saveXml(UtilServlet utilServlet,String templateXmlFileName) {
		PreparedStatement stmt = null;
	            
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(xmlDoc);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");  
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            OutputStreamWriter outputStreamWriter=null;
			try {
				outputStreamWriter = new OutputStreamWriter( 
							new FileOutputStream(templateXmlFileName), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            Result xmlResult = new StreamResult(outputStreamWriter);  
            transformer.transform(source, xmlResult);  

            System.out.println("生成XML文件成功!");
    		try {
    			String szSql = "DELETE FROM HASYS_DM_BIZTEMPLATEPAGES WHERE TemplateID=? AND BusinessId=? ";
    			PreparedStatement stat = utilServlet.dbConn.prepareStatement(szSql);
    			stat.setInt(1, templateId);
    			stat.setInt(2, bizId);
    			stat.execute();  

        		StringWriter sw = new StringWriter(); 
        		try {
        			transformer .transform(source, new StreamResult(sw));
        		} catch (TransformerException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();

        		} 
        		String szXml=sw.toString();

    			utilServlet.dbConn.setAutoCommit(false);
    			szSql = "INSERT INTO HASYS_DM_BIZTEMPLATEPAGES (ID,TemplateID,BusinessId,Name,Description,Xml) "+
    							 "VALUES(SEQ_BIZADimport.nextval,?,?,?,?,?) ";
    	        stat = utilServlet.dbConn.prepareStatement(szSql);
    	        stat.setInt(1, templateId);
    	        stat.setInt(2, bizId);
    	        stat.setString(3, name);
    	        stat.setString(4, description);
    	        StringReader reader = new StringReader(szXml);  
    	        stat.setCharacterStream(5, reader, szXml.length());  
    	        stat.executeUpdate();  
    			
    		} catch (SQLException e) {
    			e.printStackTrace();
    			return false;
    		} 	
    		finally {
    			utilServlet.DbCloseExecute(stmt);
    		}
            
            
        } catch (TransformerConfigurationException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
        String fileName1=String.format("/app_pages/dial/DmBiz%d_CustDetail.jsp", bizId);
        String fileName2=String.format("/app_pages/dial/DmBizBiz%d_CustList.jsp", bizId);
        String fileName3=String.format("/app_pages/dial/DmBiz%d_DialList.jsp", bizId);

        m_addPageRecord(utilServlet, fileName1, "客户详细页面");
        m_addPageRecord(utilServlet, fileName2, "客户列表页面");
        m_addPageRecord(utilServlet, fileName3, "客户拨打历史列表页面");
		return true;
	}
 	private String xmlGetAttrValue(Node node,String AttrName){
 		String AttrValue="";
 		if(node.getAttributes()==null){
 	 		return AttrValue;
 		}
 		Node node1=node.getAttributes().getNamedItem(AttrName);
 		if(node1==null) return AttrValue;
 		
 		AttrValue=node1.getNodeValue();
 		return AttrValue;
 	}
 	private boolean xmlSetAttrValue(Node node,String AttrName,String AttrValue){
 		Node node1=node.getAttributes().getNamedItem(AttrName);
 		if(node1==null) return false;
 		node1.setNodeValue(AttrValue);
 		return true;
 	}
 	private Node m_getChildByNodeName(Node nodeParent,String nodeName){
 		Node node=null;
        for(int ii=0;ii<nodeParent.getChildNodes().getLength();ii++){
        	Node xmlNodeChild=nodeParent.getChildNodes().item(ii);
        	String nodeName1=xmlNodeChild.getNodeName();
        	if(nodeName.equals(nodeName1)){
        		node=xmlNodeChild;
        		break;
        	}
        }
 		
 		return node;
 	}
	private static boolean m_addPageRecord(UtilServlet utilServlet,String pageName,String title){
		PreparedStatement stmt = null;
		try {
			String szSql = "DELETE FROM bu_inf_page where filenamerelpath=? ";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setString(1,pageName);
			stmt.execute();
		} catch (SQLException e) {
			utilServlet.setResultCode(2);
			utilServlet.setResultMessage("删除记录错误！");
			return false;
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}
		
		try{
			String szSql = "insert into bu_inf_page  (filenamerelpath, isfolder, filetitle) values (?,?,?); ";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setString(1,pageName);
			stmt.setInt(2,0);
			stmt.setString(3, title);
			
			stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}

		try{
			String szSql = "delete from bu_inf_navigateitem where navigateviewid=1 and filename=? ";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setString(1,pageName);
			stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}
		
		int IdMax=-1;
		ResultSet rs = null;
		try {
			String szSql ="select max(itemid) from BU_INF_NAVIGATEITEM";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			if(rs.next()){
				IdMax=rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 	
		finally {
			utilServlet.DbCloseQuery(rs, stmt);
		}
		
		try{
			String szSql = "insert into bu_inf_navigateitem  (navigateviewid, itemid, label, istab, filename) values  (1, ?, ?, 0, ?)";
 			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setInt(1,IdMax+1);
			stmt.setString(2,title);
			stmt.setString(3, pageName);
			
			stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}
		return true;
		
		
	}
 	
}
