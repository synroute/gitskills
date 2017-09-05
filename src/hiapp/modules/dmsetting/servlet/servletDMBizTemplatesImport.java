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
import com.google.gson.JsonParser;

import dmNew.setting.dbLayer.DMBizTemplateImport;
import dmNew.setting.dbLayer.DMBizTemplatesImport;
import dmNew.setting.dbLayer.DMEndCode;
import dmNew.setting.dbLayer.DMEndCodeManager;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import hiapp.system.session.Tenant;
import hiapp.utils.ConstantResultType;
import hiapp.utils.UtilServlet;

/**
 * Servlet implementation class servletDMBizTemplatesImport
 */
@WebServlet("/servletDMBizTemplatesImport")
public class servletDMBizTemplatesImport extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMBizTemplatesImport() {
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
		
		Tenant tenant = (Tenant)session.getAttribute("tenant");
		try {
			dbConn= tenant.getDbConnectionConfig().getDbConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PrintWriter printWriter = response.getWriter();
		ServiceResult Result = new ServiceResult();
		String szActionValue = request.getParameter("action");
		if(szActionValue.equals("getAllTemplates")){
			try {
				String bizId=request.getParameter("BizId");
				JsonObject jsonObject= getAllTemplates(dbConn, Integer.parseInt(bizId));
				printWriter.print(jsonObject.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("newTemplate")){
			try {
				String bizId=request.getParameter("BizId");
				String templateId=request.getParameter("TemplateID");
				String name=request.getParameter("Name");
				String description=request.getParameter("Description");
				String IsDefault=request.getParameter("IsDefault");
				String SourceType=request.getParameter("SourceType");
				newTemplate(Result,dbConn, Integer.parseInt(bizId), Integer.parseInt(templateId), name, description, Integer.parseInt(IsDefault), SourceType);
				printWriter.print(Result.toJson());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("destroyTemplate")){
			try {
				String bizId=request.getParameter("BizId");
				String templateId=request.getParameter("TemplateID");
				destroyTemplate(Result,dbConn, Integer.parseInt(bizId), Integer.parseInt(templateId));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public static JsonObject getAllTemplates(Connection dbConn,int bizId){
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<DMBizTemplateImport> listTemplate=new ArrayList<DMBizTemplateImport>();
		JsonObject jsonObject=new JsonObject();
		if(!DMBizTemplatesImport.getAllTemplates(dbConn, bizId, listTemplate)){
			return jsonObject;
		}
		
		
		JsonArray jsonArray=new JsonArray();
		for(int ii=0;ii<listTemplate.size();ii++){
			DMBizTemplateImport dmBizTemplateImport=listTemplate.get(ii);
			JsonObject jsItem=new JsonObject();
			jsItem.addProperty("TemplateID", dmBizTemplateImport.getTemplateId());
			jsItem.addProperty("Name", dmBizTemplateImport.getName());
			jsItem.addProperty("Description", dmBizTemplateImport.getDescription());
			jsItem.addProperty("IsDefault", dmBizTemplateImport.isDefault());
			jsItem.addProperty("SourceType", dmBizTemplateImport.getSourceType());
			jsonArray.add(jsItem);
		}
		jsonObject.addProperty("total", listTemplate.size());
		jsonObject.add("rows", jsonArray);
		jsonObject.addProperty("returnCode", "0");
		jsonObject.addProperty("returnMessage","成功");
		return jsonObject;
	}
	public static boolean newTemplate(ServiceResult Result,Connection dbConn,int bizId,int templateID,String szName,String szDescription,int isDefault,String szSourceType){
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_NEW);
		if(!DMBizTemplatesImport.newTemplate(dbConn, bizId, templateID, szName, szDescription, isDefault, szSourceType)){
			return false;
		}
		
		Result.setResultCode(ServiceResultCode.SUCCESS);
		Result.setReturnMessage("新建成功");
		return true;
	}
	public static boolean modifyTemplate(UtilServlet utilServlet,int bizId,int templateID,String szName,String szDescription,int isDefault,String szSourceType){
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_MODIFY);
		if(!DMBizTemplatesImport.modifyTemplate(utilServlet, bizId, templateID, szName, szDescription, isDefault, szSourceType)){
			return false;
		}
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("修改成功");
		return true;
	}
	public static boolean destroyTemplate(ServiceResult Result,Connection dbConn,int bizId,int templateID){
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_DESTROY);
		if(!DMBizTemplatesImport.destroyTemplate(dbConn, bizId, templateID)){
			return false;
		}
		Result.setResultCode(ServiceResultCode.SUCCESS);
		Result.setReturnMessage("删除成功");
		return true;
	}

}
