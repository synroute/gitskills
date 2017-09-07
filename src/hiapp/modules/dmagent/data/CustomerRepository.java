package hiapp.modules.dmagent.data;

import hiapp.modules.dmagent.QueryRequest;
import hiapp.utils.DbUtil;
import hiapp.utils.base.HiAppException;
import hiapp.utils.database.BaseRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository extends BaseRepository {
	/**
	 * 根据业务id查询worksheetId
	 * @param bizId
	 * @return
	 */
	public List<Integer> QueryWorkSheetIdByBizId(int bizId) throws HiAppException{
		List<Integer> result = null;
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			result = new ArrayList<Integer>();
			dbConn = this.getDbConnection();
			String sql = "SELECT WORKSHEETID FROM HASYS_DM_BIZWORKSHEET WHERE BIZID = ?";
			stmt = dbConn.prepareStatement(sql);
			stmt.setInt(1, bizId);
			rs = stmt.executeQuery();
			while(rs.next()){
				result.add(rs.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new HiAppException("SQLException",1);
		}finally{
			DbUtil.DbCloseQuery(rs, stmt);
			DbUtil.DbCloseConnection(dbConn);
		}
        return result;
    }
	
	/**
	 * 支持坐席根据不同业务和管理员自定义配置的查询条件查询所有客户数据.
	 * @param queryRequest
	 * @return
	 */
	public Map<String, Object> queryMyCustomers(QueryRequest queryRequest){
        System.out.println(queryRequest);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("result", 0);
        return result;
    }
	/**
	 * 支持坐席根据不同业务和管理员自定义的查询条件查询预约或待跟进的客户列表
	 * @param queryRequest
	 * @return
	 */
	public Map<String, Object> queryMyPresetCustomers(QueryRequest queryRequest){
		System.out.println(queryRequest);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}
	/**
	 * 查询不同业务和管理员自定义的查询条件查询客户列表
	 * @param queryRequest
	 * @return
	 */
	public Map<String, Object> queryAllCustomersHasPermission(QueryRequest queryRequest){
		System.out.println(queryRequest);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}
	/**
	 * 保存查询项
	 * @param queryItem
	 * @return
	 */
	public Map<String, Object> saveQueryItem(String queryItem){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}
	/**
	 * 获取查询项
	 * @return
	 */
	public Map<String, Object> getQueryItem(){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}
}
