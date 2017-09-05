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

import dm.setting.dbLayer.DMBizWorkSheets;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.system.session.Tenant;
import hiapp.system.session.User;
import hiapp.system.worksheet.dblayer.WorkSheet;
import hiapp.system.worksheet.dblayer.WorkSheetColumn;
import hiapp.system.worksheet.dblayer.WorkSheetManager;

/**
 * Servlet implementation class servletCustList
 */
@WebServlet("/servletCustList")
public class servletCustList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletCustList() {
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
		if(szActionValue.equals("getColumns")){
			try {
				String bizId="1";
				JsonObject jsonObject=getColumns(dbConn,Integer.parseInt(bizId));
				printWriter.write(jsonObject.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(szActionValue.equals("queryDistributeData")){
			try {
				String bizId="1";
				String dateStart=request.getParameter("dateStart");
				String dateEnd=request.getParameter("dateEnd");
				/*String taskList=request.getParameter("taskList");
				JsonParser jsonParser=new JsonParser();
				JsonArray jaTaskList=jsonParser.parse(taskList).getAsJsonArray();*/ 
				JsonObject jsonObject=queryDistributeData(dbConn,Integer.parseInt(bizId),dateStart,dateEnd,userid);
				printWriter.write(jsonObject.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static JsonObject getColumns(Connection dbConn,int bizId) throws Exception{
		int workSheetId=DMBizWorkSheets.getWorkSheetId(dbConn, bizId, "客户任务工作表");
		List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
		WorkSheet.getColumns(dbConn, workSheetId, listColumns);
		int workSheetIdf=DMBizWorkSheets.getWorkSheetId(dbConn, bizId, "客户分配工作表");
		
		WorkSheet.getColumns(dbConn, workSheetIdf, listColumns);
		
		int workSheetIds=DMBizWorkSheets.getWorkSheetId(dbConn, bizId, "预约工作表");
		
		WorkSheet.getColumns(dbConn, workSheetIds, listColumns);
		
		
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_QUERY);
		JsonObject jsonObject=new JsonObject();
		
		JsonArray jaColumns=new JsonArray();
        JsonObject jsCol=null; 
        jsCol=new JsonObject();
	    jsCol.addProperty("field", "cx");
	    jsCol.addProperty("title", "编号");
	    jsCol.addProperty("width", 80);
	    jsCol.addProperty("checkbox", "true");
        jaColumns.add(jsCol);
       
        
	    for(int ii=0;ii<listColumns.size();ii++){
	    	WorkSheetColumn workSheetColumn=listColumns.get(ii);
	        jsCol=new JsonObject();
		    jsCol.addProperty("field", workSheetColumn.getName());
		    jsCol.addProperty("title", workSheetColumn.getNameCh());
		    jsCol.addProperty("width", 80);
	        jaColumns.add(jsCol);
		}
        jsonObject.add("columns", jaColumns);

		return jsonObject;
	}
	public static JsonObject queryDistributeData(Connection dbConn,int bizId,String dateStart,String dateEnd,String userid) throws Exception{
		List<WorkSheetColumn> listColumn=new ArrayList<WorkSheetColumn>();
		ResultSet rs=null;
		PreparedStatement stmt = null;
		JsonObject jsonObject=new JsonObject();
		JsonArray jaRows=new JsonArray();
		try {
			int workSheetId=DMBizWorkSheets.getWorkSheetId(dbConn,bizId, "客户任务工作表");
			String szTableNameCustInTask=WorkSheetManager.getWorkSheetName(dbConn,workSheetId);
			WorkSheet.getColumns(dbConn, workSheetId, listColumn);
			String szSql;
			String szSqlPart1="Select B.BusinessID,B.DistId,B.IID,B.CID,B.USERID,B.USERNAME,B.SYSSTATE,B.ModifyTime,P.BusinessId,P.TaskID,P.IID,P.CID,P.PresetTime,P.State,P.\"Comment\",P.ModifyID,P.ModifyUserID,P.ModifyTime,P.ModifyDsp,P.ModifyLast,P.PhoneType,P.DayOrder,P.tempFirstPresetTimeUser,";
			String szSqlPart2="";
			String szSqlTaskFilter="taskid in (";
			/*for(int ii=0;ii<jaTaskList.size();ii++){
				JsonObject jsTask=jaTaskList.get(ii).getAsJsonObject();
				JsonElement jElement=jsTask.get("id");
				String taskId=jElement.getAsString();
				szSqlTaskFilter+="'";
				szSqlTaskFilter+=taskId;
				szSqlTaskFilter+="'";
				if(ii<jaTaskList.size()-1){
					szSqlTaskFilter+=",";
				}
			}		*/
			
			
			szSqlTaskFilter+=")";
			for(int nCol=0;nCol<listColumn.size();nCol++){
				WorkSheetColumn workSheetColumn=listColumn.get(nCol);
				if(!workSheetColumn.getName().equals("TASKID"))
				{
					String szItem=String.format("%s",workSheetColumn.getName());
					szSqlPart2+="C."+szItem;
					if(nCol!=listColumn.size()-1){
						szSqlPart2+=",";
					}
				}
			}
			String szSqlPart3=String.format("FROM HASYS_DM_BIZTASKDIST B  LEFT JOIN HAUDM_B"+bizId+"_CUSTIMPORT C on C.YONGHUID=B.CID LEFT JOIN  HASYS_DM_PRESETTIME P on C.IID=P.IID");

			szSql=String.format("%s\r\n%s\r\n%s\r\n where   B.ModifyTime between TO_DATE('" + dateStart + "','yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + dateEnd + "','yyyy-mm-dd hh24:mi:ss') AND B.USERID=0000 ",
								szSqlPart1,szSqlPart2,szSqlPart3);
		stmt = dbConn.prepareStatement(szSql);
		rs = stmt.executeQuery();
		
		//utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		
		int nRow=1;
		while(rs.next()){
			JsonObject jsItem=new JsonObject();
			jsItem.addProperty("no", nRow);
			nRow++;
			for(int nCol=0;nCol<listColumn.size()-1;nCol++){
				WorkSheetColumn column=listColumn.get(nCol);
				if(!column.getName().equals("MODIFYUSERID")&&!column.getName().equals("MODIFYID")&&!column.getName().equals("TASKID"))
				{
				String szFieldName=column.getName();
				String szFieldValue=rs.getString(szFieldName);
				jsItem.addProperty(szFieldName, szFieldValue);
				}
			}		
			jaRows.add(jsItem);
		}
		jsonObject.addProperty("total", nRow-1);
		jsonObject.add("rows", jaRows);
	} catch (SQLException e) {
		e.printStackTrace();
		
	} finally {
		
	}
		return jsonObject;
	}
	
	
	
	
}
