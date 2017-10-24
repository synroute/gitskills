package hiapp.modules.dmsetting.data;


import hiapp.modules.dmsetting.DMBizPermission;
import hiapp.modules.dmsetting.DMBizResult;
import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.DMDataPool;
import hiapp.modules.dmsetting.result.BizMapPermission;
import hiapp.system.buinfo.Permission;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.UserWithPermission;
import hiapp.system.buinfo.data.PermissionRepository;
import hiapp.system.buinfo.data.UserRepository;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

@Repository
public class DMBizPermissionRepository extends BaseRepository {
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private DmBizRepository dmBizRepository;
	@Autowired
	private UserRepository userRepository;
	Connection dbConn = null;
	//获取所有权限接口
	public JsonObject getAll(){
		
		
		List<DMBizPermission> listBizPermissionsiz=new ArrayList<DMBizPermission>();
		List<DMBusiness> listdmBusinesses=new ArrayList<DMBusiness>();
		List<Permission> listPermissions=new ArrayList<Permission>();
		
		
		JsonObject jsonObject=new  JsonObject();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			dbConn =this.getDbConnection();
			String szSql = "select DataPoolID,BusinessID,PermissionID,ItemName from HASYS_DM_PER_MAP_POOL where (DataPoolID in (select Id from  HASYS_DM_DATAPOOL where isDelete=0 and DataPoolType!=3) or DATAPOOLID is null)  order by businessid desc";
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMBizPermission dmBizPermission=new DMBizPermission();
				dmBizPermission.setDataPoolId(rs.getInt(1));
				dmBizPermission.setBizId(rs.getInt(2));
				dmBizPermission.setPermId(rs.getInt(3));
				dmBizPermission.setManageItemName(rs.getString(4));
				listBizPermissionsiz.add(dmBizPermission);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			
		} 
		finally {
			DbUtil.DbCloseQuery(rs, stmt);
		}
		
		
		
