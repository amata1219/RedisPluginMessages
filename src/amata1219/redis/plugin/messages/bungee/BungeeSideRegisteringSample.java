package amata1219.redis.plugin.messages.bungee;

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
