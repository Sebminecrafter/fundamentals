package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Freeze implements FundamentalCommand, Listener {
    private final Lang lang;
    private final Logging logger;
    private final JavaPlugin plugin;
    private final Map<UUID, BukkitTask> frozenPlayers;

    public Freeze(JavaPlugin plugin) {
        this.lang = Main.lang;
        this.logger = Main.logger;
        this.plugin = plugin;
        this.frozenPlayers = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length != 1) {
            return false;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(lang.getKey("msgs.offline"));
            return true;
        }

        UUID uuid = target.getUniqueId();

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("VICTIM", target.getName());

        List<List<String>> replace = helper.getReplace();

        if (frozenPlayers.containsKey(uuid)) {
            // Remove task and unfreeze
            frozenPlayers.get(uuid).cancel();
            frozenPlayers.remove(uuid);

            logger.log(lang.getKey("staffcmds.freeze.log.stop", replace));
            sender.sendMessage(lang.getKey("staffcmds.freeze.staff.stop", replace));
            target.sendMessage(lang.getKey("staffcmds.freeze.player.stop", replace));
        } else {
            // Schedule message task and freeze
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> sendMsg(target), 0L, 60L);
            frozenPlayers.put(uuid, task);

            logger.log(lang.getKey("staffcmds.freeze.log.start", replace));
            sender.sendMessage(lang.getKey("staffcmds.freeze.staff.start", replace));
            target.sendMessage(lang.getKey("staffcmds.freeze.player.start", replace));
        }
        return true;
    }

    private void sendMsg(Player player) {
        if (player == null) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(lang.getKey("staffcmds.freeze.player.actionbar")));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!frozenPlayers.containsKey(player.getUniqueId())) return;
        Location from = event.getFrom();
        Location to = event.getTo();
        // Only block actual position changes, not head rotation
        if (to != null && (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ())) {
            event.setTo(from); // Cancel the movement instead of teleporting
        }
    }

    public void cleanup() {
        frozenPlayers.clear();
    }
}
