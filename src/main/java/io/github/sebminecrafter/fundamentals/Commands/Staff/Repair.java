package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Repair implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player target;
        if (args.length > 1) {
            return false;
        } else if (args.length == 1) {
            target = Bukkit.getPlayerExact(args[0]);
        } else {
            if (!(sender instanceof Player p)) {
                return false;
            }
            target = p;
        }
        if (target == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }
        ItemStack item = target.getInventory().getItemInMainHand();
        ItemMeta meta;
        if (item.hasItemMeta()) {
            meta = item.getItemMeta();
        } else {
            Commands.safeSend(sender, lang.getKey("msgs.nullmeta"));
            return true;
        }
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(0);
        }
        item.setItemMeta(meta);
        return true;
    }
}
