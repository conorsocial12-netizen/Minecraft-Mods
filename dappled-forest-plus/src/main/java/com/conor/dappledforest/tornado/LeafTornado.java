package com.conor.dappledforest.tornado;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.conor.dappledforest.leafpiles.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A swirling column of autumn leaves that wanders through the Dappled Forest on windy days.
 * <ul>
 *   <li>Anything that walks into it - players, mobs and dropped items - gets spun around and lifted up.</li>
 *   <li>At the top it flings them out sideways. Living things get a few seconds of Slow Falling so nobody takes fall damage.</li>
 *   <li>When it blows itself out it leaves a few Leaf Piles on the ground.</li>
 * </ul>
 */
public class LeafTornado extends Entity {
	public static final float RADIUS = 2.6F;   // how close you have to be to get caught
	public static final float HEIGHT = 7.0F;   // height of the funnel
	private static final double LIFT_TOP = 5.5; // where things get flung out
	private static final int FADE_TICKS = 40;

	private static final EntityDataAccessor<Integer> DATA_LIFE =
			SynchedEntityData.defineId(LeafTornado.class, EntityDataSerializers.INT);

	private float heading = this.random.nextFloat() * Mth.TWO_PI;
	private int maxLife = 600 + this.random.nextInt(600); // 30-60 seconds
	/** Entities that were just flung out, and the game time until which they're left alone. */
	private final Map<Integer, Long> recentlyThrown = new HashMap<>();

