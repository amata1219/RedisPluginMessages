[English README is here.](https://github.com/amata1219/RedisPluginMessages/blob/master/README.md)
# RedisPluginMessages
　インメモリデータベースのRedisを利用してプレイヤーを介さずにPluginMessagingを行うSpigot/BungeeCordプラグイン。  
　従来のPluginMessagesとほぼ変わらない感覚でメッセージの送受信が出来ます。  
  SpigotとBungeeCord間のメッセージングに加え、Spigot同士のメッセージングも出来ます。

## 使用方法
1. Redisを導入し利用可能な状態にします。  
2. 対象となるBungeeCord/SpigotのpluginsフォルダにRedisPluginMessagesフォルダを作成します。  
3. 作成したRedisPluginMessagesフォルダに下記の内容のconfig.ymlを作成します。  
```yaml
unique-name-of-instance: ''
#このプラグインを読み込むインスタンスに、コンピュータ上で一意な名前を設定します。
#インスタンスがプロキシサーバー(BungeeCord、Waterfall等)の場合は自由に名前を設定出来ます。
#インスタンスがサーバーMOD(Spigot、Paper等)の場合は、プロキシサーバーの config.yml に記述されているサーバー名を指定して下さい。
#プロキシサーバーを利用していない場合は自由に名前を設定出来ます。

redis-server:
  host: 'localhost'
  port: 6379
  password: ''
  #パスワードがない場合は''(シングルクオーテーションを2個)と記述して下さい。
```
4. 各BungeeCord/SpigotのpluginsフォルダにRedisPluginMessages.jarを入れます。  
5. Redisサーバーを起動します。  
6. BungeeCord/Spigotを立ち上げると、PluginMessagingが利用可能な状態になります。  

## サンプルコード
plugin.yml
```yaml
depend: [RedisPluginMessages]
```
Spigot側
```java
import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotSideRegisteringSample extends JavaPlugin {

    private final RedisPluginMessagesAPI api = (RedisPluginMessagesAPI) getServer().getPluginManager().getPlugin("RedisPluginMessages");
    //plugin.ymlで'depend'の指定をしているため、RedisPluginMessagesが見つからない場合はこのコードが実行される前にプラグインのロードに失敗します。

    @Override
    public void onEnable() {
        api.registerIncomingChannels("req:main-world-state");
        //メッセージを受信するチャンネルを登録します。

        api.registerOutgoingChannels("res:main-world-state");
        //メッセージを送信するチャンネルを登録します。

        api.registerSubscriber("req:main-world-state", new SpigotSideSubscriberSample());
        //サブスクライバーを登録します。
    }

    @Override
    public void onDisable() {
        api.unregisterIncomingChannels("req:main-world-state");
        api.unregisterOutgoingChannels("res:main-world-state");
        //PlugManやDisableMeなどでプラグインをアンロードする場合はチャンネルの登録を解除して下さい。
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
        //sourceServerNameはそのサーバーのRedisPluginMessagesのconfig.ymlに記載された'unique-name-of-instance'の値になります。

        String message = messages.readUTF();
        Bukkit.broadcastMessage(message);
        //受信したメッセージの読み取りと出力をします。

        ByteArrayDataOutput out = ByteIO.newDataOutput();
        //データを書き込むための空のByteArrayDataOutputを作成します。

        World world = Bukkit.getWorld("world");
        out.writeLong(world.getTime());
        out.writeBoolean(world.isClearWeather());
        //ByteArrayDataOutputにメインワールドの状態を書き込みます。

        api.sendRedisMessage("res:main-world-state", out);
        //res:main-world-stateチャンネルにメッセージを送信します。
    }
    
}
```
bungee.yml
```yaml
depends: [RedisPluginMessages]
```
Bungee側
```java
import amata1219.redis.plugin.messages.common.RedisPluginMessagesAPI;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeSideRegisteringSample extends Plugin {

    private final RedisPluginMessagesAPI api = (RedisPluginMessagesAPI) getProxy().getPluginManager().getPlugin("RedisPluginMessages");
    //plugin.ymlで'depends'の指定をしているため、RedisPluginMessagesが見つからない場合はこのコードが実行される前にプラグインのロードに失敗します。

    @Override
    public void onEnable() {
        api.registerIncomingChannels("res:main-world-state");
        //メッセージを受信するチャンネルを登録します。

        api.registerOutgoingChannels("req:main-world-state");
        //メッセージを送信するチャンネルを登録します。

        api.registerSubscriber("res:main-world-state", new BungeeSideSubscriberSample());
        //サブスクライバーを登録します。
    }

    @Override
    public void onDisable() {
        api.unregisterIncomingChannels("res:main-world-state");
        api.unregisterOutgoingChannels("req:main-world-state");
        //PlugManやDisableMeなどでプラグインをアンロードする場合はチャンネルの登録を解除して下さい。
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
        //受信したメッセージの読み取りと出力をします。
    }

}
```
