package com.conor.dappledfox;

import com.conor.dappledfox.entity.DappledFoxEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
	public static final ResourceKey<EntityType<?>> DAPPLED_FOX_KEY =
			ResourceKey.create(Registries.ENTITY_TYPE, DappledFoxMod.id("dappled_fox"));

	/** Horse-sized: a horse is 1.40 x 1.60 with riders at 1.32. */
	public static final EntityType<DappledFoxEntity> DAPPLED_FOX = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			DAPPLED_FOX_KEY,
			EntityType.Builder.<DappledFoxEntity>of(DappledFoxEntity::new, MobCategory.CREATURE)
					.sized(1.2F, 1.5F)
					.eyeHeight(1.4F)
					.passengerAttachments(1.3F)
					.clientTrackingRange(10)
					.build(DAPPLED_FOX_KEY));

	private ModEntities() {
	}

	public static void initialize() {
		FabricDefaultAttributeRegistry.register(DAPPLED_FOX, DappledFoxEntity.createAttributes());
	}
}
