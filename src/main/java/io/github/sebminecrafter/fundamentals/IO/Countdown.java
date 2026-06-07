package io.github.sebminecrafter.fundamentals.IO;

import io.github.sebminecrafter.fundamentals.Main;

/*
 * Credit to Ilyushya for this code on GitHub:
 * https://gist.github.com/Ilyushya/ab06eb8f6bf071e9fb50e2f56f830b3c
 *
 * Modified by @Sebminecrafter
 */

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Function;

public class Countdown {

    private int seconds;
    private BukkitTask task;

    public Countdown(int seconds) {
        this.seconds = seconds;
    }

    public void start(Function<Integer, Void> eachTime, Runnable onComplete) {
        task = Bukkit.getScheduler().runTaskTimer(Main.getPlugin(Main.class), () -> {
            if (seconds <= 0) {
                // Countdown is complete
                task.cancel();
                onComplete.run();
                return;
            }

            eachTime.apply(seconds);

            seconds -= 1;
        }, 0L, 20L);
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
        }
    }
}