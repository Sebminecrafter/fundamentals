# TPA, Homes, and Warps

TPA, home, and warp behavior is configured by separate sections.

## TPA

| Key | Default | Purpose |
| --- | --- | --- |
| `tpa.delay` | `5` | Countdown before teleporting, in seconds |
| `tpa.cooldown` | `30` | Per-player cooldown, in seconds |
| `tpa.expiresafter` | `600` | Request lifetime in ticks (30 seconds) |

Set a delay or cooldown to `0` to remove that wait.

```yaml
tpa:
  delay: 5
  cooldown: 30
  expiresafter: 600
```

## Homes

| Key             | Default | Purpose                      |
|-----------------|---------|------------------------------|
| `home.delay`    | `5`     | Countdown before teleporting |
| `home.cooldown` | `30`    | Per-player cooldown          |
| `home.max`      | `3`     | Maximum homes per player     |

Each command toggle is controlled by `enabled.cmds.home`.

```yaml
home:
  delay: 5
  cooldown: 30
  max: 3
```

## Warps

| Key             | Default | Purpose                                     |
|-----------------|---------|---------------------------------------------|
| `warp.delay`    | `5`     | Countdown before teleporting                |
| `warp.cooldown` | `30`    | Per-player cooldown                         |
| `warp.max`      | `1`     | Maximum warps per player; `-1` is unlimited |

Only players with `fundamentals.staff.setwarp` can create warps.

```yaml
warp:
  delay: 5
  cooldown: 30
  max: 1
```

See [Commands](../commands/) and [Permissions](../permissions/) for the related commands and permissions.
