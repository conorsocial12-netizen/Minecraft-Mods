# Skin Glow (Fabric, Minecraft 26.3, client-side)

A subtle white outline around player skins (and optionally mobs) so they stand out.
Runs on any server. Needs Fabric API; Mod Menu is optional.

## Adjusting it in game
- `/skinglow` opens the settings menu (also reachable from Mod Menu -> Skin Glow -> Configure).
- `/skinglow toggle` turns it on/off, `/skinglow reset` restores defaults.
- Menu: on/off, other players, yourself (F5), hostile/passive mobs, color mode (solid/rainbow/gradient),
  intensity (lower = more subtle), red/green/blue, pulse and pulse speed, max distance.

Everything is also in `config/skinglow.json`, which live-reloads while the game runs
(extra options there: gradient color2, rainbow speed, per-type colors, team colors, swapRedBlue).
