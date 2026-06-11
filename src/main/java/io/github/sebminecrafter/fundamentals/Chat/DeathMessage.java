package io.github.sebminecrafter.fundamentals.Chat;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

public class DeathMessage implements Listener {
    private final Lang lang;

    public DeathMessage(Plugin plugin) {
        this.lang = Main.lang;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        String playerName = player.getName();
        String deathMsg = event.getDeathMessage();

        deathMsg = deathMsg == null ? "" : deathMsg;
        deathMsg = deathMsg.replaceFirst(playerName, "");
        deathMsg = deathMsg.trim();

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", playerName);
        helper.add("DEATH", deathMsg);

        event.setDeathMessage(lang.getKey("chat.death", helper.getReplace()));
    }
}
