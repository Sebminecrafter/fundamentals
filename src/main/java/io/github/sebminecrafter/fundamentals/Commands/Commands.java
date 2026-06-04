package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Config;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Commands implements CommandExecutor {

    private final Staffmode staffmode;
    private final Msg msg;
    private final Map<String, Object> commands = new HashMap<>();
    private final Config config;

    public Commands(JavaPlugin plugin) {
        // Get config
        config = Main.config;

        staffmode = new Staffmode(plugin);
        commands.put("staffmode", staffmode);

        msg = new Msg();
        commands.put("msg", msg);
    }

    public Object getCommand(String commandName) {
        if (commands.containsKey(commandName)) {
            return commands.get(commandName);
        }
        return null;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!config.isEnabled("cmds."+command.getName())) {
            sender.sendMessage("This command is disabled.");
            return true;
        }
        if (command.getName().equalsIgnoreCase("staffmode")) {
            if (args.length != 0) {return false;}
            staffmode.execute(sender);
            return true;
        }
        if (command.getName().equalsIgnoreCase("msg")) {
            if (!(args.length >= 2)) {return false;}
            msg.execute(sender, args);
            return true;
        }
        return false;
    }
}
