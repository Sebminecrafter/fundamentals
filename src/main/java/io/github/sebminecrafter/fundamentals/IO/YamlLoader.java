package io.github.sebminecrafter.fundamentals.IO;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

public final class YamlLoader {
    public static YamlConfiguration load(JavaPlugin plugin, Logging logger, String resourceName) {
        File file = new File(plugin.getDataFolder() + File.separator + resourceName);

        if (!file.exists()) {
            plugin.saveResource(resourceName, false);
        }

        YamlConfiguration yamlFile = new YamlConfiguration();
        try {
            yamlFile.load(file);

            InputStreamReader reader = new InputStreamReader(
                    Objects.requireNonNull(plugin.getResource(resourceName)));
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
            yamlFile.setDefaults(defaults);

        } catch (IOException | InvalidConfigurationException | NullPointerException e) {
            logger.log(Level.SEVERE, "Error loading " + resourceName + " or its defaults!");
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }

        return yamlFile;
    }
}
