package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class ServerStat implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        PlaceholderHelper helper = new PlaceholderHelper();

        helper.add("ONLINE",   Integer.toString(Bukkit.getOnlinePlayers().size()));
        helper.add("MAX",      Integer.toString(Bukkit.getMaxPlayers()));
        helper.add("VERSION",  Bukkit.getVersion());
        helper.add("MEMTOTAL", Long.toString(Runtime.getRuntime().totalMemory()));
        helper.add("MEMFREE",  Long.toString(Runtime.getRuntime().freeMemory()));
        helper.add("MEMUSED",  Long.toString(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        helper.add("WORLDS",   Integer.toString(Bukkit.getWorlds().size()));
        helper.add("UPTIME",   Integer.toString((int)((System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()) / 1000)));

        Commands.safeSend(sender, lang.getKey("staffcmds.serverstat", helper.getReplace()));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
