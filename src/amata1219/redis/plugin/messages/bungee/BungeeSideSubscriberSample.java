package amata1219.redis.plugin.messages.bungee;

import amata1219.redis.plugin.messages.common.RedisSubscriber;
import com.google.common.io.ByteArrayDataInput;

public class BungeeSideSubscriberSample implements RedisSubscriber {

    @Override
    public void onRedisMessageReceived(String sourceServerName, ByteArrayDataInput messages) {
        long time = messages.readLong();
        String weather = messages.readBoolean() ? "Sunny" : "Rainy";

        System.out.printf("""
                State of the %s's main world:
                    Time: %d
                    Weather: %s
                """, sourceServerName, time, weather);
        //Reads and outputs sent messages.
    }

}