		try {
			permissionRepository.permissionGetAll(listPermissions);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//添加固定列
		
		JsonArray jsonArray_fixedColumn=new JsonArray();
		
		JsonObject jsonObject_fixedColumn=new JsonObject();
		jsonObject_fixedColumn.addProperty("field", "permissionId");
		jsonObject_fixedColumn.addProperty("title", "权限id");
		jsonObject_fixedColumn.addProperty("width","80");
		jsonArray_fixedColumn.add(jsonObject_fixedColumn);
		JsonObject jsonObject_fixedColumns=new JsonObject();
		jsonObject_fixedColumns.addProperty("field", "permissionName");
		jsonObject_fixedColumns.addProperty("title", "权限名称");
		jsonObject_fixedColumns.addProperty("width","150");
		jsonArray_fixedColumn.add(jsonObject_fixedColumns);
		jsonObject.add("fixedColumns", jsonArray_fixedColumn);
		
		//获取所有业务信息
		dmBizRepository.getAllDMBusinessforper(listdmBusinesses);
		//添加业务所有列
		
		JsonArray jsonArray_biz=new JsonArray();
		JsonArray jsonArray_dataPool=new JsonArray();
		for(int col=0;col<listdmBusinesses.size();col++){
			List<DMDataPool> listDataPools=new ArrayList<DMDataPool>();
			List<DMBizPermission> listBizPermissions=new ArrayList<DMBizPermission>();
			
			int count=0;
			JsonObject jsonObject_biz=new JsonObject();
			DMBusiness dmBusiness=listdmBusinesses.get(col);
			//查询所有权限信息
			try {
				
				String szSql = "select DataPoolID,BusinessID,PermissionID,ItemName from HASYS_DM_PER_MAP_POOL  where Businessid="+dmBusiness.getBizId()+" and DataPoolID in (select Id from  HASYS_DM_DATAPOOL where isDelete=0 and DataPoolType!=3)";
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				while(rs.next()){
					DMBizPermission dmBizPermission=new DMBizPermission();
					dmBizPermission.setDataPoolId(rs.getInt(1));
					dmBizPermission.setBizId(rs.getInt(2));
					dmBizPermission.setPermId(rs.getInt(3));
					dmBizPermission.setManageItemName(rs.getString(4));
					listBizPermissions.add(dmBizPermission);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				
			} 
			finally {
				DbUtil.DbCloseQuery(rs, stmt);
			}
			//查询数据池信息
			try {
				
				String szSql = "select ID,BusinessID,DataPoolName from HASYS_DM_DATAPOOL where Businessid="+dmBusiness.getBizId()+" and ID in (select Id from  HASYS_DM_DATAPOOL where isDelete=0 and DataPoolType!=3)" ;
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				while(rs.next()){
					DMDataPool dmDataPool=new DMDataPool();
					dmDataPool.setPoolId(rs.getInt(1));
					dmDataPool.setBizId(rs.getInt(2));
					dmDataPool.setDataPoolName(rs.getString(3));
					listDataPools.add(dmDataPool);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				
			} 
			finally {
				DbUtil.DbCloseQuery(rs, stmt);
			}
			//查询该业务下有多少数据池
			try {
				
				String szSql = "select count(ID) from HASYS_DM_DATAPOOL where BusinessId="+dmBusiness.getBizId()+" and ID in (select Id from  HASYS_DM_DATAPOOL where isDelete=0 and DataPoolType!=3)" ;
				stmt = dbConn.prepareStatement(szSql);
				rs = stmt.executeQuery();
				while(rs.next()){
					count=rs.getInt(1);
					jsonObject_biz.addProperty("title", dmBusiness.getName());
					jsonObject_biz.addProperty("colspan", rs.getInt(1)+2);
					jsonArray_biz.add(jsonObject_biz);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			finally {
				DbUtil.DbCloseQuery(rs, stmt);
			}
			JsonObject jsonObject_bizFixedcolumn=new JsonObject();
			jsonObject_bizFixedcolumn.addProperty("field",dmBusiness.getBizId()+"系统配置");
			jsonObject_bizFixedcolumn.addProperty("title","系统配置");
			jsonObject_bizFixedcolumn.addProperty("width","80");
			jsonArray_dataPool.add(jsonObject_bizFixedcolumn);
			JsonObject jsonObject_bizFixedcolumns=new JsonObject();
			jsonObject_bizFixedcolumns.addProperty("field",dmBusiness.getBizId()+"数据管理");
			jsonObject_bizFixedcolumns.addProperty("title","数据管理");
			jsonObject_bizFixedcolumns.addProperty("width","80");
			jsonArray_dataPool.add(jsonObject_bizFixedcolumns);
			if (count!=0) {
				for(int pcol=0;pcol<listDataPools.size();pcol++){
					JsonObject jsonObject_dataPool=new JsonObject();
					DMDataPool dmDataPool=listDataPools.get(pcol);
					jsonObject_dataPool.addProperty("field", ""+dmDataPool.getPoolId()+"");
					jsonObject_dataPool.addProperty("title", dmDataPool.getDataPoolName());
					jsonObject_dataPool.addProperty("width","80");
					jsonArray_dataPool.add(jsonObject_dataPool);
				}
			}
			
			
			
			
		}
		jsonObject.add("bizColumns", jsonArray_biz);
		
		//添加数据池信息
		
		jsonObject.add("dataPoolColumns", jsonArray_dataPool);
		
		//获取权限配置信息
		JsonArray jsonArray_perm =new JsonArray();
		for(int row=0;row<listPermissions.size();row++){
			Permission perm=listPermissions.get(row);
			JsonObject jsonObject_perm=new JsonObject();
			
			jsonObject_perm.addProperty("permissionId", perm.getId());
			jsonObject_perm.addProperty("permissionName", perm.getName());
			
			for(int col=0;col<listBizPermissionsiz.size();col++)
			{
				DMBizPermission dmBizPermission=listBizPermissionsiz.get(col);
				
				if(perm.getId()==dmBizPermission.getPermId())
				{
					if(dmBizPermission.getManageItemName()!=null)
					{
						jsonObject_perm.addProperty(""+dmBizPermission.getBizId()+dmBizPermission.getManageItemName()+"", 1);
					}
					jsonObject_perm.addProperty(""+dmBizPermission.getDataPoolId()+"", 1);
				}	
				
				
				
			}
			
			jsonArray_perm.add(jsonObject_perm);
			
		}
		DbUtil.DbCloseConnection(dbConn);
		jsonObject.add("configInfomation", jsonArray_perm);
		return jsonObject;
	}
	
	
	//获取该权限所有信息
	public List<DMBizPermission> getPermission(int permid){
		//查询所有权限信息
		List<DMBizPermission> listBizPermissions=new ArrayList<DMBizPermission>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn=this.getDbConnection();
			String szSql = "select DataPoolID,BusinessID,PermissionID,ItemName from HASYS_DM_PER_MAP_POOL  where PermissionID="+permid+" and DataPoolID in (select Id from  HASYS_DM_DATAPOOL where isDelete=0 and DataPoolType!=3)";
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				DMBizPermission dmBizPermission=new DMBizPermission();
				dmBizPermission.setDataPoolId(rs.getInt(1));
				dmBizPermission.setBizId(rs.getInt(2));
				dmBizPermission.setPermId(rs.getInt(3));
				dmBizPermission.setManageItemName(rs.getString(4));
				listBizPermissions.add(dmBizPermission);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			
		} 
		finally {
			DbUtil.DbCloseQuery(rs, stmt);
			
		}
		return listBizPermissions;
	}
	
	
	//提交权限配置
	@SuppressWarnings("unused")
	private List<DMDataPool> getAllDataPool()
	{
		List<DMDataPool> listDataPools=new ArrayList<DMDataPool>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		//查询数据池信息
				try {
					dbConn =this.getDbConnection();
					String szSql = "select ID,BusinessID,DataPoolName from HASYS_DM_DATAPOOL where ID in (select Id from  HASYS_DM_DATAPOOL where isDelete=0 and DataPoolType!=3)" ;
					stmt = dbConn.prepareStatement(szSql);
					rs = stmt.executeQuery();
					while(rs.next()){
						DMDataPool dmDataPool=new DMDataPool();
						dmDataPool.setPoolId(rs.getInt(1));
						dmDataPool.setBizId(rs.getInt(2));
						dmDataPool.setDataPoolName(rs.getString(3));
						listDataPools.add(dmDataPool);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					
				} 
				finally {
					DbUtil.DbCloseQuery(rs, stmt);
				}
				
				return listDataPools;
	}
	
	
	
	public  boolean submit(JsonArray jArray){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn =this.getDbConnection();
			String szSql = "delete from HASYS_DM_PER_MAP_POOL";
			stmt = dbConn.prepareStatement(szSql);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
		finally {
			DbUtil.DbCloseExecute(stmt);
		}
		List<DMDataPool> listDataPools=new ArrayList<DMDataPool>();
		listDataPools=getAllDataPool();
		List<DMBusiness> listdmBusinesses=new ArrayList<DMBusiness>();
		dmBizRepository.getAllDMBusinessforper(listdmBusinesses);
		for(int row=0;row<jArray.size();row++){
			JsonObject joRow=jArray.get(row).getAsJsonObject();
			
			
			String permid = jArray.get(row).getAsJsonObject().get("permissionId").getAsString();
			
			
				for(int pcol=0;pcol<listDataPools.size();pcol++)
				{
					DMDataPool dmDataPool=listDataPools.get(pcol);
					
					if(jArray.get(row).getAsJsonObject().has(String.valueOf(dmDataPool.getPoolId())))
					{
						String isInsert=jArray.get(row).getAsJsonObject().get(String.valueOf(dmDataPool.getPoolId())).getAsString();
						if(!isInsert.equals("0"))
						{
							try {
								
								String szSql = "select BusinessID from HASYS_DM_DATAPOOL where id="+dmDataPool.getPoolId()+"" ;
								stmt = dbConn.prepareStatement(szSql);
								rs = stmt.executeQuery();
								while(rs.next()){
									String insertSql = "insert into HASYS_DM_PER_MAP_POOL(DataPoolID,BusinessID,PermissionID) values("+dmDataPool.getPoolId()+","+rs.getInt(1)+","+permid+")" ;
									stmt = dbConn.prepareStatement(insertSql);
									stmt.execute();
								}
							} catch (SQLException e) {
								e.printStackTrace();
								
							} 
							finally {
								DbUtil.DbCloseQuery(rs, stmt);
							}
						}
							
						
					}
				}
			
				for(int biz=0;biz<listdmBusinesses.size();biz++){
					DMBusiness dmBusiness=listdmBusinesses.get(biz);
					//String bizGuanli=jArray.get(row).getAsJsonObject().get(String.valueOf(dmBusiness.getBizId()+"数据管理")).getAsString();
					if(jArray.get(row).getAsJsonObject().has(String.valueOf(dmBusiness.getBizId()+"数据管理")))
					{
						String isInsert=jArray.get(row).getAsJsonObject().get(String.valueOf(String.valueOf(dmBusiness.getBizId()+"数据管理"))).getAsString();
						if(!isInsert.equals("0"))
						{
							try {
									String insertSql = "insert into HASYS_DM_PER_MAP_POOL(BusinessID,PermissionID,ItemName) values("+dmBusiness.getBizId()+","+permid+",'数据管理')" ;
									stmt = dbConn.prepareStatement(insertSql);
									rs = stmt.executeQuery();
								
							} catch (SQLException e) {
								e.printStackTrace();
							} 
							finally {
								DbUtil.DbCloseQuery(rs, stmt);
							}
						}
						
					}
					//String bizConfig=jArray.get(row).getAsJsonObject().get(String.valueOf(dmBusiness.getBizId()+"系统配置")).getAsString();
					if(jArray.get(row).getAsJsonObject().has(String.valueOf(dmBusiness.getBizId()+"系统配置")))
					{
						String isInsert=jArray.get(row).getAsJsonObject().get(String.valueOf(String.valueOf(dmBusiness.getBizId()+"系统配置"))).getAsString();
						if(!isInsert.equals("0"))
						{
							try {
									String insertSql = "insert into HASYS_DM_PER_MAP_POOL(BusinessID,PermissionID,ItemName) values("+dmBusiness.getBizId()+","+permid+",'系统配置')" ;
									stmt = dbConn.prepareStatement(insertSql);
									rs = stmt.executeQuery();
								
							} catch (SQLException e) {
								e.printStackTrace();
							} 
							finally {
								DbUtil.DbCloseQuery(rs, stmt);
							}
						}
						
					}


				}
			
			
			
		
			
			
		}
		DbUtil.DbCloseConnection(dbConn);
		
		
		return true;
		
	}


	public JsonObject getUserBizPermission() {
		JsonObject jsonObject=new JsonObject();
		//获取固定列
		JsonArray jsonArray_permFixedColumns =new JsonArray();
		getUserFixedClonums(jsonArray_permFixedColumns);
		 //获取业务列
		JsonArray jsonArray_bizColumns =new JsonArray();
		getBizClonums(jsonArray_bizColumns);
		//获取权限列
		JsonArray jsonArray_bizPermColumns =new JsonArray();
		getPermColumns(jsonArray_bizPermColumns);
		//获取用户业务限配置信息
		JsonArray jsonArray_permConfigInfo =new JsonArray();
		getPermConfigInfo(jsonArray_permConfigInfo);
		
		jsonObject.add("permFixedColumns", jsonArray_permFixedColumns);
		jsonObject.add("bizColumns", jsonArray_bizColumns);
		jsonObject.add("bizPermColumns", jsonArray_bizPermColumns);
		jsonObject.add("permConfigInfo", jsonArray_permConfigInfo);

		return jsonObject;
	}
	
	private void getUserFixedClonums(JsonArray jsonArray_permFixedColumns) {
		JsonObject jsonObject_fixedColumn1=new JsonObject();
		jsonObject_fixedColumn1.addProperty("field", "userId");
		jsonObject_fixedColumn1.addProperty("title", "用户id");
		jsonObject_fixedColumn1.addProperty("width","80");
		jsonArray_permFixedColumns.add(jsonObject_fixedColumn1);
		JsonObject jsonObject_fixedColumn2=new JsonObject();
		jsonObject_fixedColumn2.addProperty("field", "userName");
		jsonObject_fixedColumn2.addProperty("title", "用户名称");
		jsonObject_fixedColumn2.addProperty("width","150");
		jsonArray_permFixedColumns.add(jsonObject_fixedColumn2);
		JsonObject jsonObject_fixedColumn3=new JsonObject();
		jsonObject_fixedColumn3.addProperty("field", "permissionId");
		jsonObject_fixedColumn3.addProperty("title", "权限id");
		jsonObject_fixedColumn3.addProperty("width","80");
		jsonArray_permFixedColumns.add(jsonObject_fixedColumn3);
		JsonObject jsonObject_fixedColumn4=new JsonObject();
		jsonObject_fixedColumn4.addProperty("field", "permissionName");
		jsonObject_fixedColumn4.addProperty("title", "权限名称");
		jsonObject_fixedColumn4.addProperty("width","150");
		jsonArray_permFixedColumns.add(jsonObject_fixedColumn4);
		return;
	}
	
	private void getBizClonums(JsonArray jsonArray_flowColumns) {
		List<DMBusiness> listDMBusiness=new ArrayList<DMBusiness>();
		dmBizRepository.getAllDMBusiness(listDMBusiness);
		for (DMBusiness dmBusiness : listDMBusiness) {
			JsonObject jsonObject_biz=new JsonObject();
			jsonObject_biz.addProperty("title", dmBusiness.getName());
			jsonObject_biz.addProperty("colspan", 3);
			jsonArray_flowColumns.add(jsonObject_biz);
		}
	}
	
	private void getPermColumns(JsonArray jsonArray_flowPermColumns) {
		List<DMBusiness> listDMBusiness=new ArrayList<DMBusiness>();
		dmBizRepository.getAllDMBusiness(listDMBusiness);
		for (DMBusiness dmBusiness : listDMBusiness) {
			JsonObject jsonObject_fixedcolumn1=new JsonObject();
			jsonObject_fixedcolumn1.addProperty("field",dmBusiness.getBizId()+"系统配置");
			jsonObject_fixedcolumn1.addProperty("title","系统配置");
			jsonObject_fixedcolumn1.addProperty("width","80");
			jsonArray_flowPermColumns.add(jsonObject_fixedcolumn1);
			JsonObject jsonObject_fixedcolumn2=new JsonObject();
			jsonObject_fixedcolumn2.addProperty("field",dmBusiness.getBizId()+"数据管理");
			jsonObject_fixedcolumn2.addProperty("title","数据管理");
			jsonObject_fixedcolumn2.addProperty("width","80");
			jsonArray_flowPermColumns.add(jsonObject_fixedcolumn2);
			JsonObject jsonObject_fixedcolumn3=new JsonObject();
			jsonObject_fixedcolumn3.addProperty("field",dmBusiness.getBizId()+"数据源池");
			jsonObject_fixedcolumn3.addProperty("title","数据源池");
			jsonObject_fixedcolumn3.addProperty("width","80");
			jsonArray_flowPermColumns.add(jsonObject_fixedcolumn3);
		}
		return;
	}
	
	private void getPermConfigInfo(JsonArray jsonArray_permConfigInfo) {
		Connection dbConn = null;
		String szSql ="";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<BizMapPermission> listBizMapPermission=new ArrayList<BizMapPermission>();
		try {
			dbConn =this.getDbConnection();
			szSql = "select BUSINESSID,PERMISSIONID,ITEMNAME from HASYS_DM_PER_MAP_POOL order by PERMISSIONID";
			stmt = dbConn.prepareStatement(szSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				BizMapPermission bizMapPermission=new BizMapPermission();
				bizMapPermission.setBizId(rs.getInt(1));
				bizMapPermission.setPermissionId(rs.getInt(2));
				bizMapPermission.setPermItemName(rs.getString(3));
				listBizMapPermission.add(bizMapPermission);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		finally {
			DbUtil.DbCloseConnection(dbConn);
			DbUtil.DbCloseQuery(rs, stmt);
		}
		List<User> listUsers = userRepository.getAllUser(); 
		for(int row=0;row<listUsers.size();row++){
			User user=listUsers.get(row);
			JsonObject jsonObject_perm=new JsonObject();	
			jsonObject_perm.addProperty("userId", user.getId());
			jsonObject_perm.addProperty("userName", user.getName());	
			UserWithPermission userWithPermission = userRepository.getUserWithPermissionById(user.getId());
			if (userWithPermission.getPermission()!=null) {				
				jsonObject_perm.addProperty("permissionId", userWithPermission.getPermissionId());
				jsonObject_perm.addProperty("permissionName", userWithPermission.getPermissionName());
				for(int col=0;col<listBizMapPermission.size();col++)
				{
					BizMapPermission bizMapPermission=listBizMapPermission.get(col);	
					if(userWithPermission.getPermissionId()==bizMapPermission.getPermissionId())
					{
						if(bizMapPermission.getPermItemName()!=null)
						{
							jsonObject_perm.addProperty(""+bizMapPermission.getBizId()+bizMapPermission.getPermItemName()+"", 1);
						}
					}	
				}	
			}
			jsonArray_permConfigInfo.add(jsonObject_perm);		
		}	
	}
}
