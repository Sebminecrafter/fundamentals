package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static org.bukkit.Sound.BLOCK_ENDER_CHEST_OPEN;

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
        if (args.length > 1) {
            return false;
        }
        Player target;
        if (args.length == 0) {
            PlaceholderHelper helper = new PlaceholderHelper();
            helper.add("PLAYER", p.getName());
            logger.log(lang.getKey("staffcmds.enderchest.log.player", helper.getReplace()));
            p.sendMessage(lang.getKey("staffcmds.enderchest.message.player", helper.getReplace()));
            target = p;
        } else {
            if (p.hasPermission("fundamentals.staff.enderchest")) {
                target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    p.sendMessage(lang.getKey("msgs.offline"));
                    return true;
                }
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("PLAYER", p.getName());
                helper.add("OTHER", target.getName());
                logger.log(lang.getKey("staffcmds.enderchest.log.staff", helper.getReplace()));
                p.sendMessage(lang.getKey("staffcmds.enderchest.message.staff", helper.getReplace()));
            } else {
                p.sendMessage(lang.getKey("msgs.noperms"));
                return true;
            }
        }
        p.playSound(p, BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
        p.openInventory(target.getEnderChest());
        return true;
    }
}
