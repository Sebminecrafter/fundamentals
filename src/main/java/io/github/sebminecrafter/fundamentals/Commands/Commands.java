package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Config;
import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Commands implements CommandExecutor, TabCompleter {

    private final Lang lang;
    private final Map<String, FundamentalCommand> commands = new HashMap<>();
    private final Config config;

    public Commands(JavaPlugin plugin) {
        this.config = Main.config;
        this.lang = Main.lang;

        // Add each command class
        commands.put("staffmode", new Staffmode(plugin));
        commands.put("msg", new Msg());
        commands.put("feed", new Feed());
        commands.put("staffmsg", new Staffmsg());
    }

    public FundamentalCommand getCommand(String commandName) {
        if (commands.containsKey(commandName)) {
            return commands.get(commandName);
        }
        return null;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        String commandName = command.getName().toLowerCase(Locale.ENGLISH);
        if (!config.isEnabled("cmds."+commandName)) {
            sender.sendMessage(lang.getKey("msgs.disabled"));
            return true;
        }
        FundamentalCommand commandObj = getCommand(commandName);
        if (commandObj == null) {
            sender.sendMessage(lang.getKey("msgs.notfound"));
            return true;
        } else {
            return commandObj.execute(sender, args);
        }
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        String commandName = command.getName().toLowerCase(Locale.ENGLISH);
        if (!config.isEnabled("cmds."+commandName)) {
            sender.sendMessage(lang.getKey("msgs.disabled"));
            return List.of();
        }
        FundamentalCommand commandObj = getCommand(commandName);
        if (commandObj == null) {
            sender.sendMessage(lang.getKey("msgs.notfound"));
            return List.of();
        } else {
            return List.of(commandObj.tabComplete(sender, args));
        }
    }
}
