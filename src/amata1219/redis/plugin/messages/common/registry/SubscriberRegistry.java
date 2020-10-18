package amata1219.redis.plugin.messages.common.registry;

import amata1219.redis.plugin.messages.common.RedisSubscriber;
import amata1219.redis.plugin.messages.common.channel.ChannelHashing;

import java.util.ArrayList;
import java.util.HashMap;

public class SubscriberRegistry {

    private final HashMap<Integer, ArrayList<RedisSubscriber>> SUBSCRIBERS = new HashMap<>();

    public void register(String channel, RedisSubscriber subscriber) {
        Integer key = ChannelHashing.hash(channel);
        ArrayList<RedisSubscriber> list = SUBSCRIBERS.computeIfAbsent(key, v -> new ArrayList<>());
        list.add(subscriber);
    }

    public ArrayList<RedisSubscriber> getSubscribers(Integer channelHashcode) {
        return SUBSCRIBERS.get(channelHashcode);
    }

}
