package hiapp.modules.dmsetting.srv;

import java.util.ArrayList;
import java.util.List;

import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.data.DmBizRepository;
import hiapp.modules.dmsetting.result.DMBizOutboundModel;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResult;
import hiapp.utils.serviceresult.ServiceResultCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuinessController {
	public BuinessController() {
		// TODO Auto-generated constructor stub
		System.out.println("......");
	}
	@Autowired
	private DmBizRepository dmBizRepository;
	
	//新增业务
	@RequestMapping(value = "/srv/dm/dmCreateBiz.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmCreateBiz(@RequestParam("id") String id,
			@RequestParam("name") String name,
			@RequestParam("description") String description,
			@RequestParam("ownerGroupId") String ownerGroupId,
			@RequestParam("modeId") String modeId) {
		ServiceResult serviceresult = new ServiceResult();		
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = dmBizRepository.newDMBusiness(id, name,description, ownerGroupId,modeId, errMessage);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage(errMessage.toString());
			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceresult.toJson();
	}
	
	//获取全部业务
	@RequestMapping(value = "/srv/dm/dmGetAllBiz.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBiz() {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			List<DMBusiness> listDMBusiness = new ArrayList<DMBusiness>();
			if (!dmBizRepository.getAllDMBusiness(listDMBusiness)) {
				return null;
			}
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDMBusiness.size());
			recordsetResult.setPageSize(listDMBusiness.size());
			recordsetResult.setRows(listDMBusiness);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
	
	//修改业务
	@RequestMapping(value = "/srv/dm/dmModifyBiz.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBiz(@RequestParam("bizId") String szDMBId,
			@RequestParam("bizName") String szDMBName,
			@RequestParam("bizDescription") String szDMBDescription,
			@RequestParam("OwnerGroupId") String szOwnerGroupId) {
		ServiceResult serviceresult = new ServiceResult();
		if (szDMBId == "")
			szDMBId = "0";
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = dmBizRepository
					.modifyDMBusiness(szDMBId, szDMBName,
							szDMBDescription, szOwnerGroupId, errMessage);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage(errMessage.toString());
			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceresult.toJson();
	}
	
	//删除业务
	@RequestMapping(value = "/srv/dm/dmDeleteBiz.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmDeleteBiz(@RequestParam("bizId") String id) {
		ServiceResult serviceresult = new ServiceResult();
		try {
			StringBuffer errMessage = new StringBuffer();
			ServiceResultCode serviceResultCode = dmBizRepository.destroyDMBusiness(Integer.parseInt(id),errMessage);
			if (serviceResultCode != ServiceResultCode.SUCCESS) {
				serviceresult.setResultCode(serviceResultCode);
				serviceresult.setReturnMessage(errMessage.toString());
			} else {
				serviceresult.setResultCode(ServiceResultCode.SUCCESS);
				serviceresult.setReturnMessage("删除业务成功！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceresult.toJson();
	}
	
	//获取全部外呼模式信息
	@RequestMapping(value = "/srv/dm/dmGetOutboundModels.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetOutboundModels() {
		RecordsetResult recordsetResult = new RecordsetResult();
		try {
			List<DMBizOutboundModel> listDMBizOutboundModel = new ArrayList<DMBizOutboundModel>();
			if (!dmBizRepository.getAllDMBizModel(listDMBizOutboundModel)) {
				return null;
			}
			recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			recordsetResult.setPage(0);
			recordsetResult.setTotal(listDMBizOutboundModel.size());
			recordsetResult.setPageSize(listDMBizOutboundModel.size());
			recordsetResult.setRows(listDMBizOutboundModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordsetResult.toJson();
	}
}
