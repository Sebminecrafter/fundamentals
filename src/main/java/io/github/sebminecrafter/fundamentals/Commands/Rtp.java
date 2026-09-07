package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.IO.TeleportCountdown;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.sebminecrafter.fundamentals.Main.*;

public class Rtp implements FundamentalCommand {
    private static final Map<String, String> DIMENSION_ALIASES = new LinkedHashMap<>();
    static {
        DIMENSION_ALIASES.put("overworld",  "world");
        DIMENSION_ALIASES.put("nether",     "world_nether");
        DIMENSION_ALIASES.put("end",        "world_the_end");
    }

    private static final List<String> TAB_SUGGESTIONS;
    static {
        List<String> suggestions = new ArrayList<>(DIMENSION_ALIASES.keySet());
        suggestions.addAll(Arrays.asList("world", "world_nether", "world_the_end"));
        TAB_SUGGESTIONS = Collections.unmodifiableList(suggestions);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        int countdownTime = config.getInt("rtp.delay");

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());
        helper.add("SECS", Integer.toString(countdownTime));

        World targetWorld;
        if (args.length > 1) {
            return false;
        } else if (args.length == 1) {
            if (!config.getBoolean("rtp.allow-others")) {
                Commands.safeSend(sender, lang.getKey("cmds.rtp.in-world-not-allowed", helper.getReplace()));
                return true;
            }
            String input = args[0].toLowerCase(Locale.ROOT);
            String canonical = DIMENSION_ALIASES.getOrDefault(input, input);
            targetWorld = Bukkit.getWorld(canonical);
            if (targetWorld == null) {
                Commands.safeSend(sender, lang.getKey("msgs.invalid", helper.getReplace()));
                return true;
            } else if (isWorldDisallowed(targetWorld)) {
                Commands.safeSend(sender, lang.getKey("cmds.rtp.other-world-not-allowed", helper.getReplace()));
                return true;
            }
        } else {
            targetWorld = player.getWorld();
            if (isWorldDisallowed(targetWorld)) {
                Commands.safeSend(sender, lang.getKey("cmds.rtp.in-world-not-allowed", helper.getReplace()));
                return true;
            }
        }

        int max = config.getInt("rtp.max");

        int range = (max == -1)
                ? (int) targetWorld.getWorldBorder().getSize() / 2
                : max;

        Location randomLocation = findSafeLocation(targetWorld, range);
        if (randomLocation == null) {
            Commands.safeSend(sender, lang.getKey("cmds.rtp.no-safe-location", helper.getReplace()));
            return true;
        }

        Commands.safeSend(sender, lang.getKey("cmds.rtp.teleporting", helper.getReplace()));
        TeleportCountdown teleportCountdown = new TeleportCountdown(player, randomLocation, countdownTime);
        teleportCountdown.start(seconds -> sendCountdownActionBar(player, seconds),
                () -> Commands.safeSend(player, lang.getKey("msgs.tpcancelled")));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 1) return Collections.emptyList();
        String partial = args[0].toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String suggestion : TAB_SUGGESTIONS) {
            if (suggestion.startsWith(partial)) matches.add(suggestion);
        }
        return matches;
    }

    private Location findSafeLocation(World world, int range) {
        boolean isNether = world.getEnvironment() == World.Environment.NETHER;
        PlaceholderHelper helper = new PlaceholderHelper();
        int max_attempts = config.getInt("rtp.max-attempts");

        for (int attempt = 1; attempt <= max_attempts; attempt++) {
            int x = randomCoord(range);
            int z = randomCoord(range);

            int y = isNether
                    ? findNetherY(world, x, z)
                    : world.getHighestBlockYAt(x, z);

            if (y == -1) continue;

            Location candidate = new Location(world, x + 0.5, y + 1, z + 0.5);

            // Block the player would stand on
            Material floor = world.getBlockAt(x, y, z).getType();

            // Reject lava, water, and void surfaces
            if (floor == Material.LAVA || floor == Material.WATER || floor == Material.AIR) continue;

            // Confirm two air blocks for the player to occupy
            if (!world.getBlockAt(x, y + 1, z).getType().isAir()) continue;
            if (!world.getBlockAt(x, y + 2, z).getType().isAir()) continue;

            helper.add("ATTEMPTS", Integer.toString(attempt));
            logger.log(lang.getKey("cmds.rtp.log.found", helper.getReplace()));
            return candidate;
        }
        helper.add("ATTEMPTS", Integer.toString(max_attempts));
        logger.log(lang.getKey("cmds.rtp.log.failed", helper.getReplace()));
        return null;
    }

    private int randomCoord(int range) {
        int nogo = config.getInt("rtp.no-go");

        // Total span on one side = range - nogo
        int span = range - nogo;
        int offset = ThreadLocalRandom.current().nextInt(span) + nogo; // nogo..range
        return ThreadLocalRandom.current().nextBoolean() ? offset : -offset;
    }

    private int findNetherY(World world, int x, int z) {
        for (int y = 118; y > 4; y--) {
            Material mat = world.getBlockAt(x, y, z).getType();
            if (mat.isSolid()
                    && mat != Material.LAVA
                    && mat != Material.BEDROCK
                    && world.getBlockAt(x, y + 1, z).getType().isAir()
                    && world.getBlockAt(x, y + 2, z).getType().isAir()) {
                return y;
            }
        }
        return -1;
    }

    private boolean isWorldDisallowed(World world) {
        String namespacedKey = world.getKey().toString(); // "minecraft:overworld"
        String plainName     = world.getName(); // "world"
        List<String> worlds = config.getStringList("rtp.worlds");
        return !worlds.contains(namespacedKey)
                && !worlds.contains(plainName);
    }

    private void sendCountdownActionBar(Player p, int seconds) {
        PlaceholderHelper countdownHelper = new PlaceholderHelper();
        countdownHelper.add("SECS", String.valueOf(seconds));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(lang.getKey("cmds.rtp.countdown", countdownHelper.getReplace())));
    }
}