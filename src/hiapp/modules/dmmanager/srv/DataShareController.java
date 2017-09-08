package hiapp.modules.dmmanager.srv;

import hiapp.modules.dmmanager.ImportDataMessage;
import hiapp.modules.dmmanager.data.DMBizDataImport;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//数据共享类
public class DataShareController {
    @Autowired
	private DMBizDataImport dMBizDataImport;
	//根据时间筛选导入批次号查询出没有被共享的客户批次数据
	@RequestMapping(value="/srv/DataShareController/getNotShareDataByTime.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public String getNotShareDataByTime(@RequestParam(value="StartTime") String StartTime,
			                            @RequestParam(value="EndTime") String EndTime,
			                            @RequestParam(value="BusinessId") String BusinessId){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd "); 
		RecordsetResult recordsetResult=new RecordsetResult();
		String s=null;
		try {
			Date startTime = sdf.parse(StartTime);
			Date endTime = sdf.parse(EndTime);
			List<ImportDataMessage> importDataMessage=new ArrayList<ImportDataMessage>(); 
			importDataMessage = dMBizDataImport.getNotShareDataByTime(startTime,endTime,BusinessId,importDataMessage);
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);	
			recordsetResult.setTotal(importDataMessage.size());
			recordsetResult.setPageSize(importDataMessage.size());
			recordsetResult.setRows(importDataMessage);
			s=recordsetResult.toJson();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	@RequestMapping(value="/srv/DataShareController/confirmShareData.srv", method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public String confirmShareData(@RequestParam(value="BusinessId") String BusinessId){
		
		
		
		return null;
	}
	
	
	
	
	
}
