JARS=$(find ~/.gradle -name '*.jar' | grep -E 'minecraft-(common|clientonly)-deobf|fabric-(rendering|lifecycle|command|networking|object-builder)' | grep -v sources)
for j in $JARS; do unzip -Z1 "$j" | sed "s|^|$j!|"; done > /tmp/all.txt
dump() {
  for line in $(grep -E "/$1\.class$" /tmp/all.txt | head -2); do
    jar=${line%%!*}; cls=${line#*!}; cls=${cls%.class}; cls=${cls//\//.}
    echo "=== $cls"; javap -p $3 -cp "$jar" "$cls" | grep -v lambda | grep -E "${2:-.}" | head -${4:-80}
  done
}
dump Entity "abstract|protected Entity|public Entity\(|defineSynchedData|readAdditionalSaveData|addAdditionalSaveData|hurtServer|isPickable|noPhysics|setNoGravity|getHeight|spawnAtLocation|push\(|setDeltaMovement|hurtMarked|resetFallDistance|fallDistance|isPushedByFluid|shouldRender|isAttackable" "" 60
dump ItemModelResolver "public" "" 20
dump ItemDisplayContext "public static final" "" 20
dump EntityRenderer "protected EntityRenderer|public EntityRenderer|submit|createRenderState|extractRenderState|shouldRender|affectedByCulling|getBoundingBoxForCulling" "" 20
dump ItemStackRenderState "public" "" 20
dump ClientboundSetEntityMotionPacket "public" "" 10
dump ServerPlayer "connection|hurtMarked|resetFallDistance" "" 10
dump CommandSourceStack "hasPermission|permissions|getPlayer|sendSuccess|getLevel|getPosition" "" 20
dump Commands "public static|LEVEL_" "" 40
dump CommandRegistrationCallback "." "" 10
dump ServerTickEvents "." "" 20
dump ServerLevel "isRaining|players\(|getHeight|sendParticles|getBiome|getGameTime|isDay" "" 20
dump Heightmap\$Types "public static final" "" 10
dump ParticleTypes "public static final" "" 200 | grep -iE "leaf|leaves|poplar|cloud|gust|dust_plume|small_gust|white_ash|tinted"
dump MobEffects "SLOW_FALLING|LEVITATION" "" 5
dump SoundEvents "WIND|BREEZE|GUST|ELYTRA|AZALEA_LEAVES|LEAF_LITTER|WEATHER" "" 30
dump Gui "setOverlayMessage|setTitle" "" 5
dump ServerPlayer "sendSystemMessage|displayClientMessage" "" 5
dump Player "displayClientMessage|sendOverlayMessage" "" 5
dump SubmitNodeCollector "public" "" 20
dump AbstractArrowRenderer "." "" 10
