package io.github.sebminecrafter.fundamentals.Commands.Player;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.BookLoader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Info implements FundamentalCommand {
    private final BookLoader bookLoader;

    public Info(JavaPlugin plugin) {
        File infoFile = new File(plugin.getDataFolder(), "info.txt");
        if (!infoFile.exists()) plugin.saveResource("info.txt", false);

        bookLoader = new BookLoader(infoFile, lang.getKey("cmds.info.title"), lang.getKey("cmds.info.author"));
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
