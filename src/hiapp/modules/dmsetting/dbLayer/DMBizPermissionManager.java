package hiapp.modules.dmsetting.dbLayer;

import hiapp.system.buinfo.beanOld.Permission;
import hiapp.system.buinfo.beanOld.PermissionManager;
import hiapp.utils.DbUtil;
import hiapp.utils.UtilServlet;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class DMBizPermissionManager {
	public static boolean getAll(Connection dbConn,List<DMBizPermissionGridRow> listDmBizPermissionGridRows){
		List<DMBizPermission> listBizPermissions=new ArrayList<DMBizPermission>();
		List<DMBusiness> listdmBusinesses=new ArrayList<DMBusiness>();
		List<Permission> listPermissions=new ArrayList<Permission>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String szSql = "select businessid, permissionid, systemsetting, datamanager, dial from hasys_dm_permission";
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMBizPermission dmBizPermission=new DMBizPermission();
				dmBizPermission.setBizId(rs.getInt(1));
				dmBizPermission.setPermId(rs.getInt(2));
				dmBizPermission.setSystemSetting(rs.getInt(3));
				dmBizPermission.setDataManager(rs.getInt(4));
				dmBizPermission.setDial(rs.getInt(5));
				listBizPermissions.add(dmBizPermission);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally {
			DbUtil.DbCloseQuery(rs, stmt);
		}
		
		
		try {
			PermissionManager.getAllPermissions(dbConn, listPermissions);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DMBusinessManager.getAllDMBusiness(dbConn,listdmBusinesses);
		
		for(int row=0;row<listPermissions.size();row++){
			Permission perm=listPermissions.get(row);
			DMBizPermissionGridRow dmBizPermissionGridRow=new DMBizPermissionGridRow();
			DMBizPermissionGridCell cellPermId=new DMBizPermissionGridCell();
			cellPermId.setField("PermId");
			cellPermId.setValue(perm.getPermissionId());
			dmBizPermissionGridRow.addCell(cellPermId);
			
			DMBizPermissionGridCell cellPermName=new DMBizPermissionGridCell();
			cellPermName.setField("PermName");
			cellPermName.setValue(perm.getPermissionName());
			dmBizPermissionGridRow.addCell(cellPermName);

			dmBizPermissionGridRow.setPermId(perm.getPermissionId());
			for(int col=0;col<listdmBusinesses.size();col++){
				DMBusiness dmBusiness=listdmBusinesses.get(col);
				String fieldString=null;
				DMBizPermissionGridCell cellSystemSetting=new DMBizPermissionGridCell();	
				fieldString=String.format("F%d_SystemSetting", dmBusiness.getId());
				cellSystemSetting.setField(fieldString);
				dmBizPermissionGridRow.addCell(cellSystemSetting);
				
				DMBizPermissionGridCell cellDataManager=new DMBizPermissionGridCell();	
				fieldString=String.format("F%d_DataManager", dmBusiness.getId());
				cellDataManager.setField(fieldString);
				dmBizPermissionGridRow.addCell(cellDataManager);
				
				DMBizPermissionGridCell cellDial=new DMBizPermissionGridCell();	
				fieldString=String.format("F%d_Dial", dmBusiness.getId());
				cellDial.setField(fieldString);
				dmBizPermissionGridRow.addCell(cellDial);
				
				for(int ii=0;ii<listBizPermissions.size();ii++){
					DMBizPermission dmBizPermission=listBizPermissions.get(ii);
					if(dmBizPermission.getBizId()==dmBusiness.getId() && dmBizPermission.getPermId()==perm.getPermissionId()){
						cellSystemSetting.setValue(dmBizPermission.getSystemSetting());
						cellDataManager.setValue(dmBizPermission.getDataManager());
						cellDial.setValue(dmBizPermission.getDial());
					}
				}
			}
			listDmBizPermissionGridRows.add(dmBizPermissionGridRow);
		}
		return true;
	}
	public static ServiceResultCode submit(Connection dbConn,JsonArray jArray){
		PreparedStatement stmt = null;
		try {
			String szSql = "delete from hasys_dm_permission";
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return ServiceResultCode.EXECUTE_SQL_FAIL;
		} 
		finally {
			DbUtil.DbCloseExecute(stmt);
		}
		
		List<DMBusiness> listdmBusinesses=new ArrayList<DMBusiness>();
		DMBusinessManager.getAllDMBusiness(dbConn,listdmBusinesses);
		for(int row=0;row<jArray.size();row++){
			JsonObject joRow=jArray.get(row).getAsJsonObject();
			JsonElement jePermId=joRow.get("PermId");
			int permId=jePermId.getAsInt();
			for(int biz=0;biz<listdmBusinesses.size();biz++){
				DMBusiness dmBusiness=listdmBusinesses.get(biz);
				int bizId=dmBusiness.getId();
				int systemSetting=0;
				int dataManager=0;
				int dial=0;
				String fieldString=null;
				JsonElement jeElement=null;
				fieldString=String.format("F%d_SystemSetting", bizId);
				jeElement=joRow.get(fieldString);
				if(!jeElement.isJsonNull()){
					systemSetting=jeElement.getAsInt();
				}
				fieldString=String.format("F%d_DataManager", bizId);
				jeElement=joRow.get(fieldString);
				if(!jeElement.isJsonNull()){
					dataManager=jeElement.getAsInt();
				}
				fieldString=String.format("F%d_Dial", bizId);
				jeElement=joRow.get(fieldString);
				if(!jeElement.isJsonNull()){
					dial=jeElement.getAsInt();
				}
				try {
					String szSql = String.format("insert into hasys_dm_permission"+
												"(businessid, permissionid, systemsetting, datamanager, dial)"+
												"values(%d, %d, %d, %d, %d)",
												bizId,permId,systemSetting,dataManager,dial);
					stmt = dbConn.prepareStatement(szSql);
					stmt.execute();
				} catch (SQLException e) {
					e.printStackTrace();
					return ServiceResultCode.EXECUTE_SQL_FAIL;
				} 
				finally {
					DbUtil.DbCloseExecute(stmt);
				}
					
				
			}
			
			
		}
		
		
		
		return ServiceResultCode.SUCCESS;
		
	}	
}
