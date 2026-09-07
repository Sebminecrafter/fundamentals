package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Cooldowns;
import io.github.sebminecrafter.fundamentals.IO.Locations.Location;
import io.github.sebminecrafter.fundamentals.IO.Locations.JsonLocationStorage;
import io.github.sebminecrafter.fundamentals.IO.Locations.PlayerLocations;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.IO.TeleportCountdown;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

import static io.github.sebminecrafter.fundamentals.Main.*;

public class Homes implements FundamentalCommand, Listener {
    private JsonLocationStorage storage = null;
    private final Map<UUID, PlayerLocations> cache = new HashMap<>();

    public Homes(JavaPlugin plugin) {
        Path folder = Path.of(plugin.getDataFolder().toString(), "homes");

        try {
            this.storage = new JsonLocationStorage(folder);
            logger.log("Loaded home storage.");
        } catch (IOException e) {
            logger.logBoth(Level.SEVERE, "Failed to load home storage:");
            logger.logBoth(Level.SEVERE,
                    e.getMessage()
                            + " " +
                            Arrays.toString(e.getStackTrace()));
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
        logger.log("Registered events for homes.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        if (storage == null) {
            Commands.safeSend(sender, lang.getKey("cmds.home.error"));
            return true;
        }
        PlayerLocations playerLocations = cache.get(player.getUniqueId());
        if (playerLocations == null) {
            // Prevent NPEs from joining before plugin load
            playerLocations = storage.load(player.getUniqueId());
            cache.put(player.getUniqueId(), playerLocations);
        }
        Map<String, Location> homes = playerLocations.getHomes();
        switch (label.toLowerCase()) {
            case "listhomes", "homes" -> {
                if (args.length != 0)
                    return false;
                if (!homes.isEmpty()) {
                    StringBuilder message = new StringBuilder(lang.getKey("cmds.home.list"));
                    boolean i = false;
                    for (String home : homes.keySet()) {
                        String text = home;
                        if (i)
                            text = ", " + text;
                        else
                            i = true;
                        message.append(text);
                    }
                    Commands.safeSend(player, message.toString());
                } else {
                    Commands.safeSend(player, lang.getKey("cmds.home.nohomes"));
                }
            }
            case "home" -> {
                if (args.length != 1)
                    return false;
                int cooldown = config.getInt("home.cooldown");
                long remaining = Cooldowns.remainingSeconds("home", player.getUniqueId(), cooldown);
                if (remaining > 0) {
                    PlaceholderHelper cooldownHelper = new PlaceholderHelper();
                    cooldownHelper.add("SECS", Long.toString(remaining));
                    Commands.safeSend(player, lang.getKey("msgs.cooldown", cooldownHelper.getReplace()));
                    return true;
                }
                Location home = homes.get(args[0]);
                PlaceholderHelper helper = new PlaceholderHelper();
                int homeDelay = config.getInt("home.delay");
                helper.add("HOME", args[0]);
                helper.add("SECS", Integer.toString(homeDelay));

                if (home != null) {
                    World world = Bukkit.getWorld(home.world());
                    if (world == null) {
                        Commands.safeSend(player, lang.getKey("cmds.home.worldmissing", helper.getReplace()));
                        return true;
                    }
                    org.bukkit.Location destination = new org.bukkit.Location(world, home.x(), home.y(), home.z(),
                            home.yaw(), home.pitch());

                    Cooldowns.start("home", player.getUniqueId(), cooldown);
                    Commands.safeSend(player, lang.getKey("cmds.home.teleporting", helper.getReplace()));
                    TeleportCountdown teleportCountdown = new TeleportCountdown(player, destination, homeDelay);
                    teleportCountdown.start(
                            seconds -> sendCountdownActionBar(player, seconds),
                            () -> Commands.safeSend(player, lang.getKey("msgs.tpcancelled"))
                    );
                } else {
                    Commands.safeSend(player, lang.getKey("cmds.home.missing", helper.getReplace()));
                }
            }
            case "sethome" -> {
                if (args.length != 1)
                    return false;
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("HOME", args[0]);
                if (homes.containsKey(args[0])) {
                    Commands.safeSend(player, lang.getKey("cmds.home.conflict", helper.getReplace()));
                } else if (homes.size() > config.getInt("home.max")) {
                    helper.add("MAX", Integer.toString(config.getInt("home.max")));
                    Commands.safeSend(player, lang.getKey("cmds.home.max", helper.getReplace()));
                } else {
                    org.bukkit.Location loc = player.getLocation();
                    homes.put(args[0], new Location(
                            player.getWorld().getUID(),         // World      - World UUID
                            loc.getX(), loc.getY(), loc.getZ(), // X, Y, Z    - Block position
                            loc.getYaw(), loc.getPitch()        // Yaw, Pitch - Camera angle
                    ));
                    Commands.safeSend(player, lang.getKey("cmds.home.set", helper.getReplace()));
                    helper.add("PLAYER", player.getName());
                    logger.log(lang.getKey("cmds.home.setlog", helper.getReplace()));
                }
            }
            case "delhome" -> {
                if (args.length != 1)
                    return false;
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("HOME", args[0]);
                if (homes.containsKey(args[0])) {
                    homes.remove(args[0]);
                    Commands.safeSend(player, lang.getKey("cmds.home.deleted", helper.getReplace()));
                    helper.add("PLAYER", player.getName());
                    logger.log(lang.getKey("cmds.home.deletedlog", helper.getReplace()));
                } else {
                    Commands.safeSend(player, lang.getKey("cmds.home.missing", helper.getReplace()));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return List.of();
        PlayerLocations playerLocations = cache.get(player.getUniqueId());
        if (playerLocations == null) return List.of();
        return playerLocations.getHomes().keySet().stream().toList();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        cache.put(player.getUniqueId(), storage.load(player.getUniqueId()));
        logger.log("Loaded homes for player " + player.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        storage.save(uuid, cache.get(uuid));
        cache.remove(uuid);
        logger.log("Saved homes for player " + player.getName());
    }

    private void sendCountdownActionBar(Player p, int seconds) {
        PlaceholderHelper countdownHelper = new PlaceholderHelper();
        countdownHelper.add("SECS", String.valueOf(seconds));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(lang.getKey("cmds.home.countdown", countdownHelper.getReplace())));
    }
}
