package io.github.sebminecrafter.fundamentals.Commands.Staff;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static io.github.sebminecrafter.fundamentals.Main.lang;
import static io.github.sebminecrafter.fundamentals.Main.logger;

public class Skull implements FundamentalCommand {
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private CompletableFuture<PlayerProfile> fetchProfile(String name) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(
                "https://api.mojang.com/users/profiles/minecraft/"
                        + URLEncoder.encode(name, StandardCharsets.UTF_8))).build();

        return HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenCompose(res -> {
            if (res.statusCode() != 200) {
                return CompletableFuture.completedFuture(null);
            }
            JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();
            UUID uuid = UUID.fromString(obj.get("id").getAsString().replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"));
            String realName = obj.get("name").getAsString();
            return Bukkit.createPlayerProfile(uuid, realName).update();
        });
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            Commands.safeSend(sender, lang.getKey("msgs.playeronly"));
            return true;
        }
        if (args.length != 1) return false;

        Main plugin = Main.getPlugin(Main.class);

        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", player.getName());
        helper.add("TARGET", args[0]);

        fetchProfile(args[0]).thenAccept(updated ->
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (updated == null || updated.getTextures().isEmpty()) {
                        Commands.safeSend(player, lang.getKey("staffcmds.skull.fail", helper.getReplace()));
                        return;
                    }
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                    if (meta == null) return;
                    meta.setNoteBlockSound(NamespacedKey.minecraft("entity.villager.celebrate"));
                    meta.setOwnerProfile(updated);
                    head.setItemMeta(meta);
                    player.getInventory().addItem(head);
                    Commands.safeSend(player, lang.getKey("staffcmds.skull.staff", helper.getReplace()));
                    logger.log(lang.getKey("staffcmds.skull.log", helper.getReplace()));
                })
        ).exceptionally(ex -> {
            logger.log(Level.WARNING, "Profile lookup exception looking up player '" + args[0] + "' for player " + player.getName() + "\n" + ex.toString());
            return null;
        });
        return true;
    }
}
