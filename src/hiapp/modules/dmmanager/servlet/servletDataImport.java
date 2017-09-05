package hiapp.modules.dmmanager.servlet;

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

import dmNew.setting.dbLayer.DMBizTemplateImport;
import dmNew.setting.dbLayer.DMBizTemplatesImport;
import dmNew.setting.dbLayer.ImportMapColumn;
import hiapp.utils.base.DatabaseType;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.modules.dmmanager.dbLayer.DMBizDataImport;
import hiapp.system.session.Tenant;
import hiapp.system.session.User;
import hiapp.utils.ConstantResultType;
import hiapp.utils.UtilServlet;

/**
 * Servlet implementation class servletDataImport
 */
@WebServlet("/servletDataImport")
public class servletDataImport extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDataImport() {
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
		PrintWriter printWriter = response.getWriter();
		String userid="";
		try {
			Tenant tenant = (Tenant)session.getAttribute("tenant");
			dbConn= tenant.getDbConnectionConfig().getDbConnection();
			User user = (User)request.getSession(false).getAttribute("user");
			userid=user.getAgtId();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}catch(NullPointerException e)
		{
			JsonObject jsonObject =  new JsonObject();
			jsonObject.addProperty("returnCode", "2");
			jsonObject.addProperty("returnMessage","超时");
			printWriter.print(jsonObject.toString());
			return;
		}
		
