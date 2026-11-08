package com.conor.poplararrow;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ModItems {
	private static final ResourceKey<Item> POPLAR_ARROW_KEY =
			ResourceKey.create(Registries.ITEM, PoplarArrowMod.id("poplar_arrow"));

	public static final Item POPLAR_ARROW = Registry.register(
			BuiltInRegistries.ITEM,
			POPLAR_ARROW_KEY,
			new PoplarArrowItem(new Item.Properties().setId(POPLAR_ARROW_KEY)));

	static void init() {}

	private ModItems() {}
}
