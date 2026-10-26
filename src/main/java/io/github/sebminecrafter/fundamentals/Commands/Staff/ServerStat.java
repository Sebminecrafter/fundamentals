package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;

// TODO: Finish

public class ServerStat implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        PlaceholderHelper helper = new PlaceholderHelper();

        helper.add("ONLINE", Integer.toString(Bukkit.getOnlinePlayers().size()));
        helper.add("MAX",    Integer.toString(Bukkit.getMaxPlayers()));

        Commands.safeSend(sender, lang.getKey("cmds.serverstat", helper.getReplace()));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
