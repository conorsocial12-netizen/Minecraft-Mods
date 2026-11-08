package com.conor.dappledforest;

import com.conor.dappledforest.fox.ModEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dappled Forest Plus: one mod containing every Dappled Forest addition.
 * Each feature lives in its own package and has its own initializer (listed in fabric.mod.json):
 * <ul>
 *   <li>{@code arrow}     - Poplar Arrow, Leaf Blind effect, Dappled Skeleton</li>
 *   <li>{@code apple}     - Sour Candy Apple</li>
 *   <li>{@code leafpiles} - Red / Orange / Yellow Leaf Piles</li>
 *   <li>{@code fox}       - Dappled Fox</li>
 * </ul>
 * This class holds the shared mod id, the Dappled Fox spawn egg, and the creative tab entries.
 */
public class DappledForestPlus implements ModInitializer {
	public static final String MOD_ID = "dappledforest";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	private static final ResourceKey<Item> FOX_EGG_KEY = ResourceKey.create(Registries.ITEM, id("dappled_fox_spawn_egg"));

	public static final Item DAPPLED_FOX_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, FOX_EGG_KEY,
			new SpawnEggItem(new Item.Properties().setId(FOX_EGG_KEY).spawnEgg(ModEntities.DAPPLED_FOX)));

	@Override
	public void onInitialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(com.conor.dappledforest.arrow.ModItems.POPLAR_ARROW));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(output -> {
			output.accept(com.conor.dappledforest.arrow.ModItems.DAPPLED_SKELETON_SPAWN_EGG);
			output.accept(DAPPLED_FOX_SPAWN_EGG);
		});
		LOGGER.info("Dappled Forest Plus loaded: arrows, skeletons, foxes, leaf piles and sour candy apples");
	}
}
