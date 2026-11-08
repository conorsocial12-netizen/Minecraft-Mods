package com.conor.poplararrow;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
	private static final ResourceKey<EntityType<?>> POPLAR_ARROW_KEY =
			ResourceKey.create(Registries.ENTITY_TYPE, PoplarArrowMod.id("poplar_arrow"));

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

	static void init() {}

	private ModEntities() {}
}
