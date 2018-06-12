package hiapp.modules.dm.multinumberredialmode.bo;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import hiapp.modules.dm.util.RedisComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * M4 多号码重拨外呼
 * 客户信息需要按照共享批次分类，由于存在访问权限问题
 */
@Service
public class MultiNumberRedialCustomerSharePool implements Serializable{

    @Autowired
    transient private JedisSentinelPool jedisPool;

    transient private Jedis redisRedialMultiNumber;
    // 客户共享池
    //BizId + ShareBatchID <==> PriorityBlockingQueue<MultiNumberRedialCustomer>
  /*  Map<String, PriorityBlockingQueue<MultiNumberRedialCustomer>> multiNumberRedialShareMapPreseCustomerSharePool;
    Map<String, PriorityBlockingQueue<MultiNumberRedialCustomer>> multiNumberRedialShareMapCustomerSharePool;

    // 用于处理共享停止/取消的客户池，共享批次维度，用于标注已经停止/取消共享的客户
    // BizId + ShareBatchId <==> {BizId + ImportId + CustomerId <==> MultiNumberRedialCustomer}
    Map<String, Map<String, MultiNumberRedialCustomer>> multiNumberRedialShareMapShareBatchWaitStopCustomerPool;*/


    int bizId = 0;

    public void initialize() {
        redisRedialMultiNumber = jedisPool.getResource();
    }
    public MultiNumberRedialCustomerSharePool() {
    }
    public MultiNumberRedialCustomerSharePool(int bizId) {

        this.bizId = bizId;

        /*multiNumberRedialShareMapPreseCustomerSharePool = new HashMap<String, PriorityBlockingQueue<MultiNumberRedialCustomer>>();
        multiNumberRedialShareMapCustomerSharePool = new HashMap<String, PriorityBlockingQueue<MultiNumberRedialCustomer>>();

        //multiNumberRedialShareMapPreseCustomerSharePool = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, nextDialTimeComparator);
        //multiNumberRedialShareMapCustomerSharePool = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, shareBatchBeginTimeComparator);

        multiNumberRedialShareMapShareBatchWaitStopCustomerPool = new HashMap<String, Map<String, MultiNumberRedialCustomer>>();*/
    }
    //已改
    public MultiNumberRedialCustomer extractCustomer(String userId, List<String> shareBatchIdList, Jedis redisMultiNumberRedial) {
        redisRedialMultiNumber = redisMultiNumberRedial;
        Date now = new Date();
        MultiNumberRedialCustomer shareDataItem = null;

        shareDataItem = retrievePresetCustomer(shareBatchIdList, "multiNumberRedialShareMapPreseCustomerSharePool");
        if (null == shareDataItem)
            shareDataItem = retrieveGeneralCustomer(shareBatchIdList, "multiNumberRedialShareMapCustomerSharePool");


        if (null != shareDataItem)
            removeFromShareBatchStopWaitPool(shareDataItem);

        return shareDataItem;
    }


