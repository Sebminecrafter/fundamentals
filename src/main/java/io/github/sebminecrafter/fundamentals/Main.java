package io.github.sebminecrafter.fundamentals;

import io.github.sebminecrafter.fundamentals.Checks.OpChecker;
import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.Staffmode;
import io.github.sebminecrafter.fundamentals.IO.Config;
import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    public static Logging logger;
    public static Lang lang;
    public static Config config;
    private OpChecker opchecker;
    private Commands commands;

    @Override
    public void onEnable() {
        if (getDataFolder().mkdirs()) {
            getLogger().info("Plugin folder created.");
        }

        logger = new Logging(this);
        lang = new Lang(this, "lang.yml");
        config = new Config(this, "config.yml");
        commands = new Commands(this);

        if (config.isEnabled("checks.opchecker")) {
            opchecker = new OpChecker(this);
        }

        Objects.requireNonNull(this.getCommand("staffmode")).setExecutor(commands);

        logger.log(lang.getKey("started"));
    }

    @Override
    public void onDisable() {

        if (opchecker != null) {
            opchecker.stopTask();
        }

        Object staffmode = Objects.requireNonNull(commands.getCommand("staffmode"));
        if (staffmode instanceof Staffmode s) {
            s.restoreAll();
        }

        logger.log(lang.getKey("stopped"));
    }
}
