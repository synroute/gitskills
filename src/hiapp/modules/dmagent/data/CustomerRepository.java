package hiapp.modules.dmagent.data;

import hiapp.modules.dmagent.DeployPage;
import hiapp.modules.dmagent.QueryCondition;
import hiapp.modules.dmagent.QueryRequest;
import hiapp.modules.dmagent.QueryTemplate;
import hiapp.modules.dmagent.TableNameEnume;
import hiapp.utils.DbUtil;
import hiapp.utils.base.HiAppException;
import hiapp.utils.database.BaseRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository extends BaseRepository {

	/**
	 * 获取配置查询模板时需要使用的候选列
	 * 
	 * @param bizId
	 * @param deployPage
	 * @return
	 */
	public Map<String, Object> getCandidadeColumn(String bizId,
			String deployPage) {
		String inputTableName = null;
		String presetTableName = null;
		String jieGuoTableName = TableNameEnume.JIEGUOTABLENAME.getPrefix()
				+ bizId + TableNameEnume.JIEGUOTABLENAME.getSuffix();
		
		String sql = "SELECT COLUMNNAME,COLUMNNAMECH,COLUMNDESCRIPTION,DATATYPE,LENGTH FROM HASYS_WORKSHEETCOLUMN A,HASYS_WORKSHEET B WHERE A.WORKSHEETID = B.ID AND B.NAMEC = ？";
		
		// 我的客户
		if (deployPage.endsWith(DeployPage.MYCUSTOMERS.getName())) {
			inputTableName = TableNameEnume.INPUTTABLENAME.getPrefix() + bizId
					+ TableNameEnume.INPUTTABLENAME.getSuffix();
			sql = "";
		}
		// 联系计划
		else if (deployPage.endsWith(DeployPage.CONTACTPLAN.getName())) {
			presetTableName = TableNameEnume.PRESETTABLENAME.getPrefix()
					+ bizId + TableNameEnume.INPUTTABLENAME.getSuffix();
			sql = "";
		} 
		// 全部客户
		else if (deployPage.endsWith(DeployPage.ALLCUSTOMERS.getName())) {
			inputTableName = TableNameEnume.INPUTTABLENAME.getPrefix() + bizId
					+ TableNameEnume.INPUTTABLENAME.getSuffix();
			presetTableName = TableNameEnume.PRESETTABLENAME.getPrefix()
					+ bizId + TableNameEnume.INPUTTABLENAME.getSuffix();
			sql = "";
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义配置的查询条件查询所有客户数据.
	 * 
	 * @param queryRequest
	 * @return
	 */
	public Map<String, Object> queryMyCustomers(QueryRequest queryRequest) {
		System.out.println(queryRequest);
		String bizId = queryRequest.getBizId();
		String queryType = queryRequest.getQueryType();
		List<QueryCondition> queryConditions = queryRequest.getQueryCondition();
		StringBuffer sb = new StringBuffer();
		for (QueryCondition queryCondition : queryConditions) {
			sb.append(queryCondition.getField());
			sb.append(" like '");
			sb.append(queryCondition.getValue());
			sb.append("' and ");
		}
		String substring = sb.substring(0, sb.length() - 5);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义的查询条件查询预约或待跟进的客户列表
	 * 
	 * @param queryRequest
	 * @return
	 */
	public Map<String, Object> queryMyPresetCustomers(QueryRequest queryRequest) {
		System.out.println(queryRequest);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}

	/**
	 * 查询不同业务和管理员自定义的查询条件查询客户列表
	 * 
	 * @param queryRequest
	 * @return
	 */
	public Map<String, Object> queryAllCustomers(QueryRequest queryRequest) {
		System.out.println(queryRequest);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}

	/**
	 * 保存配置好的查询模板
	 * 
	 * @param queryTemplate
	 * @return
	 * @throws HiAppException
	 */
	public Map<String, Object> saveQueryTemplate(QueryTemplate queryTemplate)
			throws HiAppException {
		int bizId = queryTemplate.getBizId();
		String templateType = queryTemplate.getTemplateType();
		String templateData = queryTemplate.getTemplateData();

		Connection dbConn = null;
		PreparedStatement stmt = null;
		ByteArrayInputStream inputStream = null;
		try {
			dbConn = this.getDbConnection();
			String sql = "INSERT INTO HASYS_DM_CUPAGETEMPLATE(BUSINESSID,DEPLOYTYPE,DEPLOYTEMPLATE) VALUES (?,?,?)";
			stmt = dbConn.prepareStatement(sql);
			stmt.setInt(1, bizId);
			stmt.setString(2, templateType);
			inputStream = new ByteArrayInputStream(templateData.getBytes());
			stmt.setBlob(3, inputStream);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new HiAppException("SQLException", 1);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			DbUtil.DbCloseExecute(stmt);
			DbUtil.DbCloseConnection(dbConn);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}

	/**
	 * 获取查询模板
	 * 
	 * @return
	 */
	public Map<String, Object> getQueryTemplate() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}
}
