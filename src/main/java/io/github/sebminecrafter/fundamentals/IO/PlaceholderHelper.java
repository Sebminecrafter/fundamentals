package io.github.sebminecrafter.fundamentals.IO;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Placeholder register and parser */
public class PlaceholderHelper {
    /** Matches a {@code %name%} placeholder, capturing {@code name} */
    private static final Pattern PLACEHOLDER = Pattern.compile("%([A-Za-z0-9_-]+)%");

    private final Map<String, String> replace;

    public PlaceholderHelper() {
        replace = new HashMap<>();
    }

    /** @param placeholder bare placeholder name, without the surrounding % signs */
    public void add(String placeholder, String value) {
        replace.put(placeholder, value);
    }

    public Map<String, String> getReplace() {
        return replace;
    }

    public String parse(String text) {
        return apply(text, replace);
    }

    /**
     * Replace every {@code %name%} in {@code text} with the corresponding value in {@code values}
     *
     * @param text   text containing %name% placeholders
     * @param values map of bare name to replacement value
     */
    public static String apply(String text, Map<String, String> values) {
        if (text == null || text.indexOf('%') < 0 || values.isEmpty()) return text;
        Matcher m = PLACEHOLDER.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String value = values.get(m.group(1));
            m.appendReplacement(sb, Matcher.quoteReplacement(value != null ? value : m.group()));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
