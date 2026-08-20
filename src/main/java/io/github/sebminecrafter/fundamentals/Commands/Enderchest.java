package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.FundamentalSounds;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Enderchest implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player p)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        if (args.length > 1) {
            return false;
        }
        Player target;
        if (args.length == 0) {
            PlaceholderHelper helper = new PlaceholderHelper();
            helper.add("PLAYER", p.getName());
            logger.log(lang.getKey("staffcmds.enderchest.log.player", helper.getReplace()));
            Commands.safeSend(p, lang.getKey("staffcmds.enderchest.message.player", helper.getReplace()));
            target = p;
        } else {
            if (p.hasPermission("fundamentals.staff.enderchest")) {
                target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    Commands.safeSend(p, lang.getKey("msgs.offline"));
                    return true;
                }
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("PLAYER", p.getName());
                helper.add("OTHER", target.getName());
                logger.log(lang.getKey("staffcmds.enderchest.log.staff", helper.getReplace()));
                Commands.safeSend(p, lang.getKey("staffcmds.enderchest.message.staff", helper.getReplace()));
            } else {
                Commands.safeSend(p, lang.getKey("msgs.noperms"));
                return true;
            }
        }
        FundamentalSounds.tPSFCSimpler(p, "sounds.ec");
        p.openInventory(target.getEnderChest());
        return true;
    }
}
