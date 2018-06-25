package hiapp.modules.dm.singlenumbermode;

import com.google.gson.Gson;
import hiapp.modules.dm.Constants;
import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomer;
import hiapp.modules.dm.singlenumbermode.bo.*;
import hiapp.modules.dm.singlenumbermode.dao.SingleNumberModeDAO;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import hiapp.modules.dm.util.RedisComparator;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmsetting.DMBizPresetItem;
import hiapp.modules.dmsetting.DMPresetStateEnum;
import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * M3 单号码重复外呼
 * 坐席 抽取数据，客户信息需要按照共享批次分类，由于存在访问权限问题
 */

@Service
public class SingleNumberOutboundDataManage {

    @Autowired
    SingleNumberModeDAO singleNumberModeDAO;

    @Autowired
    DMDAO dmDAO;

    @Autowired
    private DmBizOutboundConfigRepository dmBizOutboundConfig;

    @Autowired
    private DataImportJdbc dataImportJdbc;

    @Autowired
    private DMBizMangeShare dmBizMangeShare;

    @Autowired
    private JedisSentinelPool jedisPool;

    // redis客户共享池
    private Jedis redisSingleNumberOutboud;
    // 客户共享池
    // BizID <==> {ShareBatchID <==> PriorityBlockingQueue<SingleNumberModeShareCustomerItem>}
    /*Map<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>> singleNumberOutboundMapPresetDialCustomerSharePool;
    Map<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>> singleNumberOutboundMapStageDialCustomerSharePool;
    Map<Integer, Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>>> singleNumberOutboundMapDialCustomerSharePool;

    // 等待拨打结果的客户池，坐席人员维度
    // UserID <==> {BizID + ImportID + CustomerID <==> SingleNumberModeShareCustomerItem}
    Map<String, Map<String, SingleNumberModeShareCustomerItem>> singleNumberOutboundMapWaitResultCustomerPool;

    // 等待拨打超时的客户池，抽取时间的分钟SLOT维度
    // 分钟Slot <==> {BizId + ImportId + CustomerId <==> SingleNumberModeShareCustomerItem}
    Map<Long, Map<String, SingleNumberModeShareCustomerItem>> singleNumberOutboundMapWaitTimeOutCustomerPool;

    // 等待共享停止/取消的客户池，共享批次维度，用于标注已经停止共享的客户
    // BizId + ShareBatchId <==> {BizId + ImportId + CustomerId <==> SingleNumberModeShareCustomerItem}
    Map<String, Map<String, SingleNumberModeShareCustomerItem>> singleNumberOutboundMapWaitStopCustomerPool;
*/
    Long earliestTimeSlot;

    // 重拨策略
    // BizID <==> EndCodeRedialStrategyM6
    Map<Integer, EndCodeRedialStrategy> mapBizIdVsEndCodeRedialStrategy;

    /**
     * 获取下个外呼客户
     * @param userId
     * @param bizId
     * @return
     */
    //已改
    public synchronized SingleNumberModeShareCustomerItem extractNextOutboundCustomer(String userId, int bizId) {
        Date now = new Date();

        SingleNumberModeShareCustomerItem shareDataItem = null;

        // 根据userID，获取有权限访问的shareBatchIds
        List<String> shareBatchIdList = dmBizMangeShare.getSidUserPool(bizId, userId);

        Map<String, PriorityBlockingQueue<SingleNumberModeShareCustomerItem>> shareBatchIdVsCustomerMap = null;
        PriorityBlockingQueue<SingleNumberModeShareCustomerItem> customerQueue = null;


        shareDataItem = retrievePresetCustomer(bizId, shareBatchIdList, "singleNumberOutboundMapPresetDialCustomerSharePool");
        if (null == shareDataItem) {
            shareDataItem = retrieveGeneralCustomer(bizId, shareBatchIdList, "singleNumberOutboundMapStageDialCustomerSharePool");
        }
        if (null == shareDataItem) {
            shareDataItem = retrieveGeneralCustomer(bizId, shareBatchIdList, "singleNumberOutboundMapDialCustomerSharePool");
        }

        if (null != shareDataItem) {
            //userUseState 弃用
            //singleNumberModeDAO.setUserUseState(bizId, shareDataItem.getShareBatchId(), shareDataItem.getCustomerId());

            shareDataItem.setExtractTime(now);
            shareDataItem.setUserId(userId);

            // 放入 客户等待池
            addWaitCustomer(userId, bizId, shareDataItem);
        }

        return shareDataItem;
    }
    //,,,
    public String submitOutboundResult(String userId, int bizId,
                       String shareBatchId, String importBatchId, String customerId,
                       String resultCodeType, String resultCode, Boolean isPreset, Date presetTime,
                       String dialType, Date dialTime, String customerCallId,
                       Map<String, String> mapCustomizedResultColumn, String customerInfo) {

        SingleNumberModeShareCustomerItem originCustomerItem = removeWaitCustomer(userId, bizId, importBatchId, customerId);

        EndCodeRedialStrategy endCodeRedialStrategy = mapBizIdVsEndCodeRedialStrategy.get(bizId);
        if (null == endCodeRedialStrategy) {
            endCodeRedialStrategy = getEndCodeRedialStrategyByBizId(bizId);
            mapBizIdVsEndCodeRedialStrategy.put(bizId, endCodeRedialStrategy);
        }

        // 经过 Outbound 策略处理器
        procEndcode(userId, originCustomerItem, endCodeRedialStrategy, resultCodeType, resultCode, isPreset, presetTime);

        // 插入结果表
        //dataImportJdbc.insertDataToResultTable(bizId, shareBatchId, importBatchId, customerId, userId, resultData);
        dmDAO.updateDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId, originCustomerItem.getModifyId()); // MODIFYLAST 0
        dmDAO.insertDMResult(bizId, originCustomerItem.getShareBatchId(), importBatchId, customerId,
                          originCustomerItem.getModifyId() + 1, userId, dialType, dialTime,
                            customerCallId, resultCodeType, resultCode, mapCustomizedResultColumn);

