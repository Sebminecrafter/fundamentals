# Features

## Overview of features

<!-- --8<-- [start:commands] -->
### Commands

Fundamentals features *player*, *staff*, and *privileged* command groups.
Almost any command you'd expect is already there.
<!-- --8<-- [end:commands] -->

<!-- --8<-- [start:lang] -->
### Custom Messages

Fundamentals has custom *chat formatting*, *death messages*, and *join/leave messages*.

**By default**, Fundamentals changes some chat messages, namely deaths, joins, leaves, and chatting.

Chat: `Player: message!`  
Deaths: `☠ Player died`  
Joins/Leaves: `[+] Player` / `[-] Player`

Everything supports legacy color codes (like `&c`, for red),
and MiniMessage (legacy translation, not all features are available).
<!-- --8<-- [end:lang] -->

<!-- --8<-- [start:checks] -->
### Server Checks

> [!important]
> Server Checks are run on a very short timer and may impact server performance by running advanced logic

Fundamentals features *Server Checks* which are run on timers to make sure no one is acting up
or trying to mess with the server.

- [OpChecker](/features/opchecker) - checks if anyone has been op'd every 5 seconds.
- [Update Checker](/features/updatechecker) - checks for updates for Fundamentals
<!-- --8<-- [end:checks] -->

Fundamentals also includes optional bStats metrics
--8<-- "snippets/bstats.md"