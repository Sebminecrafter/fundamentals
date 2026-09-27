package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Feed implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player player;
        if (args.length > 1) {
            return false;
        } else if (args.length == 1) {
            player = Bukkit.getPlayerExact(args[0]);
        } else {
            if (!(sender instanceof Player)) return false;
            player = (Player) sender;
        }
        if (player == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }
        player.setFoodLevel(20);
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("VICTIM", player.getName());
        Commands.safeSend(sender, lang.getKey("staffcmds.feed.staff", helper.getReplace()));
        logger.log(lang.getKey("staffcmds.feed.log", helper.getReplace()));
        return true;
    }
}
