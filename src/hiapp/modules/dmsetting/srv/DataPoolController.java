package hiapp.modules.dmsetting.srv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.modules.dmsetting.DMDataPool;
import hiapp.modules.dmsetting.data.DmBizDataPoolRepository;
import hiapp.modules.dmsetting.result.DMBizDatePoolGetUserId;
import hiapp.system.buinfo.srv.result.UserView;
import hiapp.utils.serviceresult.RecordsetResult;
import hiapp.utils.serviceresult.ServiceResultCode;
@RestController
public class DataPoolController {
	@Autowired
	private DmBizDataPoolRepository dmBizDataPool;
	
	//获取所有数据池接口
	@RequestMapping(value = "srv/dm/dmGetAllBizDataPool.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetAllBizDataPool(@RequestParam("bizId") int bizId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
				List<DMDataPool> listDmDataPool=dmBizDataPool.dmGetAllBizDataPool(bizId);
				recordsetResult.setPage(0);
				recordsetResult.setTotal(listDmDataPool.size());
				recordsetResult.setPageSize(listDmDataPool.size());
				recordsetResult.setRows(listDmDataPool);
				
				recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();
	}
	
	//新增普通数据池接口
	@RequestMapping(value = "srv/dm/dmCreateBizDataPool.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmCreateBizDataPool(@RequestParam("bizId") int bizId,
			@RequestParam("pId") int Pid,@RequestParam("dataPoolName") String DataPoolName,
			@RequestParam("dataPoolDesc") String DataPoolDes,@RequestParam("poolTopLimit") int PoolTopLimit) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
			DMDataPool dmDataPool=new DMDataPool();
			dmDataPool.setBizId(bizId);
			dmDataPool.setpId(Pid);
			dmDataPool.setDataPoolName(DataPoolName);
			dmDataPool.setDataPoolDesc(DataPoolDes);
			dmDataPool.setPoolTopLimit(PoolTopLimit);
				if(dmBizDataPool.dmCreateBizDataPool(dmDataPool))
				{
					recordsetResult.setReturnCode(dmDataPool.getPoolId());
					recordsetResult.setReturnMessage("成功");
				}else
				{
					recordsetResult.setReturnCode(1);
					recordsetResult.setReturnMessage("失败");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();
	}
	//新增坐席数据池接口
	@RequestMapping(value = "srv/dm/dmCreateBizUserDataPool.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmCreateBizUserDataPool(@RequestParam("bizId") int bizId,
			@RequestParam("pId") int Pid,@RequestParam("userId") String Userid) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
			DMDataPool dmDataPool=new DMDataPool();
			dmDataPool.setBizId(bizId);
			dmDataPool.setpId(Pid);
			
				if(dmBizDataPool.dmCreateBizUserDataPool(dmDataPool,Userid))
				{
					recordsetResult.setReturnCode(0);
					recordsetResult.setReturnMessage("成功");
				}else
				{
					recordsetResult.setReturnCode(1);
					recordsetResult.setReturnMessage("失败");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();
	}
	//修改数据池接口
	@RequestMapping(value = "srv/dm/dmModifyBizDataPool.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmModifyBizDataPool(@RequestParam("poolId") int Id,
			@RequestParam("dataPoolName") String DataPoolName,
			@RequestParam("dataPoolDesc") String DataPoolDes,@RequestParam("poolTopLimit") int PoolTopLimit) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
			DMDataPool dmDataPool=new DMDataPool();
			dmDataPool.setPoolId(Id);
			dmDataPool.setDataPoolName(DataPoolName);
			dmDataPool.setDataPoolDesc(DataPoolDes);
			dmDataPool.setPoolTopLimit(PoolTopLimit);
			
				if(dmBizDataPool.dmModifyBizDataPool(dmDataPool))
				{
					recordsetResult.setReturnCode(0);
					recordsetResult.setReturnMessage("成功");
				}else
				{
					recordsetResult.setReturnCode(1);
					recordsetResult.setReturnMessage("失败");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();
	}
	
	//删除数据池接口
		@RequestMapping(value = "srv/dm/dmDeleteBizDataPool.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
		public String dmDeleteBizDataPool(@RequestParam("poolId") int Id) {
			RecordsetResult recordsetResult = new RecordsetResult();
			try{
				
					if(dmBizDataPool.dmDeleteBizDataPool(Id))
					{
						recordsetResult.setReturnCode(0);
						recordsetResult.setReturnMessage("成功");
					}else
					{
						recordsetResult.setReturnCode(1);
						recordsetResult.setReturnMessage("失败");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					recordsetResult.setReturnCode(1);
					recordsetResult.setReturnMessage("失败");
				}
				return recordsetResult.toJson();
		}
	
	//获取数据池详细信息
	@RequestMapping(value = "srv/dm/dmGetBizDataPool.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizDataPool(@RequestParam("poolId") int poolId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
				List<DMDataPool> listDmDataPool=dmBizDataPool.dmGetBizDataPool(poolId,"数据池");
				recordsetResult.setPage(0);
				recordsetResult.setTotal(listDmDataPool.size());
				recordsetResult.setPageSize(listDmDataPool.size());
				recordsetResult.setRows(listDmDataPool);
				
				recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();
	}
	//获取该业务下所有可选用户
	@RequestMapping(value = "srv/dm/dmGetBizUser.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dmGetBizUser(@RequestParam("bizId") int bizId) {
		RecordsetResult recordsetResult = new RecordsetResult();
		try{
				List<DMBizDatePoolGetUserId> listDMBizDatePoolGetUserId=dmBizDataPool.dmGetBizUser(bizId);
				recordsetResult.setPage(0);
				recordsetResult.setTotal(listDMBizDatePoolGetUserId.size());
				recordsetResult.setPageSize(listDMBizDatePoolGetUserId.size());
				recordsetResult.setRows(listDMBizDatePoolGetUserId);
				
				recordsetResult.setResultCode(ServiceResultCode.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				recordsetResult.setReturnCode(1);
				recordsetResult.setReturnMessage("失败");
			}
			return recordsetResult.toJson();
	}
}
