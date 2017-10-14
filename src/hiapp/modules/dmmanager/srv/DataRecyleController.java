package hiapp.modules.dmmanager.srv;

import hiapp.modules.dmmanager.bean.OutputFirstRow;
import hiapp.modules.dmmanager.bean.RecyleTemplate;
import hiapp.modules.dmmanager.data.DataDistributeJdbc;
import hiapp.modules.dmmanager.data.DataRecyleJdbc;
import hiapp.system.buinfo.Permission;
import hiapp.system.buinfo.RoleInGroupSet;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.data.PermissionRepository;
import hiapp.system.buinfo.data.UserRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

@Controller
public class DataRecyleController {
	@Autowired
	private DataRecyleJdbc dataRecyleJdbc;
	@Autowired
	private DataDistributeJdbc dataDistributeJdbc;
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private UserRepository userRepository;
	/**
	 * 获取所有回收模板
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/DataRecyleController/getAllRecyleTemplate.srv")
	public void getAllRecyleTemplate(HttpServletRequest request, HttpServletResponse response){
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		List<RecyleTemplate> allRecyleTemplate = dataRecyleJdbc.getAllRecyleTemplate(bizId);
		String jsonObject=new Gson().toJson(allRecyleTemplate);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获取前台展示的列
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/DataRecyleController/getTemplateColums.srv")
	public void getTemplateColums(HttpServletRequest request, HttpServletResponse response){
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		Integer templateId=Integer.valueOf(request.getParameter("templateId"));
		List<OutputFirstRow> columns=dataRecyleJdbc.getTemplateColums(bizId, templateId);
		String jsonObject=new Gson().toJson(columns);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据时间获取已分配或已共享的数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/DataRecyleController/getDisOrShareDataByTime.srv")
	public void getDisOrShareDataByTime(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
	    String userId =String.valueOf(user.getId());
	    RoleInGroupSet roleInGroupSet=userRepository.getRoleInGroupSetByUserId(userId);
	  	Permission permission = permissionRepository.getPermission(roleInGroupSet);
	  	int permissionId = permission.getId();
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		Integer templateId=Integer.valueOf(request.getParameter("templateId"));
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		Integer dataType=Integer.valueOf(request.getParameter("dataType"));
		Integer pageNum=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String,Object> resultMap=new HashMap<String, Object>();
		if(dataType==0){
			String tempTableName="HAU_DM_H"+bizId+"S_"+userId;
			dataRecyleJdbc.getDistributeDataByTime(bizId, userId, templateId, startTime, endTime,permissionId);
			resultMap=dataDistributeJdbc.getTempNotDisData(bizId, templateId, userId, pageNum, pageSize,tempTableName);
		}else{
			resultMap=dataRecyleJdbc.getShareDataByTime(bizId, userId, templateId, startTime, endTime, pageNum, pageSize);
		}
		String jsonObject=new Gson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 回收数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/DataRecyleController/recyleData.srv")
	public void recyleData(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
	    String userId =String.valueOf(user.getId());
	    Integer bizId=Integer.valueOf(request.getParameter("bizId"));
	    String shareIds=request.getParameter("shareIds");
	    Integer dataType=Integer.valueOf(request.getParameter("dataType"));
	    Map<String,Object> resultMap=null;
	    if(dataType==0){
	    	resultMap=dataRecyleJdbc.recyleDisData(bizId, userId);
	    }else{
	    	resultMap=dataRecyleJdbc.recyleShareData(bizId,userId, shareIds);
	    }
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
