package io.github.sebminecrafter.fundamentals.IO;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.net.InetSocketAddress;

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
}
