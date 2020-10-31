package amata1219.redis.plugin.messages.common.registry;

import amata1219.redis.plugin.messages.common.forwarder.RedisMessageForwarder;
import amata1219.redis.plugin.messages.common.channel.ChannelCodec;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class ChannelRegistry {

    private final Jedis jedis;
    private final RedisMessageForwarder forwarder;
    private final HashMap<String, byte[]> CHANNELS = new HashMap<>();
    private final HashSet<String> INCOMING_CHANNELS = new HashSet<>();
    private final HashSet<String> OUTGOING_CHANNELS = new HashSet<>();

    public ChannelRegistry(Jedis jedis, RedisMessageForwarder forwarder) {
        this.jedis = jedis;
        this.forwarder = forwarder;
    }

    private void registerChannel(String channel) {
        if (!isRegistered(channel)) CHANNELS.put(channel, ChannelCodec.encode(channel));
    }

    public void registerIncomingChannel(String channel) {
        registerChannel(channel);
        INCOMING_CHANNELS.add(channel);
        new Thread(() -> jedis.subscribe(forwarder, toBytes(channel))).start();
    }

    public void registerOutgoingChannel(String channel) {
        registerChannel(channel);
        OUTGOING_CHANNELS.add(channel);
    }

    private void unregisterChannel(String channel) {
        if (!(isIncomingChannel(channel) || isOutgoingChannel(channel))) CHANNELS.remove(channel);
    }

    public void unregisterIncomingChannel(String channel) {
        forwarder.unsubscribe(toBytes(channel));
        INCOMING_CHANNELS.remove(channel);
        unregisterChannel(channel);
    }

    public void unregisterOutgoingChannel(String channel) {
        OUTGOING_CHANNELS.remove(channel);
        unregisterChannel(channel);
    }

    public void unregisterAllChannels() {
        for (String channel : CHANNELS.keySet()) {
            unregisterIncomingChannel(channel);
            unregisterOutgoingChannel(channel);
        }
    }

    private boolean isRegistered(String channel) {
        return CHANNELS.containsKey(channel);
    }

    public boolean isIncomingChannel(String channel) {
        return INCOMING_CHANNELS.contains(channel);
    }

    public boolean isOutgoingChannel(String channel) {
        return OUTGOING_CHANNELS.contains(channel);
    }

    public byte[] toBytes(String channel) {
        return CHANNELS.get(channel);
    }

}
