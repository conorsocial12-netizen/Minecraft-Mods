package com.conor.dappledforest.leafpiles;

import java.util.List;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
	private ModBlocks() {
	}

	public static final Block RED_LEAF_PILE = register("red_leaf_pile", MapColor.COLOR_RED);
	public static final Block ORANGE_LEAF_PILE = register("orange_leaf_pile", MapColor.COLOR_ORANGE);
	public static final Block YELLOW_LEAF_PILE = register("yellow_leaf_pile", MapColor.COLOR_YELLOW);

	public static final List<Block> ALL = List.of(RED_LEAF_PILE, ORANGE_LEAF_PILE, YELLOW_LEAF_PILE);

	private static Block register(String name, MapColor color) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, DappledLeafPiles.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, new LeafPileBlock(
				BlockBehaviour.Properties.of()
						.setId(blockKey)
						.mapColor(color)
						.strength(0.1f)
						.sound(SoundType.GRASS)
						.noOcclusion()
						.ignitedByLava()));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, DappledLeafPiles.id(name));
		Registry.register(BuiltInRegistries.ITEM, itemKey,
				new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
		return block;
	}

	public static void init() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
			for (Block block : ALL) {
				output.accept(block);
			}
		});
	}
}
