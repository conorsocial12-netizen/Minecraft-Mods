set -x
JARS=$(find ~/.gradle /home/runner/work -name '*.jar' 2>/dev/null | grep -iE 'minecraft|fabric' | grep -v sources)
echo "$JARS" | head -80
MC=$(find ~/.gradle /home/runner/work -name '*.jar' 2>/dev/null | grep -iE 'minecraft-(merged|client|common)' | grep -v sources | head -5)
echo "MC JARS: $MC"
for j in $JARS; do unzip -Z1 "$j" 2>/dev/null | sed "s|^|$j!|"; done > /tmp/all.txt
set +x
find_cls() { grep -E "/$1\.class$" /tmp/all.txt | head -3; }
dump() {
  for line in $(find_cls "$1"); do
    jar=${line%%!*}; cls=${line#*!}; cls=${cls%.class}; cls=${cls//\//.}
    echo "=================== $cls  ($jar)"
    javap -p -cp "$jar" "$cls" 2>&1 | head -${2:-200}
  done
}
for c in AbstractSkeleton Skeleton Bogged SpawnPlacements SpawnPlacementTypes Monster ProjectileUtil SpawnEggItem Biomes \
  SkeletonRenderer AbstractSkeletonRenderer BoggedRenderer StrayRenderer SkeletonClothingLayer SkeletonModel SkeletonRenderState \
  BiomeModifications BiomeSelectors FabricDefaultAttributeRegistry EntityModelLayerRegistry EntityRendererRegistry \
  RangedBowAttackGoal DimensionType; do dump $c 260; done
dump 'Item\$Properties' 120
dump 'EntityType\$Builder' 80
dump ModelLayers 400 | grep -iE "skeleton|stray|bogged|class"
dump Level 600 | grep -iE "bright|dark|isDay|night|class "
dump LivingEntity 2000 | grep -iE "loottable|class "
grep -iE "dappled|poplar" /tmp/all.txt | head -40
grep -iE "SpawnPlacement|spawn.*restriction|Spawn" /tmp/all.txt | grep -i fabric | head -40
for j in $MC; do
  for f in data/minecraft/worldgen/biome/dappled_forest.json data/minecraft/loot_table/entities/skeleton.json data/minecraft/loot_table/entities/bogged.json; do
    echo "=================== $f in $j"; unzip -p "$j" "$f" 2>/dev/null | head -150
  done
  unzip -Z1 "$j" | grep -iE "textures/entity/skeleton|spawn_egg|items/skeleton" | head -20
done
