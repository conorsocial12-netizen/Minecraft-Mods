package com.conor.dappledforest.leafpiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A chunky pile of autumn leaves.
 * <ul>
 *   <li>Needs no support: it stays put even if the block underneath is broken.</li>
 *   <li>Has no collision. On solid ground you just wade through it.</li>
 *   <li>Over a gap, anything that steps on it sinks slowly through (like a cobweb), then drops - a trap.</li>
 * </ul>
 */
public class LeafPileBlock extends Block {
	/** Outline / hitbox: 5 pixels tall. */
	private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 5, 16);

	/** Movement multiplier while sinking: a little slower sideways, very slow downwards. */
	private static final Vec3 SINKING = new Vec3(0.7, 0.12, 0.7);

	public LeafPileBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
			InsideBlockEffectApplier effectApplier, boolean isPrecise) {
		BlockPos below = pos.below();
		boolean nothingUnderneath = level.getBlockState(below).getCollisionShape(level, below).isEmpty();
		if (nothingUnderneath) {
			entity.makeStuckInBlock(state, SINKING);
		}
	}
}
