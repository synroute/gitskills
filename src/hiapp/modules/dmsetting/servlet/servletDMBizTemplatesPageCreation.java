package hiapp.modules.dmsetting.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dm.setting.dbLayer.DMBizTemplatePageCreation;
import dm.setting.dbLayer.DMBizTemplatesPageCreation;
import hiapp.utils.ConstantResultType;
import hiapp.utils.UtilServlet;

/**
 * Servlet implementation class servletDMBizTemplatesPageCreation
 */
//@WebServlet("/servletDMBizTemplatesPageCreation")
public class servletDMBizTemplatesPageCreation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMBizTemplatesPageCreation() {
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
		UtilServlet utilServlet=new UtilServlet(request, response);
		String szBizId=request.getParameter("BizId");
		if(utilServlet.Action.equals("getAllTemplates")){
			try {
				getAllTemplates(utilServlet,Integer.parseInt(szBizId));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("newTemplate")){
			try {
				String szTemplateID=request.getParameter("TemplateID");
				String szName=request.getParameter("Name");
				String szDescription=request.getParameter("Description");
				newTemplate(utilServlet,Integer.parseInt(szBizId),Integer.parseInt(szTemplateID),szName,szDescription);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("modifyTemplate")){
			try {
				String szTemplateID=request.getParameter("TemplateID");
				String szName=request.getParameter("Name");
				String szDescription=request.getParameter("Description");
				modifyTemplate(utilServlet,Integer.parseInt(szBizId),Integer.parseInt(szTemplateID),szName,szDescription);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(utilServlet.Action.equals("destroyTemplate")){
			try {
				String szTemplateID=request.getParameter("TemplateID");
				destroyTemplate(utilServlet,Integer.parseInt(szBizId),Integer.parseInt(szTemplateID));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		utilServlet.Response();
		utilServlet.Close();
		
	}
	public static boolean getAllTemplates(UtilServlet utilServlet,int BizId){
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<DMBizTemplatePageCreation> listTemplate=new ArrayList<DMBizTemplatePageCreation>();
		if(!DMBizTemplatesPageCreation.getAllTemplates(utilServlet, BizId, listTemplate)){
			return false;
		}
		
		JsonObject jsonObject=utilServlet.getJoResult();
		JsonArray jsonArray=new JsonArray();
		for(int ii=0;ii<listTemplate.size();ii++){
			DMBizTemplatePageCreation dmBizTemplateExport=listTemplate.get(ii);
			JsonObject jsItem=new JsonObject();
			jsItem.addProperty("TemplateID", dmBizTemplateExport.getTemplateId());
			jsItem.addProperty("Name", dmBizTemplateExport.getName());
			jsItem.addProperty("Description", dmBizTemplateExport.getDescription());
			jsonArray.add(jsItem);
		}
		jsonObject.addProperty("total", listTemplate.size());
		jsonObject.add("rows", jsonArray);
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("成功");
		return true;
	}
	public static boolean newTemplate(UtilServlet utilServlet,int bizId,int templateID,String name,String description){
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_NEW);
		if(!DMBizTemplatesPageCreation.newTemplate(utilServlet, bizId, templateID, name, description)){
			return false;
		}
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("新建成功");
		return true;
	}
	public static boolean modifyTemplate(UtilServlet utilServlet,int bizId,int templateID,String name,String description){
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_MODIFY);
		if(!DMBizTemplatesPageCreation.modifyTemplate(utilServlet, bizId, templateID, name, description)){
			return false;
		}
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("修改成功");
		return true;
		
	}
	public static boolean destroyTemplate(UtilServlet utilServlet,int bizId,int templateID){
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_DESTROY);
		if(!DMBizTemplatesPageCreation.destroyTemplate(utilServlet, bizId, templateID)){
			return false;
		}
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("删除成功");
		return true;
	}

}
