# Sour Candy Apple (Fabric, Minecraft 26.3)

Adds a **Sour Candy Apple**: an extremely rare drop from poplar leaves (red, orange or yellow) broken or decayed inside the **Dappled Forest**.

| | |
|---|---|
| Drop chance | 0.1% per leaf block (about 1 in 1,000), Dappled Forest only |
| Food | 2 hunger points (1 drumstick), always edible |
| Effect | Swiftness I for 60 seconds |
| Creative tab | Food & Drinks |

## Getting the jar

GitHub Actions builds it automatically: **Actions** tab → latest green run → **Artifacts** → `mod-jars` → unzip → drop `sour-candy-apple-1.0.0.jar` into your mods folder (needs Fabric API for 26.3).

To build locally instead: JDK 25 + Gradle, then `gradle build` in this folder.

## Tweaking

- **Drop chance:** edit `"chance"` in `src/main/resources/data/sourcandyapple/loot_table/dappled_poplar_leaves_bonus.json` (0.001 = 0.1%).
- **Hunger / effect:** `src/main/java/com/sourcandyapple/ModItems.java`.
- **Versions:** if Gradle can't find a dependency, grab the latest numbers from https://fabricmc.net/develop and update `gradle.properties` (and the Loom version in `build.gradle`).
