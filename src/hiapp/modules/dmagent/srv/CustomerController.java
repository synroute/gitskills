package hiapp.modules.dmagent.srv;

import hiapp.modules.dmagent.QueryRequest;
import hiapp.modules.dmagent.data.CustomerRepository;
import hiapp.system.buinfo.Permission;
import hiapp.system.buinfo.RoleInGroupSet;
import hiapp.system.buinfo.data.PermissionRepository;
import hiapp.system.buinfo.data.UserRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
	 * @param deployPage
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/getCandidadeColumn", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getCandidadeColumn(String bizId,String deployPage) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}
	


	/**
	 * 支持坐席根据不同业务和管理员自定义配置的查询条件查询所有客户数据.
	 * @param queryRequest
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/queryMyCustomers", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryMyCustomers(QueryRequest queryRequest) {
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
	@RequestMapping(value = "/srv/agent/queryMyPresetCustomers", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryMyPresetCustomers(QueryRequest queryRequest) {
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
	@RequestMapping(value = "/srv/agent/queryAllCustomers", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryAllCustomers(
			QueryRequest queryRequest) {
		System.out.println(queryRequest);
		RoleInGroupSet roleInGroupSet = userRepository
				.getRoleInGroupSetByUserId("1001");
		String roleInGroupString = roleInGroupSet.getRoleInGroupString();
		Permission permission = permissionRepository
				.getPermission(roleInGroupSet);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}

	/**
	 * 保存配置好的查询模板
	 * @param queryItem
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/saveQueryTemplate", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> saveQueryItem(String queryItem) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}

	/**
	 * 获取查询模板
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/getQueryTemplate", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getQueryItem() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}

}
