package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Feed implements FundamentalCommand {
    private final Lang lang;
    private final Logging logger;

    public Feed() {
        lang = Main.lang;
        logger = Main.logger;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
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
