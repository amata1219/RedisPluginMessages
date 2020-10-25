package amata1219.redis.plugin.messages.spigot;

import amata1219.redis.plugin.messages.common.RedisMessageForwarder;
import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import amata1219.redis.plugin.messages.common.RedisPublisher;
import amata1219.redis.plugin.messages.common.io.ByteIOStreams;
import amata1219.redis.plugin.messages.common.registry.ChannelRegistry;
import amata1219.redis.plugin.messages.common.registry.SubscriberRegistry;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;

public class RedisPluginMessages extends JavaPlugin implements RedisPluginMessagesAPI {

    private static RedisPluginMessages instance;

    private JedisPool pool;
    private final SubscriberRegistry subscriberRegistry = new SubscriberRegistry();
    private ChannelRegistry channelRegistry;
    private RedisPublisher publisher;
    private final ArrayList<Jedis> lentJedisList = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        String uniqueInstanceName = getConfig().getString("unique-name-of-instance");

        ByteIOStreams.initialize(uniqueInstanceName);

        ConfigurationSection section = getConfig().getConfigurationSection("redis-server");
        String password = section.getString("password");
        pool = createJedisPool(section.getString("host"), section.getInt("port"), password.isEmpty() ? null : password);

        RedisMessageForwarder forwarder = new RedisMessageForwarder(subscriberRegistry, uniqueInstanceName);
        channelRegistry = new ChannelRegistry(borrowJedis(), forwarder);
        publisher = new RedisPublisher(borrowJedis(), channelRegistry, uniqueInstanceName);
    }

    private JedisPool createJedisPool(String host, int port, String password) {
        return new JedisPool(new GenericObjectPoolConfig<>(), host, port, 2000, password);
    }

    private Jedis borrowJedis() {
        Jedis jedis = pool.getResource();
        lentJedisList.add(jedis);
        return jedis;
    }

    @Override
    public void onDisable() {
        for (Jedis jedis : lentJedisList) jedis.close();
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
