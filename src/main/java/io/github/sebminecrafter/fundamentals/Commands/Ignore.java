package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class Ignore implements FundamentalCommand {
    private final Lang lang;
    private final Logging logger;
    private final File ignorelistFile;
    private final YamlConfiguration ignorelist;

    public Ignore(JavaPlugin plugin) {
        this.lang = Main.lang;
        this.logger = Main.logger;
        this.ignorelistFile = new File(plugin.getDataFolder(), "ignorelist.yml");
        this.ignorelist = new YamlConfiguration();

        if (ignorelistFile.exists()) {
            try {
                ignorelist.load(ignorelistFile);
            } catch (IOException | InvalidConfigurationException e) {
                logger.log(Level.SEVERE, "ignorelist.yml could not be loaded!");
            }
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length != 1) {
            return false;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.getKey("msgs.playeronly"));
            return true;
        }
        Player other = Bukkit.getPlayerExact(args[0]);
        if (other == null) {
            sender.sendMessage(lang.getKey("msgs.offline"));
            return true;
        } else if (player == other) {
            sender.sendMessage(lang.getKey("msgs.self"));
            return true;
        }
        UUID playerUUID = player.getUniqueId();
        UUID otherUUID = other.getUniqueId();
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("OTHER", other.getName());
        if (isIgnoring(playerUUID, otherUUID)) {
            unignore(playerUUID, otherUUID);
            logger.log(lang.getKey("cmds.ignore.unignore.log", helper.getReplace()));
            player.sendMessage(lang.getKey("cmds.ignore.unignore.send", helper.getReplace()));
        } else {
            ignore(playerUUID, otherUUID);
            logger.log(lang.getKey("cmds.ignore.ignore.log", helper.getReplace()));
            player.sendMessage(lang.getKey("cmds.ignore.ignore.send", helper.getReplace()));
        }
        if (!saveFile()) {
            sender.sendMessage(lang.getKey("msgs.fileerror"));
        }
        return true;
    }

    public boolean isIgnoring(UUID player, UUID other) {
        return (ignorelist.contains(player.toString()) &&
                Objects.requireNonNull(
                    ignorelist.getList(player.toString()))
                        .contains(other.toString()));
    }

    private void ignore(UUID player, UUID other) {
        if (ignorelist.contains(player.toString())) {
            List<String> list = ignorelist.getStringList(player.toString());
            list.add(other.toString());
            ignorelist.set(player.toString(), list);
        } else {
            ignorelist.set(player.toString(), List.of(other.toString()));
        }
    }

    private void unignore(UUID player, UUID other) {
        if (ignorelist.contains(player.toString())) {
            List<String> list = ignorelist.getStringList(player.toString());
            list.remove(other.toString());
            ignorelist.set(player.toString(), list);
        }
    }

    private boolean saveFile() {
        try {
            ignorelist.save(ignorelistFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ignorelist.yml could not be saved!");
            return false;
        }
        return true;
    }
}
