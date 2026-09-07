package io.github.sebminecrafter.fundamentals.IO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Cooldowns {
    private static final Map<String, Map<UUID, Long>> COOLDOWNS = new HashMap<>();

    private Cooldowns() {}

    public static long remainingSeconds(String key, UUID player, int cooldownSeconds) {
        if (cooldownSeconds <= 0) return 0;
        long now = System.currentTimeMillis();
        Long expires = COOLDOWNS.getOrDefault(key, Map.of()).get(player);
        if (expires == null || expires <= now) return 0;
        return Math.max(1, (expires - now + 999) / 1000);
    }

    public static void start(String key, UUID player, int cooldownSeconds) {
        if (cooldownSeconds <= 0) return;
        COOLDOWNS.computeIfAbsent(key, ignored -> new HashMap<>())
                .put(player, System.currentTimeMillis() + cooldownSeconds * 1000L);
    }
}
