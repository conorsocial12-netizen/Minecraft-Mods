execute if entity @e[type=dappledforest:poplar_arrow] run say CI_INFO late-poplar-arrows-present
execute as @e[type=iron_golem,tag=ci_golem] if data entity @s active_effects[{id:"dappledforest:leaf_blind"}] run say CI_PASS leaf-blind-applied-late
execute if entity @e[type=dappledforest:dappled_fox] run say CI_INFO late-fox-present
execute if entity @e[type=dappledforest:dappled_skeleton] run say CI_INFO late-skeleton-present
