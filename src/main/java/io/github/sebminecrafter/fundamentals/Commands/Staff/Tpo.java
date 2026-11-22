package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.IO.PlayerInfo;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Tpo implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player executor)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        } else if (args.length != 1) {
            return false;
        }
        Optional<OfflinePlayer> targetOptional = PlayerInfo.getOfflinePlayer(args[0]);

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("player", executor.getName());

        if (targetOptional.isEmpty()) {
            Commands.safeSend(sender, lang.getKey("msgs.playernotfound", helper.getReplace()));
            return true;
        }

        OfflinePlayer target = targetOptional.get();

        helper.add("victim", target.getName());
        Location targetLocation = target.getLocation();
        if (targetLocation == null) {
            Commands.safeSend(sender, lang.getKey("msgs.playernotfound", helper.getReplace()));
            return true;
        }
        // prevent unloaded world
        if (targetLocation.getWorld() == null) {
            Commands.safeSend(sender, lang.getKey("msgs.generalerror"));
            return true;
        }
        executor.teleport(targetLocation);
        Commands.safeSend(sender, lang.getKey("staffcmds.tpo.staff", helper.getReplace()));
        logger.log(lang.getKey("staffcmds.tpo.log", helper.getReplace()));
        return true;
    }
}
