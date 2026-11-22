# To-Do

## Explanation

This is not a list of features in the plugin!

These are not final, but **ideas** that are likely to be implemented.

They may or may not be added, and are probably not yet in the plugin.

### Key

|       Meaning       |    Symbol    |
|:-------------------:|:------------:|
| Already implemented | **not here** |
|         WIP         |      ☑      |
|     Not started     |      ☐      |

## Features

### Player features/commands

- [ ] `/back` on death and teleports
- [ ] `/tptoggle` to enable/disable tp requests (toggle)
- [ ] `/helpop` to message admins
- [ ] `/msgtoggle` to toggle receiving messages
- [ ] `/tpauto` to auto-accept tpa requests

### Privileged Commands

(suggest some!)

### Staff features/commands

- [ ] `/remove` remove entities
- [ ] `/world <world>` to switch worlds
- [ ] `/near` to see nearby players within configured range
- [ ] `/vanish` to become invisible to other players
- [ ] `/staffchat` and `/alert` for staff messaging
- [ ] `/god [player]` toggles "god mode"
- [ ] `/pausechat` to pause the chat
- [ ] `/mute <player> [time]` mute a player
- [ ] `/tempban <player> <time> [reason]` tempban player

### General features

- [ ] `/afk` + AFK detection - 
    marks players as AFK, configurable time, option to announce AFK, etc.
- [ ] Proper offline players (hard to implement because of nbt data storage)
- [ ] Kits? - Feedback wanted
- [ ] Commands on first join
- [ ] Teams (`/faction`(s) or `/group`(s))
- [ ] Mail commands + system
- [ ] Simple custom commands system
- [ ] `/info` for server info, configurable
- [ ] Nicknames (`/nick`, `/realname`, etc.)

## Known Bugs (latest dev build)

Please submit bugs via the
[Issues tab](https://github.com/Sebminecrafter/Fundamentals/issues)
on GitHub

- Interacting with workstations accessed via commands is slightly buggy,
  items may disappear when shift-clicked or gui is closed
- `/serverstat`'s TPS doesn't work on Spigot

### Notices

- Update Checker WIP

- Full Essentials compatibility is not guaranteed as to not replace default Minecraft commands