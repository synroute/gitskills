package hiapp.modules.dmagent.data;

import hiapp.modules.dmagent.ConfigPageEnume;
import hiapp.modules.dmagent.ConfigTypeEnume;
import hiapp.modules.dmagent.QueryRequest;
import hiapp.modules.dmagent.QueryTemplate;
import hiapp.modules.dmagent.TableNameEnume;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.data.PermissionRepository;
import hiapp.system.buinfo.data.UserRepository;
import hiapp.utils.DbUtil;
import hiapp.utils.base.HiAppException;
import hiapp.utils.database.BaseRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

@Repository
public class CustomerRepository extends BaseRepository {
	private final String INPUT_TIME_TEMPLATE = "yyyy-MM-dd HH24:mi:ss";
	private final String OUTPUT_TIME_TEMPLATE = "yyyy-MM-dd HH:mm:ss";

	public void addColumnIndex(List<Map<String, Object>> list) {
		AtomicInteger atomicInteger = new AtomicInteger(0);
		for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator
				.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			map.put("index", atomicInteger.incrementAndGet());
		}
	}

	public List<Map<String, Object>> getJieGuoTableColumn(String sql,
			String bizId) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String jieGuoTableName = TableNameEnume.JIEGUOTABLENAME.getPrefix()
				+ bizId + TableNameEnume.JIEGUOTABLENAME.getSuffix();

		Connection dbConn = this.getDbConnection();
		PreparedStatement stmt = dbConn.prepareStatement(sql);
		stmt.setString(1, jieGuoTableName);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("COLUMNNAME", TableNameEnume.JIEGUOTABLENAME.getAbbr()+"." + rs.getString(1));
			map.put("COLUMNNAMECH", rs.getString(2));
			map.put("COLUMNDESCRIPTION", rs.getString(3));
			map.put("dataType", rs.getString(4));
			map.put("LENGTH", rs.getInt(5));
			list.add(map);
		}
		DbUtil.DbCloseQuery(rs, stmt);
		DbUtil.DbCloseConnection(dbConn);
		return list;
	}

	public List<Map<String, Object>> getInputTableColumn(String sql,
			String bizId) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String inputTableName = TableNameEnume.INPUTTABLENAME.getPrefix()
				+ bizId + TableNameEnume.INPUTTABLENAME.getSuffix();
		Connection dbConn = this.getDbConnection();
		PreparedStatement stmt = dbConn.prepareStatement(sql);
		stmt.setString(1, inputTableName);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("COLUMNNAME", TableNameEnume.INPUTTABLENAME.getAbbr()+"." + rs.getString(1));
			map.put("COLUMNNAMECH", rs.getString(2));
			map.put("COLUMNDESCRIPTION", rs.getString(3));
			map.put("dataType", rs.getString(4));
			map.put("LENGTH", rs.getInt(5));
			list.add(map);
		}
		DbUtil.DbCloseQuery(rs, stmt);
		DbUtil.DbCloseConnection(dbConn);
		return list;
	}

	public List<Map<String, Object>> getPresetTableColumn(String sql,
			String bizId) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String presetTableName = TableNameEnume.PRESETTABLENAME.getPrefix()
				+ bizId + TableNameEnume.INPUTTABLENAME.getSuffix();
		Connection dbConn = this.getDbConnection();
		PreparedStatement stmt = dbConn.prepareStatement(sql);
		stmt.setString(1, presetTableName);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("COLUMNNAME", TableNameEnume.INPUTTABLENAME.getAbbr()+"." + rs.getString(1));
			map.put("COLUMNNAMECH", rs.getString(2));
			map.put("COLUMNDESCRIPTION", rs.getString(3));
			map.put("dataType", rs.getString(4));
			map.put("LENGTH", rs.getInt(5));
			list.add(map);
		}
		DbUtil.DbCloseQuery(rs, stmt);
		DbUtil.DbCloseConnection(dbConn);
		return list;
	}

	/**
	 * 获取配置查询模板时需要使用的候选列
	 * 
	 * @param bizId
	 * @param deployPage
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getCandidadeColumn(String bizId,
			String configPage) throws SQLException {

		String sql = "SELECT COLUMNNAME,COLUMNNAMECH,COLUMNDESCRIPTION,DATATYPE,LENGTH FROM HASYS_WORKSHEETCOLUMN A,HASYS_WORKSHEET B WHERE A.WORKSHEETID = B.ID AND B.NAME = ？";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.addAll(getJieGuoTableColumn(sql, bizId));
		list.addAll(TableNameEnume.JIEGUOTABLENAME.getCandidadeColumn());

		// 我的客户
		if (ConfigPageEnume.MYCUSTOMERS.getName().equals(configPage)) {
			list.addAll(getInputTableColumn(sql, bizId));
			// list.addAll(TableNameEnume.INPUTTABLENAME.getCandidadeColumn());
		}
		// 联系计划
		else if (ConfigPageEnume.CONTACTPLAN.getName().equals(configPage)) {
			list.addAll(getPresetTableColumn(sql, bizId));
			list.addAll(TableNameEnume.PRESETTABLENAME.getCandidadeColumn());
		}
		// 全部客户
		else if (ConfigPageEnume.ALLCUSTOMERS.getName().equals(configPage)) {
			list.addAll(getInputTableColumn(sql, bizId));
			list.addAll(getPresetTableColumn(sql, bizId));
			list.addAll(TableNameEnume.PRESETTABLENAME.getCandidadeColumn());
		}

		addColumnIndex(list);
		return list;
	}

	/**
	 * 保存配置好的查询模板
	 * 
	 * @param queryTemplate
	 * @return
	 * @throws HiAppException
	 */
	public boolean saveQueryTemplate(QueryTemplate queryTemplate)
			throws Exception {
		String bizId = queryTemplate.getBizId();
		String configPage = queryTemplate.getConfigPage();
		String configType = queryTemplate.getConfigType();
		List<Map<String, String>> configTemplate = queryTemplate
				.getConfigTemplate();
		String sql = "INSERT INTO HASYS_DM_CUPAGETEMPLATE (BUSINESSID,CONFIGPAGE,CONFIGTYPE,CONFIGTEMPLATE) VALUES (?,?,?)";
		Connection dbCOnn = this.getDbConnection();
		PreparedStatement stmt = dbCOnn.prepareStatement(sql);
		stmt.setString(1, bizId);
		stmt.setString(2, configPage);
		stmt.setString(3, configType);
		stmt.setString(4, new Gson().toJson(configTemplate));
		stmt.execute();

		DbUtil.DbCloseExecute(stmt);
		DbUtil.DbCloseConnection(dbCOnn);

		return true;
	}

	/**
	 * 获取查询模板
	 * 
	 * @return
	 * @throws SQLException
	 */
	public String getQueryTemplate(QueryTemplate queryTemplate)
			throws SQLException {
		String result =  null;
		String bizId = queryTemplate.getBizId();
		String configPage = queryTemplate.getConfigPage();
		String configType = queryTemplate.getConfigType();

		String sql = "SELECT CONFIGTEMPLATE FROM HASYS_DM_CUPAGETEMPLATE WHERE BUSINESSID = ? AND CONFIGPAGE = ? AND CONFIGTYPE = ?";
		Connection dbConn = this.getDbConnection();
		PreparedStatement stmt = dbConn.prepareStatement(sql);
		stmt.setString(1, bizId);
		stmt.setString(2, configPage);
		stmt.setString(3, configType);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			result = rs.getString(1);
		}
		DbUtil.DbCloseQuery(rs, stmt);
		DbUtil.DbCloseConnection(dbConn);
		return result;
	}
	
	
	

	/**
	 * 支持坐席根据不同业务和管理员自定义配置的查询条件查询我的客户
	 * 
	 * @param queryRequest
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String,String>>  queryMyCustomers(QueryRequest queryRequest,String userId)
			throws SQLException {
		
		StringBuffer sb = new StringBuffer();
		String bizId = queryRequest.getBizId();
		
		QueryTemplate queryTemplate = new QueryTemplate();
		queryTemplate.setBizId(bizId);
		queryTemplate.setConfigPage(ConfigPageEnume.MYCUSTOMERS.getName());
		queryTemplate.setConfigType(ConfigTypeEnume.CUSTOMERLIST.getName());
		String template = getQueryTemplate(queryTemplate);

		//要查哪些字段
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = new Gson().fromJson(template,
				List.class);
		
		sb.append("SELECT ");
		for (Map<String, String> map : list) {
			String columnName = map.get("columnName");
			sb.append("`"+columnName+"`");
			sb.append(",");
		}
		sb.append(" FROM ");
		
		
		//要查哪些表
		sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
		sb.append(bizId);
		sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
		sb.append(" AS ");
		sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());
		
		sb.append(",");
		
		sb.append(TableNameEnume.JIEGUOTABLENAME.getPrefix());
		sb.append(bizId);
		sb.append(TableNameEnume.JIEGUOTABLENAME.getSuffix());
		sb.append(" AS ");
		sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr());
		
		sb.append(" WHERE ");
		
		//查询条件
		sb.append("MODIFYUSERID");
		sb.append(" = ");
		sb.append("'"+userId+"'");
		sb.append(" AND ");
		
		
		List<Map<String, String>> queryCondition = queryRequest
				.getQueryCondition();
	
		
		Map<String, String> map1 = new HashMap<String, String>();
		Map<String, String> map2 = new HashMap<String, String>();
		
		for (Map<String, String> map : queryCondition) {
			String field = map.get("field");
			String value = map.get("value");
			
			//时间字段使用范围查询
			if("date".equalsIgnoreCase(map.get("dataType"))){
				if(map1.get(field)==null){
					map1.put(field, value);
				}else{
					map2.put(field, value);
				}
			}else{
				
				//非时间字段使用模糊查询
				sb.append(field);
				sb.append(" LIKE '");
				sb.append(value);
				sb.append("' AND ");
			}
		}

		//时间字段使用范围查询
		for(Entry<String, String> entry:map1.entrySet()){
			String key = entry.getKey();
			sb.append(key);
			sb.append(" BETWEEN ");
			sb.append("TO_DATE('"+entry.getValue()+"','"+INPUT_TIME_TEMPLATE+"')");
			sb.append(" AND ");
			sb.append("TO_DATE('"+map2.get(key)+"','"+INPUT_TIME_TEMPLATE+"')");
			sb.append(" OR ");
			sb.append(key);
			sb.append(" BETWEEN ");
			sb.append("TO_DATE("+map2.get(key)+"','"+INPUT_TIME_TEMPLATE+"')");
			sb.append(" AND ");
			sb.append("TO_DATE('"+entry.getValue()+"','"+INPUT_TIME_TEMPLATE+"')");
		}
		
		Connection dbConn = this.getDbConnection();
		PreparedStatement stmt = dbConn.prepareStatement(sb.toString());
		ResultSet rs = stmt.executeQuery();
		
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(OUTPUT_TIME_TEMPLATE);
		
		//获取查询结果
		while(rs.next()){
			Map<String,String> m = new HashMap<String, String>();
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				if("date".equalsIgnoreCase(map.get("dataType"))){
					m.put(columnName, simpleDateFormat.format(rs.getDate(columnName)));
				}else{
					m.put(columnName, rs.getString(columnName));
				}
			}
			result.add(m);
		}
		
		DbUtil.DbCloseQuery(rs, stmt);
		DbUtil.DbCloseConnection(dbConn);
		
		return result;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义的查询条件查询我的预约或待跟进的客户
	 * 
	 * @param queryRequest
	 * @return
	 * @throws SQLException 
	 */
	public List<Map<String,String>> queryMyPresetCustomers(QueryRequest queryRequest,String userId) throws SQLException {
		StringBuffer sb = new StringBuffer();
		String bizId = queryRequest.getBizId();
		
		QueryTemplate queryTemplate = new QueryTemplate();
		queryTemplate.setBizId(bizId);
		queryTemplate.setConfigPage(ConfigPageEnume.MYCUSTOMERS.getName());
		queryTemplate.setConfigType(ConfigTypeEnume.CUSTOMERLIST.getName());
		String template = getQueryTemplate(queryTemplate);

		//要查哪些字段
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = new Gson().fromJson(template,
				List.class);
		
		sb.append("SELECT ");
		for (Map<String, String> map : list) {
			String columnName = map.get("columnName");
			sb.append("`"+columnName+"`");
			sb.append(",");
		}
		sb.append(" FROM ");
		
		
		//要查哪些表
		sb.append(TableNameEnume.PRESETTABLENAME.getPrefix());
		sb.append(bizId);
		sb.append(TableNameEnume.PRESETTABLENAME.getSuffix());
		sb.append(" AS ");
		sb.append(TableNameEnume.PRESETTABLENAME.getAbbr());
		
		sb.append(",");
		
		sb.append(TableNameEnume.JIEGUOTABLENAME.getPrefix());
		sb.append(bizId);
		sb.append(TableNameEnume.JIEGUOTABLENAME.getSuffix());
		sb.append(" AS ");
		sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr());
		
		sb.append(" WHERE ");
		
		//查询条件
		
		sb.append("MODIFYUSERID");
		sb.append(" = ");
		sb.append("'"+userId+"'");
		sb.append(" AND ");
		
		
		List<Map<String, String>> queryCondition = queryRequest
				.getQueryCondition();
	
		
		Map<String, String> map1 = new HashMap<String, String>();
		Map<String, String> map2 = new HashMap<String, String>();
		
		for (Map<String, String> map : queryCondition) {
			String field = map.get("field");
			String value = map.get("value");
			
			//时间字段使用范围查询
			if("date".equalsIgnoreCase(map.get("dataType"))){
				if(map1.get(field)==null){
					map1.put(field, value);
				}else{
					map2.put(field, value);
				}
			}else{
				
				//非时间字段使用模糊查询
				sb.append(field);
				sb.append(" LIKE '");
				sb.append(value);
				sb.append("' AND ");
			}
		}

		//时间字段使用范围查询
		for(Entry<String, String> entry:map1.entrySet()){
			String key = entry.getKey();
			sb.append(key);
			sb.append(" BETWEEN ");
			sb.append("TO_DATE('"+entry.getValue()+"','"+INPUT_TIME_TEMPLATE+"')");
			sb.append(" AND ");
			sb.append("TO_DATE('"+map2.get(key)+"','"+INPUT_TIME_TEMPLATE+"')");
			sb.append(" OR ");
			sb.append(key);
			sb.append(" BETWEEN ");
			sb.append("TO_DATE("+map2.get(key)+"','"+INPUT_TIME_TEMPLATE+"')");
			sb.append(" AND ");
			sb.append("TO_DATE('"+entry.getValue()+"','"+INPUT_TIME_TEMPLATE+"')");
		}
		
		Connection dbConn = this.getDbConnection();
		PreparedStatement stmt = dbConn.prepareStatement(sb.toString());
		ResultSet rs = stmt.executeQuery();
		
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(OUTPUT_TIME_TEMPLATE);
		
		//获取查询结果
		while(rs.next()){
			Map<String,String> m = new HashMap<String, String>();
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				if("date".equalsIgnoreCase(map.get("dataType"))){
					m.put(columnName, simpleDateFormat.format(rs.getDate(columnName)));
				}else{
					m.put(columnName, rs.getString(columnName));
				}
			}
			result.add(m);
		}
		
		DbUtil.DbCloseQuery(rs, stmt);
		DbUtil.DbCloseConnection(dbConn);
		
		return result;
	}
	
	public Integer getDataPoolIdByPermissionId(int permissionId) throws SQLException{
		Integer dataPoolId = null;
		Connection dbConn = this.getDbConnection();
		String sql = "SELECT DATAPOOLID FROM HASYS_DM_PER_MAP_POOL WHERE PERMISSIONID = ?";
		PreparedStatement stmt = dbConn.prepareStatement(sql);
		stmt.setInt(1, permissionId);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			dataPoolId = rs.getInt(1);
		}
		DbUtil.DbCloseQuery(rs, stmt);
		DbUtil.DbCloseConnection(dbConn);
		return dataPoolId;
	}
	
	public List<String> getChildrenDataPoolNamesById(int id) throws SQLException{
		Connection dbCOnn = this.getDbConnection();
		return null;
	}

	/**
	 * 支持根据不同业务和管理员自定义的查询条件查询所有客户
	 * 
	 * @param queryRequest
	 * @return
	 * @throws SQLException 
	 */
	public List<Map<String,String>> queryAllCustomers(QueryRequest queryRequest,String userId) throws SQLException {
		StringBuffer sb = new StringBuffer();
		String bizId = queryRequest.getBizId();
		
		QueryTemplate queryTemplate = new QueryTemplate();
		queryTemplate.setBizId(bizId);
		queryTemplate.setConfigPage(ConfigPageEnume.MYCUSTOMERS.getName());
		queryTemplate.setConfigType(ConfigTypeEnume.CUSTOMERLIST.getName());
		String template = getQueryTemplate(queryTemplate);

		//要查哪些字段
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = new Gson().fromJson(template,
				List.class);
		
		sb.append("SELECT ");
		for (Map<String, String> map : list) {
			String columnName = map.get("columnName");
			sb.append("`"+columnName+"`");
			sb.append(",");
		}
		sb.append(" FROM ");
		
		
		//要查哪些表
		sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
		sb.append(bizId);
		sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
		sb.append(" AS ");
		sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());
		
		sb.append(",");
		
		sb.append(TableNameEnume.PRESETTABLENAME.getPrefix());
		sb.append(bizId);
		sb.append(TableNameEnume.PRESETTABLENAME.getSuffix());
		sb.append(" AS ");
		sb.append(TableNameEnume.PRESETTABLENAME.getAbbr());
		
		sb.append(",");
		
		sb.append(TableNameEnume.JIEGUOTABLENAME.getPrefix());
		sb.append(bizId);
		sb.append(TableNameEnume.JIEGUOTABLENAME.getSuffix());
		sb.append(" AS ");
		sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr());
		
		sb.append(" WHERE ");
		
		//查询条件
		
		sb.append("MODIFYUSERID");
		sb.append(" = ");
		sb.append("'"+userId+"'");
		sb.append(" AND ");
		
		
		List<Map<String, String>> queryCondition = queryRequest
				.getQueryCondition();
	
		
		Map<String, String> map1 = new HashMap<String, String>();
		Map<String, String> map2 = new HashMap<String, String>();
		
		for (Map<String, String> map : queryCondition) {
			String field = map.get("field");
			String value = map.get("value");
			
			//时间字段使用范围查询
			if("date".equalsIgnoreCase(map.get("dataType"))){
				if(map1.get(field)==null){
					map1.put(field, value);
				}else{
					map2.put(field, value);
				}
			}else{
				
				//非时间字段使用模糊查询
				sb.append(field);
				sb.append(" LIKE '");
				sb.append(value);
				sb.append("' AND ");
			}
		}

		//时间字段使用范围查询
		for(Entry<String, String> entry:map1.entrySet()){
			String key = entry.getKey();
			sb.append(key);
			sb.append(" BETWEEN ");
			sb.append("TO_DATE('"+entry.getValue()+"','"+INPUT_TIME_TEMPLATE+"')");
			sb.append(" AND ");
			sb.append("TO_DATE('"+map2.get(key)+"','"+INPUT_TIME_TEMPLATE+"')");
			sb.append(" OR ");
			sb.append(key);
			sb.append(" BETWEEN ");
			sb.append("TO_DATE("+map2.get(key)+"','"+INPUT_TIME_TEMPLATE+"')");
			sb.append(" AND ");
			sb.append("TO_DATE('"+entry.getValue()+"','"+INPUT_TIME_TEMPLATE+"')");
		}
		
		Connection dbConn = this.getDbConnection();
		PreparedStatement stmt = dbConn.prepareStatement(sb.toString());
		ResultSet rs = stmt.executeQuery();
		
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(OUTPUT_TIME_TEMPLATE);
		
		//获取查询结果
		while(rs.next()){
			Map<String,String> m = new HashMap<String, String>();
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				if("date".equalsIgnoreCase(map.get("dataType"))){
					m.put(columnName, simpleDateFormat.format(rs.getDate(columnName)));
				}else{
					m.put(columnName, rs.getString(columnName));
				}
			}
			result.add(m);
		}
		
		DbUtil.DbCloseQuery(rs, stmt);
		DbUtil.DbCloseConnection(dbConn);
		
		return result;
	}

}
