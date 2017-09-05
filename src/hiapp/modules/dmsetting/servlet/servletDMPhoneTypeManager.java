package hiapp.modules.dmsetting.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.system.session.Tenant;

/**
 * Servlet implementation class servletDMPhoneTypeManager
 */
@WebServlet("/servletDMPhoneTypeManager")
public class servletDMPhoneTypeManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMPhoneTypeManager() {
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
		String param=request.getParameter("param");
		JsonObject returnData = new JsonParser().parse(request.getParameter("param")).getAsJsonObject(); 
		//UtilServlet utilServlet=new UtilServlet(request, response);
		 String szActionValue = returnData.get("action").getAsString();
		 if (szActionValue.equals("add")) {
			 String bizid=returnData.get("bizId").getAsString();
			 String name=returnData.get("name").getAsString();
			 String nameCh=returnData.get("nameCh").getAsString();
			 String decription="";String fieldMap="";
			 if(returnData.has("decription")) {
				 decription=returnData.get("decription").getAsString();
			}
			 if(returnData.has("fieldMap")) {
				 fieldMap=returnData.get("fieldMap").getAsString();
			}
			 
			 String dialOrder=returnData.get("dialOrder").getAsString();
			 JsonObject jsonObject;
			try {
				jsonObject = addPhoneType(dbConn,bizid,name,nameCh,decription,fieldMap,dialOrder);
				printWriter.print(jsonObject.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}else if (szActionValue.equals("delete")) {
			String bizid=returnData.get("bizId").getAsString();
			 JsonObject jsonObject;
			try {
				jsonObject = deletePhoneType(dbConn,bizid);
				printWriter.print(jsonObject.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}else if (szActionValue.equals("modify")) {
			 String bizid=returnData.get("bizId").getAsString();
			 String name=returnData.get("name").getAsString();
			 String nameCh=returnData.get("nameCh").getAsString();
			 String decription=returnData.get("decription").getAsString();
			 String fieldMap=returnData.get("fieldMap").getAsString();
			 String dialOrder=returnData.get("dialOrder").getAsString();
			 String name_old=returnData.get("name_old").getAsString();
			 String namech_old=returnData.get("namech_old").getAsString();
			 JsonObject jsonObject;
			try {
				jsonObject = modifyPhoneType(dbConn,bizid,name,nameCh,decription,fieldMap,dialOrder,name_old,namech_old);
				printWriter.print(jsonObject.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}else if (szActionValue.equals("getall")) {
			 String bizid=returnData.get("bizId").getAsString();
			 String namech="";
			 if(returnData.has("namech"))
			 {
				 namech=returnData.get("namech").getAsString();
			 }
			 JsonObject jsonObject;
			jsonObject = GetAllPhoneType(dbConn,bizid,namech);
			printWriter.print(jsonObject.toString());
				
		}
	}
	
	public JsonObject addPhoneType(Connection conn,String bizid,String name,String nameCh,String decription,String fieldMap,String dialOrder) throws SQLException
	{
		PreparedStatement stmt = null;
		String sql = "insert into HASYS_DM_BIZPHONETYPE (BUSINESSID,name,nameCh,decription,CUSTOMERCOLUMNMAP,dialOrder) values("+bizid+",'"+name+"','"+nameCh+"','"+decription+"','"+fieldMap+"',"+dialOrder+")";
        stmt = conn.prepareStatement(sql);
        int i = stmt.executeUpdate();
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
	
	public JsonObject deletePhoneType(Connection conn,String bizid) throws SQLException
	{
		PreparedStatement stmt = null;
		String sql = "delete from HASYS_DM_BIZPHONETYPE where BUSINESSID="+bizid+"";
        stmt = conn.prepareStatement(sql);
        int i = stmt.executeUpdate();
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
	
	public JsonObject modifyPhoneType(Connection conn,String bizid,String name,String nameCh,String decription,String fieldMap,String dialOrder,String name_old,String namech_old) throws SQLException
	{
		PreparedStatement stmt = null;
		String sql = "update HASYS_DM_BIZPHONETYPE set name='"+name+"',nameCh='"+nameCh+"',decription='"+decription+"',CUSTOMERCOLUMNMAP='"+fieldMap+"',dialOrder='"+dialOrder+"' where BUSINESSID="+bizid+" and name='"+name_old+"' and nameCh='"+namech_old+"'";
        stmt = conn.prepareStatement(sql);
        int i = stmt.executeUpdate();
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
	
	public JsonObject GetAllPhoneType(Connection conn,String bizid,String namech)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JsonArray JsonArray = new JsonArray();
		JsonObject JsonObject = new JsonObject();
		try {
			String sql="select * from HASYS_DM_BIZPHONETYPE where BUSINESSID="+bizid+"";
			if(!namech.equals(""))
			{
				sql=sql+" and nameCh like'%"+namech+"%'";
			}
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			int count=0;
			while(rs.next())
			{
				JsonObject JsonObject_rows = new JsonObject();
				JsonObject_rows.addProperty("bizId", rs.getString("BUSINESSID"));
				JsonObject_rows.addProperty("name", rs.getString("Name"));
				JsonObject_rows.addProperty("nameCh", rs.getString("NameCh"));
				JsonObject_rows.addProperty("decription", rs.getString("Decription"));
				JsonObject_rows.addProperty("fieldMap", rs.getString("CUSTOMERCOLUMNMAP"));
				JsonObject_rows.addProperty("dialOrder", rs.getString("DialOrder"));
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
}
