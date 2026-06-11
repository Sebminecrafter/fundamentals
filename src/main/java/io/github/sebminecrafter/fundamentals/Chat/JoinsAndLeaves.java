package io.github.sebminecrafter.fundamentals.Chat;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinsAndLeaves implements Listener {
    private final Lang lang;

    public JoinsAndLeaves(JavaPlugin plugin) {
        this.lang = Main.lang;

        // Register events
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());

        if (player.hasPlayedBefore()) {
            event.setJoinMessage(lang.getKey("chat.join", helper.getReplace()));
        } else {
            event.setJoinMessage(lang.getKey("chat.firstjoin", helper.getReplace()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());

        event.setQuitMessage(lang.getKey("chat.leave", helper.getReplace()));
    }
}
