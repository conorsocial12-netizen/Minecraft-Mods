package com.conor.dappledforest.arrow;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class ModItems {
	private static final ResourceKey<Item> POPLAR_ARROW_KEY =
			ResourceKey.create(Registries.ITEM, PoplarArrowMod.id("poplar_arrow"));
	private static final ResourceKey<Item> DAPPLED_SKELETON_SPAWN_EGG_KEY =
			ResourceKey.create(Registries.ITEM, PoplarArrowMod.id("dappled_skeleton_spawn_egg"));

	public static final Item POPLAR_ARROW = Registry.register(
			BuiltInRegistries.ITEM,
			POPLAR_ARROW_KEY,
			new PoplarArrowItem(new Item.Properties().setId(POPLAR_ARROW_KEY)));

	public static final Item DAPPLED_SKELETON_SPAWN_EGG = Registry.register(
			BuiltInRegistries.ITEM,
			DAPPLED_SKELETON_SPAWN_EGG_KEY,
			new SpawnEggItem(new Item.Properties()
					.setId(DAPPLED_SKELETON_SPAWN_EGG_KEY)
					.spawnEgg(ModEntities.DAPPLED_SKELETON)));

	static void init() {}

	private ModItems() {}
}
