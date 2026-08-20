package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Socialspy implements FundamentalCommand {
    private final List<UUID> spyingPlayers;

    public Socialspy() {
        spyingPlayers = new ArrayList<>();
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
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());
        if (spyingPlayers.contains(player.getUniqueId())) {
            logger.log(lang.getKey("staffcmds.socialspy.disable.log", helper.getReplace()));
            Commands.safeSend(player, lang.getKey("staffcmds.socialspy.disable.staff", helper.getReplace()));
            spyingPlayers.remove(player.getUniqueId());
        } else {
            logger.log(lang.getKey("staffcmds.socialspy.enable.log", helper.getReplace()));
            Commands.safeSend(player, lang.getKey("staffcmds.socialspy.enable.staff", helper.getReplace()));
            spyingPlayers.add(player.getUniqueId());
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    public void sendToSpyingPlayers(String message) {
        for (UUID uuid : spyingPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                Commands.safeSend(player, message);
            } else {
                spyingPlayers.remove(uuid);
            }
        }
    }
}
