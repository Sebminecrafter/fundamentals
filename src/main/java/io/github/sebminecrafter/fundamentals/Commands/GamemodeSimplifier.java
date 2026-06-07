package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.Lang;
import io.github.sebminecrafter.fundamentals.IO.Logging;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamemodeSimplifier implements FundamentalCommand {
    private final Lang lang;
    private final Logging logger;

    public GamemodeSimplifier() {
        this.lang = Main.lang;
        this.logger = Main.logger;
    }

    private GameMode switchGm(String string) {
        GameMode gamemode;
        switch (string) {
            case "s", "survival" -> gamemode = GameMode.SURVIVAL;
            case "c", "creative" -> gamemode = GameMode.CREATIVE;
            case "a", "adventure" -> gamemode = GameMode.ADVENTURE;
            case "sp", "spectator" -> gamemode = GameMode.SPECTATOR;
            default -> gamemode = null;
        }
        return gamemode;
    }

    /*   WARNING!!!
     *   ABSOLUTE SPAGHETTI CODE AHEAD!!!  */

    /// Spaghetti code
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length > 2) {
            return false;
        }
        String name = label.toLowerCase();
        GameMode gamemode;
        Player player;
        switch (name) {
            case "gms" -> gamemode = GameMode.SURVIVAL;
            case "gmc" -> gamemode = GameMode.CREATIVE;
            case "gma" -> gamemode = GameMode.ADVENTURE;
            case "gmsp" -> gamemode = GameMode.SPECTATOR;
            default -> {
                if (args.length == 0) {
                    return false;
                } else if (args.length == 1) {
                    gamemode = switchGm(args[0]);
                } else {
                    gamemode = switchGm(args[1]);
                }
            }
        }
        // Check valid gamemode
        if (gamemode == null) {
            sender.sendMessage(lang.getKey("msgs.invalid"));
            return true;
        }

        if (name.equals("gm") || name.equals("gamemode")) {
            if (args.length > 1) {
                player = Bukkit.getPlayer(args[0]);
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(lang.getKey("msgs.playeronly"));
                    return true;
                }
                player = Bukkit.getPlayer(sender.getName());
            }
        } else {
            if (args.length > 1) {
                return false;
            } else if (args.length == 1) {
                player = Bukkit.getPlayer(args[0]);
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(lang.getKey("msgs.playeronly"));
                    return true;
                }
                player = Bukkit.getPlayer(sender.getName());
            }
        }
        if (player == null) {
            sender.sendMessage(lang.getKey("msgs.offline"));
            return true;
        }
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("VICTIM", player.getName());
        helper.add("GM", gamemode.name());
        player.setGameMode(gamemode);
        player.sendMessage(lang.getKey("staffcmds.gamemode.staff", helper.getReplace()));
        logger.log(lang.getKey("staffcmds.gamemode.log", helper.getReplace()));
        return true;
    }

    /// less but still spaghetti code
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length < 2) {
            Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            for (Player player : onlinePlayers) {
                list.add(player.getName());
            }
        } else if (args.length == 3) {
            list = List.of(
                    "adventure",
                    "creative",
                    "survival",
                    "spectator",
                    "a", "c", "s", "sp"
            );
        } else {
            list = List.of(args[args.length-1]);
        }
        return list;
    }
}
