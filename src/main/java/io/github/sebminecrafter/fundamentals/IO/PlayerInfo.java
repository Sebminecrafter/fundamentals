package io.github.sebminecrafter.fundamentals.IO;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Optional;

public class PlayerInfo {
    private final Player player;

    public PlayerInfo(@NonNull Player player) {
        this.player = player;
    }

    public String getIP() {
        InetSocketAddress address = player.getAddress();
        if (address == null) return "unknown";
        return address.getAddress().getHostAddress();
    }

    public int getPort() {
        InetSocketAddress address = player.getAddress();
        if (address == null) return 0;
        return address.getPort();
    }

    public String getName() {
        return player.getName();
    }

    public static Optional<OfflinePlayer> getOfflinePlayer(String name) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(player -> player.getName() != null)
                .filter(player -> player.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
