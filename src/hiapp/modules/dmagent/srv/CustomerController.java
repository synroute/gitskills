package hiapp.modules.dmagent.srv;

import hiapp.modules.dmagent.QueryRequest;
import hiapp.modules.dmagent.QueryTemplate;
import hiapp.modules.dmagent.data.CustomerRepository;
import hiapp.system.buinfo.Permission;
import hiapp.system.buinfo.RoleInGroupSet;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.data.PermissionRepository;
import hiapp.system.buinfo.data.UserRepository;
import hiapp.utils.base.HiAppException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class CustomerController {
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PermissionRepository permissionRepository;

	/**
	 * 获取配置查询模板时需要使用的候选列
	 * @param bizId
	 * @param configPage
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping(value = "/srv/agent/getCandidadeColumn", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getCandidadeColumn(String bizId,
			String configPage) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> candidadeColumn = null;
		
		try {
			candidadeColumn = customerRepository
					.getCandidadeColumn(bizId, configPage);
		} catch (HiAppException e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
		}
		
		result.put("data", candidadeColumn);
		result.put("result", 0);
		
		return result;
	}

	/**
	 * 保存配置好的查询模板
	 * 
	 * @param queryItem
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/saveQueryTemplate", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> saveQueryTemplate(QueryTemplate queryTemplate) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(customerRepository.saveQueryTemplate(queryTemplate)){
			result.put("result", 0);
		}else{
			result.put("result", 1);
			result.put("reason", "参数错误！");
		}
		
		return result;
	}

	/**
	 * 获取查询模板
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/srv/agent/getQueryTemplate", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getQueryTemplate(QueryTemplate queryTemplate) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		
		try {
			list = new Gson().fromJson(customerRepository.getQueryTemplate(queryTemplate), List.class);
		} catch (HiAppException e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
		}
		
		result.put("data", list);
		result.put("result", 0);
		return result;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义配置的查询条件查询所有客户数据.
	 * 
	 * @param queryRequest
	 * @return
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = "/srv/agent/queryMyCustomers", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryMyCustomers(QueryRequest queryRequest,HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = null;
		
		try {
			List<Map<String, Object>> queryMyCustomers = customerRepository.queryMyCustomers(queryRequest, ((User)session.getAttribute("user")).getId());
		} catch (HiAppException e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
		}
		
		result.put("data", list);
		result.put("result", 0);
		return result;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义的查询条件查询预约或待跟进的客户列表
	 * 
	 * @param queryRequest
	 * @return
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = "/srv/agent/queryMyPresetCustomers", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryMyPresetCustomers(QueryRequest queryRequest,HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = null;
		
		try {
			List<Map<String, Object>> queryMyCustomers = customerRepository.queryMyPresetCustomers(queryRequest, ((User)session.getAttribute("user")).getId());
		} catch (HiAppException e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
		}
		
		result.put("data", list);
		result.put("result", 0);
		return result;
	}

	/**
	 * 查询不同业务和管理员自定义的查询条件查询客户列表
	 * 
	 * @param queryRequest
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/queryAllCustomers", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryAllCustomers(QueryRequest queryRequest,
			HttpSession session) {
		User user = (User) session.getAttribute("user");
		String userId = user.getId();
		RoleInGroupSet roleInGroupSet = userRepository.getRoleInGroupSetByUserId(userId);
		Permission permission = permissionRepository.getPermission(roleInGroupSet);
		int permissionId = permission.getId();
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String,Object>> list = null;
		
		try {
			list = customerRepository.queryAllCustomers(queryRequest, permissionId);
		} catch (HiAppException e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
		}
		
		result.put("data", list);
		result.put("result", 0);
		return result;
	}

}
