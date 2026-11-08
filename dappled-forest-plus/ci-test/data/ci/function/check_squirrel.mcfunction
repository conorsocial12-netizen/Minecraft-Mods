execute as @e[type=dappledforest:acorn_squirrel,tag=ci_sq] unless items entity @s weapon.mainhand minecraft:stick run say CI_INFO squirrel-not-holding-stick
execute as @e[type=dappledforest:acorn_squirrel,tag=ci_sq] if items entity @s weapon.mainhand minecraft:stick run say CI_INFO squirrel-holding-stick
execute if entity @e[type=item,nbt={Item:{id:"minecraft:stick"}},x=20,y=100,z=-23,dx=5,dy=4,dz=5] run say CI_PASS squirrel-fetched-stick-to-owner
execute unless entity @e[type=item,nbt={Item:{id:"minecraft:stick"}},x=20,y=100,z=-23,dx=5,dy=4,dz=5] run say CI_FAIL squirrel-fetched-stick-to-owner
execute as @e[type=item,nbt={Item:{id:"minecraft:stick"}}] at @s run say CI_INFO stick-is-here
execute as @e[type=dappledforest:acorn_squirrel,tag=ci_sq] at @s run say CI_INFO squirrel-is-here
