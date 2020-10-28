package amata1219.redis.plugin.messages.spigot;

import amata1219.redis.plugin.messages.common.forwarder.RedisMessageForwarder;
import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import amata1219.redis.plugin.messages.common.RedisPublisher;
import amata1219.redis.plugin.messages.common.io.ByteIO;
import amata1219.redis.plugin.messages.common.Redis;
import amata1219.redis.plugin.messages.common.registry.ChannelRegistry;
import amata1219.redis.plugin.messages.common.registry.SubscriberRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPluginMessages extends JavaPlugin implements RedisPluginMessagesAPI {

    private static RedisPluginMessages instance;

    private Redis redis;
    private final SubscriberRegistry subscriberRegistry = new SubscriberRegistry();
    private ChannelRegistry channelRegistry;
    private RedisPublisher publisher;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        String uniqueInstanceName = getConfig().getString("unique-name-of-instance");

        ByteIO.initialize(uniqueInstanceName);

        ConfigurationSection section = getConfig().getConfigurationSection("redis-server");
        String password = section.getString("password");
        redis = new Redis(section.getString("host"), section.getInt("port"), password.isEmpty() ? null : password);

        RedisMessageForwarder forwarder = new RedisMessageForwarder(subscriberRegistry, uniqueInstanceName);
        channelRegistry = new ChannelRegistry(redis.createInstance(), forwarder);
        publisher = new RedisPublisher(redis.createInstance(), channelRegistry, uniqueInstanceName);
    }

    private JedisPool createJedisPool(String host, int port, String password) {
        return new JedisPool(new JedisPoolConfig(), host, port, 60000, password);
    }

    @Override
    public void onDisable() {
        redis.closeAllInstances();
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
