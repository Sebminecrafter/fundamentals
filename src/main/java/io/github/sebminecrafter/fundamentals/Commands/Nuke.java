package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

import java.util.ArrayList;
import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Nuke implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        List<Player> targets = new ArrayList<>();
        if (args.length == 0) {
            targets.addAll(Bukkit.getOnlinePlayers());
        } else {
            for (String arg : args) {
                Player p = Bukkit.getPlayerExact(arg);
                if (p == null) {
                    Commands.safeSend(sender, arg);
                    Commands.safeSend(sender, lang.getKey("msgs.offline"));
                    return true;
                }
                targets.add(p);
            }
        }
        StringBuilder targetsStr = new StringBuilder();
        for (Player p : targets) {
            if (!targetsStr.isEmpty()) {
                targetsStr.append(", ");
            }
            targetsStr.append(p.getName());
        }

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("TARGETS", targetsStr.toString());
        Commands.safeSend(sender, lang.getKey("staffcmds.nuke.staff", helper.getReplace()));
        logger.log(lang.getKey("staffcmds.nuke.log", helper.getReplace()));
        for (Player p : targets) {
            Location loc = p.getLocation();
            World w = p.getWorld();

            double newY = Math.max(loc.getY()+20, w.getHighestBlockYAt(loc)+10);
            loc.setY(newY);

            w.spawn(loc, TNTPrimed.class, false, tnt -> tnt.setFuseTicks(50));
            Commands.safeSend(p, lang.getKey("staffcmds.nuke.message", helper.getReplace()));
        }
        return true;
    }
}