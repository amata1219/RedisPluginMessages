package amata1219.redis.plugin.messages.common.registry;

import amata1219.redis.plugin.messages.common.Redis;
import amata1219.redis.plugin.messages.common.RedisMessageForwarder;
import amata1219.redis.plugin.messages.common.channel.ChannelCodec;

import java.util.HashMap;
import java.util.HashSet;

public class ChannelRegistry {

    private final Redis redis;
    private final RedisMessageForwarder forwarder;
    private final HashMap<String, byte[]> CHANNELS = new HashMap<>();
    private final HashSet<String> INCOMING_CHANNELS = new HashSet<>();
    private final HashSet<String> OUTGOING_CHANNELS = new HashSet<>();

    public ChannelRegistry(Redis redis, RedisMessageForwarder forwarder) {
        this.redis = redis;
        this.forwarder = forwarder;
    }

    private void registerChannel(String channel) {
        if (!isRegistered(channel)) CHANNELS.put(channel, ChannelCodec.encode(channel));
    }

    public void registerIncomingChannels(String... channels) {
        for (String channel : channels) {
            registerChannel(channel);
            INCOMING_CHANNELS.add(channel);
        }

        new Thread(() -> {
            try {
                redis.createInstance().subscribe(forwarder, channelsToBytes(channels));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void registerOutgoingChannels(String... channels) {
        for (String channel : channels) {
            registerChannel(channel);
            OUTGOING_CHANNELS.add(channel);
        }
    }

    private void unregisterChannel(String channel) {
        if (!(isIncomingChannel(channel) || isOutgoingChannel(channel))) CHANNELS.remove(channel);
    }

    public void unregisterIncomingChannels(String... channels) {
        forwarder.unsubscribe(channelsToBytes(channels));
        for (String channel : channels) {
            INCOMING_CHANNELS.remove(channel);
            unregisterChannel(channel);
        }
    }

    public void unregisterOutgoingChannels(String... channels) {
        for (String channel : channels) {
            OUTGOING_CHANNELS.remove(channel);
            unregisterChannel(channel);
        }
    }

    public void unregisterAllChannels() {
        String[] channels = CHANNELS.keySet().toArray(new String[0]);
        unregisterIncomingChannels(channels);
        unregisterOutgoingChannels(channels);
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

    private byte[][] channelsToBytes(String... channels) {
        byte[][] bytes = new byte[channels.length][];
        for (int i = 0; i < channels.length; i++) bytes[i] = toBytes(channels[i]);
        return bytes;
    }

}
