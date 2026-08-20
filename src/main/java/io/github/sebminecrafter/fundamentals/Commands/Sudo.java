package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Sudo implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length < 2) {
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }

        boolean first = true;
        StringBuilder command = new StringBuilder();
        for (String arg : args) {
            if (first) {
                first = false;
                continue;
            }
            if (!command.toString().isBlank()) {
                command.append(" ");
            }
            command.append(arg);
        }

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("VICTIM", target.getName());
        helper.add("CMD", command.toString());

        logger.log(lang.getKey("staffcmds.sudo.log", helper.getReplace()));
        Commands.safeSend(sender, lang.getKey("staffcmds.sudo.staff", helper.getReplace()));
        Bukkit.dispatchCommand(target, command.toString());

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return FundamentalCommand.super.tabComplete(sender, args);
    }
}
