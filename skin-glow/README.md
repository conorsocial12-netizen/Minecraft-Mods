# Skin Glow (Fabric, Minecraft 26.3, client-side)

Adds an adjustable colored glow outline to players (others + yourself in F5), and optionally mobs.
Works by turning on the game's own entity outline and supplying your color, so it runs on any server (client-only).

## Build
Built automatically by GitHub Actions (see repo README).

## Settings (config/skinglow.json, created on first launch, live-reloads while the game runs)
- enabled, players, self, hostileMobs, passiveMobs, otherEntities, includeInvisible, maxDistance
- colorMode: SOLID | RAINBOW | GRADIENT, color, color2, rainbowSpeed, rainbowSaturation, rainbowOffsetPerEntity, gradientSpeed
- brightness (0..1), pulse, pulseSpeed, pulseMinBrightness
- playerColor / selfColor / hostileColor / passiveColor (hex, blank = use main color)
- respectTeamColors, swapRedBlue (flip if red/blue look swapped)
