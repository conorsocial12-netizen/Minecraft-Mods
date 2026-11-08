package com.conor.poplararrow;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * "Leaf Blind" is a status effect with no gameplay logic of its own. The server applies it,
 * vanilla syncs it to the client for free, and the client HUD draws leaves while it's active.
 * Using an effect (instead of a custom packet) means milk clears it and /effect works on it.
 */
public final class ModEffects {
	public static final Holder<MobEffect> LEAF_BLIND = Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT,
			PoplarArrowMod.id("leaf_blind"),
			new LeafBlindEffect());

	public static class LeafBlindEffect extends MobEffect {
		public LeafBlindEffect() {
			super(MobEffectCategory.HARMFUL, 0xE0782A); // autumn orange
		}
	}

	static void init() {}

	private ModEffects() {}
}
