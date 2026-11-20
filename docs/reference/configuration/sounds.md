# Sounds

The `sounds` section maps plugin events to Minecraft sound IDs.

```yaml
sounds:
  countdown: "block.note_block.hat"
  canceled: "entity.villager.no"
  teleport: "entity.player.teleport"
```

Use Minecraft sound IDs such as `block.note_block.hat` or `entity.player.teleport`.
No file or resource pack is required.

## Available groups

- `countdown`, `canceled`, and `teleport` are used for countdown and teleport events.
- `tpa-receive` and `tpa-accept` are used for TPA requests.
- `msg`, `broadcast`, and `broadcastworld` are used by messaging and broadcasting.
- `ec` is used for the enderchest action.
- `hat` is used when placing or removing a head.

For common Minecraft sound IDs, the plugin uses
[minecraft-sounds.vercel.app](https://minecraft-sounds.vercel.app/) as a lookup reference.

The teleport countdown keys also apply to [TPA and home settings](teleports/).
