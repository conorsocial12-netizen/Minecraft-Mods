package com.conor.dappledfox.entity;

import com.conor.dappledfox.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Dappled Fox: a big, rideable fox from the Dappled Forest.
 *
 * <ul>
 *   <li>Wild: feed it sweet berries; each berry has a 1-in-3 chance to tame it.</li>
 *   <li>Tamed: put a saddle on it to ride and steer it (works like a horse, including jumping).</li>
 *   <li>Riding: your attack turns into the fox's bite, which hits harder than a fist.</li>
 *   <li>Sweet or glow berries heal it, grow babies, and make two tamed foxes breed.</li>
 * </ul>
 */
public class DappledFoxEntity extends AbstractHorse {
	public static final byte BITE_EVENT = 4;
	public static final int BITE_ANIM_LENGTH = 8;
	public static final int BITE_COOLDOWN = 10;
	private static final int TAME_CHANCE = 3; // 1 in 3 per berry

	private int biteCooldown;
	private int biteAnimTicks;

	public DappledFoxEntity(EntityType<? extends DappledFoxEntity> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return AbstractHorse.createBaseHorseAttributes()
				.add(Attributes.MAX_HEALTH, 30.0)
				.add(Attributes.MOVEMENT_SPEED, 0.3)     // horses average ~0.225; foxes are quick
				.add(Attributes.JUMP_STRENGTH, 0.85)     // clears about 2.5 blocks
				.add(Attributes.ATTACK_DAMAGE, 6.0)      // base bite: 3 hearts
				.add(Attributes.STEP_HEIGHT, 1.0)
				.add(Attributes.SAFE_FALL_DISTANCE, 5.0); // lands like a cat
	}

	// ---------------------------------------------------------------- AI

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.3));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.2, stack -> stack.is(Items.SWEET_BERRIES), false));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	// ---------------------------------------------------------------- Taming, feeding, riding

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!this.isTamed() && !this.isBaby()) {
			if (stack.is(Items.SWEET_BERRIES)) {
				if (!this.level().isClientSide()) {
					stack.consume(1, player);
					this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
					if (this.random.nextInt(TAME_CHANCE) == 0) {
						this.tameWithName(player); // hearts + advancement
					} else {
						this.level().broadcastEntityEvent(this, (byte) 6); // smoke
					}
				}
				return InteractionResult.SUCCESS;
			}
			if (!this.isVehicle()) {
				// Wild foxes won't let you climb on. Bring berries.
				this.makeMad();
				return InteractionResult.SUCCESS;
			}
		}

		if (this.isFood(stack) && (this.isTamed() || this.isBaby())) {
			return this.fedFood(player, stack);
		}

		if (this.isTamed() && !this.isBaby() && !this.isVehicle()) {
			if (player.isSecondaryUseActive()) {
				this.openCustomInventoryScreen(player); // saddle slot
				return InteractionResult.SUCCESS;
			}
			if (!stack.isEmpty()) {
				// Lets saddles, name tags etc. do their normal thing.
				InteractionResult result = stack.interactLivingEntity(player, this, hand);
				if (result.consumesAction()) return result;
			}
			this.doPlayerRide(player);
			return InteractionResult.SUCCESS;
		}

		return super.mobInteract(player, hand);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(Items.SWEET_BERRIES) || stack.is(Items.GLOW_BERRIES);
	}

	@Override
	protected boolean handleEating(Player player, ItemStack stack) {
		if (!this.isFood(stack)) return super.handleEating(player, stack);

		boolean used = false;
		if (this.getHealth() < this.getMaxHealth()) {
			this.heal(4.0F);
			used = true;
		}
		if (this.isBaby()) {
			this.ageUp(10);
			used = true;
		} else if (this.isTamed() && this.getAge() == 0 && this.canFallInLove()) {
			this.setInLove(player);
			used = true;
		}
		if (used) this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
		return used;
	}

	@Override
	public boolean canMate(Animal other) {
		return other != this
				&& other instanceof DappledFoxEntity fox
				&& this.isTamed() && fox.isTamed()
				&& this.isInLove() && fox.isInLove();
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return ModEntities.DAPPLED_FOX.create(level, EntitySpawnReason.BREEDING);
	}

	@Override
	protected boolean canPerformRearing() {
		return false; // foxes don't rear up like horses
	}

	// ---------------------------------------------------------------- Bite

	/** Called on the server when the rider attacks something. */
	public boolean tryRiderBite(ServerLevel level, Player rider, Entity target) {
		if (this.biteCooldown > 0) return false;
		this.biteCooldown = BITE_COOLDOWN;

		// Fox bite + half of whatever the rider's weapon would do.
		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE)
				+ (float) rider.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5F;

		// The fox does the biting, the rider gets the credit (XP, loot, aggro).
		DamageSource source = new DamageSource(
				level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.MOB_ATTACK),
				this, rider);

		boolean hit = target.hurtServer(level, source, damage);
		if (hit && target instanceof LivingEntity living) {
			float yaw = this.getYRot() * Mth.DEG_TO_RAD;
			// Shove the target away from the fox's snout
			living.push(-Mth.sin(yaw) * 0.6, 0.15, Mth.cos(yaw) * 0.6);
			living.hurtMarked = true;
		}
		this.playSound(SoundEvents.FOX_BITE, 1.0F, 0.8F + this.random.nextFloat() * 0.3F);
		level.broadcastEntityEvent(this, BITE_EVENT);
		return hit;
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == BITE_EVENT) {
			this.biteAnimTicks = BITE_ANIM_LENGTH;
		} else {
			super.handleEntityEvent(id);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.biteCooldown > 0) this.biteCooldown--;
		if (this.biteAnimTicks > 0) this.biteAnimTicks--;
	}

	/** 0 → 1 → 0 over the bite, for the head-lunge animation. */
	public float getBiteAmount(float partialTick) {
		if (this.biteAnimTicks <= 0) return 0.0F;
		float t = (BITE_ANIM_LENGTH - this.biteAnimTicks + partialTick) / BITE_ANIM_LENGTH;
		return Mth.sin(Mth.clamp(t, 0.0F, 1.0F) * Mth.PI);
	}

	// ---------------------------------------------------------------- Spawning

	@Override
	public boolean checkSpawnRules(LevelAccessor level, EntitySpawnReason reason) {
		if (reason == EntitySpawnReason.NATURAL || reason == EntitySpawnReason.CHUNK_GENERATION) {
			BlockState below = level.getBlockState(this.blockPosition().below());
			if (!below.is(BlockTags.FOXES_SPAWNABLE_ON) && !below.is(BlockTags.ANIMALS_SPAWNABLE_ON)) {
				return false;
			}
		}
		return super.checkSpawnRules(level, reason);
	}

	// ---------------------------------------------------------------- Sounds (reuse vanilla fox sounds)

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.FOX_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.FOX_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.FOX_DEATH;
	}

	@Override
	protected SoundEvent getEatingSound() {
		return SoundEvents.FOX_EAT;
	}

	@Override
	protected SoundEvent getAngrySound() {
		return SoundEvents.FOX_AGGRO;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		if (!state.liquid()) {
			this.playSound(SoundEvents.WOLF_STEP.value(), 0.2F, 0.9F);
		}
	}

	@Override
	public float getVoicePitch() {
		return super.getVoicePitch() * 0.8F; // bigger fox, deeper voice
	}
}
