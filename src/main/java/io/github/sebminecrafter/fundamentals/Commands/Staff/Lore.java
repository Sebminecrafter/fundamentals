package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Lore implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length == 0) return false;
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }

        StringBuilder loreBuilder = new StringBuilder();
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());

        for (String part : args) {
            if (!loreBuilder.isEmpty()) {
                loreBuilder.append(" ");
            }
            loreBuilder.append(part);
        }

        String newLore = lang.formatColors(loreBuilder.toString());
        ItemStack item = player.getInventory().getItemInMainHand();
        helper.add("LORE", newLore);
        helper.add("ITEM", item.getType().name());
        if (item.getType() == Material.AIR) {
            Commands.safeSend(sender, lang.getKey("msgs.no-item", helper.getReplace()));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            logger.log(lang.getKey("msgs.nullmeta"));
            return true;
        }

        meta.setLore(List.of(newLore));
        item.setItemMeta(meta);
        logger.log(lang.getKey("staffcmds.lore.log", helper.getReplace()));
        Commands.safeSend(sender, lang.getKey("staffcmds.lore.staff", helper.getReplace()));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length < 1) return List.of("<item lore>");
        return List.of();
    }
}
