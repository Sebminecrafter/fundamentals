# Fundamentals

![Logo](docs/assets/logo.png)

> [!WARNING]
> This plugin is currently in beta.

A nice, featureful, everything you need Bukkit plugin.

The perfect plugin to set up your Minecraft server:

- Configurable commands with permissions
- Custom chat, joins/leaves, deaths
- Everything is customizable in language file `lang.yml`

## Download

Head over to the [Modrinth](https://modrinth.com/plugin/fundamentalswastaken) page or [GitHub Releases](https://github.com/Sebminecrafter/fundamentals/releases) (in-dev builds only available on GitHub releases)

## Wiki

Head over to [the Wiki](https://sebminecrafter.github.io/fundamentals)
for more detailed information about the plugin and using it!

### Commands

Fundamentals features *player*, *staff*, and *privileged* command groups.
Almost any command you'd expect is already there.

### Custom Messages

Fundamentals has custom *chat formatting*, *death messages*, and *join/leave messages*.

**By default**, the chat looks like this: `Player: message!`,
the deaths are like this: `☠ Player died`,
and the joins and leaves like this: `[+] Player` / `[-] Player`

Everything supports legacy color codes (like `&c`, for red),
and MiniMessage (legacy translation, not all features are available).

### Server Checks

> [!IMPORTANT]
> Server Checks are run on a very short timer and may impact server performance by running advanced logic 

Fundamentals features *Server Checks* which are run on timers to make sure no one is acting up
or trying to mess with the server.

- `OpChecker` - checks if anyone has been op'd every 5 seconds.
- `UpdateChecker` - checks for updates for Fundamentals

![bStats statistics](https://bstats.org/signatures/bukkit/fundamentalswastaken.svg)
