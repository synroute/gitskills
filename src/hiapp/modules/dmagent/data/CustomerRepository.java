package hiapp.modules.dmagent.data;

import hiapp.modules.dmagent.ConfigPageEnume;
import hiapp.modules.dmagent.ConfigTypeEnume;
import hiapp.modules.dmagent.QueryRequest;
import hiapp.modules.dmagent.QueryTemplate;
import hiapp.modules.dmagent.TableNameEnume;
import hiapp.modules.dmsetting.data.DmBizDataPoolRepository;
import hiapp.modules.dmsetting.data.DmWorkSheetRepository;
import hiapp.system.worksheet.bean.WorkSheetColumn;
import hiapp.system.worksheet.data.WorkSheetRepository;
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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

@Repository
public class CustomerRepository extends BaseRepository {
	@Autowired
	private WorkSheetRepository workSheetRepository;
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
	@Autowired
	private DmBizDataPoolRepository dmBizDataPoolRepository;

	private final String INPUT_TIME_TEMPLATE = "MM/dd/YYYY HH24:mi:ss";
	private final String INPUT_TIME_JTEMPLATE = "MM/dd/YYYY HH:mm:ss";
	private final String OUTPUT_TIME_TEMPLATE = "yyyy/MM/dd HH:mm:ss";

