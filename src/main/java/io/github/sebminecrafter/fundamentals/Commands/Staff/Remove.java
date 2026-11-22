package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Remove implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        if (args.length > 1) return false;
        int distance = 10;
        if (args.length == 1) {
            try {
                distance = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                Commands.safeSend(sender, lang.getKey("msgs.invalid"));
            }
        }
        int count = 0;
        for (Entity entity : player.getNearbyEntities(distance, distance, distance)) {
            if (entity instanceof Player) continue;
            entity.remove();
            count++;
        }
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("player", player.getName());
        helper.add("entities", Integer.toString(count));
        logger.log(lang.getKey("staffcmds.remove.log", helper.getReplace()));
        Commands.safeSend(player, lang.getKey("staffcmds.remove.staff", helper.getReplace()));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return List.of("[distance]");
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
        }
        return List.of();
    }
}
