package io.github.sebminecrafter.fundamentals.Checks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class UpdateChecker {
    private static final Pattern VERSION = Pattern.compile(
            "^(\\d+)\\.(\\d+)\\.(\\d+)(-SNAPSHOT)?$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern MC_VERSION = Pattern.compile(
            "^(?:\\d+\\.\\d+(?:\\.\\d+)?(?:-(?:pre|rc)\\d+)?|\\d{2}w\\d{2}[a-z])$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SNAPSHOT = Pattern.compile("^(\\d{2})w(\\d{2})([a-z])$");
    private static final Pattern PRE_RELEASE = Pattern.compile("^(pre|rc)(\\d+)$");
    private final AtomicBoolean busy = new AtomicBoolean(false);
    private volatile String stagedVersion = null;

    private final Main plugin;
    private final String projectId;

    public UpdateChecker(Main plugin, String projectId) {
        this.plugin = plugin;
        this.projectId = projectId;
    }

    public void check() {
        run(null, false);
    }

    public void check(CommandSender sender) {
        run(sender, false);
    }

    public void update(CommandSender sender) {
        run(sender, true);
    }

    private void run(CommandSender sender, boolean install) {
        String serverVersion = Bukkit.getBukkitVersion()
                .split("-")[0]
                .replaceFirst("\\.build\\.\\d+$", "");
        String currentVersion = plugin.getDescription().getVersion();

        if (!MC_VERSION.matcher(serverVersion).matches()
                || !VERSION.matcher(currentVersion).matches()) {
            send(sender, Level.WARNING,
                    "Unknown plugin/server version! MC: " + serverVersion
                            + " Fundamentals: " + currentVersion);
            return;
        }

        if (!busy.compareAndSet(false, true)) {
            send(sender, Level.INFO, "An update check is already running.");
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Optional<JsonObject> newer = checkForUpdates(sender, serverVersion, currentVersion);
                if (install && newer.isPresent()) {
                    installUpdate(sender, newer.get());
                }
            } finally {
                busy.set(false);
            }
        });
    }

    private void send(CommandSender sender, Level level, String message) {
        if (sender == null) {
            logger.log(level, message);
        } else {
            Bukkit.getScheduler().runTask(plugin, () ->
                    Commands.safeSend(sender, message)
            );
        }
    }

    private Optional<JsonObject> checkForUpdates(CommandSender sender, String serverVersion,
                                                 String currentVersion) {
        try {
            String loaders = URLEncoder.encode(
                    "[\"paper\",\"spigot\",\"bukkit\"]",
                    StandardCharsets.UTF_8
            );

            String url = "https://api.modrinth.com/v2/project/"
                    + projectId
                    + "/version?loaders=" + loaders
                    + "&include_changelog=false";

            HttpResponse<String> response;

            try (HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()) {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(15))
                        .header("User-Agent", buildUserAgent())
                        .GET()
                        .build();

                response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );
            }

            if (response.statusCode() != 200) {
                send(sender, Level.WARNING,
                        "Checking for update failed! Received HTTP status code "
                                + response.statusCode());
                return Optional.empty();
            }

            JsonArray versions = JsonParser.parseString(response.body())
                    .getAsJsonArray();

            String latestVersion = null;
            JsonObject latestObject = null;

            for (int i = 0; i < versions.size(); i++) {
                JsonObject version = versions.get(i).getAsJsonObject();
                String versionNumber = version.get("version_number").getAsString();

                if (!VERSION.matcher(versionNumber).matches()) {
                    continue;
                }

                JsonArray gameVersions = version.getAsJsonArray("game_versions");

                if (!supportsServer(gameVersions, serverVersion)) {
                    continue;
                }

                if (latestVersion == null
                        || isNewerPluginVersion(versionNumber, latestVersion)) {
                    latestVersion = versionNumber;
                    latestObject = version;
                }
            }

            if (latestVersion == null) {
                send(sender, Level.INFO,
                        "No compatible version(s) found on Modrinth.");
                return Optional.empty();
            }

            if (isNewerPluginVersion(latestVersion, currentVersion)) {
                if (latestVersion.equals(stagedVersion)) {
                    send(sender, Level.INFO, "Version " + latestVersion
                            + " is already downloaded. Restart the server to apply it.");
                    return Optional.empty();
                }

                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("cur", currentVersion);
                helper.add("new", latestVersion);
                helper.add("url", "https://modrinth.com/plugin/" + projectId);

                send(sender, Level.WARNING, lang.getKey("update-checker.found", helper.getReplace()));
                return Optional.of(latestObject);
            } else {
                send(sender, Level.INFO, "No updates available :)");
                return Optional.empty();
            }
        } catch (Exception e) {
            send(sender, Level.WARNING,
                    "Checking for update failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    private boolean supportsServer(JsonArray gameVersions, String serverVersion) {
        if (gameVersions == null) {
            return false;
        }

        for (int i = 0; i < gameVersions.size(); i++) {
            String version = gameVersions.get(i).getAsString();

            if (!MC_VERSION.matcher(version).matches()) {
                continue;
            }

            if (compareVersion(version, serverVersion) <= 0) {
                return true;
            }
        }

        return false;
    }

    private int compareVersion(String a, String b) {
        return Long.compare(
                minecraftVersionNumber(a),
                minecraftVersionNumber(b)
        );
    }

    private String buildUserAgent() {
        String userAgent = plugin.getName() + "/" + plugin.getDescription().getVersion();
        String website = plugin.getDescription().getWebsite();
        if (website != null && !website.isBlank())
            userAgent += " (" + website + ")";
        return userAgent;
    }

    /** Get time-like number from mc version string */
    private long minecraftVersionNumber(String version) {
        String lower = version.toLowerCase(Locale.ROOT);

        Matcher snapshot = SNAPSHOT.matcher(lower);
        if (snapshot.matches()) {
            long year = Long.parseLong(snapshot.group(1));
            long week = Long.parseLong(snapshot.group(2));
            long revision = snapshot.group(3).charAt(0) - 'a';
            return year * 1_000_000_000L + week * 1_000L + revision;
        }

        String[] halves = lower.split("-", 2);
        String[] parts = halves[0].split("\\.");

        long major = Long.parseLong(parts[0]);
        long minor = Long.parseLong(parts[1]);
        long patch = parts.length > 2 ? Long.parseLong(parts[2]) : 0;

        long stage = 999; // a full release sorts after its pre-releases and RCs
        if (halves.length > 1) {
            Matcher pre = PRE_RELEASE.matcher(halves[1]);
            if (pre.matches()) {
                stage = ("pre".equals(pre.group(1)) ? 100 : 200) + Long.parseLong(pre.group(2));
            }
        }

        return major * 1_000_000_000L
                + minor * 1_000_000L
                + patch * 1_000L
                + stage;
    }

    private boolean isNewerPluginVersion(String a, String b) {
        Matcher first = VERSION.matcher(a);
        Matcher second = VERSION.matcher(b);

        if (!first.matches() || !second.matches()) {
            throw new IllegalArgumentException("Invalid plugin version");
        }

        for (int i = 1; i <= 3; i++) {
            long firstNumber = Long.parseLong(first.group(i));
            long secondNumber = Long.parseLong(second.group(i));

            if (firstNumber != secondNumber) {
                return firstNumber > secondNumber;
            }
        }

        boolean aSnapshot = first.group(4) != null;
        boolean bSnapshot = second.group(4) != null;

        return bSnapshot && !aSnapshot;
    }

    private void stageUpdate(Path downloaded) throws IOException {
        Path updateDir = Bukkit.getUpdateFolderFile().toPath();
        Files.createDirectories(updateDir);
        Path target = updateDir.resolve(plugin.getJarName());
        try {
            Files.move(downloaded, target,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(downloaded, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void installUpdate(CommandSender sender, JsonObject version) {
        String versionNumber = version.get("version_number").getAsString();

        if (stagedVersion != null) {
            send(sender, Level.INFO, "Version " + stagedVersion
                    + " is already downloaded. Restart the server to apply it.");
            return;
        }

        try {
            JsonObject file = getPrimaryFile(version);

            String filename = file.get("filename").getAsString();
            if (!filename.toLowerCase(Locale.ROOT).endsWith(".jar")) {
                throw new IOException("Primary file is not a jar: " + filename);
            }

            send(sender, Level.INFO, "Downloading " + versionNumber + "...");
            Path verified = downloadAndVerify(file);
            stageUpdate(verified);

            stagedVersion = versionNumber;
            send(sender, Level.INFO, "Update " + versionNumber
                    + " downloaded and verified. Restart the server to apply it.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            send(sender, Level.WARNING, "Update download was interrupted.");
        } catch (Exception e) {
            send(sender, Level.WARNING, "Update failed: " + e.getMessage());
        }
    }

    private JsonObject getPrimaryFile(JsonObject version) throws IOException {
        JsonArray files = version.getAsJsonArray("files");
        if (files == null || files.isEmpty()) throw new IOException("Version has no files");

        for (int i = 0; i < files.size(); i++) {
            JsonObject f = files.get(i).getAsJsonObject();
            if (f.has("primary") && f.get("primary").getAsBoolean()) return f;
        }
        return files.get(0).getAsJsonObject();
    }

    /** Downloads the file to a temp file in the update folder and verifies its SHA-512. */
    private Path downloadAndVerify(JsonObject file) throws IOException, InterruptedException {
        String url = file.get("url").getAsString();
        JsonObject hashes = file.getAsJsonObject("hashes");
        if (hashes == null || !hashes.has("sha512")) {
            throw new IOException("No sha512 hash provided, refusing to install");
        }
        String expected = hashes.get("sha512").getAsString();

        Path updateDir = Bukkit.getUpdateFolderFile().toPath();
        Files.createDirectories(updateDir);
        Path tmp = Files.createTempFile(updateDir, "download-", ".tmp");

        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMinutes(2))
                    .header("User-Agent", buildUserAgent())
                    .GET()
                    .build();

            HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(tmp));
            if (response.statusCode() != 200) {
                throw new IOException("Download returned HTTP " + response.statusCode());
            }

            String actual = sha512(tmp);
            if (!actual.equalsIgnoreCase(expected)) {
                throw new IOException("Hash mismatch, download discarded");
            }
            return tmp;
        } catch (IOException | InterruptedException | RuntimeException e) {
            Files.deleteIfExists(tmp); // never leave a bad file behind
            throw e;
        }
    }

    private static String sha512(Path file) throws IOException {
        try (InputStream in = Files.newInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) md.update(buf, 0, n);
            return HexFormat.of().formatHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }
}