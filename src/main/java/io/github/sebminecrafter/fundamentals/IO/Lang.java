package io.github.sebminecrafter.fundamentals.IO;

import io.github.sebminecrafter.fundamentals.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lang {
    private final YamlConfiguration config;
    private final MiniMessage miniMessage;
    private final LegacyComponentSerializer legacy;
    private final Pattern GRADIENT;


    public Lang(JavaPlugin plugin) {
        this.config = YamlLoader.load(plugin, Main.logger, "lang.yml");
        this.miniMessage = MiniMessage.miniMessage(MiniMessage.Preset.NON_INTERACTABLE);
        this.legacy = LegacyComponentSerializer.builder()
                .character('§').hexColors().useUnusualXRepeatedCharacterHexFormat().build();
        this.GRADIENT = Pattern.compile(
                "<gradient((?::#[0-9A-Fa-f]{6})+)>(.*?)</gradient>", Pattern.DOTALL);
    }

    public String formatColors(String input) {
        if (input == null || input.isEmpty()) return input;

        // 1. Expand <gradient:#hex:#hex[...]>text</gradient> to §x sequences
        Matcher m = GRADIENT.matcher(input);
        StringBuilder gb = new StringBuilder();
        while (m.find()) {
            String[] stopHex = m.group(1).substring(1).split(":");
            List<Color> stops = new ArrayList<>();
            for (String h : stopHex) stops.add(new Color(Integer.parseInt(h.substring(1), 16)));

            String text = m.group(2);
            List<Integer> vis = new ArrayList<>();
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == '§') { i += (i + 1 < text.length() && Character.toLowerCase(text.charAt(i + 1)) == 'x') ? 13 : 1; }
                else vis.add(i);
            }

            StringBuilder grad = new StringBuilder();
            int last = 0;
            for (int p = 0; p < vis.size(); p++) {
                int idx = vis.get(p);
                grad.append(text, last, idx);
                double t = vis.size() == 1 ? 0 : (double) p / (vis.size() - 1);
                double s = t * (stops.size() - 1);
                int lo = (int) s, hi = Math.min(lo + 1, stops.size() - 1);
                double f = s - lo;
                Color a = stops.get(lo), b = stops.get(hi);
                int r = (int) Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * f);
                int g = (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * f);
                int bl = (int) Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * f);
                String hex = String.format("%06X", new Color(r, g, bl).getRGB() & 0xFFFFFF);
                StringBuilder code = new StringBuilder("§x");
                for (char ch : hex.toCharArray()) code.append('§').append(ch);
                grad.append(code).append(text.charAt(idx));
                last = idx + 1;
            }
            if (last < text.length()) grad.append(text, last, text.length());
            m.appendReplacement(gb, Matcher.quoteReplacement(grad.toString()));
        }
        m.appendTail(gb);

        // 2. Hide pre-built §x sequences from MiniMessage, parse remaining tags, then restore
        String afterMini = legacy.serialize(miniMessage.deserialize(
                gb.toString().replace("§", "\uE000")
        )).replace("\uE000", "§");

        // 3. Translate & codes last so § never enters MiniMessage's parser
        return ChatColor.translateAlternateColorCodes('&', afterMini);
    }

    /** Get the translation for `key`
     * @param key The translation key path (like `command.example.response`)
     * */
    public String getKey(String key) {
        String string = _getKey(key);
        string = formatColors(string);
        return string;
    }

    /** Get the translation for `key`, with placeholders
     * @param key The translation key path (like `command.example.response`)
     * @param list List of Lists, each should be a pair of String (value) and String (replacement)
     *  */
    public String getKey(String key, List< List<String> > list) {
        String string = _getKey(key);
        for (List<String> stringList : list) {
            if (stringList.size() < 2) {
                continue;
            }
            string = string.replace(stringList.getFirst(), stringList.getLast());
        }
        string = formatColors(string);
        return string;
    }

    // Internal method to simplify code
    private String _getKey(String key) {
        String crKey = config.getString(key);
        if (crKey == null) {
            crKey = key;
        }
        return crKey;
    }
}
