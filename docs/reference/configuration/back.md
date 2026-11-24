# Back

The `back` section controls `/back`, which returns players to their previous location
(before their last teleport, or where they died).

```yaml
back:
  delay: 5
  cooldown: 30
  on-death: true
  ignored-causes: ["ENDER_PEARL", "CHORUS_FRUIT", "SPECTATE"]
```

| Key              | Default                                | Purpose                                                    |
|------------------|----------------------------------------|------------------------------------------------------------|
| `delay`          | `5`                                    | Countdown before teleporting, in seconds                   |
| `cooldown`       | `30`                                   | Per-player cooldown, in seconds                            |
| `on-death`       | `true`                                 | Remember the death location for `/back`                    |
| `ignored-causes` | `ENDER_PEARL`, `CHORUS_FRUIT`, `SPECTATE` | Teleport causes that do not update the `/back` location |

Enable or disable `/back` with `enabled.cmds.back`.
Saving the death location also requires the `fundamentals.player.back.ondeath` permission.

Locations are stored in memory only and are cleared when a player leaves or the server restarts.
Running `/back` again returns the player to where they were before using it.

Valid causes are listed in the
[Spigot `TeleportCause` docs](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/player/PlayerTeleportEvent.TeleportCause.html).
