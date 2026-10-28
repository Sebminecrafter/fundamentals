package io.github.sebminecrafter.fundamentals.Commands.Staff;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class ServerStat implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        PlaceholderHelper helper = new PlaceholderHelper();

        helper.add("ONLINE",   Integer.toString(Bukkit.getOnlinePlayers().size()));
        helper.add("MAX",      Integer.toString(Bukkit.getMaxPlayers()));
        helper.add("VERSION",  Bukkit.getVersion());
        helper.add("MEMTOTAL", bytesToMbString(Runtime.getRuntime().totalMemory()));
        helper.add("MEMFREE",  bytesToMbString(Runtime.getRuntime().freeMemory()));
        helper.add("MEMUSED",  bytesToMbString(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        helper.add("WORLDS",   Integer.toString(Bukkit.getWorlds().size()));
        helper.add("UPTIME",   Integer.toString((int)((System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()) / 1000)));
        helper.add("TPS1M",    tpsString(0));
        helper.add("TPS5M",    tpsString(1));
        helper.add("TPS15M",   tpsString(2));

        Commands.safeSend(sender, lang.getKey("staffcmds.serverstat", helper.getReplace()));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    private static String tpsString(int i) {
        double[] tps = getTPS();

        if (tps != null && tps.length > i) {
            DecimalFormat format = new DecimalFormat("#.#");
            return format.format(tps[i]);
        } else {
            return "?";
        }
    }

    private static String bytesToMbString(long bytes) {
        return Integer.toString((int)(bytes / 1_048_576));
    }

    private static double[] getTPS() {
        try {
            // getTPS() sometimes exists, only on certain software though
            try {
                Method method = Bukkit.getServer().getClass().getMethod("getTPS");
                return (double[]) method.invoke(Bukkit.getServer());
            } catch (NoSuchMethodException ignored) {
                // No getTPS() method
            }

            // Try to use spigots' recentTps
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            String version = packageName.substring(packageName.lastIndexOf('.') + 1);

            Class<?> minecraftServerClass = Class.forName(
                    "net.minecraft.server." + version + ".MinecraftServer"
            );

            Method getServer = minecraftServerClass.getMethod("getServer");
            Object server = getServer.invoke(null);

            Field recentTps = minecraftServerClass.getField("recentTps");
            return (double[]) recentTps.get(server);
        } catch (ReflectiveOperationException | ClassCastException | SecurityException e) {
            return null;
        }
    }
}
