package com.conor.poplararrow;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * A skeleton covered in poplar leaves. Behaves like a normal skeleton (bow AI, burns in daylight,
 * same health and sounds) but every arrow it fires is a Poplar Arrow, so hits cover your screen in leaves.
 */
public class DappledSkeleton extends Skeleton {
	public DappledSkeleton(EntityType<? extends DappledSkeleton> type, Level level) {
		super(type, level);
	}

	/** The ammo a mob "has" for its bow. Skeletons normally get plain arrows; we hand out Poplar Arrows. */
	@Override
	public ItemStack getProjectile(ItemStack weapon) {
		return new ItemStack(ModItems.POPLAR_ARROW);
	}

	/** Normal skeletons turn into Strays in powder snow. A leafy one shouldn't, so do nothing. */
	@Override
	public void startFreezeConversion(int ticks) {
	}

	/**
	 * Natural spawning rule: normal monster rules (dark enough, valid block, not peaceful)
	 * plus it has to be night-time and open to the sky, so it only shows up on the forest floor at night,
	 * never in caves under the forest or during a thunderstorm day.
	 */
	public static boolean checkDappledSpawnRules(EntityType<? extends Mob> type, ServerLevelAccessor level,
			EntitySpawnReason reason, BlockPos pos, RandomSource random) {
		if (reason == EntitySpawnReason.NATURAL) {
			if (!level.getLevel().isDarkOutside()) return false;
			if (!level.canSeeSky(pos)) return false;
		}
		return Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
	}
}
