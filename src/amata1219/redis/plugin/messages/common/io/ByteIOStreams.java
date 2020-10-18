package amata1219.redis.plugin.messages.common.io;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ByteIOStreams {

    private static String HOST_SERVER_NAME;

    public static void initializeWith(String hostServerName) {
        HOST_SERVER_NAME = hostServerName;
    }

    public static ByteArrayDataInput newDataInput(byte[] bytes) {
        return ByteStreams.newDataInput(bytes);
    }

    public static ByteArrayDataOutput newDataOutput() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(HOST_SERVER_NAME);
        return out;
    }

}
