package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import hiapp.modules.dm.util.RedisComparator;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.beans.Transient;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * M6 多号码预测外呼
 * Hidialer 抽取数据，客户信息不需要按照共享批次分类，由于不存在访问权限问题
 */
@Service
public class CustomerSharePool implements Serializable {
    private static final long serialVersionUID = 1930058734521485334L;
   /* @Autowired
    private DMBizMangeShare dmBizMangeShare;*/

    // 客户共享池
    // ShareBatchID <==> PriorityBlockingQueue<MultiNumberCustomer>
    //Map<String, PriorityBlockingQueue<MultiNumberCustomer>> multiNumberMapPreseCustomerSharePool;
    //Map<String, PriorityBlockingQueue<MultiNumberCustomer>> multiNumberMapCustomerSharePool;

    /*PriorityBlockingQueue<MultiNumberCustomer> multiNumberMapPreseCustomerSharePool;
    PriorityBlockingQueue<MultiNumberCustomer> multiNumberMapCustomerSharePool;

    // 用于处理共享停止的客户池，共享批次维度，用于标注已经停止共享的客户
    // BizId + ShareBatchId <==> {BizId + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<String, Map<String, MultiNumberCustomer>> multiNumberMapShareBatchWaitStopCustomerPool;*/
    transient Jedis redisMultiNumber;
   /* @Autowired
    transient private JedisSentinelPool jedisSentinelPool;*/
    int bizId = 0;

    byte[] serializePrese = GenericitySerializeUtil.serialize("multiNumberMapPreseCustomerSharePool");
    byte[] serializeCustomer = GenericitySerializeUtil.serialize("multiNumberMapCustomerSharePool");
    byte[] serializeWaitMap = GenericitySerializeUtil.serialize("multiNumberMapShareBatchWaitStopCustomerPool");
    //用于注入redis
    public void initialize() {
        /*redisMultiNumber = jedisSentinelPool.getResource();
        //初始化
        redisMultiNumber.set(serializePrese, GenericitySerializeUtil.serialize(new PriorityBlockingQueue<MultiNumberCustomer>(1, nextDialTimeComparator)));
        redisMultiNumber.set(serializeCustomer, GenericitySerializeUtil.serialize(new PriorityBlockingQueue<MultiNumberCustomer>(1, shareBatchBeginTimeComparator)));*/
    }
    public CustomerSharePool(int bizId, Jedis redisMultiNumberPredict) {
        this.bizId = bizId;
        redisMultiNumber = redisMultiNumberPredict;
    }
    public CustomerSharePool() {

        //multiNumberMapPreseCustomerSharePool = new HashMap<String, PriorityBlockingQueue<MultiNumberCustomer>>();
        //multiNumberMapCustomerSharePool = new HashMap<String, PriorityBlockingQueue<MultiNumberCustomer>>();
/*
        multiNumberMapPreseCustomerSharePool = new PriorityBlockingQueue<MultiNumberCustomer>(1, nextDialTimeComparator);
        multiNumberMapCustomerSharePool = new PriorityBlockingQueue<MultiNumberCustomer>(1, shareBatchBeginTimeComparator);

        multiNumberMapShareBatchWaitStopCustomerPool = new HashMap<String, Map<String, MultiNumberCustomer>>();*/
    }
    //已改
    public MultiNumberCustomer extractCustomer(String userId, Jedis redisMultiNumberPredict) {
        redisMultiNumber = redisMultiNumberPredict;
        Date now = new Date();
        MultiNumberCustomer shareDataItem = peekNextValidCustomer(serializePrese);
        if (null != shareDataItem && shareDataItem.getCurPresetDialTime().before(now)) {
            //取出来
            PriorityBlockingQueue<MultiNumberCustomer> priorityBlockingQueue = GenericitySerializeUtil.unserialize(redisMultiNumber.get(serializePrese));
            shareDataItem = priorityBlockingQueue.poll();
            //存回去
            redisMultiNumber.set(serializePrese, GenericitySerializeUtil.serialize(priorityBlockingQueue));
            return shareDataItem;
        }

        return retrieveNextValideCustomer(serializeCustomer);
    }

