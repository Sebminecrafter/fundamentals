package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Tpo implements FundamentalCommand {
    private final Lang lang;
    private final Logging logger;

    public Tpo() {
        this.lang = Main.lang;
        this.logger = Main.logger;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player executor)) {
            sender.sendMessage(lang.getKey("msgs.playeronly"));
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
        if (target == null) {
            sender.sendMessage(lang.getKey("staffcmds.tpo.error", replace));
            return true;
        }
        executor.teleport(target.getLocation());
        sender.sendMessage(lang.getKey("staffcmds.tpo.staff", replace));
        logger.log(lang.getKey("staffcmds.tpo.log", replace));
        return true;
    }
}
