package hiapp.modules.dmsetting.servlet;

import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import hiapp.system.session.Tenant;
import hiapp.utils.DbUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dmNew.setting.dbLayer.DMWorkSheetManager;

/**
 * Servlet implementation class ServletDMWorkSheetModify
 */
@WebServlet("/ServletDMWorkSheetModify")
public class ServletDMWorkSheetModify extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDMWorkSheetModify() {
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
		ServiceResult serviceresult = new ServiceResult();
		String bizId=request.getParameter("bizId");
		String id= request.getParameter("id");
		String nameCh= request.getParameter("nameCh");
		String name= request.getParameter("name");
		String description=request.getParameter("description");
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode =DMWorkSheetRepository.modify(dbConn,Integer.parseInt(bizId),id,nameCh,name,description,errMessage); 
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				String errmessage1 = errMessage.toString();
				serviceresult.setReturnMessage(errMessage.toString());
			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("�޸Ĺ�����ɹ�");

			}
			PrintWriter printWriter = null;
			printWriter = response.getWriter();
			printWriter.write(serviceresult.toJson());
		} catch (Exception e) {
			serviceresult.setReturnCode(1);
			serviceresult.setReturnMessage("ʧ��");
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
		}
	}
}
