package hiapp.modules.dmagent.srv;

import hiapp.modules.dmagent.QueryRequest;
import hiapp.modules.dmagent.QueryTemplate;
import hiapp.modules.dmagent.TableNameEnume;
import hiapp.modules.dmagent.data.CustomerRepository;
import hiapp.system.buinfo.User;
import hiapp.system.dictionary.srv.DictionaryConfigurationController;
import hiapp.utils.base.HiAppException;

import java.sql.SQLException;
import java.util.ArrayList;
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
	private DictionaryConfigurationController dictionaryConfigurationController;

	// 从会话中获取当前用户的用户Id
	@RequestMapping(value = "/srv/agent/getUserId.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getUserId(HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result.put("data", ((User) session.getAttribute("user")).getId());
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
		}
		result.put("result", 0);
		return result;
	}

	/**
	 * 获取配置筛选模板时需要使用的待选列
	 * 
	 * @param bizId
	 * @param configPage
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping(value = "/srv/agent/getSourceColumn.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getSourceColumn(String bizId, String configPage) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> candidadeColumn = null;

		try {
			candidadeColumn = customerRepository.getSourceColumn(bizId,
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
	 * 保存配置好的筛选模板（包括普通筛选和高级筛选）
	 * 
	 * @param queryItem
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/saveFilterTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> saveFilterTemplate(QueryTemplate queryTemplate) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (customerRepository.saveFilterTemplate(queryTemplate)) {
			result.put("result", 0);
		} else {
			result.put("result", 1);
			result.put("reason", "参数错误！");
			return result;
		}

		return result;
	}

	/**
	 * 保存客户列表显示模板（在显示客户信息的时候显示哪些字段以及显示的样式）
	 * 
	 * @param queryItem
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/saveDisplayTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> saveDisplayTemplate(QueryTemplate queryTemplate) {
		return saveFilterTemplate(queryTemplate);
	}

	/**
	 * 获取筛选模板（包括普通筛选和高级筛选）
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/srv/agent/getFilterTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getFilterTemplate(QueryTemplate queryTemplate) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			list = new Gson().fromJson(
					customerRepository.getTemplate(queryTemplate), List.class);
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
	 * 获取HTML格式的高级筛选模板
	 * 
	 * @param queryTemplate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/srv/agent/getAdvancedFilterTemplateForHTML.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getAdvancedFilterTemplateForHTML(
			QueryTemplate queryTemplate) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			list = new Gson().fromJson(
					customerRepository.getTemplate(queryTemplate), List.class);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
			return result;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<form id='gjSearch' class='easyui-form'>");
		for (Map<String, Object> map : list) {
			sb.append("<div style='margin:20px;float:left;'>");
			String columnName = (String) map.get("columnName");
			String columnNameCH = (String) map.get("columnNameCH");
			String controlType = (String) map.get("controlType");
			String dataType = (String) map.get("dataType");
			sb.append("<label>" + columnNameCH + ":</label>");
			if ("文本框".equals(controlType)) {
				sb.append("<input class='easyui-textbox' name='param' columnName='"
						+ columnName
						+ "' dataType='"
						+ dataType
						+ "' style='width:250px'>");
			} else if ("日期时间框".equals(controlType)) {
				sb.append("<input class='easyui-datetimebox' name='param' columnName='"
						+ columnName
						+ "' dataType='"
						+ dataType
						+ "' style='width:250px'>");
			} else if ("下拉框".equals(controlType)) {
				sb.append("<select class='easyui-combobox' name='param' columnName='"
						+ columnName
						+ "' dataType='"
						+ dataType
						+ "' style='width:250px'>");
				String dictId = (String) map.get("dictId");
				String dictLevel = (String) map.get("dictLevel");
				List<String> itemsText = dictionaryConfigurationController
						.getItemsByDictIdAndLevel(Integer.parseInt(dictId),
								Integer.parseInt(dictLevel));
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
	 * 获取客户列表展示模板（包括展示哪些列和每列展示的样式）
	 * 
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/getDisplayTemplate.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> getDisplayTemplate(QueryTemplate queryTemplate) {
		return getFilterTemplate(queryTemplate);
	}

	/**
	 * 该方法处理客户列表的显示，用于将数据匹配显示模板，匹配一个客户的信息
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, String> dataToDisplayPattern(Map[][] data,
			Map<String, String> addMap) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		String result = "<table width=376px height=50px cellpadding=0 cellspacing=0>"
				+ "<tr>" + "<th style='width:33.3%;font-size:14px;color:"
				+ data[0][0].get("fontColor")
				+ ";text-align:left;display: inline-block;height: 20px;'>"
				+ "<p style='background:;line-height:20px;border-radius:20px;margin-top: 1px;'>"
				+ data[0][0].get("value")
				+ "</p>"
				+ "</th>"
				+ "<th style='width:33.3%;font-size:14px;color:"
				+ data[0][1].get("fontColor")
				+ ";text-align:left;display: inline-block;height: 20px;'>"
				+ "<p style='background:;line-height:20px;border-radius:20px;margin-top: 1px;'>"
				+ data[0][1].get("value")
				+ "</p>"
				+ "</th>"
				+ "<th style='width:33.3%;font-size:14px;color:"
				+ data[0][2].get("fontColor")
				+ ";text-align:left;display: inline-block;height: 20px;'>"
				+ "<p style='background:;line-height:20px;border-radius:20px;margin-top: 1px;'>"
				+ data[0][2].get("value")
				+ "</p>"
				+ "</th>"
				+ "</tr>"
				+ "<tr>"
				+ "<th style='width:33.3%;font-size:14px;color:"
				+ data[1][0].get("fontColor")
				+ ";text-align:left;display: inline-block;height: 20px;'>"
				+ "<p style='background:;line-height:20px;border-radius:20px;margin-top: 1px;'>"
				+ data[1][0].get("value")
				+ "</p>"
				+ "<th style='width:33.3%;font-size:14px;color:"
				+ data[1][1].get("fontColor")
				+ ";text-align:left;display: inline-block;height: 20px;'>"
				+ "<p style='background:;line-height:20px;border-radius:20px;margin-top: 1px;'>"
				+ data[1][1].get("value")
				+ "</p>"
				+ "</th>"
				+ "<th style='width:33.3%;font-size:14px;color:"
				+ data[1][2].get("fontColor")
				+ ";text-align:left;display: inline-block;height: 20px;'>"
				+ "<p style='background:;line-height:20px;border-radius:20px;margin-top: 1px;'>"
				+ data[1][2].get("value")
				+ "</p>"
				+ "</th>"
				+ "</tr>"
				+ "<tr>"
				+ "<th style='width:33.3%;font-size:14px;color:"
				+ data[2][0].get("fontColor")
				+ ";text-align:left;display: inline-block;height: 20px;'>"
				+ "<p style='background:;line-height:20px;border-radius:20px;margin-top: 1px;'>"
				+ data[2][0].get("value")
				+ "</p>"
				+ "</th>"
				+ "<th style='width:33.3%;font-size:14px;color:"
				+ data[2][1].get("fontColor")
				+ ";text-align:left;display: inline-block;height: 20px;'>"
				+ "<p style='background:;line-height:20px;border-radius:20px;margin-top: 1px;'>"
				+ data[2][1].get("value")
				+ "</p>"
				+ "</th>"
				+ "<th style='width:33.3%;font-size:14px;color:"
				+ data[2][2].get("fontColor")
				+ ";text-align:left;display: inline-block;height: 20px;'>"
				+ "<p style='background:;line-height:20px;border-radius:20px;margin-top: 1px;'>"
				+ data[2][2].get("value")
				+ "</p>"
				+ "</th>"
				+ "</tr>"
				+ "</table>";
		hashMap.put("compose", result);
		hashMap.putAll(addMap);
		return hashMap;
	}

	/**
	 * 该方法处理客户列表的显示，用于将数据匹配显示模板，匹配所有客户的信息
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, String>> listToHtml(
			List<List<Map<String, Object>>> queryData) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (List<Map<String, Object>> list : queryData) {
			HashMap<String, String> addMap = new HashMap<String, String>();
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
				Object rowNumber = map.get("rowNumber");
				Object colNumber = map.get("colNumber");
				String columnName = (String) map.get("columnName");
				if (rowNumber != null && colNumber != null) {
					int row = Integer.parseInt((String) rowNumber) - 1;
					int col = Integer.parseInt((String) colNumber) - 1;
					maps[row][col] = map;
					if (columnName.equals(TableNameEnume.PRESETTABLENAME
							.getAbbr() + "." + "IID")) {
						addMap.put(
								(columnName.substring(3, columnName.length())),
								((String) map.get("value")));
					} else if (columnName.equals(TableNameEnume.PRESETTABLENAME
							.getAbbr() + "." + "CID")) {
						addMap.put(
								(columnName.substring(3, columnName.length())),
								((String) map.get("value")));
					} else if (columnName.equals(TableNameEnume.RESULTTABLENAME
							.getAbbr() + "." + "SOURCEID")) {
						addMap.put(
								(columnName.substring(3, columnName.length())),
								((String) map.get("value")));
					} else if (columnName.equals(TableNameEnume.INPUTTABLENAME
							.getAbbr() + "." + "IID")) {
						addMap.put(
								(columnName.substring(3, columnName.length())),
								((String) map.get("value")));
					} else if (columnName.equals(TableNameEnume.INPUTTABLENAME
							.getAbbr() + "." + "CID")) {
						addMap.put(
								(columnName.substring(3, columnName.length())),
								((String) map.get("value")));
					}

				} else {
					addMap.put((columnName.substring(3, columnName.length())),
							((String) map.get("value")));
				}
			}
			result.add(dataToDisplayPattern(maps, addMap));
		}
		return result;
	}

	/**
	 * 根据条件查询前台页面上“我的客户”模块下符合条件的客户
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

		if (queryRequest.hasQueryNext()) {
			pageNum = 1;
			try {
				list = customerRepository.queryMyNextCustomer(queryRequest,
						userId);
				count += list.size();
			} catch (HiAppException e) {
				e.printStackTrace();
				result.put("total", 1);
				result.put("reason", e.getMessage());
				return result;
			}
		}

		try {
			list1 = customerRepository.queryMyCustomers(queryRequest, userId);
			list.addAll(list1);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("total", 1);
			result.put("reason", e.getMessage());
			return result;
		}

		result.put("rows", listToHtml(list));
		result.put("total", 0);
		result.put("pageSize", pageSize);
		result.put("pageNum", pageNum);
		result.put("recordCount", count);
		int pageCount = count / pageSize;
		result.put("pageCount", (count % pageSize == 0) ? pageCount
				: pageCount + 1);
		return result;
	}

	/**
	 * 根据条件查询前台页面上“联系计划”模块下符合条件的客户
	 * 
	 * @param queryRequest
	 * @return
	 */
	@RequestMapping(value = "/srv/agent/queryMyPresetCustomers.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public Map<String, Object> queryMyPresetCustomers(
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
	 * 根据条件查询前台页面上“所有客户”模块下符合条件的客户
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
