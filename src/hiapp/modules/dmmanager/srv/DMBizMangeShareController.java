package hiapp.modules.dmmanager.srv;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import hiapp.system.buinfo.RoleInGroupSet;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.data.UserRepository;
import hiapp.system.buinfo.srv.result.UserView;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//共享管理
@RestController
public class DMBizMangeShareController{
	@Autowired
	private DMBizMangeShare bizMangeShare;
	@Autowired
	private UserRepository userRepository;
	//根据userid的权限 获取到所有的共享批次数据
		@RequestMapping(value="/srv/DMBizMangeShareController/getUserShareBatch.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
		public String getUserShareBatch(HttpServletRequest request){
			HttpSession session=request.getSession(false);
			User user=(User) session.getAttribute("user");
			RecordsetResult recordsetResult=new RecordsetResult();
			String s=null;
			try {
				List<ShareBatchItem> shareBatchItem=new ArrayList<ShareBatchItem>(); 
				List<ShareBatchItem> list = bizMangeShare.getUserShareBatch(shareBatchItem,user);
				recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
				recordsetResult.setPage(0);	
				recordsetResult.setTotal(list.size());
				recordsetResult.setPageSize(list.size());
				recordsetResult.setRows(list);
				s=recordsetResult.toJson();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return s;
		}
	
		//根据userid的权限 获取到规定时间内的共享批次数据，通过业务id
		@RequestMapping(value="/srv/DMBizMangeShareController/getUserShareBatchByTime.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
		public String getUserShareBatchByTime(@RequestParam(value="BusinessID") String businessID,
				                              @RequestParam(value="StartTime") String startTime,
				                              @RequestParam(value="EndTime") String endTime,
				                              HttpServletRequest request){
			HttpSession session=request.getSession(false);
			User user=(User) session.getAttribute("user");
			RecordsetResult recordsetResult=new RecordsetResult();
			String s=null;
			try {
				List<ShareBatchItem> shareBatchItem=new ArrayList<ShareBatchItem>();
				List<ShareBatchItem> list=bizMangeShare.getUserShareBatchByTime(businessID,startTime,endTime,user,shareBatchItem);
				recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
				recordsetResult.setPage(0);	
				recordsetResult.setTotal(list.size());
				recordsetResult.setPageSize(list.size());
				recordsetResult.setRows(list);
				s=recordsetResult.toJson();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return s;
		}
		
		//接收一个共享批次号 设置共享批次的启动时间和结束时间  
		@RequestMapping(value="/srv/DMBizMangeShareController/setShareDataTime.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
		public String setShareDataTime(HttpServletRequest request,
				                       @RequestParam(value="ShareID") String shareID,
				                       @RequestParam(value="StartTime") String startTime,
				                       @RequestParam(value="EndTime") String endTime,
				                       @RequestParam(value="userId") String userId
				){
			HttpSession session=request.getSession(false);
			User user=(User) session.getAttribute("user");
			ServiceResult serviceresult = new ServiceResult();
			ServiceResultCode serviceResultCode=null;
			String s=null;
			try {
				serviceResultCode= bizMangeShare.setShareDataTime(shareID,startTime,endTime,user,userId);
				if(serviceResultCode != ServiceResultCode.SUCCESS){
			    	 serviceresult.setResultCode(serviceResultCode);
					 serviceresult.setReturnMessage("失败"); 
					 s=serviceresult.toJson();
					 return s;
			     }else{
			    	 serviceresult.setReturnCode(0);
					 serviceresult.setResultCode(ServiceResultCode.SUCCESS);
					 serviceresult.setReturnMessage("成功");
					 s=serviceresult.toJson();
					 return s;
			     }
			} catch (Exception e) {
				e.printStackTrace();
			}
			return s;
		}
		
		//设置共享数据是启动还是停止还是暂停ShareID
		@RequestMapping(value="/srv/DMBizMangeShareController/modifyShareState.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
		public String modifyShareState(@RequestParam(value="ShareID") String shareID,
                                       @RequestParam(value="Flag") String flag){
			ServiceResult serviceresult = new ServiceResult();
			ServiceResultCode serviceResultCode=null;
			String s=null;
			try {
				serviceResultCode=bizMangeShare.modifyShareState(shareID,flag);
				if(serviceResultCode != ServiceResultCode.SUCCESS){
			    	 serviceresult.setResultCode(serviceResultCode);
					 serviceresult.setReturnMessage("失败"); 
					 s=serviceresult.toJson();
					 return s;
			     }else{
			    	 serviceresult.setReturnCode(0);
					 serviceresult.setResultCode(ServiceResultCode.SUCCESS);
					 serviceresult.setReturnMessage("成功");
					 s=serviceresult.toJson();
					 return s;
			     }
			} catch (Exception e) {
				e.printStackTrace();
			}
			return s;
		}
		//查询本批次创建人权限下共享区内所属座席池
		@RequestMapping(value="/srv/DMBizMangeShareController/selectShareCustomer.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
		public String selectShareCustomer(HttpServletRequest request){
			HttpSession session=request.getSession(false);
			User user=(User) session.getAttribute("user");
			List<UserView> listUserView =  new ArrayList<UserView>();
			RecordsetResult recordsetResult = new RecordsetResult();
			try {
				List<User> list = bizMangeShare.selectShareCustomer(user);
				for (User users : list) {
					UserView userView = new UserView();
					String userId = users.getId();
					String name = users.getName();
					RoleInGroupSet roleInGroupSetByUserId = userRepository.getRoleInGroupSetByUserId(userId);
					userView.setUserId(userId);
					userView.setUserName(name);
					userView.setGroupRoleString(roleInGroupSetByUserId
							.getRoleInGroupString());
					listUserView.add(userView);
				}
				recordsetResult.setTotal(listUserView.size());
				recordsetResult.setPageSize(listUserView.size());
				recordsetResult.setRows(listUserView);
				recordsetResult.setPage(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return recordsetResult.toJson();
		}
		//将共享数据指定给哪个用户
		@RequestMapping(value="/srv/DMBizMangeShareController/addShareCustomerfByUserId.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
		public String addShareCustomerfByUserId(@RequestParam(value="UserId") String[] userId,
				                                @RequestParam(value="ShareID") String shareID,
				                                @RequestParam(value="BusinessID") String businessID){
			ServiceResult serviceresult = new ServiceResult();
			String p=null;
			String poolId=null;
			String s=null;
			try {
				for (int i = 0; i < userId.length; i++) {
					s=userId[i];
					poolId=bizMangeShare.addShareCustomerfByUserId(businessID,s);
					bizMangeShare.addShareCustomerfByUserIds(poolId,shareID,businessID,s);
				}
					 serviceresult.setReturnMessage("指定共享成功"); 
					 p=serviceresult.toJson();
					 return p;
			} catch (Exception e) {
				e.printStackTrace();
				 serviceresult.setReturnMessage("指定共享成功");
				 p=serviceresult.toJson();
				return p;
			}
		}
}
