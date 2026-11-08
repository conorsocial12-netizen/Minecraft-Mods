forceload add -16 -16 47 47
scoreboard objectives add ci dummy
# 1) A pile placed in mid-air must stay put
setblock 0 200 0 dappledleafpiles:red_leaf_pile
# 2) Trap: pile over a 10 block drop, armor stand dropped on it
setblock 5 190 5 minecraft:stone
setblock 5 200 5 dappledleafpiles:orange_leaf_pile
summon minecraft:armor_stand 5.5 200.4 5.5 {Tags:["ci"]}
setblock 9 190 9 minecraft:stone
setblock 9 200 9 dappledleafpiles:red_leaf_pile
summon minecraft:item 9.5 200.4 9.5 {Tags:["ci2"],PickupDelay:32767,Age:-32768,Item:{id:"minecraft:stick",count:1}}
function ci:diag
# 3) Worldgen feature places piles on grass
fill 15 150 15 35 150 35 minecraft:grass_block
place feature dappledleafpiles:leaf_piles 25 151 25
execute store result score #gen ci run fill 15 151 15 35 152 35 minecraft:air replace #dappledleafpiles:leaf_piles
execute if score #gen ci matches 1.. run say CI_PASS worldgen-feature
execute unless score #gen ci matches 1.. run say CI_FAIL worldgen-feature
# 4) Breaking a pile drops the pile item
setblock 0 205 0 dappledleafpiles:yellow_leaf_pile
loot spawn 0 230 0 mine 0 205 0
function ci:diag
execute if entity @e[type=item,name="Yellow Leaf Pile"] run say CI_PASS loot
execute unless entity @e[type=item,name="Yellow Leaf Pile"] run say CI_FAIL loot
schedule function ci:check_slow 30t
schedule function ci:check_end 300t
