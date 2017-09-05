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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.system.session.Tenant;

/**
 * Servlet implementation class servletDMHiDialerSetting
 */
@WebServlet("/servletDMHiDialerSetting")
public class servletDMHiDialerSetting extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMHiDialerSetting() {
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
		PrintWriter printWriter = response.getWriter();
		Connection dbConn = null;
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
		
		JsonObject returnData = new JsonParser().parse(request.getParameter("param")).getAsJsonObject(); 
		
		String szActionValue = returnData.get("action").getAsString();
		 if (szActionValue.equals("submit")) {
			 String bizid=returnData.get("bizId").getAsString();
			 String serviceNo=returnData.get("serviceNo").getAsString();
			 String dialPrefix=returnData.get("dialPrefix").getAsString();
			 String businessCode=returnData.get("businessCode").getAsString();
			 String IVRScript=returnData.get("IVRScript").getAsString();
			 String maxRingCount=returnData.get("maxRingCount").getAsString();
			 String dialRatio=returnData.get("dialRatio").getAsString();
			 String maxReadyAgentCount=returnData.get("maxReadyAgentCount").getAsString();
			 
			 String maxCallingCount=returnData.get("maxCallingCount").getAsString();
			 String dialMode=returnData.get("dialMode").getAsString();
			 String timeUnitLong=returnData.get("timeUnitLong").getAsString();
			 String maxCountPerTimeUnit=returnData.get("maxCountPerTimeUnit").getAsString();
			 JsonArray returnDatas =returnData.get("timeList").getAsJsonArray();
			 
			 
			 String timeStart = returnDatas.get(0).getAsJsonObject().get("timeStart").getAsString();
			 String timeEnd = returnDatas.get(0).getAsJsonObject().get("timeEnd").getAsString();
					 
			try {
				JsonObject jsonObject = submit(dbConn,bizid,serviceNo,dialPrefix,businessCode,IVRScript,maxRingCount,dialRatio,maxReadyAgentCount,maxCallingCount,dialMode,timeUnitLong,maxCountPerTimeUnit,timeStart,timeEnd);
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

	
	public JsonObject submit(Connection conn,String bizid,String serviceNo,String dialPrefix,String businessCode,String IVRScript,String maxRingCount,String dialRatio,String maxReadyAgentCount,String maxCallingCount,String dialMode,String timeUnitLong,String maxCountPerTimeUnit,String timeStart,String timeEnd) throws SQLException, DocumentException{

		PreparedStatement stmt = null;
		ResultSet rs = null;
		JsonObject JsonObject = new JsonObject();
		String sql="select DetailSettingXml from HASYS_DM_Business where ID="+bizid+"";
		stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		int count=0;
		String xml="";
		while(rs.next())
		{
			xml=rs.getString("DetailSettingXml");
		}
		Document document = DocumentHelper.parseText(xml);
		Element rootElt = document.getRootElement(); // 获取根节点
		if(rootElt.elementIterator("HiDialerSetting")!=null)
		{
		Iterator iter = rootElt.elementIterator("HiDialerSetting"); 
		  while (iter.hasNext()) {
			  Element recordEle = (Element) iter.next();

			  
			  recordEle.setAttributeValue("serviceNo",serviceNo );
			  recordEle.setAttributeValue("DialPrefix",dialPrefix );
			  recordEle.setAttributeValue("BusinessID",businessCode );
			  recordEle.setAttributeValue("IVRScript",IVRScript); 	
			  recordEle.setAttributeValue("MaxRingCount",maxRingCount);
			  recordEle.setAttributeValue("DialRatio",dialRatio);
			  recordEle.setAttributeValue("MaxReadyAgentCount",maxReadyAgentCount );
			  recordEle.setAttributeValue("MaxCallingCount",maxCallingCount );
			  recordEle.setAttributeValue("DialMode",dialMode);
			  recordEle.setAttributeValue("TimeUnitLong",timeUnitLong); 	
			  recordEle.setAttributeValue("MaxCountPerTimeUnit",maxCountPerTimeUnit);
			  Iterator iters = recordEle.elementIterator("PermissionCallTime");
				  while (iters.hasNext()) {
						Element ItemElement = (Element) iters.next();
						Iterator iterss = ItemElement.elementIterator("Item");
						while(iterss.hasNext())
						{
							 Element item = (Element) iterss.next();
							 recordEle.setAttributeValue("TimeStart",timeStart );
							 recordEle.setAttributeValue("TimeEnd",timeEnd );
						}
				  }
			  }
		  count+=1;
		  
		  }else{
			  Element recordEle = rootElt.addElement("HiDialerSetting");
			  recordEle.addAttribute("serviceNo",serviceNo );
			  recordEle.addAttribute("DialPrefix",dialPrefix );
			  recordEle.addAttribute("BusinessID",businessCode );
			  recordEle.addAttribute("IVRScript",IVRScript); 	
			  recordEle.addAttribute("MaxRingCount",maxRingCount);
			  recordEle.addAttribute("DialRatio",dialRatio);
			  recordEle.addAttribute("MaxReadyAgentCount",maxReadyAgentCount );
			  recordEle.addAttribute("MaxCallingCount",maxCallingCount );
			  recordEle.addAttribute("DialMode",dialMode);
			  recordEle.addAttribute("TimeUnitLong",timeUnitLong); 	
			  recordEle.addAttribute("MaxCountPerTimeUnit",maxCountPerTimeUnit);
			  
			  Element PermissionCallTime=recordEle.addElement("PermissionCallTime");
			  Element itemElement=PermissionCallTime.addElement("Item");
			  itemElement.addAttribute("TimeStart",timeStart );
			  itemElement.addAttribute("TimeEnd",timeEnd);
				
			
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
	            
	             String updatesql = "update HASYS_DM_Business set DetailSettingXml='"+updatexml+"' where ID="+bizid+"";
	            
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
