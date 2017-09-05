package hiapp.modules.dmsetting.servlet;

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

import dmNew.setting.dbLayer.DMBizTemplateExport;
import dmNew.setting.dbLayer.DMBizTemplateImport;
import dmNew.setting.dbLayer.ImportMapColumn;
import hiapp.system.session.Tenant;
import hiapp.utils.base.DatabaseType;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

/**
 * Servlet implementation class servletDMBizTemplateExport
 */
@WebServlet("/servletDMBizTemplateExport")
public class servletDMBizTemplateExport extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMBizTemplateExport() {
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
		
		Tenant tenant = (Tenant)session.getAttribute("tenant");
		try {
			dbConn= tenant.getDbConnectionConfig().getDbConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ServiceResult Result=new ServiceResult();
		String szActionValue = request.getParameter("action");
		String bizId=request.getParameter("bizId");
		String templateId=request.getParameter("templateId");
		PrintWriter printWriter = response.getWriter();
		if(szActionValue.equals("getMapColumns")){
			try {
				JsonObject jsonObject= getMapColumns(dbConn,Integer.parseInt(bizId),Integer.parseInt(templateId));
				printWriter.print(jsonObject.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("modifiedMapColumns")){
			try {
				String szMapColumns=request.getParameter("MapColumns");
				JsonParser jsonParser=new JsonParser();
				JsonArray jsonArray=jsonParser.parse(szMapColumns).getAsJsonArray();
				modifiedMapColumns(Result,dbConn,Integer.parseInt(bizId),Integer.parseInt(templateId),jsonArray);
				printWriter.print(Result.toJson());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(szActionValue.equals("getxml")){
			try {
				
				getxml(Result,dbConn,Integer.parseInt(bizId),Integer.parseInt(templateId));
				printWriter.print(Result.toJson());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public static boolean getxml(ServiceResult Result,Connection dbConn,int bizId ,int templateId) throws Exception{
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_SUBMIT);
		
		Result.setResultCode(ServiceResultCode.SUCCESS);
		Result.setReturnMessage("成功");
		DMBizTemplateExport.getxml(dbConn, bizId ,templateId);

		
		
		return true;
	}
	
	public static boolean modifiedMapColumns(ServiceResult Result,Connection dbConn,int bizId ,int templateId,JsonArray jaMapColumns) throws Exception{
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_SUBMIT);
		
		Result.setResultCode(ServiceResultCode.SUCCESS);
		Result.setReturnMessage("成功");
		DMBizTemplateExport.modifiedMapColumns(dbConn, bizId ,templateId,jaMapColumns);

		
		
		return true;
	}
	
	public static JsonObject getMapColumns(Connection dbConn,int bizId ,int templateId) throws Exception{
		
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<ImportMapColumn> listMapColumn =new ArrayList<ImportMapColumn>();
		DMBizTemplateExport.getMapColumns(dbConn, DatabaseType.ORACLE,bizId, templateId, listMapColumn);
		
		JsonObject jsonObject=new JsonObject();
		JsonArray jsonArray=new JsonArray();
        for(int ii=0;ii<listMapColumn.size();ii++){
        	ImportMapColumn importMapColumn=listMapColumn.get(ii);
			JsonObject jsColumn=new JsonObject();
			jsColumn.addProperty("index", ii+1);
			jsColumn.addProperty("WorkSheetIds", importMapColumn.getWorksheetid());
			jsColumn.addProperty("WorkSheetNames", importMapColumn.getWorksheetName());
			jsColumn.addProperty("WorkSheetNameChs", importMapColumn.getWorksheetNameCh());
			jsColumn.addProperty("Names", importMapColumn.getName());
			jsColumn.addProperty("NameChs", importMapColumn.getNameCh());
			jsColumn.addProperty("CellAddr", importMapColumn.getCellAddr());
			jsColumn.addProperty("RowIndex", importMapColumn.getRowIndex());
			jsColumn.addProperty("ColIndex", importMapColumn.getColIndex());
			jsColumn.addProperty("ExcelHeader", importMapColumn.getExcelColumnName());
			jsColumn.addProperty("WorkSheetId", importMapColumn.getxmlWorksheetid());
			jsColumn.addProperty("WorkSheetName", importMapColumn.getxmlWorksheetName());
			jsColumn.addProperty("WorkSheetColName", importMapColumn.getxmlWorkSheetColName());
			jsonArray.add(jsColumn);
        }

		jsonObject.addProperty("total",listMapColumn.size());
		jsonObject.add("rows", jsonArray);
		return jsonObject;
	}
	
}
