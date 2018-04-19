package hiapp.modules.redistest;

import hiapp.modules.dm.multinumberredialmode.bo.MultiNumberRedialCustomer;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import hiapp.modules.dm.util.RedisComparator;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Created by shtou on 2018/4/16.
 */
public class RedisTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        //jedis.set("a123" + "a", "1");
        jedis.set(GenericitySerializeUtil.serialize("a1"), GenericitySerializeUtil.serialize("a"));
        jedis.set(GenericitySerializeUtil.serialize("a2"), GenericitySerializeUtil.serialize("a2"));
        //jedis.set("a2" + "a", "2");
        //jedis.set("a5" + "a", "3");

        /*jedis.hset(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool"+1),
        GenericitySerializeUtil.serialize("b"),GenericitySerializeUtil.serialize("c"));
        jedis.hset(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool"+2),
        GenericitySerializeUtil.serialize("c"),GenericitySerializeUtil.serialize("d"));*/
        Set<byte[]> keys = jedis.keys(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool*"));

        for (byte[] key : keys) {
            String s = GenericitySerializeUtil.unserialize(key);
            System.out.println(s);
            s = s.substring(s.lastIndexOf('l') + 1);
            System.out.println(s);
        }
        /*PriorityBlockingQueue<MultiNumberRedialCustomer> queue = new PriorityBlockingQueue<MultiNumberRedialCustomer>(1, nextDialTimeComparator);
        System.out.println(queue);
        //jedis.set("a".getBytes(), GenericitySerializeUtil.serialize(queue));
        queue = GenericitySerializeUtil.unserialize(jedis.get("a".getBytes()));
        System.out.println(queue);
        System.out.println(queue);*/
        /*Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        System.out.println(set);
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            if ("a".equals(next))
            iterator.remove();
        }
        System.out.println(set);*/
        //jedis.hset("a", "c", "c");
        //jedis.hdel("a", "c");
        //jedis.hset("a", "b", "c");
        //System.out.println(jedis.del("a"));

        //System.out.println(jedis.hexists("a", "b"));
        //jedis.hset(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool"+1),
                //GenericitySerializeUtil.serialize("b"),GenericitySerializeUtil.serialize("c"));
        //jedis.hset(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool"+2),
                //GenericitySerializeUtil.serialize("c"),GenericitySerializeUtil.serialize("d"));
        /*Set<byte[]> keys = jedis.keys(GenericitySerializeUtil.serialize("mapTimeOutWaitPhoneConnectCustomerPool*"));

        for (byte[] key : keys) {
            String s = GenericitySerializeUtil.unserialize(key);
            s = s.substring(s.lastIndexOf('l') + 1);
            System.out.println(s);
        }*/
        //Long a = jedis.hdel("c");


    }
    //匿名Comparator实现
    private static RedisComparator<MultiNumberRedialCustomer> nextDialTimeComparator = new RedisComparator<MultiNumberRedialCustomer>() {

        @Override
        public int compare(MultiNumberRedialCustomer c1, MultiNumberRedialCustomer c2) {
            return (c1.getCurPresetDialTime().before(c2.getCurPresetDialTime())) ? 1 : -1;
        }
    };
}
