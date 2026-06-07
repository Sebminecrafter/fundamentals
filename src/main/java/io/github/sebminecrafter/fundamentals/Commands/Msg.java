package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Msg implements FundamentalCommand {
    private final Lang lang;
    private final Logging logger;

    public Msg() {
        this.lang = Main.lang;
        this.logger = Main.logger;
    }

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
        Player receiver = Bukkit.getPlayer(args[0]);
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("OTHER", args[0]);
        helper.add("MSG", message.toString());
        if (receiver == null) {
            sender.sendMessage(lang.getKey("msgs.offline", helper.getReplace()));
            return true;
        }
        receiver.sendMessage(lang.getKey("cmds.msg.receive", helper.getReplace()));
        sender.sendMessage(lang.getKey("cmds.msg.send", helper.getReplace()));
        logger.log(lang.getKey("cmds.msg.log", helper.getReplace()));
        return true;
    }
}
