execute if entity @e[type=armor_stand,tag=ci,y=198,dy=3] run say CI_PASS sinking-slowly
execute unless entity @e[type=armor_stand,tag=ci,y=198,dy=3] run say CI_FAIL sinking-slowly
