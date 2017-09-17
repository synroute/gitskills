package hiapp.modules.dmmanager.srv;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dmmanager.data.DMBizDataShare;
import hiapp.system.buinfo.User;
import hiapp.utils.idfactory.IdFactory;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//数据共享类
@RestController
public class DMBizShareController {
    @Autowired
	private DMBizDataShare dMBizDataImport;
    @Autowired
    private IdFactory idFactory;
//    @Autowired
    private ShareBatchItem shareBatchItem;
	//根据时间筛选导入批次号查询出没有被共享的客户批次数据
	@RequestMapping(value="/srv/DataShareController/getNotShareDataByTime.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public Map<String, Object> getNotShareDataByTime(@RequestParam(value="StartTime") String StartTime,
			                            @RequestParam(value="EndTime") String EndTime,
			                            @RequestParam(value="BusinessID") String BusinessId,
			                            @RequestParam(value="templateId") String templateId){
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String,Object>> list=null;
		try {
			// List<导入批次信息表>
			//List<ImportBatchMassage> importDataMessage=new ArrayList<ImportBatchMassage>(); 
			//通过时间和业务id查询出所有没有被共享的批次号数据
			//importDataMessage = dMBizDataImport.getNotShareDataByTime(StartTime,EndTime,BusinessId,importDataMessage);
			list = dMBizDataImport.getNotShareDataByTimes(StartTime,EndTime,BusinessId,templateId);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 1);
			result.put("reason", e.getMessage());
		}
		result.put("data", list);
	    result.put("result", 0);
		return result;
	}
	//选择要共享的客户数据确认创建共享批次
	@RequestMapping(value="/srv/DataShareController/confirmShareData.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public String confirmShareData(HttpServletRequest request,
			@RequestParam(value="businessId") String[] businessId,
			@RequestParam(value="importId") String[] importId,
			@RequestParam(value="shareName") String shareName,
			@RequestParam(value="description") String description){
		HttpSession session=request.getSession(false);
		User user=(User) session.getAttribute("user");
		ServiceResult serviceresult = new ServiceResult();
		ServiceResultCode serviceResultCode=null;
		String newId = idFactory.newId("DM_SID");
		String s=null;
		try {
			for (int i = 0; i < businessId.length; i++){
			 String bizid=businessId[i];
			 String iId = importId[i];
			 //向单号码重播共享状态表添加数据并返回共享批次号id
			 dMBizDataImport.confirmShareData(iId,bizid,user,newId);
			 //向单号码重播共享历史表状态表添加数据
			 dMBizDataImport.confirmShareDataOne(iId,bizid,user,newId);
			 //查询当前的业务的数据池
			 String dataPool = dMBizDataImport.confirmShareDataTwo(bizid);
			 //更改数据池记录表数据
			 dMBizDataImport.confirmShareDataThree(iId,dataPool,user);
			 //向数据池操作记录表添加数据
			 dMBizDataImport.confirmShareDataFree(iId,user,dataPool);
			 //向共享批次信息表添加数据
			 serviceResultCode = dMBizDataImport.confirmShareDataFive(bizid,newId,shareName,description);
			}
			if(serviceResultCode != ServiceResultCode.SUCCESS){
		    	 serviceresult.setResultCode(serviceResultCode);
				 serviceresult.setReturnMessage("共享失败"); 
				 s=serviceresult.toJson();
				 return s;
		     }else{
		    	 serviceresult.setReturnCode(0);
				 serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				 serviceresult.setReturnMessage("共享成功");
				 s=serviceresult.toJson();
				 return s;
			}
			}catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	//追加共享
	@RequestMapping(value="/srv/DataShareController/appendShareDataByShareId.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public String appendShareDataByShareId(@RequestParam(value="ShareBatchId") String shareBatchId,
		                                   @RequestParam(value="saveid") String[] saveid,
			                               @RequestParam(value="businessId") String[] businessId,
			                               @RequestParam(value="importId") String[] importId,
			                               HttpServletRequest request,
			                               @RequestParam(value="shareName") String shareName,
			                   			   @RequestParam(value="description") String description){
		HttpSession session=request.getSession(false);
		User user=(User) session.getAttribute("user");
		ServiceResult serviceresult = new ServiceResult();
		ServiceResultCode serviceResultCode=null;
		String s=null;
		try {
			for (int i = 0; i < saveid.length; i++) {	
				 String shareid=saveid[i];
				 String bizid=businessId[i];
				 String iId = importId[i];
			//向单号码重播共享状态表添加数据并返回共享批次号id
			 dMBizDataImport.confirmShareData(shareid,bizid,user,shareBatchId);
			 //向单号码重播共享历史表状态表添加数据
			 dMBizDataImport.confirmShareDataOne(shareid,bizid,user,shareBatchId);
			 //查询当前的业务的数据池
			 String dataPool = dMBizDataImport.confirmShareDataTwo(bizid);
			 //更改数据池记录表数据
			 dMBizDataImport.confirmShareDataThree(iId,dataPool,user);
			 //向数据池操作记录表添加数据
			 dMBizDataImport.confirmShareDataFree(iId,user,dataPool);
			 //向共享批次信息表添加数据
			 serviceResultCode = dMBizDataImport.confirmShareDataFive(bizid,shareBatchId,shareName,description);
			}
			if(serviceResultCode != ServiceResultCode.SUCCESS){
		    	 serviceresult.setResultCode(serviceResultCode);
				 serviceresult.setReturnMessage("追加共享失败"); 
				 s=serviceresult.toJson();
				 return s;
		     }else{
		    	 serviceresult.setReturnCode(0);
				 serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				 serviceresult.setReturnMessage("追加共享成功");
				 s=serviceresult.toJson();
				 return s;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
}
