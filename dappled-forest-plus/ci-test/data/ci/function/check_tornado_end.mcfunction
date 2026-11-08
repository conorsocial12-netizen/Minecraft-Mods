execute unless entity @e[type=dappledforest:leaf_tornado,tag=ci_tornado2] run say CI_PASS tornado-blows-out
execute if entity @e[type=dappledforest:leaf_tornado,tag=ci_tornado2] run say CI_FAIL tornado-blows-out
execute positioned 14 0 -4 positioned over motion_blocking_no_leaves store result score #piles ci run fill ~-7 ~-3 ~-7 ~7 ~3 ~7 minecraft:air replace #dappledforest:leaf_piles
execute if score #piles ci matches 1.. run say CI_PASS tornado-leaves-leaf-piles
execute unless score #piles ci matches 1.. run say CI_FAIL tornado-leaves-leaf-piles
