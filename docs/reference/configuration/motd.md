# Server MOTD

The `motd` section changes the Minecraft server-list MOTD.
It is separate from the join MOTD configured under `enabled.chat.motd` and the `/motd` command.

```yaml
motd:
  default: |
    <gradient:#FF4714:#FFFFFF>My Minecraft Server</gradient>
    <gradient:#FFFFFF:#FF4714>Using Fundamentals</gradient>
  full: |
    <gradient:#FF4714:#FFFFFF>My Minecraft Server</gradient>
    <red>Server is full!</red>
```

| Key       | Purpose                                             |
|-----------|-----------------------------------------------------|
| `default` | MOTD shown normally (needs to be set)               |
| `full`    | MOTD shown when the server reaches maximum capacity |

Set either value to `""` to use the server default.
Both MiniMessage-style formatting and Minecraft legacy color codes are supported.
This section can contain up to two lines. Use a YAML block string for a multi-line MOTD.

Enable server-list MOTD handling with `enabled.server-motd`.
Custom join MOTD text is separate and can be managed through [language configuration](../language/).
