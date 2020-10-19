package amata1219.redis.plugin.messages.bungee;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FileConfiguration {

    private final RedisPluginMessages plugin = RedisPluginMessages.instance();
    private final String fileName;
    private final File file;
    private Configuration config;

    public FileConfiguration(String fileName) {
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
    }

    public void createFileInDirectory() {
        if (file.exists()) return;

        File directory = plugin.getDataFolder();
        if (!directory.exists()) directory.mkdir();

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStream in = plugin.getResourceAsStream(fileName)) {
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfigurationToFile() {
        try {
            provider().save(config, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfigurationFromFile() {
        try {
            config = provider().load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration config() {
        if (config == null) loadConfigurationFromFile();
        return config;
    }

    private ConfigurationProvider provider() {
        return ConfigurationProvider.getProvider(YamlConfiguration.class);
    }

}
