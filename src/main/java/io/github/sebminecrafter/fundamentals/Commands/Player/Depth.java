package io.github.sebminecrafter.fundamentals.Commands.Player;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Depth implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player p)) {
                return false;
            }
            target = p;
        } else if (args.length == 1) {
            target = Bukkit.getPlayerExact(args[0]);
        } else {
            return false;
        }

        if (target == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }

        int depth = target.getLocation().getBlockY() - target.getWorld().getSeaLevel();

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("DEPTH", Integer.toString(depth));

        Commands.safeSend(sender, lang.getKey("cmds.depth", helper.getReplace()));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
