package com.conor.dappledleafpiles;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Registers the leaf pile feature and adds it to the Dappled Forest.
 * How often it appears is set in data/dappledleafpiles/worldgen/placed_feature/leaf_piles.json.
 */
public final class ModWorldgen {
	private ModWorldgen() {
	}

	public static final Feature<NoneFeatureConfiguration> LEAF_PILES = Registry.register(
			BuiltInRegistries.FEATURE, DappledLeafPiles.id("leaf_piles"),
			new LeafPileFeature(NoneFeatureConfiguration.CODEC));

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
