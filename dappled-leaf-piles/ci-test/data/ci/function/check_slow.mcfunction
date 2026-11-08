execute if entity @e[type=armor_stand,tag=ci,x=4,z=4,dx=3,dz=3,y=198,dy=3] run say CI_PASS sinking-slowly
execute unless entity @e[type=armor_stand,tag=ci,x=4,z=4,dx=3,dz=3,y=198,dy=3] run say CI_FAIL sinking-slowly
execute if entity @e[type=item,x=11,y=230,z=11,dx=2,dy=3,dz=2] run say CI_PASS loot
execute unless entity @e[type=item,x=11,y=230,z=11,dx=2,dy=3,dz=2] run say CI_FAIL loot
