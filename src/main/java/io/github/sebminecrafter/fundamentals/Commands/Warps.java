package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Locations.Location;
import io.github.sebminecrafter.fundamentals.IO.Locations.Warp;
import io.github.sebminecrafter.fundamentals.IO.Locations.WarpStorage;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.IO.TeleportCountdown;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

import static io.github.sebminecrafter.fundamentals.Main.*;

public class Warps implements FundamentalCommand {
    private WarpStorage storage = null;
    private final Map<String, Warp> warps = new HashMap<>();

    public Warps(JavaPlugin plugin) {
        Path dataFolder = Path.of(plugin.getDataFolder().toString());

        try {
            this.storage = new WarpStorage(dataFolder);
            // Load all persisted warps into memory on startup.
            warps.putAll(storage.load());
            logger.log("Loaded warps storage (" + warps.size() + " warps).");
        } catch (IOException e) {
            logger.logBoth(Level.SEVERE, "Failed to load warps storage:");
            logger.logBoth(Level.SEVERE,
                    e.getMessage()
                            + " " +
                            Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        if (storage == null) {
            Commands.safeSend(sender, lang.getKey("cmds.warp.error"));
            return true;
        }
        switch (label.toLowerCase()) {
            case "listwarps", "warps" -> {
                if (args.length != 0)
                    return false;
                if (!warps.isEmpty()) {
                    StringBuilder message = new StringBuilder(lang.getKey("cmds.warp.list"));
                    boolean i = false;
                    for (String warp : warps.keySet()) {
                        String text = warp;
                        if (i)
                            text = ", " + text;
                        else
                            i = true;
                        message.append(text);
                    }
                    Commands.safeSend(player, message.toString());
                } else {
                    Commands.safeSend(player, lang.getKey("cmds.warp.nowarps"));
                }
            }
            case "warp" -> {
                if (args.length != 1)
                    return false;
                Warp warp = warps.get(args[0]);
                PlaceholderHelper helper = new PlaceholderHelper();
                int warpDelay = config.getInt("warp.delay");

                helper.add("HOME", args[0]);
                helper.add("SECS", Integer.toString(warpDelay));
                if (warp != null) {
                    World world = Bukkit.getWorld(warp.world());
                    if (world == null) {
                        Commands.safeSend(player, lang.getKey("cmds.warp.worldmissing", helper.getReplace()));
                        return true;
                    }
                    org.bukkit.Location destination = new org.bukkit.Location(world, warp.x(), warp.y(), warp.z(),
                            warp.yaw(), warp.pitch());

                    Commands.safeSend(player, lang.getKey("cmds.warp.teleporting", helper.getReplace()));
                    TeleportCountdown teleportCountdown = new TeleportCountdown(player, destination, warpDelay);
                    teleportCountdown.start(
                            seconds -> sendCountdownActionBar(player, seconds),
                            () -> Commands.safeSend(player, lang.getKey("msgs.tpcancelled"))
                    );
                } else {
                    Commands.safeSend(player, lang.getKey("cmds.warp.missing", helper.getReplace()));
                }
            }
            case "setwarp" -> {
                if (args.length != 1)
                    return false;
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("WARP", args[0]);
                if (warps.containsKey(args[0])) {
                    Commands.safeSend(player, lang.getKey("cmds.warp.conflict", helper.getReplace()));
                } else {
                    org.bukkit.Location loc = player.getLocation();
                    warps.put(args[0], new Warp(
                            new Location(
                                    player.getWorld().getUID(),         // World      - World UUID
                                    loc.getX(), loc.getY(), loc.getZ(), // X, Y, Z    - Block position
                                    loc.getYaw(), loc.getPitch()        // Yaw, Pitch - Camera angle
                            ), player.getUniqueId()));
                    saveWarps();
                    Commands.safeSend(player, lang.getKey("cmds.warp.set", helper.getReplace()));
                    helper.add("PLAYER", player.getName());
                    logger.log(lang.getKey("cmds.warp.setlog", helper.getReplace()));
                }
            }
            case "delwarp" -> {
                if (args.length != 1)
                    return false;
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("HOME", args[0]);
                if (warps.containsKey(args[0])) {
                    warps.remove(args[0]);
                    saveWarps();
                    Commands.safeSend(player, lang.getKey("cmds.warp.deleted", helper.getReplace()));
                    helper.add("PLAYER", player.getName());
                    logger.log(lang.getKey("cmds.warp.deletedlog", helper.getReplace()));
                } else {
                    Commands.safeSend(player, lang.getKey("cmds.warp.missing", helper.getReplace()));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return warps.keySet().stream().toList();
    }

    private void saveWarps() {
        if (storage == null) return;
        try {
            storage.save(warps);
        } catch (RuntimeException e) {
            logger.logBoth(Level.SEVERE, "Failed to save warps: " + e.getMessage());
        }
    }

    private void sendCountdownActionBar(Player p, int seconds) {
        PlaceholderHelper countdownHelper = new PlaceholderHelper();
        countdownHelper.add("SECS", String.valueOf(seconds));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(lang.getKey("cmds.warp.countdown", countdownHelper.getReplace())));
    }
}
