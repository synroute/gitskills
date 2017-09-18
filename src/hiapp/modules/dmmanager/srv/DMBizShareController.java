package hiapp.modules.dmmanager.srv;

import hiapp.modules.dmmanager.bean.WorkSheetColumn;
import hiapp.modules.dmmanager.data.DMBizDataShare;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.system.buinfo.User;
import hiapp.utils.idfactory.IdFactory;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

//数据共享类
@RestController
public class DMBizShareController {
    @Autowired
	private DMBizDataShare dMBizDataImport;
    @Autowired
    private IdFactory idFactory;
    @Autowired
	private DataImportJdbc dataImportJdbc;
	//根据时间筛选导入批次号查询出没有被共享的客户批次数据
	@RequestMapping(value="/srv/DataShareController/getNotShareDataByTime.srv")
	public void getNotShareDataByTime(@RequestParam(value="StartTime") String StartTime,
			                            @RequestParam(value="EndTime") String EndTime,
			                            @RequestParam(value="BusinessID") String BusinessId,
			                            @RequestParam(value="templateId") String templateId,
			                            HttpServletResponse response
			                            ){
		List<Map<String,Object>> dataList=null;
		List<Map<String,Object>> allDataList=new ArrayList<Map<String,Object>>();
		try {
			String workSheetId=dataImportJdbc.getWookSeetId(Integer.valueOf(BusinessId));
			//获取要展示的列
			List<WorkSheetColumn> sheetColumnList=dataImportJdbc.getWorkSeetColumnList(workSheetId);
			dataList = dMBizDataImport.getNotShareDataByTimes(StartTime,EndTime,BusinessId,templateId);
			for (int i = 0; i < dataList.size(); i++) {
				Map<String,Object> map=new HashMap<String, Object>();
				for (int j = 0; j < sheetColumnList.size(); j++) {
					if(dataList.get(i).keySet().contains(sheetColumnList.get(j).getField())){
						map.put(sheetColumnList.get(j).getField(),dataList.get(i).get(sheetColumnList.get(j).getField()));
					}else{
						map.put(sheetColumnList.get(j).getField(),"");
					}
				}
				allDataList.add(map);
			}
		    String jsonObject=new Gson().toJson(allDataList);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//选择要共享的客户数据确认创建共享批次
	@RequestMapping(value="/srv/DataShareController/confirmShareData.srv",method=RequestMethod.POST,produces="application/json;charset=utf-8")
	public String confirmShareData(HttpServletRequest request,
			@RequestParam(value="businessId") String businessId,
			@RequestParam(value="importId") String importId,
			@RequestParam(value="shareName") String shareName,
			@RequestParam(value="description") String description){
		System.out.println(businessId);
		System.out.println(importId);
		System.out.println(shareName);
		System.out.println(description);
		
		HttpSession session=request.getSession(false);
		User user=(User) session.getAttribute("user");
		ServiceResult serviceresult = new ServiceResult();
		ServiceResultCode serviceResultCode=null;
		String newId = idFactory.newId("DM_SID");
		String s=null;
		String bizid=businessId;
		String[] ary = importId.split(",");
		try {
			for (int i = 0; i < ary.length; i++){
			 String iId = ary[i];
			 //向单号码重播共享状态表添加数据并返回共享批次号id
			 dMBizDataImport.confirmShareData(iId,bizid,user,newId);
			 //向单号码重播共享历史表状态表添加数据
			 dMBizDataImport.confirmShareDataOne(iId,bizid,user,newId);
			 //查询当前的业务的数据池
			 int dataPool = dMBizDataImport.confirmShareDataTwo(bizid);
			 //更改数据池记录表数据
			 dMBizDataImport.confirmShareDataThree(iId,dataPool,user);
			 //向数据池操作记录表添加数据
			 dMBizDataImport.confirmShareDataFree(iId,user,dataPool);
			 //向共享批次信息表添加数据
			 serviceResultCode = dMBizDataImport.confirmShareDataFive(bizid,newId,shareName,description,user);
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
			                               @RequestParam(value="businessId") String businessId,
			                               @RequestParam(value="importId") String[] importId,
			                               HttpServletRequest request,
			                               @RequestParam(value="shareName") String shareName,
			                   			   @RequestParam(value="description") String description){
		HttpSession session=request.getSession(false);
		User user=(User) session.getAttribute("user");
		ServiceResult serviceresult = new ServiceResult();
		ServiceResultCode serviceResultCode=null;
		String s=null;
		 String bizid=businessId;
		try {
			for (int i = 0; i < saveid.length; i++) {	
				 String shareid=saveid[i];
				 String iId = importId[i];
			//向单号码重播共享状态表添加数据并返回共享批次号id
			 dMBizDataImport.confirmShareData(shareid,bizid,user,shareBatchId);
			 //向单号码重播共享历史表状态表添加数据
			 dMBizDataImport.confirmShareDataOne(shareid,bizid,user,shareBatchId);
			 //查询当前的业务的数据池
			 int dataPool = dMBizDataImport.confirmShareDataTwo(bizid);
			 //更改数据池记录表数据
			 dMBizDataImport.confirmShareDataThree(iId,dataPool,user);
			 //向数据池操作记录表添加数据
			 dMBizDataImport.confirmShareDataFree(iId,user,dataPool);
			 //向共享批次信息表添加数据
			 serviceResultCode = dMBizDataImport.confirmShareDataFive(bizid,shareBatchId,shareName,description,user);
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
