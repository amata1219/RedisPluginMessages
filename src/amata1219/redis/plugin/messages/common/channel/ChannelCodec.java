package amata1219.redis.plugin.messages.common.channel;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ChannelCodec {

    private static final Charset ENCODING_SCHEME = StandardCharsets.UTF_8;

    public static byte[] encode(String channel) {
        return channel.getBytes(ENCODING_SCHEME);
    }

    public static String decode(byte[] bytes) {
        return new String(bytes);
    }

}
