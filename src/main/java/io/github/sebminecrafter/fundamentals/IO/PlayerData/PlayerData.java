package io.github.sebminecrafter.fundamentals.IO.PlayerData;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Different player-specific stored data
 */
public final class PlayerData {

    // Stuff related to ignoring players
    private Set<UUID> ignored = new LinkedHashSet<>();

    public synchronized boolean isIgnoring(UUID other) {
        return ignored != null && ignored.contains(other);
    }

    public synchronized void ignore(UUID other) {
        if (ignored == null) {
            ignored = new LinkedHashSet<>();
        }
        ignored.add(other);
    }

    public synchronized void unignore(UUID other) {
        if (ignored != null) {
            ignored.remove(other);
        }
    }

    // other code

    synchronized <T> T locked(java.util.function.Supplier<T> action) {
        return action.get();
    }
}
