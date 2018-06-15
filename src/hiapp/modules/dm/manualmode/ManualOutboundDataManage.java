package hiapp.modules.dm.manualmode;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomer;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomerPool;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberPredictStateEnum;
import hiapp.modules.dm.util.SQLUtil;
import hiapp.modules.dmmanager.AreaTypeEnum;
import hiapp.modules.dmmanager.OperationNameEnum;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmsetting.DMBizPresetItem;
import hiapp.modules.dmsetting.DMPresetStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * M1 手动分配模式
 * 坐席 抽取数据，客户信息需要按照共享批次分类，由于存在访问权限问题
 */

@Service
public class ManualOutboundDataManage {

    @Autowired
    ManualModeDAO manualModeDAO;

    @Autowired
    ManualModeCustomerPool customerPool;

    @Autowired
    DMDAO dmDAO;

    @Autowired
    DMBizMangeShare dmBizMangeShare;

    @Autowired
    private DataImportJdbc dataImportJdbc;


    public void initialize() {
        customerPool.initialize();
    }

    public synchronized ManualModeCustomer extractNextOutboundCustomer(String userId, int bizId) {
        ManualModeCustomer customer = customerPool.extractCustomer(userId, bizId);
        if (null != customer) {
            //
            Integer poolId = dmBizMangeShare.getDataPoolId(bizId, userId, customer.getDataPoolIdCur());
            customer.setDataPoolIdLast(customer.getDataPoolIdCur());
            customer.setDataPoolIdCur(poolId);
            customer.setAreaTypeLast(customer.getAreaTypeCur());
            customer.setAreaTypeCur(AreaTypeEnum.NO);
            customer.setIsRecover(0);
            customer.setModifyUserId(userId);
            customer.setModifyTime(new Date());
            customer.setOperationName(OperationNameEnum.Extract);
            manualModeDAO.updatePool(customer);

            //
            manualModeDAO.insertPoolOperation(customer, OperationNameEnum.Extract);
        }

        return customer;
    }

    public String submitOutboundResult(String userId, int bizId, String shareBatchId, String importBatchId, String customerId,
                                       String resultCodeType, String resultCode, Boolean isPreset, Date presetTime, String dialType,
                                       Date dialTime, String customerCallId,
                                       Map<String, String> mapCustomizedResultColumn, String customerInfo) {

        //提交：insert导入表、结果表、预约表（如果外呼页面有预约勾选）

        Integer originalModifyId = dmDAO.getModifyIdFromImportTable(bizId, importBatchId, customerId);
        Integer newModifyId = originalModifyId + 1;

        dmDAO.updateDMResult(bizId, shareBatchId, importBatchId, customerId, originalModifyId);
        dmDAO.insertDMResult(bizId, shareBatchId, importBatchId, customerId,
                newModifyId, userId, dialType, dialTime,
                customerCallId, resultCodeType, resultCode, mapCustomizedResultColumn);

        // 插入导入客户表
        dataImportJdbc.insertDataToImPortTable(bizId, importBatchId, customerId, userId, customerInfo, newModifyId);

        if (isPreset) {
            // 插入预约表
            DMBizPresetItem presetItem = new DMBizPresetItem();
            presetItem.setSourceId(shareBatchId);
            presetItem.setImportId(importBatchId);
            presetItem.setCustomerId(customerId);
            presetItem.setPresetTime(presetTime);
            presetItem.setState(DMPresetStateEnum.InUse.getStateName());
            presetItem.setComment("xxx");
            presetItem.setModifyId(newModifyId);
            presetItem.setModifyLast(1);
            presetItem.setModifyUserId(userId);
            presetItem.setModifyTime(new Date());
            presetItem.setModifyDesc("xxx");
            presetItem.setPhoneType("xxx");
            
            /*if(dmDAO.getPresetcount(bizId, presetItem))
            {
            	dmDAO.updatePresetItem(bizId, presetItem,DMPresetStateEnum.InUse.getStateName());
            }else {
            	dmDAO.insertPresetItem(bizId, presetItem);
			}*/
            dmDAO.updatePresetItem(bizId, presetItem,DMPresetStateEnum.InUse.getStateName());
            dmDAO.insertPresetItem(bizId, presetItem);

        }else {
        	DMBizPresetItem presetItem = new DMBizPresetItem();
            presetItem.setSourceId(shareBatchId);
            presetItem.setImportId(importBatchId);
            presetItem.setCustomerId(customerId);
            
            presetItem.setState(DMPresetStateEnum.FinishPreset.getStateName());
            presetItem.setComment("xxx");
            presetItem.setModifyId(newModifyId);
            presetItem.setModifyLast(1);
            presetItem.setModifyUserId(userId);
            presetItem.setModifyTime(new Date());
            presetItem.setModifyDesc("xxx");
            presetItem.setPhoneType("xxx");
            if(dmDAO.getPresetcount(bizId, presetItem))
            {
                dmDAO.updatePresetItem(bizId, presetItem,DMPresetStateEnum.FinishPreset.getStateName());
                dmDAO.insertPresetItem(bizId, presetItem);
            }else {

			}
		}
        //提交外呼结果时删除被抽取的customer
        customerPool.deleteRedisCustomer(bizId, userId);
        return "";
    }

