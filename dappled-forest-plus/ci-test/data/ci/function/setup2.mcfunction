# 5) Every item exists under the new dappledforest: names
summon minecraft:item 8.5 240 8.5 {Tags:["ci_items"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:poplar_arrow",count:1}}
summon minecraft:item 8.5 240 8.5 {Tags:["ci_items"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:sour_candy_apple",count:1}}
summon minecraft:item 8.5 240 8.5 {Tags:["ci_items"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:dappled_skeleton_spawn_egg",count:1}}
summon minecraft:item 8.5 240 8.5 {Tags:["ci_items"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:dappled_fox_spawn_egg",count:1}}
summon minecraft:item 8.5 240 8.5 {Tags:["ci_items"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:red_leaf_pile",count:1}}
execute unless entity @e[type=item,tag=ci_items,nbt={Item:{id:"dappledforest:poplar_arrow"}}] run say CI_INFO missing-poplar_arrow
execute unless entity @e[type=item,tag=ci_items,nbt={Item:{id:"dappledforest:sour_candy_apple"}}] run say CI_INFO missing-sour_candy_apple
execute unless entity @e[type=item,tag=ci_items,nbt={Item:{id:"dappledforest:dappled_skeleton_spawn_egg"}}] run say CI_INFO missing-dappled_skeleton_spawn_egg
execute unless entity @e[type=item,tag=ci_items,nbt={Item:{id:"dappledforest:dappled_fox_spawn_egg"}}] run say CI_INFO missing-dappled_fox_spawn_egg
execute unless entity @e[type=item,tag=ci_items,nbt={Item:{id:"dappledforest:red_leaf_pile"}}] run say CI_INFO missing-red_leaf_pile
execute store result score #items ci if entity @e[type=item,tag=ci_items]
execute if score #items ci matches 5 run say CI_PASS all-items-registered
execute unless score #items ci matches 5 run say CI_FAIL all-items-registered
# 6) Dappled Fox spawns
fill 15 120 15 25 120 25 minecraft:grass_block
summon dappledforest:dappled_fox 20 121 20 {Tags:["ci_fox"]}
execute if entity @e[type=dappledforest:dappled_fox,tag=ci_fox] run say CI_PASS fox-spawns
execute unless entity @e[type=dappledforest:dappled_fox,tag=ci_fox] run say CI_FAIL fox-spawns
# 7) Dappled Skeleton spawns with a bow, shoots Poplar Arrows, and they apply Leaf Blind
time set midnight
execute positioned -8 0 -8 positioned over motion_blocking run summon dappledforest:dappled_skeleton ~ ~1 ~
execute positioned -2 0 -8 positioned over motion_blocking run summon minecraft:iron_golem ~ ~1 ~ {NoAI:1b,Tags:["ci_golem"]}
execute if items entity @e[type=dappledforest:dappled_skeleton,limit=1] weapon.mainhand minecraft:bow run say CI_PASS skeleton-has-bow
execute unless items entity @e[type=dappledforest:dappled_skeleton,limit=1] weapon.mainhand minecraft:bow run say CI_FAIL skeleton-has-bow
# 8) Dappled Skeleton loot table drops Poplar Arrows or bones (6 rolls so it can't come up empty)
fill -6 230 -6 -18 230 -18 minecraft:glass
execute positioned -12 231 -12 run loot spawn ~ ~ ~ loot dappledforest:entities/dappled_skeleton
execute positioned -12 231 -12 run loot spawn ~ ~ ~ loot dappledforest:entities/dappled_skeleton
execute positioned -12 231 -12 run loot spawn ~ ~ ~ loot dappledforest:entities/dappled_skeleton
execute positioned -12 231 -12 run loot spawn ~ ~ ~ loot dappledforest:entities/dappled_skeleton
execute positioned -12 231 -12 run loot spawn ~ ~ ~ loot dappledforest:entities/dappled_skeleton
execute positioned -12 231 -12 run loot spawn ~ ~ ~ loot dappledforest:entities/dappled_skeleton
schedule function ci:check_combat 100t
schedule function ci:check_late 200t
# 9) Acorn Squirrel: spawns, acorn exists, loot works, and a tamed squirrel fetches a stick to its owner
summon minecraft:item 8.5 240 8.5 {Tags:["ci_acorn"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:acorn",count:1}}
execute if entity @e[type=item,tag=ci_acorn] run say CI_PASS acorn-item
execute unless entity @e[type=item,tag=ci_acorn] run say CI_FAIL acorn-item
fill 20 100 -30 40 100 -10 minecraft:stone
fill 20 101 -30 40 104 -10 minecraft:air
summon minecraft:armor_stand 22.5 101 -20.5 {UUID:[I;11,22,33,44],Tags:["ci_owner"],NoGravity:1b}
summon dappledforest:acorn_squirrel 24.5 101 -20.5 {Owner:[I;11,22,33,44],Tags:["ci_sq"],PersistenceRequired:1b}
summon minecraft:item 32.5 101 -20.5 {Tags:["ci_fetch"],Item:{id:"minecraft:stick",count:1}}
execute if entity @e[type=dappledforest:acorn_squirrel,tag=ci_sq] run say CI_PASS squirrel-spawns
execute unless entity @e[type=dappledforest:acorn_squirrel,tag=ci_sq] run say CI_FAIL squirrel-spawns
execute as @e[type=dappledforest:acorn_squirrel,tag=ci_sq] if data entity @s Owner run say CI_INFO squirrel-is-tamed
fill -6 220 -6 -18 220 -18 minecraft:glass
execute positioned -12 221 -12 run loot spawn ~ ~ ~ loot dappledforest:entities/acorn_squirrel
schedule function ci:check_squirrel 240t
