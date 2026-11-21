package io.github.sebminecrafter.fundamentals.Commands.Player;

import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Exit implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (sender instanceof Player player) {
            player.kickPlayer(lang.getKey("cmds.exit.kick"));
            return true;
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(lang.getKey("cmds.exit.stop"));
            Bukkit.getServer().shutdown();
            return true;
        }
        sender.sendMessage(lang.getKey("cmds.exit.other"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
