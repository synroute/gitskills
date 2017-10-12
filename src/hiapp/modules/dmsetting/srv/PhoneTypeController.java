package hiapp.modules.dmsetting.srv;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hiapp.modules.dmsetting.DMBizPhoneType;
import hiapp.modules.dmsetting.data.DmBizPhoneTypeRepository;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;

@RestController
public class PhoneTypeController {
	@Autowired
	private DmBizPhoneTypeRepository dmBizPhoneTypeRepository;
	//获取所有号码类型
	@RequestMapping(value = "srv/dm/dmGetAllBizPhoneType.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBizPhoneType(@RequestParam("bizId") String bizId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
			List<DMBizPhoneType> listDmBizPhoneType=new ArrayList<DMBizPhoneType>();
			listDmBizPhoneType=dmBizPhoneTypeRepository.dmGetAllBizPhoneType(bizId);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDmBizPhoneType.size());
			recordsetResult.setPageSize(listDmBizPhoneType.size());
			recordsetResult.setRows(listDmBizPhoneType);
		} catch (Exception e) {
			e.printStackTrace();
			recordsetResult.setReturnCode(1);
			recordsetResult.setReturnMessage("失败");
		}
		return recordsetResult.toJson();
	}
	
	//修改号码类型
	@RequestMapping(value = "srv/dm/dmModifyBizPhoneType.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizPhoneType(@RequestParam("bizId") String bizId,@RequestParam("name") String name,
			@RequestParam("nameCh") String nameCh,@RequestParam("description") String description,
			@RequestParam("customerColumnMap") String customerColumnMap,@RequestParam("dialOrder") int dialOrder) {
			
		ServiceResult serviceresult = new ServiceResult();
		DMBizPhoneType dmBizPhoneType=new DMBizPhoneType();
		dmBizPhoneType.setBizId(bizId);
		dmBizPhoneType.setName(name);
		dmBizPhoneType.setNameCh(nameCh);
		dmBizPhoneType.setDescription(description);
		dmBizPhoneType.setCustomerColumnMap(customerColumnMap);
		dmBizPhoneType.setDialOrder(dialOrder);
		StringBuffer errMessage=new StringBuffer();
		if(dmBizPhoneTypeRepository.dmModifyBizPhoneType(dmBizPhoneType, errMessage))
		{
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("成功");
		}else {
			serviceresult.setReturnCode(1);
			serviceresult.setReturnMessage("失败");
		}
		return serviceresult.toJson();
	}
	
	//删除号码类型
	@RequestMapping(value = "srv/dm/dmDeleteBizPhoneType.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmDeleteBizPhoneType(@RequestParam("bizId") String bizId,@RequestParam("name") String name) {
		
		ServiceResult serviceresult = new ServiceResult();
		DMBizPhoneType dmBizPhoneType=new DMBizPhoneType();
		if(dmBizPhoneTypeRepository.dmDeleteBizPhoneType(bizId, name))
		{
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("成功");
		}else {
			serviceresult.setReturnCode(1);
			serviceresult.setReturnMessage("失败");
		}
		return serviceresult.toJson();
	}
	//添加号码类型
	@RequestMapping(value = "srv/dm/dmAddBizPhoneType.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmAddBizPhoneType(@RequestParam("bizId") String bizId,@RequestParam("name") String name,
			@RequestParam("nameCh") String nameCh,@RequestParam("description") String description,
			@RequestParam("customerColumnMap") String customerColumnMap,@RequestParam("dialOrder") int dialOrder) {
		
		ServiceResult serviceresult = new ServiceResult();
		DMBizPhoneType dmBizPhoneType=new DMBizPhoneType();
		dmBizPhoneType.setBizId(bizId);
		dmBizPhoneType.setName(name);
		dmBizPhoneType.setNameCh(nameCh);
		dmBizPhoneType.setDescription(description);
		dmBizPhoneType.setCustomerColumnMap(customerColumnMap);
		dmBizPhoneType.setDialOrder(dialOrder);
		StringBuffer errMessage=new StringBuffer();
		if(dmBizPhoneTypeRepository.dmAddBizPhoneType(dmBizPhoneType, errMessage))
		{
			serviceresult.setReturnCode(0);
			serviceresult.setReturnMessage("成功");
		}else {
			serviceresult.setReturnCode(1);
			serviceresult.setReturnMessage("失败");
		}
		return serviceresult.toJson();
	}
	
	
	
	
	
	
	
}
