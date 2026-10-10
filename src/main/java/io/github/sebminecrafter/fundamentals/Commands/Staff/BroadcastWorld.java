package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.FundamentalSounds;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class BroadcastWorld implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length < 2) return false;
        World world = Bukkit.getWorld(args[0]);
        if (world == null) return false;
        List<String> words = new ArrayList<>(Arrays.stream(args.clone()).toList());
        words.removeFirst();
        String message = String.join(" ", words);
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("MSG", message);
        helper.add("WORLD", world.getName());
        logger.logBoth(lang.getKey("staffcmds.broadcastworld.log", helper.getReplace()));
        Commands.safeSend(sender, lang.getKey("staffcmds.broadcastworld.staff", helper.getReplace()));
        Bukkit.broadcastMessage(lang.getKey("staffcmds.broadcastworld.player", helper.getReplace()));
        for (Player player : world.getPlayers()) {
            FundamentalSounds.tPSFCSimpler(player, "sounds.broadcastworld");
            player.sendTitle(lang.getKey("staffcmds.broadcastworld.player", helper.getReplace()), null, 20, 60, 20);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > 1) {
            return List.of(args[args.length - 1]);
        } else {
            List<String> worlds = new ArrayList<>(3);
            Bukkit.getWorlds().forEach(world -> worlds.add(world.getName()));
            return worlds;
        }
    }
}
