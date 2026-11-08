package com.conor.dappledforest.arrow;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * A normal arrow (same base damage, crits, Power/Punch/Flame all work) that also
 * smothers the target's screen in autumn leaves.
 */
public class PoplarArrow extends AbstractArrow {
	/** How long the leaves stay on screen per hit (ticks). 20 ticks = 1 second. */
	public static final int LEAF_DURATION_TICKS = 6 * 20;
	/** Each extra hit while still covered adds a layer, up to this amplifier (0 = 1 layer). */
	public static final int MAX_LAYERS = 2;

	private static final String[] LEAF_BLOCKS = {"red_poplar_leaves", "orange_poplar_leaves", "yellow_poplar_leaves"};

	public PoplarArrow(EntityType<? extends PoplarArrow> type, Level level) {
		super(type, level);
	}

	public PoplarArrow(Level level, LivingEntity owner, ItemStack pickup, ItemStack weapon) {
		super(ModEntities.POPLAR_ARROW, owner, level, pickup, weapon);
	}

	public PoplarArrow(Level level, double x, double y, double z, ItemStack pickup, ItemStack weapon) {
		super(ModEntities.POPLAR_ARROW, x, y, z, level, pickup, weapon);
	}

	@Override
	protected void doPostHurtEffects(LivingEntity target) {
		super.doPostHurtEffects(target);

		MobEffectInstance existing = target.getEffect(ModEffects.LEAF_BLIND);
		int layers = existing == null ? 0 : Math.min(existing.getAmplifier() + 1, MAX_LAYERS);

		// ambient=false, visible particles=false (the leaf burst below looks better), show icon=true
		target.addEffect(new MobEffectInstance(ModEffects.LEAF_BLIND, LEAF_DURATION_TICKS, layers, false, false, true),
				this.getEffectSource());

		if (this.level() instanceof ServerLevel server) {
			for (String leaf : LEAF_BLOCKS) {
				Block block = BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(leaf));
				if (block == Blocks.AIR) continue; // safety if an ID ever changes
				server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, block.defaultBlockState()),
						target.getX(), target.getEyeY(), target.getZ(),
						8, 0.3, 0.3, 0.3, 0.05);
			}
		}
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return new ItemStack(ModItems.POPLAR_ARROW);
	}
}
