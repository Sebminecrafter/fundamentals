package io.github.sebminecrafter.fundamentals.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Homes implements FundamentalCommand {
    private final JavaPlugin plugin;

    public Homes(JavaPlugin plugin) {
        this.plugin = plugin;

        // Load homes
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        sender.sendMessage(ChatColor.RED + "Not yet implemented!");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        return null;
    }
}
