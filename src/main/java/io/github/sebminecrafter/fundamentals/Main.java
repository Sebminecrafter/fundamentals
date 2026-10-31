package io.github.sebminecrafter.fundamentals;

import io.github.sebminecrafter.fundamentals.Chat.GlobalChat;
import io.github.sebminecrafter.fundamentals.Checks.OpChecker;
import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.Player.Homes;
import io.github.sebminecrafter.fundamentals.Commands.Staff.Freeze;
import io.github.sebminecrafter.fundamentals.Commands.Player.Ignore;
import io.github.sebminecrafter.fundamentals.Commands.Staff.Staffmode;
import io.github.sebminecrafter.fundamentals.IO.Config;
import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.Checks.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static Logging logger;
    public static Lang lang;
    public static Config config;
    public static GlobalChat chat;
    public static Commands commands;
    public static UpdateChecker updateChecker;
    public static int pluginId = 34782;
    private OpChecker opchecker;
    private Metrics metrics;

    @Override
    public void onEnable() {
        // Create folder if it doesn't exist
        if (getDataFolder().mkdirs()) {
            getLogger().info("Plugin folder created.");
        }

        // Create objects
        logger = new Logging(this);
        lang = new Lang(this);
        config = new Config(this);
        commands = new Commands(this);
        chat = new GlobalChat(this, (Ignore) commands.getCommand("ignore"));
        updateChecker = new UpdateChecker(this, "fundamentalswastaken");

        // Start checks
        if (config.isEnabled("checks.opchecker")) {
            opchecker = new OpChecker(this);
        }
        if (config.isEnabled("checks.updatechecker")) {
            updateChecker.check();
        }

        // Register all commands
        getDescription().getCommands().keySet().forEach(commandName -> {
            if (!config.isEnabled("cmds."+commandName)) return;
            PluginCommand command = getCommand(commandName);
            if (command != null) {
                command.setExecutor(commands);
                command.setTabCompleter(commands);
            }
        });

        // Register bStats metrics
        metrics = new Metrics(this, pluginId);

        logger.log(lang.getKey("started"));
    }

    @Override
    public void onDisable() {
        // Stop tasks
        if (opchecker != null) opchecker.stopTask();
        if (metrics != null) metrics.shutdown();

        if (commands != null) {
            // Persist data that is only written on change/quit
            if (commands.getCommand("home") instanceof Homes h) h.saveAll();

            // Cleanup commands
            if (commands.getCommand("staffmode") instanceof Staffmode s) s.restoreAll();
            if (commands.getCommand("freeze")    instanceof Freeze f)    f.cleanup();
        }

        if (logger != null) {
            if (lang != null) logger.log(lang.getKey("stopped"));
            // Close logger
            logger.closeFileHandler();
        }
    }
}
