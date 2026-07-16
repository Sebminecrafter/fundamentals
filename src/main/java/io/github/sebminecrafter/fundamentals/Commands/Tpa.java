package io.github.sebminecrafter.fundamentals.Commands;

import io.github.sebminecrafter.fundamentals.IO.*;
import io.github.sebminecrafter.fundamentals.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Tpa implements FundamentalCommand {
    private final Lang lang;
    private final Logging logger;
    private final Config config;
    private final long requestExpiry;
    private final HashMap<UUID, UUID> tparequests;
    private final HashMap<UUID, UUID> tpahererequests;
    private final HashMap<UUID, BukkitTask> tpatasks;
    private final HashMap<UUID, BukkitTask> tpaheretasks;
    private final int countdownTime;
    private final Ignore ignore;

    public Tpa(long requestExpiry, Ignore ignore) {
        this.lang = Main.lang;
        this.logger = Main.logger;
        this.config = Main.config;

        this.requestExpiry = requestExpiry;
        this.tparequests = new HashMap<>();
        this.tpahererequests = new HashMap<>();
        this.tpatasks = new HashMap<>();
        this.tpaheretasks = new HashMap<>();
        this.countdownTime = config.getInt("tpa.delay");
        this.ignore = ignore;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (label.equalsIgnoreCase("tpa")) {
            Player player;
            if (!(sender instanceof Player executor)) {
                sender.sendMessage(lang.getKey("msgs.playeronly"));
                return true;
            } else if (args.length != 1) {
                return false;
            } else {
                player = Bukkit.getPlayerExact(args[0]);
                if (player == null) {
                    sender.sendMessage(lang.getKey("msgs.offline"));
                    return true;
                } else if (player == executor) {
                    sender.sendMessage(lang.getKey("msgs.self"));
                    return true;
                } else if (ignore.isIgnoring(player.getUniqueId(), executor.getUniqueId())) {
                    sender.sendMessage(lang.getKey("msgs.ignored"));
                    return true;
                }
            }
            sendTpRequest(executor, player);
            return true;
        } else if (label.equalsIgnoreCase("tpahere")) {
            if (!config.isEnabled("cmds.tpahere")) {
                sender.sendMessage(lang.getKey("msgs.disabled"));
                return true;
            }
            Player player;
            if (!(sender instanceof Player executor)) {
                sender.sendMessage(lang.getKey("msgs.playeronly"));
                return true;
            } else if (args.length != 1) {
                return false;
            } else {
                player = Bukkit.getPlayerExact(args[0]);
                if (player == null) {
                    sender.sendMessage(lang.getKey("msgs.offline"));
                    return true;
                } else if (player == executor) {
                    sender.sendMessage(lang.getKey("msgs.self"));
                    return true;
                } else if (ignore.isIgnoring(player.getUniqueId(), executor.getUniqueId())) {
                    sender.sendMessage(lang.getKey("msgs.ignored"));
                    return true;
                }
            }
            sendTpHereRequest(executor, player);
            return true;
        } else if (label.equalsIgnoreCase("tpaccept")) {
            if (!(sender instanceof Player executor)) {
                sender.sendMessage(lang.getKey("msgs.playeronly"));
                return true;
            } else if (args.length != 0) {
                return false;
            }
            acceptTpRequest(executor);
            return true;
        } else if (label.equalsIgnoreCase("tpdeny")) {
            if (!(sender instanceof Player executor)) {
                sender.sendMessage(lang.getKey("msgs.playeronly"));
                return true;
            } else if (args.length != 0) {
                return false;
            }
            denyTpRequest(executor);
            return true;
        } else if (label.equalsIgnoreCase("tpacancel")) {
            if (!(sender instanceof Player executor)) {
                sender.sendMessage(lang.getKey("msgs.playeronly"));
                return true;
            } else if (args.length != 0) {
                return false;
            }
            removeTpRequest(executor);
            return true;
        }
        return false;
    }

    private void sendTpRequest(Player sender, Player receiver) {
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("OTHER", receiver.getName());
        List<List<String>> replace = helper.getReplace();
        if (tparequests.containsValue(sender.getUniqueId()) ||
                tpahererequests.containsValue(sender.getUniqueId())) {
            sender.sendMessage(lang.getKey("cmds.tpa.multiple", replace));
        } else {
            tparequests.put(receiver.getUniqueId(), sender.getUniqueId());
            logger.log(lang.getKey("cmds.tpa.request.tpa.log", replace));
            sender.sendMessage(lang.getKey("cmds.tpa.request.tpa.sent", replace));
            receiver.sendMessage(lang.getKey("cmds.tpa.request.tpa.receive", replace));

            BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () ->
                    expireTpRequest(receiver.getUniqueId(), sender.getUniqueId()), requestExpiry);
            tpatasks.put(receiver.getUniqueId(), task);
        }
    }

    private void sendTpHereRequest(Player sender, Player receiver) {
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        helper.add("OTHER", receiver.getName());
        List<List<String>> replace = helper.getReplace();
        if (tparequests.containsValue(sender.getUniqueId()) ||
                tpahererequests.containsValue(sender.getUniqueId())) {
            sender.sendMessage(lang.getKey("cmds.tpa.multiple", replace));
        } else {
            tpahererequests.put(receiver.getUniqueId(), sender.getUniqueId());
            logger.log(lang.getKey("cmds.tpa.request.tpahere.log", replace));
            sender.sendMessage(lang.getKey("cmds.tpa.request.tpahere.sent", replace));
            receiver.sendMessage(lang.getKey("cmds.tpa.request.tpahere.receive", replace));

            BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () ->
                    expireTpHereRequest(receiver.getUniqueId(), sender.getUniqueId()), requestExpiry);
            tpaheretasks.put(receiver.getUniqueId(), task);
        }
    }

    // Called by the scheduler when a /tpa request expires
    private void expireTpRequest(UUID receiverUuid, UUID senderUuid) {
        if (!tparequests.containsKey(receiverUuid)) return; // Already handled

        PlaceholderHelper helper = new PlaceholderHelper();
        Player sender = Bukkit.getPlayer(senderUuid);
        Player receiver = Bukkit.getPlayer(receiverUuid);

        if (sender != null) helper.add("PLAYER", sender.getName());
        if (receiver != null) helper.add("OTHER", receiver.getName());

        logger.log(lang.getKey("cmds.tpa.expired.log", helper.getReplace()));
        if (sender != null) sender.sendMessage(lang.getKey("cmds.tpa.expired.sent", helper.getReplace()));
        if (receiver != null) receiver.sendMessage(lang.getKey("cmds.tpa.expired.receive", helper.getReplace()));

        tparequests.remove(receiverUuid);
        tpatasks.remove(receiverUuid);
    }

    // Called by the scheduler when a /tpahere request expires
    private void expireTpHereRequest(UUID receiverUuid, UUID senderUuid) {
        if (!tpahererequests.containsKey(receiverUuid)) return; // Already handled

        PlaceholderHelper helper = new PlaceholderHelper();
        Player sender = Bukkit.getPlayer(senderUuid);
        Player receiver = Bukkit.getPlayer(receiverUuid);

        if (sender != null) helper.add("PLAYER", sender.getName());
        if (receiver != null) helper.add("OTHER", receiver.getName());

        logger.log(lang.getKey("cmds.tpa.expired.log", helper.getReplace()));
        if (sender != null) sender.sendMessage(lang.getKey("cmds.tpa.expired.sent", helper.getReplace()));
        if (receiver != null) receiver.sendMessage(lang.getKey("cmds.tpa.expired.receive", helper.getReplace()));

        tpahererequests.remove(receiverUuid);
        tpaheretasks.remove(receiverUuid);
    }

    private void removeTpRequest(Player sender) {
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", sender.getName());
        if (tparequests.containsKey(sender.getUniqueId())) {
            Player receiver = Bukkit.getPlayer(tparequests.get(sender.getUniqueId()));
            if (receiver != null) {
                helper.add("OTHER", receiver.getName());
            }
            logger.log(lang.getKey("cmds.tpa.cancelled.log", helper.getReplace()));
            sender.sendMessage(lang.getKey("cmds.tpa.cancelled.sent", helper.getReplace()));
            if (receiver != null) {
                receiver.sendMessage(lang.getKey("cmds.tpa.cancelled.receive", helper.getReplace()));
            }
            cancelTask(tpatasks, sender.getUniqueId());
            tparequests.remove(sender.getUniqueId());
        } else if (tpahererequests.containsKey(sender.getUniqueId())) {
            Player receiver = Bukkit.getPlayer(tpahererequests.get(sender.getUniqueId()));
            if (receiver != null) {
                helper.add("OTHER", receiver.getName());
            }
            logger.log(lang.getKey("cmds.tpa.cancelled.log", helper.getReplace()));
            sender.sendMessage(lang.getKey("cmds.tpa.cancelled.sent", helper.getReplace()));
            if (receiver != null) {
                receiver.sendMessage(lang.getKey("cmds.tpa.cancelled.receive", helper.getReplace()));
            }
            cancelTask(tpaheretasks, sender.getUniqueId());
            tpahererequests.remove(sender.getUniqueId());
        } else {
            sender.sendMessage(lang.getKey("cmds.tpa.none"));
        }
    }

    private void acceptTpRequest(Player receiver) {
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", receiver.getName());

        if (tparequests.containsKey(receiver.getUniqueId())) {
            Player requester = Bukkit.getPlayer(tparequests.get(receiver.getUniqueId()));
            if (requester == null) {
                receiver.sendMessage(lang.getKey("msgs.offline"));
                cancelTask(tpatasks, receiver.getUniqueId());
                tparequests.remove(receiver.getUniqueId());
                return;
            }
            helper.add("OTHER", requester.getName());
            logger.log(lang.getKey("cmds.tpa.accepted.log", helper.getReplace()));
            receiver.sendMessage(lang.getKey("cmds.tpa.accepted.sent", helper.getReplace()));
            requester.sendMessage(lang.getKey("cmds.tpa.accepted.receive", helper.getReplace()));
            cancelTask(tpatasks, receiver.getUniqueId());
            tparequests.remove(receiver.getUniqueId());
            TeleportCountdown teleportCountdown = new TeleportCountdown(requester, receiver.getLocation(), countdownTime);
            teleportCountdown.start(seconds -> sendCountdownActionBar(requester, receiver, seconds), null);
        } else if (tpahererequests.containsKey(receiver.getUniqueId())) {
            Player requester = Bukkit.getPlayer(tpahererequests.get(receiver.getUniqueId()));
            if (requester == null) {
                receiver.sendMessage(lang.getKey("msgs.offline"));
                cancelTask(tpaheretasks, receiver.getUniqueId());
                tpahererequests.remove(receiver.getUniqueId());
                return;
            }
            helper.add("OTHER", requester.getName());
            logger.log(lang.getKey("cmds.tpa.accepted.log", helper.getReplace()));
            receiver.sendMessage(lang.getKey("cmds.tpa.accepted.sent", helper.getReplace()));
            requester.sendMessage(lang.getKey("cmds.tpa.accepted.receive", helper.getReplace()));
            cancelTask(tpaheretasks, receiver.getUniqueId());
            tpahererequests.remove(receiver.getUniqueId());
            TeleportCountdown teleportCountdown = new TeleportCountdown(receiver, requester.getLocation(), countdownTime);
            teleportCountdown.start(seconds -> sendCountdownActionBar(receiver, requester, seconds), null);
        } else {
            receiver.sendMessage(lang.getKey("cmds.tpa.none"));
        }
    }

    private void denyTpRequest(Player receiver) {
        PlaceholderHelper helper = new PlaceholderHelper();
        helper.add("PLAYER", receiver.getName());
        if (tparequests.containsKey(receiver.getUniqueId())) {
            Player requester = Bukkit.getPlayer(tparequests.get(receiver.getUniqueId()));
            if (requester != null) {
                helper.add("OTHER", requester.getName());
            }
            logger.log(lang.getKey("cmds.tpa.denied.log", helper.getReplace()));
            receiver.sendMessage(lang.getKey("cmds.tpa.denied.sent", helper.getReplace()));
            if (requester != null) {
                requester.sendMessage(lang.getKey("cmds.tpa.denied.receive", helper.getReplace()));
            }
            cancelTask(tpatasks, receiver.getUniqueId());
            tparequests.remove(receiver.getUniqueId());
        } else if (tpahererequests.containsKey(receiver.getUniqueId())) {
            Player requester = Bukkit.getPlayer(tpahererequests.get(receiver.getUniqueId()));
            if (requester != null) {
                helper.add("OTHER", requester.getName());
            }
            logger.log(lang.getKey("cmds.tpa.denied.log", helper.getReplace()));
            receiver.sendMessage(lang.getKey("cmds.tpa.denied.sent", helper.getReplace()));
            if (requester != null) {
                requester.sendMessage(lang.getKey("cmds.tpa.denied.receive", helper.getReplace()));
            }
            cancelTask(tpaheretasks, receiver.getUniqueId());
            tpahererequests.remove(receiver.getUniqueId());
        } else {
            receiver.sendMessage(lang.getKey("cmds.tpa.none"));
        }
    }

    private void cancelTask(HashMap<UUID, BukkitTask> taskMap, UUID uuid) {
        BukkitTask task = taskMap.remove(uuid);
        if (task != null) task.cancel();
    }

    private void sendCountdownActionBar(Player teleportingPlayer, Player otherPlayer, int seconds) {
        PlaceholderHelper countdownHelper = new PlaceholderHelper();
        countdownHelper.add("PLAYER", teleportingPlayer.getName());
        countdownHelper.add("OTHER", otherPlayer.getName());
        countdownHelper.add("SECS", String.valueOf(seconds));
        teleportingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(lang.getKey("cmds.tpa.countdown", countdownHelper.getReplace())));
    }
}