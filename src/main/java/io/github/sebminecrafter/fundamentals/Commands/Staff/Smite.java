package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Smite implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player target;
        Location loc;
        if (args.length > 1) {
            return false;
        } else if (args.length == 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                Commands.safeSend(sender, lang.getKey("msgs.offline"));
                return true;
            }
            loc = target.getLocation();
        } else {
            if (!(sender instanceof Player p)) {
                return false;
            }
            target = p;
            loc = target.getTargetBlock(null, 600).getLocation();
        }
        target.getWorld().strikeLightning(loc);
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("TARGET", target.getName());
        Commands.safeSend(sender, lang.getKey("staffcmds.smite.staff", helper.getReplace()));
        logger.log(lang.getKey("staffcmds.smite.log", helper.getReplace()));
        return true;
    }
}
