package hiapp.modules.dm.hidialermode.bo;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.redismanager.JedisUtils;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import hiapp.modules.dm.util.RedisComparator;
import hiapp.modules.dmmanager.data.DMBizMangeShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * M2 Hidialer自动外呼
 * Hidialer 抽取数据，客户信息不需要按照共享批次分类，由于不存在访问权限问题
 */

@Component
public class HidialerModeCustomerSharePool {

    @Autowired
    private DMBizMangeShare dmBizMangeShare;
    //注入连接池
    @Autowired
    private JedisUtils jedisUtils;

    // 客户共享池
    // BizId <==> PriorityBlockingQueue<HidialerModeCustomer>
    Map<Integer, PriorityBlockingQueue<HidialerModeCustomer>> mapPreseCustomerSharePool;
    Map<Integer, PriorityBlockingQueue<HidialerModeCustomer>> mapCustomerSharePool;

    // 用于处理共享停止的客户池，共享批次维度，用于标注已经停止共享的客户
    // BizId + ShareBatchId <==> {BizId + ImportId + CustomerId <==> MultiNumberCustomer}
    Map<String, Map<String, HidialerModeCustomer>> mapShareBatchWaitStopCustomerPool;

    //set集合用于清除redis中的数据
    Set<byte[]> hidialerSet;

    private Jedis hidailerJedis;

    public HidialerModeCustomerSharePool() {
        hidialerSet = new HashSet<>();
    }

    public void clear() {
        hidailerJedis = jedisUtils.getJedis();
        //先删除redis中的数据
        for (byte[] bytes : hidialerSet) {
            hidailerJedis.del(bytes);
        }
        hidialerSet.clear();
        jedisUtils.close(hidailerJedis);
    }

