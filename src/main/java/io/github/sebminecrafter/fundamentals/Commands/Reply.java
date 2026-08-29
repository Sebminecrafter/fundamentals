package io.github.sebminecrafter.fundamentals.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Reply implements FundamentalCommand {
    private final Msg msg;

    public Reply(Msg msg) {
        this.msg = msg;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        UUID targetUUID = msg.getLastMessaged(player.getUniqueId());
        if (targetUUID == null) {
            Commands.safeSend(sender, lang.getKey("cmds.reply.none"));
            return true;
        }
        Player target = Bukkit.getPlayer(targetUUID);
        if (target == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline"));
            return true;
        }
        List<String> newArgs = new ArrayList<>();
        newArgs.add(target.getName());
        newArgs.addAll(Arrays.stream(args).toList());
        msg.execute(sender, newArgs.toArray(new String[0]), "msg");
        return true;
    }
}
