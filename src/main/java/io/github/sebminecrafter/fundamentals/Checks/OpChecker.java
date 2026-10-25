package io.github.sebminecrafter.fundamentals.Checks;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Level;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;
import static org.bukkit.Bukkit.getServer;

public class OpChecker implements Listener {

    private final BukkitTask task;
    private final Map<UUID, Boolean> opStatus;

    public OpChecker(JavaPlugin plugin) {
        this.opStatus = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
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
        String cmd = event.getMessage();
        String player = event.getPlayer().getName();

        String type = opCommandType(cmd);
        if (type == null) {return;}

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("CMD", cmd);
        helper.add("EXECUTOR", player);

        logger.logBoth(Level.WARNING, lang.getKey("ops.player." + type, helper.getReplace()));
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String cmd = event.getCommand();
        String type = opCommandType(cmd);
        if (type == null) {return;}

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("CMD", cmd);

        logger.logBoth(lang.getKey("ops.console." + type, helper.getReplace()));
    }

    /** returns "op" or "deop" from a command string */
    static String opCommandType(String commandLine) {
        String cmd = commandLine.strip().toLowerCase(Locale.ROOT);
        if (cmd.startsWith("/")) cmd = cmd.substring(1).stripLeading();
        if (cmd.startsWith("minecraft:")) cmd = cmd.substring("minecraft:".length());
        if (cmd.startsWith("op ")) return "op";
        if (cmd.startsWith("deop ")) return "deop";
        return null;
    }

    public void checkForOps() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean current = player.isOp();
            boolean previous = opStatus.getOrDefault(player.getUniqueId(), current);
            if (current != previous) {
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("PLAYER", player.getName());
                if (current) {
                    logger.logBoth(Level.WARNING, lang.getKey("ops.detect.op", helper.getReplace()));
                } else {
                    logger.logBoth(Level.WARNING, lang.getKey("ops.detect.deop", helper.getReplace()));
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
