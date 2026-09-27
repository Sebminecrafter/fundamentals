package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class More implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length != 0) return false;
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }

        int amount = 64;

        ItemStack item = player.getInventory().getItemInMainHand();

        int maxStackSize = item.getType().getMaxStackSize();
        if (maxStackSize > 1) {
            amount = maxStackSize;
        }

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());
        helper.add("ITEM", item.getType().name());
        helper.add("AMOUNT", Integer.toString(item.getAmount()));
        helper.add("NEWAMOUNT", Integer.toString(amount));

        item.setAmount(amount);

        logger.log(lang.getKey("staffcmds.more.log", helper.getReplace()));
        Commands.safeSend(player, lang.getKey("staffcmds.more.staff", helper.getReplace()));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
