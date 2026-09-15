package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

import java.util.HashSet;
import java.util.Set;

import static io.github.sebminecrafter.fundamentals.Main.*;

public class BigNuke implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        Player target;
        Player p;
        if (args.length == 0) {
            if (!(sender instanceof Player pl)) {
                return false;
            }
            p = pl;
        } else if (args.length == 1) {
            p = Bukkit.getPlayerExact(args[0]);
            if (p == null) {
                Commands.safeSend(sender, lang.getKey("msgs.offline"));
                return true;
            }
        } else {
            return false;
        }
        target = p;

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("TARGET", target.getName());
        Commands.safeSend(sender, lang.getKey("staffcmds.bignuke.staff", helper.getReplace()));
        logger.log(lang.getKey("staffcmds.bignuke.log", helper.getReplace()));

        World world = target.getWorld();
        Location loc = target.getLocation();

        int distance = config.getInt("bignuke.distance");

        // Spawn center TNT
        world.spawn(loc, TNTPrimed.class);

        for (int radius = 1; radius < distance; radius += 2) {
            int x = 0;
            int z = radius;
            int d = 3 - 2 * radius;

            Set<Location> points = new HashSet<>();

            while (x <= z) {
                addCirclePoint(points, world, loc, x, z);
                addCirclePoint(points, world, loc, x, -z);
                addCirclePoint(points, world, loc, -x, z);
                addCirclePoint(points, world, loc, -x, -z);
                addCirclePoint(points, world, loc, z, x);
                addCirclePoint(points, world, loc, z, -x);
                addCirclePoint(points, world, loc, -z, x);
                addCirclePoint(points, world, loc, -z, -x);

                if (d < 0) {
                    d += 4 * x + 6;
                } else {
                    d += 4 * (x - z) + 10;
                    z--;
                }
                x++;
            }

            for (Location circleLoc : points) {
                world.spawn(circleLoc, TNTPrimed.class);
            }
        }
        return true;
    }

    private void addCirclePoint(Set<Location> points, World world, Location origin, int dx, int dz) {
        points.add(new Location(world,
                Math.round(origin.getX()) + dx,
                origin.getY(),
                Math.round(origin.getZ()) + dz));
    }
}