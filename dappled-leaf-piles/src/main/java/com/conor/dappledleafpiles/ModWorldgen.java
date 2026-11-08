package com.conor.dappledleafpiles;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Adds the leaf pile patches to the Dappled Forest. The patch itself is data-driven:
 * worldgen/configured_feature/leaf_piles.json (shape of a patch) and placed_feature/leaf_piles.json (how often).
 */
public final class ModWorldgen {
	private ModWorldgen() {
	}

	private static final ResourceKey<Biome> DAPPLED_FOREST =
			ResourceKey.create(Registries.BIOME, Identifier.withDefaultNamespace("dappled_forest"));

	private static final ResourceKey<PlacedFeature> LEAF_PILES_PLACED =
			ResourceKey.create(Registries.PLACED_FEATURE, DappledLeafPiles.id("leaf_piles"));

	public static void init() {
		BiomeModifications.addFeature(
				BiomeSelectors.includeByKey(DAPPLED_FOREST),
				GenerationStep.Decoration.VEGETAL_DECORATION,
				LEAF_PILES_PLACED);
	}
}
