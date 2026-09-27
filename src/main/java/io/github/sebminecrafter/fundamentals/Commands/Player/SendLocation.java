package io.github.sebminecrafter.fundamentals.Commands.Player;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class SendLocation implements FundamentalCommand {
    private final Msg msg;

    public SendLocation(Msg msg) {
        this.msg = msg;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        if (args.length != 1) {
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }
        Location loc = player.getLocation();
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());
        helper.add("PX", Integer.toString(loc.getBlockX()));
        helper.add("PY", Integer.toString(loc.getBlockY()));
        helper.add("PZ", Integer.toString(loc.getBlockZ()));
        String str = lang.getKey("cmds.sendloc.message", helper.getReplace());
        msg.execute(sender, new String[]{target.getName(), str}, "msg");
        return true;
    }
}
