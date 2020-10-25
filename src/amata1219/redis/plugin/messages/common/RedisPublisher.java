package amata1219.redis.plugin.messages.common;

import amata1219.redis.plugin.messages.common.registry.ChannelRegistry;
import com.google.common.io.ByteArrayDataOutput;
import redis.clients.jedis.Jedis;

public class RedisPublisher {

    private final Jedis jedis;
    private final ChannelRegistry registry;

    public RedisPublisher(Jedis jedis, ChannelRegistry registry, String hostInstanceName) {
        this.jedis = jedis;
        this.registry = registry;
    }

    public void sendRedisMessage(String channel, ByteArrayDataOutput messages) {
        jedis.publish(registry.toBytes(channel), messages.toByteArray());
    }

}
