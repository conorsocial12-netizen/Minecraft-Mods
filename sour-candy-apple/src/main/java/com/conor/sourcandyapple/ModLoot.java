package com.conor.sourcandyapple;

import java.util.Set;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

/**
 * Adds one extra pool to the three vanilla poplar leaf loot tables. That pool points at
 * data/sourcandyapple/loot_table/dappled_poplar_leaves_bonus.json, which holds the actual
 * rules (Dappled Forest only, 0.1% chance). Tweak the chance in that JSON file.
 */
public final class ModLoot {
	private ModLoot() {
	}

	private static final Set<ResourceKey<LootTable>> POPLAR_LEAVES = Set.of(
			vanillaBlockTable("red_poplar_leaves"),
			vanillaBlockTable("orange_poplar_leaves"),
			vanillaBlockTable("yellow_poplar_leaves"));

	private static final ResourceKey<LootTable> BONUS_TABLE = ResourceKey.create(
			Registries.LOOT_TABLE,
			Identifier.fromNamespaceAndPath(SourCandyApple.MOD_ID, "dappled_poplar_leaves_bonus"));

	private static ResourceKey<LootTable> vanillaBlockTable(String block) {
		return ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("blocks/" + block));
	}

	public static void init() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			// Only touch the built-in tables, so data packs that replace them are respected.
			if (source.isBuiltin() && POPLAR_LEAVES.contains(key)) {
				tableBuilder.withPool(LootPool.lootPool()
						.add(NestedLootTable.lootTableReference(BONUS_TABLE)));
			}
		});
	}
}
