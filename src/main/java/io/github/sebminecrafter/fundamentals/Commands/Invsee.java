package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Invsee implements FundamentalCommand {
    private final Lang lang;
    private final Logging logger;

    public Invsee() {
        this.lang = Main.lang;
        this.logger = Main.logger;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player executor)) {
            sender.sendMessage(lang.getKey("msgs.playeronly"));
            return true;
        }

        Player player;

        if (args.length != 1) {return false;}
        else {
            player = Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                sender.sendMessage(lang.getKey("msgs.offline"));
                return true;
            } else if (player == executor) {
                sender.sendMessage(lang.getKey("msgs.self"));
                return true;
            }
        }

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", executor.getName());
        helper.add("VICTIM", player.getName());

        executor.openInventory(player.getInventory());
        logger.log(lang.getKey("staffcmds.invsee.log", helper.getReplace()));
        sender.sendMessage(lang.getKey("staffcmds.invsee.staff", helper.getReplace()));

        return true;
    }
}
