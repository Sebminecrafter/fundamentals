package io.github.sebminecrafter.fundamentals.Checks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private final JavaPlugin plugin;
    private final String projectId;

    public UpdateChecker(JavaPlugin plugin, String projectId) {
        this.plugin = plugin;
        this.projectId = projectId;
    }

    public void check() {
        check(null);
    }

    public void check(CommandSender sender) {
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

        plugin.getServer().getScheduler()
                .runTaskAsynchronously(plugin,
                        () -> checkForUpdates(sender, serverVersion, currentVersion));
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

    private void checkForUpdates(CommandSender sender, String serverVersion,
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

            String website = plugin.getDescription().getWebsite();
            String userAgent = plugin.getName() + "/" + currentVersion;

            if (website != null && !website.isBlank()) {
                userAgent += " (" + website + ")";
            }

            HttpResponse<String> response;

            try (HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()) {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(15))
                        .header("User-Agent", userAgent)
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
                return;
            }

            JsonArray versions = JsonParser.parseString(response.body())
                    .getAsJsonArray();

            String latestVersion = null;

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
                }
            }

            if (latestVersion == null) {
                send(sender, Level.INFO,
                        "No compatible version(s) found on Modrinth.");
                return;
            }

            if (isNewerPluginVersion(latestVersion, currentVersion)) {
                PlaceholderHelper helper = new PlaceholderHelper();
                helper.add("CUR", currentVersion);
                helper.add("NEW", latestVersion);
                helper.add("URL", "https://modrinth.com/plugin/" + projectId);

                String message = """
                        Update found!
                        Current version: CUR
                        Latest: NEW
                        Download it at URL
                        """;

                send(sender, Level.WARNING, helper.parse(message));
            } else {
                send(sender, Level.INFO, "No updates available :)");
            }

        } catch (Exception e) {
            send(sender, Level.WARNING,
                    "Checking for update failed: " + e.getMessage());
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

    private long minecraftVersionNumber(String version) {
        if (version.matches("(?i)\\d{2}w\\d{2}[a-z]")) {
            int year = Integer.parseInt(version.substring(0, 2));
            int week = Integer.parseInt(version.substring(3, 5));
            int revision = Character.toLowerCase(version.charAt(5)) - 'a';

            return (2000L + year) * 1_000_000
                    + week * 1_000L
                    + revision;
        }

        String[] parts = version.split("[.-]");

        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = 0;

        if (parts.length > 2 && parts[2].matches("\\d+")) {
            patch = Integer.parseInt(parts[2]);
        }

        long releaseYear;
        int releaseNumber;
        int patchNumber;

        if (major == 1) {
            releaseYear = 2025L + minor - 20;
            releaseNumber = patch;
            patchNumber = 0;
        } else {
            releaseYear = 2000L + major;
            releaseNumber = minor;
            patchNumber = patch;
        }

        int stage = 999;

        if (version.matches("(?i).*-(pre|rc)\\d+")) {
            boolean prerelease = version.toLowerCase().contains("-pre");
            int number = Integer.parseInt(
                    parts[parts.length - 1].replaceAll("\\D", "")
            );

            stage = (prerelease ? 100 : 200) + number;
        }

        return releaseYear * 1_000_000
                + releaseNumber * 10_000L
                + patchNumber * 1_000L
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
}