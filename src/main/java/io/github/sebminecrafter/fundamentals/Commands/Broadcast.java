package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Broadcast implements FundamentalCommand {
    private final Logging logger;
    private final Lang lang;

    public Broadcast() {
        logger = Main.logger;
        lang = Main.lang;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length < 1) return false;
        String message = String.join(" ", args);
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("MSG", message);
        List<List<String>> replace = helper.getReplace();
        logger.logBoth(lang.getKey("staffcmds.broadcast.log", replace));
        sender.sendMessage(lang.getKey("staffcmds.broadcast.staff", replace));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(lang.getKey("staffcmds.broadcast.player", replace));
            player.sendTitle(lang.getKey("staffcmds.broadcast.player", replace), null, 20, 60, 20);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > 0) {
            return List.of(args[args.length - 1]);
        } else {
            return List.of();
        }
    }
}
