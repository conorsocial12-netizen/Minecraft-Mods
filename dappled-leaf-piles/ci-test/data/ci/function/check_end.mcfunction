function ci:diag
execute if entity @e[type=armor_stand,tag=ci,x=4,z=4,dx=3,dz=3,y=190,dy=2] run say CI_PASS fell-through
execute unless entity @e[type=armor_stand,tag=ci,x=4,z=4,dx=3,dz=3,y=190,dy=2] run say CI_FAIL fell-through
execute if block 0 200 0 dappledleafpiles:red_leaf_pile run say CI_PASS floats-without-support
execute unless block 0 200 0 dappledleafpiles:red_leaf_pile run say CI_FAIL floats-without-support
say CI_DONE
