package io.github.sebminecrafter.fundamentals.IO.Locations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class WarpStorage {

    private static final Type WARP_MAP_TYPE =
            new TypeToken<Map<String, Warp>>() {}.getType();

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
        } catch (IOException e) {
            throw new RuntimeException("Failed to load warps.json", e);
        }
    }

    public void save(Map<String, Warp> warps) {
        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(warps, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save warps.json", e);
        }
    }
}
