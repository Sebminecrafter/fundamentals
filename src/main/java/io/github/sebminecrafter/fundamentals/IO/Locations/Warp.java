package io.github.sebminecrafter.fundamentals.IO.Locations;

import java.util.UUID;

public record Warp(Location location, UUID owner) {
    public UUID world() {
        return location.world();
    }

    public double x() {
        return location.x();
    }

    public double y() {
        return location.y();
    }

    public double z() {
        return location.z();
    }

    public float yaw() {
        return location.yaw();
    }

    public float pitch() {
        return location.pitch();
    }
}
