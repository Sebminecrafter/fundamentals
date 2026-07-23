# Fundamentals

![Logo](logo.png)

> [!WARNING]
> This plugin is currently in beta.

A nice, featureful, everything you need Bukkit plugin.

The perfect plugin to set up your Minecraft server:

- Everything you need
- All compatible
- Together
- In one plugin
- Fully customizable messages in YAML language file `lang.yml`

## Download

Head over to the [Modrinth](https://modrinth.com/plugin/fundamentalswastaken) page or [GitHub Releases](https://github.com/Sebminecrafter/fundamentals/releases)

## Features

### Player Commands

These are commands for players on your server to use.

- Message a player with `/msg`
- Teleport requests with `/tpa`, `/tpahere`
- Homes with `/sethome`, `/delhome`, `/listhomes`.
- Ignore a player's messages and requests `/ignore`
- Welcome someone with `/welcome`

### Staff Commands

These are commands specifically made for moderation or server management.

- Freeze player with `/freeze`
- See a player's inventory with `/invsee`
- Send a player a private message from "Staff" with `/staffmsg`
- Enter *staff mode* with `/staffmode`
- Refill your hunger with `/feed`
- Custom `/gamemode`, with shorthands like `/gm`, `/gms`, `/gmc`, `/gma`, and `/gmsp`.
- Teleport an offline player with `/tpo`
- Fly in survival with `/fly`

### Custom Messages

Fundamentals has custom *chat formatting*, *death messages*, and *join/leave messages*.

**By default**, the chat looks like this: `Player: message!`,
the deaths are like this: `☠ Player died`,
and the joins and leaves like this: `[+] Player` / `[-] Player`

Everything supports legacy color codes (like `&c`, for red).
MiniMessage support is currently unavailable.

### Server Checks

> [!IMPORTANT]
> Server Checks are run on a very short timer and may impact server performance by running advanced logic 

Fundamentals features *Server Checks* which are run on timers to make sure no one is acting up
or trying to mess with the server.

- `OpChecker` - checks if anyone has been op'd every 5 seconds.
