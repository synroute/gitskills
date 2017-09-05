package hiapp.modules.dmsetting.servlet;

import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import hiapp.system.session.Tenant;
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

import dmNew.setting.dbLayer.DMBusiness;
import dmNew.setting.dbLayer.DMBusinessManager;
import dmNew.setting.dbLayer.DMWorkSheetManager;
import dmNew.setting.dbLayer.DMWorkSheet;

/**
 * Servlet implementation class ServletDMWorkSheetGetAll
 */
@WebServlet("/ServletDMWorkSheetGetAll")
public class ServletDMWorkSheetGetAll extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDMWorkSheetGetAll() {
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
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		Tenant tenant = (Tenant) session.getAttribute("tenant");
		Connection dbConn = null;
		try {
			dbConn = tenant.getDbConnectionConfig().getDbConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		try {
			String bizId=request.getParameter("bizId");

			List<DMWorkSheet> listWorkSheet= new ArrayList<DMWorkSheet>();
			DMWorkSheetManager.getWorkSheetAll(dbConn, Integer.parseInt(bizId), listWorkSheet);
			RecordsetResult recordsetResult = new RecordsetResult();
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listWorkSheet.size());
			recordsetResult.setPageSize(listWorkSheet.size());
			recordsetResult.setRows(listWorkSheet);
			PrintWriter printWriter = null;
			printWriter = response.getWriter();
			printWriter.write(recordsetResult.toJson());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
		}
	}
}
