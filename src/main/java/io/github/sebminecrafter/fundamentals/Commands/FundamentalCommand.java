package io.github.sebminecrafter.fundamentals.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface FundamentalCommand {
    default boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("Not implemented yet!");
        return true;
    }
    default String[]  tabComplete(CommandSender sender, String[] args) {
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
