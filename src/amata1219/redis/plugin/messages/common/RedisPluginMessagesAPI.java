package amata1219.redis.plugin.messages.common;

import amata1219.redis.plugin.messages.common.registry.ChannelRegistry;
import amata1219.redis.plugin.messages.common.registry.SubscriberRegistry;
import com.google.common.io.ByteArrayDataOutput;

public interface RedisPluginMessagesAPI {

    ChannelRegistry channelRegistry();

    default void registerIncomingChannels(String... channels) {
        channelRegistry().registerIncomingChannels(channels);
    }

    default void unregisterIncomingChannels(String... channels) {
        channelRegistry().unregisterIncomingChannels(channels);
    }

    default boolean isIncomingChannel(String channel) {
        return channelRegistry().isIncomingChannel(channel);
    }

    default void registerOutgoingChannels(String... channels) {
        channelRegistry().registerOutgoingChannels(channels);
    }

    default void unregisterOutgoingChannels(String... channels) {
        channelRegistry().unregisterOutgoingChannels(channels);
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
