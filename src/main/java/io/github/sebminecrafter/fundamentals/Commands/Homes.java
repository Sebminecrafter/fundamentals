package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Homes.Home;
import io.github.sebminecrafter.fundamentals.IO.Homes.JsonHomeStorage;
import io.github.sebminecrafter.fundamentals.IO.Homes.PlayerHomes;
import io.github.sebminecrafter.fundamentals.IO.Config;
import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.IO.TeleportCountdown;
import io.github.sebminecrafter.fundamentals.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class Homes implements FundamentalCommand, Listener {
    private JsonHomeStorage storage = null;
    private final Logging logger;
    private final Lang lang;
    private final int homeDelay;
    private final Map<UUID, PlayerHomes> cache = new HashMap<>();

    public Homes(JavaPlugin plugin) {
        this.logger = Main.logger;
        this.lang = Main.lang;
        Config config = Main.config;
        this.homeDelay = config.getInt("home.delay");

        Path folder = Path.of(plugin.getDataFolder().toString(), "homes");
        try {
            logger.log("Loaded home storage.");
            this.storage = new JsonHomeStorage(folder);
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
            sender.sendMessage(lang.getKey("msgs.playeronly"));
            return true;
        }
        if (storage == null) {
            sender.sendMessage(lang.getKey("cmds.home.error"));
            return true;
        }
        Map<String, Home> homes = cache.get(player.getUniqueId()).getHomes();
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
                    player.sendMessage(message.toString());
                } else {
                    player.sendMessage(lang.getKey("cmds.home.nohomes"));
                }
            }
            case "home" -> {
                if (args.length != 1)
                    return false;
                Home home = homes.get(args[0]);
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("HOME", args[0]);
                if (home != null) {
                    World world = Bukkit.getWorld(home.world());
                    if (world == null) {
                        player.sendMessage(lang.getKey("cmds.home.worldmissing", helper.getReplace()));
                        return true;
                    }
                    Location destination = new Location(world, home.x(), home.y(), home.z(),
                            home.yaw(), home.pitch());

                    player.sendMessage(lang.getKey("cmds.home.teleporting", helper.getReplace()));
                    TeleportCountdown teleportCountdown = new TeleportCountdown(player, destination, homeDelay);
                    teleportCountdown.start(
                            seconds -> sendCountdownActionBar(player, seconds),
                            () -> player.sendMessage(lang.getKey("msgs.tpcancelled"))
                    );
                } else {
                    player.sendMessage(lang.getKey("cmds.home.missing", helper.getReplace()));
                }
            }
            case "sethome" -> {
                if (args.length != 1)
                    return false;
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("HOME", args[0]);
                if (homes.containsKey(args[0])) {
                    player.sendMessage(lang.getKey("cmds.home.conflict", helper.getReplace()));
                } else {
                    Location loc = player.getLocation();
                    homes.put(args[0], new Home(
                        player.getWorld().getUID(),         // World      - World UUID
                        loc.getX(), loc.getY(), loc.getZ(), // X, Y, Z    - Block position
                        loc.getYaw(), loc.getPitch()        // Yaw, Pitch - Camera angle
                    ));
                    player.sendMessage(lang.getKey("cmds.home.set", helper.getReplace()));
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
                    player.sendMessage(lang.getKey("cmds.home.deleted", helper.getReplace()));
                    helper.add("PLAYER", player.getName());
                    logger.log(lang.getKey("cmds.home.deletedlog", helper.getReplace()));
                } else {
                    player.sendMessage(lang.getKey("cmds.home.missing", helper.getReplace()));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return List.of();
        Map<String, Home> homes = cache.get(player.getUniqueId()).getHomes();
        return homes.keySet().stream().toList();
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
