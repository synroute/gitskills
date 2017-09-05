package hiapp.modules.dmsetting.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import dmNew.setting.dbLayer.DMBizCreatepage;
import dmNew.setting.dbLayer.ImportMapColumn;
import hiapp.system.session.Tenant;
import hiapp.system.session.User;
import hiapp.utils.base.DatabaseType;

/**
 * Servlet implementation class servletDMVizCreatepage
 */
@WebServlet("/servletDMBizCreatepage")
public class servletDMBizCreatepage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMBizCreatepage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
HttpSession session=request.getSession(false);
		
		Connection dbConn = null;
		PrintWriter printWriter = response.getWriter();
		String userid="";
		try {
			Tenant tenant = (Tenant)session.getAttribute("tenant");
			dbConn= tenant.getDbConnectionConfig().getDbConnection();
			User user = (User)request.getSession(false).getAttribute("user");
			userid=user.getAgtId();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JsonObject jsonObject = null;
			jsonObject.addProperty("returnCode", "2");
			jsonObject.addProperty("returnMessage","锟斤拷时");
			printWriter.print(jsonObject.toString());
			return;
		}catch(NullPointerException e)
		{
			JsonObject jsonObject =  new JsonObject();
			jsonObject.addProperty("returnCode", "2");
			jsonObject.addProperty("returnMessage","瓒呮椂");
			printWriter.print(jsonObject.toString());
			return;
		}
		
		String szActionValue =  request.getParameter("action");
		String bizId=request.getParameter("bizId");
		if (szActionValue.equals("getKHMapColumns")) {
			 JsonObject jsonObject;
			 try {
				 jsonObject = getKHMapColumns(dbConn,Integer.parseInt(bizId));
					printWriter.print(jsonObject.toString());
				
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			
		}
		if (szActionValue.equals("getJGMapColumns")) {
			 JsonObject jsonObject;
			 try {
				 jsonObject = getJGMapColumns(dbConn,Integer.parseInt(bizId));
					printWriter.print(jsonObject.toString());
				
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			
		}
		if (szActionValue.equals("getMapColumns")) {
			 JsonObject jsonObject;
			 try {
				 jsonObject = getMapColumns(dbConn,Integer.parseInt(bizId));
					printWriter.print(jsonObject.toString());
				
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			
		}
		
		if (szActionValue.equals("Createxml")) {
			 
			 try {
				 	String szKHMapColumns=request.getParameter("KHMapColumns");
					JsonParser jsonParser=new JsonParser();
					JsonArray KHjsonArray=jsonParser.parse(szKHMapColumns).getAsJsonArray();
					
					String szJGMapColumns=request.getParameter("JGMapColumns");
					
					JsonArray JGjsonArray=jsonParser.parse(szJGMapColumns).getAsJsonArray();
					
					String szMapColumns=request.getParameter("MapColumns");
					
					JsonArray jsonArray=jsonParser.parse(szMapColumns).getAsJsonArray();
					CreateXml(dbConn,bizId,KHjsonArray,JGjsonArray,jsonArray);
					
				
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			
		}
		
	}
	
public static JsonObject getMapColumns(Connection dbConn,int bizId ) throws Exception{
		
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<ImportMapColumn> listMapColumn =new ArrayList<ImportMapColumn>();
		DMBizCreatepage.getWorkMapColumns(dbConn, DatabaseType.ORACLE,bizId, listMapColumn);
		
		JsonObject jsonObject=new JsonObject();
		JsonArray jsonArray=new JsonArray();
        for(int ii=0;ii<listMapColumn.size();ii++){
        	ImportMapColumn importMapColumn=listMapColumn.get(ii);
			JsonObject jsColumn=new JsonObject();
			jsColumn.addProperty("index", ii+1);
			jsColumn.addProperty("WorkSheetId", importMapColumn.getWorksheetid());
			jsColumn.addProperty("WorkSheetName", importMapColumn.getWorksheetName());
			jsColumn.addProperty("WorkSheetNameCh", importMapColumn.getWorksheetNameCh());
			jsColumn.addProperty("Name", importMapColumn.getName());
			jsColumn.addProperty("NameCh", importMapColumn.getNameCh());
			jsColumn.addProperty("WorkSheetIds", importMapColumn.getWorksheetid());
			jsColumn.addProperty("WorkSheetNames", importMapColumn.getWorksheetName());
			jsColumn.addProperty("WorkSheetNameChs", importMapColumn.getWorksheetNameCh());
			jsColumn.addProperty("Names", importMapColumn.getName());
			jsColumn.addProperty("NameChs", importMapColumn.getNameCh());
			jsonArray.add(jsColumn);
        }

		jsonObject.addProperty("total",listMapColumn.size());
		jsonObject.add("rows", jsonArray);
		return jsonObject;
	}
	

	public static JsonObject getKHMapColumns(Connection dbConn,int bizId) throws Exception{
		
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<ImportMapColumn> listMapColumn =new ArrayList<ImportMapColumn>();
		DMBizCreatepage.getMapColumns(dbConn, DatabaseType.ORACLE,bizId, listMapColumn,"客户导入工作表");
		
		JsonObject jsonObject=new JsonObject();
		JsonArray jsonArray=new JsonArray();
	    for(int ii=0;ii<listMapColumn.size();ii++){
	    	ImportMapColumn importMapColumn=listMapColumn.get(ii);
			JsonObject jsColumn=new JsonObject();
			jsColumn.addProperty("index", ii+1);
			
				
			jsColumn.addProperty("ColumnName", importMapColumn.getName());
			jsColumn.addProperty("ColumnNameCh", importMapColumn.getNameCh());
			jsColumn.addProperty("ControlTypes", "文本");
			jsColumn.addProperty("PrefixText", importMapColumn.getNameCh());
			jsColumn.addProperty("ControlType", "文本框");
			jsonArray.add(jsColumn);
	    }
	    
		jsonObject.addProperty("total",listMapColumn.size());
		jsonObject.add("rows", jsonArray);
		return jsonObject;
	}
	public static JsonObject getJGMapColumns(Connection dbConn,int bizId ) throws Exception{
			
			//utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
			List<ImportMapColumn> listMapColumn =new ArrayList<ImportMapColumn>();
			DMBizCreatepage.getMapColumns(dbConn, DatabaseType.ORACLE,bizId, listMapColumn,"结果表");
			
			JsonObject jsonObject=new JsonObject();
			JsonArray jsonArray=new JsonArray();
		    for(int ii=0;ii<listMapColumn.size();ii++){
		    	ImportMapColumn importMapColumn=listMapColumn.get(ii);
				JsonObject jsColumn=new JsonObject();
				if(!importMapColumn.getNameCh().contains("ID"))
				{
					jsColumn.addProperty("index", ii+1);	
					jsColumn.addProperty("ColumnName", importMapColumn.getName());
					jsColumn.addProperty("ColumnNameCh", importMapColumn.getNameCh());
					jsColumn.addProperty("ControlTypes", "文本");
					jsColumn.addProperty("PrefixText", importMapColumn.getNameCh());
					jsColumn.addProperty("ControlType", "文本框");
					jsonArray.add(jsColumn);
				}
		    }
		
			jsonObject.addProperty("total",listMapColumn.size());
			jsonObject.add("rows", jsonArray);
			return jsonObject;
		}
	
	public void CreateXml(Connection conn,String bizid,JsonArray jaKHMapColumns,JsonArray jaJGMapColumns,JsonArray jaGridMapColumns) throws SQLException, DocumentException, IOException
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql="select xml from HASYS_DM_BIZADDSETTING where BusinessID="+bizid+"";
		stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		int count=0;
		String xml="";
		while(rs.next())
		{
			xml=rs.getString("xml");
			count+=1;
		}
		
		if (count==0) {
			// 閸掓稑缂揦ML閺傚洦銆傞弽锟�  
	        Document document = DocumentHelper.createDocument();  
	        // 閸掓稑缂撻弽纭呭Ν閻愮tems  
	        Element page = document.addElement("page");  
	        page.addAttribute("bizId",bizid );
	        page.addAttribute("pageType","Dial" );
	        
	        
	        // 閸掓稑缂撻弽纭呭Ν閻愰�涚瑓閻ㄥ埇tem鐎涙劘濡悙锟�  
	        Element CustDetailPage = page.addElement("CustDetailPage");
	        CustDetailPage.addAttribute("PageFile","DmBiz5_CustDetail" );
	        CustDetailPage.addAttribute("Template","dial/dial_template2.temp" );
	        CustDetailPage.addAttribute("Title","涓氬姟5_瀹㈡埛鎷ㄦ墦" );
	        
	        
	        
	        // item閼哄倻鍋ｉ張澶夎⒈娑擃亜鐡欓懞鍌滃仯  
	        Element WorkSheetDetailList = CustDetailPage.addElement("WorkSheetDetailList"); 
	        WorkSheetDetailList.addAttribute("PageItemType","Panel" );
	        
	        Create(bizid, jaKHMapColumns, WorkSheetDetailList, "澶栨嫧涓氬姟"+bizid+"瀹㈡埛浠诲姟宸ヤ綔琛�");
	        Create(bizid, jaJGMapColumns, WorkSheetDetailList, "缁撴灉琛�");
	        
	        Element DataGrid = CustDetailPage.addElement("DataGrid"); 
	        DataGrid.addAttribute("PageItemType","GridControl" );
	        DataGrid.addAttribute("Title","" );
	        for(int grid=0;grid<jaGridMapColumns.size();grid++)
	        {	
	        	JsonObject jso=jaGridMapColumns.get(grid).getAsJsonObject();
	        	jso.get("").getAsString();
		        Element Item = DataGrid.addElement("Item");
		        Item.addAttribute("ColumnName",jso.get("ColumnName").getAsString() );
		        Item.addAttribute("Operation",jso.get("Operation").getAsString() );
		        Item.addAttribute("Title",jso.get("Title").getAsString() );
		        Item.addAttribute("Width",jso.get("Width").getAsString() );
		        Item.addAttribute("WorkSheetColumnName",jso.get("WorkSheetColumnName").getAsString() );
	        }
	        // 鐠佸墽鐤哫ML閺傚洦銆傞弽鐓庣础
	        OutputFormat outputFormat = OutputFormat.createPrettyPrint();  
	       /* // 鐠佸墽鐤哫ML缂傛牜鐖滈弬鐟扮础,閸楄櫕妲搁悽銊﹀瘹鐎规氨娈戠紓鏍垳閺傜懓绱℃穱婵嗙摠XML閺傚洦銆傞崚鏉跨摟缁楋缚瑕�(String),鏉╂瑩鍣锋稊鐔峰讲娴犮儲瀵氱�规矮璐烥BK閹存牗妲窱SO8859-1  
	        outputFormat.setEncoding("UTF-8");
	        //outputFormat.setSuppressDeclaration(true); //閺勵垰鎯侀悽鐔堕獓xml婢讹拷
	        outputFormat.setIndent(true); //鐠佸墽鐤嗛弰顖氭儊缂傗晞绻�
	        outputFormat.setIndent("    "); //娴犮儱娲撴稉顏嗏敄閺嶅吋鏌熷蹇撶杽閻滄壆缂夋潻锟�
	        outputFormat.setNewlines(true); //鐠佸墽鐤嗛弰顖氭儊閹广垼顢�
*/	          
	        try {  
	            // stringWriter鐎涙顑佹稉鍙夋Ц閻€劍娼垫穱婵嗙摠XML閺傚洦銆傞惃锟�  
	            StringWriter stringWriter = new StringWriter();  
	            // xmlWriter閺勵垳鏁ら弶銉﹀ΩXML閺傚洦銆傞崘娆忓弳鐎涙顑佹稉鑼畱(瀹搞儱鍙�)  
	            XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);  
	              
	            // 閹跺﹤鍨卞鍝勩偨閻ㄥ垕ML閺傚洦銆傞崘娆忓弳鐎涙顑佹稉锟�  
	            xmlWriter.write(document);
	              
	            // 閹垫挸宓冪�涙顑佹稉锟�,閸楄櫕妲竂ML閺傚洦銆�  
	            String insertxml=stringWriter.toString();
	            
	            xmlWriter.close();
	            
	            String insertsql = "INSERT INTO HASYS_DM_BIZADDSETTING (BusinessId,XML) values("+bizid+",'"+insertxml+"')";
	            stmt = conn.prepareStatement(insertsql);
	            stmt.executeUpdate();
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }      
		}
	}
	
	private Element Create(String bizid,JsonArray jaMapColumns,Element WorkSheetDetailList,String Name){

        Element WorkSheetDetail = WorkSheetDetailList.addElement("WorkSheetDetail"); 
        WorkSheetDetail.addAttribute("GroupBoxName",Name );
        WorkSheetDetail.addAttribute("GroupBoxTitle",Name );
        WorkSheetDetail.addAttribute("HasVisibleItem","1" );
        WorkSheetDetail.addAttribute("PageItemType","ControlPanel" );
        WorkSheetDetail.addAttribute("WorkSheetId","165" );
        
        Element FlowLayoutSetting = WorkSheetDetail.addElement("FlowLayoutSetting");
        FlowLayoutSetting.addAttribute("ColumnCount","4" );
        FlowLayoutSetting.addAttribute("IsPreFixHasColon","1" );
        FlowLayoutSetting.addAttribute("IsRatioCalculate","0" );
        FlowLayoutSetting.addAttribute("PageItemType","ControlPanel" );
        FlowLayoutSetting.addAttribute("RowHeight","27" );
        FlowLayoutSetting.addAttribute("RowPedding","4" );
        
        for(int co=0;co<4;co++)
	    {
	        Element Col = FlowLayoutSetting.addElement("Col");
	        Col.addAttribute("ColWidth","246" );
	        Col.addAttribute("PostFixWidth","0" );
	        Col.addAttribute("PreFixIndent","80" );
        }
         
        Element FlowLayoutItems = WorkSheetDetail.addElement("FlowLayoutItems");
        
        for(int it=0;it<jaMapColumns.size();it++)
        {
        	JsonObject jso=jaMapColumns.get(it).getAsJsonObject();
        	
	        Element Item = FlowLayoutItems.addElement("Item");
	        Item.addAttribute("ColumnName",jso.get("ColumnName").getAsString());
	        Item.addAttribute("ComboboxOptions",jso.get("ComboboxOptions").getAsString());
	        Item.addAttribute("ControlNameOptr",jso.get("ControlNameOptr").getAsString() );
	        Item.addAttribute("ControlType",jso.get("ControlType").getAsString());
	        Item.addAttribute("IsDisabled",jso.get("IsDisabled").getAsString());
	        Item.addAttribute("IsFromFirstCol",jso.get("IsFromFirstCol").getAsString());
	        Item.addAttribute("IsIncludeDialButton",jso.get("IsIncludeDialButton").getAsString());
	        Item.addAttribute("IsMustFill",jso.get("IsMustFill").getAsString());
	        Item.addAttribute("IsReadOnly",jso.get("IsReadOnly").getAsString());
	        Item.addAttribute("IsVisible",jso.get("IsVisible").getAsString());
	        Item.addAttribute("Length",jso.get("Length").getAsString());
	        Item.addAttribute("Mask",jso.get("Mask").getAsString());
	        Item.addAttribute("OccupyColCount",jso.get("OccupyColCount").getAsString());
	        Item.addAttribute("OccupyRowCount",jso.get("OccupyRowCount").getAsString());
	        Item.addAttribute("PostfixText",jso.get("PostfixText").getAsString());
	        Item.addAttribute("PrefixText",jso.get("PrefixText").getAsString());
	        Item.addAttribute("WorkSheetColumnNameCh",jso.get("WorkSheetColumnNameCh").getAsString());
        }
        return WorkSheetDetailList;
	}
	

}
