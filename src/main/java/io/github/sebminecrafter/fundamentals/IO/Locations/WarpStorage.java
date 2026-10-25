package io.github.sebminecrafter.fundamentals.IO.Locations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WarpStorage {

    private static final Type WARP_MAP_TYPE =
            new TypeToken<Map<String, Warp>>() {}.getType();

    private static final Logger LOGGER = Logger.getLogger(WarpStorage.class.getName());

    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public WarpStorage(Path dataFolder) throws IOException {
        Files.createDirectories(dataFolder);
        this.file = dataFolder.resolve("warps.json");
    }

    public Map<String, Warp> load() {
        if (Files.notExists(file)) return new HashMap<>();
        try (Reader reader = Files.newBufferedReader(file)) {
            Map<String, Warp> map = gson.fromJson(reader, WARP_MAP_TYPE);
            return map != null ? map : new HashMap<>();
        } catch (JsonParseException e) {
            Path backup = file.resolveSibling("warps.json.corrupt");
            LOGGER.severe("Could not parse warps.json (" + e.getMessage() + "); moving it to " + backup.getFileName());
            try {
                Files.move(file, backup, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException moveError) {
                throw new RuntimeException("Failed to back up corrupt warps.json", moveError);
            }
            return new HashMap<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load warps.json", e);
        }
    }

    public void save(Map<String, Warp> warps) {
        Path temp = file.resolveSibling("warps.json.tmp");
        try (Writer writer = Files.newBufferedWriter(temp)) {
            gson.toJson(warps, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save warps.json", e);
        }
        try {
            try {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save warps.json", e);
        }
    }
}
