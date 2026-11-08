forceload add -16 -16 47 47
scoreboard objectives add ci dummy
# 1) A pile placed in mid-air must stay put
setblock 0 200 0 dappledforest:red_leaf_pile
# 2) Trap: pile over a 10 block drop, armor stand dropped on it
setblock 5 190 5 minecraft:stone
setblock 5 200 5 dappledforest:orange_leaf_pile
summon minecraft:armor_stand 5.5 200.4 5.5 {Tags:["ci"]}
setblock 9 190 9 minecraft:stone
setblock 9 200 9 dappledforest:red_leaf_pile
summon minecraft:item 9.5 200.4 9.5 {Tags:["ci2"],PickupDelay:32767,Age:-32768,Item:{id:"minecraft:stick",count:1}}
# 3) Worldgen feature places piles on grass
fill 15 150 15 35 150 35 minecraft:grass_block
place feature dappledforest:leaf_piles 25 151 25
execute store result score #gen ci run fill 15 151 15 35 152 35 minecraft:air replace #dappledforest:leaf_piles
execute if score #gen ci matches 1.. run say CI_PASS worldgen-feature
execute unless score #gen ci matches 1.. run say CI_FAIL worldgen-feature
# 4) Breaking a pile drops the pile item
setblock 0 205 0 dappledforest:yellow_leaf_pile
fill 6 230 6 18 230 18 minecraft:glass
loot spawn 12.5 231 12.5 mine 0 205 0
schedule function ci:check_slow 30t
schedule function ci:check_end 300t
# 5) Every item exists under the new dappledforest: names
summon minecraft:item 40.5 240 40.5 {Tags:["ci_items"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:poplar_arrow",count:1}}
summon minecraft:item 40.5 240 40.5 {Tags:["ci_items"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:sour_candy_apple",count:1}}
summon minecraft:item 40.5 240 40.5 {Tags:["ci_items"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:dappled_skeleton_spawn_egg",count:1}}
summon minecraft:item 40.5 240 40.5 {Tags:["ci_items"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:dappled_fox_spawn_egg",count:1}}
summon minecraft:item 40.5 240 40.5 {Tags:["ci_items"],NoGravity:1b,PickupDelay:32767,Item:{id:"dappledforest:red_leaf_pile",count:1}}
execute store result score #items ci if entity @e[type=item,tag=ci_items]
execute if score #items ci matches 5 run say CI_PASS all-items-registered
execute unless score #items ci matches 5 run say CI_FAIL all-items-registered
# 6) Dappled Fox spawns
fill 30 120 0 40 120 10 minecraft:grass_block
summon dappledforest:dappled_fox 35 121 5 {Tags:["ci_fox"]}
execute if entity @e[type=dappledforest:dappled_fox,tag=ci_fox] run say CI_PASS fox-spawns
execute unless entity @e[type=dappledforest:dappled_fox,tag=ci_fox] run say CI_FAIL fox-spawns
# 7) Dappled Skeleton spawns with a bow, shoots Poplar Arrows, and they apply Leaf Blind
time set midnight
fill -40 120 -10 -20 120 10 minecraft:stone
fill -40 121 -10 -20 125 10 minecraft:air
summon dappledforest:dappled_skeleton -36 121 0
summon minecraft:iron_golem -28 121 0 {NoAI:1b,Tags:["ci_golem"]}
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
schedule function ci:check_combat 60t
