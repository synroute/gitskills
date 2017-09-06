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

import dmNew.setting.dbLayer.DMBusinessManager;

/**
 * Servlet implementation class ServletDMBusinessDestroy
 */
@WebServlet("/ServletDMBusinessDestroy")
public class ServletDMBusinessDestroy extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDMBusinessDestroy() {
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
		String id= request.getParameter("id");
		
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = DMBusinessRepository.destroyDMBusiness(dbConn, Integer.parseInt(id),errMessage);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage(errMessage.toString());

			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("删除业务成功！");
			}
			
			PrintWriter printWriter=null;
			printWriter = response.getWriter();
			printWriter.write(serviceresult.toJson());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
		}
	}
}
