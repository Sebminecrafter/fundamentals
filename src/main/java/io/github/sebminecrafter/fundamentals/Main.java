package io.github.sebminecrafter.fundamentals;

import io.github.sebminecrafter.fundamentals.Chat.GlobalChat;
import io.github.sebminecrafter.fundamentals.Checks.OpChecker;
import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.Ignore;
import io.github.sebminecrafter.fundamentals.Commands.Staffmode;
import io.github.sebminecrafter.fundamentals.IO.Config;
import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    public static Logging logger;
    public static Lang lang;
    public static Config config;
    public static GlobalChat chat;
    private OpChecker opchecker;
    private Commands commands;

    @Override
    public void onEnable() {
        if (getDataFolder().mkdirs()) {
            getLogger().info("Plugin folder created.");
        }

        logger = new Logging(this);
        lang = new Lang(this);
        config = new Config(this);
        commands = new Commands(this);
        chat = new GlobalChat(this, (Ignore) commands.getCommand("ignore"));

        if (config.isEnabled("checks.opchecker")) {
            opchecker = new OpChecker(this);
        }

        for (String commandName : getDescription().getCommands().keySet()) {
            PluginCommand command = getCommand(commandName);
            if (command != null) {
                command.setExecutor(commands);
                command.setTabCompleter(commands);
            }
        }

        logger.log(lang.getKey("started"));
    }

    @Override
    public void onDisable() {

        commands.freeze.cleanup();

        if (opchecker != null) {
            opchecker.stopTask();
        }

        Object staffmode = Objects.requireNonNull(commands.getCommand("staffmode"));
        if (staffmode instanceof Staffmode s) {
            s.restoreAll();
        }

        logger.log(lang.getKey("stopped"));
        logger.closeFileHandler();
    }
}
