package io.github.sebminecrafter.fundamentals.IO;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.LogRecord;

class LogFormatter extends java.util.logging.Formatter {
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public String format(LogRecord r) {
        return "[" + LocalTime.now().format(F) + "] [" + r.getLevel() + "] " + r.getMessage() + '\n';
    }
}
