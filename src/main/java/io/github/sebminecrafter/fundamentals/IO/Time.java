package io.github.sebminecrafter.fundamentals.IO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static io.github.sebminecrafter.fundamentals.Main.config;

public class Time {
    private final Date date;
    private final DateFormat formatter;

    public Time(Date date, String pattern) {
        this.date = date;
        this.formatter = new SimpleDateFormat(pattern);
    }

    public Time(Date date) {
        this(date, config.getString("time-format"));
    }

    public Time(String pattern) {
        this(new Date(), pattern);
    }

    public Time() {
        this(new Date(), config.getString("time-format"));
    }

    @Override
    public String toString() {
        return formatter.format(date);
    }

    public static String now() {
        return new Time().toString();
    }
}
