package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Staffmode implements Listener, FundamentalCommand {

    private final Map<UUID, GameMode> savedGameModes = new HashMap<>();
    private final Map<UUID, Location> savedLocations = new HashMap<>();
    private final JavaPlugin plugin;
    private final Logging logger;
    private final Lang lang;

    public Staffmode(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = Main.logger;
        this.lang = Main.lang;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player wantsToTpTo = null;
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.getKey("msgs.playeronly"));
            return true;
        } else if (args.length > 1) {
            return false;
        } else if (args.length == 1) {
            wantsToTpTo = Bukkit.getPlayerExact(args[0]);
        }

        UUID uuid = player.getUniqueId();

        if (wantsToTpTo == null) {
            if (savedGameModes.containsKey(uuid)) {
                restore(player);
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("PLAYER", player.getName());
                player.sendMessage(lang.getKey("staffcmds.staffmode.staff.exit", helper.getReplace()));
                logger.log(lang.getKey("staffcmds.staffmode.log.exit", helper.getReplace()));
            } else {
                savedGameModes.put(uuid, player.getGameMode());
                savedLocations.put(uuid, player.getLocation().clone());
                player.setGameMode(GameMode.SPECTATOR);
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("PLAYER", player.getName());
                player.sendMessage(lang.getKey("staffcmds.staffmode.staff.enter", helper.getReplace()));
                logger.log(lang.getKey("staffcmds.staffmode.log.enter", helper.getReplace()));
            }
        } else {
            savedGameModes.put(uuid, player.getGameMode());
            savedLocations.put(uuid, player.getLocation().clone());
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(wantsToTpTo);
            PlaceholderHelper helper = new PlaceholderHelper();
            helper.add("PLAYER", player.getName());
            helper.add("VICTIM", wantsToTpTo.getName());
            player.sendMessage(lang.getKey("staffcmds.staffmode.staff.enter", helper.getReplace())+lang.getKey("staffcmds.staffmode.addon", helper.getReplace()));
            logger.log(lang.getKey("staffcmds.staffmode.log.enter", helper.getReplace())+lang.getKey("staffcmds.staffmode.addon", helper.getReplace()));
        }
        return true;
    }

    private void restore(Player player) {
        UUID uuid = player.getUniqueId();
        GameMode previousGameMode = savedGameModes.remove(uuid);
        Location previousLocation = savedLocations.remove(uuid);

        if (previousGameMode != null) player.setGameMode(previousGameMode);
        if (previousLocation != null) player.teleport(previousLocation);
    }

    public void restoreAll() {
        for (UUID uuid : new ArrayList<>(savedGameModes.keySet())) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                restore(player);
            }
        }
        savedGameModes.clear();
        savedLocations.clear();
    }

    private void removePlayer(@NonNull Player player) {
        UUID uuid = player.getUniqueId();
        savedGameModes.remove(uuid);
        savedLocations.remove(uuid);
    }

    @EventHandler
    public void onPlayerQuit(@NonNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (savedGameModes.containsKey(player.getUniqueId())) {
            restore(player);
        }
    }

    @EventHandler
    public void onPlayerGameModeChange(@NonNull PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SPECTATOR) return;
        if (event.getNewGameMode() == GameMode.SPECTATOR) return;
        if (savedGameModes.containsKey(player.getUniqueId())) {
            PlaceholderHelper helper = new PlaceholderHelper();
            helper.add("PLAYER", player.getName());
            player.sendMessage(lang.getKey("staffcmds.staffmode.staff.exit", helper.getReplace()));
            logger.log(lang.getKey("staffcmds.staffmode.log.exit", helper.getReplace()));
            removePlayer(player);
        }
    }
}