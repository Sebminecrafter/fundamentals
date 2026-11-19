package io.github.sebminecrafter.fundamentals.Other;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static io.github.sebminecrafter.fundamentals.Main.config;
import static io.github.sebminecrafter.fundamentals.Main.lang;

public class ServerMotd implements Listener {
    public ServerMotd(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onServerListPing(@NotNull ServerListPingEvent event) {
        String motd = config.getString("motd.default");
        if (Bukkit.getServer().getOnlinePlayers().size() >= Bukkit.getServer().getMaxPlayers()) {
            motd = config.getString("motd.full");
        }
        motd = lang.formatColors(motd);
        event.setMotd(motd);
    }
}
