package com.vivid;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Client-side particle effects for combat: mob-specific "material" bursts when something gets hurt
 * (feathers from chickens, gunpowder from creepers...), bigger bursts on death, fancier critical
 * hits and impact sparks on your own hits. Nothing here touches gameplay or the server.
 */
public final class HitParticles {

	// ------------------------------------------------------------------ events

	/** Any living entity took damage (seen on the client). */
	public static void onHurt(LivingEntity e, DamageSource source) {
		VividConfig c = VividConfig.get();
		if (!c.enabled || !c.particlesEnabled || !c.hitParticles) return;
		if (c.ownHitsOnly && (source == null || source.getEntity() != Minecraft.getInstance().player)) return;
		if (e == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
		burst(e, materials(e, c), (int) Math.round(7 * c.hitAmount), 0.18);
	}

	/** A living entity died. */
	public static void onDeath(LivingEntity e) {
		VividConfig c = VividConfig.get();
		if (!c.enabled || !c.particlesEnabled || !c.deathBurst) return;
		burst(e, materials(e, c), (int) Math.round(20 * c.hitAmount), 0.28);
	}

	/** You landed a critical hit (magic = enchanted weapon bonus). */
	public static void onCrit(Entity target, boolean magic) {
		VividConfig c = VividConfig.get();
		if (!c.enabled || !c.particlesEnabled || !c.fancyCrits) return;
		Level level = target.level();
		RandomSource r = target.getRandom();
		double x = target.getX(), y = target.getY(0.55), z = target.getZ();
		int ring = (int) Math.round(16 * c.critAmount);
		ParticleOptions main = magic ? ParticleTypes.ENCHANTED_HIT : ParticleTypes.CRIT;

		// Expanding ring around the target
		for (int i = 0; i < ring; i++) {
			double a = (Math.PI * 2 * i) / Math.max(1, ring) + r.nextDouble() * 0.2;
			double speed = 0.45 + r.nextDouble() * 0.15;
			level.addParticle(main, x, y, z, Math.cos(a) * speed, 0.05 + r.nextDouble() * 0.1, Math.sin(a) * speed);
		}
		// Sparkles shooting upwards
		int sparks = (int) Math.round(6 * c.critAmount);
		for (int i = 0; i < sparks; i++) {
			level.addParticle(magic ? ParticleTypes.END_ROD : ParticleTypes.FIREWORK, x, y, z,
					(r.nextDouble() - 0.5) * 0.15, 0.1 + r.nextDouble() * 0.15, (r.nextDouble() - 0.5) * 0.15);
		}
	}

	/** You swung at and hit an entity. */
	public static void onAttack(Entity attacker, Entity target) {
		VividConfig c = VividConfig.get();
		if (!c.enabled || !c.particlesEnabled || !c.impactSparks) return;
		if (!(target instanceof LivingEntity)) return;
		Level level = target.level();
		RandomSource r = target.getRandom();
		// Point on the target's surface facing the attacker
		double dx = attacker.getX() - target.getX();
		double dz = attacker.getZ() - target.getZ();
		double len = Math.max(0.001, Math.sqrt(dx * dx + dz * dz));
		double half = target.getBbWidth() * 0.5;
		double x = target.getX() + dx / len * half;
		double z = target.getZ() + dz / len * half;
		double y = target.getY(0.6);
		int n = (int) Math.round(5 * c.critAmount);
		for (int i = 0; i < n; i++) {
			level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z,
					(r.nextDouble() - 0.5) * 0.6 + dx / len * 0.2, (r.nextDouble() - 0.3) * 0.4, (r.nextDouble() - 0.5) * 0.6 + dz / len * 0.2);
		}
	}

	// ------------------------------------------------------------------ helpers

	private static void burst(LivingEntity e, ParticleOptions[] kinds, int count, double speed) {
		if (kinds == null || kinds.length == 0 || count <= 0) return;
		Level level = e.level();
		RandomSource r = e.getRandom();
		double w = e.getBbWidth() * 0.5;
		for (int i = 0; i < count; i++) {
			ParticleOptions p = kinds[i % kinds.length];
			double x = e.getX() + (r.nextDouble() - 0.5) * w * 1.6;
			double y = e.getY(0.3 + r.nextDouble() * 0.5);
			double z = e.getZ() + (r.nextDouble() - 0.5) * w * 1.6;
			level.addParticle(p, x, y, z,
					(r.nextDouble() - 0.5) * speed, 0.06 + r.nextDouble() * speed * 0.8, (r.nextDouble() - 0.5) * speed);
		}
	}

	private static ParticleOptions item(Item item) {
		return new ItemParticleOption(ParticleTypes.ITEM, item);
	}

