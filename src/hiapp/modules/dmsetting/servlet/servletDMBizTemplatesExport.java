package hiapp.modules.dmsetting.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dmNew.setting.dbLayer.DMBizTemplateExport;
import dmNew.setting.dbLayer.DMBizTemplateImport;
import dmNew.setting.dbLayer.DMBizTemplatesExport;
import dmNew.setting.dbLayer.DMBizTemplatesImport;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import hiapp.system.session.Tenant;
import hiapp.utils.ConstantResultType;
import hiapp.utils.UtilServlet;

/**
 * Servlet implementation class servletDMBizTemplatesExport
 */
@WebServlet("/servletDMBizTemplatesExport")
public class servletDMBizTemplatesExport extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMBizTemplatesExport() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);
		Connection dbConn = null; 
		
		String szBizId=request.getParameter("BizId");
		Tenant tenant = (Tenant)session.getAttribute("tenant");
		try {
			dbConn= tenant.getDbConnectionConfig().getDbConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ServiceResult Result = new ServiceResult();
		PrintWriter printWriter = response.getWriter();
		String szActionValue = request.getParameter("action");
		if(szActionValue.equals("getAllTemplates")){
			try {
				JsonObject jsonObject= getAllTemplates(dbConn,Integer.parseInt(szBizId));
				printWriter.print(jsonObject.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("newTemplate")){
			try {
				String szTemplateID=request.getParameter("ExportTemplateID");
				String szName=request.getParameter("Name");
				String szDescription=request.getParameter("Description");
				String szIsDefault=request.getParameter("IsDefault");
				newTemplate(Result,dbConn,Integer.parseInt(szBizId),Integer.parseInt(szTemplateID),szName,szDescription,Integer.parseInt(szIsDefault));
				
				printWriter.print(Result.toJson());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("modifyTemplate")){
			try {
				String szTemplateID=request.getParameter("TemplateID");
				String szName=request.getParameter("Name");
				String szDescription=request.getParameter("Description");
				String szIsDefault=request.getParameter("IsDefault");
				modifyTemplate(Result,dbConn,Integer.parseInt(szBizId),Integer.parseInt(szTemplateID),szName,szDescription,Integer.parseInt(szIsDefault));
				
				printWriter.print(Result.toJson());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(szActionValue.equals("destroyTemplate")){
			try {
				String szTemplateID=request.getParameter("TemplateID");
				destroyTemplate(Result,dbConn,Integer.parseInt(szBizId),Integer.parseInt(szTemplateID));
				
				printWriter.print(Result.toJson());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public static JsonObject getAllTemplates(Connection conn,int BizId){
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<DMBizTemplateExport> listTemplate=new ArrayList<DMBizTemplateExport>();
		JsonObject jsonObject=new JsonObject();
		if(!DMBizTemplatesExport.getAllTemplates(conn, BizId, listTemplate)){
			return jsonObject;
		}
		
		
		JsonArray jsonArray=new JsonArray();
		for(int ii=0;ii<listTemplate.size();ii++){
			DMBizTemplateExport dmBizTemplateExport=listTemplate.get(ii);
			JsonObject jsItem=new JsonObject();
			jsItem.addProperty("ExportTemplateID", dmBizTemplateExport.getTemplateID());
			jsItem.addProperty("ExportName", dmBizTemplateExport.getName());
			jsItem.addProperty("Description", dmBizTemplateExport.getDescription());
			jsItem.addProperty("IsDefault", dmBizTemplateExport.getIsDefault());
			jsonArray.add(jsItem);
		}
		jsonObject.addProperty("total", listTemplate.size());
		jsonObject.add("rows", jsonArray);
		jsonObject.addProperty("returnCode", "0");
		jsonObject.addProperty("returnMessage","成功");
		return jsonObject;
	}
	public static boolean newTemplate(ServiceResult Result,Connection conn,int bizId,int templateID,String name,String description,int isDefault){
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_NEW);
		if(!DMBizTemplatesExport.newTemplate(conn, bizId, templateID, name, description, isDefault)){
			return false;
		}
		
		Result.setResultCode(ServiceResultCode.SUCCESS);
		Result.setReturnMessage("新建成功");
		return true;
	}
	public static boolean modifyTemplate(ServiceResult Result,Connection conn,int bizId,int templateID,String name,String description,int isDefault){
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_MODIFY);
		if(!DMBizTemplatesExport.modifyTemplate(conn, bizId, templateID, name, description, isDefault)){
			return false;
		}
		
		Result.setResultCode(ServiceResultCode.SUCCESS);
		Result.setReturnMessage("修改成功");
		return true;
		
	}
	public static boolean destroyTemplate(ServiceResult Result,Connection conn,int bizId,int templateID){
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_MODIFY);
		if(!DMBizTemplatesExport.destroyTemplate(conn, bizId, templateID)){
			return false;
		}
		
		Result.setResultCode(ServiceResultCode.SUCCESS);
		Result.setReturnMessage("删除 成功");
		return true;
	}

}
