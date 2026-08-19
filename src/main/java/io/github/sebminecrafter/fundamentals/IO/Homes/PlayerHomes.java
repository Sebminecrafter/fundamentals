package io.github.sebminecrafter.fundamentals.IO.Homes;

import java.util.HashMap;
import java.util.Map;

public final class PlayerHomes {

    private final Map<String, Home> homes = new HashMap<>();

    public int getVersion() {
        return 1;
    }

    public Map<String, Home> getHomes() {
        return homes;
    }
}