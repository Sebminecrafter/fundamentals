package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Fly implements FundamentalCommand {
    private final Lang lang;
    private final Logging logger;

    public Fly() {
        this.lang = Main.lang;
        this.logger = Main.logger;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player player;
        if (args.length == 0) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(lang.getKey("msgs.playeronly"));
                return true;
            }
            player = p;
        } else if (args.length == 1) {
            player = Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                sender.sendMessage(lang.getKey("msgs.offline"));
                return true;
            }
        } else {
            return false;
        }
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());
        List<List<String>> replace = helper.getReplace();
        if (player.getAllowFlight()) {
            sender.sendMessage(lang.getKey("staffcmds.fly.exit.staff", replace));
            logger.log(lang.getKey("staffcmds.fly.exit.log", replace));
            player.setFlying(false);
            player.setAllowFlight(false);
        } else {
            sender.sendMessage(lang.getKey("staffcmds.fly.enter.staff", replace));
            logger.log(lang.getKey("staffcmds.fly.enter.log", replace));
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