    /**
     * 注意：MultiNumberRedialStateEnum.WAIT_NEXT_DAY_DIAL 放入非预约队列
     *
     */
    //已改
    public void add(MultiNumberRedialCustomer customer, Jedis redisMultiNumberRedial) {
        redisRedialMultiNumber = redisMultiNumberRedial;
        PriorityBlockingQueue<MultiNumberRedialCustomer> queue;
        if (MultiNumberRedialStateEnum.PRESET_DIAL.equals(customer.getState())
            || MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL.equals(customer.getState())) {
            byte[] queueSerialize = GenericitySerializeUtil.
                    serialize("multiNumberRedialShareMapPreseCustomerSharePool" + customer.getShareToken());
            queue = GenericitySerializeUtil.unserialize(redisRedialMultiNumber.get(queueSerialize));
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, nextDialTimeComparator);
            }
            queue.put(customer);
            redisRedialMultiNumber.set(queueSerialize, GenericitySerializeUtil.serialize(queue));

        } else {
            byte[] queueSerialize = GenericitySerializeUtil.
                    serialize("multiNumberRedialShareMapCustomerSharePool" + customer.getShareToken());
            queue = GenericitySerializeUtil.unserialize(redisRedialMultiNumber.get(queueSerialize));
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, shareBatchBeginTimeComparator);
            }
            queue.put(customer);
            redisRedialMultiNumber.set(queueSerialize, GenericitySerializeUtil.serialize(queue));
        }


        // step 2 : 放入 等停止/取消的客户池
        redisRedialMultiNumber.hset(GenericitySerializeUtil.serialize("multiNumberRedialShareMapShareBatchWaitStopCustomerPool" + customer.getShareToken()),
                GenericitySerializeUtil.serialize(customer.getCustomerToken()),
                GenericitySerializeUtil.serialize(customer));
    }

    /**
     * 仅标注已经停止共享，由于没办法直接从PriorityBlockingQueue里面移除。
     * @param shareBatchIds
     */
    /*public void stopShareBatch(List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            Map<String, MultiNumberRedialCustomer> mapWaitStopPool;
            mapWaitStopPool = multiNumberRedialShareMapShareBatchWaitStopCustomerPool.remove(shareBatchId);
            for (MultiNumberRedialCustomer item : mapWaitStopPool.values()) {
                item.setInvalid(true);
            }
        }
    }*/

    //已改
    public void stopShareBatch(List<String> shareBatchIds) {
        for (String shareBatchId : shareBatchIds) {
            redisRedialMultiNumber.del(GenericitySerializeUtil.serialize("multiNumberRedialShareMapPreseCustomerSharePool" + bizId + shareBatchId));
            redisRedialMultiNumber.del(GenericitySerializeUtil.serialize("multiNumberRedialShareMapCustomerSharePool" + bizId + shareBatchId));
            redisRedialMultiNumber.del(GenericitySerializeUtil.serialize("multiNumberRedialShareMapShareBatchWaitStopCustomerPool" + bizId + shareBatchId));
        }
    }
    //已改
    public List<MultiNumberRedialCustomer> cancelShare(List<CustomerBasic> customerBasicList) {
        List<MultiNumberRedialCustomer> customerList = new ArrayList<MultiNumberRedialCustomer>();
        for (CustomerBasic customerBasic : customerBasicList) {
            byte[] mapSerialize = GenericitySerializeUtil.serialize("multiNumberRedialShareMapShareBatchWaitStopCustomerPool" + customerBasic.getSourceToken());
            byte[] fieldSerialize = GenericitySerializeUtil.serialize(customerBasic.getCustomerToken());
            MultiNumberRedialCustomer customer = GenericitySerializeUtil.unserialize(redisRedialMultiNumber.hget(mapSerialize, fieldSerialize));
            if (null == customer)
                continue;

            customer.setInvalid(true);
            //存回去
            redisRedialMultiNumber.hset(mapSerialize, fieldSerialize, GenericitySerializeUtil.serialize(customer));
            customerList.add(customer);
        }

        return customerList;
    }

    ///////////////////////////////////////////////////////////////////
    //已改
    private MultiNumberRedialCustomer retrievePresetCustomer(List<String> shareBatchIdList,
                          String preseCustomerSharePool) {

        Date now = new Date();

        MultiNumberRedialCustomer shareDataItem = null;
        for (String shareBatchId : shareBatchIdList) {
            byte[] queueSerialize = GenericitySerializeUtil.serialize(preseCustomerSharePool + bizId + shareBatchId);
            PriorityBlockingQueue<MultiNumberRedialCustomer> presetCustomerPool = GenericitySerializeUtil.unserialize(redisRedialMultiNumber.
                    get(queueSerialize));

            if (null == presetCustomerPool || presetCustomerPool.isEmpty()) {
                redisRedialMultiNumber.del(queueSerialize);
                continue;
            }

            shareDataItem = presetCustomerPool.peek();

            if (shareDataItem.getInvalid()) {
                presetCustomerPool.poll();  // 丢弃 作废的客户
                //丢弃完成之后再存回去
                redisRedialMultiNumber.set(queueSerialize, GenericitySerializeUtil.serialize(preseCustomerSharePool));
                continue;
            }

            if (shareDataItem.getCurPresetDialTime().before(now)) {
                shareDataItem = presetCustomerPool.poll();
                redisRedialMultiNumber.set(queueSerialize, GenericitySerializeUtil.serialize(preseCustomerSharePool));
                return shareDataItem;
            }
        }

        return null;
    }
    //已改
    private MultiNumberRedialCustomer retrieveGeneralCustomer(List<String> shareBatchIdList,
                      String customerSharePool) {
        MultiNumberRedialCustomer shareDataItem = null;
        for (String shareBatchId : shareBatchIdList) {
            byte[] queueSerialize = GenericitySerializeUtil.serialize(customerSharePool + bizId + shareBatchId);
            PriorityBlockingQueue<MultiNumberRedialCustomer> customerPool = GenericitySerializeUtil.unserialize(redisRedialMultiNumber.
                    get(queueSerialize));
            if (null == customerPool || customerPool.isEmpty()) {
                redisRedialMultiNumber.del(queueSerialize);
                continue;
            }

            shareDataItem = customerPool.poll();
            //丢弃完成之后再存回去
            redisRedialMultiNumber.set(queueSerialize, GenericitySerializeUtil.serialize(customerPool));
            if (shareDataItem.getInvalid()) {
                continue;   // 丢弃 作废的客户
            }

            return shareDataItem;
        }

        return null;
    }
    //已改
    private void removeFromShareBatchStopWaitPool(MultiNumberRedialCustomer customer) {
        redisRedialMultiNumber.hdel(GenericitySerializeUtil.serialize("multiNumberRedialShareMapShareBatchWaitStopCustomerPool" + customer.getShareToken()),
                GenericitySerializeUtil.serialize(customer.getCustomerToken()));
    }
    //已改
    //匿名Comparator实现
    private static RedisComparator<MultiNumberRedialCustomer> nextDialTimeComparator = new RedisComparator<MultiNumberRedialCustomer>() {

        @Override
        public int compare(MultiNumberRedialCustomer c1, MultiNumberRedialCustomer c2) {
            return (c1.getCurPresetDialTime().before(c2.getCurPresetDialTime())) ? 1 : -1;
        }
    };

    //匿名Comparator实现
    private static RedisComparator<MultiNumberRedialCustomer> shareBatchBeginTimeComparator = new RedisComparator<MultiNumberRedialCustomer>() {

        @Override
        public int compare(MultiNumberRedialCustomer c1, MultiNumberRedialCustomer c2) {
            return (c1.getShareBatchStartTime().before(c2.getShareBatchStartTime())) ? 1 : -1;
        }
    };

}
