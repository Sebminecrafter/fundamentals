package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

        ZonedDateTime time = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault());

        String year       = Integer.toString(time.getYear());
        String month      = Integer.toString(time.getMonthValue());
        String monthStr   = time.getMonth().toString();
        String date       = Integer.toString(time.getDayOfMonth());
        String dayOfWeek  = time.getDayOfWeek().toString();
        String hour       = Integer.toString(time.getHour());
        String twelveHour = Integer.toString(time.getHour() % 12 == 0 ? 12 : time.getHour() % 12);
        String AMPM       = time.getHour() >= 12 ? "PM" : "AM";
        String minute     = String.format("%02d", time.getMinute());
        String second     = String.format("%02d", time.getSecond());

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("player", name);
        helper.add("year", year);
        helper.add("monthnum", month);
        helper.add("monthname", monthStr);
        helper.add("date", date);
        helper.add("weekday", dayOfWeek);
        helper.add("24hour", hour);
        helper.add("12hour", twelveHour);
        helper.add("ampm", AMPM);
        helper.add("minute", minute);
        helper.add("second", second);

        Commands.safeSend(sender, lang.getKey("cmds.seen", helper.getReplace()));

        return true;
    }
}
