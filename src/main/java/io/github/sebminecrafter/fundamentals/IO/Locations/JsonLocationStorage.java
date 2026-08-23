package io.github.sebminecrafter.fundamentals.IO.Locations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class JsonLocationStorage implements LocationStorage {

    private final Path folder;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public JsonLocationStorage(Path folder) throws IOException {
        this.folder = folder;
        Files.createDirectories(folder);
    }

    @Override
    public PlayerLocations load(UUID player) {
        Path file = folder.resolve(player + ".json");

        if (Files.notExists(file))
            return new PlayerLocations();

        try (Reader reader = Files.newBufferedReader(file)) {
            PlayerLocations locations = gson.fromJson(reader, PlayerLocations.class);
            return locations != null ? locations : new PlayerLocations();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(UUID player, PlayerLocations locations) {
        Path file = folder.resolve(player + ".json");

        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(locations, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}