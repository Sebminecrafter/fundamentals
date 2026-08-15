package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Feed implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player player;
        if (args.length > 1) {
            return false;
        } else if (args.length == 1) {
            player = Bukkit.getPlayerExact(args[0]);
        } else {
            if (!(sender instanceof Player)) return false;
            player = (Player) sender;
        }
        if (player == null) {
            sender.sendMessage(lang.getKey("msgs.offline"));
            return true;
        }
        PotionEffect effect = new PotionEffect(PotionEffectType.SATURATION, 5, 255, false, false);
        player.addPotionEffect(effect);
        player.setFoodLevel(20);
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("VICTIM", player.getName());
        sender.sendMessage(lang.getKey("staffcmds.feed.staff", helper.getReplace()));
        logger.log(lang.getKey("staffcmds.feed.log", helper.getReplace()));
        return true;
    }
}
