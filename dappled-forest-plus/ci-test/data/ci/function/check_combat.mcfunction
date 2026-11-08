execute if entity @e[type=dappledforest:poplar_arrow] run say CI_PASS skeleton-shoots-poplar-arrows
execute unless entity @e[type=dappledforest:poplar_arrow] run say CI_FAIL skeleton-shoots-poplar-arrows
execute if entity @e[type=minecraft:arrow] run say CI_FAIL skeleton-shot-plain-arrows
execute as @e[type=iron_golem,tag=ci_golem] if data entity @s active_effects[{id:"dappledforest:leaf_blind"}] run say CI_PASS leaf-blind-applied
execute as @e[type=iron_golem,tag=ci_golem] unless data entity @s active_effects[{id:"dappledforest:leaf_blind"}] run say CI_INFO golem-not-hit-yet
execute if entity @e[type=item,x=-18,y=230,z=-18,dx=12,dy=4,dz=12] run say CI_PASS skeleton-loot
execute unless entity @e[type=item,x=-18,y=230,z=-18,dx=12,dy=4,dz=12] run say CI_FAIL skeleton-loot
