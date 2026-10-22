package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Bottom implements FundamentalCommand {
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

        Location startLocation = target.getLocation().clone();

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("TARGET", target.getName());

        Block block = null;

        for (int i = targetWorld.getMinHeight(); i < targetWorld.getMaxHeight(); i++) {
            Block testBlock = new Location(targetWorld, startLocation.getX(), i, startLocation.getZ()).getBlock();
            Block above_1 = testBlock.getLocation().add(0, 1, 0).getBlock();
            Block above_2 = testBlock.getLocation().add(0, 2, 0).getBlock();

            if (above_1.isPassable() && above_2.isPassable()) {
                block = above_1;
                break;
            }
        }

        if (block == null) {
            Commands.safeSend(sender, lang.getKey("staffcmds.bottom.fail", helper.getReplace()));
            return true;
        }

        helper.add("BX", Integer.toString(block.getX()));
        helper.add("BY", Integer.toString(block.getY()));
        helper.add("BZ", Integer.toString(block.getZ()));

        target.teleport(block.getLocation());
        logger.log(lang.getKey("staffcmds.bottom.log", helper.getReplace()));
        Commands.safeSend(sender, lang.getKey("staffcmds.bottom.staff", helper.getReplace()));
        return true;
    }
}
