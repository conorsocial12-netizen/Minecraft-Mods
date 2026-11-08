package com.conor.dappledforest.squirrel;

import com.conor.dappledforest.DappledForestPlus;
import com.conor.dappledforest.arrow.mixin.SpawnPlacementsInvoker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

/** Registers the Acorn Squirrel, the Acorn, the spawn egg and the squirrel's Dappled Forest spawning. */
public class SquirrelMod implements ModInitializer {
	private static final ResourceKey<EntityType<?>> SQUIRREL_KEY =
			ResourceKey.create(Registries.ENTITY_TYPE, DappledForestPlus.id("acorn_squirrel"));
	private static final ResourceKey<Item> ACORN_KEY = ResourceKey.create(Registries.ITEM, DappledForestPlus.id("acorn"));
	private static final ResourceKey<Item> EGG_KEY = ResourceKey.create(Registries.ITEM, DappledForestPlus.id("acorn_squirrel_spawn_egg"));

	public static final EntityType<AcornSquirrel> ACORN_SQUIRREL = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			SQUIRREL_KEY,
			EntityType.Builder.<AcornSquirrel>of(AcornSquirrel::new, MobCategory.CREATURE)
					.sized(0.4F, 0.5F)
					.eyeHeight(0.4F)
					.clientTrackingRange(8)
					.build(SQUIRREL_KEY));

	/** A small snack: 1 hunger point. Also tames, heals and breeds squirrels. */
	public static final Item ACORN = Registry.register(BuiltInRegistries.ITEM, ACORN_KEY,
			new Item(new Item.Properties().setId(ACORN_KEY)
					.food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.1F).build())));

	public static final Item ACORN_SQUIRREL_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, EGG_KEY,
			new SpawnEggItem(new Item.Properties().setId(EGG_KEY).spawnEgg(ACORN_SQUIRREL)));

	/** Spawn weight in the Dappled Forest (rabbits elsewhere are 4, foxes 8). Groups of 2-3. */
	public static final int SPAWN_WEIGHT = 10;

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(ACORN_SQUIRREL, AcornSquirrel.createAttributes());

		SpawnPlacementsInvoker.dappledforest$register(ACORN_SQUIRREL,
				SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				Animal::checkAnimalSpawnRules);

		BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.DAPPLED_FOREST),
				MobCategory.CREATURE, ACORN_SQUIRREL, SPAWN_WEIGHT, 2, 3);

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> output.accept(ACORN));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(output -> output.accept(ACORN_SQUIRREL_SPAWN_EGG));
	}
}
