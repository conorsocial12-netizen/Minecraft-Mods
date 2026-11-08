package com.conor.dappledforest.arrow;

import com.conor.dappledforest.arrow.mixin.SpawnPlacementsInvoker;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ModEntities {
	private static final ResourceKey<EntityType<?>> POPLAR_ARROW_KEY =
			ResourceKey.create(Registries.ENTITY_TYPE, PoplarArrowMod.id("poplar_arrow"));
	private static final ResourceKey<EntityType<?>> DAPPLED_SKELETON_KEY =
			ResourceKey.create(Registries.ENTITY_TYPE, PoplarArrowMod.id("dappled_skeleton"));

	// Same size / tracking settings as the vanilla arrow
	public static final EntityType<PoplarArrow> POPLAR_ARROW = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			POPLAR_ARROW_KEY,
			EntityType.Builder.<PoplarArrow>of(PoplarArrow::new, MobCategory.MISC)
					.noLootTable()
					.sized(0.5F, 0.5F)
					.eyeHeight(0.13F)
					.clientTrackingRange(4)
					.updateInterval(20)
					.build(POPLAR_ARROW_KEY));

	// Same size as a vanilla skeleton
	public static final EntityType<DappledSkeleton> DAPPLED_SKELETON = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			DAPPLED_SKELETON_KEY,
			EntityType.Builder.<DappledSkeleton>of(DappledSkeleton::new, MobCategory.MONSTER)
					.sized(0.6F, 1.99F)
					.eyeHeight(1.74F)
					.ridingOffset(-0.7F)
					.clientTrackingRange(8)
					.build(DAPPLED_SKELETON_KEY));

	/** Spawn weight in the dappled forest. Normal skeletons there are 100, zombies 95, spiders 100. */
	public static final int DAPPLED_SKELETON_WEIGHT = 60;

	static void init() {
		FabricDefaultAttributeRegistry.register(DAPPLED_SKELETON, AbstractSkeleton.createAttributes());

		SpawnPlacementsInvoker.dappledforest$register(DAPPLED_SKELETON,
				SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				DappledSkeleton::checkDappledSpawnRules);

		BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.DAPPLED_FOREST),
				MobCategory.MONSTER, DAPPLED_SKELETON, DAPPLED_SKELETON_WEIGHT, 1, 3);
	}

	private ModEntities() {}
}
