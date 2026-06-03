package io.github.sebminecrafter.fundamentals.IO;

import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.*;

public class Logging {
    private final Logger logger;
    private final Logger secondaryLogger;

    public Logging(@NonNull JavaPlugin plugin) {
        logger = plugin.getLogger();
        secondaryLogger = Logger.getLogger("Fundamentals Logger");
        File logFile = new File(plugin.getDataFolder(), "Fundamentals.log");
        try {
            if (logFile.createNewFile()) {
                log("Created log file.");
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Failed to create log file.");
            log(Level.SEVERE, "Stack trace: "+ Arrays.toString(e.getStackTrace()));
        }
        try {
            FileHandler slFh = new FileHandler(logFile.getPath(), true);
            secondaryLogger.addHandler(slFh);
            Formatter simpleFormatter = new logFormatter();
            slFh.setFormatter(simpleFormatter);
        } catch (SecurityException | IOException e) {
            logger.severe(Arrays.toString(e.getStackTrace()));
        }
    }

    public void logBoth(Level level, String message) {
        logger.log(level, message);
        if (secondaryLogger != null && secondaryLogger.isLoggable(level)) {
            secondaryLogger.log(level, message);
        }
    }

    public void logBoth(String message) {
        logBoth(Level.INFO, message);
    }

    public void log(Level level, String message) {
        if (logger != null) {
            logger.log(level, message);
        }
    }
    public void log(String message) {
        log(Level.INFO, message);
    }
}

