package hiapp.modules.dmagent.srv;

import hiapp.modules.dmagent.QueryRequest;
import hiapp.modules.dmagent.QueryTemplate;
import hiapp.modules.dmagent.data.CustomerRepository;
import hiapp.system.buinfo.User;
import hiapp.system.dictionary.DictItem;
import hiapp.system.dictionary.data.DictRepository;
import hiapp.utils.base.HiAppException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private DictRepository dictRepository;

	// 获取UserId
	@RequestMapping(value = "/srv/agent/getUserId.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getUserId(HttpSession session) {
		return ((User) session.getAttribute("user")).getId();
	}

	// 更具字典id和字典级别id获取字典文本
	@RequestMapping(value = "/srv/agent/getItemsByDictIdAndLevel.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public List<String> getItemsByDictIdAndLevel(int dicId, int level) {
		// 拿数据
		List<DictItem> listDictItem = new ArrayList<DictItem>();
		dictRepository.getDicItemsByDicId(String.valueOf(dicId), listDictItem);
		ArrayList<String> arrayList = new ArrayList<String>();
		// 参数不合理
		if (level < 0 || level > 4) {
			return arrayList;
		}
		// level为1
		Set<Integer> set = new HashSet<Integer>();

		for (DictItem dictItem : listDictItem) {
			if (dictItem.getItemParent() == -1) {
				arrayList.add(dictItem.getItemText());
				set.add(dictItem.getItemId());
			}
		}
		if (1 == level) {
			return arrayList;
		}
		// level为其他
		int count = 2;
		while (count <= level) {
			arrayList = new ArrayList<String>();
			Set<Integer> newSet = new HashSet<Integer>();
			for (DictItem dictItem : listDictItem) {
				for (int pId : set) {
					if (dictItem.getItemParent() == pId) {
						arrayList.add(dictItem.getItemText());
						newSet.add(dictItem.getItemId());
					}
				}
			}
			set = newSet;
			if (count == level) {
				return arrayList;
			}
			count++;
		}
		return new ArrayList<String>();
	}

	/**
	 * 获取查询HTML模板
	 * @param queryTemplate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/srv/agent/getQueryTemplateForHTML.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getQueryTemplateForHTML(QueryTemplate queryTemplate) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			list = new Gson().fromJson(
					customerRepository.getQueryTemplate(queryTemplate),
					List.class);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
			return result;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<form id='gjSearch' class='gjSearch' class='easyui-form'>");
		for (Map<String, Object> map : list) {
			sb.append("<div style='margin:20px;float:left;width:200px'>");
			String columnName = (String) map.get("columnName");
			String columnNameCH = (String) map.get("columnNameCH");
			String controlType = (String) map.get("controlType");
			String dataType = (String) map.get("dataType");
			if ("文本框".equals(controlType)) {
				sb.append("<input class='easyui-textbox' name='param' columnName='"
						+ columnName
						+ "' dataType='"
						+ dataType
						+ "' style='width:250px' data-options='label:'"
						+ columnNameCH + ":''>");
			} else if ("日期时间框".equals(controlType)) {
				sb.append("<input class='easyui-datetimebox' name='param' columnName='"
						+ columnName
						+ "' dataType='"
						+ dataType
						+ "' label='"
						+ columnNameCH
						+ "' labelPosition='left' style='width:250px'>");
			} else if ("下拉框".equals(controlType)) {
				sb.append("<select class='easyui-combobox' name='param' columnName='"
						+ columnName
						+ "' dataType='"
						+ dataType
						+ "' style='width:250px' data-options='label:'"
						+ columnNameCH + ":''>");
				String dictId = (String) map.get("dictId");
				String dictLevel = (String) map.get("dictLevel");
				List<String> itemsText = getItemsByDictIdAndLevel(
						Integer.parseInt(dictId), Integer.parseInt(dictLevel));
				for (String string : itemsText) {
					sb.append("<option>" + string + "</option>");
				}
				sb.append("</select>");
			}
			sb.append("</div>");
		}
		sb.append("</form>");
		result.put("data", sb.toString());
		result.put("result", 0);
		return result;
	}

	/**
	 * 获取配置查询模板时需要使用的候选列
	 * 
	 * @param bizId
	 * @param configPage
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping(value = "/srv/agent/getCandidadeColumn.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getCandidadeColumn(String bizId,
			String configPage) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> candidadeColumn = null;

		try {
			candidadeColumn = customerRepository.getCandidadeColumn(bizId,
					configPage);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
			return result;
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
	@RequestMapping(value = "/srv/agent/saveQueryTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> saveQueryTemplate(QueryTemplate queryTemplate) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (customerRepository.saveQueryTemplate(queryTemplate)) {
			result.put("result", 0);
		} else {
			result.put("result", 1);
			result.put("reason", "参数错误！");
			return result;
		}

		return result;
	}

	/**
	 * 保存配置好的查询模板
	 * 
	 * @param queryItem
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/saveListTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> saveListTemplate(QueryTemplate queryTemplate) {
		return saveQueryTemplate(queryTemplate);
	}

	/**
	 * 获取查询模板
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/srv/agent/getQueryTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getQueryTemplate(QueryTemplate queryTemplate) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			list = new Gson().fromJson(
					customerRepository.getQueryTemplate(queryTemplate),
					List.class);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
			return result;
		}

		result.put("data", list);
		result.put("result", 0);
		return result;
	}

	/**
	 * 获取列表模板
	 * 
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/getListTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getListTemplate(QueryTemplate queryTemplate) {
		return getQueryTemplate(queryTemplate);
	}

	/**
	 * 讲数据注入模板
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String dataToListPattern1(Map[][] data) {
		return "<table width=100% height=100% cellpadding=0 cellspacing=0>"
				+ "<tr height=18px>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[0][0].get("fontColor")
				+ "'>"
				+ data[0][0].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[0][1].get("fontColor")
				+ "'>"
				+ data[0][1].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[0][2].get("fontColor")
				+ "'>"
				+ data[0][2].get("value")
				+ "</th>"
				+ "</tr>"

				+ "<tr height=18px>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[1][0].get("fontColor")
				+ "'>"
				+ data[1][0].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[1][1].get("fontColor")
				+ "'>"
				+ data[1][1].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[1][2].get("fontColor")
				+ "'>"
				+ data[1][2].get("value")
				+ "</th>"
				+ "</tr>"

				+ "<tr height=18px>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[2][0].get("fontColor")
				+ "'>"
				+ data[2][0].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[2][1].get("fontColor")
				+ "'>"
				+ data[2][1].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[2][2].get("fontColor")
				+ "'>"
				+ data[2][2].get("value")
				+ "</th>" + "</tr>" + "</table>";
	}

	/**
	 * 讲数据注入模板
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<String,String> dataToListPattern(Map[][] data) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		String result = "<table width=100% height=100% cellpadding=0 cellspacing=0>"
				+ "<tr height=18px>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[0][0].get("fontColor")
				+ "'>"
				+ data[0][0].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[0][1].get("fontColor")
				+ "'>"
				+ data[0][1].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[0][2].get("fontColor")
				+ "'>"
				+ data[0][2].get("value")
				+ "</th>"
				+ "</tr>"
				+ "<tr height=18px>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[1][0].get("fontColor")
				+ "'>"
				+ data[1][0].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[1][1].get("fontColor")
				+ "'>"
				+ data[1][1].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[1][2].get("fontColor")
				+ "'>"
				+ data[1][2].get("value")
				+ "</th>"
				+ "</tr>"
				+ "<tr height=18px>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[2][0].get("fontColor")
				+ "'>"
				+ data[2][0].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[2][1].get("fontColor")
				+ "'>"
				+ data[2][1].get("value")
				+ "</th>"
				+ "<th width=118px align=left style='font-size:8px;color: "
				+ data[2][2].get("fontColor")
				+ "'>"
				+ data[2][2].get("value")
				+ "</th>" + "</tr>" + "</table>";
		hashMap.put("compose", result);
		return hashMap;
	}

	/**
	 * 把要显示的数据拼接成html供前台显示
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String,String>> listToHtml(List<List<Map<String, Object>>> queryData) {
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for (List<Map<String, Object>> list : queryData) {
			Map[][] maps = new Map[3][3];
			// 设置默认值
			for (int i = 0; i < maps.length; i++) {
				for (int j = 0; j < maps[i].length; j++) {
					maps[i][j] = new HashMap<String, Object>();
					maps[i][j].put("value", "");
					maps[i][j].put("fontColor", "black");
				}
			}
			// 匹配模板
			for (Map<String, Object> map : list) {
				maps[Integer.parseInt((String) map.get("rowNumber")) - 1][Integer
						.parseInt((String) map.get("colNumber")) - 1] = map;
			}
			result.add(dataToListPattern(maps));
		}
		return result;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义配置的查询条件查询我的客户数据.
	 * 
	 * @param queryRequest
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/queryMyCustomers.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryMyCustomers(QueryRequest queryRequest,
			HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();
		List<List<Map<String, Object>>> list1 = new ArrayList<List<Map<String, Object>>>();
		String userId = ((User) session.getAttribute("user")).getId();

		int pageSize = queryRequest.getPageSize();
		int pageNum = queryRequest.getPageNum();
		if (queryRequest.hasQueryNext()) {
			pageNum = 1;
			try {
				list = customerRepository.queryMyNextCustomer(queryRequest,
						userId);
			} catch (HiAppException e) {
				e.printStackTrace();
				result.put("result", 1);
				result.put("reason", e.getMessage());
				return result;
			}
		}

		int count = 0;
		try {
			count = customerRepository.queryMyCustomersCount(queryRequest,
					userId);
		} catch (HiAppException e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
			return result;
		}

		try {
			list1 = customerRepository.queryMyCustomers(queryRequest, userId);
			list.addAll(list1);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
			return result;
		}

		result.put("data",listToHtml(list));
		result.put("result", 0);
		result.put("pageSize", pageSize);
		result.put("pageNum", pageNum);
		result.put("recordCount", count);
		int pageCount = count / pageSize;
		result.put("pageCount", (count % pageSize == 0) ? pageCount
				: pageCount + 1);
		return result;
	}

	/**
	 * 支持坐席根据不同业务和管理员自定义的查询条件查询预约或待跟进的客户列表
	 * 
	 * @param queryRequest
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/queryMyPresetCustomers.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String,Object> queryMyPresetCustomers(
			QueryRequest queryRequest, HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();
		String userId = ((User) session.getAttribute("user")).getId();
		int pageSize = queryRequest.getPageSize();
		int pageNum = queryRequest.getPageNum();
		try {
			list = customerRepository.queryMyPresetCustomers(queryRequest,
					userId);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
			return result;
		}

		int count = 0;
		try {
			count = customerRepository.queryMyPresetCustomersCount(
					queryRequest, userId);
		} catch (HiAppException e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
			return result;
		}

		result.put("data", listToHtml(list));
		result.put("result", 0);
		result.put("pageSize", pageSize);
		result.put("pageNum", pageNum);
		result.put("recordCount", count);
		int pageCount = count / pageSize;
		result.put("pageCount", (count % pageSize == 0) ? pageCount
				: pageCount + 1);
		return result;
	}

	/**
	 * 查询不同业务和管理员自定义的查询条件查询客户列表
	 * 
	 * @param queryRequest
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/queryAllCustomers.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryAllCustomers(QueryRequest queryRequest,
			HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();
		String userId = ((User) session.getAttribute("user")).getId();

		int pageSize = queryRequest.getPageSize();
		int pageNum = queryRequest.getPageNum();

		int count = 0;
		try {
			count = customerRepository.queryAllCustomersCount(queryRequest,
					userId);
		} catch (HiAppException e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
			return result;
		}

		try {
			list = customerRepository.queryAllCustomers(queryRequest, userId);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
			return result;
		}

		result.put("data", listToHtml(list));
		result.put("result", 0);
		result.put("pageSize", pageSize);
		result.put("pageNum", pageNum);
		result.put("recordCount", count);
		int pageCount = count / pageSize;
		result.put("pageCount", (count % pageSize == 0) ? pageCount
				: pageCount + 1);
		return result;
	}

}
