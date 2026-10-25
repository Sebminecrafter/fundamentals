package io.github.sebminecrafter.fundamentals.Chat;

import io.github.sebminecrafter.fundamentals.Commands.Player.Ignore;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

        this.disallowedRegex = compileDisallowedRegex();

        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (config.isEnabled("chat.customjl")) {
            new JoinsAndLeaves(plugin);
        }
        if (config.isEnabled("chat.customdeaths")) {
            new DeathMessage(plugin);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!config.isEnabled("chat.globalchat")) return;

        Player player = event.getPlayer();
        String message = event.getMessage();
        if (message.startsWith("/")) return;

        event.setCancelled(true);
        if (notAllowed(message, player)) return;

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getDisplayName());

        String formattedMessage = lang.getKey("chat.format", helper.getReplace(),
                Map.of("MSG", formatPlayerText(player, message)));
        logger.log(formattedMessage);
        for (Player p : new ArrayList<>(event.getRecipients())) {
            if (ignore.isIgnoring(p.getUniqueId(), player.getUniqueId())) continue;
            p.sendMessage(formattedMessage);
        }
    }

    /** Formats a player message based on config */
    public String formatPlayerText(Player player, String message) {
        String text;
        if (canUseColors(player)) {
            text = lang.formatColors(message);
        } else {
            text = message.replace("§", "");
        }
        return text.replace("\n", "").replace("\r", "");
    }

    private Pattern compileDisallowedRegex() {
        String regex = config.getString("enabled.chat.disallowedregex");
        // empty = no filter
        if (regex == null || regex.isBlank()) return null;
        try {
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
        } catch (PatternSyntaxException e) {
            logger.log(Level.SEVERE, "Invalid enabled.chat.disallowedregex, the chat filter is disabled: "
                    + e.getDescription());
            return null;
        }
    }

    private boolean canUseColors(Player player) {
        // Colors allowed at all?
        if (!config.isEnabled("chat.colors")) return false;
        // Need a perm for it?
        if (!config.isEnabled("chat.needpermforcolors")) return true;
        // Do they have the perm or not?
        return player.hasPermission("fundamentals.player.chat.colors");
    }

    public boolean notAllowed(String message, Player player) {
        Matcher matcher;

        matcher = urlRegex.matcher(message);
        if (matcher.find() && !config.isEnabled("chat.allowlinks")) {
            player.sendMessage(lang.getKey("chat.nolinks"));
            return true;
        }

        if (disallowedRegex != null && disallowedRegex.matcher(message).find()) {
            player.sendMessage(lang.getKey("chat.disallowed"));
            return true;
        }

        return false;
    }
}
