package io.github.sebminecrafter.fundamentals.IO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Wrap for {@link Countdown} to run a movement/damage-check countdown
 * to teleport a player to (world, x, y, z, yaw, pitch)
 */
public class TeleportCountdown {

    private final Player player;
    private final Location destination;
    private final Countdown countdown;

    private boolean cancelOnMove = true;
    private boolean cancelOnDamage = true;
    private double moveThresholdSquared = 0.01;

    private Location startLocation;
    private double startHealth;

    public TeleportCountdown(Player player, String worldName, double x, double y, double z,
                              float yaw, float pitch, int seconds) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World not found: " + worldName);
        }
        this.player = player;
        this.destination = new Location(world, x, y, z, yaw, pitch);
        this.countdown = new Countdown(seconds);
    }

    public TeleportCountdown(Player player, Location destination, int seconds) {
        this.player = player;
        this.destination = destination.clone();
        this.countdown = new Countdown(seconds);
    }

    public TeleportCountdown cancelOnMove(boolean cancelOnMove) {
        this.cancelOnMove = cancelOnMove;
        return this;
    }

    public TeleportCountdown cancelOnDamage(boolean cancelOnDamage) {
        this.cancelOnDamage = cancelOnDamage;
        return this;
    }

    /** Distance (in blocks) the player may move before the countdown cancels. Default ~0.1 blocks. */
    public TeleportCountdown moveThreshold(double blocks) {
        this.moveThresholdSquared = blocks * blocks;
        return this;
    }

    /**
     * Starts the countdown.
     *
     * @param eachTick    called once per second remaining, unless the tick was a cancel
     * @param onCancelled called if the player moved or took damage before completion (may be null)
     */
    public void start(Consumer<Integer> eachTick, Runnable onCancelled) {
        startLocation = player.getLocation();
        startHealth = player.getHealth();

        countdown.start(seconds -> {
            if (hasMovedOrTookDamage()) {
                countdown.cancel();
                if (onCancelled != null) onCancelled.run();
                return null;
            }
            if (eachTick != null) eachTick.accept(seconds);
            return null;
        }, () -> player.teleport(destination));
    }

    private boolean hasMovedOrTookDamage() {
        boolean moved = cancelOnMove && player.getLocation().distanceSquared(startLocation) > moveThresholdSquared;
        boolean damaged = cancelOnDamage && player.getHealth() < startHealth;
        return moved || damaged;
    }

    public void cancel() {
        countdown.cancel();
    }

    public Location getDestination() {
        return destination.clone();
    }
}
