package io.github.sebminecrafter.fundamentals.Chat;

import io.github.sebminecrafter.fundamentals.Commands.Ignore;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.sebminecrafter.fundamentals.Main.*;

public class GlobalChat implements Listener {
    private final Ignore ignore;
    private final Pattern urlRegex;
    private final Pattern disallowedRegex;

    public GlobalChat(JavaPlugin plugin, Ignore ignore) {
        this.ignore = ignore;

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
        event.setCancelled(true);
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (message.startsWith("/")) return;
        if (!config.isEnabled("chat.globalchat")) return;
        if (notAllowed(message, player)) return;

        if (config.isEnabled("chat.colors")) {
            message = lang.formatColors(message);
        }

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getDisplayName());
        helper.add("MSG", message);

        String formattedMessage = lang.getKey("chat.format", helper.getReplace());
        logger.log(formattedMessage);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (ignore.isIgnoring(p.getUniqueId(), player.getUniqueId())) continue;
            p.sendMessage(formattedMessage);
        }
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
