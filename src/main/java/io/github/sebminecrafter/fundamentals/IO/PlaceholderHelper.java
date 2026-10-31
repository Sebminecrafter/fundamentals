package io.github.sebminecrafter.fundamentals.IO;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collects placeholder values and substitutes them into text.
 * <p>
 * Placeholders are registered by bare name (like {@code PLAYER}) but only match in text
 * when written wrapped in percent signs (like {@code %PLAYER%}).
 */
public class PlaceholderHelper {
    /** Matches a {@code %NAME%} placeholder, capturing NAME. */
    private static final Pattern PLACEHOLDER = Pattern.compile("%([A-Za-z0-9_]+)%");

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
     * Replace every {@code %NAME%} in `text` whose NAME is a key of `values`.
     * Unknown placeholders and stray % signs are left untouched, and replacement values
     * are never re-scanned (so a value containing {@code %OTHER%} is not expanded).
     *
     * @param text   text containing %NAME% placeholders
     * @param values map of bare NAME to replacement value
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
