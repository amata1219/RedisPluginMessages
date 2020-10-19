package amata1219.redis.plugin.messages.bungee;

import amata1219.redis.plugin.messages.common.RedisMessageForwarder;
import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import amata1219.redis.plugin.messages.common.RedisPublisher;
import amata1219.redis.plugin.messages.common.registry.ChannelRegistry;
import amata1219.redis.plugin.messages.common.registry.SubscriberRegistry;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;

public class RedisPluginMessages extends Plugin implements RedisPluginMessagesAPI {

    private static RedisPluginMessages instance;

    private JedisPool pool;
    private final SubscriberRegistry subscriberRegistry = new SubscriberRegistry();
    private ChannelRegistry channelRegistry;
    private RedisPublisher publisher;
    private ArrayList<Jedis> lentJedises;

    @Override
    public void onEnable() {
        instance = this;

        FileConfiguration fileConfiguration = new FileConfiguration("config.yml");
        fileConfiguration.createFileInDirectory();
        Configuration config = fileConfiguration.config();

        String uniqueServerName = config.getString("unique-server-name");

        Configuration section = config.getSection("redis-server");
        String password = section.getString("password");
        pool = createJedisPool(section.getString("host"), section.getInt("port"), password.isEmpty() ? null : password);

        RedisMessageForwarder forwarder = new RedisMessageForwarder(subscriberRegistry, uniqueServerName);
        channelRegistry = new ChannelRegistry(borrowJedis(), forwarder);
        publisher = new RedisPublisher(borrowJedis(), channelRegistry, uniqueServerName);
    }

    private JedisPool createJedisPool(String host, int port, String password) {
        return new JedisPool(new GenericObjectPoolConfig<>(), host, port, 2000, password);
    }

    private Jedis borrowJedis() {
        Jedis jedis = pool.getResource();
        lentJedises.add(jedis);
        return jedis;
    }

    @Override
    public void onDisable() {
        for (Jedis jedis : lentJedises) jedis.close();
    }

    public static RedisPluginMessages instance() {
        return instance;
    }

    @Override
    public ChannelRegistry channelRegistry() {
        return channelRegistry;
    }

    @Override
    public SubscriberRegistry subscriberRegistry() {
        return subscriberRegistry;
    }

    @Override
    public RedisPublisher publisher() {
        return publisher;
    }

}
