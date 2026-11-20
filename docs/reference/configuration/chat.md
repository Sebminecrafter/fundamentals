# Chat Configuration

Chat features and filtering are configured under `enabled.chat`.

```yaml
enabled:
  # ...
  chat:
    globalchat: true
    customjl: true
    customdeaths: true
    motd: true
    allowlinks: false
    colors: true
    needpermforcolors: false
    disallowedregex: "..."
```

| Key                 | Default          | Purpose                                                   |
|---------------------|------------------|-----------------------------------------------------------|
| `globalchat`        | `true`           | Enables Fundamentals global chat handling                 |
| `customjl`          | `true`           | Enables custom join/leave messages                        |
| `customdeaths`      | `true`           | Enables custom death messages                             |
| `motd`              | `true`           | Enables the join MOTD (message of the day)                |
| `allowlinks`        | `false`          | Allows messages matching a URL pattern                    |
| `colors`            | `true`           | Enables automatic MiniMessage and legacy color formatting |
| `needpermforcolors` | `false`          | Whether to require a permission to use colors in chat     |
| `disallowedregex`   | check config.yml | Regex used to block disallowed language                   |

When `needpermforcolors` is `true`, players need `fundamentals.player.chat.colors` to use colors.
See [Permissions](../permissions/) for the complete permission reference.

!!! warning "Edit regular expressions carefully"
    `disallowedregex` is Java regex embedded in the YAML.
    Be careful when changing it as YAML requires extra escaping.
    The default value is intentionally complex.

Custom chat, join, leave, and death text is configured in [lang.yml](../language/).
