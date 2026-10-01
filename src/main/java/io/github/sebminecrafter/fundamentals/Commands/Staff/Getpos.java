package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Getpos implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length != 1) return false;
        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }
        Location location = player.getLocation();
        String world = player.getWorld().getName();
        String x = Double.toString(location.getX());
        String y = Double.toString(location.getY());
        String z = Double.toString(location.getZ());
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("VICTIM", player.getName());
        helper.add("PX", x);
        helper.add("PY", y);
        helper.add("PZ", z);
        helper.add("PWORLD", world);

        Commands.safeSend(sender, lang.getKey("staffcmds.getpos", helper.getReplace()));
        return true;
    }
}