	public LeafTornado(EntityType<? extends LeafTornado> type, Level level) {
		super(type, level);
		this.noPhysics = true;
		this.setNoGravity(true);
		this.entityData.set(DATA_LIFE, this.maxLife);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_LIFE, 900);
	}

	/** Ticks left before it blows itself out (synced so the client can fade it). */
	public int getLife() {
		return this.entityData.get(DATA_LIFE);
	}

	public void setLife(int ticks) {
		this.entityData.set(DATA_LIFE, ticks);
		this.maxLife = Math.max(this.maxLife, ticks);
	}

	/** 0..1, used by the renderer to grow in and fade out. */
	public float getStrength(float partialTick) {
		float age = this.tickCount + partialTick;
		float in = Mth.clamp(age / FADE_TICKS, 0.0F, 1.0F);
		float out = Mth.clamp((this.getLife() - partialTick) / FADE_TICKS, 0.0F, 1.0F);
		return Math.min(in, out);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide()) {
			this.clientParticles();
			return;
		}
		ServerLevel level = (ServerLevel) this.level();

		int life = this.getLife() - 1;
		this.entityData.set(DATA_LIFE, life);
		if (life <= 0) {
			this.blowOut(level);
			return;
		}

		this.wander(level);
		this.catchEntities(level);

		if (this.tickCount % 30 == 0) {
			level.playSound(null, this.getX(), this.getY() + 2, this.getZ(),
					this.random.nextBoolean() ? SoundEvents.BREEZE_IDLE_GROUND : SoundEvents.BREEZE_IDLE_AIR,
					SoundSource.WEATHER, 1.2F, 0.5F + this.random.nextFloat() * 0.2F);
		}
		if (this.tickCount % 8 == 0) {
			level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LEAF_LITTER_STEP,
					SoundSource.WEATHER, 0.6F, 0.8F + this.random.nextFloat() * 0.4F);
		}
	}

	// ------------------------------------------------------------------ Movement

	private void wander(ServerLevel level) {
		// Drift in a slowly changing direction; turn back if about to leave the Dappled Forest
		this.heading += (this.random.nextFloat() - 0.5F) * 0.25F;
		double speed = 0.06;
		double nx = this.getX() - Mth.sin(this.heading) * speed;
		double nz = this.getZ() + Mth.cos(this.heading) * speed;
		BlockPos ahead = BlockPos.containing(nx - Mth.sin(this.heading) * 3, this.getY(), nz + Mth.cos(this.heading) * 3);
		if (!level.getBiome(ahead).is(Biomes.DAPPLED_FOREST) && level.getBiome(this.blockPosition()).is(Biomes.DAPPLED_FOREST)) {
			this.heading += Mth.PI * (0.6F + this.random.nextFloat() * 0.8F);
			return;
		}
		double ground = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(nx), Mth.floor(nz));
		double ny = Mth.lerp(0.15, this.getY(), ground);
		this.setPos(nx, ny, nz);
	}

	// ------------------------------------------------------------------ Lifting

	private void catchEntities(ServerLevel level) {
		long now = level.getGameTime();
		this.recentlyThrown.values().removeIf(until -> until < now);

		AABB area = new AABB(this.getX() - RADIUS, this.getY() - 1.0, this.getZ() - RADIUS,
				this.getX() + RADIUS, this.getY() + HEIGHT, this.getZ() + RADIUS);
		List<Entity> caught = level.getEntities(this, area, this::canCatch);

		for (Entity e : caught) {
			if (this.recentlyThrown.containsKey(e.getId())) continue;
			double dx = e.getX() - this.getX();
			double dz = e.getZ() - this.getZ();
			double dist = Math.sqrt(dx * dx + dz * dz);
			if (dist > RADIUS) continue;
			double nx = dist > 0.01 ? dx / dist : 1.0;
			double nz = dist > 0.01 ? dz / dist : 0.0;
			double height = e.getY() - this.getY();
			Vec3 motion;

			if (height < LIFT_TOP) {
				// Spin around the funnel (tangent), drift towards the middle, and rise
				double spin = 0.45;
				double inward = dist > 1.0 ? -0.08 : 0.04;
				motion = new Vec3(-nz * spin + nx * inward, 0.28, nx * spin + nz * inward);
				e.resetFallDistance();
			} else {
				// Flung out of the top
				motion = new Vec3(nx * 1.1 + (-nz) * 0.4, 0.35, nz * 1.1 + nx * 0.4);
				this.recentlyThrown.put(e.getId(), now + 60);
				if (e instanceof LivingEntity living) {
					living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, false, false, true));
				}
				level.playSound(null, e.getX(), e.getY(), e.getZ(), SoundEvents.BREEZE_JUMP, SoundSource.WEATHER, 0.8F, 1.2F);
			}

			e.setDeltaMovement(motion);
			if (e instanceof ServerPlayer player) {
				// Players move themselves on their own computer, so tell their game about the push
				player.connection.send(new ClientboundSetEntityMotionPacket(player));
			}
		}
	}

	private boolean canCatch(Entity e) {
		if (!e.isAlive() || e.isPassenger() || e.isSpectator() || e instanceof LeafTornado) return false;
		if (e instanceof Player p && (p.getAbilities().flying || p.isCreative())) return false;
		return e instanceof LivingEntity || e instanceof ItemEntity;
	}

	// ------------------------------------------------------------------ Ending

	/** Leave 2-4 Leaf Piles around where it died out, with a final burst of leaves. */
	private void blowOut(ServerLevel level) {
		int piles = 2 + this.random.nextInt(3);
		for (int i = 0; i < piles * 4 && piles > 0; i++) {
			int x = Mth.floor(this.getX() + (this.random.nextFloat() - 0.5F) * 5.0F);
			int z = Mth.floor(this.getZ() + (this.random.nextFloat() - 0.5F) * 5.0F);
			BlockPos pos = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
			if (level.getBlockState(pos).canBeReplaced() && level.getFluidState(pos).isEmpty()
					&& level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
				Block pile = ModBlocks.ALL.get(this.random.nextInt(ModBlocks.ALL.size()));
				level.setBlockAndUpdate(pos, pile.defaultBlockState());
				piles--;
			}
		}
		for (SimpleParticleType leaf : LEAVES) {
			level.sendParticles(leaf, this.getX(), this.getY() + 2.5, this.getZ(), 25, 1.5, 2.0, 1.5, 0.05);
		}
		level.playSound(null, this.getX(), this.getY() + 1, this.getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(),
				SoundSource.WEATHER, 1.0F, 0.6F);
		this.discard();
	}

	// ------------------------------------------------------------------ Client visuals

	static final SimpleParticleType[] LEAVES = {
			ParticleTypes.RED_POPLAR_LEAVES, ParticleTypes.ORANGE_POPLAR_LEAVES, ParticleTypes.YELLOW_POPLAR_LEAVES};

	private void clientParticles() {
		float strength = this.getStrength(0.0F);
		int count = Math.round(5 * strength);
		for (int i = 0; i < count; i++) {
			float h = this.random.nextFloat() * HEIGHT;
			float r = 0.4F + h / HEIGHT * 2.0F;
			float a = this.random.nextFloat() * Mth.TWO_PI;
			double px = this.getX() + Mth.cos(a) * r;
			double pz = this.getZ() + Mth.sin(a) * r;
			// Tangential velocity so the leaves look like they're circling
			this.level().addParticle(LEAVES[this.random.nextInt(3)], px, this.getY() + h, pz,
					-Mth.sin(a) * 0.35, 0.05, Mth.cos(a) * 0.35);
		}
		if (this.random.nextInt(4) == 0) {
			this.level().addParticle(ParticleTypes.DUST_PLUME, this.getX() + (this.random.nextFloat() - 0.5F) * 2,
					this.getY() + 0.1, this.getZ() + (this.random.nextFloat() - 0.5F) * 2, 0, 0.05, 0);
		}
	}

	// ------------------------------------------------------------------ Misc

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		return false; // you can't punch wind
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		this.setLife(input.getIntOr("Life", 900));
		this.heading = input.getFloatOr("Heading", this.heading);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		output.putInt("Life", this.getLife());
		output.putFloat("Heading", this.heading);
	}
}
