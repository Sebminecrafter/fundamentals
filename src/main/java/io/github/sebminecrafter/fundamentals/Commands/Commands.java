package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.Commands.Player.*;
import io.github.sebminecrafter.fundamentals.Commands.Privileged.Rest;
import io.github.sebminecrafter.fundamentals.Commands.Privileged.Workstations.*;
import io.github.sebminecrafter.fundamentals.Commands.Staff.*;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static io.github.sebminecrafter.fundamentals.Main.config;
import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Commands implements CommandExecutor, TabCompleter {
    public final Map<String, FundamentalCommand> commands = new HashMap<>();

    public Commands(Main plugin) {
        // Staff commands
        commands.put("broadcast", new Broadcast());
        commands.put("broadcastworld", new BroadcastWorld());
        commands.put("staffmode", new Staffmode(plugin));
        commands.put("feed", new Feed());
        commands.put("heal", new Heal());
        commands.put("staffmsg", new Staffmsg());
        commands.put("gamemode", new GamemodeSimplifier());
        commands.put("invsee", new Invsee());
        commands.put("freeze", new Freeze(plugin));
        commands.put("tpo", new Tpo());
        commands.put("fly", new Fly());
        commands.put("fundamentals", new Fundamentals());
        commands.put("socialspy", new Socialspy());
        commands.put("nuke", new Nuke());
        commands.put("bignuke", new BigNuke());
        commands.put("rename", new Rename());
        commands.put("more", new More());
        commands.put("skull", new Skull());
        commands.put("smite", new Smite());
        commands.put("burn", new Burn());
        commands.put("extinguish", new Extinguish());
        commands.put("rest", new Rest());
        commands.put("repair", new Repair());
        commands.put("kickall", new Kickall());

        // Player commands
        commands.put("ignore", new Ignore(plugin));
        commands.put("msg", new Msg((Ignore) commands.get("ignore"), (Socialspy) commands.get("socialspy")));
        commands.put("reply", new Reply((Msg) commands.get("msg")));
        commands.put("tpa", new Tpa(config.getInt("tpa.expiresafter"), (Ignore) commands.get("ignore")));
        commands.put("rtp", new Rtp(plugin));
        commands.put("home", new Homes(plugin));
        commands.put("warp", new Warps(plugin));
        commands.put("welcome", new Welcome());
        commands.put("enderchest", new Enderchest());
        commands.put("ping", new Ping());
        commands.put("sudo", new Sudo());
        commands.put("clearchat", new Clearchat());
        commands.put("rules", new Rules(plugin));
        commands.put("trash", new Trash());
        commands.put("sendloc", new SendLocation((Msg) commands.get("msg")));
        commands.put("getpos", new Getpos());
        commands.put("suicide", new Suicide());

        // Privileged commands
        commands.put("workbench", new Workbench());
        commands.put("stonecutter", new Stonecutter());
        commands.put("smithingtable", new SmithingTable());
        commands.put("loom", new Loom());
        commands.put("grindstone", new Grindstone());
        commands.put("anvil", new Anvil());
        commands.put("cartographytable", new CartographyTable());
    }

    public FundamentalCommand getCommand(String commandName) {
        if (commands.containsKey(commandName)) {
            return commands.get(commandName);
        }
        return null;
    }

    /** Sends a message only if it is non-null and not blank after stripping color codes. */
    public static void safeSend(CommandSender sender, String message) {
        if (message == null || message.isBlank()) return;
        // Strip color codes and test for empty message
        String stripped = message.replaceAll("§[0-9A-Fa-fK-Ok-oRr]", "").strip();
        if (stripped.isEmpty()) return;
        sender.sendMessage(message);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        String commandName = command.getName().toLowerCase(Locale.ENGLISH);
        if (!config.isEnabled("cmds."+commandName)) {
            safeSend(sender, lang.getKey("msgs.disabled"));
            return true;
        }
        FundamentalCommand commandObj = getCommand(commandName);
        if (commandObj == null) {
            safeSend(sender, lang.getKey("msgs.notfound"));
            return true;
        } else {
            return commandObj.execute(sender, args, label);
        }
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        String commandName = command.getName().toLowerCase(Locale.ENGLISH);
        if (!config.isEnabled("cmds."+commandName)) {
            return List.of();
        }
        FundamentalCommand commandObj = getCommand(commandName);
        if (commandObj == null) {
            safeSend(sender, lang.getKey("msgs.notfound"));
            return List.of();
        } else {
            return commandObj.tabComplete(sender, args);
        }
    }
}
