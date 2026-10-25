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
import java.util.Locale;
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

    private static final Pattern SNAPSHOT = Pattern.compile("^(\\d{2})w(\\d{2})([a-z])$");
    private static final Pattern PRE_RELEASE = Pattern.compile("^(pre|rc)(\\d+)$");

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
}