package io.github.sebminecrafter.fundamentals.Commands;

import org.bukkit.command.CommandSender;

public interface FundamentalCommand {
    default boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("Not implemented yet!");
        return true;
    }
    default String[] tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
