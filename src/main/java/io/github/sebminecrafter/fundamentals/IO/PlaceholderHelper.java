package io.github.sebminecrafter.fundamentals.IO;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderHelper {
    private final List<List<String>> replace;

    public PlaceholderHelper() {
        replace = new ArrayList<>();
    }

    public void add(String placeholder, String value) {
        List<String> replaceList = new ArrayList<>();
        replaceList.add(placeholder);
        replaceList.add(value);
        replace.add(replaceList);
    }

    public List<List<String>> getReplace() {
        return replace;
    }
}
