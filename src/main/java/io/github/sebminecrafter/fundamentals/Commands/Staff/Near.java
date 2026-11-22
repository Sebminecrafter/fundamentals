package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.sebminecrafter.fundamentals.Main.*;

public class Near implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        int distance = config.getInt("near.distance");
        Player player;
        if (args.length == 0 && sender instanceof Player p) {
            player = p;
        } else if (args.length == 1 && sender instanceof Player p) {
            player = p;
            try {
                distance = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                Commands.safeSend(p, lang.getKey("msgs.invalid"));
                return true;
            }
        } else if (args.length == 2) {
            player = Bukkit.getPlayerExact(args[1]);
            try {
                distance = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                Commands.safeSend(sender, lang.getKey("msgs.invalid"));
                return true;
            }
        } else {
            return false;
        }

        if (player == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }

        List<String> players = player.getNearbyEntities(distance, distance, distance).stream()
                .filter(e -> e instanceof Player)
                .map(Entity::getName)
                .toList();

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("player", sender.getName());
        helper.add("count", Integer.toString(players.size()));
        helper.add("players", String.join(", ", players));

        Commands.safeSend(sender, lang.getKey("staffcmds.near.staff", helper.getReplace()));
        logger.log(lang.getKey("staffcmds.near.log", helper.getReplace()));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return List.of("[distance] [player]");
        } else if (args.length == 1) {
            String input = args[0];

            if (!input.matches("\\d*")) {
                return List.of();
            }

            long start = input.isEmpty() ? 0 : Long.parseLong(input) * 10;
            long end = start + 9;

            return java.util.stream.LongStream.rangeClosed(start, end)
                    .mapToObj(String::valueOf)
                    .toList();
        } else if (args.length == 2) {
            return Bukkit.matchPlayer(args[1]).stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
