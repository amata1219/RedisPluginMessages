[日本語のREADMEはこちらになります。](https://github.com/amata1219/RedisPluginMessages/blob/master/README-ja.md)
# RedisPluginMessages  
Spigot/BungeeCord plugin for PluginMessaging without a player using Redis, an in-memory database.  
Messages can be sent and received in much the same way as with conventional PluginMessages.

## Usage  
1. Install Redis and make it available.
2. Create a RedisPluginMessages folder in the plugins' folder of the target BungeeCord/Spigot.
3. Create a config.yml file with the following contents in the created RedisPluginMessages folder.
```yaml
unique-name-of-instance: ''
#Set a unique name on your computer for the instance that will load this plugin.
#If the instance is a proxy server (BungeeCord, Waterfall, etc.), you can set the name freely.
#If the instance is a server mod (Spigot, Paper, etc.), specify the server name described in the config.yml of the proxy server.
#If you are not using a proxy server, you can set the name freely.

redis-server:
  host: 'localhost'
  port: 6379
  password: ''
  #If there is no password, please write ''(two single quotation marks).
```
4. Put RedisPluginMessages.jar in the plugins' folder of each BungeeCord/Spigot.
5. Start the Redis server.
6. Launch BungeeCord/Spigot and PluginMessaging will be available.

## SampleCode  
plugin.yml
```yaml
depend: [RedisPluginMessages]
```
Spigot side
```java
import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotSideRegisteringSample extends JavaPlugin {

    private final RedisPluginMessagesAPI api = (RedisPluginMessagesAPI) getServer().getPluginManager().getPlugin("RedisPluginMessages");
    //If RedisPluginMessages is not found because 'depend' is specified in plugin.yml, the plugin will fail to load before this code is executed.

    @Override
    public void onEnable() {
        api.registerIncomingChannels("req:main-world-state");
        //Register a channel to receive messages.

        api.registerOutgoingChannels("res:main-world-state");
        //Register a channel to send messages.

        api.registerSubscriber("req:main-world-state", new SpigotSideSubscriberSample());
        //Register a message subscriber.
    }

    @Override
    public void onDisable() {
        api.unregisterIncomingChannels("req:main-world-state");
        api.unregisterOutgoingChannels("res:main-world-state");
        //If this plugin may be unloaded by PlugMan, DisableMe, etc., please unregister the channels.
    }
    
}
```
```java
import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import amata1219.redis.plugin.messages.common.RedisSubscriber;
import amata1219.redis.plugin.messages.common.io.ByteIO;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class SpigotSideSample implements RedisSubscriber {

    private final RedisPluginMessagesAPI api = (RedisPluginMessagesAPI) Bukkit.getPluginManager().getPlugin("RedisPluginMessages");

    @Override
    public void onRedisMessageReceived(String sourceServerName, ByteArrayDataInput messages) {
        Bukkit.broadcastMessage("Message received from %s!".formatted(sourceServerName));
        //The sourceServerName is the value of 'unique-name-of-instance' in the config.yml of RedisPluginMessages for that server.

        String message = messages.readUTF();
        Bukkit.broadcastMessage(message);
        //Reads and outputs received messages.

        ByteArrayDataOutput out = ByteIO.newDataOutput();
        //Create an empty ByteArrayDataOutput for writing data.

        World world = Bukkit.getWorld("world");
        out.writeLong(world.getTime());
        out.writeBoolean(world.isClearWeather());
        //Write state of main world to ByteArrayDataOutput.

        api.sendRedisMessage("res:main-world-state", out);
        //Send a message to the res:main-world-state channel
    }
    
}
```
bungee.yml
```yaml
depends: [RedisPluginMessages]
```
Bungee side
```java
import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeSideRegisteringSample extends Plugin {

    private final RedisPluginMessagesAPI api = (RedisPluginMessagesAPI) getProxy().getPluginManager().getPlugin("RedisPluginMessages");
    //If RedisPluginMessages is not found because 'depends' is specified in bungee.yml, the plugin will fail to load before this code is executed.

    @Override
    public void onEnable() {
        api.registerIncomingChannels("res:main-world-state");
        //Register a channel to receive messages.

        api.registerOutgoingChannels("req:main-world-state");
        //Register a channel to send messages.

        api.registerSubscriber("res:main-world-state", new BungeeSideSubscriberSample());
        //Register a message subscriber.
    }

    @Override
    public void onDisable() {
        api.unregisterIncomingChannels("res:main-world-state");
        api.unregisterOutgoingChannels("req:main-world-state");
        //If this plugin may be unloaded by PlugMan, DisableMe, etc., please unregister the channels.
    }

}
```
```java
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
        //Reads and outputs received messages.
    }

}
```