package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ping implements FundamentalCommand {
    private final Lang lang;

    public Ping() {
        this.lang = Main.lang;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length > 1) {
            return false;
        }
        Player target;
        PlaceholderHelper helper = new PlaceholderHelper();
        // Self ping
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                return false;
            }
            target = player;
            helper.add("PING", String.valueOf(target.getPing()));
            sender.sendMessage(lang.getKey("cmds.ping.self", helper.getReplace()));
        } else { // Other ping
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(lang.getKey("msgs.offline"));
                return true;
            }
            helper.add("PING", String.valueOf(target.getPing()));
            helper.add("PLAYER", target.getName());
            sender.sendMessage(lang.getKey("cmds.ping.other", helper.getReplace()));
        }
        return true;
    }
}
