package amata1219.redis.plugin.messages.common.io;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ByteIOStreams {

    private static String HOST_INSTANCE_NAME;

    public static void initialize(String hostInstanceName) {
        if (HOST_INSTANCE_NAME != null) throw new UnsupportedOperationException();
        HOST_INSTANCE_NAME = hostInstanceName;
    }

    public static ByteArrayDataInput newDataInput(byte[] bytes) {
        return ByteStreams.newDataInput(bytes);
    }

    public static ByteArrayDataOutput newDataOutput() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(HOST_INSTANCE_NAME);
        return out;
    }

}
