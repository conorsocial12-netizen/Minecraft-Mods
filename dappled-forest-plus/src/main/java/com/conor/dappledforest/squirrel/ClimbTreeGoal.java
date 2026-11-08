package com.conor.dappledforest.squirrel;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * Every so often a squirrel that's free to roam picks a nearby tree, scampers up the trunk
 * (it can climb walls like a spider) and sits up in the canopy for a while.
 */
public class ClimbTreeGoal extends Goal {
	private static final int SEARCH_RADIUS = 7;

	private final AcornSquirrel squirrel;
	private BlockPos treeTop;
	private int ticks;

	public ClimbTreeGoal(AcornSquirrel squirrel) {
		this.squirrel = squirrel;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (this.squirrel.isPassenger() || this.squirrel.isOrderedToSit() || this.squirrel.isInWater()) return false;
		// Tamed squirrels stay with their owner instead
		if (this.squirrel.isTame()) return false;
		if (this.squirrel.getRandom().nextInt(160) != 0) return false;
		this.treeTop = this.findTreeTop();
		return this.treeTop != null;
	}

	@Override
	public boolean canContinueToUse() {
		return this.ticks < 400 && this.treeTop != null && !this.squirrel.isInWater();
	}

	@Override
	public void start() {
		this.ticks = 0;
		this.squirrel.getNavigation().moveTo(this.treeTop.getX() + 0.5, this.treeTop.getY(), this.treeTop.getZ() + 0.5, 1.2);
	}

	@Override
	public void tick() {
		this.ticks++;
		if (this.ticks % 20 == 0 && this.squirrel.blockPosition().distSqr(this.treeTop) > 2) {
			this.squirrel.getNavigation().moveTo(this.treeTop.getX() + 0.5, this.treeTop.getY(), this.treeTop.getZ() + 0.5, 1.2);
		}
	}

	@Override
	public void stop() {
		this.treeTop = null;
	}

	/** Find a log within reach and return the air block just above that tree's canopy. */
	private BlockPos findTreeTop() {
		BlockPos origin = this.squirrel.blockPosition();
		var level = this.squirrel.level();
		for (int attempt = 0; attempt < 12; attempt++) {
			BlockPos probe = origin.offset(
					this.squirrel.getRandom().nextInt(SEARCH_RADIUS * 2 + 1) - SEARCH_RADIUS,
					this.squirrel.getRandom().nextInt(3) - 1,
					this.squirrel.getRandom().nextInt(SEARCH_RADIUS * 2 + 1) - SEARCH_RADIUS);
			if (!level.getBlockState(probe).is(BlockTags.LOGS)) continue;
			BlockPos pos = probe;
			int guard = 0;
			// Up the trunk, then up through the leaves, to the first air block
			while (guard++ < 24 && (level.getBlockState(pos).is(BlockTags.LOGS) || level.getBlockState(pos).is(BlockTags.LEAVES))) {
				pos = pos.above();
			}
			if (level.getBlockState(pos).isAir() && pos.getY() - origin.getY() >= 3) return pos;
		}
		return null;
	}
}
