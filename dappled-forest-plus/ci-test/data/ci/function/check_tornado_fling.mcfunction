execute as @e[tag=ci_cow] if data entity @s active_effects[{id:"minecraft:slow_falling"}] run say CI_PASS tornado-flings-with-slow-falling
execute as @e[tag=ci_cow] unless data entity @s active_effects[{id:"minecraft:slow_falling"}] run say CI_INFO cow-has-no-slow-falling
execute unless score #cy2 ci matches 10.. run say CI_FAIL tornado-lifts-mobs
