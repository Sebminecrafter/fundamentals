package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Welcome implements FundamentalCommand {
    private final Logging logger;
    private final Lang lang;

    public Welcome() {
        logger = Main.logger;
        lang = Main.lang;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length != 1) {
            return false;
        }
        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) {
            sender.sendMessage(lang.getKey("msgs.offline"));
            return true;
        }
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("OTHER", player.getName());
        List<List<String>> replace = helper.getReplace();
        logger.log(lang.getKey("cmds.welcome.log", replace));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(lang.getKey("cmds.welcome.send", replace));
        }
        return true;
    }
}
