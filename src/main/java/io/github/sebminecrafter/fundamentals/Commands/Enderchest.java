package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Enderchest implements FundamentalCommand {
    private final Lang lang;
    private final Logging logger;

    public Enderchest() {
        this.lang = Main.lang;
        this.logger = Main.logger;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(lang.getKey("msgs.playeronly"));
            return true;
        }
        if (args.length == 0) {
            PlaceholderHelper helper = new PlaceholderHelper();
            helper.add("PLAYER", p.getName());
            logger.log(lang.getKey("staffcmds.enderchest.log.player", helper.getReplace()));
            p.sendMessage(lang.getKey("staffcmds.enderchest.message.player", helper.getReplace()));
            p.openInventory(p.getEnderChest());
            return true;
        } else if (args.length == 1) {
            if (p.hasPermission("fundamentals.staff.enderchest")) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    p.sendMessage(lang.getKey("msgs.offline"));
                    return true;
                }
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("PLAYER", p.getName());
                helper.add("OTHER", target.getName());
                logger.log(lang.getKey("staffcmds.enderchest.log.staff", helper.getReplace()));
                p.sendMessage(lang.getKey("staffcmds.enderchest.message.staff", helper.getReplace()));
                p.openInventory(target.getEnderChest());
                return true;
            } else {
                p.sendMessage(lang.getKey("msgs.noperms"));
                return true;
            }
        }
        return false;
    }
}
