package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
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
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.getKey("msgs.playeronly"));
            return true;
        }
        if (args.length != 0) return false;
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
