package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Fly implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player player;
        if (args.length == 0) {
            if (!(sender instanceof Player p)) {
                Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
                return true;
            }
            player = p;
        } else if (args.length == 1) {
            player = Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                Commands.safeSend(sender, lang.getKey("msgs.offline"));
                return true;
            }
        } else {
            return false;
        }
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());
        if (player.getAllowFlight()) {
            Commands.safeSend(player, lang.getKey("staffcmds.fly.exit.staff", helper.getReplace()));
            logger.log(lang.getKey("staffcmds.fly.exit.log", helper.getReplace()));
            player.setFlying(false);
            player.setAllowFlight(false);
        } else {
            Commands.safeSend(player, lang.getKey("staffcmds.fly.enter.staff", helper.getReplace()));
            logger.log(lang.getKey("staffcmds.fly.enter.log", helper.getReplace()));
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        return true;
    }
}
