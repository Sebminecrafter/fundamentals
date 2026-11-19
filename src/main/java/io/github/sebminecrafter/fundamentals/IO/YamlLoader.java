package io.github.sebminecrafter.fundamentals.IO;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

public final class YamlLoader {
    private static final int CONFIG_VERSION = 1;

    public YamlLoader() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static YamlConfiguration load(JavaPlugin plugin, Logging logger, String resourceName) {
        File file = new File(plugin.getDataFolder() + File.separator + resourceName);

        if (!file.exists()) {
            plugin.saveResource(resourceName, false);
        }

        YamlConfiguration configuration = new YamlConfiguration();
        YamlConfiguration defaults;

        try {
            configuration.options().parseComments(true);
            configuration.load(file);

            InputStreamReader reader = new InputStreamReader(
                    Objects.requireNonNull(plugin.getResource(resourceName)),
                    StandardCharsets.UTF_8
            );

            defaults = YamlConfiguration.loadConfiguration(reader);
            defaults.options().parseComments(true);
            configuration.setDefaults(defaults);
            reader.close();
        } catch (IOException | InvalidConfigurationException | NullPointerException e) {
            logger.log(Level.SEVERE, "Error loading " + resourceName + " or its defaults!");
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
            return configuration;
        }

        // No version
        if (!configuration.contains("version", true)) {
            logger.log(Level.WARNING,
                    "Config file " + resourceName + " does not have a version specified!"
            );
            logger.log(Level.WARNING, "Fundamentals will not update this config file, " +
                    "though it is recommended to manually do so.");

            return configuration;
        }

        // Has a version, check it
        int ver = configuration.getInt("version");

        // Version is higher (unknown)
        if (ver > CONFIG_VERSION) {
            logger.log(Level.WARNING,
                    "Version of config file " + resourceName
                            + " is greater than expected! "
                            + "Expected: " + CONFIG_VERSION
                            + " Got: " + ver
            );
            logger.log(Level.WARNING, "Fundamentals will not update this config file.");

            return configuration;
        }

        // Version is lower (should update)
        if (ver < CONFIG_VERSION) {
            logger.log("Updating config file " + resourceName + " with current defaults.");

            update(configuration, defaults);

            configuration.set("version", CONFIG_VERSION);

            try {
                configuration.save(file);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error saving updated config file " + resourceName);
                logger.log(Level.SEVERE, e.getMessage());
            }

            return configuration;
        }

        // Version is same / fallback case
        return configuration;
    }

    private static void update(ConfigurationSection configuration, ConfigurationSection defaults) {
        for (String key : configuration.getKeys(false).toArray(new String[0])) {
            if (!defaults.contains(key)) {
                // Preserve obsolete values in a separate section
                Object value = configuration.get(key);

                configuration.set("removed." + key, value);
                configuration.set(key, null);
            }
        }

        for (String key : defaults.getKeys(false)) {
            if (defaults.isConfigurationSection(key)) {
                ConfigurationSection defaultSection =
                        defaults.getConfigurationSection(key);

                ConfigurationSection existingSection =
                        configuration.getConfigurationSection(key);

                if (existingSection == null) {
                    existingSection = configuration.createSection(key);
                }

                update(existingSection, defaultSection);
            } else if (!configuration.contains(key)) {
                configuration.set(key, defaults.get(key));

                configuration.setComments(key, defaults.getComments(key));
                configuration.setInlineComments(
                        key, defaults.getInlineComments(key)
                );
            }
        }
    }
}
