package amata1219.redis.plugin.messages.common.connection;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisConnection {

    private final JedisPool pool;
    private Jedis jedis;

    public JedisConnection(JedisPool pool) {
        this.pool = pool;
        this.jedis = pool.getResource();
    }

    public Jedis jedis() {
        if (!jedis.isConnected()) {
            jedis.close();
            jedis = pool.getResource();
        }
        return jedis;
    }


}
