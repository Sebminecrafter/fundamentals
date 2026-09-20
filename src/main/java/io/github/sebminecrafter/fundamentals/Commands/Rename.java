package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Rename implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length == 0) return false;
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }

        StringBuilder newNameBuilder = new StringBuilder();
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());

        for (String part : args) {
            if (!newNameBuilder.isEmpty()) {
                newNameBuilder.append(" ");
            }
            newNameBuilder.append(part);
        }

        String newName = lang.formatColors(newNameBuilder.toString());
        ItemStack item = player.getInventory().getItemInMainHand();
        helper.add("NAME", newName);
        helper.add("ITEM", item.getType().name());
        if (item.getType() == Material.AIR) {
            Commands.safeSend(sender, lang.getKey("staffcmds.rename.no-item", helper.getReplace()));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            logger.log(lang.getKey("staffcmds.rename.meta", helper.getReplace()));
            return true;
        }

        meta.setItemName(newName);
        item.setItemMeta(meta);
        logger.log(lang.getKey("staffcmds.rename.log", helper.getReplace()));
        Commands.safeSend(sender, lang.getKey("staffcmds.rename.staff", helper.getReplace()));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length < 1) return List.of("<item name>");
        return List.of();
    }
}
