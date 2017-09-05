package hiapp.modules.dmsetting.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.system.session.Tenant;

/**
 * Servlet implementation class servletDMADDSetting
 */
@WebServlet("/servletDMADDSetting")
public class servletDMADDSetting extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMADDSetting() {
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
		
		try {
			Tenant tenant = (Tenant)session.getAttribute("tenant");
			dbConn= tenant.getDbConnectionConfig().getDbConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JsonObject jsonObject =  new JsonObject();
			jsonObject.addProperty("returnCode", "2");
			jsonObject.addProperty("returnMessage","超时");
			printWriter.print(jsonObject.toString());
			return;
		}catch(NullPointerException e)
		{
			JsonObject jsonObject =  new JsonObject();
			jsonObject.addProperty("returnCode", "2");
			jsonObject.addProperty("returnMessage","超时");
			printWriter.print(jsonObject.toString());
			return;
		}
		
		JsonObject returnData = new JsonParser().parse(request.getParameter("param")).getAsJsonObject();
		String szActionValue = returnData.get("action").getAsString();
		 if (szActionValue.equals("modify")) {
			 String bizid=returnData.get("bizId").getAsString();
			 String codeType=returnData.get("codeType").getAsString();
			 String Code=returnData.get("Code").getAsString();
			 String noRedial_C=returnData.get("noRedial_C").getAsString();
			 String noRedial_P=returnData.get("noRedial_P").getAsString();
			 String interval=returnData.get("interval").getAsString();
			 String redialTimes=returnData.get("redialTimes").getAsString();
			 
			try {
				JsonObject jsonObject = modify(dbConn,bizid,codeType,Code,noRedial_C,noRedial_P,interval,redialTimes);
				printWriter.print(jsonObject.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
	}
	
	public JsonObject modify(Connection conn,String bizid,String EndCodeType,String EndCode,String IsCustStop,String IsPhoneStop,String RedialMinutes,String RedialCount) throws SQLException, DocumentException
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JsonObject JsonObject = new JsonObject();
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
				if(ItemElement.attributeValue("EndCode").equals(EndCode))
				{
		  	        ItemElement.setAttributeValue("EndCodeType",EndCodeType );
		  	        ItemElement.setAttributeValue("EndCode",EndCode );
		  	        ItemElement.setAttributeValue("IsCustStop",IsCustStop );
		  	        ItemElement.setAttributeValue("IsPhoneStop",IsPhoneStop); 	
		  	        ItemElement.setAttributeValue("RedialMinutes",RedialMinutes);
		  	        ItemElement.setAttributeValue("RedialCount",RedialCount);
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
	            if(i>0)
	    		{
	    			JsonObject.addProperty("returnCode", "0");
	    			JsonObject.addProperty("returnMessage","成功");
	    		}else
	    		{
	    			JsonObject.addProperty("returnCode", "1");
	    			JsonObject.addProperty("returnMessage","失败");
	    		}
	            
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }      
	        return JsonObject;
			
			
		
	}
	
}
