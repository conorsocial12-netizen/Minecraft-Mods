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
schedule function ci:setup2 60t
