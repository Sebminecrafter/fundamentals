package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static org.bukkit.Bukkit.getServer;

public class Msg implements FundamentalCommand {
    private final Lang lang;

    public Msg() {
        this.lang = Main.lang;
    }

    private void log(String a) {getServer().getLogger().info(a);}

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        log(Arrays.toString(args));
        if (args.length < 2) {
            return false;
        }
        StringBuilder message = new StringBuilder();
        for (int i=1;i<args.length;i++) {
            message.append(args[i]);
            message.append(" ");
        }
        log(message.toString());
        Player receiver = Bukkit.getPlayer(args[0]);
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("OTHER", args[0]);
        helper.add("MSG", message.toString());
        if (receiver == null) {
            sender.sendMessage(lang.getKey("cmds.msg.offline", helper.getReplace()));
            return true;
        }
        receiver.sendMessage(lang.getKey("cmds.msg.recieve", helper.getReplace()));
        sender.sendMessage(lang.getKey("cmds.msg.send", helper.getReplace()));
        return true;
    }

    @Override
    public String[]  tabComplete(CommandSender sender, String[] args) {
        String[] strings;
        if (args.length < 2) {
            Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            strings = new String[onlinePlayers.length];
            for (int i=0;i<onlinePlayers.length;i++) {
                strings[i] = onlinePlayers[i].getName();
            }
        } else {
            strings = new String[1];
            strings[0] = args[args.length-1];
        }
        return strings;
    }
}
