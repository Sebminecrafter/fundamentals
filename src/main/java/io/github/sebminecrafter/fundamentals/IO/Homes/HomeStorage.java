package io.github.sebminecrafter.fundamentals.IO.Homes;

import java.util.UUID;

public interface HomeStorage {

    PlayerHomes load(UUID player);

    void save(UUID player, PlayerHomes homes);

}
