# TODO

## Explanation

This is not a list of features in the plugin!

These are not final, but **ideas** that are likely to be implemented.
They may or may not be added, and are probably not yet in the plugin.
Most of these are copied from Essentials, as to create a more seamless transition,
  but some have been removed as they replaced completely fine vanilla commands or were
  no longer relevant in current versions of Minecraft.

### Key

|       Meaning       |    Symbol    |
|:-------------------:|:------------:|
| Already implemented | **not here** |
|         WIP         |      ☑      |
|     Not started     |      ☐      |

### Player features/commands
- [ ] `/back` on death and teleports
- [ ] `/playtime [player]` see playtime of self or other
- [ ] `/tptoggle` to enable/disable tp requests (toggle)
- [ ] `/helpop` to message admins
- [ ] `/msgtoggle` to toggle receiving messages
- [ ] `/tpauto` to auto-accept tpa requests
- [ ] `/depth` to show current depth relative to the sea

### Privileged Commands
- [ ] `/hat [item/hand] [player]` put held or other item on a player's head

### Staff features/commands
- [ ] `/bottom` and `/top`
- [ ] `/whois` to get player info
- [ ] `/lore` to change item lore
- [ ] `/remove` remove entities
- [ ] `/world <world>` to switch worlds
- [ ] `/near` to see nearby players within configured range
- [ ] `/vanish` to become invisible to other players
- [ ] `/staffchat` and `/alert` for staff messaging
- [ ] `/god [player]` toggles "god mode"
- [ ] `/serverstat` to check uptime, performance, etc.
- [ ] `/mute <player> [time]` mute a player
- [ ] `/tempban <player> <time> [reason]` tempban player

### General features

- [ ] AFK \
  `/afk` + AFK detection \
  Marks players as AFK, configurable time, option to announce AFK, etc.

- [ ] Proper offline players (hard to implement because of nbt data storage)

- [ ] Kits? Or separate plugin? lmk your ideas

- [ ] Commands on first join

- [ ] Mail commands + system

- [ ] Join MOTD / `/motd`

- [ ] Custom MOTD with legacy colors + MiniMessage

- [ ] Simple custom commands system

- [ ] `/info` for server info, configurable

- [ ] Nicknames (`/nick`, `/realname`, etc.)

## Bugs

- Interacting with workstations accessed via commands is slightly buggy,
  items may disappear when shift-clicked or gui is closed

Please submit bugs via the
[Issues tab](https://github.com/Sebminecrafter/Fundamentals/issues)
on GitHub

(no other current ***known*** bugs in latest build)

## Notices

- Warps and reloading have not been fully tested.
