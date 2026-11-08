package com.conor.dappledforest.squirrel;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * The Acorn Squirrel: a tiny, quick critter from the Dappled Forest.
 * <ul>
 *   <li>Wild: skittish, climbs trees, and drops an acorn now and then.</li>
 *   <li>Feed it acorns: 1 in 3 chance per acorn to tame it. Acorns also heal it and breed two tamed squirrels.</li>
 *   <li>Tamed: follows you and fetches any dropped item within 12 blocks, one at a time, bringing it to your feet.</li>
 *   <li>Right-click with an empty hand: sit / stand.</li>
 *   <li>Crouch + right-click with an empty hand: it hops onto your shoulder. It hops off to fetch, then climbs back on.
 *       Hold crouch for a second to put it down for good.</li>
 * </ul>
 */
public class AcornSquirrel extends TamableAnimal {
	private static final EntityDataAccessor<Boolean> DATA_CLIMBING =
			SynchedEntityData.defineId(AcornSquirrel.class, EntityDataSerializers.BOOLEAN);

	private static final int TAME_CHANCE = 3;           // 1 in 3 per acorn
	private static final int SNEAK_TICKS_TO_HOP_OFF = 20; // hold crouch for 1 second

	/** Ticks until a wild squirrel drops its next acorn (like a chicken's egg timer). */
	private int acornTime = this.random.nextInt(6000) + 6000;
	/** The squirrel wants to ride on its owner's shoulder whenever it isn't busy fetching. */
	private boolean perchMode;
	private int ownerSneakTicks;
	private int remountCooldown;

	public AcornSquirrel(EntityType<? extends AcornSquirrel> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return TamableAnimal.createAnimalAttributes()
				.add(Attributes.MAX_HEALTH, 8.0)
				.add(Attributes.MOVEMENT_SPEED, 0.33)
				.add(Attributes.SAFE_FALL_DISTANCE, 24.0); // lands on its feet from tree tops
	}

