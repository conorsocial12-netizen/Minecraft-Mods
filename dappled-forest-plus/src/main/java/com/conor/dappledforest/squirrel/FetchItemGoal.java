package com.conor.dappledforest.squirrel;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Tamed squirrels fetch any dropped item within {@link #RANGE} blocks, one stack at a time,
 * and drop it at their owner's feet. Items the owner threw away themselves are left alone,
 * and so are items already lying next to the owner.
 */
public class FetchItemGoal extends Goal {
	public static final double RANGE = 12.0;
	private static final int GIVE_UP_TICKS = 300; // 15 seconds

	private final AcornSquirrel squirrel;
	private ItemEntity target;
	private int ticks;
	private int cooldown;

	public FetchItemGoal(AcornSquirrel squirrel) {
		this.squirrel = squirrel;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	private LivingEntity owner() {
		return this.squirrel.getOwner();
	}

	private boolean isFetchable(ItemEntity item, LivingEntity owner) {
		return item.isAlive()
				&& !item.getItem().isEmpty()
				&& item.getOwner() != owner                 // owner threw it away on purpose
				&& item.distanceToSqr(owner) > 3.0 * 3.0     // already at the owner's feet
				&& !item.isInLava() && !item.isOnFire();
	}

	private ItemEntity findItem(LivingEntity owner) {
		List<ItemEntity> items = this.squirrel.level().getEntitiesOfClass(ItemEntity.class,
				this.squirrel.getBoundingBox().inflate(RANGE, 4.0, RANGE), item -> this.isFetchable(item, owner));
		return items.stream().min(Comparator.comparingDouble(this.squirrel::distanceToSqr)).orElse(null);
	}

	@Override
	public boolean canUse() {
		if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		}
		LivingEntity owner = this.owner();
		if (!this.squirrel.isTame() || this.squirrel.isOrderedToSit() || owner == null || this.squirrel.isBaby()) return false;
		if (!this.squirrel.getMainHandItem().isEmpty()) return true; // still has something to deliver
		this.cooldown = 10;
		this.target = this.findItem(owner);
		return this.target != null;
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity owner = this.owner();
		if (owner == null || this.squirrel.isOrderedToSit() || this.ticks > GIVE_UP_TICKS) return false;
		if (!this.squirrel.getMainHandItem().isEmpty()) return true;
		return this.target != null && this.isFetchable(this.target, owner);
	}

	@Override
	public void start() {
		this.ticks = 0;
		this.squirrel.hopOff(); // jump off the shoulder to go and get it
	}

	@Override
	public void stop() {
		// Couldn't reach the owner in time: drop it where we are so it isn't lost
		if (!this.squirrel.getMainHandItem().isEmpty() && this.ticks > GIVE_UP_TICKS) {
			this.dropFor(this.owner(), this.squirrel.getX(), this.squirrel.getY() + 0.3, this.squirrel.getZ());
		}
		this.target = null;
		this.squirrel.getNavigation().stop();
		this.cooldown = 20;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		this.ticks++;
		LivingEntity owner = this.owner();
		if (owner == null) return;

		if (this.squirrel.getMainHandItem().isEmpty()) {
			// Phase 1: run to the item and grab it
			if (this.target == null) return;
			this.squirrel.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
			if (this.squirrel.distanceToSqr(this.target) < 1.6 * 1.6) {
				ItemStack stack = this.target.getItem().copy();
				this.target.discard();
				this.squirrel.holdItem(stack);
				this.squirrel.playSound(SoundEvents.ITEM_PICKUP, 0.4F, 1.6F);
				this.target = null;
				this.ticks = 0;
			} else if (this.ticks % 10 == 1) {
				this.squirrel.getNavigation().moveTo(this.target, 1.4);
			}
		} else {
			// Phase 2: bring it back and drop it at the owner's feet
			this.squirrel.getLookControl().setLookAt(owner, 30.0F, 30.0F);
			if (this.squirrel.distanceToSqr(owner) < 2.0 * 2.0) {
				this.dropFor(owner, owner.getX(), owner.getY() + 0.25, owner.getZ());
				this.squirrel.playSound(SoundEvents.FOX_SPIT, 0.6F, 1.6F);
				this.ticks = GIVE_UP_TICKS + 100; // done: end the goal
			} else if (this.ticks % 10 == 1) {
				this.squirrel.getNavigation().moveTo(owner, 1.4);
			}
		}
	}

	private void dropFor(LivingEntity owner, double x, double y, double z) {
		ItemStack stack = this.squirrel.takeHeldItem();
		if (stack.isEmpty()) return;
		ItemEntity drop = new ItemEntity(this.squirrel.level(), x, y, z, stack);
		drop.setNoPickUpDelay();
		if (owner != null) drop.setThrower(owner); // so we don't fetch it straight back
		drop.setDeltaMovement(0, 0.1, 0);
		this.squirrel.level().addFreshEntity(drop);
	}
}
