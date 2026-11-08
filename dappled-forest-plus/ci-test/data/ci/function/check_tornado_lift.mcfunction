execute store result score #cy2 ci run data get entity @e[tag=ci_cow,limit=1] Pos[1] 10
scoreboard players operation #cy2 ci -= #cy1 ci
execute if score #cy2 ci matches 10.. run say CI_PASS tornado-lifts-mobs
execute unless score #cy2 ci matches 10.. run say CI_INFO cow-not-lifted-yet
schedule function ci:check_tornado_fling 80t
