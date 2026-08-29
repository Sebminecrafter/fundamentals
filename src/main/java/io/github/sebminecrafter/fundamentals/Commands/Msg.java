package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.FundamentalSounds;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Msg implements FundamentalCommand {
    private final Ignore ignore;
    private final Socialspy socialspy;
    private final Map<UUID, UUID> lastMessaged;

    public Msg(Ignore ignore, Socialspy socialspy) {
        this.ignore = ignore;
        this.socialspy = socialspy;
        this.lastMessaged = new HashMap<>();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        if (args.length < 2) {
            return false;
        }
        StringBuilder message = new StringBuilder();
        for (int i=1;i<args.length;i++) {
            message.append(args[i]);
            message.append(" ");
        }
        Player receiver = Bukkit.getPlayerExact(args[0]);
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("OTHER", args[0]);
        helper.add("MSG", message.toString());
        if (receiver == null) {
            Commands.safeSend(sender, lang.getKey("msgs.offline", helper.getReplace()));
            return true;
        }
        if (ignore.isIgnoring(receiver.getUniqueId(), player.getUniqueId())) {
            Commands.safeSend(sender, lang.getKey("msgs.ignored"));
            return true;
        }
        if (Main.chat.notAllowed(message.toString(), player)) {
            Commands.safeSend(sender, lang.getKey("chat.disallowed"));
            return true;
        }
        // Add to last messaged (and remove previous value)
        lastMessaged.remove(receiver.getUniqueId());
        lastMessaged.remove(player.getUniqueId());
        lastMessaged.put(receiver.getUniqueId(), player.getUniqueId());
        lastMessaged.put(player.getUniqueId(), receiver.getUniqueId());

        Commands.safeSend(receiver, lang.getKey("cmds.msg.receive", helper.getReplace()));
        Commands.safeSend(sender, lang.getKey("cmds.msg.send", helper.getReplace()));
        logger.log(lang.getKey("cmds.msg.log", helper.getReplace()));
        FundamentalSounds.tPSFCSimpler(receiver, "sounds.msg");
        socialspy.sendToSpyingPlayers(lang.getKey("staffcmds.socialspy.msg", helper.getReplace()));
        return true;
    }

    public UUID getLastMessaged(UUID uuid) {
        return lastMessaged.get(uuid);
    }
}
