execute store result score #sqearly ci run data get entity @e[type=dappledforest:acorn_squirrel,tag=ci_sq,limit=1] Pos[0]
execute if score #sqearly ci matches 0..19 run say CI_INFO sqearly-x-0-19
execute if score #sqearly ci matches 20..22 run say CI_INFO sqearly-x-20-22
execute if score #sqearly ci matches 23..25 run say CI_INFO sqearly-x-23-25
execute if score #sqearly ci matches 26..28 run say CI_INFO sqearly-x-26-28
execute if score #sqearly ci matches 29..31 run say CI_INFO sqearly-x-29-31
execute if score #sqearly ci matches 32..34 run say CI_INFO sqearly-x-32-34
execute if score #sqearly ci matches 35..60 run say CI_INFO sqearly-x-35-60
