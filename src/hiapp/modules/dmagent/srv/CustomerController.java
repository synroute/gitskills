package hiapp.modules.dmagent.srv;

import hiapp.modules.dmagent.QueryRequest;
import hiapp.modules.dmagent.data.CustomerRepository;
import hiapp.utils.base.HiAppException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
	@Autowired
	private CustomerRepository customerRepository;
	
	/**
	 * 根据业务id查询worksheetId
	 * @param bizId
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/QueryWorkSheetIdByBizId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> QueryWorkSheetIdByBizId(int bizId){
        Map<String, Object> result = null;
		try {
			result = new HashMap<String, Object>();
			List<Integer> list = customerRepository.QueryWorkSheetIdByBizId(bizId);
			result.put("data", list);
			result.put("result", 0);
		} catch (HiAppException e) {
			e.printStackTrace();
			result.put("result", 0);
			result.put("reason", e.getMessage());
		}
        return result;
    }

	/**
	 * 支持坐席根据不同业务和管理员自定义配置的查询条件查询所有客户数据.
	 * @param queryRequest
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/queryMyCustomers", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
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
	@RequestMapping(value = "/srv/agent/queryMyPresetCustomers", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
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
	@RequestMapping(value = "/srv/agent/queryAllCustomersHasPermission", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
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
	@RequestMapping(value = "/srv/agent/saveQueryItem", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> saveQueryItem(String queryItem){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}
	/**
	 * 获取查询项
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/getQueryItem", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getQueryItem(){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}
	
}
