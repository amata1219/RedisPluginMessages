package amata1219.redis.plugin.messages.common;

import amata1219.redis.plugin.messages.common.registry.ChannelRegistry;
import amata1219.redis.plugin.messages.common.registry.SubscriberRegistry;
import com.google.common.io.ByteArrayDataOutput;

public interface RedisPluginMessagesAPI {

    ChannelRegistry channelRegistry();

    default void registerIncomingChannel(String... channels) {
        for (String channel : channels) channelRegistry().registerIncomingChannel(channel);
    }

    default boolean isIncomingChannel(String channel) {
        return channelRegistry().isIncomingChannel(channel);
    }

    default void registerOutgoingChannel(String... channels) {
        for (String channel : channels) channelRegistry().registerOutgoingChannel(channel);
    }

    default boolean isOutgoingChannel(String channel) {
        return channelRegistry().isOutgoingChannel(channel);
    }

    SubscriberRegistry subscriberRegistry();

    default void registerSubscriber(String channel, RedisSubscriber subscriber) {
        subscriberRegistry().register(channel, subscriber);
    }

    RedisPublisher publisher();

    default void sendRedisMessage(String channel, ByteArrayDataOutput messages) {
        publisher().sendRedisMessage(channel, messages);
    }

}
