package io.github.sebminecrafter.fundamentals.Commands.Privileged;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Rest implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player target;
        if (args.length > 2) return false;
        if (args.length == 1) {
            target = Bukkit.getPlayerExact(args[0]);
        } else {
            if (!(sender instanceof Player p)) {
                Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
                return true;
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

        Commands.safeSend(sender, lang.getKey("staffcmds.rest.staff"));
        logger.log(lang.getKey("staffcmds.rest.log"));

        target.setStatistic(Statistic.TIME_SINCE_REST, 0);
        return true;
    }
}
