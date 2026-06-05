package io.github.sebminecrafter.fundamentals.IO;

import io.github.sebminecrafter.fundamentals.Main;
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
    private final YamlConfiguration config;

    public Config(JavaPlugin plugin, String fileName) {
        Logging logger = Main.logger;
        YamlConfiguration configFile = new YamlConfiguration();
        File file = new File(plugin.getDataFolder() + File.separator + fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        try {
            // Load config file
            configFile.load(file);
            // Load defaults
            InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(plugin.getResource(fileName)));
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
}
