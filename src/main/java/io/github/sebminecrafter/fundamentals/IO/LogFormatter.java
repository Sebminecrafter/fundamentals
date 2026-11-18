package io.github.sebminecrafter.fundamentals.IO;

import java.util.logging.LogRecord;

import static io.github.sebminecrafter.fundamentals.Main.config;

class LogFormatter extends java.util.logging.Formatter {
    @Override
    public String format(LogRecord r) {
        return "[" + (new Time(config.getString("log-time-format"))) + "] "
                + "[" + r.getLevel() + "] "
                + r.getMessage() + '\n';
    }
}