		ServiceResult Result=new ServiceResult();
		String szActionValue = request.getParameter("action");
		
		
		if(szActionValue.equals("getAllTemplates")){
			try {
				String bizId=request.getParameter("bizId");
				JsonArray jsonArray=getAllTempates(dbConn,Integer.parseInt(bizId));
				printWriter.write(jsonArray.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("getColumns")){
			try {
				String bizId=request.getParameter("bizId");
				String templateId=request.getParameter("templateId");
				JsonObject jsonObject=getColumns(dbConn,Integer.parseInt(bizId),Integer.parseInt(templateId));
				printWriter.write(jsonObject.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("getExcelData")){
			try {
				String bizId=request.getParameter("bizId");
				String tableName=request.getParameter("tableName");
				String templateId=request.getParameter("templateId");
				JsonObject jsonObject=getExcelData(dbConn,Integer.parseInt(bizId),Integer.parseInt(templateId),tableName);
				printWriter.write(jsonObject.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("ImportIdCreate")){
			try {
				String bizId=request.getParameter("bizId");
				ImportIdCreate(dbConn,Integer.parseInt(bizId));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("save2Db")){
			try {
				String bizId=request.getParameter("bizId");
				String importId=request.getParameter("ImportId");
				String importIndex=request.getParameter("ImportIndex");
				String templateId=request.getParameter("templateId");
				String tableNameExcel=request.getParameter("TableName");
				save2Db(dbConn,Integer.parseInt(bizId),importId,Integer.parseInt(importIndex),Integer.parseInt(templateId),tableNameExcel,userid);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	
	}
	public static JsonArray getAllTempates(Connection dbConn,int bizId) throws Exception{
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_COMBOBOXDICTIONAY);
		JsonArray jaDictionary=new JsonArray();
		List<DMBizTemplateImport> listTemplate=new ArrayList<DMBizTemplateImport>();
		DMBizTemplatesImport.getAllTemplates(dbConn, bizId, listTemplate);
		
		for(int ii=0;ii<listTemplate.size();ii++){
			DMBizTemplateImport dmBizTemplateImport=listTemplate.get(ii);
			String szText=String.format("%d:%s", dmBizTemplateImport.getTemplateId(),dmBizTemplateImport.getName());
			JsonObject jsonObject=new JsonObject();
			jsonObject.addProperty("id", dmBizTemplateImport.getTemplateId());
			jsonObject.addProperty("text", szText);
			jaDictionary.add(jsonObject);
		} 
		return jaDictionary;
	}
	public static JsonObject getColumns(Connection dbConn,int bizId,int templateId) throws Exception{
		List<ImportMapColumn> lsImportMapColumns=new ArrayList<ImportMapColumn>();
		DMBizTemplateImport.getMapColumns(dbConn,DatabaseType.ORACLE, bizId, templateId, lsImportMapColumns);
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_QUERY);
		JsonObject jsonObject=new JsonObject();
		
		JsonArray jaColumns=new JsonArray();
        JsonObject jsCol=new JsonObject(); 
	    jsCol.addProperty("field", "no");
	    jsCol.addProperty("title", "序号");
	    jsCol.addProperty("width", 80);
	    jaColumns.add(jsCol);

	    for(int ii=0;ii<lsImportMapColumns.size();ii++){
	    	ImportMapColumn importMapColumn=lsImportMapColumns.get(ii);
	        jsCol=new JsonObject(); 
	        
            String dbFieldName=importMapColumn.getName();
            if(dbFieldName.equals("ID")) continue;
            if(dbFieldName.equals("IID")) continue;
	        jsCol.addProperty("field", importMapColumn.getName());
	        jsCol.addProperty("title", importMapColumn.getNameCh());
	        jsCol.addProperty("width", 80);
	        jaColumns.add(jsCol);
	        
	        jsonObject.add("columns", jaColumns);

		}
		return jsonObject;
	}
	public static JsonObject getExcelData(Connection dbConn,int bizId,int templateId,String tableName) throws Exception{
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		JsonObject jsonObject=new JsonObject();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<ImportMapColumn> lsImportMapColumns=new ArrayList<ImportMapColumn>();
		DMBizTemplateImport.getMapColumns(dbConn,DatabaseType.ORACLE, bizId, templateId, lsImportMapColumns);
		
		try {
			String szSql =String.format("SELECT * from %s ",tableName);
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			int nCols=rs.getMetaData().getColumnCount();
			if(rs.next()){
				for(int ii=0;ii<nCols;ii++){
					String szExcelColumnName=rs.getString(ii+1);
	    			for(int jj=0;jj<lsImportMapColumns.size();jj++){
	    				ImportMapColumn importMapColumn=lsImportMapColumns.get(jj);
	    				String szExcelColumnName1=importMapColumn.getNameCh();
	    				if(szExcelColumnName1.equals(szExcelColumnName)){
	    					importMapColumn.setDbFieldIndex(ii);
	    				}
	    			}
				}
			}
			JsonArray jaRows=new JsonArray();
			int nRow=1;
			while(rs.next()){
				JsonObject jsItem=new JsonObject();
				jsItem.addProperty("no", nRow);
				nRow++;
				for(int ii=0;ii<nCols;ii++){
					String szFieldValue=rs.getString(ii+1);
					
				    for(int kk=0;kk<lsImportMapColumns.size();kk++){
				    	ImportMapColumn importMapColumn=lsImportMapColumns.get(kk);
	    				if(importMapColumn.getDbFieldIndex()==ii){
	    					jsItem.addProperty(importMapColumn.getName(), szFieldValue);
	    				}

					}

					
				}		
				jaRows.add(jsItem);
			}
			jsonObject.addProperty("total", rs.getRow());
			jsonObject.add("rows", jaRows);
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			
		}
		return jsonObject;
	}
	public static boolean ImportIdCreate(Connection dbConn,int  bizId) throws Exception{
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_QUERY);
		StringBuffer importId=new StringBuffer();
		StringBuffer importIndex=new StringBuffer();
		
		DMBizDataImport.ImportIdCreate(dbConn, bizId, importId,importIndex);
		String importIdString=importId.toString();
		String importIndexString=importIndex.toString();
		/*utilServlet.setResultCode(0);
		utilServlet.setResultMessage("成功");
		utilServlet.setResultValue("ImportId", importIdString);
		utilServlet.setResultValue("ImportIndex", importIndexString);*/
		
		return true;
	}
	public static boolean save2Db(Connection dbConn,int  bizId,String importId,int importIndex,int templateId,String tableNameExcel,String userid) throws Exception{
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_SUBMIT);
		DMBizDataImport.save2Db(dbConn, bizId, importId, importIndex,templateId,tableNameExcel,userid);
		/*utilServlet.setResultCode(0);
		utilServlet.setResultMessage("导入成功");*/
		return true;
	}

}