        // 插入导入客户表
        dataImportJdbc.insertDataToImPortTable(bizId, importBatchId, customerId, userId, customerInfo, originCustomerItem.getModifyId() + 1);

        return "";
    }

    /*
     * 重新初始化处理不适合，举例说明：系统运行中，进行启用共享批次操作，会导致获取外呼客户的功能暂停一段时间。
     *
     */
    //,,,
    public String startShareBatch(int bizId, List<String> shareBatchIds) {

        // 设置共享批次状态
        dmDAO.updateShareBatchState(bizId, shareBatchIds, ShareBatchStateEnum.ENABLE.getName());

        List<ShareBatchItem> shareBatchItems = shareBatchIncrementalProc(bizId, shareBatchIds);

        loadCustomersIncremental(shareBatchItems);
        return "";
    }
    //已改
    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        removeFromCustomerSharePool(bizId, shareBatchIds, "singleNumberOutboundMapPresetDialCustomerSharePool");
        removeFromCustomerSharePool(bizId, shareBatchIds, "singleNumberOutboundMapStageDialCustomerSharePool");
        removeFromCustomerSharePool(bizId, shareBatchIds, "singleNumberOutboundMapDialCustomerSharePool");

        markShareBatchStopFromCustomerWaitPool(bizId, shareBatchIds);
    }
    //,,,
    public Boolean appendCustomersToShareBatch(int bizId, List<String> shareBatchIds) {

        // 获取ACTIVE状态的 shareBatchIds
        List<ShareBatchItem> shareBatchItemList = new ArrayList<ShareBatchItem>();
        dmDAO.getActiveShareBatchItems(shareBatchIds, shareBatchItemList);

        loadCustomersAppend(bizId, shareBatchItemList);
        return true;
    }

    // 用户登录通知
    //已改
    public void onLogin(String userId) {
        Map<byte[], byte[]> mapUserWaitResultPool = redisSingleNumberOutboud.hgetAll(GenericitySerializeUtil.serialize(
                "singleNumberOutboundMapWaitResultCustomerPool" + userId));
        if (null == mapUserWaitResultPool)
            return;
        Set<Map.Entry<byte[], byte[]>> entries = mapUserWaitResultPool.entrySet();
        for (Map.Entry<byte[], byte[]> entry : entries) {
            SingleNumberModeShareCustomerItem customerItem = GenericitySerializeUtil.unserialize(entry.getValue());
            // 放回客户共享池
            if (!customerItem.getInvalid()) {
                addCustomerToSharePool(customerItem);
            }

            Long timeSlot = customerItem.getExtractTime().getTime()/Constants.timeSlotSpan;
            removeWaitTimeOutCustomer(customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId(), timeSlot);

            removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                    customerItem.getCustomerId());
        }

    }
    //已改
    public void initialize() {
        //初始化redis
        redisSingleNumberOutboud = jedisPool.getResource();
        mapBizIdVsEndCodeRedialStrategy = new HashMap<Integer, EndCodeRedialStrategy>();

        Date now = new Date();
        earliestTimeSlot = now.getTime()/Constants.timeSlotSpan;
        System.out.println("SingleNumber Outbound InitComplete ...");
    }
    //已改,通配符不管用
    public void dailyProc(List<ShareBatchItem> shareBatchItems) {
        //每天清空单号码重播的数据，再加载
        Set<byte[]> keys = redisSingleNumberOutboud.keys("*singleNumberOutbound*".getBytes());
        for (byte[] key : keys) {
            String byteString = null;
            try {
                byteString =GenericitySerializeUtil.unserialize(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //字符串不包含manualMode直接結束
            if (byteString != null && byteString.contains("singleNumberOutbound")) {
                redisSingleNumberOutboud.del(key);
            }
        }
        loadCustomersDaily(shareBatchItems);
    }
    //,,,
    public void cancelOutboundTask(int bizId, List<CustomerBasic> customerBasicList) {
        // step 1 : remove from share pool
        List<SingleNumberModeShareCustomerItem> customerList = cancelShare(bizId, customerBasicList);

        // step 2 : update state in database and insert to share history table
        List<Integer> customerDBIdList = new ArrayList<Integer>();
        for (SingleNumberModeShareCustomerItem customer : customerList) {
            customer.setState(SingleNumberModeShareCustomerStateEnum.CANCELLED);
            singleNumberModeDAO.insertCustomerShareStateHistory(customer);

            customerDBIdList.add(customer.getId());
        }

        singleNumberModeDAO.updateCustomerShareStateToCancel(bizId, customerDBIdList, SingleNumberModeShareCustomerStateEnum.CANCELLED);
    }
    //已改
    public List<SingleNumberModeShareCustomerItem> cancelShare(int bizId, List<CustomerBasic> customerBasicList) {
        List<SingleNumberModeShareCustomerItem> customerList = new ArrayList<SingleNumberModeShareCustomerItem>();
        for (CustomerBasic customerBasic : customerBasicList) {
            byte[] mapSerialize = GenericitySerializeUtil.
                    serialize("singleNumberOutboundMapWaitStopCustomerPool" + customerBasic.getSourceToken());
            //先从redis中取出来设置
            Map<byte[], byte[]> oneShareBatchCustomerPool = redisSingleNumberOutboud.hgetAll(mapSerialize);
            byte[] fieldSerialize = GenericitySerializeUtil.serialize(customerBasic.getCustomerToken());
            SingleNumberModeShareCustomerItem customer = GenericitySerializeUtil.unserialize(oneShareBatchCustomerPool.
                    get(fieldSerialize));
            if (null == customer)
                continue;

            customer.setInvalid(true);
            //再存回redis中
            oneShareBatchCustomerPool.put(fieldSerialize, GenericitySerializeUtil.serialize(customer));
            redisSingleNumberOutboud.hmset(mapSerialize, oneShareBatchCustomerPool);
            customerList.add(customer);
        }

        return customerList;
    }

    /////////////////////////////////////////////////////

    /**
     * 过滤出当天需要激活的共享批次
     * @param bizId
     * @param shareBatchIds
     */
    //,,,
    private List<ShareBatchItem> shareBatchIncrementalProc(int bizId, /*IN*/List<String> shareBatchIds) {
        //List<String> expiredShareBatchIds = new ArrayList<String>();
        dmDAO.expireShareBatchsByEndTime(/*expiredShareBatchIds*/);

        List<ShareBatchItem> shareBatchItems = dmDAO.getCurDayNeedActiveShareBatchItems(bizId, shareBatchIds);

        dmDAO.activateShareBatchByStartTime(bizId, shareBatchItems);

        return shareBatchItems;
    }

    //,,,
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

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.CREATED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.APPENDED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.REVERT);

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList2 = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_STAGE_DIAL);
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);

        List<SingleNumberModeShareCustomerItem> shareDataItems = new ArrayList<SingleNumberModeShareCustomerItem>();
        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            shareDataItems.clear();

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();


            // 根据未接通拨打日期，决定是否清零<当日未接通重拨次数>
            singleNumberModeDAO.clearPreviousDayLostCallCount(bizId);

            // 成批从DB取数据
            Boolean result = singleNumberModeDAO.getGivenBizShareDataItemsByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = singleNumberModeDAO.getGivenBizShareDataItemsByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareCustomerStateList2, shareDataItems);


            // 收集客户共享状态为 SingleNumberModeShareCustomerStateEnum.APPENDED 的客户信息
            // 后续需要更改状态为 SingleNumberModeShareCustomerStateEnum.CREATED
            HashSet<String> appendedStateShareBatchIdSet = new HashSet<String>();

            for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {
                if (needJoinCustomerPool(bizId, customerItem))
                    addCustomerToSharePool(customerItem);

                if (SingleNumberModeShareCustomerStateEnum.APPENDED.equals(customerItem.getState())) {
                    appendedStateShareBatchIdSet.add(customerItem.getShareBatchId());
                }
            }

            if (!appendedStateShareBatchIdSet.isEmpty()) {
                singleNumberModeDAO.updateCustomerShareState(bizId, appendedStateShareBatchIdSet,
                        SingleNumberModeShareCustomerStateEnum.APPENDED, SingleNumberModeShareCustomerStateEnum.CREATED);
            }
        }
    }

    // 用于共享批次的启用，根据共享批次，不会导致重复加载
    //,,,
    private void loadCustomersIncremental(List<ShareBatchItem> shareBatchItems) {

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

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.CREATED);
        //shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.APPENDED);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.REVERT);

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList2 = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_STAGE_DIAL);
        shareCustomerStateList2.add(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);

        List<SingleNumberModeShareCustomerItem> shareDataItems = new ArrayList<SingleNumberModeShareCustomerItem>();

        for (Map.Entry<Integer, List<ShareBatchItem>> entry : mapBizIdVsShareBatchItem.entrySet()) {

            int bizId = entry.getKey();
            List<ShareBatchItem> givenBizShareBatchItems = entry.getValue();

            // 成批从DB取数据
            Boolean result = singleNumberModeDAO.getGivenBizShareDataItemsByState(
                    bizId, givenBizShareBatchItems, shareCustomerStateList, shareDataItems);

            // 成批从DB取数据, 根据nextDialTime
            result = singleNumberModeDAO.getGivenBizShareDataItemsByStateAndNextDialTime(
                    bizId, givenBizShareBatchItems, shareCustomerStateList2, shareDataItems);
        }

        for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {
            addCustomerToSharePool(customerItem);
        }
    }
    //已改
    //匿名Comparator实现
    private static RedisComparator<SingleNumberModeShareCustomerItem> nextDialTimeComparator = new RedisComparator<SingleNumberModeShareCustomerItem>() {

        @Override
        public int compare(SingleNumberModeShareCustomerItem c1, SingleNumberModeShareCustomerItem c2) {
            if (c1 != null && c2 != null && c1.getNextDialTime() != null && c2.getNextDialTime() != null){
                return (c1.getNextDialTime().before(c2.getNextDialTime())) ? 1 : -1;
            }
            return 0;
        }
    };

    //匿名Comparator实现
    private static RedisComparator<SingleNumberModeShareCustomerItem> shareBatchBeginTimeComparator = new RedisComparator<SingleNumberModeShareCustomerItem>() {

        @Override
        public int compare(SingleNumberModeShareCustomerItem c1, SingleNumberModeShareCustomerItem c2) {
            if (c1 != null && c2 != null&& c1.getShareBatchStartTime() != null && c2.getShareBatchStartTime() != null){
                return (c1.getShareBatchStartTime().before(c2.getShareBatchStartTime())) ? 1 : -1;
            }
            return 0;
        }
    };
    //已改
    private void addWaitCustomer(String userId, int bizId, SingleNumberModeShareCustomerItem customerItem) {
        redisSingleNumberOutboud.hset(GenericitySerializeUtil.serialize("singleNumberOutboundMapWaitResultCustomerPool" + userId),
                GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                GenericitySerializeUtil.serialize(customerItem));

        Long timeSlot = customerItem.getExtractTime().getTime()/Constants.timeSlotSpan;
        redisSingleNumberOutboud.hset(GenericitySerializeUtil.serialize("singleNumberOutboundMapWaitTimeOutCustomerPool" + timeSlot),
                GenericitySerializeUtil.serialize(customerItem.getBizId() + customerItem.getImportBatchId() + customerItem.getCustomerId()),
                GenericitySerializeUtil.serialize(customerItem));
    }
    //已改
    private SingleNumberModeShareCustomerItem removeWaitCustomer(String userId, int bizId, String importBatchId, String customerId) {

        SingleNumberModeShareCustomerItem customerItem = removeWaitResultCustome(userId, bizId, importBatchId, customerId);
        if (null == customerItem)
            return customerItem;

        Long timeSlot = customerItem.getExtractTime().getTime()/Constants.timeSlotSpan;
        removeWaitTimeOutCustomer(bizId, importBatchId, customerId, timeSlot);

        removeWaitStopCustomer(bizId, customerItem.getShareBatchId(), importBatchId, customerId);

        return customerItem;
    }
    //已改
    private SingleNumberModeShareCustomerItem removeWaitResultCustome(String userId, int bizId, String importBatchId, String customerId) {
        SingleNumberModeShareCustomerItem customerItem = null;
        byte[] removeSerialize = GenericitySerializeUtil.serialize("singleNumberOutboundMapWaitResultCustomerPool" + userId);
        byte[] fieldSerialize = GenericitySerializeUtil.serialize(bizId + importBatchId + customerId);
        Boolean hexists = redisSingleNumberOutboud.hexists(removeSerialize, fieldSerialize);
        if (hexists){
            customerItem =  GenericitySerializeUtil.unserialize(redisSingleNumberOutboud.hget(removeSerialize, fieldSerialize));
            redisSingleNumberOutboud.hdel(removeSerialize, fieldSerialize);
        }
        return customerItem;
    }
    //已改
    private void removeWaitTimeOutCustomer(int bizId, String importBatchId, String customerId, Long timeSlot) {
        redisSingleNumberOutboud.hdel(GenericitySerializeUtil.serialize("singleNumberOutboundMapWaitTimeOutCustomerPool" + timeSlot),
                GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
    }
    //已改
    private void removeWaitStopCustomer(int bizId, String shareBatchId, String importBatchId, String customerId) {
        redisSingleNumberOutboud.hdel(GenericitySerializeUtil.serialize("singleNumberOutboundMapWaitStopCustomerPool" + bizId + shareBatchId),
                GenericitySerializeUtil.serialize(bizId + importBatchId + customerId));
    }
    //不用redis
    private void procEndcode(String userId, SingleNumberModeShareCustomerItem originCustomerItem,
                             EndCodeRedialStrategy endCodeRedialStrategy,
                             String resultCodeType, String resultCode, Boolean isPreset, Date presetTime) {

        Date now = new Date();

        RedialState newRedialState = endCodeRedialStrategy.getNextRedialState(resultCodeType, resultCode);
        RedialStateTypeEnum redialStateType = newRedialState.getStateTypeEnum();

        SingleNumberModeShareCustomerItem item = new SingleNumberModeShareCustomerItem();
        item.setBizId(originCustomerItem.getBizId());
        item.setShareBatchId(originCustomerItem.getShareBatchId());
        item.setImportBatchId(originCustomerItem.getImportBatchId());
        item.setCustomerId(originCustomerItem.getCustomerId());
        item.setEndCodeType(resultCodeType);
        item.setEndCode(resultCode);
        item.setModifyUserId(userId);
        item.setModifyTime(now);
        item.setModifyId(originCustomerItem.getModifyId() + 1);
        item.setLastDialTime(now);  //TODO
        item.setShareBatchStartTime(originCustomerItem.getShareBatchStartTime());

        if (RedialStateTypeEnum.REDIAL_STATE_FINISHED.equals(redialStateType)) {
            item.setState(SingleNumberModeShareCustomerStateEnum.FINISHED);

            // 更新共享状态表
            singleNumberModeDAO.updateCustomerShareStateToFinish(item);

            // 插入共享历史表
            singleNumberModeDAO.insertCustomerShareStateHistory(item);

        } else if (RedialStateTypeEnum.REDIAL_STATE_PRESET.equals(redialStateType)) {
            item.setState(SingleNumberModeShareCustomerStateEnum.PRESET_DIAL);

            // 更新共享状态表   nextDialTime
            item.setNextDialTime(presetTime);
            singleNumberModeDAO.updateCustomerShareStateToPreset(item);

            // 插入共享历史表
            singleNumberModeDAO.insertCustomerShareStateHistory(item);

            // 插入预约表
            DMBizPresetItem presetItem = new DMBizPresetItem();
            presetItem.setSourceId(originCustomerItem.getShareBatchId());
            presetItem.setCustomerId(originCustomerItem.getCustomerId());
            presetItem.setImportId(originCustomerItem.getImportBatchId());
            presetItem.setPresetTime(presetTime);
            presetItem.setState(DMPresetStateEnum.InUse.getStateName());
            presetItem.setComment("xxx");
            presetItem.setModifyId(item.getModifyId());
            presetItem.setModifyLast(1);
            presetItem.setModifyUserId(userId);
            presetItem.setModifyTime(now);
            presetItem.setModifyDesc("xxx");
            presetItem.setPhoneType("xxx");
            dmDAO.insertPresetItem(originCustomerItem.getBizId(), presetItem);

            // 不要移出候选池，预约在今天
            if (DateUtil.isSameDay(now, presetTime)) {
                addCustomerToSharePool(item);
            }

        } else if (RedialStateTypeEnum.REDIAL_STATE_STAGE.equals(redialStateType)) {
            item.setState(SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_STAGE_DIAL);

            // 更新共享状态表  nextDialTime  curRedialStageCount
            // 到达最后阶段，直接跳转状态
            item.setCurRedialStageCount(originCustomerItem.getCurRedialStageCount() + 1);
            if (item.getCurRedialStageCount() >= endCodeRedialStrategy.getStageLimit()) {
                item.setState(SingleNumberModeShareCustomerStateEnum.getFromString(
                        endCodeRedialStrategy.getStageExceedNextStateName()));
            } else {
                item.setNextDialTime(DateUtil.getNextXDay(newRedialState.getStageRedialDelayDaysNum()));
            }

            singleNumberModeDAO.updateCustomerShareStateToStage(item);

            // 插入共享历史表
            singleNumberModeDAO.insertCustomerShareStateHistory(item);

        } else if (RedialStateTypeEnum.REDIAL_STATE_LOSTCALL.equals(redialStateType)) {
            item.setState(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL);

            // 更新共享状态表  lostcallFirstDay  curDayLostCallCount  lostcallTotalCount
            // 总未接通数到达限定值，直接跳转状态
            // 每天未接通数到达限定值，移出候选池。每天处理时重新移回候选池。
            if (0 == originCustomerItem.getLostCallTotalCount()) {
                // 第一次发生未接通 的情形
                item.setLostCallFirstDay(now);
                originCustomerItem.setLostCallFirstDay(now);  // 必须设置，为了保持后续一致
            }

            item.setLostCallTotalCount(originCustomerItem.getLostCallTotalCount() + 1);
            item.setLostCallCurDayCount(originCustomerItem.getLostCallCurDayCount() + 1);
            if (item.getLostCallTotalCount() >= newRedialState.getLoopRedialDialCountNum()) {
                item.setState(SingleNumberModeShareCustomerStateEnum.getFromString(
                        newRedialState.getLoopRedialCountExceedNextState()));
            } else {
                int todayLoopRedialCountLimit = newRedialState.getLoopRedialPerdayCountLimitNum();
                if (DateUtil.isSameDay(now, originCustomerItem.getLostCallFirstDay())) {
                    todayLoopRedialCountLimit = newRedialState.getLoopRedialFirstDialDayDialCountLimitNum();
                }

                if (item.getLostCallCurDayCount() < todayLoopRedialCountLimit) {
                    //不要移出候选池，还需要继续拨打
                    addCustomerToSharePool(item);
                }
            }

            singleNumberModeDAO.updateCustomerShareStateToLostCall(item);

            // 插入共享历史表
            singleNumberModeDAO.insertCustomerShareStateHistory(item);
        }

        //若是当前是预约拨打，更新 预约状态 @ 预约表
        if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(originCustomerItem.getState())) {
            dmDAO.updatePresetState(item.getBizId(), item.getShareBatchId(), item.getImportBatchId(),
                    item.getCustomerId(), originCustomerItem.getModifyId(),
                    DMPresetStateEnum.FinishPreset.getStateName());
        }

    }
    //,,,
    private EndCodeRedialStrategy getEndCodeRedialStrategyByBizId(int bizId) {
        String jsonEndCodeRedialStrategy = dmBizOutboundConfig.dmGetAllBizOutboundSetting(bizId);

        EndCodeRedialStrategyFromDB endCodeRedialStrategyFromDB = new Gson().fromJson(jsonEndCodeRedialStrategy,
                EndCodeRedialStrategyFromDB.class);

        return EndCodeRedialStrategy.getInstance(endCodeRedialStrategyFromDB);
    }
    //已改,,,
    private void addCustomerToSharePool(SingleNumberModeShareCustomerItem newCustomerItem) {
        PriorityBlockingQueue<SingleNumberModeShareCustomerItem> queue;
        byte[] fieldSerialize = GenericitySerializeUtil.serialize(newCustomerItem.getShareBatchId());
        if (SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_STAGE_DIAL.equals(newCustomerItem.getState())) {
            //先从redis中取出来
            byte[] mapSerialize = GenericitySerializeUtil.serialize("singleNumberOutboundMapStageDialCustomerSharePool" + newCustomerItem.getBizId());
            queue = GenericitySerializeUtil.unserialize(redisSingleNumberOutboud.hget(mapSerialize,fieldSerialize));
            if (null == queue || queue.isEmpty()) {
                queue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, nextDialTimeComparator);
            }
            queue.put(newCustomerItem);
            redisSingleNumberOutboud.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(queue));

           // 下面两个同理
        } else if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(newCustomerItem.getState())) {
            //先从redis中取出来
            byte[] mapSerialize = GenericitySerializeUtil.serialize("singleNumberOutboundMapPresetDialCustomerSharePool" + newCustomerItem.getBizId());
            queue = GenericitySerializeUtil.unserialize(redisSingleNumberOutboud.hget(mapSerialize,fieldSerialize));
            if (null == queue || queue.isEmpty()) {
                queue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, nextDialTimeComparator);
            }
            queue.put(newCustomerItem);
            redisSingleNumberOutboud.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(queue));
        } else {
            //先从redis中取出来
            byte[] mapSerialize = GenericitySerializeUtil.serialize("singleNumberOutboundMapDialCustomerSharePool" + newCustomerItem.getBizId());
            queue = GenericitySerializeUtil.unserialize(redisSingleNumberOutboud.hget(mapSerialize,fieldSerialize));
            if (null == queue || queue.isEmpty()) {
                queue = new PriorityBlockingQueue<SingleNumberModeShareCustomerItem>(1, shareBatchBeginTimeComparator);
            }
            queue.put(newCustomerItem);
            redisSingleNumberOutboud.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(queue));
        }
        redisSingleNumberOutboud.hset(GenericitySerializeUtil.serialize("singleNumberOutboundMapWaitStopCustomerPool" + newCustomerItem.getShareToken()),
                GenericitySerializeUtil.serialize(newCustomerItem.getCustomerToken()),GenericitySerializeUtil.serialize(newCustomerItem));
        System.out.println("M3 add customer: bizId[" + newCustomerItem.getBizId()
                + "] shareId[" + newCustomerItem.getShareBatchId() + "] IID[" + newCustomerItem.getImportBatchId()
                + "] CID[" + newCustomerItem.getCustomerId() + "]");
    }

    // 用于过滤 当日重拨已满的客户
    //,,,
    Boolean needJoinCustomerPool(int bizId, SingleNumberModeShareCustomerItem customerItem) {

        if (!SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL.equals(customerItem.getState()))
            return true;

        Date now = new Date();
        if (!DateUtil.isSameDay(customerItem.getLostCallCurDay(), now))
            return true;

        EndCodeRedialStrategy endCodeRedialStrategy = mapBizIdVsEndCodeRedialStrategy.get(bizId);
        if (null == endCodeRedialStrategy) {
            endCodeRedialStrategy = getEndCodeRedialStrategyByBizId(bizId);
            mapBizIdVsEndCodeRedialStrategy.put(bizId, endCodeRedialStrategy);
        }

        RedialState redialState = endCodeRedialStrategy.getNextRedialState(customerItem.getEndCodeType(), customerItem.getEndCode());

        int curDayLostCallRedialCountLimit = 0;
        if (DateUtil.isSameDay(customerItem.getLostCallFirstDay(), now)) {
            curDayLostCallRedialCountLimit = redialState.getLoopRedialFirstDialDayDialCountLimitNum();
        } else {
            curDayLostCallRedialCountLimit = redialState.getLoopRedialPerdayCountLimitNum();
        }

        if (customerItem.getLostCallCurDayCount() < curDayLostCallRedialCountLimit)
            return true;

        return false;
    }

    // 处理追加客户的情形
    //,,,,
    private void loadCustomersAppend(int bizId, List<ShareBatchItem> shareBatchItems) {
        System.out.println("bizId : " + bizId);

        List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList = new ArrayList<SingleNumberModeShareCustomerStateEnum>();
        shareCustomerStateList.add(SingleNumberModeShareCustomerStateEnum.APPENDED);

        List<SingleNumberModeShareCustomerItem> shareDataItems = new ArrayList<SingleNumberModeShareCustomerItem>();

        // TODO 成批从DB取数据
        Boolean result = singleNumberModeDAO.getGivenBizShareDataItemsByState(
                bizId, shareBatchItems, shareCustomerStateList, shareDataItems);

        // 记录客户共享状态为 SingleNumberModeShareCustomerStateEnum.APPENDED 的客户信息
        // 后续需要更改状态为 SingleNumberModeShareCustomerStateEnum.CREATED
        HashSet<String> appendedStateShareBatchIdSet = new HashSet<String>();

        for (SingleNumberModeShareCustomerItem customerItem : shareDataItems) {
            addCustomerToSharePool(customerItem);

            if (SingleNumberModeShareCustomerStateEnum.APPENDED.equals(customerItem.getState())) {
                appendedStateShareBatchIdSet.add(customerItem.getShareBatchId());
            }
        }

        singleNumberModeDAO.updateCustomerShareState(bizId, appendedStateShareBatchIdSet,
                SingleNumberModeShareCustomerStateEnum.APPENDED, SingleNumberModeShareCustomerStateEnum.CREATED);
    }
    //已改
    private void removeFromCustomerSharePool(int bizId, List<String> shareBatchIds,
                                             String customerPoolString)  {
        byte[] mapSerialize = GenericitySerializeUtil.serialize(
                customerPoolString + bizId);
        Map<byte[], byte[]> shareBatchIdVsCustomerMap = redisSingleNumberOutboud.hgetAll(mapSerialize);
        if (!shareBatchIdVsCustomerMap.isEmpty()) {
            for (String shareBatchId : shareBatchIds) {
                redisSingleNumberOutboud.hdel(mapSerialize, GenericitySerializeUtil.serialize(shareBatchId));
            }
        }
    }

    /**
     * 仅标注已经停止共享，不从等待池中移除。需要等待已拨打的结果。
     * @param bizId
     * @param shareBatchIds
     */
    //已改
    private void markShareBatchStopFromCustomerWaitPool(int bizId, List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            byte[] mapSerialize = GenericitySerializeUtil.serialize("singleNumberOutboundMapWaitStopCustomerPool" + bizId + shareBatchId);
            Map<byte[], byte[]> mapWaitStopPool = redisSingleNumberOutboud.hgetAll(mapSerialize);
            Set<Map.Entry<byte[], byte[]>> entries = mapWaitStopPool.entrySet();
            for (Map.Entry<byte[], byte[]> entry : entries) {
                SingleNumberModeShareCustomerItem item = GenericitySerializeUtil.unserialize(entry.getValue());
                item.setInvalid(true);
                redisSingleNumberOutboud.hset(mapSerialize, entry.getKey(), GenericitySerializeUtil.serialize(item));
            }
        }
    }

    /**
     * 获取有具体拨打时间（时分）的客户
     * @param bizId
     * @param shareBatchIdList
     * @return
     */
    //已改
    private SingleNumberModeShareCustomerItem retrievePresetCustomer(int bizId, List<String> shareBatchIdList,
                       String customerSharePoolString) {
        byte[] mapSerialize = GenericitySerializeUtil.serialize(customerSharePoolString + bizId);
        Map<byte[], byte[]> shareBatchIdVsCustomerMap = redisSingleNumberOutboud.hgetAll(mapSerialize);
        if (shareBatchIdVsCustomerMap.isEmpty()) {
            return null;
        }

        Date now = new Date();

        // TODO 目前在一个共享批次中取得就返回了，其实可以PEEK遍所有共享批次后比较拨打时间，确定先取那个客户
        for (String shareBatchId : shareBatchIdList) {
            byte[] fieldSerialize = GenericitySerializeUtil.serialize(shareBatchId);
            PriorityBlockingQueue<SingleNumberModeShareCustomerItem> customerQueue = GenericitySerializeUtil.
                    unserialize(redisSingleNumberOutboud.hget(mapSerialize, fieldSerialize));

            if (null == customerQueue || customerQueue.isEmpty()) {
                continue;
            }

            SingleNumberModeShareCustomerItem shareDataItem = customerQueue.peek();
            if (shareDataItem.getInvalid()) {
                customerQueue.poll();  // 丢弃 作废的客户
                redisSingleNumberOutboud.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(customerQueue));
                continue;
            }

            if (shareDataItem.getNextDialTime().before(now)) {
                shareDataItem = customerQueue.poll();
                redisSingleNumberOutboud.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(customerQueue));
                return shareDataItem;
            }
        }

        return null;
    }

    /**
     * 获取当天拨打的客户
     * @param bizId
     * @param shareBatchIdList
     * @return
     */
    //已改
    private SingleNumberModeShareCustomerItem retrieveGeneralCustomer(int bizId, List<String> shareBatchIdList,
                   String customerSharePoolString) {

        byte[] mapSerialize = GenericitySerializeUtil.serialize(customerSharePoolString + bizId);
        Map<byte[], byte[]> shareBatchIdVsCustomerMap = redisSingleNumberOutboud.hgetAll(mapSerialize);
        if (shareBatchIdVsCustomerMap.isEmpty()) {
            return null;
        }

        for (String shareBatchId : shareBatchIdList) {
            byte[] fieldSerialize = GenericitySerializeUtil.serialize(shareBatchId);
            PriorityBlockingQueue<SingleNumberModeShareCustomerItem> customerQueue = GenericitySerializeUtil.
                    unserialize(redisSingleNumberOutboud.hget(mapSerialize, fieldSerialize));
            if (null == customerQueue || customerQueue.isEmpty()) {
                continue;
            }

            SingleNumberModeShareCustomerItem shareDataItem = customerQueue.poll();
            redisSingleNumberOutboud.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(customerQueue));
            if (shareDataItem.getInvalid()){
                continue;
            }
            return shareDataItem;
        }

        return null;
    }
    //已改
    public void timeoutProc() {
        Date now =  new Date();
        Long curTimeSlot = now.getTime()/Constants.timeSlotSpan;
        Long timeoutTimeSlot = curTimeSlot - Constants.timeoutThreshold/Constants.timeSlotSpan;

        while (earliestTimeSlot < timeoutTimeSlot) {
            Map<byte[], byte[]> mapTimeSlotWaitTimeOutPool = redisSingleNumberOutboud.hgetAll(GenericitySerializeUtil.serialize("singleNumberOutboundMapWaitTimeOutCustomerPool" + earliestTimeSlot++));
            if (mapTimeSlotWaitTimeOutPool.isEmpty())
                continue;
            Set<Map.Entry<byte[], byte[]>> entries = mapTimeSlotWaitTimeOutPool.entrySet();
            for (Map.Entry<byte[], byte[]> entry : entries) {
                SingleNumberModeShareCustomerItem customerItem = GenericitySerializeUtil.unserialize(entry.getValue());
                // 放回客户共享池
                if (!customerItem.getInvalid()) {
                    addCustomerToSharePool(customerItem);
                }

                removeWaitResultCustome(customerItem.getUserId(), customerItem.getBizId(), customerItem.getImportBatchId(), customerItem.getCustomerId());

                removeWaitStopCustomer( customerItem.getBizId(), customerItem.getShareBatchId(), customerItem.getImportBatchId(),
                        customerItem.getCustomerId());
            }
        }
    }


}

