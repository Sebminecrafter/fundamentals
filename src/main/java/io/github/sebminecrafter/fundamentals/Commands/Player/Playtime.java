package io.github.sebminecrafter.fundamentals.Commands.Player;

import io.github.sebminecrafter.fundamentals.Commands.Commands;
import io.github.sebminecrafter.fundamentals.Commands.FundamentalCommand;
import io.github.sebminecrafter.fundamentals.IO.PlaceholderHelper;
import io.github.sebminecrafter.fundamentals.IO.PlayerInfo;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

import static io.github.sebminecrafter.fundamentals.Main.lang;

public class Playtime implements FundamentalCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (args.length == 0 && sender instanceof Player player) {
            PlaceholderHelper helper = getHelperForTicks(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
            helper.add("player", player.getName());

            Commands.safeSend(player, lang.getKey("cmds.playtime.self", helper.getReplace()));

            return true;
        } else if (args.length == 1) {
            Optional<OfflinePlayer> target = PlayerInfo.getOfflinePlayer(args[0]);

            if (target.isEmpty()) {
                PlaceholderHelper tempHelper = new PlaceholderHelper();
                tempHelper.add("player", args[0]);
                Commands.safeSend(sender, lang.getKey("msgs.playernotfound", tempHelper.getReplace()));
                return true;
            }

            OfflinePlayer player = target.get();

            PlaceholderHelper helper = getHelperForTicks(target.get().getStatistic(Statistic.PLAY_ONE_MINUTE));
            helper.add("player", player.getName());

            Commands.safeSend(sender, lang.getKey("cmds.playtime.other", helper.getReplace()));

            return true;
        }
        return false;
    }

    private static PlaceholderHelper getHelperForTicks(long ticks) {
        long seconds = ticks / 20L;
        PlaceholderHelper helper = new PlaceholderHelper();
        Duration duration = Duration.ofSeconds(seconds);

        helper.add("seconds", Integer.toString(duration.toSecondsPart()));
        helper.add("minutes", Integer.toString(duration.toMinutesPart()));
        helper.add("hours", Integer.toString(duration.toHoursPart()));
        helper.add("days", Long.toString(duration.toDaysPart()));

        helper.add("total-seconds", Long.toString(duration.toSeconds()));
        helper.add("total-minutes", Long.toString(duration.toMinutes()));
        helper.add("total-hours", Long.toString(duration.toHours()));

        return helper;
    }
}
