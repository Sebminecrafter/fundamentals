package io.github.sebminecrafter.fundamentals.IO;

import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Lang {
    private final YamlConfiguration config;

    public Lang(JavaPlugin plugin) {
        this.config = YamlLoader.load(plugin, Main.logger, "lang.yml");
    }

    public String formatColorCodes(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /** Get the translation for `key`
     * @param key The translation key path (like `command.example.response`)
     * */
    public String getKey(String key) {
        String string = _getKey(key);
        string = formatColorCodes(string);
        return string;
    }

    /** Get the translation for `key`, with placeholders
     * @param key The translation key path (like `command.example.response`)
     * @param list List of Lists, each should be a pair of String (value) and String (replacement)
     *  */
    public String getKey(String key, List< List<String> > list) {
        String string = _getKey(key);
        for (List<String> stringList : list) {
            if (stringList.size() < 2) {
                continue;
            }
            string = string.replace(stringList.getFirst(), stringList.getLast());
        }
        string = formatColorCodes(string);
        return string;
    }

    // Internal method to simplify code
    private String _getKey(String key) {
        String crKey = config.getString(key);
        if (crKey == null) {
            crKey = key;
        }
        return crKey;
    }
}
