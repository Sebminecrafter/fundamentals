package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Clearchat implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        int lines = 100;
        if (args.length == 1) {
            boolean invalid = false;
            try {
                lines = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                invalid = true;
            }
            if (lines < 1) {
                invalid = true;
            }
            if (invalid) {
                sender.sendMessage(lang.getKey("msgs.invalid"));
                return true;
            }
        } else if (args.length > 1) {
            return false;
        }
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("LINES", Integer.toString(lines));
        for (int i = 0; i < lines; i++) {
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(""));
        }
        logger.log(lang.getKey("staffcmds.clearchat.log", helper.getReplace()));
        sender.sendMessage(lang.getKey("staffcmds.clearchat.staff", helper.getReplace()));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length < 1) {
            return List.of("(lines, default 100)");
        }
        return List.of();
    }
}
