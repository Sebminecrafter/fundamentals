package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Config;
import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Fundamentals implements FundamentalCommand {
    private final Config config;
    private final Lang lang;
    private final Logging logger;

    public Fundamentals() {
        this.config = Main.config;
        this.lang = Main.lang;
        this.logger = Main.logger;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length == 0) {
            return false;
        }
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        switch (args[0].toLowerCase()) {
            case "reloadconfig" -> {
                if (args.length != 1) {
                    return false;
                }
                config.loadConfig();
                logger.log(lang.getKey("staffcmds.fundamentals.reloadconfig.log", helper.getReplace()));
                sender.sendMessage(lang.getKey("staffcmds.fundamentals.reloadconfig.staff", helper.getReplace()));
                return true;
            }
            case "enablecommand" -> {
                if (args.length != 2) {
                    return false;
                }
                ConfigurationSection cmdsSection = config.getConfigurationSection("enabled.cmds");
                if (cmdsSection == null) {
                    sender.sendMessage(lang.getKey("msgs.readerror"));
                    return true;
                }
                String command = args[1].toLowerCase();
                helper.add("COMMAND", command);
                if (!cmdsSection.contains(command)) {
                    sender.sendMessage(lang.getKey("msgs.notfound"));
                    return true;
                }
                if (config.isEnabled("cmds." + command)) {
                    sender.sendMessage(lang.getKey("staffcmds.fundamentals.enablecommand.state", helper.getReplace()));
                    return true;
                }
                config.setEnabled("cmds."+command, true);
                logger.log(lang.getKey("staffcmds.fundamentals.enablecommand.log", helper.getReplace()));
                sender.sendMessage(lang.getKey("staffcmds.fundamentals.enablecommand.staff", helper.getReplace()));
                return true;
            }
            case "disablecommand" -> {
                if (args.length != 2) {
                    return false;
                }
                ConfigurationSection cmdsSection = config.getConfigurationSection("enabled.cmds");
                if (cmdsSection == null) {
                    sender.sendMessage(lang.getKey("msgs.readerror"));
                    return true;
                }
                String command = args[1].toLowerCase();
                helper.add("COMMAND", command);
                if (!cmdsSection.contains(command)) {
                    sender.sendMessage(lang.getKey("msgs.notfound"));
                    return true;
                }
                if (!config.isEnabled("cmds." + command)) {
                    sender.sendMessage(lang.getKey("staffcmds.fundamentals.disablecommand.state", helper.getReplace()));
                    return true;
                }
                config.setEnabled("cmds."+command, false);
                logger.log(lang.getKey("staffcmds.fundamentals.disablecommand.log", helper.getReplace()));
                sender.sendMessage(lang.getKey("staffcmds.fundamentals.disablecommand.staff", helper.getReplace()));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return Stream.of("reloadconfig", "enablecommand", "disablecommand")
                    .filter(s -> s.startsWith(partial))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("enablecommand") || args[0].equalsIgnoreCase("disablecommand"))) {
            ConfigurationSection cmdsSection = config.getConfigurationSection("enabled.cmds");
            if (cmdsSection == null) return new ArrayList<>();
            String partial = args[1].toLowerCase();
            return cmdsSection.getKeys(false).stream()
                    .filter(s -> s.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
