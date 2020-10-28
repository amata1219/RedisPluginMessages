package amata1219.redis.plugin.messages.common;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;

public class Redis {

    private final String host;
    private final int port;
    private final String password;

    private final ArrayList<Jedis> instances = new ArrayList<>();

    public Redis(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public Jedis createInstance() {
        Jedis jedis = new Jedis(host, port);
        if (password != null) {
            String result = jedis.auth(password);
            if (!result.equals("OK")) System.out.println("Could not authenticate with password '" + password + "'");
        }
        instances.add(jedis);
        return jedis;
    }

    public void closeAllInstances() {
        for (Jedis jedis : instances) jedis.close();
    }

}