    /*
    public void add(MultiNumberCustomer customer) {
        PriorityBlockingQueue<MultiNumberCustomer> queue;
        if (MultiNumberPredictStateEnum.PRESET_DIAL.equals(customer.getState())
            || MultiNumberPredictStateEnum.WAIT_REDIAL.equals(customer.getState()) ) {
            queue = multiNumberMapPreseCustomerSharePool.get(customer.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberCustomer>(1, nextDialTimeComparator);
                multiNumberMapPreseCustomerSharePool.put(customer.getShareBatchId(), queue);
            }

        } else {
            queue = multiNumberMapCustomerSharePool.get(customer.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberCustomer>(1, shareBatchBeginTimeComparator);
                multiNumberMapCustomerSharePool.put(customer.getShareBatchId(), queue);
            }
        }

        queue.put(customer);
    }
    */
    //已改
    public void add(MultiNumberCustomer customer, Jedis redisMultiNumberPredict) {
        redisMultiNumber = redisMultiNumberPredict;
        if (GenericitySerializeUtil.unserialize(redisMultiNumber.get(serializePrese)) == null){
            redisMultiNumber.set(serializePrese, GenericitySerializeUtil.serialize(new PriorityBlockingQueue<MultiNumberCustomer>(1, nextDialTimeComparator)));
        }
        if (GenericitySerializeUtil.unserialize(redisMultiNumber.get(serializeCustomer)) == null){
            redisMultiNumber.set(serializeCustomer, GenericitySerializeUtil.serialize(new PriorityBlockingQueue<MultiNumberCustomer>(1, shareBatchBeginTimeComparator)));
        }
        //初始化
        PriorityBlockingQueue<MultiNumberCustomer> queue;
        if (MultiNumberPredictStateEnum.PRESET_DIAL.equals(customer.getState())
            || MultiNumberPredictStateEnum.WAIT_REDIAL.equals(customer.getState()) ) {
            //先取出来
            queue = GenericitySerializeUtil.unserialize(redisMultiNumber.get(serializePrese));
            /*if (queue == null){
                queue = new PriorityBlockingQueue<MultiNumberCustomer>(1, nextDialTimeComparator);
            }*/
            queue.put(customer);
            //再存进去
            redisMultiNumber.set(serializePrese, GenericitySerializeUtil.serialize(queue));
        } else {
            //先取出来
            queue = GenericitySerializeUtil.unserialize(redisMultiNumber.get(serializeCustomer));
            queue.put(customer);
            //再存进去
            redisMultiNumber.set(serializeCustomer, GenericitySerializeUtil.serialize(queue));
        }

        redisMultiNumber.hset(GenericitySerializeUtil.serialize(serializeWaitMap),
                GenericitySerializeUtil.serialize(customer.getCustomerToken()),
                GenericitySerializeUtil.serialize(customer));
    }

    /**
     * 仅标注已经停止共享，由于没办法直接从PriorityBlockingQueue里面移除。
     * @param shareBatchIds
     */
    //已改
    public void stopShareBatch(List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {

            Map<byte[], byte[]> mapWaitStopPool = redisMultiNumber.hgetAll(GenericitySerializeUtil.serialize
                    ("multiNumberMapShareBatchWaitStopCustomerPool" + bizId + shareBatchId));
            mapWaitStopPool.remove(GenericitySerializeUtil.serialize(bizId + shareBatchId));
            Set<Map.Entry<byte[], byte[]>> entries = mapWaitStopPool.entrySet();
            for (Map.Entry<byte[], byte[]> entry : entries) {
                MultiNumberCustomer item = GenericitySerializeUtil.unserialize(entry.getValue());
                item.setInvalid(true);
                //存回去
                mapWaitStopPool.put(entry.getKey(), GenericitySerializeUtil.serialize(item));
            }
            redisMultiNumber.hmset(GenericitySerializeUtil.serialize
                    ("multiNumberMapShareBatchWaitStopCustomerPool" + bizId + shareBatchId), mapWaitStopPool);
        }
    }

