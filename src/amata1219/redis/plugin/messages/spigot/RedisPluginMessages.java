package amata1219.redis.plugin.messages.spigot;

import amata1219.redis.plugin.messages.common.RedisMessageForwarder;
import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import amata1219.redis.plugin.messages.common.RedisPublisher;
import amata1219.redis.plugin.messages.common.io.ByteIO;
import amata1219.redis.plugin.messages.common.Redis;
import amata1219.redis.plugin.messages.common.registry.ChannelRegistry;
import amata1219.redis.plugin.messages.common.registry.SubscriberRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

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
        channelRegistry = new ChannelRegistry(redis, forwarder);
        publisher = new RedisPublisher(redis.createInstance(), channelRegistry, uniqueInstanceName);
    }

    @Override
    public void onDisable() {
        channelRegistry.unregisterAllChannels();
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
