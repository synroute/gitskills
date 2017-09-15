package hiapp.modules.dmagent.srv;

import hiapp.modules.dmagent.QueryRequest;
import hiapp.modules.dmagent.QueryTemplate;
import hiapp.system.buinfo.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CopyOfCustomerControllerTest {
	/**
	 * 测试接口
	 * @return
	 */
	@RequestMapping(value = "/sayHello", produces = "application/json;charset=utf-8")
	public String sayHello(){
		return "hello";
	}

	/**
	 * 获取配置查询模板时需要使用的候选列
	 * 
	 * @param bizId
	 * @param configPage
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping(value = "/srv/agent/getCandidadeColumn", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getCandidadeColumn(@RequestParam("bizId")String bizId,
			@RequestParam("configPage")String configPage) {
		System.out.println(bizId + ":" + configPage);

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> candidadeColumn = new ArrayList<Map<String, Object>>();

		for (int i = 1; i < 21; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("columnName", "名称" + i);
			map.put("columnNameCH", "中文名称" + i);
			map.put("columnDesc", "描述" + i);
			map.put("dataType", "字段类型" + i);
			map.put("length", "字段长度" + i);
			candidadeColumn.add(map);
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
		System.out.println(queryTemplate);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 0);
		return result;
	}

	/**
	 * 获取查询模板
	 * 
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/getQueryTemplate", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getQueryTemplate(QueryTemplate queryTemplate) {

		System.out.println(queryTemplate);

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (int i = 1; i < 21; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("columnName", "列名" + i);
			map.put("columnNameCH", "用户填入的显示中文" + i);
			map.put("dataType", "数据类型" + i);
			list.add(map);
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
	@RequestMapping(value = "/srv/agent/queryMyCustomers", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryMyCustomers(QueryRequest queryRequest,
			HttpSession session) {
		System.out.println(queryRequest
				+ ((User) session.getAttribute("user")).getId());

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		
		
		for(int i=1;i<21;i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put("姓名", "李四"+i);
			map.put("年龄", "18"+i);
			map.put("通话日期", "2017/02/25 12:12:12"+i);
			list.add(map);
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
	@RequestMapping(value = "/srv/agent/queryMyPresetCustomers", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryMyPresetCustomers(
			QueryRequest queryRequest, HttpSession session) {
		System.out.println(queryRequest
				+ ((User) session.getAttribute("user")).getId());

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		
		
		for(int i=1;i<21;i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put("姓名", "李四"+i);
			map.put("年龄", "18"+i);
			map.put("通话日期", "2017/02/25 12:12:12"+i);
			list.add(map);
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
		System.out.println(queryRequest
				+ ((User) session.getAttribute("user")).getId());

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		
		
		for(int i=1;i<21;i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put("姓名", "李四"+i);
			map.put("年龄", "18"+i);
			map.put("通话日期", "2017/02/25 12:12:12"+i);
			list.add(map);
		}
		

		result.put("data", list);
		result.put("result", 0);
		return result;
	}

}
