package io.github.sebminecrafter.fundamentals.Commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Rules implements FundamentalCommand {
    private final static int MAX_LENGTH = 1024;
    private final File rulesFile;
    private String rulesText;

    public Rules(JavaPlugin plugin) {
        this.rulesFile = new File(plugin.getDataFolder(), "rules.txt");
        plugin.saveResource("rules.txt", false);

        loadRules();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player p)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        ItemMeta iMeta = book.getItemMeta();
        if (!(iMeta instanceof BookMeta meta)) {
            Commands.safeSend(p, lang.getKey("msgs.generalerror"));
            return true;
        }
        meta.setTitle("Server Rules");
        meta.setAuthor("Fundamentals/Server");

        // Iterate through rules text and form book
        int length = 0;
        StringBuilder currentPage = new StringBuilder();
        for (String word : rulesText.split(" ")) {
            if (word.equals("NEXT_PAGE")) {
                meta.addPage(currentPage.toString());
                currentPage = new StringBuilder();
                length = 0;
                continue;
            } else if (word.equals("NEW_LINE")) {
                currentPage.append("\n");
                length += 73;
                continue;
            }

            if (word.isBlank()) continue;
            else if (length != 0) word = " " + word;

            if (word.length() >= (MAX_LENGTH - length)) {
                word = word.substring(0, MAX_LENGTH - length - 3) + "...";
            }
            length += word.length();
            if (length >= MAX_LENGTH) {
                meta.addPage(currentPage.toString());
                currentPage = new StringBuilder(word);
                length = word.length();
            } else {
                currentPage.append(word);
            }
        }
        if (!currentPage.isEmpty()) {
            meta.addPage(currentPage.toString());
        }

        book.setItemMeta(meta);

        p.openBook(book);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    public void loadRules() {
        String prevRulesText = (rulesText != null ? rulesText : "");
        try (BufferedReader reader = new BufferedReader(new FileReader(rulesFile))) {
            rulesText = reader.readAllAsString();
        } catch (IOException e) {
            rulesText = prevRulesText;
            logger.log("Failed to load rules");
        }
        // Remove line endings as they cause issues. Replace with spaces.
        rulesText = rulesText.replace("\r", "");
        rulesText = rulesText.replace("\n", " ");
    }
}
