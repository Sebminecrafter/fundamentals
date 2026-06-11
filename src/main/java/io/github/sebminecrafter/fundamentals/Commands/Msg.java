package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.Chat.GlobalChat;
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
    private final Ignore ignore;
    private final GlobalChat chat;

    public Msg(Ignore ignore) {
        this.lang = Main.lang;
        this.logger = Main.logger;
        this.ignore = ignore;
        this.chat = Main.chat;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.getKey("msgs.playeronly"));
            return true;
        }
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
        helper.add("OTHER", args[0]);
        helper.add("MSG", message.toString());
        if (receiver == null) {
            sender.sendMessage(lang.getKey("msgs.offline", helper.getReplace()));
            return true;
        }
        if (ignore.isIgnoring(receiver.getUniqueId(), player.getUniqueId())) {
            sender.sendMessage(lang.getKey("msgs.ignored"));
            return true;
        }
        if (chat.notAllowed(message.toString(), player)) {
            sender.sendMessage(lang.getKey("chat.disallowed"));
            return true;
        }
        receiver.sendMessage(lang.getKey("cmds.msg.receive", helper.getReplace()));
        sender.sendMessage(lang.getKey("cmds.msg.send", helper.getReplace()));
        logger.log(lang.getKey("cmds.msg.log", helper.getReplace()));
        return true;
    }
}
