# Dappled Leaf Piles (Fabric, Minecraft 26.3)

Chunky 3D leaf piles in the three Dappled Forest colours: **Red**, **Orange** and **Yellow Leaf Pile**.

- **Bigger than leaf litter:** a heaped, layered 3D model about 5 pixels tall, randomly rotated when placed.
- **Floats:** piles need no support. Break the block underneath and the pile stays where it is.
- **Trap:** piles have no collision. On solid ground you walk through them normally. Over a gap, anything that steps on one sinks slowly through it, then falls. Cover a pit with them.
- **Worldgen:** small mixed-colour patches generate across the Dappled Forest.
- Breaks instantly (fastest with a hoe) and drops itself. Found in the Natural Blocks creative tab.

## Getting the jar

Actions tab → latest green **Build mods** run → Artifacts → `mod-jars` → unzip → put `dappled-leaf-piles-1.0.0.jar` in your mods folder (needs Fabric API for 26.3).

## Tweaking

- **How many patches spawn:** `"count"` in `src/main/resources/data/dappledleafpiles/worldgen/placed_feature/leaf_piles.json` (patches per chunk).
- **Piles per patch / spread:** `TRIES` and `SPREAD` in `LeafPileFeature.java`.
- **How slowly you sink:** `SINKING` in `LeafPileBlock.java` (lower Y = slower).

`ci-test/` is a test data pack used by GitHub Actions only. It isn't part of the mod.
