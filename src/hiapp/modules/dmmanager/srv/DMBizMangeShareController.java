package hiapp.modules.dmmanager.srv;

import hiapp.modules.dmmanager.ShareBatchItemS;
import hiapp.modules.dmmanager.TreePool;
import hiapp.modules.dmmanager.UserItem;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import hiapp.modules.dmsetting.data.DmWorkSheetRepository;
import hiapp.system.buinfo.Permission;
import hiapp.system.buinfo.RoleInGroupSet;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.data.GroupRepository;
import hiapp.system.buinfo.data.PermissionRepository;
import hiapp.system.buinfo.data.UserRepository;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

//共享管理
@RestController
public class DMBizMangeShareController {
	@Autowired
	private DMBizMangeShare bizMangeShare;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private PermissionRepository permissionRepository;
    @Autowired
    private DmWorkSheetRepository dmWorkSheetRepository;
	// 根据userid的权限和业务id 获取到所有业务下共享批次客户数据
	@RequestMapping(value = "/srv/DMBizMangeShareController/getUserShareBatch.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public void getUserShareBatch(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "businessId") String businessID){
		HttpSession session = request.getSession(false);
		User userid=(User) session.getAttribute("user");
		Integer bizid = Integer.valueOf(businessID);
		Integer pageNum=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultMap = bizMangeShare.getUserShareBatch(userid.getId(),bizid,pageNum,pageSize);
		String jsonObject=new Gson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//  获取到规定时间内的共享批次数据，通过业务id
	@RequestMapping(value = "/srv/DMBizMangeShareController/getUserShareBatchByTime.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String getUserShareBatchByTime(
			@RequestParam(value = "BusinessID") String businessID,
			@RequestParam(value = "StartTime") String startTime,
			@RequestParam(value = "EndTime") String endTime,
			HttpServletRequest request) {
		Integer page=Integer.valueOf(request.getParameter("page"));
		Integer rows=Integer.valueOf(request.getParameter("rows"));
		String json = null;
		try {
			List<ShareBatchItemS> shareBatchItem = new ArrayList<ShareBatchItemS>();
			Map<String,Object> resultMap = bizMangeShare.getUserShareBatchByTime(
					businessID, startTime, endTime,shareBatchItem,page,rows);
			json = new Gson().toJson(resultMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	// 接收一个共享批次号 设置共享批次的启动时间和结束时间
	@RequestMapping(value = "/srv/DMBizMangeShareController/setShareDataTime.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String setShareDataTime(HttpServletRequest request,
			@RequestParam(value = "ShareID") String shareID,
			@RequestParam(value = "StartTime") String startTime,
			@RequestParam(value = "EndTime") String endTime) {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("user");
		ServiceResult serviceresult = new ServiceResult();
		ServiceResultCode serviceResultCode = null;
		String s = null;
		String[] shareId = shareID.split(",");
		try {
			serviceResultCode = bizMangeShare.setShareDataTime(shareId,
					startTime, endTime, user);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage("失败");
				s = serviceresult.toJson();
				return s;
			} else {
				serviceresult.setReturnCode(0);
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("成功");
				s = serviceresult.toJson();
				return s;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	// 设置共享数据是启动还是停止还是暂停ShareID
	@RequestMapping(value = "/srv/DMBizMangeShareController/modifyShareState.srv", produces = "application/json;charset=utf-8")
	public String modifyShareState(
			@RequestParam(value = "ShareID") String shareID,
			@RequestParam(value = "Flag") int flag) {
		ServiceResult serviceresult = new ServiceResult();
		ServiceResultCode serviceResultCode = null;
		String s = null;
		String[] shareId = shareID.split(",");
		try {
			serviceResultCode = bizMangeShare.modifyShareState(shareId, flag);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage("失败");
				s = serviceresult.toJson();
				return s;
			} else {
				serviceresult.setReturnCode(0);
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("成功");
				s = serviceresult.toJson();
				return s;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	// 将共享数据指定给哪个用户
	@RequestMapping(value = "/srv/DMBizMangeShareController/addShareCustomerfByUserId.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String addShareCustomerfByUserId(
			@RequestParam(value = "UserId") String userID,
			@RequestParam(value = "ShareID") String shareID,
			@RequestParam(value = "BusinessID") String businessID,String dataPoolName) {
		ServiceResult serviceresult = new ServiceResult();
		String p = null;
		String[] shareId = shareID.split(",");
		String[] userId=userID.split(",");
		String[] poolName=dataPoolName.split(",");
		try {
				bizMangeShare.addShareCustomerfByUserIds(userId, shareId,
						businessID, poolName);
			serviceresult.setReturnMessage("指定共享成功");
			p = serviceresult.toJson();
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			serviceresult.setReturnMessage("指定共享成功");
			p = serviceresult.toJson();
			return p;
		}
	}
	//页面一加载 查询所有能被共享的用户
	@RequestMapping(value = "/srv/DMBizMangeShareController/selectShareCustomer.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public void TreeViewByUserId(HttpSession session,HttpServletResponse response,HttpServletRequest request,
			@RequestParam(value = "BusinessID") Integer businessID
			){
		RoleInGroupSet roleInGroupSet=userRepository.getRoleInGroupSetByUserId(((User) session.getAttribute("user")).getId());
		Permission permission = permissionRepository.getPermission(roleInGroupSet);
		int permissionId = permission.getId();
		String shareId=request.getParameter("shareId");
		String[] arrShareId=null;
		TreePool treePool=new TreePool();
		bizMangeShare.getUserPoolTree(permissionId,treePool,businessID);
		List<Integer> dataPoolIdList=null;
		if(shareId!=null||!"".equals(shareId)){
			arrShareId=shareId.split(",");
			dataPoolIdList=bizMangeShare.getDataPoolIds(businessID, arrShareId[0]);
		}
		UserItem userItem=bizMangeShare.getUserPoolTreeByPermissionID(businessID,treePool,dataPoolIdList);
		String gson=new Gson().toJson(userItem);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(gson);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//对指定共享批次数据进行删除
	@RequestMapping(value = "/srv/DMBizMangeShareController/DeleteShareBatchDataByShareId.srv",produces = "application/json;charset=utf-8")
	public String DeleteShareBatchDataByShareId(String shareIds,
			@RequestParam(value = "businessId") String businessID){
		    String[] shareId = shareIds.split(",");
		    Integer bizId = Integer.valueOf(businessID);
		    ServiceResultCode serviceResultCode=null;
		    ServiceResult serviceresult = new ServiceResult();
		    String returnMessage=null;
		    try {
		    	serviceResultCode = bizMangeShare.DeleteShareBatchDataByShareId(shareId,bizId);
		    	if(serviceResultCode != ServiceResultCode.SUCCESS){
			    	 serviceresult.setResultCode(serviceResultCode);
					 serviceresult.setReturnMessage("删除失败"); 
					 returnMessage=serviceresult.toJson();
					 return returnMessage;
			     }else{
			    	 serviceresult.setReturnCode(0);
					 serviceresult.setResultCode(ServiceResultCode.SUCCESS);
					 serviceresult.setReturnMessage("删除成功");
					 returnMessage=serviceresult.toJson();
					 return returnMessage;
					 }
		    } catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	
	
}