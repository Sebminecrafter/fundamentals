# Enabled Modules and Commands

The `enabled` section controls which major modules and commands Fundamentals loads.
Set a value to `true` to enable it or `false` to disable it.

```yaml
enabled:
  server-motd: true
  checks:
    opchecker: true
    updatechecker: true
    autoupdate: false
  cmds:
    tpa: true
    rtp: true
  chat:
    globalchat: true
```

## Server checks

| Key                            | Default | Purpose                                |
|--------------------------------|---------|----------------------------------------|
| `enabled.checks.opchecker`     | `true`  | Checks for player operator changes     |
| `enabled.checks.updatechecker` | `true`  | Checks for updates at plugin startup   |
| `enabled.checks.autoupdate`    | `false` | Downloads updates for the next restart |

See [OpChecker](../../features/opchecker/) and [Update Checker](../../features/updatechecker/).

## Commands

The `enabled.cmds` section contains separate keys for every player, staff, and privileged command.
For example, `enabled.cmds.bignuke` controls `/bignuke`, while `enabled.cmds.tpa` controls the TPA
command group.

All command names and permissions are listed in [Commands](../commands/) and [Permissions](../permissions/).

!!! note
    Some commands depend on another command. For example, `reply` requires `msg` to be enabled.

## Chat modules

The `enabled.chat` section contains the main chat feature switches:
`globalchat`, `customjl`, `customdeaths`, and `motd`.
Chat formatting and filtering settings are documented on the [Chat configuration page](../chat/).
