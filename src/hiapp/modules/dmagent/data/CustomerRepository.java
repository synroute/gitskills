package hiapp.modules.dmagent.data;

import hiapp.modules.dmagent.ConfigPageEnume;
import hiapp.modules.dmagent.ConfigTypeEnume;
import hiapp.modules.dmagent.QueryRequest;
import hiapp.modules.dmagent.QueryTemplate;
import hiapp.modules.dmagent.TableNameEnume;
import hiapp.system.buinfo.Permission;
import hiapp.system.buinfo.RoleInGroupSet;
import hiapp.system.buinfo.data.PermissionRepository;
import hiapp.system.buinfo.data.UserRepository;
import hiapp.utils.DbUtil;
import hiapp.utils.base.HiAppException;
import hiapp.utils.database.BaseRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Repository
public class CustomerRepository extends BaseRepository {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PermissionRepository permissionRepository;

	private final String INPUT_TIME_TEMPLATE = "MM/dd/YYYY HH24:mi:ss";
	private final String INPUT_TIME_JTEMPLATE = "MM/dd/YYYY HH:mm:ss";
	private final String OUTPUT_TIME_TEMPLATE = "yyyy/MM/dd HH:mm:ss";

	// 给返回的候选列添加序号
	public void addColumnIndex(List<Map<String, Object>> list) {
		AtomicInteger atomicInteger = new AtomicInteger(0);
		for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator
				.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			map.put("index", atomicInteger.incrementAndGet());
		}
	}

	// 获取指定业务中用户自定义表中用户自定义的列用于候选列
	public List<Map<String, Object>> getJieGuoTableColumn(String sql,
			String bizId) throws HiAppException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String jieGuoTableName = TableNameEnume.JIEGUOTABLENAME.getPrefix()
					+ bizId + TableNameEnume.JIEGUOTABLENAME.getSuffix();

			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sql);
			stmt.setObject(1, jieGuoTableName);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("COLUMNNAME", TableNameEnume.JIEGUOTABLENAME.getAbbr()
						+ "." + rs.getString(1));
				map.put("COLUMNNAMECH", rs.getString(2));
				map.put("COLUMNDESCRIPTION", rs.getString(3));
				map.put("dataType", rs.getString(4));
				map.put("LENGTH", rs.getInt(5));
				list.add(map);
			}
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
			throw new HiAppException("getJieGuoTableColumn SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}
		return list;
	}

	// 获取指定业务中导入表中用户自定义的列用于候选列
	public List<Map<String, Object>> getInputTableColumn(String sql,
			String bizId) throws HiAppException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String inputTableName = TableNameEnume.INPUTTABLENAME.getPrefix()
					+ bizId + TableNameEnume.INPUTTABLENAME.getSuffix();
			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sql);
			stmt.setObject(1, inputTableName);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("COLUMNNAME", TableNameEnume.INPUTTABLENAME.getAbbr()
						+ "." + rs.getString(1));
				map.put("COLUMNNAMECH", rs.getString(2));
				map.put("COLUMNDESCRIPTION", rs.getString(3));
				map.put("dataType", rs.getString(4));
				map.put("LENGTH", rs.getInt(5));
				list.add(map);
			}
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
			throw new HiAppException("getInputTableColumn SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return list;
	}

	// 获取指定业务中候选表中用户自定义的列用于候选列
	public List<Map<String, Object>> getPresetTableColumn(String sql,
			String bizId) throws HiAppException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String presetTableName = TableNameEnume.PRESETTABLENAME.getPrefix()
					+ bizId + TableNameEnume.INPUTTABLENAME.getSuffix();
			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sql);
			stmt.setObject(1, presetTableName);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("COLUMNNAME", TableNameEnume.INPUTTABLENAME.getAbbr()
						+ "." + rs.getString(1));
				map.put("COLUMNNAMECH", rs.getString(2));
				map.put("COLUMNDESCRIPTION", rs.getString(3));
				map.put("dataType", rs.getString(4));
				map.put("LENGTH", rs.getInt(5));
				list.add(map);
			}
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
			throw new HiAppException("getPresetTableColumn SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return list;
	}

	/**
	 * 获取配置查询模板时需要使用的候选列
	 * 
	 * @param bizId
	 * @param deployPage
	 * @return
	 * @throws HiAppException
	 */
	public List<Map<String, Object>> getCandidadeColumn(String bizId,
			String configPage) throws HiAppException {

		String sql = "SELECT COLUMNNAME,COLUMNNAMECH,COLUMNDESCRIPTION,DATATYPE,LENGTH FROM HASYS_WORKSHEETCOLUMN A,HASYS_WORKSHEET B WHERE A.ISSYSCOLUMN = 0 AND A.WORKSHEETID = B.ID AND B.NAME = ?";

		// 获取用户自定义表中用户自定义的候选列
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			list.addAll(getJieGuoTableColumn(sql, bizId));
			// 获取用户自定义表中固定的候选列
			list.addAll(TableNameEnume.JIEGUOTABLENAME.getCandidadeColumn());

			// 我的客户
			if (ConfigPageEnume.MYCUSTOMERS.getName().equals(configPage)) {
				// 获取导入表中用户自定义的候选列
				list.addAll(getInputTableColumn(sql, bizId));
				// 获取导入表中固定的候选列
				list.addAll(TableNameEnume.INPUTTABLENAME.getCandidadeColumn());
			}
			// 联系计划
			else if (ConfigPageEnume.CONTACTPLAN.getName().equals(configPage)) {
				// 获取预约表中用户自定义的候选列
				list.addAll(getPresetTableColumn(sql, bizId));
				// 获取预约表中固定的候选列
				list.addAll(TableNameEnume.PRESETTABLENAME.getCandidadeColumn());
			}
			// 全部客户
			else if (ConfigPageEnume.ALLCUSTOMERS.getName().equals(configPage)) {
				// 获取导入表中用户自定义的候选列
				list.addAll(getInputTableColumn(sql, bizId));
				// 获取预约表中用户自定义的候选列
				list.addAll(getPresetTableColumn(sql, bizId));
				// 获取预约表中固定的候选列
				list.addAll(TableNameEnume.PRESETTABLENAME.getCandidadeColumn());
			}
		} catch (HiAppException e) {
			throw e;
		}

		return list;
	}

	/**
	 * 保存配置好的查询模板
	 * 
	 * @param queryTemplate
	 * @return
	 */
	public boolean saveQueryTemplate(QueryTemplate queryTemplate) {
		String bizId = queryTemplate.getBizId();
		String configPage = queryTemplate.getConfigPage();
		String configType = queryTemplate.getConfigType();
		List<Map<String, String>> configTemplate = queryTemplate
				.getConfigTemplate();

		Connection dbCOnn = null;
		PreparedStatement stmt = null;

		try {
			String sql = "INSERT INTO HASYS_DM_CUPAGETEMPLATE (ID,BUSINESSID,CONFIGPAGE,CONFIGTYPE,CONFIGTEMPLATE) VALUES (SEQ_HASYS_DM_CUPAGETEMPLATE.NEXTVAL,?,?,?,?)";
			dbCOnn = this.getDbConnection();
			stmt = dbCOnn.prepareStatement(sql);
			stmt.setObject(1, bizId);
			stmt.setObject(2, configPage);
			stmt.setObject(3, configType);
			stmt.setObject(4, new Gson().toJson(configTemplate));
			stmt.execute();
		} catch (SQLException e) {
			if (e instanceof SQLIntegrityConstraintViolationException) {
				DbUtil.DbCloseExecute(stmt);
				try {
					String sql = "UPDATE HASYS_DM_CUPAGETEMPLATE SET CONFIGTEMPLATE = ? WHERE BUSINESSID = ? AND CONFIGPAGE = ? AND CONFIGTYPE = ?";
					stmt = dbCOnn.prepareStatement(sql);
					stmt.setObject(2, bizId);
					stmt.setObject(3, configPage);
					stmt.setObject(4, configType);
					stmt.setObject(1, new Gson().toJson(configTemplate));
					stmt.execute();
					return true;
				} catch (SQLException e1) {
					e.printStackTrace();
					return false;
				}
			} else {
				e.printStackTrace();
				return false;
			}
		} finally {
			DbUtil.DbCloseExecute(stmt);
			DbUtil.DbCloseConnection(dbCOnn);
		}

		return true;
	}

	/**
	 * 获取查询模板
	 * 
	 * @return
	 * @throws HiAppException
	 */
	public String getQueryTemplate(QueryTemplate queryTemplate)
			throws HiAppException {
		String result = null;
		String bizId = queryTemplate.getBizId();
		String configPage = queryTemplate.getConfigPage();
		String configType = queryTemplate.getConfigType();

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = "SELECT CONFIGTEMPLATE FROM HASYS_DM_CUPAGETEMPLATE WHERE BUSINESSID = ? AND CONFIGPAGE = ? AND CONFIGTYPE = ?";

		try {
			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sql);
			stmt.setObject(1, bizId);
			stmt.setObject(2, configPage);
			stmt.setObject(3, configType);
			rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
			throw new HiAppException("getQueryTemplate SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return result;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义配置的查询条件查询我的客户数量
	 * 
	 * @return
	 * @throws HiAppException
	 */
	public int queryMyCustomersCount(QueryRequest queryRequest, String userId)
			throws HiAppException {
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		int count = 0;
		try {
			String bizId = queryRequest.getBizId();
			sb.append("SELECT COUNT(*) ");
			sb.append(" FROM ");
			// 要查哪些表
			sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());
			sb.append(",");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.JIEGUOTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr());
			sb.append(" WHERE ");
			// 查询条件
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);

			sb.append(" OR ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);

			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(1);

			sb.append(" AND ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(1);

			List<Map<String, String>> queryCondition = queryRequest
					.getQueryCondition();

			if (queryCondition != null) {
				if (queryCondition.size() > 0) {
					sb.append(" AND ");
				}

				Map<String, String> map1 = new HashMap<String, String>();
				Map<String, String> map2 = new HashMap<String, String>();

				for (Map<String, String> map : queryCondition) {
					String field = map.get("field");
					String value = map.get("value");
					String type = map.get("dataType");
					if (value != null) {
						// 时间字段使用范围查询
						if (type != null && type.toLowerCase().contains("date")) {
							try {
								new SimpleDateFormat(INPUT_TIME_JTEMPLATE)
										.parse(value);
								if (map1.get(field) == null) {
									map1.put(field, value);
								} else {
									map2.put(field, value);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						} else {

							// 非时间字段使用模糊查询
							sb.append(field);
							sb.append(" LIKE '%");
							sb.append(value);
							sb.append("%' AND ");
						}
					}
				}

				if (map1.isEmpty()) {
					sb = new StringBuffer(sb.substring(0, sb.length() - 5));
				}

				// 时间字段使用范围查询
				for (Entry<String, String> entry : map1.entrySet()) {
					String key = entry.getKey();
					sb.append("(" + key);
					sb.append(" BETWEEN ");
					sb.append("TO_DATE('" + entry.getValue() + "','"
							+ INPUT_TIME_TEMPLATE + "')");
					sb.append(" AND ");
					sb.append("TO_DATE('" + map2.get(key) + "','"
							+ INPUT_TIME_TEMPLATE + "'))");
					sb.append(" OR ");
					sb.append("(" + key);
					sb.append(" BETWEEN ");
					sb.append("TO_DATE('" + map2.get(key) + "','"
							+ INPUT_TIME_TEMPLATE + "')");
					sb.append(" AND ");
					sb.append("TO_DATE('" + entry.getValue() + "','"
							+ INPUT_TIME_TEMPLATE + "'))");
				}

			}

			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sb.toString());
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new HiAppException("getMyCustomersCount Exception", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}
		return count;
	}

	/**
	 * 支持坐席查询下一条我的客户
	 * 
	 * @param queryRequest
	 * @param userId
	 * @return
	 * @throws HiAppException
	 */
	public List<List<Map<String, Object>>> queryMyNextCustomer(
			QueryRequest queryRequest, String userId) throws HiAppException {
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<List<Map<String, Object>>> result = new ArrayList<List<Map<String, Object>>>();

		StringBuffer sb = new StringBuffer();
		try {
			String bizId = queryRequest.getBizId();

			QueryTemplate queryTemplate = new QueryTemplate();
			queryTemplate.setBizId(bizId);
			queryTemplate.setConfigPage(ConfigPageEnume.MYCUSTOMERS.getName());
			queryTemplate.setConfigType(ConfigTypeEnume.CUSTOMERLIST.getName());
			String template = getQueryTemplate(queryTemplate);

			sb.append("SELECT ");

			// 要查哪些字段
			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = new Gson().fromJson(template,
					List.class);
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				sb.append(columnName);
				sb.append(",");
			}

			sb.append("ROWNUM RN");

			sb.append(" FROM ");

			// 要查哪些表
			sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());

			sb.append(",");

			sb.append(TableNameEnume.JIEGUOTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.JIEGUOTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr());

			sb.append(" WHERE ");

			// 查询条件
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
			sb.append(" = ");
			sb.append("'" + queryRequest.getIID() + "'");

			sb.append(" AND ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "." + "IID");
			sb.append(" = ");
			sb.append("'" + queryRequest.getIID() + "'");

			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
			sb.append(" = ");
			sb.append("'" + queryRequest.getCID() + "'");

			sb.append(" AND ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "." + "CID");
			sb.append(" = ");
			sb.append("'" + queryRequest.getCID() + "'");

			sb.append(" AND ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "SourceID");
			sb.append(" = ");
			sb.append("'" + queryRequest.getSourceId() + "'");

			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sb.toString());
			rs = stmt.executeQuery();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					OUTPUT_TIME_TEMPLATE);

			// 获取查询结果
			if (rs.next()) {
				List<Map<String, Object>> record = new ArrayList<Map<String, Object>>();
				ListIterator<Map<String, String>> listIterator = list
						.listIterator();
				AtomicInteger atomicInteger = new AtomicInteger(1);
				while (listIterator.hasNext()) {
					Map<String, Object> map = new HashMap<String, Object>();
					Map<String, String> next = listIterator.next();
					String rowNumber = next.get("rowNumber");
					String colNumber = next.get("colNumber");
					String fontColor = next.get("fontColor");
					String type = next.get("dataType");
					if (type != null && type.toLowerCase().contains("date")) {
						map.put("value", simpleDateFormat.format(rs
								.getDate(atomicInteger.getAndIncrement())));
					} else {
						map.put("value",
								rs.getObject(atomicInteger.getAndIncrement()));
					}
					map.put("rowNumber", rowNumber);
					map.put("colNumber", colNumber);
					map.put("fontColor", fontColor);
					record.add(map);
				}
				result.add(record);
			}
		} catch (JsonSyntaxException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyCustomers JsonSyntaxException", 1);
		} catch (HiAppException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyCustomers HiAppException", 1);
		} catch (SQLException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyCustomers SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return result;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义配置的查询条件查询我的客户
	 * 
	 * @param queryRequest
	 * @return
	 * @throws HiAppException
	 */
	public List<List<Map<String, Object>>> queryMyCustomers(
			QueryRequest queryRequest, String userId) throws HiAppException {

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<List<Map<String, Object>>> result = new ArrayList<List<Map<String, Object>>>();

		StringBuffer sb = new StringBuffer();
		try {
			String bizId = queryRequest.getBizId();

			QueryTemplate queryTemplate = new QueryTemplate();
			queryTemplate.setBizId(bizId);
			queryTemplate.setConfigPage(ConfigPageEnume.MYCUSTOMERS.getName());
			queryTemplate.setConfigType(ConfigTypeEnume.CUSTOMERLIST.getName());
			String template = getQueryTemplate(queryTemplate);

			sb.append("SELECT * FROM (SELECT ");

			// 要查哪些字段
			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = new Gson().fromJson(template,
					List.class);
			int flag1 = 0;
			int flag2 = 0;
			int flag3 = 0;
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				if (columnName != null) {
					if (columnName.equals(TableNameEnume.INPUTTABLENAME
							.getAbbr() + "." + "IID")) {
						flag1 = 1;
					} else if (columnName
							.equals(TableNameEnume.INPUTTABLENAME
									.getAbbr() + "." + "CID")) {
						flag2 = 1;
					} else if (columnName
							.equals(TableNameEnume.JIEGUOTABLENAME
									.getAbbr() + "." + "SOURCEID")) {
						flag3 = 1;
					}
				}
			}
			if (flag1 == 0) {
				HashMap<String, String> hashMap1 = new HashMap<String, String>();
				hashMap1.put("columnName",
						TableNameEnume.INPUTTABLENAME.getAbbr() + "."
								+ "IID");
				list.add(hashMap1);
			}
			if (flag2 == 0) {
				HashMap<String, String> hashMap2 = new HashMap<String, String>();
				hashMap2.put("columnName",
						TableNameEnume.INPUTTABLENAME.getAbbr() + "."
								+ "CID");
				list.add(hashMap2);
			}
			if (flag3 == 0) {
				HashMap<String, String> hashMap3 = new HashMap<String, String>();
				hashMap3.put("columnName",
						TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
								+ "SOURCEID");
				list.add(hashMap3);
			}
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				sb.append(columnName);
				sb.append(",");
			}

			sb.append("ROWNUM RN");

			sb.append(" FROM ");

			// 要查哪些表
			sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());

			sb.append(",");

			sb.append(TableNameEnume.JIEGUOTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.JIEGUOTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr());

			sb.append(" WHERE ");

			// 查询条件
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);

			sb.append(" OR ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);

			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(1);

			sb.append(" AND ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(1);

			sb.append(" AND ");
			sb.append("ROWNUM");
			sb.append(" <= ");
			sb.append(queryRequest.getEnd());

			List<Map<String, String>> queryCondition = queryRequest
					.getQueryCondition();

			if (queryCondition != null) {
				if (queryCondition.size() > 0) {
					sb.append(" AND ");
				}

				Map<String, String> map1 = new HashMap<String, String>();
				Map<String, String> map2 = new HashMap<String, String>();

				for (Map<String, String> map : queryCondition) {
					String field = map.get("field");
					String value = map.get("value");
					String type = map.get("dataType");
					if (value != null) {
						// 时间字段使用范围查询
						if (type != null && type.toLowerCase().contains("date")) {
							try {
								new SimpleDateFormat(INPUT_TIME_JTEMPLATE)
										.parse(value);
								if (map1.get(field) == null) {
									map1.put(field, value);
								} else {
									map2.put(field, value);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						} else {

							// 非时间字段使用模糊查询
							sb.append(field);
							sb.append(" LIKE '%");
							sb.append(value);
							sb.append("%' AND ");
						}
					}
				}

				if (map1.isEmpty()) {
					sb = new StringBuffer(sb.substring(0, sb.length() - 5));
				}

				// 时间字段使用范围查询
				for (Entry<String, String> entry : map1.entrySet()) {
					String key = entry.getKey();
					sb.append("(" + key);
					sb.append(" BETWEEN ");
					sb.append("TO_DATE('" + entry.getValue() + "','"
							+ INPUT_TIME_TEMPLATE + "')");
					sb.append(" AND ");
					sb.append("TO_DATE('" + map2.get(key) + "','"
							+ INPUT_TIME_TEMPLATE + "'))");
					sb.append(" OR ");
					sb.append("(" + key);
					sb.append(" BETWEEN ");
					sb.append("TO_DATE('" + map2.get(key) + "','"
							+ INPUT_TIME_TEMPLATE + "')");
					sb.append(" AND ");
					sb.append("TO_DATE('" + entry.getValue() + "','"
							+ INPUT_TIME_TEMPLATE + "'))");
				}
			}

			sb.append(" ORDER BY ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "MODIFYTIME");
			sb.append(" DESC,");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "MODIFYTIME");
			sb.append(" DESC)");

			sb.append(" WHERE ");
			sb.append("RN");
			sb.append(" >= ");
			sb.append(queryRequest.getStart());

			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sb.toString());
			rs = stmt.executeQuery();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					OUTPUT_TIME_TEMPLATE);

			// 获取查询结果
			while (rs.next()) {
				List<Map<String, Object>> record = new ArrayList<Map<String, Object>>();
				ListIterator<Map<String, String>> listIterator = list
						.listIterator();
				AtomicInteger atomicInteger = new AtomicInteger(1);
				while (listIterator.hasNext()) {
					Map<String, Object> map = new HashMap<String, Object>();
					Map<String, String> next = listIterator.next();
					String rowNumber = next.get("rowNumber");
					String colNumber = next.get("colNumber");
					String fontColor = next.get("fontColor");
					String columnName = next.get("columnName");
					String type = next.get("dataType");
					if (type != null && type.toLowerCase().contains("date")) {
						map.put("value", simpleDateFormat.format(rs
								.getDate(atomicInteger.getAndIncrement())));
					} else {
						map.put("value",
								rs.getObject(atomicInteger.getAndIncrement()));
					}
					map.put("rowNumber", rowNumber);
					map.put("colNumber", colNumber);
					map.put("fontColor", fontColor);
					map.put("columnName", columnName);
					record.add(map);
				}
				result.add(record);
			}
		} catch (JsonSyntaxException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyCustomers JsonSyntaxException", 1);
		} catch (HiAppException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyCustomers HiAppException", 1);
		} catch (SQLException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyCustomers SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return result;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义的查询条件查询我的预约或待跟进的客户的数量
	 * 
	 * @param queryRequest
	 * @return
	 * @throws HiAppException
	 * @throws SQLException
	 */
	public int queryMyPresetCustomersCount(QueryRequest queryRequest,
			String userId) throws HiAppException {

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		int count = 0;

		StringBuffer sb = new StringBuffer();
		try {

			String bizId = queryRequest.getBizId();

			sb.append("SELECT COUNT(*) ");

			sb.append(" FROM ");

			// 要查哪些表
			sb.append(TableNameEnume.PRESETTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.PRESETTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr());

			sb.append(",");

			sb.append(TableNameEnume.JIEGUOTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.JIEGUOTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr());

			sb.append(" WHERE ");

			// 查询条件

			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);

			sb.append(" OR ");

			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);

			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(1);

			sb.append(" AND ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(1);

			List<Map<String, String>> queryCondition = queryRequest
					.getQueryCondition();

			if (queryCondition != null) {
				if (queryCondition.size() > 0) {
					sb.append(" AND ");
				}

				Map<String, String> map1 = new HashMap<String, String>();
				Map<String, String> map2 = new HashMap<String, String>();

				for (Map<String, String> map : queryCondition) {
					String field = map.get("field");
					String value = map.get("value");
					String type = map.get("dataType");

					if (value != null) {
						// 时间字段使用范围查询
						if (type != null && type.toLowerCase().contains("date")) {
							try {
								new SimpleDateFormat(INPUT_TIME_JTEMPLATE)
										.parse(value);
								if (map1.get(field) == null) {
									map1.put(field, value);
								} else {
									map2.put(field, value);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						} else {

							// 非时间字段使用模糊查询
							sb.append(field);
							sb.append(" LIKE '%");
							sb.append(value);
							sb.append("%' AND ");
						}
					}
				}

				if (map1.isEmpty()) {
					sb = new StringBuffer(sb.substring(0, sb.length() - 5));
				}

				// 时间字段使用范围查询
				for (Entry<String, String> entry : map1.entrySet()) {
					String key = entry.getKey();
					sb.append("(" + key);
					sb.append(" BETWEEN ");
					sb.append("TO_DATE('" + entry.getValue() + "','"
							+ INPUT_TIME_TEMPLATE + "')");
					sb.append(" AND ");
					sb.append("TO_DATE('" + map2.get(key) + "','"
							+ INPUT_TIME_TEMPLATE + "'))");
					sb.append(" OR ");
					sb.append("(" + key);
					sb.append(" BETWEEN ");
					sb.append("TO_DATE('" + map2.get(key) + "','"
							+ INPUT_TIME_TEMPLATE + "')");
					sb.append(" AND ");
					sb.append("TO_DATE('" + entry.getValue() + "','"
							+ INPUT_TIME_TEMPLATE + "'))");
				}

			}

			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sb.toString());
			rs = stmt.executeQuery();

			// 获取查询结果
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (JsonSyntaxException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException(
					"queryMyPresetCustomers JsonSyntaxException", 1);
		} catch (SQLException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyPresetCustomers SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}
		return count;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义的查询条件查询我的预约或待跟进的客户
	 * 
	 * @param queryRequest
	 * @return
	 * @throws HiAppException
	 * @throws SQLException
	 */
	public List<List<Map<String, Object>>> queryMyPresetCustomers(
			QueryRequest queryRequest, String userId) throws HiAppException {

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		StringBuffer sb = new StringBuffer();
		List<List<Map<String, Object>>> result = new ArrayList<List<Map<String, Object>>>();
		try {

			String bizId = queryRequest.getBizId();

			QueryTemplate queryTemplate = new QueryTemplate();
			queryTemplate.setBizId(bizId);
			queryTemplate.setConfigPage(ConfigPageEnume.CONTACTPLAN.getName());
			queryTemplate.setConfigType(ConfigTypeEnume.CUSTOMERLIST.getName());
			String template = getQueryTemplate(queryTemplate);

			sb.append("SELECT * FROM (SELECT ");

			// 要查哪些字段
			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = new Gson().fromJson(template,
					List.class);
			int flag1 = 0;
			int flag2 = 0;
			int flag3 = 0;
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				if (columnName != null) {
					if (columnName.equals(TableNameEnume.PRESETTABLENAME
							.getAbbr() + "." + "IID")) {
						flag1 = 1;
					} else if (columnName
							.equals(TableNameEnume.PRESETTABLENAME
									.getAbbr() + "." + "CID")) {
						flag2 = 1;
					} else if (columnName
							.equals(TableNameEnume.JIEGUOTABLENAME
									.getAbbr() + "." + "SOURCEID")) {
						flag3 = 1;
					}
				}
			}
			if (flag1 == 0) {
				HashMap<String, String> hashMap1 = new HashMap<String, String>();
				hashMap1.put("columnName",
						TableNameEnume.PRESETTABLENAME.getAbbr() + "."
								+ "IID");
				list.add(hashMap1);
			}
			if (flag2 == 0) {
				HashMap<String, String> hashMap2 = new HashMap<String, String>();
				hashMap2.put("columnName",
						TableNameEnume.PRESETTABLENAME.getAbbr() + "."
								+ "CID");
				list.add(hashMap2);
			}
			if (flag3 == 0) {
				HashMap<String, String> hashMap3 = new HashMap<String, String>();
				hashMap3.put("columnName",
						TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
								+ "SOURCEID");
				list.add(hashMap3);
			}
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				sb.append(columnName);
				sb.append(",");
			}
			sb.append("ROWNUM RN");
			sb.append(" FROM ");

			// 要查哪些表
			sb.append(TableNameEnume.PRESETTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.PRESETTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr());

			sb.append(",");

			sb.append(TableNameEnume.JIEGUOTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.JIEGUOTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr());

			sb.append(" WHERE ");

			// 查询条件

			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);

			sb.append(" OR ");

			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);

			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(1);

			sb.append(" AND ");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(1);

			sb.append(" AND ");
			sb.append("ROWNUM");
			sb.append(" <= ");

			sb.append(queryRequest.getEnd());

			List<Map<String, String>> queryCondition = queryRequest
					.getQueryCondition();

			if (queryCondition != null) {
				if (queryCondition.size() > 0) {
					sb.append(" AND ");
				}

				Map<String, String> map1 = new HashMap<String, String>();
				Map<String, String> map2 = new HashMap<String, String>();

				for (Map<String, String> map : queryCondition) {
					String field = map.get("field");
					String value = map.get("value");
					String type = map.get("dataType");

					if (value != null) {
						// 时间字段使用范围查询
						if (type != null && type.toLowerCase().contains("date")) {
							try {
								new SimpleDateFormat(INPUT_TIME_JTEMPLATE)
										.parse(value);
								if (map1.get(field) == null) {
									map1.put(field, value);
								} else {
									map2.put(field, value);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						} else {

							// 非时间字段使用模糊查询
							sb.append(field);
							sb.append(" LIKE '%");
							sb.append(value);
							sb.append("%' AND ");
						}
					}
				}

				if (map1.isEmpty()) {
					sb = new StringBuffer(sb.substring(0, sb.length() - 5));
				}

				// 时间字段使用范围查询
				for (Entry<String, String> entry : map1.entrySet()) {
					String key = entry.getKey();
					sb.append("(" + key);
					sb.append(" BETWEEN ");
					sb.append("TO_DATE('" + entry.getValue() + "','"
							+ INPUT_TIME_TEMPLATE + "')");
					sb.append(" AND ");
					sb.append("TO_DATE('" + map2.get(key) + "','"
							+ INPUT_TIME_TEMPLATE + "'))");
					sb.append(" OR ");
					sb.append("(" + key);
					sb.append(" BETWEEN ");
					sb.append("TO_DATE('" + map2.get(key) + "','"
							+ INPUT_TIME_TEMPLATE + "')");
					sb.append(" AND ");
					sb.append("TO_DATE('" + entry.getValue() + "','"
							+ INPUT_TIME_TEMPLATE + "'))");
				}

			}

			sb.append(" ORDER BY ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYTIME");
			sb.append(" DESC,");
			sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
					+ "MODIFYTIME");
			sb.append(" DESC)");

			sb.append(" WHERE ");
			sb.append("RN");
			sb.append(" >= ");
			sb.append(queryRequest.getStart());

			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sb.toString());
			rs = stmt.executeQuery();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					OUTPUT_TIME_TEMPLATE);

			// 获取查询结果
			while (rs.next()) {
				List<Map<String, Object>> record = new ArrayList<Map<String, Object>>();
				ListIterator<Map<String, String>> listIterator = list
						.listIterator();
				AtomicInteger atomicInteger = new AtomicInteger(1);
				while (listIterator.hasNext()) {
					Map<String, Object> map = new HashMap<String, Object>();
					Map<String, String> next = listIterator.next();
					String rowNumber = next.get("rowNumber");
					String colNumber = next.get("colNumber");
					String fontColor = next.get("fontColor");
					String columnName = next.get("columnName");
					String type = next.get("dataType");
					if (type != null && type.toLowerCase().contains("date")) {
						map.put("value", simpleDateFormat.format(rs
								.getDate(atomicInteger.getAndIncrement())));
					} else {
						map.put("value",
								rs.getObject(atomicInteger.getAndIncrement()));
					}
					map.put("rowNumber", rowNumber);
					map.put("colNumber", colNumber);
					map.put("fontColor", fontColor);
					map.put("columnName", columnName);
					record.add(map);
				}
				result.add(record);
			}
		} catch (JsonSyntaxException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException(
					"queryMyPresetCustomers JsonSyntaxException", 1);
		} catch (HiAppException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyPresetCustomers HiAppException", 1);
		} catch (SQLException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyPresetCustomers SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return result;
	}

	// 通过权限id和业务id获取数据池id
	public Integer getDataPoolIdByPermissionId(int permissionId, String bizId)
			throws HiAppException {
		Integer dataPoolId = null;
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT DATAPOOLID FROM HASYS_DM_PER_MAP_POOL WHERE PERMISSIONID = ? AND BUSINESSID = ? AND ITEMNAME = '数据管理'";
		try {
			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sql);
			stmt.setInt(1, permissionId);
			stmt.setString(2, bizId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				dataPoolId = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
			throw new HiAppException(
					"getDataPoolIdByPermissionId SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}
		return dataPoolId;
	}

	// 获取当前业务下的所有数据池记录
	public List<Map<String, Object>> getDataPoolByBizId(String bizId)
			throws HiAppException {
		String sql = "SELECT ID,PID,DATAPOOLNAME FROM HASYS_DM_DATAPOOL WHERE BUSINESSID = ?";
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sql);
			stmt.setObject(1, bizId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("dataPoolId", rs.getInt(1));
				map.put("pId", rs.getInt(2));
				map.put("dataPoolName", rs.getString(3));
				list.add(map);
			}
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
			throw new HiAppException(
					"getChildrenDataPoolNamesById SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}
		return list;
	}

	// 获取当前数据池和所有子数据池下的数据池名称
	public void getAllChildrenDataPoolNames(int dataPoolId,
			List<Map<String, Object>> dataPools, Set<String> dataPoolNames) {

		for (Map<String, Object> dataPool : dataPools) {
			int id = (int) dataPool.get("dataPoolId");
			int pId = (int) dataPool.get("pId");
			String dataPoolName = (String) dataPool.get("dataPoolName");
			if (id == dataPoolId) {
				dataPoolNames.add(dataPoolName);
			} else if (pId == dataPoolId) {
				getAllChildrenDataPoolNames(id, dataPools, dataPoolNames);
			}
		}

	}

	// 获取当前数据池和所有子数据池下的坐席id拼接成的字符串
	public String getUserIdsFromDataPool(int dataPoolId, String bizId,
			String userId) throws HiAppException {

		List<Map<String, Object>> dataPools = getDataPoolByBizId(bizId);

		Set<String> dataPoolNames = new HashSet<String>();

		getAllChildrenDataPoolNames(dataPoolId, dataPools, dataPoolNames);

		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(userId);
		sb.append(",");
		for (String string : dataPoolNames) {
			Pattern pattern = Pattern.compile("\\D*(\\d+)\\D*");
			Matcher matcher = pattern.matcher(string);
			while (matcher.find()) {
				String group = matcher.group(1);
				sb.append(group);
				sb.append(",");
			}
		}
		sb = new StringBuffer(sb.substring(0, sb.length() - 1));
		sb.append(")");
		return sb.toString();
	}

	/**
	 * 支持根据不同业务和管理员自定义的查询条件查询所有客户的数量
	 * 
	 * @param queryRequest
	 * @return
	 * @throws HiAppException
	 * @throws SQLException
	 */
	public int queryAllCustomersCount(QueryRequest queryRequest, String userId)
			throws HiAppException {
		StringBuffer sb = new StringBuffer();

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			String bizId = queryRequest.getBizId();

			RoleInGroupSet roleInGroupSet = userRepository
					.getRoleInGroupSetByUserId(userId);
			Permission permission = permissionRepository
					.getPermission(roleInGroupSet);
			int permissionId = permission.getId();

			Integer dataPoolId = getDataPoolIdByPermissionId(permissionId,
					bizId);

			if (dataPoolId != null) {
				String userIds = getUserIdsFromDataPool(dataPoolId, bizId,
						userId);

				if (!"()".equals(userIds)) {
					sb.append("SELECT COUNT(*) ");

					sb.append(" FROM ");

					// 要查哪些表
					sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
					sb.append(bizId);
					sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
					sb.append(" ");
					sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());

					sb.append(",");

					sb.append(TableNameEnume.PRESETTABLENAME.getPrefix());
					sb.append(bizId);
					sb.append(TableNameEnume.PRESETTABLENAME.getSuffix());
					sb.append(" ");
					sb.append(TableNameEnume.PRESETTABLENAME.getAbbr());

					sb.append(",");

					sb.append(TableNameEnume.JIEGUOTABLENAME.getPrefix());
					sb.append(bizId);
					sb.append(TableNameEnume.JIEGUOTABLENAME.getSuffix());
					sb.append(" ");
					sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr());

					sb.append(" WHERE ");

					// 查询条件

					sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
							+ "MODIFYUSERID");
					sb.append(" IN ");
					sb.append(userIds);
					sb.append(" OR ");

					sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
							+ "MODIFYUSERID");
					sb.append(" IN ");
					sb.append(userIds);
					sb.append(" OR ");

					sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
							+ "MODIFYUSERID");
					sb.append(" IN ");
					sb.append(userIds);

					sb.append(" AND ");
					sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
							+ "MODIFYLAST");
					sb.append(" = ");
					sb.append(1);

					sb.append(" AND ");
					sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
							+ "MODIFYLAST");
					sb.append(" = ");
					sb.append(1);

					sb.append(" AND ");
					sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
							+ "MODIFYLAST");
					sb.append(" = ");
					sb.append(1);

					List<Map<String, String>> queryCondition = queryRequest
							.getQueryCondition();

					if (queryCondition != null) {
						if (queryCondition.size() > 0) {
							sb.append(" AND ");
						}

						Map<String, String> map1 = new HashMap<String, String>();
						Map<String, String> map2 = new HashMap<String, String>();

						for (Map<String, String> map : queryCondition) {
							String field = map.get("field");
							String value = map.get("value");
							String type = map.get("dataType");
							if (value != null) {
								// 时间字段使用范围查询
								if (type != null
										&& type.toLowerCase().contains("date")) {
									try {
										new SimpleDateFormat(
												INPUT_TIME_JTEMPLATE)
												.parse(value);
										if (map1.get(field) == null) {
											map1.put(field, value);
										} else {
											map2.put(field, value);
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
								} else {

									// 非时间字段使用模糊查询
									sb.append(field);
									sb.append(" LIKE '%");
									sb.append(value);
									sb.append("%' AND ");
								}
							}
						}

						if (map1.isEmpty()) {
							sb = new StringBuffer(sb.substring(0,
									sb.length() - 5));
						}

						// 时间字段使用范围查询
						for (Entry<String, String> entry : map1.entrySet()) {
							String key = entry.getKey();
							sb.append("(" + key);
							sb.append(" BETWEEN ");
							sb.append("TO_DATE('" + entry.getValue() + "','"
									+ INPUT_TIME_TEMPLATE + "')");
							sb.append(" AND ");
							sb.append("TO_DATE('" + map2.get(key) + "','"
									+ INPUT_TIME_TEMPLATE + "'))");
							sb.append(" OR ");
							sb.append("(" + key);
							sb.append(" BETWEEN ");
							sb.append("TO_DATE('" + map2.get(key) + "','"
									+ INPUT_TIME_TEMPLATE + "')");
							sb.append(" AND ");
							sb.append("TO_DATE('" + entry.getValue() + "','"
									+ INPUT_TIME_TEMPLATE + "'))");
						}
					}

					dbConn = this.getDbConnection();
					stmt = dbConn.prepareStatement(sb.toString());
					rs = stmt.executeQuery();

					// 获取查询结果
					if (rs.next()) {
						count = rs.getInt(1);
					}
				}
			}

		} catch (JsonSyntaxException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException(
					"getUserIdsFromDataPool JsonSyntaxException", 1);
		} catch (HiAppException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("getUserIdsFromDataPool HiAppException", 1);
		} catch (SQLException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("getUserIdsFromDataPool SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return count;
	}

	/**
	 * 支持根据不同业务和管理员自定义的查询条件查询所有客户
	 * 
	 * @param queryRequest
	 * @return
	 * @throws HiAppException
	 * @throws SQLException
	 */
	public List<List<Map<String, Object>>> queryAllCustomers(
			QueryRequest queryRequest, String userId) throws HiAppException {
		StringBuffer sb = new StringBuffer();

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<List<Map<String, Object>>> result = new ArrayList<List<Map<String, Object>>>();

		try {
			String bizId = queryRequest.getBizId();

			QueryTemplate queryTemplate = new QueryTemplate();
			queryTemplate.setBizId(bizId);
			queryTemplate.setConfigPage(ConfigPageEnume.ALLCUSTOMERS.getName());
			queryTemplate.setConfigType(ConfigTypeEnume.CUSTOMERLIST.getName());
			String template = getQueryTemplate(queryTemplate);

			RoleInGroupSet roleInGroupSet = userRepository
					.getRoleInGroupSetByUserId(userId);
			Permission permission = permissionRepository
					.getPermission(roleInGroupSet);
			int permissionId = permission.getId();

			Integer dataPoolId = getDataPoolIdByPermissionId(permissionId,
					bizId);

			if (dataPoolId != null) {
				String userIds = getUserIdsFromDataPool(dataPoolId, bizId,
						userId);

				if (!"()".equals(userIds)) {
					sb.append("SELECT * FROM (SELECT ");

					// 要查哪些字段
					@SuppressWarnings("unchecked")
					List<Map<String, String>> list = new Gson().fromJson(
							template, List.class);
					int flag1 = 0;
					int flag2 = 0;
					int flag3 = 0;
					for (Map<String, String> map : list) {
						String columnName = map.get("columnName");
						if (columnName != null) {
							if (columnName.equals(TableNameEnume.INPUTTABLENAME
									.getAbbr() + "." + "IID")) {
								flag1 = 1;
							} else if (columnName
									.equals(TableNameEnume.INPUTTABLENAME
											.getAbbr() + "." + "CID")) {
								flag2 = 1;
							} else if (columnName
									.equals(TableNameEnume.JIEGUOTABLENAME
											.getAbbr() + "." + "SOURCEID")) {
								flag3 = 1;
							}
						}
					}
					if (flag1 == 0) {
						HashMap<String, String> hashMap1 = new HashMap<String, String>();
						hashMap1.put("columnName",
								TableNameEnume.INPUTTABLENAME.getAbbr() + "."
										+ "IID");
						list.add(hashMap1);
					}
					if (flag2 == 0) {
						HashMap<String, String> hashMap2 = new HashMap<String, String>();
						hashMap2.put("columnName",
								TableNameEnume.INPUTTABLENAME.getAbbr() + "."
										+ "CID");
						list.add(hashMap2);
					}
					if (flag3 == 0) {
						HashMap<String, String> hashMap3 = new HashMap<String, String>();
						hashMap3.put("columnName",
								TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
										+ "SOURCEID");
						list.add(hashMap3);
					}
					for (Map<String, String> map : list) {
						String columnName = map.get("columnName");
						sb.append(columnName);
						sb.append(",");
					}
					sb.append("ROWNUM RN");
					sb.append(" FROM ");

					// 要查哪些表
					sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
					sb.append(bizId);
					sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
					sb.append(" ");
					sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());

					sb.append(",");

					sb.append(TableNameEnume.PRESETTABLENAME.getPrefix());
					sb.append(bizId);
					sb.append(TableNameEnume.PRESETTABLENAME.getSuffix());
					sb.append(" ");
					sb.append(TableNameEnume.PRESETTABLENAME.getAbbr());

					sb.append(",");

					sb.append(TableNameEnume.JIEGUOTABLENAME.getPrefix());
					sb.append(bizId);
					sb.append(TableNameEnume.JIEGUOTABLENAME.getSuffix());
					sb.append(" ");
					sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr());

					sb.append(" WHERE ");

					// 查询条件

					sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
							+ "MODIFYUSERID");
					sb.append(" IN ");
					sb.append(userIds);
					sb.append(" OR ");

					sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
							+ "MODIFYUSERID");
					sb.append(" IN ");
					sb.append(userIds);
					sb.append(" OR ");

					sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
							+ "MODIFYUSERID");
					sb.append(" IN ");
					sb.append(userIds);

					sb.append(" AND ");
					sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
							+ "MODIFYLAST");
					sb.append(" = ");
					sb.append(1);

					sb.append(" AND ");
					sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
							+ "MODIFYLAST");
					sb.append(" = ");
					sb.append(1);

					sb.append(" AND ");
					sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
							+ "MODIFYLAST");
					sb.append(" = ");
					sb.append(1);

					sb.append(" AND ");
					sb.append("ROWNUM");
					sb.append(" <= ");

					sb.append(queryRequest.getEnd());

					List<Map<String, String>> queryCondition = queryRequest
							.getQueryCondition();

					if (queryCondition != null) {
						if (queryCondition.size() > 0) {
							sb.append(" AND ");
						}

						Map<String, String> map1 = new HashMap<String, String>();
						Map<String, String> map2 = new HashMap<String, String>();

						for (Map<String, String> map : queryCondition) {
							String field = map.get("field");
							String value = map.get("value");
							String type = map.get("dataType");

							if (value != null) {
								// 时间字段使用范围查询
								if (type != null
										&& type.toLowerCase().contains("date")) {
									try {
										new SimpleDateFormat(
												INPUT_TIME_JTEMPLATE)
												.parse(value);
										if (map1.get(field) == null) {
											map1.put(field, value);
										} else {
											map2.put(field, value);
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
								} else {

									// 非时间字段使用模糊查询
									sb.append(field);
									sb.append(" LIKE '%");
									sb.append(value);
									sb.append("%' AND ");
								}
							}
						}

						if (map1.isEmpty()) {
							sb = new StringBuffer(sb.substring(0,
									sb.length() - 5));
						}

						// 时间字段使用范围查询
						for (Entry<String, String> entry : map1.entrySet()) {
							String key = entry.getKey();
							sb.append("(" + key);
							sb.append(" BETWEEN ");
							sb.append("TO_DATE('" + entry.getValue() + "','"
									+ INPUT_TIME_TEMPLATE + "')");
							sb.append(" AND ");
							sb.append("TO_DATE('" + map2.get(key) + "','"
									+ INPUT_TIME_TEMPLATE + "'))");
							sb.append(" OR ");
							sb.append("(" + key);
							sb.append(" BETWEEN ");
							sb.append("TO_DATE('" + map2.get(key) + "','"
									+ INPUT_TIME_TEMPLATE + "')");
							sb.append(" AND ");
							sb.append("TO_DATE('" + entry.getValue() + "','"
									+ INPUT_TIME_TEMPLATE + "'))");
						}
					}

					sb.append(" ORDER BY ");
					sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
							+ "MODIFYTIME");
					sb.append(" DESC,");
					sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
							+ "MODIFYTIME");
					sb.append(" DESC,");
					sb.append(TableNameEnume.JIEGUOTABLENAME.getAbbr() + "."
							+ "MODIFYTIME");
					sb.append(" DESC)");

					sb.append(" WHERE ");
					sb.append("RN");
					sb.append(" >= ");
					sb.append(queryRequest.getStart());
					dbConn = this.getDbConnection();
					stmt = dbConn.prepareStatement(sb.toString());
					rs = stmt.executeQuery();

					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
							OUTPUT_TIME_TEMPLATE);

					// 获取查询结果
					while (rs.next()) {
						List<Map<String, Object>> record = new ArrayList<Map<String, Object>>();
						ListIterator<Map<String, String>> listIterator = list
								.listIterator();
						AtomicInteger atomicInteger = new AtomicInteger(1);
						while (listIterator.hasNext()) {
							Map<String, Object> map = new HashMap<String, Object>();
							Map<String, String> next = listIterator.next();
							String rowNumber = next.get("rowNumber");
							String colNumber = next.get("colNumber");
							String fontColor = next.get("fontColor");
							String columnName = next.get("columnName");
							String type = next.get("dataType");
							if (type != null
									&& type.toLowerCase().contains("date")) {
								map.put("value", simpleDateFormat.format(rs
										.getDate(atomicInteger
												.getAndIncrement())));
							} else {
								map.put("value", rs.getObject(atomicInteger
										.getAndIncrement()));
							}
							map.put("rowNumber", rowNumber);
							map.put("colNumber", colNumber);
							map.put("fontColor", fontColor);
							map.put("columnName", columnName);
							record.add(map);
						}
						result.add(record);
					}
				}
			}

		} catch (JsonSyntaxException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException(
					"getUserIdsFromDataPool JsonSyntaxException", 1);
		} catch (HiAppException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("getUserIdsFromDataPool HiAppException", 1);
		} catch (SQLException e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("getUserIdsFromDataPool SQLException", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return result;
	}

}
