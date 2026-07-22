package io.github.sebminecrafter.fundamentals.IO;

import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
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
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }
        YamlConfiguration configFile = new YamlConfiguration();
        try {
            // Load config file
            configFile.load(file);
            // Load defaults
            InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(plugin.getResource("config.yml")));
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
            configFile.setDefaults(defaults);
        } catch (IOException | InvalidConfigurationException | NullPointerException e) {
            logger.log(Level.SEVERE, "Error loading config file or defaults!");
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
        this.config = configFile;
    }

    public boolean isEnabled(String path) {
        return getBoolean("enabled."+path);
    }
    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving config!");
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public Object getKey(String path) {
        return config.get(path);
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
