package io.github.sebminecrafter.fundamentals.IO;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static io.github.sebminecrafter.fundamentals.Main.config;

public class FundamentalSounds {
    public static void tryPlaySound(Player receiver, String sound, float volume, float pitch) {
        Sound receiveSound = Registry.SOUNDS.get(NamespacedKey.minecraft(sound));
        if (receiveSound != null) {
            receiver.playSound(receiver, receiveSound, volume, pitch);
        }
    }
    public static void tryPlaySoundFromConfig(Player receiver, String path, float volume, float pitch) {
        tryPlaySound(receiver, config.getString(path), volume, pitch);
    }
    public static void tPSFCSimpler(Player receiver, String path) {
        tryPlaySoundFromConfig(receiver, path, 1, 1);
    }
}
