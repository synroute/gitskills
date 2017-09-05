package hiapp.modules.dmsetting.dbLayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import hiapp.utils.UtilServlet;

public class DMEndCodeManager {
	public static boolean getAllEndCodes(UtilServlet utilServlet,int bizId,List<DMEndCode> listEndCodes){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String szSql = "select codetype, code, description from hasys_dm_bizendcode where businessid=? ";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setInt(1,bizId);	
			rs = stmt.executeQuery();
			while(rs.next()){
				DMEndCode dmEndCode=new DMEndCode();
				dmEndCode.setEndCodeType(rs.getString(1));
				dmEndCode.setEndCode(rs.getString(2));
				dmEndCode.setDescription(rs.getString(3));
				listEndCodes.add(dmEndCode);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			utilServlet.DbCloseQuery(rs, stmt);
		}
		return true;
		
	}
	public static boolean submitEndCodes(UtilServlet utilServlet,int bizId,JsonArray jaEndCodes){
		PreparedStatement stmt = null;
		try {
			String szSql = "delete from hasys_dm_bizendcode where businessid=? ";
			stmt = utilServlet.dbConn.prepareStatement(szSql);
			stmt.setInt(1,bizId);	
			stmt.execute();
		} catch (SQLException e) {
			utilServlet.setResultCode(2);
			utilServlet.setResultMessage("删除记录错误！");
			return false;
		} finally {
			utilServlet.DbCloseExecute(stmt);
		}
		
		for(int ii=0;ii<jaEndCodes.size();ii++){
			JsonObject jsEndCode=jaEndCodes.get(ii).getAsJsonObject();
			String endCodeType=utilServlet.getJsonProperty(jsEndCode, "EndCodeType");
			String endCode=utilServlet.getJsonProperty(jsEndCode, "EndCode");
			String endCodeDescription=utilServlet.getJsonProperty(jsEndCode, "EndCodeDescription");
			String szSql = "INSERT INTO HASYS_DM_BIZENDCODE (BusinessId,CodeType,Code,Description) VALUES(?,?,?,?) ";
			try {
				stmt = utilServlet.dbConn.prepareStatement(szSql);
				stmt.setInt(1,bizId);
				stmt.setString(2,endCodeType);
				stmt.setString(3,endCode);
				stmt.setString(4,endCodeDescription);
				stmt.execute();
			} catch (Exception e) {
				utilServlet.setResultCode(2);
				utilServlet.setResultMessage("插入记录错误！");
				return false;
			} finally {
				utilServlet.DbCloseExecute(stmt);
			}
			
		}
		return true;
		
		
	}
	
}
