package io.github.sebminecrafter.fundamentals.Commands.Player;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static io.github.sebminecrafter.fundamentals.Main.*;

public class Ignore implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length != 1) {
            return false;
        }
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        Player other = Bukkit.getPlayerExact(args[0]);
        if (other == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        } else if (player == other) {
            Commands.safeSend(sender, lang.getKey("msgs.self"));
            return true;
        }
        UUID playerUUID = player.getUniqueId();
        UUID otherUUID = other.getUniqueId();
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("player", sender.getName());
        helper.add("other", other.getName());
        if (isIgnoring(playerUUID, otherUUID)) {
            playerData.get(playerUUID).unignore(otherUUID);
            logger.log(lang.getKey("cmds.ignore.unignore.log", helper.getReplace()));
            Commands.safeSend(player, lang.getKey("cmds.ignore.unignore.send", helper.getReplace()));
        } else {
            playerData.get(playerUUID).ignore(otherUUID);
            logger.log(lang.getKey("cmds.ignore.ignore.log", helper.getReplace()));
            Commands.safeSend(player, lang.getKey("cmds.ignore.ignore.send", helper.getReplace()));
        }
        if (!playerData.save(playerUUID)) {
            Commands.safeSend(sender, lang.getKey("msgs.fileerror"));
        }
        return true;
    }

    public boolean isIgnoring(UUID player, UUID other) {
        return playerData.get(player).isIgnoring(other);
    }
}
