package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Tpo implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player executor)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        } else if (args.length != 1) {
            return false;
        }
        OfflinePlayer target = null;
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String name = offlinePlayer.getName();
            if (name == null) {
                continue;
            }
            if (name.equalsIgnoreCase(args[0])) {
                target = offlinePlayer;
            }
        }
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", executor.getName());
        helper.add("VICTIM", args[0]);
        List<List<String>> replace = helper.getReplace();
        if (target == null || target.getName() == null) {
            Commands.safeSend(sender, lang.getKey("staffcmds.tpo.error", replace));
            return true;
        }
        Location targetLocation = target.getLocation();
        if (targetLocation == null) {
            Commands.safeSend(sender, lang.getKey("staffcmds.tpo.error", replace));
            return true;
        }
        executor.teleport(targetLocation);
        Commands.safeSend(sender, lang.getKey("staffcmds.tpo.staff", replace));
        logger.log(lang.getKey("staffcmds.tpo.log", replace));
        return true;
    }
}
