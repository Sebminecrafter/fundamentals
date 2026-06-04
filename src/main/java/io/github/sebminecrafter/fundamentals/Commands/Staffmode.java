package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.Main;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class Staffmode implements Listener {

    private final Map<UUID, GameMode> savedGameModes = new HashMap<>();
    private final Map<UUID, Location> savedLocations = new HashMap<>();
    private final JavaPlugin plugin;
    private final Logging logger;

    public Staffmode(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = Main.logger;

        getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void execute(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be run by a player.");
            return;
        }

        UUID uuid = player.getUniqueId();

        if (savedGameModes.containsKey(uuid)) {
            restore(player);
            player.sendMessage("You have left staff mode.");
            logger.log(player.getName()+" exited staff mode.");
        } else {
            savedGameModes.put(uuid, player.getGameMode());
            savedLocations.put(uuid, player.getLocation().clone());
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("You have entered staff mode.");
            logger.log(player.getName()+" entered staff mode.");
        }
    }

    private void restore(Player player) {
        UUID uuid = player.getUniqueId();
        GameMode previousGameMode = savedGameModes.remove(uuid);
        Location previousLocation = savedLocations.remove(uuid);

        if (previousGameMode != null) player.setGameMode(previousGameMode);
        if (previousLocation != null) player.teleport(previousLocation);
    }

    public void restoreAll() {
        for (UUID uuid : savedGameModes.keySet()) {
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
            player.sendMessage("You have left staff mode.");
            logger.log(player.getName()+" exited staff mode.");
            removePlayer(player);
        }
    }
}