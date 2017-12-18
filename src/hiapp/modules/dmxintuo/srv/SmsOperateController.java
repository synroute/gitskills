package hiapp.modules.dmxintuo.srv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hiapp.modules.dmsetting.DMWorkSheetTypeEnum;
import hiapp.modules.dmsetting.data.DmWorkSheetRepository;
import hiapp.modules.dmxintuo.bean.SmsTemplate;
import hiapp.modules.dmxintuo.data.SmsOperateJdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

@Controller
public class SmsOperateController {
	@Autowired
	private SmsOperateJdbc smsOperateJdbc;
	@Autowired
	private DmWorkSheetRepository dmWorkSheetRepository;
	
	/**
	 * 获取所有模板
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/SmsOperateController/getAllSimTemplate.srv")
	public void getAllSimTemplate(HttpServletRequest request, HttpServletResponse response){
		List<SmsTemplate> resultList = smsOperateJdbc.getAllData();
		String jsonObject=new Gson().toJson(resultList);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 添加一个模板
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/SmsOperateController/insertSmsTemplate.srv")
	public void  insertSmsTemplate(HttpServletRequest request, HttpServletResponse response){
		String  templateName=request.getParameter("templateName");
		String  templateType=request.getParameter("templateType");
		String content=request.getParameter("content");
		Map<String, Object> result = smsOperateJdbc.inserSmstTemplate(templateName, templateType, content);
		String jsonObject=new Gson().toJson(result);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取导入表所有自定义字段
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/SmsOperateController/getImportColumns.srv")
	public void getImportColumns(HttpServletRequest request, HttpServletResponse response){
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		String workSheetId =dmWorkSheetRepository.getWorkSheetIdByType(bizId,DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());
		List<Map<String, Object>> reslutList = smsOperateJdbc.getImportColumns(workSheetId);
		String jsonObject=new Gson().toJson(reslutList);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据id获取模板信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/SmsOperateController/getSmsTemplateInfoById.srv")
	public void getSmsTemplateInfoById(HttpServletRequest request, HttpServletResponse response){
		Integer id=Integer.valueOf(request.getParameter("id"));
		SmsTemplate sms = smsOperateJdbc.getSmsTemplateInfoById(id);
		String jsonObject=new Gson().toJson(sms);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据id 删除模板
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/SmsOperateController/deleteTemplateById.srv")
	public  void deleteTemplateById(HttpServletRequest request, HttpServletResponse response){
		Integer id=Integer.valueOf(request.getParameter("id"));
		Map<String, Object> result = smsOperateJdbc.deleteTemplateById(id);
		String jsonObject=new Gson().toJson(result);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取短信类型
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/SmsOperateController/getSmsType.srv")
	public void  getSmsType(HttpServletRequest request, HttpServletResponse response){
		List<Map<String, Object>> resultList = smsOperateJdbc.getSmsType();
		String jsonObject=new Gson().toJson(resultList);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 批量删除
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/SmsOperateController/deleteBatchData.srv")
	public void deleteBatchData(HttpServletRequest request, HttpServletResponse response){
		String ids=request.getParameter("ids");
		Map<String, Object> result = smsOperateJdbc.deteleBatchData(ids);
		String jsonObject=new Gson().toJson(result);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据合作机构获取短信模板内容
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/SmsOperateController/getContentByType.srv")
	public void getContentByType(HttpServletRequest request, HttpServletResponse response){
		String templateType=request.getParameter("templateType");
		Map<String, Object> resultMap = smsOperateJdbc.getContentByType(templateType);
		String jsonObject=new Gson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}