package io.github.sebminecrafter.fundamentals.IO;

import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config {
    private YamlConfiguration config;
    private final File file;
    private final Logging logger;
    private final JavaPlugin plugin;

    public Config(JavaPlugin plugin) {
        this.logger = Main.logger;
        this.file = new File(plugin.getDataFolder() + File.separator + "config.yml");
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.config = YamlLoader.load(plugin, logger, "config.yml");
    }

    public boolean isEnabled(String path) {
        return getBoolean("enabled."+path);
    }

    public void setEnabled(String path, boolean value) {
        config.set("enabled."+path, value);
        saveConfig();
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving config!");
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public String getString(String path) {
        return config.getString(path);
    }
    public int getInt(String path) {
        return config.getInt(path);
    }
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }
    public ConfigurationSection getConfigurationSection(String s) {
        return config.getConfigurationSection(s);
    }
}
