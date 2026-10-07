package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Burn implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length > 2 || args.length < 1) return false;
        int secs = 5;
        if (args.length > 1) {
            try {
                secs = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }

        target.setFireTicks(target.getFireTicks() + (secs * 20));

        return true;
    }
}
