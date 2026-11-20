# Random Teleport

The `rtp` section controls `/rtp` safety limits and destination selection.

```yaml
rtp:
  delay: 5
  cooldown: 30
  no-go: 100
  max: -1
  worlds: ["minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"]
  allow-others: true
  max-attempts: 20
```

| Key            | Default                    | Purpose                                                  |
|----------------|----------------------------|----------------------------------------------------------|
| `delay`        | `5`                        | Countdown before teleporting, in seconds                 |
| `cooldown`     | `30`                       | Per-player cooldown, in seconds                          |
| `no-go`        | `100`                      | Minimum distance away from spawn                         |
| `max`          | `-1`                       | Maximum destination distance; `-1` uses the world border |
| `worlds`       | Overworld, Nether, and End | Worlds eligible for random teleport                      |
| `allow-others` | `true`                     | Allows `/rtp` with a world argument                      |
| `max-attempts` | `20`                       | Maximum locations searched before stopping               |

Enable or disable `/rtp` with `enabled.cmds.rtp`.
World names use Minecraft resource locations such as `minecraft:overworld`.
