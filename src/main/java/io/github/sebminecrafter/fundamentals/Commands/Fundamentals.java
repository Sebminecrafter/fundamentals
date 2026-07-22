package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Config;
import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
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
        switch (label.toLowerCase()) {
            case "reloadconfig" -> {
                if (args.length != 1) {
                    return false;
                }
                logger.log(lang.getKey("staffcmds.fundamentals.reloadconfig.log"));
                sender.sendMessage(lang.getKey("staffcmds.fundamentals.reloadconfig.staff"));

            }
            case "enablecommand" -> {
                if (args.length != 2) {
                    return false;
                }
                ConfigurationSection cmdsSection = config.getConfigurationSection("enabled.cmds");
                if (cmdsSection == null) {
                    sender.sendMessage(lang.getKey("msgs."));
                }
            }
            case "disablecommand" -> {
                if (args.length != 2) {
                    return false;
                }
                ConfigurationSection cmdsSection = config.getConfigurationSection("enabled.cmds");
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
