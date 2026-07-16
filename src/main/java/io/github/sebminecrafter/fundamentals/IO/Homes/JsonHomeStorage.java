package io.github.sebminecrafter.fundamentals.IO.Homes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class JsonHomeStorage implements HomeStorage {

    private final Path folder;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public JsonHomeStorage(Path folder) throws IOException {
        this.folder = folder;
        Files.createDirectories(folder);
    }

    @Override
    public PlayerHomes load(UUID player) {
        Path file = folder.resolve(player + ".json");

        if (Files.notExists(file))
            return new PlayerHomes();

        try (Reader reader = Files.newBufferedReader(file)) {
            PlayerHomes homes = gson.fromJson(reader, PlayerHomes.class);
            return homes != null ? homes : new PlayerHomes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(UUID player, PlayerHomes homes) {
        Path file = folder.resolve(player + ".json");

        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(homes, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}