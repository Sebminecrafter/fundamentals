package io.github.sebminecrafter.fundamentals.Commands.Privileged.Workstations;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Workbench implements FundamentalCommand  {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        Inventory workbench = Bukkit.createInventory(player, InventoryType.WORKBENCH);
        player.openInventory(workbench);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
