execute as @e[type=dappledforest:acorn_squirrel,tag=ci_sq] unless items entity @s weapon.mainhand minecraft:stick run say CI_INFO squirrel-not-holding-stick
execute as @e[type=dappledforest:acorn_squirrel,tag=ci_sq] if items entity @s weapon.mainhand minecraft:stick run say CI_INFO squirrel-holding-stick
execute if entity @e[type=item,nbt={Item:{id:"minecraft:stick"}},x=20,y=100,z=-23,dx=5,dy=4,dz=5] run say CI_PASS squirrel-fetched-stick-to-owner
execute unless entity @e[type=item,nbt={Item:{id:"minecraft:stick"}},x=20,y=100,z=-23,dx=5,dy=4,dz=5] run say CI_FAIL squirrel-fetched-stick-to-owner
execute as @e[type=item,nbt={Item:{id:"minecraft:stick"}}] at @s run say CI_INFO stick-is-here
execute as @e[type=dappledforest:acorn_squirrel,tag=ci_sq] at @s run say CI_INFO squirrel-is-here
# diagnostics
execute store result score #sq ci run data get entity @e[type=dappledforest:acorn_squirrel,tag=ci_sq,limit=1] Pos[0]
execute if score #sq ci matches 0..19 run say CI_INFO sq-x-0-19
execute if score #sq ci matches 20..22 run say CI_INFO sq-x-20-22
execute if score #sq ci matches 23..25 run say CI_INFO sq-x-23-25
execute if score #sq ci matches 26..28 run say CI_INFO sq-x-26-28
execute if score #sq ci matches 29..31 run say CI_INFO sq-x-29-31
execute if score #sq ci matches 32..34 run say CI_INFO sq-x-32-34
execute if score #sq ci matches 35..60 run say CI_INFO sq-x-35-60
execute store result score #stick ci run data get entity @e[type=item,nbt={Item:{id:"minecraft:stick"}},limit=1] Pos[0]
execute if score #stick ci matches 0..19 run say CI_INFO stick-x-0-19
execute if score #stick ci matches 20..22 run say CI_INFO stick-x-20-22
execute if score #stick ci matches 23..25 run say CI_INFO stick-x-23-25
execute if score #stick ci matches 26..28 run say CI_INFO stick-x-26-28
execute if score #stick ci matches 29..31 run say CI_INFO stick-x-29-31
execute if score #stick ci matches 32..34 run say CI_INFO stick-x-32-34
execute if score #stick ci matches 35..60 run say CI_INFO stick-x-35-60
execute store result score #owner ci run data get entity @e[type=armor_stand,tag=ci_owner,limit=1] Pos[0]
execute if score #owner ci matches 0..19 run say CI_INFO owner-x-0-19
execute if score #owner ci matches 20..22 run say CI_INFO owner-x-20-22
execute if score #owner ci matches 23..25 run say CI_INFO owner-x-23-25
execute if score #owner ci matches 26..28 run say CI_INFO owner-x-26-28
execute if score #owner ci matches 29..31 run say CI_INFO owner-x-29-31
execute if score #owner ci matches 32..34 run say CI_INFO owner-x-32-34
execute if score #owner ci matches 35..60 run say CI_INFO owner-x-35-60
