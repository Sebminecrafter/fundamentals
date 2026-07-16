package io.github.sebminecrafter.fundamentals.IO.Homes;

import java.util.UUID;

public record Home(
        UUID world,
        double x,
        double y,
        double z,
        float yaw,
        float pitch
) {}