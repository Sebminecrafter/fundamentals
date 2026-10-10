package io.github.sebminecrafter.fundamentals.Commands.Player;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            Commands.safeSend(sender, lang.getKey("msgs.self"));
            return true;
        }
        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }
        if (player.hasPlayedBefore()) {
            Commands.safeSend(sender, lang.getKey("cmds.welcome.hasplayedbefore"));
            return true;
        }
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("OTHER", player.getName());
        logger.log(lang.getKey("cmds.welcome.log", helper.getReplace()));
        Bukkit.broadcastMessage(lang.getKey("cmds.welcome.send", helper.getReplace()));
        return true;
    }
}
