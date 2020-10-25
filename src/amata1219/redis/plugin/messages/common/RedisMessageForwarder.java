package amata1219.redis.plugin.messages.common;

import amata1219.redis.plugin.messages.common.channel.ChannelHashing;
import amata1219.redis.plugin.messages.common.io.ByteIOStreams;
import amata1219.redis.plugin.messages.common.registry.SubscriberRegistry;
import com.google.common.io.ByteArrayDataInput;
import redis.clients.jedis.BinaryJedisPubSub;

import java.util.ArrayList;

public class RedisMessageForwarder extends BinaryJedisPubSub {

    private final SubscriberRegistry registry;
    private final String hostInstanceName;

    public RedisMessageForwarder(SubscriberRegistry registry, String hostInstanceName) {
        this.registry = registry;
        this.hostInstanceName = hostInstanceName;
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        ByteArrayDataInput in = ByteIOStreams.newDataInput(message);
        String sourceServerName = in.readUTF();
        if (sourceServerName.equals(hostInstanceName)) return;
        ArrayList<RedisSubscriber> subscribers = registry.getSubscribers(ChannelHashing.hash(channel));
        for (RedisSubscriber subscriber : subscribers) subscriber.onRedisMessageReceived(sourceServerName, in);
    }

}