    /*
    public void stopShareBatch(List<String> shareBatchIds) {
        removeFromCustomerSharePool(shareBatchIds, multiNumberMapPreseCustomerSharePool);
        removeFromCustomerSharePool(shareBatchIds, multiNumberMapCustomerSharePool);
    }

    private void removeFromCustomerSharePool(List<String> shareBatchIds,
                                             Map<String, PriorityBlockingQueue<MultiNumberCustomer>> shareBatchIdVsCustomerMap)  {
        PriorityBlockingQueue<MultiNumberCustomer> queue;
        if (null != shareBatchIdVsCustomerMap) {
            for (String shareBatchId : shareBatchIds) {
                queue = shareBatchIdVsCustomerMap.remove(shareBatchId);
            }
        }
    }*/
    //已改
    public List<MultiNumberCustomer> cancelShare(List<CustomerBasic> customerBasicList) {
        List<MultiNumberCustomer> customerList = new ArrayList<MultiNumberCustomer>();
        for (CustomerBasic customerBasic : customerBasicList) {
            byte[] mapSerialize = GenericitySerializeUtil.serialize(
                    "multiNumberMapShareBatchWaitStopCustomerPool" + customerBasic.getSourceToken());
            Map<byte[], byte[]> oneShareBatchCustomerPool = redisMultiNumber.hgetAll(mapSerialize);
            if (null == oneShareBatchCustomerPool || oneShareBatchCustomerPool.isEmpty()) {
                continue;
            }

            byte[] fieldSerialize = GenericitySerializeUtil.serialize(customerBasic.getCustomerToken());
            MultiNumberCustomer customer = GenericitySerializeUtil.unserialize(
                    oneShareBatchCustomerPool.remove(fieldSerialize));
            if (null == customer)
                continue;

            customer.setInvalid(true);
            redisMultiNumber.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(customer));
            customerList.add(customer);
        }

        return customerList;
    }


    //////////////////////////////////////////////////////////
    //已改
    private MultiNumberCustomer peekNextValidCustomer(byte[] customerPreseSharePool) {
        PriorityBlockingQueue<MultiNumberCustomer> customerSharePool = GenericitySerializeUtil.unserialize(redisMultiNumber.get(customerPreseSharePool));
        while (!customerSharePool.isEmpty()) {
            MultiNumberCustomer shareDataItem = customerSharePool.peek();
            if (shareDataItem.getInvalid()) {
                customerSharePool.poll();  // 丢弃 已经停止共享的客户
                //存回去
                redisMultiNumber.set(customerPreseSharePool, GenericitySerializeUtil.serialize(customerSharePool));
                continue;
            }

            return shareDataItem;
            //break;   // 第一个有效客户还未到拨打时间，就跳出
        }

        return null;
    }
    //已改
    private MultiNumberCustomer retrieveNextValideCustomer(byte[] customerSharePool) {
        PriorityBlockingQueue<MultiNumberCustomer> priorityBlockingQueue = GenericitySerializeUtil.unserialize(redisMultiNumber.get(customerSharePool));
        while (!priorityBlockingQueue.isEmpty()) {
            MultiNumberCustomer shareDataItem = priorityBlockingQueue.poll();
            //再存回去
            redisMultiNumber.set(customerSharePool, GenericitySerializeUtil.serialize(priorityBlockingQueue));
            if (!shareDataItem.getInvalid())
                return shareDataItem;
        }

        return null;
    }
    //已改
    //匿名Comparator实现
    private static RedisComparator<MultiNumberCustomer> nextDialTimeComparator = new RedisComparator<MultiNumberCustomer>() {

        @Override
        public int compare(MultiNumberCustomer c1, MultiNumberCustomer c2) {
            if (c1 != null && c2 != null && c1.getCurPresetDialTime() != null && c2.getCurPresetDialTime() != null){
                return (c1.getCurPresetDialTime().before(c2.getCurPresetDialTime())) ? 1 : -1;
            }
            return 0;
        }
    };

    //匿名Comparator实现
    private static RedisComparator<MultiNumberCustomer> shareBatchBeginTimeComparator = new RedisComparator<MultiNumberCustomer>() {

        @Override
        public int compare(MultiNumberCustomer c1, MultiNumberCustomer c2) {
            if (c1 != null && c2 != null && c1.getShareBatchStartTime() != null && c2.getShareBatchStartTime() != null){
                return (c1.getShareBatchStartTime().before(c2.getShareBatchStartTime())) ? 1 : -1;
            }
            return 0;
        }
    };


}
