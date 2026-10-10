package io.github.sebminecrafter.fundamentals.Commands.Privileged;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.FundamentalSounds;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Hat implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        if (args.length > 0) return false;

        ItemStack hand = player.getInventory().getItemInMainHand();
        ItemStack helmet = player.getInventory().getHelmet();
        player.getInventory().setItemInMainHand(helmet);
        player.getInventory().setHelmet(hand);

        Commands.safeSend(sender, lang.getKey("cmds.hat"));
        FundamentalSounds.tPSFCSimpler(player, "sounds.hat");

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
