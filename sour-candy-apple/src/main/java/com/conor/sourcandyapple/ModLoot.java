package com.conor.sourcandyapple;

import java.util.Set;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

/**
 * Adds one extra pool to the three vanilla poplar leaf loot tables:
 * a Sour Candy Apple, only inside the Dappled Forest, with a {@link #DROP_CHANCE} chance.
 */
public final class ModLoot {
	private ModLoot() {
	}

	/** 0.001 = 0.1% = about 1 in 1,000 leaf blocks. */
	public static final float DROP_CHANCE = 0.001f;

	private static final Set<ResourceKey<LootTable>> POPLAR_LEAVES = Set.of(
			vanillaBlockTable("red_poplar_leaves"),
			vanillaBlockTable("orange_poplar_leaves"),
			vanillaBlockTable("yellow_poplar_leaves"));

	/** Same format as a vanilla loot table / predicate JSON condition. */
	private static final String IN_DAPPLED_FOREST = """
			{"condition": "minecraft:location_check", "predicate": {"biomes": "minecraft:dappled_forest"}}
			""";

	private static ResourceKey<LootTable> vanillaBlockTable(String block) {
		return ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("blocks/" + block));
	}

	public static void init() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			// Only touch the built-in tables, so data packs that replace them are respected.
			if (source.isBuiltin() && POPLAR_LEAVES.contains(key)) {
				LootItemCondition inDappledForest = LootItemCondition.DIRECT_CODEC
						.parse(registries.createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(IN_DAPPLED_FOREST))
						.getOrThrow();

				tableBuilder.withPool(LootPool.lootPool()
						.add(LootItem.lootTableItem(ModItems.SOUR_CANDY_APPLE))
						.when(() -> inDappledForest)
						.when(LootItemRandomChanceCondition.randomChance(DROP_CHANCE)));
			}
		});
	}
}
