package amata1219.redis.plugin.messages.common.registry;

import amata1219.redis.plugin.messages.common.RedisSubscriber;
import amata1219.redis.plugin.messages.common.channel.ChannelHashing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SubscriberRegistry {

    private final HashMap<Integer, List<RedisSubscriber>> SUBSCRIBERS = new HashMap<>();

    public void register(String channel, RedisSubscriber subscriber) {
        Integer key = ChannelHashing.hash(channel);
        List<RedisSubscriber> list = SUBSCRIBERS.computeIfAbsent(key, v -> new ArrayList<>());
        list.add(subscriber);
    }

    public List<RedisSubscriber> subscribers(Integer channelHashcode) {
        return SUBSCRIBERS.getOrDefault(channelHashcode, Collections.emptyList());
    }

}