	/** What bits fly off each mob when it's hit. */
	private static ParticleOptions[] materials(LivingEntity e, VividConfig c) {
		String id = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath();
		return switch (id) {
			case "chicken" -> new ParticleOptions[]{item(Items.FEATHER)};
			case "parrot" -> new ParticleOptions[]{item(Items.FEATHER)};
			case "creeper" -> new ParticleOptions[]{item(Items.GUNPOWDER), item(Items.GUNPOWDER), ParticleTypes.SMOKE};
			case "skeleton", "stray", "bogged", "parched", "skeleton_horse" -> new ParticleOptions[]{item(Items.BONE)};
			case "wither_skeleton" -> new ParticleOptions[]{item(Items.BONE), item(Items.COAL)};
			case "zombie", "husk", "drowned", "zombie_villager", "zombie_horse", "zombified_piglin", "zoglin" ->
					new ParticleOptions[]{item(Items.ROTTEN_FLESH)};
			case "spider", "cave_spider" -> new ParticleOptions[]{item(Items.STRING)};
			case "sheep" -> new ParticleOptions[]{item(e instanceof Sheep s ? Items.WOOL.pick(s.getColor()) : Items.STRING)};
			case "cow", "mooshroom", "horse", "donkey", "mule", "llama", "trader_llama" -> new ParticleOptions[]{item(Items.LEATHER)};
			case "pig", "hoglin" -> new ParticleOptions[]{item(Items.PORKCHOP)};
			case "rabbit" -> new ParticleOptions[]{item(Items.RABBIT_HIDE)};
			case "slime" -> new ParticleOptions[]{ParticleTypes.ITEM_SLIME, item(Items.SLIME_BALL)};
			case "magma_cube" -> new ParticleOptions[]{item(Items.MAGMA_CREAM), ParticleTypes.FLAME};
			case "blaze" -> new ParticleOptions[]{item(Items.BLAZE_POWDER), ParticleTypes.FLAME};
			case "enderman" -> new ParticleOptions[]{ParticleTypes.PORTAL, ParticleTypes.PORTAL, item(Items.ENDER_PEARL)};
			case "endermite", "ender_dragon" -> new ParticleOptions[]{ParticleTypes.PORTAL};
			case "iron_golem" -> new ParticleOptions[]{item(Items.IRON_INGOT)};
			case "copper_golem" -> new ParticleOptions[]{item(Items.COPPER_INGOT), ParticleTypes.SCRAPE};
			case "snow_golem" -> new ParticleOptions[]{ParticleTypes.ITEM_SNOWBALL, ParticleTypes.SNOWFLAKE};
			case "ghast", "happy_ghast" -> new ParticleOptions[]{item(Items.GHAST_TEAR), ParticleTypes.WHITE_ASH};
			case "squid" -> new ParticleOptions[]{ParticleTypes.SQUID_INK, item(Items.INK_SAC)};
			case "glow_squid" -> new ParticleOptions[]{ParticleTypes.GLOW_SQUID_INK, item(Items.GLOW_INK_SAC)};
			case "witch" -> new ParticleOptions[]{ParticleTypes.WITCH};
			case "bee" -> new ParticleOptions[]{item(Items.HONEYCOMB), ParticleTypes.FALLING_HONEY};
			case "phantom" -> new ParticleOptions[]{item(Items.PHANTOM_MEMBRANE)};
			case "guardian", "elder_guardian" -> new ParticleOptions[]{item(Items.PRISMARINE_SHARD), ParticleTypes.BUBBLE};
			case "cod" -> new ParticleOptions[]{item(Items.COD), ParticleTypes.SPLASH};
			case "salmon" -> new ParticleOptions[]{item(Items.SALMON), ParticleTypes.SPLASH};
			case "tropical_fish" -> new ParticleOptions[]{item(Items.TROPICAL_FISH), ParticleTypes.SPLASH};
			case "pufferfish" -> new ParticleOptions[]{item(Items.PUFFERFISH), ParticleTypes.SPLASH};
			case "turtle" -> new ParticleOptions[]{item(Items.TURTLE_SCUTE)};
			case "armadillo" -> new ParticleOptions[]{item(Items.ARMADILLO_SCUTE)};
			case "breeze" -> new ParticleOptions[]{item(Items.BREEZE_ROD), ParticleTypes.WHITE_ASH};
			case "shulker" -> new ParticleOptions[]{item(Items.SHULKER_SHELL)};
			case "piglin", "piglin_brute" -> new ParticleOptions[]{item(Items.GOLD_NUGGET)};
			case "villager", "wandering_trader" -> new ParticleOptions[]{ParticleTypes.ANGRY_VILLAGER};
			case "warden" -> new ParticleOptions[]{ParticleTypes.SCULK_SOUL, item(Items.ECHO_SHARD)};
			case "allay", "vex" -> new ParticleOptions[]{ParticleTypes.END_ROD};
			case "creaking" -> new ParticleOptions[]{item(Items.PALE_OAK_LOG), item(Items.RESIN_CLUMP)};
			case "wither" -> new ParticleOptions[]{ParticleTypes.SMOKE, ParticleTypes.SOUL};
			default -> c.genericPuff ? new ParticleOptions[]{ParticleTypes.POOF} : null;
		};
	}

	private HitParticles() {}
}
