package com.conor.dappledleafpiles;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Scatters a small patch of mixed-colour leaf piles on solid ground around the origin. */
public class LeafPileFeature extends Feature<NoneFeatureConfiguration> {
	private static final int TRIES = 14;
	private static final int SPREAD = 4;

	public LeafPileFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		RandomSource random = context.random();
		BlockPos origin = context.origin();
		int placed = 0;

		for (int i = 0; i < TRIES; i++) {
			BlockPos pos = origin.offset(
					random.nextInt(SPREAD * 2 + 1) - SPREAD,
					random.nextInt(3) - 1,
					random.nextInt(SPREAD * 2 + 1) - SPREAD);
			BlockPos below = pos.below();
			if (level.isEmptyBlock(pos) && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
				level.setBlock(pos, ModBlocks.random(random).defaultBlockState(), Block.UPDATE_CLIENTS);
				placed++;
			}
		}
		return placed > 0;
	}
}
