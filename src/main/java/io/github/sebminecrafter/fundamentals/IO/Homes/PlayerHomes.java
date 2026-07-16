package io.github.sebminecrafter.fundamentals.IO.Homes;

import java.util.HashMap;
import java.util.Map;

public final class PlayerHomes {

    private int version = 1;
    private final Map<String, Home> homes = new HashMap<>();

    public int getVersion() {
        return version;
    }

    public Map<String, Home> getHomes() {
        return homes;
    }
}