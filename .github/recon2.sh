JARS=$(find ~/.gradle -name '*.jar' | grep -E 'minecraft-(common|clientonly)-deobf|fabric-rendering|fabric-entity|fabric-object-builder' | grep -v sources)
for j in $JARS; do unzip -Z1 "$j" | sed "s|^|$j!|"; done > /tmp/all.txt
dump() { # $1 class simple name regex, $2 grep filter, $3 -c?
  for line in $(grep -E "/$1\.class$" /tmp/all.txt | head -2); do
    jar=${line%%!*}; cls=${line#*!}; cls=${cls%.class}; cls=${cls//\//.}
    echo "=== $cls"; javap -p $3 -cp "$jar" "$cls" | grep -v lambda | grep -E "${2:-.}" | head -${4:-80}
  done
}
dump TamableAnimal "public|protected" "" 90
dump Wolf "mobInteract|isFood|tame|Goal" "" 40
dump FollowOwnerGoal "public" ; dump SitWhenOrderedToGoal "public"; dump AvoidEntityGoal "public" ; dump TemptGoal "public"
dump Entity "startRiding|stopRiding|canAddPassenger|positionRider|getPassengerAttachmentPoint|canRide|removePassenger|isPassenger|getVehicle|addPassenger|PassengerRidingPosition|getVehicleAttachmentPoint|ejectPassengers|rideTick" "" 60
dump Player "canAddPassenger|positionRider|Passenger|removeEntitiesOnShoulder|ShoulderEntity|rideTick|ejectPassengers|startRiding" "" 40
dump ServerPlayer "Passenger|startRiding|ejectPassengers|stopRiding" "" 30
dump WallClimberNavigation "public"; dump Spider "Climb|createNavigation|onClimbable|DATA_FLAGS" "" 20
dump Animal "isFood|checkAnimalSpawnRules|getBreedOffspring|mobInteract|usePlayerItem|canMate" "" 30
dump Mob "spawnAtLocation|setItemSlot|getItemBySlot|canPickUpLoot|wantsToPickUp|pickUpItem|setPersistenceRequired|getNavigation|createNavigation" "" 40
dump Chicken "eggTime|aiStep|spawnAtLocation" "" 20
dump ItemEntity "public ItemEntity|setPickUpDelay|setDefaultPickUpDelay|getItem|hasPickUpDelay|setNoPickUpDelay|getOwner|setThrower" "" 30
dump FoxHeldItemLayer "" "-c" 120
dump FoxRenderer "public|extract" "" 30
dump FoxRenderState "." "" 30
dump HoldingEntityRenderState "." "" 30
dump LivingEntityRenderState "public" "" 80
dump EntityRenderState "public" "" 80
dump LivingEntityRenderer "setupRotations|submit|scale|addLayer|extractRenderState|getRenderOffset|shouldShowName" "" 30
dump EntityRenderer "getRenderOffset|submit|extractRenderState|finalizeRenderState" "" 30
dump MobRenderer "public|protected" "" 20
dump ParrotOnShoulderLayer "." "" 30
dump ArmedModel "." "" 10
dump RenderLayer "public|protected" "" 20
dump Rabbit "Sound|createAttributes" "" 20
dump SoundEvents "SQUIRREL|RABBIT_AMBIENT|FOX_AMBIENT|FOX_EAT|FOX_HURT|FOX_SCREECH|RABBIT_HURT|RABBIT_DEATH|RABBIT_JUMP|FOX_SNIFF|ARMADILLO" "" 30
dump ShoulderRidingEntity "." "" 20
dump FoodProperties "public" "" 30
dump EntityDimensions "public static" "" 10
dump EntityType\$Builder "public" "" 40
