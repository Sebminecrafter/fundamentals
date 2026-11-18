package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.IO.Time;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.util.Date;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Seen implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length != 1) return false;
        long millis;
        String name;
        if (Bukkit.getPlayerExact(args[0]) != null) {
            millis = Instant.now().toEpochMilli();
            name = args[0];
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            millis = target.getLastPlayed();
            name = target.getName();
        }

        if (millis == 0) {
            Commands.safeSend(sender, lang.getKey("msgs.playernotfound"));
            return true;
        }

        String time_string = new Time(new Date(millis)).toString();

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("player", name);
        helper.add("time", time_string);

        Commands.safeSend(sender, lang.getKey("cmds.seen", helper.getReplace()));

        return true;
    }
}
