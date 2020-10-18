package amata1219.redis.plugin.messages.common.channel;

import java.util.Arrays;

public class ChannelHashing {

    public static Integer hash(byte[] channelBytes) {
        return Arrays.hashCode(channelBytes);
    }

    public static Integer hash(String channel) {
        return hash(ChannelCodec.encode(channel));
    }

}
