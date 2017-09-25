package hiapp.modules.dmmanager.srv;

import hiapp.modules.dmmanager.bean.WorkSheetColumn;
import hiapp.modules.dmmanager.data.DMBizDataShare;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmsetting.DMWorkSheetTypeEnum;
import hiapp.modules.dmsetting.data.DmWorkSheetRepository;
import hiapp.system.buinfo.User;
import hiapp.system.worksheet.data.WorkSheetRepository;
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
	private DMBizDataShare dMBizDataShare;
    @Autowired
    private IdFactory idFactory;
    @Autowired
	private DataImportJdbc dataImportJdbc;
    @Autowired
    private DmWorkSheetRepository dmWorkSheetRepository;
	//根据时间筛选导入批次号查询出没有被共享的客户批次数据
	@RequestMapping(value="/srv/DataShareController/getNotShareDataByTime.srv")
	public void getNotShareDataByTime(@RequestParam(value="StartTime") String StartTime,
			                            @RequestParam(value="EndTime") String EndTime,
			                            @RequestParam(value="BusinessID") String BusinessId,
			                            @RequestParam(value="templateId") String templateId,
			                            @RequestParam(value="sourceType" )String sourceType,
			                            HttpServletResponse response
			                            ){
		List<Map<String,Object>> dataList=null;
		List<Map<String,Object>> allDataList=new ArrayList<Map<String,Object>>();
		try {
			//此接口不通 待修改
			//String workSheetId=dmWorkSheetRepository.getWorkSheetIdByType(Integer.valueOf(BusinessId),DMWorkSheetTypeEnum.WSTDM_IMPORT.getType());
			String workSheetId=dataImportJdbc.getWookSeetId(Integer.valueOf(BusinessId));
			//获取要展示的列
			List<WorkSheetColumn> sheetColumnList=dataImportJdbc.getWorkSeetColumnList(workSheetId);
			dataList = dMBizDataShare.getNotShareDataByTimes(StartTime,EndTime,BusinessId,templateId,sourceType);
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
	//创建共享添加事务
	@RequestMapping(value="/srv/DataShareController/confirmShareData.srv",method=RequestMethod.POST,produces="application/json;charset=utf-8")
	public String addConfirmShareData(HttpServletRequest request,
			@RequestParam(value="businessId") String businessId,
			@RequestParam(value="importId") String importId,
			@RequestParam(value="shareName") String shareName,
			@RequestParam(value="description") String description,
			@RequestParam(value="CID") String Cid){
		HttpSession session=request.getSession(false);
		User user=(User) session.getAttribute("user");
		ServiceResult serviceresult = new ServiceResult();
		ServiceResultCode serviceResultCode=null;
		String newShareId = idFactory.newId("DM_SID");
		int bizid=Integer.parseInt(businessId);
		String[] iId = importId.split(",");
		String[] customerid=Cid.split(",");
		String returnMessage=null;
		try {
				 serviceResultCode=dMBizDataShare.addConfirmShareData(bizid,iId, user,newShareId,customerid, shareName, description);
			if(serviceResultCode != ServiceResultCode.SUCCESS){
		    	 serviceresult.setResultCode(serviceResultCode);
				 serviceresult.setReturnMessage("共享失败"); 
				 returnMessage=serviceresult.toJson();
				 return returnMessage;
		     }else{
		    	 serviceresult.setReturnCode(0);
				 serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				 serviceresult.setReturnMessage("共享成功");
				 returnMessage=serviceresult.toJson();
				 return returnMessage;
			}
		} catch (Exception e) {
		e.printStackTrace();
		}
		return null;
	}
	//追加共享添加事务
	@RequestMapping(value="/srv/DataShareController/appendShareDataByShareId.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public String addConfirmShareDataByShareId(HttpServletRequest request,
			@RequestParam(value="businessId") String businessId,
			@RequestParam(value="importId") String importId,
			@RequestParam(value="shareName") String shareName,
			@RequestParam(value="description") String description,
			@RequestParam(value="shareid") String newShareId,
			@RequestParam(value="CID") String Cid){
		HttpSession session=request.getSession(false);
		User user=(User) session.getAttribute("user");
		ServiceResult serviceresult = new ServiceResult();
		ServiceResultCode serviceResultCode=null;
		int bizid=Integer.parseInt(businessId);
		String[] importid = importId.split(",");
		String[] customerid=Cid.split(",");
		String returnMessage=null;
		try {
				 serviceResultCode=dMBizDataShare.addConfirmShareData(bizid,importid, user,newShareId,customerid, shareName, description);
			if(serviceResultCode != ServiceResultCode.SUCCESS){
		    	 serviceresult.setResultCode(serviceResultCode);
				 serviceresult.setReturnMessage("共享失败"); 
				 returnMessage=serviceresult.toJson();
				 return returnMessage;
		     }else{
		    	 serviceresult.setReturnCode(0);
				 serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				 serviceresult.setReturnMessage("共享成功");
				 returnMessage=serviceresult.toJson();
				 return returnMessage;
			}
		} catch (Exception e) {
		e.printStackTrace();
		}
		return null;
	}
}
