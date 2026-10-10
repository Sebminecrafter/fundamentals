package io.github.sebminecrafter.fundamentals.IO;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderHelper {
    private final Map<String, String> replace;

    public PlaceholderHelper() {
        replace = new HashMap<>();
    }

    public void add(String placeholder, String value) {
        replace.put(placeholder, value);
    }

    public Map<String, String> getReplace() {
        return replace;
    }
}
