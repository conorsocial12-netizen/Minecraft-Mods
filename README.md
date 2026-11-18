# Minecraft Mods (Fabric 26.3)

Each folder is one mod. GitHub Actions builds them all automatically on every change.

**Getting the jars:** Actions tab → latest run with a green tick → Artifacts → `mod-jars` → unzip → drop the `.jar` into your Modrinth profile (needs Fabric API).

## Mods
- **poplar-arrow** – Arrow + poplar leaves = Poplar Arrow. Normal arrow damage, plus autumn leaves pile up around the target's screen (stacks up to 3 hits).
  - **Dappled Skeleton** – a skeleton covered in poplar leaves that shoots Poplar Arrows. Spawns only in dappled forests, at night, on the surface. Drops bones, Poplar Arrows and sometimes poplar leaves.
- **sour-candy-apple** – Extremely rare (0.1%) drop from poplar leaves in the Dappled Forest. Eat it for 2 hunger points and Swiftness I for 1 minute.
- **dappled-leaf-piles** – Red, orange and yellow 3D leaf piles that generate in the Dappled Forest. They float without support, and you slowly sink through them over a gap, so they make great traps.
- **dappled-fox** – A big rideable fox for the Dappled Forest. Tame with sweet berries, saddle it, bite mobs while riding. Orange, red or yellow coats.
- **skin-glow** – Subtle white outline around player skins (and optionally mobs) so they stand out. `/skinglow` opens an interactive settings menu (also in Mod Menu): intensity, color, who gets it, pulse, distance. Client-side, works on any server; needs Fabric API.
- **dappled-forest-plus** – ⭐ All of the above in one mod (**Dappled Forest Plus**). Everything uses `dappledforest:` names, e.g. `/give @s dappledforest:poplar_arrow`. Install this *instead of* the separate mods, not alongside them, or everything spawns twice.

## Minecraft But Better
A separate selection of mods that make vanilla Minecraft feel better, in the `minecraft-but-better/` folder. They build the same way and land in the same `mod-jars` download.
- **3d-skin-layers** (`minecraft-but-better/3d-skin-layers`) – Skin overlays (hat/hair, jacket, sleeves, pants) become real 3D pixels instead of flat panels. `/skinlayers` opens the settings (also in Mod Menu). Client-side, works on any server; needs Fabric API.
