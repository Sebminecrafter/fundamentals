package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.BookLoader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Rules implements FundamentalCommand {
    private final BookLoader bookLoader;

    public Rules(JavaPlugin plugin) {
        File rulesFile = new File(plugin.getDataFolder(), "rules.txt");
        plugin.saveResource("rules.txt", false);

        bookLoader = new BookLoader(rulesFile, lang.getKey("cmds.rules.title"), lang.getKey("cmds.rules.author"));
        bookLoader.load();
    }

    public void reload() {
        bookLoader.load();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player p)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }

        p.openBook(bookLoader.getBook());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
