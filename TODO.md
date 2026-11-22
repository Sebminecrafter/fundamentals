# To-Do

## Explanation

This is not a list of features in the plugin!

These are not final, but **ideas** that are likely to be implemented.

They may or may not be added, and are probably not yet in the plugin.

Each specific category is ordered from
\[easiest to implement\] / \[most likely to be added\]
to
\[hardest to implement\] / \[least likely to be added\]

### Key

|       Meaning       |    Symbol    |
|:-------------------:|:------------:|
| Already implemented | **not here** |
|         WIP         |      ☑      |
|     Not started     |      ☐      |

## Features

### Player features/commands

- [ ] `/helpop` to message admins
- [ ] `/tpauto` to auto-accept tpa requests
- [ ] `/tptoggle` to enable/disable tp requests (toggle)
- [ ] `/msgtoggle` to toggle receiving messages
- [ ] `/back` on death and teleports
- [ ] `/trade` to trade between players

### Privileged Commands

(suggest some!)

### Staff features/commands

- [ ] `/world <world>` to switch worlds
- [ ] `/vanish` to become invisible to other players
- [ ] `/staffchat` and `/alert` for staff messaging
- [ ] `/god [player]` toggles "god mode"
- [ ] `/pausechat` to pause the chat
- [ ] `/mute <player> [time]` mute a player
- [ ] `/tempban <player> <time> [reason]` tempban player

### General features

- [ ] `/info` for server info, configurable
- [ ] Nicknames (`/nick`, `/realname`, etc.)
- [ ] Commands on first join
- [ ] Simple custom commands system
- [ ] Proper offline players for invsee/ecsee (hard to implement because of nbt data storage)
- [ ] Teams (`/faction`(s) or `/group`(s))
- [ ] Mail commands + system
- [ ] `/afk` + AFK detection -
  marks players as AFK, configurable time, option to announce AFK, etc.
- [ ] Kits? - Feedback wanted

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

### Future Plans / Roadmap

> [!note]
> These are future plans, not the current state of the project.

I plan to hopefully migrate this whole project to the Paper API in the future.
I have started to notice that, first of all, I and a lot of other peoples
servers use Paper, and transitioning to Paper API is a reasonable option.
It would definitely help to reach the Folia and modern paper ecosystem.
Please note that if you are using a Bukkit/Spigot server you will always be able to use
Fundamentals, but at some point you will be stuck on some sort of "Legacy" branch.
I would appreciate any help or thoughts on this!
