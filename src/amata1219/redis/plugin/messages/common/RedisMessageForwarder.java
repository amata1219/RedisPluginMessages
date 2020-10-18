package amata1219.redis.plugin.messages.common;

import amata1219.redis.plugin.messages.common.channel.ChannelHashing;
import amata1219.redis.plugin.messages.common.io.ByteIOStreams;
import amata1219.redis.plugin.messages.common.registry.SubscriberRegistry;
import com.google.common.io.ByteArrayDataInput;
import redis.clients.jedis.BinaryJedisPubSub;

import java.util.ArrayList;

public class RedisMessageForwarder extends BinaryJedisPubSub {

    private final SubscriberRegistry registry;
    private final String hostServerName;

    public RedisMessageForwarder(SubscriberRegistry registry, String hostServerName) {
        this.registry = registry;
        this.hostServerName = hostServerName;
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        ByteArrayDataInput in = ByteIOStreams.newDataInput(message);
        String sourceServerName = in.readUTF();
        if (sourceServerName.equals(hostServerName)) return;
        ArrayList<RedisSubscriber> subscribers = registry.getSubscribers(ChannelHashing.hash(channel));
        for (RedisSubscriber subscriber : subscribers) subscriber.onRedisMessageReceived(sourceServerName, in);
    }

}