    /*
     * 重新初始化处理不适合，举例说明：系统运行中，进行启用共享批次操作，会导致获取外呼客户的功能暂停一段时间。
     *
     */
    public String startShareBatch(int bizId, List<String> shareBatchIds) {

        // 设置共享批次状态
        dmDAO.updateShareBatchState(bizId, shareBatchIds, ShareBatchStateEnum.ENABLE.getName());

        List<ShareBatchItem> shareBatchItems = shareBatchIncrementalProc(bizId, shareBatchIds);

        loadCustomersIncremental(shareBatchItems);

        return "";
    }

    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        customerPool.stopShareBatch(bizId, shareBatchIds);
    }

    public Boolean appendCustomersToShareBatch(int bizId, List<String> shareBatchIds) {

        // 获取ACTIVE状态的 shareBatchIds
        List<ShareBatchItem> shareBatchItemList = new ArrayList<ShareBatchItem>();
        dmDAO.getActiveShareBatchItems(shareBatchIds, shareBatchItemList);

        loadCustomersAppend(bizId, shareBatchItemList);
        return true;
    }

    // 用户登录通知
    public void onLogin(String userId) {
    }


    ////////////////////////////////////////////////////////////
    public void dailyProc(List<ShareBatchItem> shareBatchItems) {
        customerPool.clear();

        loadCustomersDaily(shareBatchItems);
    }

    public void timeoutProc() {
    }

    public void cancelOutboundTask(int bizId, List<CustomerBasic> customerBasicList) {
        // step 1: remove from pool
        List<ManualModeCustomer> customerList = customerPool.cancelShare(bizId, customerBasicList);

        // step 2: update state in DB AND insert Pool_ORE table
        Date now = new Date();
        List<Integer> customerDBIdList = new ArrayList<Integer>();
        for (ManualModeCustomer customer : customerList) {
            manualModeDAO.insertPoolOperation(customer, OperationNameEnum.CANCELLED);

            customerDBIdList.add(customer.getId());
        }

        manualModeDAO.updateCustomerOperationName(bizId, customerDBIdList, OperationNameEnum.CANCELLED);
    }

    private void loadCustomersDaily(List<ShareBatchItem> shareBatchItems) {

        Date now = new Date();

        // 根据BizId, 归类共享客户数据
        Map<Integer, List<ShareBatchItem>> mapBizIdVsShareBatchItem = new HashMap<Integer, List<ShareBatchItem>>();
        for (ShareBatchItem shareBatchItem : shareBatchItems) {
            List<ShareBatchItem> shareBatchItemList = mapBizIdVsShareBatchItem.get(shareBatchItem.getBizId());
            if (null == shareBatchItemList) {
                shareBatchItemList = new ArrayList<ShareBatchItem>();
                mapBizIdVsShareBatchItem.put(shareBatchItem.getBizId(), shareBatchItemList);
            }
            shareBatchItemList.add(shareBatchItem);
        }

        // 初始化共享池
        List<ManualModeCustomer> shareCustomerItems = new ArrayList<ManualModeCustomer>();

        // 记录客户共享状态为 OperationNameEnum.APPERND 的客户信息
        // 后续需要更改状态为 OperationNameEnum.Sharing
        List<Integer> appendedCustomerIdList = new ArrayList<Integer>();

        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {
            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            shareCustomerItems.clear();
            appendedCustomerIdList.clear();

            manualModeDAO.getGivenBizShareCustomers(bizId, givenBizShareBatchItems, shareCustomerItems);

            for (ManualModeCustomer customer : shareCustomerItems) {
                customerPool.addCustomer(customer);

                if (OperationNameEnum.APPERND.equals(customer.getOperationName())) {
                    appendedCustomerIdList.add(customer.getId());

                    manualModeDAO.insertPoolOperation(customer, OperationNameEnum.Sharing);
                }
            }

            if (!appendedCustomerIdList.isEmpty()) {
                manualModeDAO.updateCustomerOperationName(bizId, appendedCustomerIdList, OperationNameEnum.Sharing);
            }
        }
    }

    // 用于共享批次的启用，根据共享批次，不会导致重复加载
    private void loadCustomersIncremental(List<ShareBatchItem> shareBatchItems) {
        loadCustomersDaily(shareBatchItems);
    }

    /**
     * 过滤出当天需要激活的共享批次
     * @param bizId
     * @param shareBatchIds
     */
    private List<ShareBatchItem> shareBatchIncrementalProc(int bizId, /*IN,OUT*/List<String> shareBatchIds) {
        //List<String> expiredShareBatchIds = new ArrayList<String>();
        dmDAO.expireShareBatchsByEndTime(/*expiredShareBatchIds*/);

        List<ShareBatchItem> shareBatchItems = dmDAO.getCurDayNeedActiveShareBatchItems(bizId, shareBatchIds);

        dmDAO.activateShareBatchByStartTime(bizId, shareBatchItems);

        return shareBatchItems;
    }

    // 处理追加客户的情形
    private void loadCustomersAppend(int bizId, List<ShareBatchItem> shareBatchItems) {

        List<ManualModeCustomer> customerList = new ArrayList<ManualModeCustomer>();

        Boolean result = manualModeDAO.getGivenBizShareCustomersByOperationType(
                                bizId, shareBatchItems, OperationNameEnum.APPERND, customerList);

        // 记录客户共享状态为 OperationNameEnum.APPERND 的客户信息
        // 后续需要更改状态为 OperationNameEnum.Sharing
        List<Integer> appendedCustomerIdList = new ArrayList<Integer>();

        for (ManualModeCustomer customer : customerList) {
            customerPool.addCustomer(customer);

            if (OperationNameEnum.APPERND.equals(customer.getOperationName())) {
                appendedCustomerIdList.add(customer.getId());

                manualModeDAO.insertPoolOperation(customer, OperationNameEnum.Sharing);
            }
        }

        if (!appendedCustomerIdList.isEmpty()) {
            manualModeDAO.updateCustomerOperationName(bizId, appendedCustomerIdList, OperationNameEnum.Sharing);
        }
    }

}
