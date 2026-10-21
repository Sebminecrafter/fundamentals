package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Top implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player p)) return false;
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
        World targetWorld = target.getWorld();
        Block block = targetWorld.getHighestBlockAt(target.getLocation());
        Block above_1 = block.getLocation().add(0, 1, 0).getBlock();
        Block above_2 = block.getLocation().add(0, 2, 0).getBlock();

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("TARGET", target.getName());
        helper.add("BX", Integer.toString(above_1.getX()));
        helper.add("BY", Integer.toString(above_1.getY()));
        helper.add("BZ", Integer.toString(above_1.getZ()));

        if (above_1.isPassable() && above_2.isPassable()) {
            target.teleport(above_1.getLocation());
            logger.log(lang.getKey("staffcmds.top.log", helper.getReplace()));
            Commands.safeSend(sender, lang.getKey("staffcmds.top.staff", helper.getReplace()));
            return true;
        }
        Commands.safeSend(sender, lang.getKey("staffcmds.top.fail", helper.getReplace()));
        return true;
    }
}
