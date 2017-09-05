package hiapp.modules.dmmanager.servlet;
//huanghe 需要Seq创建
import java.io.IOException;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.corba.se.impl.orbutil.closure.Constant;

import dm.setting.dbLayer.DMBizWorkSheets;
import hiapp.WorkSheetManager.WorkSheet;
import hiapp.WorkSheetManager.WorkSheetColumn;
import hiapp.WorkSheetManager.WorkSheetManager;
import hiapp.modules.dmmanager.dbLayer.DMBizTaskCreate;
import hiapp.utils.ConstantResultType;
import hiapp.utils.UtilServlet;

/**
 * Servlet implementation class servletTaskCreate
 */
@WebServlet("/servletTaskCreate")
public class servletTaskCreate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletTaskCreate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UtilServlet utilServlet=new UtilServlet(request, response);
		if(utilServlet.Action.equals("getColumns")){
			try {
				String bizId=request.getParameter("bizId");
				getColumns(utilServlet,Integer.parseInt(bizId));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("queryImportData")){
			try {
				String bizId=request.getParameter("bizId");
				String szdateStart=request.getParameter("dateStart");
				String szdateEnd=request.getParameter("dateEnd");
				queryImportData(utilServlet,Integer.parseInt(bizId),szdateStart,szdateEnd);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("save2Db")){
			try {
				String bizId=request.getParameter("bizId");
				String taskId=request.getParameter("taskId");
				String taskName=request.getParameter("taskName");
				String taskDescription=request.getParameter("taskDescription");
				String taskIndex=request.getParameter("taskIndex");
				String taskData=request.getParameter("taskData");
				JsonParser jsonParser=new JsonParser();
				JsonArray jsonArray=jsonParser.parse(taskData).getAsJsonArray();
				save2Db(utilServlet,Integer.parseInt(bizId),taskId,taskName,taskDescription,Integer.parseInt(taskIndex),jsonArray);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("taskIdCreate")){
			try {
				String bizId=request.getParameter("bizId");
				taskIdCreate(utilServlet,Integer.parseInt(bizId));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		utilServlet.Response();
		utilServlet.Close();
		
	}

    
	public static boolean getColumns(UtilServlet utilServlet,int bizId) throws Exception{
		
		List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
		int workSheetId=DMBizWorkSheets.getWorkSheetId(utilServlet,bizId, "客户导入工作表");
		WorkSheet.getColumns(utilServlet, workSheetId, listColumns);
		
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_QUERY);
		JsonObject jsonObject=utilServlet.getJoResult();
		
		JsonArray jaColumns=new JsonArray();
        JsonObject jsCol=new JsonObject(); 
	    jsCol.addProperty("field", "no");
	    jsCol.addProperty("title", "序号");
	    jsCol.addProperty("width", 80);
	    jaColumns.add(jsCol);

	    for(int ii=0;ii<listColumns.size();ii++){
	    	WorkSheetColumn workSheetColumn=listColumns.get(ii);
	        jsCol=new JsonObject(); 
	        
            String dbFieldName=workSheetColumn.getName();
            if(dbFieldName.equals("ID")) continue;
	        jsCol.addProperty("field", workSheetColumn.getName());
	        jsCol.addProperty("title", workSheetColumn.getNameCh());
	        jsCol.addProperty("width", 80);
	        jaColumns.add(jsCol);
	        
	        jsonObject.add("columns", jaColumns);

		}
	    jsonObject.add("columns", jaColumns);
		return true;
	}
	public static boolean queryImportData(UtilServlet utilServlet,int bizId,String szdateStart,String szdateEnd) throws Exception{
		List<WorkSheetColumn> listColumn=new ArrayList<WorkSheetColumn>();
		ResultSet rs=null;
		PreparedStatement stmt = null;
		try {
			int workSheetId=DMBizWorkSheets.getWorkSheetId(utilServlet,bizId, "客户导入工作表");
			String szTableNameImport=WorkSheetManager.getWorkSheetName(utilServlet,workSheetId);
			WorkSheet.getColumns(utilServlet, workSheetId, listColumn);
			String szTimeStart=String.format("%s 00:00:00",szdateStart);
			String szTimeEnd=String.format("%s 23:59:59",szdateEnd);
			String szSql;
			String szSqlPart1="Select ";
			String szSqlPart2="";
			for(int nCol=0;nCol<listColumn.size();nCol++){
				WorkSheetColumn workSheetColumn=listColumn.get(nCol);
				String szItem=String.format("%s.%s",szTableNameImport,workSheetColumn.getName());
				szSqlPart2+=szItem;
				if(nCol!=listColumn.size()-1){
					szSqlPart2+=",";
				}
			}
			String szSqlPart3=String.format("FROM HASYS_DM_IMPORTINFO INNER JOIN %s ON HASYS_DM_IMPORTINFO.IID = %s.IID AND HASYS_DM_IMPORTINFO.BusinessID=%d", 
												szTableNameImport,								
												szTableNameImport,bizId);

			szSql=String.format("%s\r\n%s\r\n%s\r\n where HASYS_DM_IMPORTINFO.ImportTime >=to_date('%s','yyyy-MM-dd HH24:MI:SS') and HASYS_DM_IMPORTINFO.ImportTime<=to_date('%s','yyyy-MM-dd HH24:MI:SS')  ORDER BY %s.ID",
									szSqlPart1,szSqlPart2,szSqlPart3,szTimeStart,szTimeEnd,szTableNameImport);
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			
			utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
			JsonObject jsonObject=utilServlet.getJoResult();
			JsonArray jaRows=new JsonArray();
			int nRow=1;
			while(rs.next()){
				JsonObject jsItem=new JsonObject();
				jsItem.addProperty("no", nRow);
				nRow++;
				for(int nCol=0;nCol<listColumn.size()-1;nCol++){
					WorkSheetColumn column=listColumn.get(nCol);
					String szFieldName=column.getName();
					String szFieldValue=rs.getString(nCol+1);
					jsItem.addProperty(szFieldName, szFieldValue);
				}		
				jaRows.add(jsItem);
			}
			jsonObject.addProperty("total", nRow-1);
			jsonObject.add("rows", jaRows);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			utilServlet.DbCloseQuery(rs, stmt);
		}

		return true;
	}
	public static boolean taskIdCreate(UtilServlet utilServlet,int bizId) throws Exception{
		StringBuffer taskId=new StringBuffer();
		StringBuffer taskIndex=new StringBuffer();
		if(!DMBizTaskCreate.taskIdCreate(utilServlet, bizId, taskId, taskIndex)){
			return false;
		}
		String szTaskId=taskId.toString();
		String szTaskIndex=taskIndex.toString();
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_QUERY);
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("获取任务ID成功！");
		utilServlet.setResultValue("TaskId", szTaskId);
		utilServlet.setResultValue("TaskIndex", szTaskIndex);
		return true;
		
	}
	public static boolean save2Db(UtilServlet utilServlet,int bizId,String taskId,String taskName,String taskDescription,int taskIndex, JsonArray jaTaskData) throws Exception{
//		移入模型中
//		然后再进行插入任务记录
//		添加TaskIndex
//		最后，数据分配
		if(!DMBizTaskCreate.save2Db(utilServlet, bizId, taskId, taskName,taskDescription,taskIndex,jaTaskData)){
			return false;
		}
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_SUBMIT);
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("创建任务成功！");
		return true;
	}
}
