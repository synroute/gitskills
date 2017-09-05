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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dmNew.setting.dbLayer.DMBizPermissionGridCell;
import dmNew.setting.dbLayer.DMBizPermissionGridRow;
import dmNew.setting.dbLayer.DMBizPermissionManager;
import dmNew.setting.dbLayer.DMBusiness;
import dmNew.setting.dbLayer.DMBusinessManager;

/**
 * Servlet implementation class ServletDMBizPermissionGetAll
 */
@WebServlet("/ServletDMBizPermissionGetAll")
public class ServletDMBizPermissionGetAll extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDMBizPermissionGetAll() {
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
			List<DMBizPermissionGridRow> listDmBizPermissionGridRows=new ArrayList<DMBizPermissionGridRow>();
			if (!DMBizPermissionManager.getAll(dbConn, listDmBizPermissionGridRows)) {
				return;
			}
			JsonArray jaRows=new JsonArray();
			for(int ii=0;ii<listDmBizPermissionGridRows.size();ii++){
				DMBizPermissionGridRow row=listDmBizPermissionGridRows.get(ii);
		        JsonObject jsonRow=new JsonObject();
		        List<DMBizPermissionGridCell> listCells=row.getListBizPermissionGridCells();
		        for(int jj=0;jj<listCells.size();jj++){
		        	DMBizPermissionGridCell cell=listCells.get(jj);
		        	jsonRow.addProperty(cell.getField(),cell.getValue());
		        }
		        jaRows.add(jsonRow);
			}
	        JsonObject jsonObject=new JsonObject();
	        jsonObject.addProperty("total",listDmBizPermissionGridRows.size());
	        jsonObject.add("rows",jaRows);
			PrintWriter printWriter = null;
			printWriter = response.getWriter();
			printWriter.write(jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.DbCloseConnection(dbConn);
		}
	}
}
