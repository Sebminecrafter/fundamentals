package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Welcome implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length != 1) {
            return false;
        }
        if (Objects.equals(args[0], sender.getName())) {
            sender.sendMessage(lang.getKey("msgs.self"));
            return true;
        }
        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) {
            sender.sendMessage(lang.getKey("msgs.offline"));
            return true;
        }
        if (player.hasPlayedBefore()) {
            sender.sendMessage(lang.getKey("cmds.welcome.hasplayedbefore"));
            return true;
        }
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("OTHER", player.getName());
        List<List<String>> replace = helper.getReplace();
        logger.log(lang.getKey("cmds.welcome.log", replace));
        Bukkit.broadcastMessage(lang.getKey("cmds.welcome.send", replace));
        return true;
    }
}
