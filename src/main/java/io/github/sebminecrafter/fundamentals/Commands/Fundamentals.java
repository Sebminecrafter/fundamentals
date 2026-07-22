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
        switch (label.toLowerCase()) {
            case "reloadconfig" -> {
                if (args.length != 1) {
                    return false;
                }
                config.loadConfig();
                logger.log(lang.getKey("staffcmds.fundamentals.reloadconfig.log", helper.getReplace()));
                sender.sendMessage(lang.getKey("staffcmds.fundamentals.reloadconfig.staff", helper.getReplace()));
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
                if (config.isEnabled(command)) {
                    sender.sendMessage(lang.getKey("staffcmds.fundamentals.enablecommand.state", helper.getReplace()));
                    return true;
                }
                config.setEnabled("cmds."+command, true);
                logger.log(lang.getKey("staffcmds.fundamentals.enablecommand.log", helper.getReplace()));
                sender.sendMessage(lang.getKey("staffcmds.fundamentals.enablecommand.staff", helper.getReplace()));
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
                if (!config.isEnabled(command)) {
                    sender.sendMessage(lang.getKey("staffcmds.fundamentals.disablecommand.state", helper.getReplace()));
                    return true;
                }
                config.setEnabled("cmds."+command, false);
                logger.log(lang.getKey("staffcmds.fundamentals.disablecommand.log", helper.getReplace()));
                sender.sendMessage(lang.getKey("staffcmds.fundamentals.disablecommand.staff", helper.getReplace()));
            }
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return List.of(
                    "reloadconfig",
                    "enablecommand",
                    "disablecommand"
            );
        }
        if (args.length == 2 && (args[1].equalsIgnoreCase("enablecommand") || args[1].equalsIgnoreCase("disablecommand"))) {
            ConfigurationSection cmdsSection = config.getConfigurationSection("enabled.cmds");
            return cmdsSection != null
                    ? new ArrayList<>(cmdsSection.getKeys(false))
                    : new ArrayList<>();
        }
        return List.of();
    }
}
