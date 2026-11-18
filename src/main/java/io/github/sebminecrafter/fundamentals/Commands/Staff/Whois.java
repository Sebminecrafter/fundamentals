package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.IO.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Whois implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length != 1) return false;

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }

        PlayerInfo playerInfo = new PlayerInfo(target);
        PlaceholderHelper helper = new PlaceholderHelper();

        Location loc = target.getLocation();

        String position =
                loc.getBlockX() + ", " +
                loc.getBlockY() + ", " +
                loc.getBlockZ() + ", in world: " +
                target.getWorld().getName();

        helper.add("target", playerInfo.getName());
        helper.add("ip", playerInfo.getIP());
        helper.add("port", Integer.toString(playerInfo.getPort()));
        helper.add("pos", position);

        Commands.safeSend(sender, lang.getKey("staffcmds.whois", helper.getReplace()));

        return true;
    }
}
