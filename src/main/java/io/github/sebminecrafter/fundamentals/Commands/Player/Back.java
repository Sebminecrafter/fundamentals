package io.github.sebminecrafter.fundamentals.Commands.Player;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.Cooldowns;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.IO.TeleportCountdown;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import static io.github.sebminecrafter.fundamentals.Main.config;
import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Back implements FundamentalCommand, Listener {
    private static final String PERM_ON_DEATH = "fundamentals.player.back.ondeath";

    private final Map<UUID, Location> lastLocations = new HashMap<>();

    public Back(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        logger.log("Registered events for /back");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        if (args.length != 0) {
            return false;
        }

        int cooldown = config.getInt("back.cooldown");
        long remaining = Cooldowns.remainingSeconds("back", player.getUniqueId(), cooldown);
        if (remaining > 0) {
            PlaceholderHelper cooldownHelper = new PlaceholderHelper();
            cooldownHelper.add("secs", Long.toString(remaining));
            Commands.safeSend(player, lang.getKey("msgs.cooldown", cooldownHelper.getReplace()));
            return true;
        }

        Location previous = lastLocations.get(player.getUniqueId());
        if (previous == null) {
            Commands.safeSend(player, lang.getKey("cmds.back.none"));
            return true;
        }
        if (!previous.isWorldLoaded() || previous.getWorld() == null) {
            lastLocations.remove(player.getUniqueId());
            Commands.safeSend(player, lang.getKey("cmds.back.world-unloaded"));
            return true;
        }

        int countdownTime = config.getInt("back.delay");
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("player", player.getName());
        helper.add("secs", Integer.toString(countdownTime));

        Cooldowns.start("back", player.getUniqueId(), cooldown);
        Commands.safeSend(player, lang.getKey("cmds.back.teleporting", helper.getReplace()));
        new TeleportCountdown(player, previous, countdownTime).start(
                seconds -> sendCountdownActionBar(player, seconds),
                () -> Commands.safeSend(player, lang.getKey("msgs.tpcancelled"))
        );
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("fundamentals.player.back")) return;
        if (ignoredCauses().contains(event.getCause())) return;

        Location from = event.getFrom();
        Location to = event.getTo();
        // Ignore small teleports
        if (to != null && from.getWorld() == to.getWorld() && from.distanceSquared(to) < 3.0) return;

        lastLocations.put(player.getUniqueId(), from.clone());
    }

    /** Remember where the player died, if enabled and permitted. */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        if (!config.getBoolean("back.on-death")) return;
        Player player = event.getEntity();
        if (!player.hasPermission("fundamentals.player.back")
                || !player.hasPermission(PERM_ON_DEATH)) return;

        lastLocations.put(player.getUniqueId(), player.getLocation().clone());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastLocations.remove(event.getPlayer().getUniqueId());
    }

    private Set<PlayerTeleportEvent.TeleportCause> ignoredCauses() {
        Set<PlayerTeleportEvent.TeleportCause> causes = EnumSet.noneOf(PlayerTeleportEvent.TeleportCause.class);
        for (String name : config.getStringList("back.ignored-causes")) {
            String key = name.trim().toUpperCase(java.util.Locale.ROOT);

            // CHORUS_FRUIT -> CONSUMABLE_EFFECT in Paper >1.21.5
            if (key.equals("CHORUS_FRUIT")) {
                try {
                    PlayerTeleportEvent.TeleportCause.valueOf("CHORUS_FRUIT");
                } catch (IllegalArgumentException e) {
                    key = "CONSUMABLE_EFFECT";
                }
            }

            try {
                causes.add(PlayerTeleportEvent.TeleportCause.valueOf(key));
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Config warning: Unknown teleport cause (in back.ignored-causes) '" + name + "'!");
            }
        }
        return causes;
    }

    private void sendCountdownActionBar(Player p, int seconds) {
        PlaceholderHelper countdownHelper = new PlaceholderHelper();
        countdownHelper.add("secs", String.valueOf(seconds));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacy(lang.getKey("cmds.back.countdown", countdownHelper.getReplace())));
    }
}
