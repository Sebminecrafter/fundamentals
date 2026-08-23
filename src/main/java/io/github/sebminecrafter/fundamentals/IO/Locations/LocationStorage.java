package io.github.sebminecrafter.fundamentals.IO.Locations;

import java.util.UUID;

public interface LocationStorage {

    PlayerLocations load(UUID player);

    void save(UUID player, PlayerLocations homes);

}
