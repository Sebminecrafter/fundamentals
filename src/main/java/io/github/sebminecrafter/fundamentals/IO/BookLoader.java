package io.github.sebminecrafter.fundamentals.IO;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class BookLoader {
    private final static int MAX_LENGTH = 1024;
    private final File file;
    private final String title;
    private final String author;
    private String text;
    private ItemStack currentBook;

    public BookLoader(File file, String title, String author) {
        this.file = file;
        this.title = title;
        this.author = author;
    }

    public void load() {
        loadText();
        loadBook();
    }

    public ItemStack getBook() {
        return currentBook;
    }

    private void loadBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta iMeta = book.getItemMeta();
        if (!(iMeta instanceof BookMeta meta)) {
            currentBook = currentBook == null ? book : currentBook;
            return;
        }
        meta.setTitle(title);
        meta.setAuthor(author);

        // Iterate through rules text and form book
        int length = 0;
        StringBuilder currentPage = new StringBuilder();
        for (String word : text.split(" ")) {
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
            else if (!currentPage.isEmpty() && !currentPage.substring(currentPage.length() - 1).equals("\n")) word = " " + word;

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

        currentBook = book;
    }

    private void loadText() {
        String prevText = (text != null ? text : "");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            text = reader.readAllAsString();
        } catch (IOException e) {
            text = prevText;
            logger.log("Failed to load rules");
        }
        // Remove line endings as they cause issues with parsing. Replace with spaces.
        text = text.replace("\r", "")
            .replace("\n", " ");
        text = lang.formatColors(text);
    }
}
