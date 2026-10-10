package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Kickall implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers().stream().toList());
        if (sender instanceof Player player) {
            players.remove(player);
        }

        String message = "Kicked by an operator";

        if (args.length > 0) {
            message = String.join(" ", args);
        }

        for (Player p : players) {
            p.kickPlayer(message);
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
