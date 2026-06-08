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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Freeze implements FundamentalCommand {
    private final Lang lang;
    private final Logging logger;
    private final Map<UUID, Location> frozenPlayers;
    private final BukkitTask task;

    public Freeze(JavaPlugin plugin) {
        this.lang = Main.lang;
        this.logger = Main.logger;
        this.frozenPlayers = new HashMap<>();
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, this::runFreeze, 0L, 5L);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length != 1) {
            return false;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage();
            return true;
        }

        UUID uuid = target.getUniqueId();

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("VICTIM", target.getName());

        List<List<String>> replace = helper.getReplace();

        if (frozenPlayers.containsKey(uuid)) {
            frozenPlayers.remove(uuid);
            logger.log(lang.getKey("staffcmds.freeze.log.stop", replace));
            sender.sendMessage(lang.getKey("staffcmds.freeze.staff.stop", replace));
            target.sendMessage(lang.getKey("staffcmds.freeze.player.stop", replace));
        } else {
            frozenPlayers.put(uuid, target.getLocation());
            logger.log(lang.getKey("staffcmds.freeze.log.start", replace));
            sender.sendMessage(lang.getKey("staffcmds.freeze.staff.start", replace));
            target.sendMessage(lang.getKey("staffcmds.freeze.player.start", replace));
        }
        return true;
    }

    private void runFreeze() {
        for (UUID uuid : frozenPlayers.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            Location location = frozenPlayers.get(uuid);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(lang.getKey("staffcmds.freeze.player.actionbar")));
            player.addPotionEffect(PotionEffectType.SLOWNESS.createEffect(20, 255));
            player.teleport(location);
        }
    }

    public void cleanup() {
        frozenPlayers.clear();
        task.cancel();
    }
}
