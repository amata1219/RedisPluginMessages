package amata1219.redis.plugin.messages.spigot;

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