	// 根据业务ID获取结果表中作为待选列的非固定列
	public List<Map<String, Object>> getResultTableColumn(String bizId)
			throws HiAppException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String resultTableName = TableNameEnume.RESULTTABLENAME.getPrefix()
				+ bizId + TableNameEnume.RESULTTABLENAME.getSuffix();
		String worksheetId = workSheetRepository
				.getWorksheetIdByName(resultTableName);
		List<WorkSheetColumn> listWorkSheetColumn = new ArrayList<WorkSheetColumn>();
		if (dmWorkSheetRepository.getWorkSheetColumnByWorksheetId(
				listWorkSheetColumn, worksheetId)) {
			for (WorkSheetColumn workSheetColumn : listWorkSheetColumn) {
				if ("0".equals(workSheetColumn.getFixedColumn())) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("COLUMNNAME",
							TableNameEnume.RESULTTABLENAME.getAbbr() + "."
									+ workSheetColumn.getColumnName());
					map.put("COLUMNNAMECH", workSheetColumn.getColumnNameCh());
					map.put("COLUMNDESCRIPTION", workSheetColumn.getColumnDes());
					map.put("dataType", workSheetColumn.getDataType());
					map.put("LENGTH", workSheetColumn.getColumnLength());
					list.add(map);
				}
			}
		}
		return list;
	}

	// 根据业务ID获取导入表中作为待选列的非固定列
	public List<Map<String, Object>> getInputTableColumn(String bizId)
			throws HiAppException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String inputTableName = TableNameEnume.INPUTTABLENAME.getPrefix()
				+ bizId + TableNameEnume.INPUTTABLENAME.getSuffix();

		String worksheetId = workSheetRepository
				.getWorksheetIdByName(inputTableName);
		List<WorkSheetColumn> listWorkSheetColumn = new ArrayList<WorkSheetColumn>();
		if (dmWorkSheetRepository.getWorkSheetColumnByWorksheetId(
				listWorkSheetColumn, worksheetId)) {
			for (WorkSheetColumn workSheetColumn : listWorkSheetColumn) {
				if ("0".equals(workSheetColumn.getFixedColumn())) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("COLUMNNAME",
							TableNameEnume.INPUTTABLENAME.getAbbr() + "."
									+ workSheetColumn.getColumnName());
					map.put("COLUMNNAMECH", workSheetColumn.getColumnNameCh());
					map.put("COLUMNDESCRIPTION", workSheetColumn.getColumnDes());
					map.put("dataType", workSheetColumn.getDataType());
					map.put("LENGTH", workSheetColumn.getColumnLength());
					list.add(map);
				}
			}
		}

		return list;
	}

	// 根据业务ID获取预约表中作为待选列的非固定列
	public List<Map<String, Object>> getPresetTableColumn(String bizId)
			throws HiAppException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String presetTableName = TableNameEnume.PRESETTABLENAME.getPrefix()
				+ bizId + TableNameEnume.INPUTTABLENAME.getSuffix();

		String worksheetId = workSheetRepository
				.getWorksheetIdByName(presetTableName);
		List<WorkSheetColumn> listWorkSheetColumn = new ArrayList<WorkSheetColumn>();
		if (dmWorkSheetRepository.getWorkSheetColumnByWorksheetId(
				listWorkSheetColumn, worksheetId)) {
			for (WorkSheetColumn workSheetColumn : listWorkSheetColumn) {
				if ("0".equals(workSheetColumn.getFixedColumn())) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("COLUMNNAME",
							TableNameEnume.PRESETTABLENAME.getAbbr() + "."
									+ workSheetColumn.getColumnName());
					map.put("COLUMNNAMECH", workSheetColumn.getColumnNameCh());
					map.put("COLUMNDESCRIPTION", workSheetColumn.getColumnDes());
					map.put("dataType", workSheetColumn.getDataType());
					map.put("LENGTH", workSheetColumn.getColumnLength());
					list.add(map);
				}
			}
		}

		return list;
	}

	/**
	 * 获取配置查询模板时需要使用的待选列
	 * 
	 * @param bizId
	 * @param deployPage
	 * @return
	 * @throws HiAppException
	 */
	public List<Map<String, Object>> getSourceColumn(String bizId,
			String configPage) throws HiAppException {

		// 获取用户自定义表中用户自定义的候选列
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			list.addAll(getResultTableColumn(bizId));
			// 获取用户自定义表中固定的候选列
			list.addAll(TableNameEnume.RESULTTABLENAME.getCandidadeColumn());

			// 我的客户
			if (ConfigPageEnume.MYCUSTOMERS.getName().equals(configPage)) {
				// 获取导入表中用户自定义的候选列
				list.addAll(getInputTableColumn(bizId));
				// 获取导入表中固定的候选列
				list.addAll(TableNameEnume.INPUTTABLENAME.getCandidadeColumn());
			}
			// 联系计划
			else if (ConfigPageEnume.CONTACTPLAN.getName().equals(configPage)) {
				// 获取预约表中用户自定义的候选列
				list.addAll(getPresetTableColumn(bizId));
				// 获取预约表中固定的候选列
				list.addAll(TableNameEnume.PRESETTABLENAME.getCandidadeColumn());
			}
			// 全部客户
			else if (ConfigPageEnume.ALLCUSTOMERS.getName().equals(configPage)) {
				// 获取导入表中用户自定义的候选列
				list.addAll(getInputTableColumn(bizId));
				// 获取导入表中固定的候选列
				list.addAll(TableNameEnume.INPUTTABLENAME.getCandidadeColumn());
				// 获取预约表中用户自定义的候选列
				list.addAll(getPresetTableColumn(bizId));
				// 获取预约表中固定的候选列
				list.addAll(TableNameEnume.PRESETTABLENAME.getCandidadeColumn());
			}
		} catch (HiAppException e) {
			throw e;
		}

		return list;
	}

	/**
	 * 保存配置好的筛选模板（包括高级筛选和普通筛选）
	 * 
	 * @param queryTemplate
	 * @return
	 */
	public boolean saveFilterTemplate(QueryTemplate queryTemplate) {
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
	 * 获取筛选模板(包括普通筛选和高级筛选)和列表显示模板（显示客户的哪些字段包含样式）
	 * 
	 * @return
	 * @throws HiAppException
	 */
	public String getTemplate(QueryTemplate queryTemplate)
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
	 * 根据条件查询前台页面上“我的客户”模块下符合条件的客户的数量
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

			sb.append(" INNER JOIN ");

			sb.append(TableNameEnume.RESULTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.RESULTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr());

			sb.append(" ON ");

			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "IID");

			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "CID");

			

			sb.append(" WHERE ");
			// 查询条件
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);
			
			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
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
					if(!field.equals(""))
					{
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

			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★queryMyCustomersCount★★★★★★★★★★★★★★★★★★★★");
			System.out.println(sb.toString());
			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");

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
	 * “我的客户”模块点击下一条
	 * 
	 * @param queryRequest
	 * @param userId
	 * @return
	 * @throws HiAppException
	 */
	@SuppressWarnings("unchecked")
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
			String template = getTemplate(queryTemplate);

			sb.append("SELECT ");

			// 要查哪些字段
			List<Map<String, String>> list = new Gson().fromJson(template,
					List.class);
			// IID,CID,SourceID必须查
			int flag1 = 0;
			int flag2 = 0;
			int flag3 = 0;
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				if (columnName != null) {
					if (columnName.equals(TableNameEnume.INPUTTABLENAME
							.getAbbr() + "." + "IID")) {
						flag1 = 1;
					} else if (columnName.equals(TableNameEnume.INPUTTABLENAME
							.getAbbr() + "." + "CID")) {
						flag2 = 1;
					} else if (columnName.equals(TableNameEnume.RESULTTABLENAME
							.getAbbr() + "." + "SOURCEID")) {
						flag3 = 1;
					}
				}
			}
			if (flag1 == 0) {
				HashMap<String, String> hashMap1 = new HashMap<String, String>();
				hashMap1.put("columnName",
						TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
				list.add(hashMap1);
			}
			if (flag2 == 0) {
				HashMap<String, String> hashMap2 = new HashMap<String, String>();
				hashMap2.put("columnName",
						TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
				list.add(hashMap2);
			}
			if (flag3 == 0) {
				HashMap<String, String> hashMap3 = new HashMap<String, String>();
				hashMap3.put("columnName",
						TableNameEnume.RESULTTABLENAME.getAbbr() + "."
								+ "SOURCEID");
				list.add(hashMap3);
			}
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				sb.append(columnName);
				sb.append(",");
			}
			sb = new StringBuffer(sb.substring(0, sb.length()-1));
			sb.append(" FROM ");

			// 要查哪些表
			sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());

			sb.append(" LEFT JOIN (select * from ");

			sb.append(TableNameEnume.RESULTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.RESULTTABLENAME.getSuffix());
			sb.append(" where HAU_DM_B"+bizId+"C_RESULT.SOURCEID='"+queryRequest.getSourceId()+"') ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr());

			sb.append(" ON ");
			sb.append("(");

			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "IID");

			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "CID");

			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");

			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");

			sb.append(")");

			sb.append(" WHERE ");

			// 查询条件

			
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(1);
			
			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "IID");
			sb.append(" = ");
			sb.append("'"+queryRequest.getIID()+"'");

			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "CID");
			sb.append(" = ");
			sb.append("'"+queryRequest.getCID()+"'");



			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★queryMyNextCustomer★★★★★★★★★★★★★★★★★★★★");
			System.out.println(sb.toString());
			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");

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
					String columnName = next.get("columnName");
					String bgColor=next.get("bgColor");
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
					map.put("bgColor", bgColor);
					record.add(map);
				}
				result.add(record);
			}
		} catch (Exception e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyCustomers Exception", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return result;
	}

	/**
	 * 根据条件查询前台页面上“我的客户”模块下符合条件的客户
	 * 
	 * @param queryRequest
	 * @return
	 * @throws HiAppException
	 */
	@SuppressWarnings("unchecked")
	public List<List<Map<String, Object>>> queryMyCustomers(
			QueryRequest queryRequest, String userId) throws HiAppException {

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<List<Map<String, Object>>> result = new ArrayList<List<Map<String, Object>>>();

		StringBuffer sb = new StringBuffer();
		try {
			String bizId = queryRequest.getBizId();
			dbConn = this.getDbConnection();
			QueryTemplate queryTemplate = new QueryTemplate();
			queryTemplate.setBizId(bizId);
			queryTemplate.setConfigPage(ConfigPageEnume.MYCUSTOMERS.getName());
			queryTemplate.setConfigType(ConfigTypeEnume.CUSTOMERLIST.getName());
			String template = getTemplate(queryTemplate);

			sb.append("SELECT * FROM (SELECT ");

			// 要查哪些字段
			List<Map<String, String>> list = new Gson().fromJson(template,
					List.class);
			// IID,CID,SourceID必须查
			int flag1 = 0;
			int flag2 = 0;
			int flag3 = 0;
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				if (columnName != null) {
					if (columnName.equals(TableNameEnume.INPUTTABLENAME
							.getAbbr() + "." + "IID")) {
						flag1 = 1;
					} else if (columnName.equals(TableNameEnume.INPUTTABLENAME
							.getAbbr() + "." + "CID")) {
						flag2 = 1;
					} else if (columnName.equals(TableNameEnume.RESULTTABLENAME
							.getAbbr() + "." + "SOURCEID")) {
						flag3 = 1;
					}
				}
			}
			if (flag1 == 0) {
				HashMap<String, String> hashMap1 = new HashMap<String, String>();
				hashMap1.put("columnName",
						TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
				list.add(hashMap1);
			}
			if (flag2 == 0) {
				HashMap<String, String> hashMap2 = new HashMap<String, String>();
				hashMap2.put("columnName",
						TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
				list.add(hashMap2);
			}
			if (flag3 == 0) {
				HashMap<String, String> hashMap3 = new HashMap<String, String>();
				hashMap3.put("columnName",
						TableNameEnume.RESULTTABLENAME.getAbbr() + "."
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

			String sql="select OutboundID from HASYS_DM_Business a left join Hasys_DM_BIZTypeMode b on a.outboundmddeid=b.OutboundMode where businessid="+bizId+"";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			int outid=0;
			while (rs.next()) {
				outid=rs.getInt(1);
			}
			if(outid==1)
			{
				// 要查哪些表
				sb.append("HAU_DM_B"+bizId+"C_POOL a left join HAU_DM_B"+bizId+"C_IMPORT DR on a.iid=DR.iid and a.cid=DR.cid left join (select * from HASYS_DM_DATAPOOL where businessid="+bizId+") a1 on a.DataPoolIDCur=a1.id left join HAU_DM_B"+bizId+"C_Result JG on a.iid=JG.iid and a.cid=JG.cid ");
				
	
				sb.append(" WHERE ");
	
				// 查询条件
				sb.append("ROWNUM");
				sb.append(" <= ");
				sb.append(queryRequest.getEnd());
	
				sb.append(" AND ");
				sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
						+ "MODIFYLAST");
				sb.append(" = ");
				sb.append("1");
	
				/*sb.append(" AND ");
				sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
						+ "MODIFYUSERID");
				sb.append(" = ");
				sb.append(userId);*/
				
				sb.append("and a1.datapoolname='"+userId+"'");
			}else
			{
				// 要查哪些表
				sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
				sb.append(bizId);
				sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
				sb.append(" ");
				sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());
	
				sb.append(" INNER JOIN ");
	
				sb.append(TableNameEnume.RESULTTABLENAME.getPrefix());
				sb.append(bizId);
				sb.append(TableNameEnume.RESULTTABLENAME.getSuffix());
				sb.append(" ");
				sb.append(TableNameEnume.RESULTTABLENAME.getAbbr());
	
				sb.append(" ON ");
	
				sb.append("(");
	
				sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
				sb.append(" = ");
				sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "IID");
	
				sb.append(" AND ");
				sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
				sb.append(" = ");
				sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "CID");
	
				sb.append(")");
	
				sb.append(" WHERE ");
	
				// 查询条件
				sb.append("ROWNUM");
				sb.append(" <= ");
				sb.append(queryRequest.getEnd());
	
				sb.append(" AND ");
				sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
						+ "MODIFYLAST");
				sb.append(" = ");
				sb.append("1");
	
				sb.append(" AND ");
				sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
						+ "MODIFYUSERID");
				sb.append(" = ");
				sb.append(userId);
			}
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
					if (!field.equals("")) {
						
					
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
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
					+ "MODIFYTIME");
			sb.append(" DESC)");

			sb.append(" WHERE ");
			sb.append("RN");
			sb.append(" >= ");
			sb.append(queryRequest.getStart());

			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★★★queryMyCustomers★★★★★★★★★★★★★★★★★★★★★★");
			System.out.println(sb.toString());
			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");

			
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
					String bgColor = next.get("bgColor");
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
					map.put("bgColor", bgColor);
					record.add(map);
				}
				result.add(record);
			}
		} catch (Exception e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyCustomers Exception", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return result;
	}

	/**
	 * 根据条件查询前台页面上“联系计划”模块下符合条件的客户的数量
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

			sb.append(" INNER JOIN ");

			sb.append(TableNameEnume.RESULTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.RESULTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr());

			sb.append(" ON ");
			sb.append("(");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "IID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "IID");

			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "CID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "CID");

			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");

			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(")");

			sb.append(" WHERE ");

			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);

			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(1);

			// 查询条件
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

			System.out
					.println("★★★★★★★★★★★★★★★★★★★queryMyPresetCustomersCount★★★★★★★★★★★★★★★★★★");
			System.out.println(sb.toString());
			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");

			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sb.toString());
			rs = stmt.executeQuery();

			// 获取查询结果
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyPresetCustomers Exception", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}
		return count;
	}

	/**
	 * 根据条件查询前台页面上“联系计划”模块下符合条件的客户
	 * 
	 * @param queryRequest
	 * @return
	 * @throws HiAppException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
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
			String template = getTemplate(queryTemplate);

			sb.append("SELECT * FROM (SELECT ");

			// 要查哪些字段
			List<Map<String, String>> list = new Gson().fromJson(template,
					List.class);
			// IID,CID,SourceID必须查
			int flag1 = 0;
			int flag2 = 0;
			int flag3 = 0;
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				if (columnName != null) {
					if (columnName.equals(TableNameEnume.PRESETTABLENAME
							.getAbbr() + "." + "IID")) {
						flag1 = 1;
					} else if (columnName.equals(TableNameEnume.PRESETTABLENAME
							.getAbbr() + "." + "CID")) {
						flag2 = 1;
					} else if (columnName.equals(TableNameEnume.RESULTTABLENAME
							.getAbbr() + "." + "SOURCEID")) {
						flag3 = 1;
					}
				}
			}
			if (flag1 == 0) {
				HashMap<String, String> hashMap1 = new HashMap<String, String>();
				hashMap1.put("columnName",
						TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "IID");
				list.add(hashMap1);
			}
			if (flag2 == 0) {
				HashMap<String, String> hashMap2 = new HashMap<String, String>();
				hashMap2.put("columnName",
						TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "CID");
				list.add(hashMap2);
			}
			if (flag3 == 0) {
				HashMap<String, String> hashMap3 = new HashMap<String, String>();
				hashMap3.put("columnName",
						TableNameEnume.RESULTTABLENAME.getAbbr() + "."
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

			sb.append(" INNER JOIN ");

			sb.append(TableNameEnume.RESULTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.RESULTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr());

			sb.append(" ON ");
			sb.append("(");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "IID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "IID");

			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "CID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "CID");

			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");

			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(")");

			sb.append(" WHERE ");

			// 查询条件
			sb.append("ROWNUM");
			sb.append(" <= ");
			sb.append(queryRequest.getEnd());
			
			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYUSERID");
			sb.append(" = ");
			sb.append(userId);

			sb.append(" AND ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
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

			sb.append(" ORDER BY ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYTIME");
			sb.append(" DESC,");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
					+ "MODIFYTIME");
			sb.append(" DESC)");

			sb.append(" WHERE ");
			sb.append("RN");
			sb.append(" >= ");
			sb.append(queryRequest.getStart());

			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★queryMyPresetCustomers★★★★★★★★★★★★★★★★★★★★");
			System.out.println(sb.toString());
			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");

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
					String bgColor = next.get("bgColor");
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
					map.put("bgColor", bgColor);
					record.add(map);
				}
				result.add(record);
			}
		} catch (Exception e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryMyPresetCustomers Exception", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return result;
	}

	/**
	 * 根据条件查询前台页面上“所有客户”模块下符合条件的客户的数量
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
			sb.append("SELECT COUNT(*) ");
			sb.append(" FROM ");

			// 要查哪些表
			sb.append(TableNameEnume.RESULTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.RESULTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr());
			
			sb.append(" LEFT JOIN HASYS_DM_SID SID ON ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "SOURCEID");
			sb.append(" = ");
			sb.append(" SID.SHAREID ");
	
			sb.append(" LEFT JOIN ");
	
			sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());
	
			sb.append(" ON (");
	
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "IID");
			sb.append(" = ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
	
			sb.append(" AND ");
	
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "CID");
			sb.append(" = ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
			
			sb.append(" AND ");
	
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "MODIFYLAST");
			sb.append(" = ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "MODIFYLAST");
	
			sb.append(")");
	
			sb.append(" LEFT JOIN ");
	
			sb.append(TableNameEnume.PRESETTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.PRESETTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr());
	
			sb.append(" ON (");
	
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
			sb.append(" = ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "IID");
			sb.append(" AND ");
	
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
			sb.append(" = ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "CID");
			
			sb.append(" AND ");
	
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "MODIFYLAST");
			sb.append(" = ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "MODIFYLAST");
	
			sb.append(")");
	
			sb.append(" WHERE ");
			
			sb.append("  SID.CREATEUSERID =  ");
			sb.append(userId);
			sb.append(" AND ");
			
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append("1");

			// 查询条件
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

			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★queryAllCustomersCount★★★★★★★★★★★★★★★★★★★★");
			System.out.println(sb.toString());
			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");

			dbConn = this.getDbConnection();
			stmt = dbConn.prepareStatement(sb.toString());
			rs = stmt.executeQuery();

			// 获取查询结果
			if (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryAllCustomersCount Exception", 1);
		} finally {
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		return count;
	}

	/**
	 * 根据条件查询前台页面上“所有客户”模块下符合条件的客户
	 * 
	 * @param queryRequest
	 * @return
	 * @throws HiAppException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
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
			String template = getTemplate(queryTemplate);
			sb.append("SELECT * FROM (SELECT ");
	
			// 要查哪些字段
			List<Map<String, String>> list = new Gson().fromJson(template,
					List.class);
			// IID,CID,SourceID必须查
			int flag1 = 0;
			int flag2 = 0;
			int flag3 = 0;
			for (Map<String, String> map : list) {
				String columnName = map.get("columnName");
				if (columnName != null) {
					if (columnName.equals(TableNameEnume.INPUTTABLENAME
							.getAbbr() + "." + "IID")) {
						flag1 = 1;
					} else if (columnName.equals(TableNameEnume.INPUTTABLENAME
							.getAbbr() + "." + "CID")) {
						flag2 = 1;
					} else if (columnName.equals(TableNameEnume.RESULTTABLENAME
							.getAbbr() + "." + "SOURCEID")) {
						flag3 = 1;
					}
				}
			}
			if (flag1 == 0) {
				HashMap<String, String> hashMap1 = new HashMap<String, String>();
				hashMap1.put("columnName",
						TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
				list.add(hashMap1);
			}
			if (flag2 == 0) {
				HashMap<String, String> hashMap2 = new HashMap<String, String>();
				hashMap2.put("columnName",
						TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
				list.add(hashMap2);
			}
			if (flag3 == 0) {
				HashMap<String, String> hashMap3 = new HashMap<String, String>();
				hashMap3.put("columnName",
						TableNameEnume.RESULTTABLENAME.getAbbr() + "."
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
			sb.append(TableNameEnume.RESULTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.RESULTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr());
			
			sb.append(" LEFT JOIN HASYS_DM_SID SID ON ");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "SOURCEID");
			sb.append(" = ");
			sb.append(" SID.SHAREID ");
	
			sb.append(" LEFT JOIN ");
	
			sb.append(TableNameEnume.INPUTTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.INPUTTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr());
	
			sb.append(" ON (");
	
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "IID");
			sb.append(" = ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
	
			sb.append(" AND ");
	
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "CID");
			sb.append(" = ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
			
			sb.append(" AND ");
	
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "." + "MODIFYLAST");
			sb.append(" = ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "MODIFYLAST");
	
			sb.append(")");
	
			sb.append(" LEFT JOIN ");
	
			sb.append(TableNameEnume.PRESETTABLENAME.getPrefix());
			sb.append(bizId);
			sb.append(TableNameEnume.PRESETTABLENAME.getSuffix());
			sb.append(" ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr());
	
			sb.append(" ON (");
	
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "IID");
			sb.append(" = ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "IID");
			sb.append(" AND ");
	
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "CID");
			sb.append(" = ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "CID");
			
			sb.append(" AND ");
	
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "." + "MODIFYLAST");
			sb.append(" = ");
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "." + "MODIFYLAST");
	
			sb.append(")");
	
	
			sb.append(" WHERE ");
			// 查询条件
	
			sb.append("ROWNUM");
			sb.append(" <= ");
			sb.append(queryRequest.getEnd());
			
			sb.append(" AND ");
			sb.append("  SID.CREATEUSERID =  ");
			sb.append(userId);
			
			sb.append(" AND ");
			sb.append(TableNameEnume.INPUTTABLENAME.getAbbr() + "."
					+ "MODIFYLAST");
			sb.append(" = ");
			sb.append("1");
	
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
			sb.append(TableNameEnume.PRESETTABLENAME.getAbbr() + "."
					+ "MODIFYTIME");
			sb.append(" DESC,");
			sb.append(TableNameEnume.RESULTTABLENAME.getAbbr() + "."
					+ "MODIFYTIME");
			sb.append(" DESC)");
	
			sb.append(" WHERE ");
			sb.append("RN");
			sb.append(" >= ");
			sb.append(queryRequest.getStart());
	
			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★queryAllCustomers★★★★★★★★★★★★★★★★★★★★");
			System.out.println(sb.toString());
			System.out
					.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
	
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
					String bgColor = next.get("bgColor");
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
					map.put("bgColor", bgColor);
					record.add(map);
				}
				result.add(record);
			}
	
		} catch (Exception e) {
			System.out.println(sb);
			e.printStackTrace();
			throw new HiAppException("queryAllCustomers Exception", 1);
		}
	
		return result;
	}

}
