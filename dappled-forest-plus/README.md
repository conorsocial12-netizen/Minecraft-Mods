# Dappled Forest Plus

Every Dappled Forest mod in one jar (Fabric, Minecraft 26.3, needs Fabric API).

| Feature | What it does | Test command |
|---|---|---|
| Poplar Arrow | Arrow + any poplar leaves. Normal damage, and leaves pile up on the target's screen (stacks to 3 hits). | `/give @s dappledforest:poplar_arrow 16` |
| Dappled Skeleton | Leaf-covered skeleton that shoots Poplar Arrows. Dappled Forest only, at night, on the surface. | `/give @s dappledforest:dappled_skeleton_spawn_egg` |
| Dappled Fox | Big rideable fox. Tame with sweet berries, saddle it, bite while riding. Orange / red / yellow. | `/give @s dappledforest:dappled_fox_spawn_egg` |
| Leaf Piles | Red / orange / yellow piles that generate in the forest, float, and slowly swallow you over a gap. | `/give @s dappledforest:red_leaf_pile 16` |
| Acorn Squirrel | Tiny red squirrel that climbs trees and drops acorns. Tame with acorns (1 in 3). Fetches dropped items to your feet. Right-click: sit. Crouch + right-click: rides on your shoulder (hold crouch 1s to put it down). | `/give @s dappledforest:acorn_squirrel_spawn_egg` |
| Acorn | Snack (1 hunger). Tames, heals and breeds squirrels. | `/give @s dappledforest:acorn 16` |
| Leaf Tornado | On windy days (about 1 in 3 in-game days, 2–4 minutes) swirling leaf tornadoes wander the Dappled Forest. They lift and spin players, mobs and items, fling them out with Slow Falling, and leave Leaf Piles when they die out. | `/leafwind start` (op) or `/summon dappledforest:leaf_tornado` |
| Sour Candy Apple | 0.1% drop from poplar leaves in the forest. 2 hunger + Swiftness I for 1 minute. | `/give @s dappledforest:sour_candy_apple` |

Install this **instead of** the separate mods (poplar-arrow, dappled-fox, dappled-leaf-piles, sour-candy-apple), not alongside them.

`ci-test/` is a data pack used by the GitHub server test only. It isn't part of the mod.
