package io.github.sebminecrafter.fundamentals.Commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public class Motd implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        io.github.sebminecrafter.fundamentals.Chat.Motd.message(sender);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
