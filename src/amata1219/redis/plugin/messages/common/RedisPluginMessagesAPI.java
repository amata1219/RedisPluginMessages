package amata1219.redis.plugin.messages.common;

import amata1219.redis.plugin.messages.common.registry.ChannelRegistry;
import amata1219.redis.plugin.messages.common.registry.SubscriberRegistry;

public interface RedisPluginMessagesAPI {

    String uniqueInstanceName();

    ChannelRegistry channelRegistry();

    SubscriberRegistry subscriberRegistry();

    RedisPublisher publisher();

}
