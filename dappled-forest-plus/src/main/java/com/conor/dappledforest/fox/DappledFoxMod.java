package com.conor.dappledforest.fox;

import com.conor.dappledforest.fox.entity.DappledFoxEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;

public class DappledFoxMod implements ModInitializer {
	public static final String MOD_ID = "dappledforest";

	/** Biomes the Dappled Fox spawns in. Defaults to minecraft:dappled_forest (see data/dappledforest/tags/worldgen/biome). */
	public static final TagKey<Biome> SPAWNS_IN = TagKey.create(Registries.BIOME, id("spawns_dappled_fox"));

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModEntities.initialize();

		// Natural spawning: weight 8, groups of 1-2 (wolves are 8 / 4-4, foxes 8 / 2-4 for comparison).
		BiomeModifications.addSpawn(BiomeSelectors.tag(SPAWNS_IN), MobCategory.CREATURE, ModEntities.DAPPLED_FOX, 8, 1, 2);

		// While riding a tamed Dappled Fox, your attack becomes the fox's bite.
		AttackEntityCallback.EVENT.register((player, level, hand, target, hitResult) -> {
			if (player.isSpectator()) return InteractionResult.PASS;
			if (!(player.getVehicle() instanceof DappledFoxEntity fox) || !fox.isTamed() || target == fox) {
				return InteractionResult.PASS;
			}
			if (level instanceof ServerLevel serverLevel) {
				fox.tryRiderBite(serverLevel, player, target);
			}
			// SUCCESS replaces the normal punch; on the client it still sends the attack to the server.
			return InteractionResult.SUCCESS;
		});
	}
}
