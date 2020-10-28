package amata1219.redis.plugin.messages.bungee;

import amata1219.redis.plugin.messages.common.Redis;
import amata1219.redis.plugin.messages.common.forwarder.RedisMessageForwarder;
import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import amata1219.redis.plugin.messages.common.RedisPublisher;
import amata1219.redis.plugin.messages.common.io.ByteIO;
import amata1219.redis.plugin.messages.common.registry.ChannelRegistry;
import amata1219.redis.plugin.messages.common.registry.SubscriberRegistry;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class RedisPluginMessages extends Plugin implements RedisPluginMessagesAPI {

    private static RedisPluginMessages instance;

    private Redis redis;
    private final SubscriberRegistry subscriberRegistry = new SubscriberRegistry();
    private ChannelRegistry channelRegistry;
    private RedisPublisher publisher;

    @Override
    public void onEnable() {
        instance = this;

        FileConfiguration fileConfiguration = new FileConfiguration("config.yml");
        fileConfiguration.createFileIntoDirectory();

        Configuration config = fileConfiguration.config();

        String uniqueInstanceName = config.getString("unique-name-of-instance");

        ByteIO.initialize(uniqueInstanceName);

        Configuration section = config.getSection("redis-server");
        String password = section.getString("password");
        redis = new Redis(section.getString("host"), section.getInt("port"), password.isEmpty() ? null : password);

        RedisMessageForwarder forwarder = new RedisMessageForwarder(subscriberRegistry, uniqueInstanceName);
        channelRegistry = new ChannelRegistry(redis.createInstance(), forwarder);
        publisher = new RedisPublisher(redis.createInstance(), channelRegistry, uniqueInstanceName);
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