    //已改,,,
    public HidialerModeCustomer extractCustomer(String userId, Integer bizId) {
        Date now = new Date();
        HidialerModeCustomer shareDataItem = null;
        byte[] priSerialize = GenericitySerializeUtil.serialize("hidialerMapPreseCustomerSharePool" + bizId);
        try {
            hidailerJedis = jedisUtils.getJedis();
            PriorityBlockingQueue<HidialerModeCustomer> oneBizPresetCustomerPool = GenericitySerializeUtil.unserialize(hidailerJedis.get(priSerialize));
            while (!oneBizPresetCustomerPool.isEmpty()) {
                shareDataItem = oneBizPresetCustomerPool.peek();
                if (null == shareDataItem)
                    break;

                if (shareDataItem.getInvalid()) {
                    oneBizPresetCustomerPool.poll();  // 扔掉已经停止共享批次的客户
                    hidailerJedis.set(priSerialize, GenericitySerializeUtil.serialize(oneBizPresetCustomerPool));
                    removeFromShareBatchStopWaitPool(shareDataItem);
                    continue;
                }

                if (null != shareDataItem && shareDataItem.getNextDialTime().before(now)) {
                    shareDataItem = oneBizPresetCustomerPool.poll();
                    hidailerJedis.set(priSerialize, GenericitySerializeUtil.serialize(oneBizPresetCustomerPool));
                    removeFromShareBatchStopWaitPool(shareDataItem);
                    return shareDataItem;
                }

                break;   //NOTE: 取不到合适的，就跳出
            }

            PriorityBlockingQueue<HidialerModeCustomer> oneBizCustomerPoolRedis = GenericitySerializeUtil.unserialize(hidailerJedis.get(GenericitySerializeUtil.serialize("hidialerMapCustomerSharePool" + bizId)));
            while (null != oneBizCustomerPoolRedis && oneBizCustomerPoolRedis != null) {
                shareDataItem = oneBizCustomerPoolRedis.poll();
                hidailerJedis.set(GenericitySerializeUtil.serialize("hidialerMapCustomerSharePool" + bizId), GenericitySerializeUtil.serialize(oneBizCustomerPoolRedis));
                if (null == shareDataItem)
                    break;

                removeFromShareBatchStopWaitPool(shareDataItem);

                if (shareDataItem.getInvalid())
                    continue;

                return shareDataItem;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(hidailerJedis);
        }

        return shareDataItem;
    }

    /*
    public void add(MultiNumberCustomer customer) {
        PriorityBlockingQueue<MultiNumberCustomer> queue;
        if (MultiNumberPredictStateEnum.PRESET_DIAL.equals(customer.getState())
            || MultiNumberPredictStateEnum.WAIT_REDIAL.equals(customer.getState()) ) {
            queue = mapPreseCustomerSharePool.get(customer.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberCustomer>(1, nextDialTimeComparator);
                mapPreseCustomerSharePool.put(customer.getShareBatchId(), queue);
            }

        } else {
            queue = mapCustomerSharePool.get(customer.getShareBatchId());
            if (null == queue) {
                queue = new PriorityBlockingQueue<MultiNumberCustomer>(1, shareBatchBeginTimeComparator);
                mapCustomerSharePool.put(customer.getShareBatchId(), queue);
            }
        }

        queue.put(customer);
    }
    */
    //已改,,,
    public void add(HidialerModeCustomer customer) {
        PriorityBlockingQueue<HidialerModeCustomer> queue;
        try {
            hidailerJedis = jedisUtils.getJedis();
            if (HidialerModeCustomerStateEnum.WAIT_REDIAL.equals(customer.getState())) {
                queue = GenericitySerializeUtil.unserialize(hidailerJedis.get(GenericitySerializeUtil.serialize(customer.getMapPreseCustomerSharePoolId())));
                if (null == queue || queue.isEmpty()) {
                    queue = new PriorityBlockingQueue<HidialerModeCustomer>(1, nextDialTimeComparator);
                    //存入set集合，用于删除
                    hidialerSet.add(GenericitySerializeUtil.serialize(customer.getMapPreseCustomerSharePoolId()));
                }
                queue.put(customer);
                hidailerJedis.set(GenericitySerializeUtil.serialize(customer.getMapPreseCustomerSharePoolId()), GenericitySerializeUtil.serialize(queue));
            } else {
                queue = GenericitySerializeUtil.unserialize(hidailerJedis.get(GenericitySerializeUtil.serialize(customer.getMapCustomerSharePoolId())));
                if (null == queue || queue.isEmpty()) {
                    queue = new PriorityBlockingQueue<HidialerModeCustomer>(1, shareBatchBeginTimeComparator);
                    //存入set集合，用于删除
                    hidialerSet.add(GenericitySerializeUtil.serialize(customer.getMapPreseCustomerSharePoolId()));
                }
                queue.put(customer);
                hidailerJedis.set(GenericitySerializeUtil.serialize(customer.getMapCustomerSharePoolId()), GenericitySerializeUtil.serialize(queue));
            }

            hidailerJedis.hset(GenericitySerializeUtil.serialize(customer.getShareToken()),
                    GenericitySerializeUtil.serialize(customer.getCustomerToken()), GenericitySerializeUtil.serialize(customer));

            //存入set集合，用于删除
            hidialerSet.add(GenericitySerializeUtil.serialize(customer.getShareToken()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(hidailerJedis);
        }
    }

    /**
     * 仅标注已经停止共享，由于没办法直接从PriorityBlockingQueue里面移除。
     * 已改，在redis中直接删除
     *
     * @param shareBatchIds
     */
    public void stopShareBatch(int bizId, List<String> shareBatchIds) {
        try {
            hidailerJedis = jedisUtils.getJedis();
            for (String shareBatchId : shareBatchIds) {
                hidailerJedis.hdel(GenericitySerializeUtil.serialize(bizId + shareBatchId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(hidailerJedis);
        }
    }

    /*
    public void stopShareBatch(List<String> shareBatchIds) {
        removeFromCustomerSharePool(shareBatchIds, mapPreseCustomerSharePool);
        removeFromCustomerSharePool(shareBatchIds, mapCustomerSharePool);
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
    public List<HidialerModeCustomer> cancelShare(int bizId, List<CustomerBasic> customerBasicList) {
        List<HidialerModeCustomer> customerList = new ArrayList<HidialerModeCustomer>();
        try {
            hidailerJedis = jedisUtils.getJedis();
            for (CustomerBasic customerBasic : customerBasicList) {
                Map<byte[], byte[]> oneShareBatchCustomerPoolRedis = hidailerJedis.hgetAll(GenericitySerializeUtil.serialize(customerBasic.getSourceToken()));

                if (oneShareBatchCustomerPoolRedis.isEmpty())
                    continue;

                HidialerModeCustomer customer = GenericitySerializeUtil.unserialize(oneShareBatchCustomerPoolRedis.get(GenericitySerializeUtil.serialize(customerBasic.getCustomerToken())));
                if (null == customer)
                    continue;

                customer.setInvalid(true);
                oneShareBatchCustomerPoolRedis.put(GenericitySerializeUtil.serialize(customerBasic.getCustomerToken()), GenericitySerializeUtil.serialize(customer));
                hidailerJedis.hmset(GenericitySerializeUtil.serialize(customerBasic.getSourceToken()), oneShareBatchCustomerPoolRedis);
                customerList.add(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(hidailerJedis);
        }

        return customerList;
    }

    //////////////////////////////////////////////////////////
    //已改,,,
    private void removeFromShareBatchStopWaitPool(HidialerModeCustomer customer) {
        try {
            hidailerJedis = jedisUtils.getJedis();
            Map<byte[], byte[]> oneShareBatchPoolRedis = hidailerJedis.hgetAll(GenericitySerializeUtil.serialize(customer.getShareToken()));
            if (oneShareBatchPoolRedis.isEmpty()) {
                return;
            }
            //在redis中移除
            hidailerJedis.hdel(GenericitySerializeUtil.serialize(customer.getShareToken()), GenericitySerializeUtil.serialize(customer.getCustomerToken()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisUtils.close(hidailerJedis);
        }
    }

    //匿名Comparator实现
    private static RedisComparator<HidialerModeCustomer> nextDialTimeComparator = new RedisComparator<HidialerModeCustomer>() {

        @Override
        public int compare(HidialerModeCustomer c1, HidialerModeCustomer c2) {
            if (c1 != null && c2 != null && c1.getNextDialTime() != null && c2.getNextDialTime() != null)
                return (c1.getNextDialTime().before(c2.getNextDialTime())) ? 1 : -1;
            return 0;
        }
    };

    //匿名Comparator实现
    private static RedisComparator<HidialerModeCustomer> shareBatchBeginTimeComparator = new RedisComparator<HidialerModeCustomer>() {

        @Override
        public int compare(HidialerModeCustomer c1, HidialerModeCustomer c2) {
            if (c1 != null && c2 != null && c1.getShareBatchStartTime() != null && c2.getShareBatchStartTime() != null)
                return (c1.getShareBatchStartTime().before(c2.getShareBatchStartTime())) ? 1 : -1;
            return 0;
        }
    };

}
