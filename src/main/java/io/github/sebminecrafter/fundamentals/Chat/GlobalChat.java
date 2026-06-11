package io.github.sebminecrafter.fundamentals.Chat;

import io.github.sebminecrafter.fundamentals.IO.Config;
import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalChat implements Listener {
    private final Lang lang;
    private final Config config;
    private final Pattern urlRegex;
    private final Pattern disallowedRegex;

    public GlobalChat(JavaPlugin plugin) {
        this.lang = Main.lang;
        this.config = Main.config;

        this.urlRegex = Pattern.compile(
                "\\b(?:(?:https?|ftp)://|www\\.)?(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,63}(?::\\d{1,5})?(?:/\\S*)?\\b",
                Pattern.CASE_INSENSITIVE+Pattern.UNICODE_CASE);

        this.disallowedRegex = Pattern.compile(
                config.getString("enabled.chat.disallowedregex"),
                Pattern.CASE_INSENSITIVE+Pattern.UNICODE_CASE);

        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (config.isEnabled("chat.customjl")) {
            new JoinsAndLeaves(plugin);
        }
        if (config.isEnabled("chat.customdeaths")) {
            new DeathMessage(plugin);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (message.startsWith("/") && !message.startsWith("/msg")) return;
        if (!config.isEnabled("chat.globalchat")) {
            event.setCancelled(true);
            return;
        }

        if (config.isEnabled("chat.colors")) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        if (notAllowed(message, player)) {
            event.setMessage("");
            event.setCancelled(true);
            return;
        }

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getDisplayName());
        helper.add("MSG", message);

        event.setMessage(lang.getKey("chat.format", helper.getReplace()));
        event.setFormat(event.getMessage());
    }

    public boolean notAllowed(String message, Player player) {
        Matcher matcher;

        matcher = urlRegex.matcher(message);
        if (matcher.find() && !config.isEnabled("chat.allowlinks")) {
            player.sendMessage(lang.getKey("chat.nolinks"));
            return true;
        }

        matcher = disallowedRegex.matcher(message);
        if (matcher.find()) {
            player.sendMessage(lang.getKey("chat.disallowed"));
            return true;
        }

        return false;
    }
}
