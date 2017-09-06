package hiapp.modules.dmsetting.servlet;

import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import hiapp.system.session.Tenant;
import hiapp.utils.ConstantResultType;
import hiapp.utils.DbUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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

import dm.setting.dbLayer.BizPermissionItem;
import dm.setting.dbLayer.DMBizPermission;
import dmNew.setting.dbLayer.DMBusiness;
import dmNew.setting.dbLayer.DMBusinessManager;

/**
 * Servlet implementation class ServletDMBizPermissionColumns
 */
@WebServlet("/ServletDMBizPermissionColumns")
public class ServletDMBizPermissionColumns extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDMBizPermissionColumns() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);
		Tenant tenant = (Tenant)session.getAttribute("tenant");
		Connection dbConn = null;
		try {
			dbConn= tenant.getDbConnectionConfig().getDbConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		
		JsonArray jaColumns1=new JsonArray();
		JsonArray jaColumns2=new JsonArray();
        JsonObject jso=new JsonObject();
        jso.addProperty("field", "PermId");
        jso.addProperty("title", "权锟斤拷ID");
        jso.addProperty("width", 80);
        jaColumns1.add(jso);
        jso.addProperty("title", "权锟斤拷ID");
        jaColumns2.add(jso);
		
        jso=new JsonObject();
        jso.addProperty("field", "PermName");
        jso.addProperty("title", "权锟斤拷锟斤拷锟斤拷");
        jso.addProperty("width", 80);
        jaColumns1.add(jso);
        jso.addProperty("title", "权锟斤拷锟斤拷锟斤拷");
        jaColumns2.add(jso);
		

        List<DMBusiness> listDMBusiness=new ArrayList<DMBusiness>();
        DMBusinessRepository.getAllDMBusiness(dbConn, listDMBusiness);
        for(int ii=0;ii<listDMBusiness.size();ii++){
        	DMBusiness dmBusiness=listDMBusiness.get(ii);
        	jso=new JsonObject();
			String szLabel=String.format("%d:%s", dmBusiness.getId(),dmBusiness.getName());
			
			JsonObject jsBiz11=new JsonObject();
			jsBiz11.addProperty("field", "FSystemSetting");
			jsBiz11.addProperty("title", szLabel);
			jsBiz11.addProperty("width", 80);
			jsBiz11.addProperty("colspan", 3);
			jsBiz11.addProperty("align", "center");
			jaColumns1.add(jsBiz11);	
			
			JsonObject jsBiz21=new JsonObject();
			jsBiz21.addProperty("field", String.format("F%d_SystemSetting", dmBusiness.getId()));
			jsBiz21.addProperty("title", "系统锟斤拷锟斤拷");
			jsBiz21.addProperty("width", 80);
			jaColumns2.add(jsBiz21);	
			
			JsonObject jsBiz22=new JsonObject();
			jsBiz22.addProperty("field", String.format("F%d_DataManager", dmBusiness.getId()));
			jsBiz22.addProperty("title", "锟斤拷锟捷癸拷锟斤拷");
			jsBiz22.addProperty("width", 80);
			jaColumns2.add(jsBiz22);
			
			JsonObject jsBiz23=new JsonObject();
			jsBiz23.addProperty("field", String.format("F%d_Dial", dmBusiness.getId()));
			jsBiz23.addProperty("title", "锟解拨锟斤拷锟斤拷");
			jsBiz23.addProperty("width", 80);
			jaColumns2.add(jsBiz23);        
		}	
		ServiceResult serviceresult = new ServiceResult();
		StringBuffer errMessage = new StringBuffer();
        JsonObject jsonObject=new JsonObject();
		JsonArray jaaColumns=new JsonArray();
		jaaColumns.add(jaColumns1);
		jaaColumns.add(jaColumns2);
		jsonObject.add("Columns", jaaColumns);
		
			ServiceResultCode serviceResultCode = ServiceResultCode.SUCCESS;
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage(errMessage.toString());

			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("删锟斤拷业锟斤拷晒锟斤拷锟�");
			}
			
			PrintWriter printWriter=null;
			printWriter = response.getWriter();
			printWriter.write(jsonObject.toString());
	}
}
