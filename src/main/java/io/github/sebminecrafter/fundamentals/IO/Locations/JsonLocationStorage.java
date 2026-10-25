package io.github.sebminecrafter.fundamentals.IO.Locations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Logger;

public class JsonLocationStorage implements LocationStorage {

    private static final Logger LOGGER = Logger.getLogger(JsonLocationStorage.class.getName());

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
        } catch (JsonParseException e) {
            Path backup = folder.resolve(player + ".json.corrupt");
            LOGGER.warning("Could not parse " + file.getFileName() + " (" + e.getMessage()
                    + "); moving it to " + backup.getFileName());
            try {
                Files.move(file, backup, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException moveError) {
                throw new RuntimeException(moveError);
            }
            return new PlayerLocations();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(UUID player, PlayerLocations locations) {
        Path file = folder.resolve(player + ".json");
        Path temp = folder.resolve(player + ".json.tmp");

        // Write to a temp file first so a crash mid-write can't leave a truncated file
        try (Writer writer = Files.newBufferedWriter(temp)) {
            gson.toJson(locations, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            try {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}