package io.github.sebminecrafter.fundamentals.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public interface FundamentalCommand {
    default boolean execute(CommandSender sender, String[] args, String label) {
        sender.sendMessage("Not implemented yet!");
        return true;
    }
    default List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else {
            return List.of(args[args.length - 1]);
        }
    }
}
