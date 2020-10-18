package amata1219.redis.plugin.messages.common;

import com.google.common.io.ByteArrayDataInput;

public interface RedisSubscriber {

    void onRedisMessageReceived(String sourceServerName, ByteArrayDataInput message);

}
