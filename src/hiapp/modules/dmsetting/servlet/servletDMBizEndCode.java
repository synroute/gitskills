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

import dm.setting.dbLayer.DMEndCode;
import dm.setting.dbLayer.DMEndCodeManager;
import hiapp.system.session.Tenant;
import hiapp.utils.ConstantResultType;
import hiapp.utils.UtilServlet;

/**
 * Servlet implementation class servletDMBizEndCode
 */
@WebServlet("/servletDMBizEndCode")
public class servletDMBizEndCode extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMBizEndCode() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session=request.getSession(false);
		
		Connection dbConn = null;
		PrintWriter printWriter = response.getWriter();
		try {
			Tenant tenant = (Tenant)session.getAttribute("tenant");
			dbConn= tenant.getDbConnectionConfig().getDbConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}catch(NullPointerException e)
		{
			JsonObject jsonObject =  new JsonObject();
			jsonObject.addProperty("returnCode", "2");
			jsonObject.addProperty("returnMessage","超时");
			printWriter.print(jsonObject.toString());
			return;
		}
		String param=request.getParameter("param");
		 JsonObject returnData = new JsonParser().parse(request.getParameter("param")).getAsJsonObject(); 
		//UtilServlet utilServlet=new UtilServlet(request, response);
		 String szActionValue = returnData.get("action").getAsString();
		if(szActionValue.equals("getAllEndCodes")){
			try {
				String bizId=returnData.get("bizId").getAsString();
				Boolean Issuccess= getAllEndCodes(dbConn, Integer.parseInt(bizId));
				 String results="";
				 JsonObject JsonObject = new JsonObject();
					
					
				if(Issuccess)
				{
					JsonObject.addProperty("returnCode", "0");
					JsonObject.addProperty("returnMessage","鎴愬姛");
				}else
				{
					JsonObject.addProperty("returnCode", "1");
					JsonObject.addProperty("returnMessage","澶辫触");
				}
				printWriter.print(JsonObject.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("submitEndCodes")){
			try {
				String bizId=returnData.get("bizId").getAsString();
				String szEndCodes=returnData.get("jaEndCodes").getAsString();
				JsonParser jsonParser=new JsonParser();
				JsonArray jsonArray=jsonParser.parse(szEndCodes).getAsJsonArray();
				Boolean Issuccess=submitEndCodes(dbConn, Integer.parseInt(bizId),jsonArray);
				String results="";	
				JsonObject JsonObject = new JsonObject();
				
				
				if(Issuccess)
				{
					JsonObject.addProperty("returnCode", "0");
					JsonObject.addProperty("returnMessage","成功");
				}else
				{
					JsonObject.addProperty("returnCode", "1");
					JsonObject.addProperty("returnMessage","失败");
				}
				printWriter.print(JsonObject.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
			}
		}else if(szActionValue.equals("add")){
			String bizid=returnData.get("bizId").getAsString();
			 String CodeType=returnData.get("codeType").getAsString();
			 String Code=returnData.get("code").getAsString();
			 String decription="";
			 if(returnData.has("decription"))
			 {
			   decription=returnData.get("decription").getAsString();
			 }
			 JsonObject jsonObject;
			try {
				jsonObject = addCodeType(dbConn,bizid,CodeType,Code,decription);
				printWriter.print(jsonObject.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(szActionValue.equals("delete")){
			String bizid=returnData.get("bizId").getAsString();
			 String CodeType=returnData.get("codeType").getAsString();
			 String Code=returnData.get("code").getAsString();
			 JsonObject jsonObject;
			try {
				jsonObject = deleteCodeType(dbConn,bizid,CodeType,Code);
				printWriter.print(jsonObject.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(szActionValue.equals("modify")){
			String bizid=returnData.get("bizId").getAsString();
			String CodeType=returnData.get("codeType").getAsString();
			 String Code=returnData.get("code").getAsString();
			 String decription="";
			 if(returnData.has("decription"))
			 {
			   decription=returnData.get("decription").getAsString();
			 }
			 String CodeType_old=returnData.get("codeType_old").getAsString();
			 String Code_old=returnData.get("code_old").getAsString();
			 JsonObject jsonObject;
			try {
				jsonObject = modifyCodeType(dbConn,bizid,CodeType,Code,decription,CodeType_old,Code_old);
				printWriter.print(jsonObject.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (szActionValue.equals("getall")) {
			 String bizid=returnData.get("bizId").getAsString();
			 String code="";
			 if(returnData.has("code"))
			 {
				 code=returnData.get("code").getAsString();
			 }
			 JsonObject jsonObject;
			jsonObject = GetAllEndCode(dbConn,bizid,code);
			printWriter.print(jsonObject.toString());
				
		}
		
		//utilServlet.Response();
		//utilServlet.Close();
	}
	public JsonObject addCodeType(Connection conn,String bizid,String CodeType,String Code,String decription) throws SQLException, DocumentException, IOException
	{
		PreparedStatement stmt = null;
		String sql = "INSERT INTO HASYS_DM_BIZENDCODE (BusinessId,CodeType,Code,Description) values("+bizid+",'"+CodeType+"','"+Code+"','"+decription+"')";
       stmt = conn.prepareStatement(sql);
       int i = stmt.executeUpdate();
       add(conn, bizid, CodeType, Code);
       JsonObject JsonObject = new JsonObject();
       if(i>0)
		{
			JsonObject.addProperty("returnCode", "0");
			JsonObject.addProperty("returnMessage","鎴愬姛");
		}else
		{
			JsonObject.addProperty("returnCode", "1");
			JsonObject.addProperty("returnMessage","澶辫触");
		}
       return JsonObject;
	}
	
	
	public JsonObject modifyCodeType(Connection conn,String bizid,String CodeType,String Code,String decription,String codetype_old,String code_old) throws SQLException, DocumentException, IOException
	{
		PreparedStatement stmt = null;
		String sql = "update HASYS_DM_BIZENDCODE set CodeType='"+CodeType+"',Code='"+Code+"',Description='"+decription+"' where BusinessId="+bizid+" and CodeType='"+codetype_old+"' and Code='"+code_old+"'";
       stmt = conn.prepareStatement(sql);
       int i = stmt.executeUpdate();
       modift(conn, bizid, CodeType, Code,codetype_old,code_old);
       JsonObject JsonObject = new JsonObject();
       if(i>0)
		{
			JsonObject.addProperty("returnCode", "0");
			JsonObject.addProperty("returnMessage","成功");
		}else
		{
			JsonObject.addProperty("returnCode", "1");
			JsonObject.addProperty("returnMessage","失败");
		}
       return JsonObject;
	}
	
	public void modift(Connection conn,String bizid,String EndCodeType,String EndCode,String codetype_old,String code_old ) throws SQLException, DocumentException{
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
		
		Document document = DocumentHelper.parseText(xml);
		Element rootElt = document.getRootElement(); // 获取根节点

		Iterator iter = rootElt.elementIterator("EndCodeRedialStrategy"); 
		  while (iter.hasNext()) {
			  Element recordEle = (Element) iter.next();
			  Iterator iters = recordEle.elementIterator("Item");
			  while (iters.hasNext()) {
				Element ItemElement = (Element) iters.next();
				if(ItemElement.attributeValue("EndCode").equals(code_old)&&ItemElement.attributeValue("EndCodeType").equals(codetype_old))
				{
		  	        ItemElement.setAttributeValue("EndCodeType",EndCodeType );
		  	        ItemElement.setAttributeValue("EndCode",EndCode );
				}
			  }
		  }
			  // 设置XML文档格式
	        OutputFormat outputFormat = OutputFormat.createPrettyPrint();  
	       
	        try {  
	            // stringWriter字符串是用来保存XML文档的  
	            StringWriter stringWriter = new StringWriter();  
	            // xmlWriter是用来把XML文档写入字符串的(工具)  
	            XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);  
	              
	            // 把创建好的XML文档写入字符串  
	            xmlWriter.write(document);
	              
	            // 打印字符串,即是XML文档  
	            String updatexml=stringWriter.toString();
	            
	              
	            xmlWriter.close();
	            
	            String updatesql = "update HASYS_DM_BIZADDSETTING set XML='"+updatexml+"' where BusinessId="+bizid+"";
	            stmt = conn.prepareStatement(updatesql);
	            int i=stmt.executeUpdate();
	            
	            
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }      
	}
	
	
	public void add(Connection conn,String bizid,String EndCodeType,String EndCode) throws SQLException, DocumentException, IOException
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
			// 鍒涘缓XML鏂囨。鏍�  
	        Document document = DocumentHelper.createDocument();  
	        // 鍒涘缓鏍硅妭鐐筰tems  
	        Element AddSetting = document.addElement("AddSetting");  
	        // 鍒涘缓鏍硅妭鐐逛笅鐨刬tem瀛愯妭鐐�  
	        Element EndCodeRedialStrategy = AddSetting.addElement("EndCodeRedialStrategy");  
	        // item鑺傜偣鏈変袱涓瓙鑺傜偣  
	        Element ItemElement = EndCodeRedialStrategy.addElement("Item");
	        ItemElement.addAttribute("EndCodeType",EndCodeType );
	        ItemElement.addAttribute("EndCode",EndCode );
	        ItemElement.addAttribute("IsCustStop","" );
	        ItemElement.addAttribute("IsPhoneStop","" );
	        ItemElement.addAttribute("RedialMinutes","" );
	        ItemElement.addAttribute("RedialCount","" );
	         
	        // 璁剧疆XML鏂囨。鏍煎紡
	        OutputFormat outputFormat = OutputFormat.createPrettyPrint();  
	       /* // 璁剧疆XML缂栫爜鏂瑰紡,鍗虫槸鐢ㄦ寚瀹氱殑缂栫爜鏂瑰紡淇濆瓨XML鏂囨。鍒板瓧绗︿覆(String),杩欓噷涔熷彲浠ユ寚瀹氫负GBK鎴栨槸ISO8859-1  
	        outputFormat.setEncoding("UTF-8");
	        //outputFormat.setSuppressDeclaration(true); //鏄惁鐢熶骇xml澶�
	        outputFormat.setIndent(true); //璁剧疆鏄惁缂╄繘
	        outputFormat.setIndent("    "); //浠ュ洓涓┖鏍兼柟寮忓疄鐜扮缉杩�
	        outputFormat.setNewlines(true); //璁剧疆鏄惁鎹㈣
*/	          
	        try {  
	            // stringWriter瀛楃涓叉槸鐢ㄦ潵淇濆瓨XML鏂囨。鐨�  
	            StringWriter stringWriter = new StringWriter();  
	            // xmlWriter鏄敤鏉ユ妸XML鏂囨。鍐欏叆瀛楃涓茬殑(宸ュ叿)  
	            XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);  
	              
	            // 鎶婂垱寤哄ソ鐨刋ML鏂囨。鍐欏叆瀛楃涓�  
	            xmlWriter.write(document);
	              
	            // 鎵撳嵃瀛楃涓�,鍗虫槸XML鏂囨。  
	            String insertxml=stringWriter.toString();
	            
	            xmlWriter.close();
	            
	            String insertsql = "INSERT INTO HASYS_DM_BIZADDSETTING (BusinessId,XML) values("+bizid+",'"+insertxml+"')";
	            stmt = conn.prepareStatement(insertsql);
	            stmt.executeUpdate();
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }      
		}else
		{
			
			Document document = DocumentHelper.parseText(xml);
			Element rootElt = document.getRootElement(); // 鑾峰彇鏍硅妭鐐�
			
			Iterator iter = rootElt.elementIterator("EndCodeRedialStrategy"); 
			  while (iter.hasNext()) {
				  Element recordEle = (Element) iter.next();
				  Element ItemElement = recordEle.addElement("Item");
	    	        ItemElement.addAttribute("EndCodeType",EndCodeType );
	    	        ItemElement.addAttribute("EndCode",EndCode );
	    	        ItemElement.addAttribute("IsCustStop","" );
	    	        ItemElement.addAttribute("IsPhoneStop","" ); 	
	    	        ItemElement.addAttribute("RedialMinutes","" );
	    	        ItemElement.addAttribute("RedialCount","" );	
			  }
			
			
			  // 璁剧疆XML鏂囨。鏍煎紡
	        OutputFormat outputFormat = OutputFormat.createPrettyPrint();  
	       /* // 璁剧疆XML缂栫爜鏂瑰紡,鍗虫槸鐢ㄦ寚瀹氱殑缂栫爜鏂瑰紡淇濆瓨XML鏂囨。鍒板瓧绗︿覆(String),杩欓噷涔熷彲浠ユ寚瀹氫负GBK鎴栨槸ISO8859-1  
	        outputFormat.setEncoding("UTF-8");
	        //outputFormat.setSuppressDeclaration(true); //鏄惁鐢熶骇xml澶�
	        outputFormat.setIndent(true); //璁剧疆鏄惁缂╄繘
	        outputFormat.setIndent("    "); //浠ュ洓涓┖鏍兼柟寮忓疄鐜扮缉杩�
	        outputFormat.setNewlines(true); //璁剧疆鏄惁鎹㈣
*/	          
	        try {  
	            // stringWriter瀛楃涓叉槸鐢ㄦ潵淇濆瓨XML鏂囨。鐨�  
	            StringWriter stringWriter = new StringWriter();  
	            // xmlWriter鏄敤鏉ユ妸XML鏂囨。鍐欏叆瀛楃涓茬殑(宸ュ叿)  
	            XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);  
	              
	            // 鎶婂垱寤哄ソ鐨刋ML鏂囨。鍐欏叆瀛楃涓�  
	            xmlWriter.write(document);
	              
	            // 鎵撳嵃瀛楃涓�,鍗虫槸XML鏂囨。  
	            String updatexml=stringWriter.toString();
	            
	              
	            xmlWriter.close();
	            
	            String updatesql = "update HASYS_DM_BIZADDSETTING set XML='"+updatexml+"' where BusinessId="+bizid+"";
	            stmt = conn.prepareStatement(updatesql);
	            stmt.executeUpdate();
	            
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }      
			
			
			
		}
	}
	
	
	public JsonObject deleteCodeType(Connection conn,String bizid,String CodeType,String Code) throws SQLException
	{
		PreparedStatement stmt = null;
		String sql = "delete from HASYS_DM_BIZENDCODE where BusinessId="+bizid+" and CodeType='"+CodeType +"' and Code='"+Code+"'";
       stmt = conn.prepareStatement(sql);
       int i = stmt.executeUpdate();
       JsonObject JsonObject = new JsonObject();
       if(i>0)
		{
			JsonObject.addProperty("returnCode", "0");
			JsonObject.addProperty("returnMessage","鎴愬姛");
		}else
		{
			JsonObject.addProperty("returnCode", "1");
			JsonObject.addProperty("returnMessage","澶辫触");
		}
       return JsonObject;
	}
	
	
	public JsonObject GetAllEndCode(Connection conn,String bizid,String Code)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JsonArray JsonArray = new JsonArray();
		JsonObject JsonObject = new JsonObject();
		try {
			String sql="select * from HASYS_DM_BIZENDCODE where BUSINESSID="+bizid+"";
			if(!Code.equals(""))
			{
				sql=sql+" and Code like'%"+Code+"%'";
			}
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			int count=0;
			while(rs.next())
			{
				JsonObject JsonObject_rows = new JsonObject();
				JsonObject_rows.addProperty("bizId", rs.getString("BUSINESSID"));
				JsonObject_rows.addProperty("codeType", rs.getString("CODETYPE"));
				JsonObject_rows.addProperty("code", rs.getString("CODE"));
				JsonObject_rows.addProperty("decription", rs.getString("DESCRIPTION"));
				JsonArray.add(JsonObject_rows);	
				count += 1;
			}
			 /*JsonObject.addProperty("returnCode", "0");
			JsonObject.addProperty("returnMessage","成功");*/
			JsonObject.addProperty("total", count);
			JsonObject.add("rows", JsonArray);
			
		} catch (SQLException e) {
			e.printStackTrace();	
			JsonObject.addProperty("returnCode", "1");
			JsonObject.addProperty("returnMessage","失败");
			JsonObject.addProperty("total", 0);
			JsonObject.add("rows", JsonArray);
		} finally {
			
		}
		
		return JsonObject;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static boolean getAllEndCodes(Connection dbConn,int bizId) throws Exception{
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<DMEndCode> listEndCode=new ArrayList<DMEndCode>();
		/*if(!DMEndCodeManager.getAllEndCodes(dbConn, bizId,listEndCode)){
			return false;
		}*/
		
		JsonObject jsonObject=new JsonObject() ;
		JsonArray jsonArray=new JsonArray();
		for(int ii=0;ii<listEndCode.size();ii++){
			DMEndCode dmEndCode=listEndCode.get(ii);
			JsonObject jsItem=new JsonObject();
			jsItem.addProperty("EndCodeType", dmEndCode.getEndCodeType());
			jsItem.addProperty("EndCode", dmEndCode.getEndCode());
			jsItem.addProperty("EndCodeDescription", dmEndCode.getDescription());
			jsonArray.add(jsItem);
		}
		jsonObject.addProperty("total", listEndCode.size());
		jsonObject.add("rows", jsonArray);
		//utilServlet.setResultCode(0);
		//utilServlet.setResultMessage("閹存劕濮�");

		return true;
		
	}
	public static boolean submitEndCodes(Connection dbConn,int bizId,JsonArray jaEndCodes) throws Exception{
		
		/*if(!DMEndCodeManager.submitEndCodes(dbConn, bizId, jaEndCodes)){
			return false;
		}*/
		

		return true;
		
	}

}
