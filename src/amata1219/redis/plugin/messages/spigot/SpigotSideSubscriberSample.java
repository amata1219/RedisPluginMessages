package amata1219.redis.plugin.messages.spigot;

import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import amata1219.redis.plugin.messages.common.RedisSubscriber;
import amata1219.redis.plugin.messages.common.io.ByteIO;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class SpigotSideSubscriberSample implements RedisSubscriber {

    private final RedisPluginMessagesAPI api = (RedisPluginMessagesAPI) Bukkit.getPluginManager().getPlugin("RedisPluginMessages");

    @Override
    public void onRedisMessageReceived(String sourceServerName, ByteArrayDataInput messages) {
        Bukkit.broadcastMessage("Message received from %s!".formatted(sourceServerName));
        //The sourceServerName is 'unique-name-of-instance' in the config.yml of RedisPluginMessages for that server.

        String message = messages.readUTF();
        Bukkit.broadcastMessage(message);
        //Reads and outputs sent messages.

        ByteArrayDataOutput out = ByteIO.newDataOutput();
        //Create an empty ByteArrayDataOutput for writing data.

        World world = Bukkit.getWorld("world");
        out.writeLong(world.getTime());
        out.writeBoolean(world.isClearWeather());
        //Write world state to ByteArrayDataOutput.

        api.sendRedisMessage("res:main-world-state", out);
        //Send a message to the main-world-state channel
    }

}
