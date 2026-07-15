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

Head over to the [Modrinth](https://modrinth.com/plugin/fundamentalswastaken) page or [Github Releases](https://github.com/Sebminecrafter/fundamentals/releases)

## Features

### Player Commands

These are commands for players on your server to use.

- Message a player with `/msg`
- Teleport requests with `/tpa`, `/tpahere`
- Ignore a player's messages and requests `/ignore`

### Staff Commands

These are commands specifically made for moderation or server management.

- Freeze player with `/freeze`
- See a player's inventory with `/invsee`
- Send a player a private message from "Staff" with `/staffmsg`
- Enter *staff mode* with `/staffmode`
- Refill your hunger with `/feed`
- Custom `/gamemode`, with shorthands like `/gm`, `/gms`, `/gmc`, etc.
- Teleport an offline player with `/tpo`
- Fly in survival with `/fly`

### Server Checks

> [!IMPORTANT]
> Server Checks are run on a very short timer and may impact server performance by running advanced logic 

Fundamentals features *Server Checks* which are run on timers to make sure no one is acting up
or trying to mess with the server.

- `OpChecker` - checks if anyone has been op'd every 5 seconds.
