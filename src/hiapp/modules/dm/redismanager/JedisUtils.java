package hiapp.modules.dm.redismanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

/**
 * Created by shizhenshuang on 2018/6/27.
 */
@Component
public class JedisUtils {

    @Autowired
    private JedisSentinelPool jedisPool;

    /**
     * 获取Jedis对象
     *
     * @return Jedis对象
     */
    public synchronized Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public void close(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
