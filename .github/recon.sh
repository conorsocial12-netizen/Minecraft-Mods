J=$(find ~/.gradle -name 'sponge-mixin*.jar' | grep -v sources | head -1); echo "MIXIN JAR: $J"
javap -p -cp "$J" 'org.spongepowered.asm.mixin.MixinEnvironment$CompatibilityLevel' | grep JAVA
for j in $(find ~/.gradle -name '*.jar' | grep -iE 'minecraft-(merged|clientonly)-deobf' | grep -v sources); do unzip -Z1 "$j" | grep -E "textures/entity/skeleton/|items/.*spawn_egg|models/item/skeleton_spawn_egg" | head -12; unzip -p "$j" assets/minecraft/items/skeleton_spawn_egg.json; unzip -p "$j" assets/minecraft/models/item/skeleton_spawn_egg.json; done
M=$(find ~/.gradle -name 'minecraft-clientonly-deobf-26.3.jar' | head -1)
javap -c -p -cp "$M" net.minecraft.client.model.monster.skeleton.SkeletonModel | sed -n '/createDefaultSkeletonMesh/,/return/p' | grep -E "ldc|bipush|iconst|sipush|invoke|fconst" | head -80
javap -c -p -cp "$M" net.minecraft.client.model.geom.builders.LayerDefinitions | grep -n -B2 -A12 "STRAY_OUTER_LAYER" | head -40
