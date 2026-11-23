package io.github.sebminecrafter.fundamentals.IO.PlayerData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSON file for each player, in 'plugins/Fundamentals/playerdata/'
 */
public class PlayerDataStore implements Listener {

    private static final Logger LOGGER = Logger.getLogger(PlayerDataStore.class.getName());

    private final Path folder;
    private final Map<UUID, PlayerData> cache = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public PlayerDataStore(JavaPlugin plugin) throws IOException {
        this.folder = plugin.getDataFolder().toPath().resolve("playerdata");
        Files.createDirectories(folder);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public PlayerData get(UUID player) {
        return cache.computeIfAbsent(player, this::load);
    }

    public boolean save(UUID player) {
        PlayerData data = cache.get(player);
        if (data == null) return true;
        Path file = fileFor(player);
        Path temp = folder.resolve(player + ".json.tmp");
        try {
            // use data lock and temp file for safety
            String json = data.locked(() -> gson.toJson(data));
            try (Writer writer = Files.newBufferedWriter(temp)) {
                writer.write(json);
            }
            try {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not save " + file.getFileName(), e);
            return false;
        }
    }

    /** Remove player data from memory */
    public void unload(UUID player) {
        cache.remove(player);
    }

    public void saveAll() {
        cache.keySet().forEach(this::save);
    }

    private PlayerData load(UUID player) {
        Path file = fileFor(player);
        if (Files.notExists(file)) return new PlayerData();

        try (Reader reader = Files.newBufferedReader(file)) {
            PlayerData data = gson.fromJson(reader, PlayerData.class);
            return data != null ? data : new PlayerData();
        } catch (JsonParseException e) {
            Path backup = folder.resolve(player + ".json.corrupt");
            LOGGER.warning("Could not parse " + file.getFileName() + " (" + e.getMessage()
                    + "); moving it to " + backup.getFileName());
            try {
                Files.move(file, backup, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException moveError) {
                LOGGER.log(Level.SEVERE, "Could not move corrupt file aside", moveError);
            }
            return new PlayerData();
        } catch (IOException e) {
            // Don't hand back empty data here: a later save would overwrite a good file
            // that we merely failed to read. Fail loudly instead.
            throw new RuntimeException("Could not read " + file.getFileName(), e);
        }
    }

    private Path fileFor(UUID player) {
        return folder.resolve(player + ".json");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        save(id);
        cache.remove(id);
    }
}
