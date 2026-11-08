package com.conor.dappledforest.arrow.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** SpawnPlacements.register is private in vanilla and Fabric has no API for it, so we call it through this. */
@Mixin(SpawnPlacements.class)
public interface SpawnPlacementsInvoker {
	@Invoker("register")
	static <T extends Mob> void dappledforest$register(EntityType<T> type, SpawnPlacementType placement,
			Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate) {
		throw new AssertionError();
	}
}
