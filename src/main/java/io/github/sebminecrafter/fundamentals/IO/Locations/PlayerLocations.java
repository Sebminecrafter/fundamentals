package io.github.sebminecrafter.fundamentals.IO.Locations;

import java.util.HashMap;
import java.util.Map;

public final class PlayerLocations {

    private final Map<String, Location> homes = new HashMap<>();

    public Map<String, Location> getHomes() {
        return homes;
    }
}