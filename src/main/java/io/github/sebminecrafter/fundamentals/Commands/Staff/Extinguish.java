package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Extinguish implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player target;
        if (args.length > 1) {
            return false;
        } else if (args.length == 1) {
            target = Bukkit.getPlayerExact(args[0]);
        } else {
            if (!(sender instanceof Player p)) {
                return false;
            }
            target = p;
        }
        if (target == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("TARGET", target.getName());

        Commands.safeSend(sender, lang.getKey("staffcmds.extinguish.staff", helper.getReplace()));
        logger.log(lang.getKey("staffcmds.extinguish.log", helper.getReplace()));

        target.setFireTicks(0);

        return true;
    }
}