	// ------------------------------------------------------------------ AI

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.6));
		this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(3, new FetchItemGoal(this));
		this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.2, 1.6,
				e -> !this.isTame() && !(e instanceof Player p && (isFoodStack(p.getMainHandItem()) || isFoodStack(p.getOffhandItem())))));
		this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
		this.goalSelector.addGoal(6, new TemptGoal(this, 1.1, AcornSquirrel::isFoodStack, false));
		this.goalSelector.addGoal(7, new FollowOwnerGoal(this, 1.3, 6.0F, 2.0F));
		this.goalSelector.addGoal(8, new ClimbTreeGoal(this));
		this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
	}

	private static boolean isFoodStack(ItemStack stack) {
		return stack.is(SquirrelMod.ACORN);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return isFoodStack(stack);
	}

	// ------------------------------------------------------------------ Climbing (spider-style)

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new WallClimberNavigation(this, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_CLIMBING, false);
	}

	public boolean isClimbing() {
		return this.entityData.get(DATA_CLIMBING);
	}

	@Override
	public boolean onClimbable() {
		return this.isClimbing() && !this.isPassenger();
	}

	// ------------------------------------------------------------------ Ticking

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide()) return;

		this.entityData.set(DATA_CLIMBING, this.horizontalCollision && !this.isPassenger());

		if (this.remountCooldown > 0) this.remountCooldown--;
		LivingEntity owner = this.getOwner();

		if (this.getVehicle() instanceof Player rider) {
			// Sitting on a shoulder: face the same way as the player
			this.setYRot(rider.yBodyRot);
			this.yBodyRot = rider.yBodyRot;
			this.yHeadRot = rider.getYHeadRot();
			if (rider.isShiftKeyDown()) {
				if (++this.ownerSneakTicks >= SNEAK_TICKS_TO_HOP_OFF) {
					this.perchMode = false;
					this.hopOff();
				}
			} else {
				this.ownerSneakTicks = 0;
			}
			if (rider.isInWater() || rider.isFallFlying()) {
				this.hopOff();
			}
		} else if (this.perchMode && owner instanceof Player player && this.canPerchOn(player)) {
			this.perchOn(player);
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.level() instanceof ServerLevel serverLevel && this.isAlive() && !this.isBaby() && !this.isTame()) {
			if (--this.acornTime <= 0) {
				this.playSound(SoundEvents.CHICKEN_EGG, 0.6F, 1.6F);
				this.spawnAtLocation(serverLevel, SquirrelMod.ACORN);
				this.acornTime = this.random.nextInt(6000) + 6000;
			}
		}
	}

	// ------------------------------------------------------------------ Shoulder perch

	public boolean isPerchMode() {
		return this.perchMode;
	}

	boolean canPerchOn(Player player) {
		return this.remountCooldown == 0
				&& !this.isOrderedToSit()
				&& this.getMainHandItem().isEmpty()
				&& !player.isShiftKeyDown()
				&& !player.isInWater()
				&& !player.isSpectator()
				&& player.getPassengers().isEmpty()
				&& this.distanceToSqr(player) < 4.0;
	}

	void perchOn(Player player) {
		this.getNavigation().stop();
		if (this.startRiding(player, true, true)) {
			this.playSound(SoundEvents.RABBIT_JUMP, 0.6F, 1.5F);
		}
	}

	/** Jump down off the shoulder (it keeps perchMode, so it climbs back on later unless told otherwise). */
	public void hopOff() {
		if (this.isPassenger()) {
			this.stopRiding();
			this.remountCooldown = 40;
			this.ownerSneakTicks = 0;
		}
	}

	/** Where the squirrel attaches to a player: on the left shoulder instead of on top of the head. */
	@Override
	public Vec3 getVehicleAttachmentPoint(Entity vehicle) {
		if (vehicle instanceof Player player) {
			float yaw = player.yBodyRot * ((float) Math.PI / 180F);
			double side = 0.36;
			// Our position = vehicle's passenger point - this vector. Passenger point is the top of the player's head.
			return new Vec3(-side * Math.cos(yaw), 0.42, -side * Math.sin(yaw));
		}
		return super.getVehicleAttachmentPoint(vehicle);
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		// Don't suffocate in ceilings while riding on a shoulder
		if (this.isPassenger() && source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)) return false;
		return super.hurtServer(level, source, amount);
	}

	// ------------------------------------------------------------------ Interaction

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		// Taming with acorns
		if (!this.isTame() && this.isFood(stack)) {
			if (!player.getAbilities().instabuild) stack.shrink(1);
			if (!this.level().isClientSide()) {
				this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.6F);
				if (this.random.nextInt(TAME_CHANCE) == 0) {
					this.tame(player);
					this.getNavigation().stop();
					this.setOrderedToSit(false);
					this.level().broadcastEntityEvent(this, (byte) 7); // hearts
				} else {
					this.level().broadcastEntityEvent(this, (byte) 6); // smoke
				}
			}
			return InteractionResult.SUCCESS;
		}

		if (this.isTame() && this.isOwnedBy(player)) {
			// Heal with acorns (if full health, the acorn goes to breeding instead, below)
			if (this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
				if (!player.getAbilities().instabuild) stack.shrink(1);
				if (!this.level().isClientSide()) {
					this.heal(3.0F);
					this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.6F);
				}
				return InteractionResult.SUCCESS;
			}
			if (stack.isEmpty()) {
				if (!this.level().isClientSide()) {
					if (player.isSecondaryUseActive()) {
						// Crouch + right-click: onto the shoulder
						this.perchMode = true;
						this.setOrderedToSit(false);
						this.remountCooldown = 0;
						if (this.canPerchOnIgnoringDistance(player)) this.perchOn(player);
					} else {
						// Right-click: sit / stand
						this.perchMode = false;
						this.setOrderedToSit(!this.isOrderedToSit());
						this.setJumping(false);
						this.getNavigation().stop();
						this.setTarget(null);
					}
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.mobInteract(player, hand);
	}

	private boolean canPerchOnIgnoringDistance(Player player) {
		return this.getMainHandItem().isEmpty() && player.getPassengers().isEmpty() && !player.isInWater();
	}

	// ------------------------------------------------------------------ Breeding

	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		AcornSquirrel baby = SquirrelMod.ACORN_SQUIRREL.create(level, net.minecraft.world.entity.EntitySpawnReason.BREEDING);
		if (baby != null && this.isTame()) {
			baby.setOwnerReference(this.getOwnerReference());
			baby.setTame(true, true);
		}
		return baby;
	}

	// ------------------------------------------------------------------ Fetching helpers (used by FetchItemGoal)

	void holdItem(ItemStack stack) {
		this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, stack);
	}

	ItemStack takeHeldItem() {
		ItemStack held = this.getMainHandItem().copy();
		this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		return held;
	}

	// ------------------------------------------------------------------ Saving

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putInt("AcornLayTime", this.acornTime);
		output.putBoolean("PerchMode", this.perchMode);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.acornTime = input.getIntOr("AcornLayTime", this.acornTime);
		this.perchMode = input.getBooleanOr("PerchMode", false);
	}

	// ------------------------------------------------------------------ Sounds

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.RABBIT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.RABBIT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.RABBIT_DEATH;
	}

	@Override
	public float getVoicePitch() {
		return super.getVoicePitch() * 1.5F; // little chittery squeaks
	}

	@Override
	public void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
		// Too light to make footstep sounds
	}
}
