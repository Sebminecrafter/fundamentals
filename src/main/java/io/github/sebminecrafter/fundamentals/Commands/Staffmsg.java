package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Staffmsg implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length < 2) {
            return false;
        }
        StringBuilder message = new StringBuilder();
        for (int i=1;i<args.length;i++) {
            message.append(args[i]);
            message.append(" ");
        }
        Player receiver = Bukkit.getPlayerExact(args[0]);
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("VICTIM", args[0]);
        helper.add("MSG", message.toString());
        if (receiver == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline", helper.getReplace()));
            return true;
        }
        Commands.safeSend(receiver, lang.getKey("staffcmds.staffmsg.player", helper.getReplace()));
        Commands.safeSend(sender, lang.getKey("staffcmds.staffmsg.staff", helper.getReplace()));
        logger.log(lang.getKey("staffcmds.staffmsg.log", helper.getReplace()));
        return true;
    }
}
