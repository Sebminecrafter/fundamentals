package io.github.sebminecrafter.fundamentals.Checks;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.Main;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getServer;

public class OpChecker implements Listener {

    private final BukkitTask task;
    private final Map<UUID, Boolean> opStatus;
    private final Logging logger;
    private final Lang lang;

    public OpChecker(JavaPlugin plugin) {
        this.logger = Main.logger;
        this.lang = Main.lang;

        this.opStatus = new HashMap<>();
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            opStatus.put(player.getUniqueId(), player.isOp());
        }

        // Enable listener
        getServer().getPluginManager().registerEvents(this, plugin);

        this.task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::checkForOps,
                0L,
                100L
        );
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().toLowerCase();
        String player = event.getPlayer().getName();

        if (!cmd.startsWith("op ") && !cmd.startsWith("deop ")) {return;}

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("CMD", cmd);
        helper.add("EXECUTOR", player);
        List<List<String>> replace = helper.getReplace();

        if (cmd.startsWith("op ")) {
            logger.logBoth(Level.WARNING, lang.getKey("ops.player.op", replace));
        } else if (cmd.startsWith("deop ")) {
            logger.logBoth(Level.WARNING, lang.getKey("ops.player.deop", replace));
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String cmd = event.getCommand().toLowerCase();
        if (!cmd.startsWith("op ") && !cmd.startsWith("deop ")) {return;}

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("CMD", cmd);
        List<List<String>> replace = helper.getReplace();

        if (cmd.startsWith("op ")) {
            logger.logBoth(lang.getKey("ops.console.op", replace));
        } else if (cmd.startsWith("deop ")) {
            logger.logBoth(lang.getKey("ops.console.deop", replace));
        }
    }

    public void checkForOps() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean current = player.isOp();
            boolean previous = opStatus.getOrDefault(player.getUniqueId(), current);
            if (current != previous) {
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("PLAYER", player.getName());
                List<List<String>> replace = helper.getReplace();
                if (current) {
                    logger.logBoth(Level.WARNING, lang.getKey("ops.detect.op", replace));
                } else {
                    logger.logBoth(Level.WARNING, lang.getKey("ops.detect.deop", replace));
                }
            }
            opStatus.put(player.getUniqueId(), current);
        }
    }

    public void stopTask() {
        if (task != null) {
            task.cancel();
        }
    }
}
