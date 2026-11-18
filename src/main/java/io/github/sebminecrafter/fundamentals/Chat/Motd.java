package io.github.sebminecrafter.fundamentals.Chat;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.IO.Time;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Motd implements Listener {
    public Motd(JavaPlugin plugin) {
        // Register events
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        message(player);
    }

    public static void message(CommandSender sender) {
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("player", sender.getName());
        helper.add("time", Time.now());

        sender.sendMessage(lang.getKey("motd", helper.getReplace()));
    }
}
