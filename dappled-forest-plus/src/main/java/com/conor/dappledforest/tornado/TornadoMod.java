package com.conor.dappledforest.tornado;

import java.util.HashMap;
import java.util.Map;

import com.conor.dappledforest.DappledForestPlus;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Leaf Tornadoes and Windy Days.
 * <p>
 * Every so often (on average about once every three in-game days) the Overworld gets a windy spell lasting
 * 2-4 minutes. While it's windy, leaf tornadoes form around any player who is in a Dappled Forest
 * (up to {@link #MAX_NEARBY} near each player). Ops can start or stop the wind with {@code /leafwind start|stop}.
 */
public class TornadoMod implements ModInitializer {
	private static final ResourceKey<EntityType<?>> TORNADO_KEY =
			ResourceKey.create(Registries.ENTITY_TYPE, DappledForestPlus.id("leaf_tornado"));

	public static final EntityType<LeafTornado> LEAF_TORNADO = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			TORNADO_KEY,
			EntityType.Builder.<LeafTornado>of(LeafTornado::new, MobCategory.MISC)
					.noLootTable()
					.sized(LeafTornado.RADIUS * 2, LeafTornado.HEIGHT)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build(TORNADO_KEY));

	/** Chance per tick of a windy spell starting: about 1 in 3 per in-game day (24000 ticks). */
	private static final float WIND_CHANCE_PER_TICK = 1.0F / 72000.0F;
	private static final int MAX_NEARBY = 2;

	/** Ticks of wind left, per world. 0 = calm. */
	private static final Map<ResourceKey<Level>, Integer> windTicks = new HashMap<>();

	@Override
	public void onInitialize() {
		ServerTickEvents.END_LEVEL_TICK.register(TornadoMod::tickWorld);

		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> dispatcher.register(
				Commands.literal("leafwind")
						.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.then(Commands.literal("start").executes(ctx -> {
							startWind(ctx.getSource().getLevel());
							ctx.getSource().sendSuccess(() -> Component.literal("The wind picks up in the Dappled Forest."), true);
							return 1;
						}))
						.then(Commands.literal("stop").executes(ctx -> {
							stopWind(ctx.getSource().getLevel());
							ctx.getSource().sendSuccess(() -> Component.literal("The wind dies down."), true);
							return 1;
						}))));
	}

	public static boolean isWindy(ServerLevel level) {
		return windTicks.getOrDefault(level.dimension(), 0) > 0;
	}

	public static void startWind(ServerLevel level) {
		windTicks.put(level.dimension(), 2400 + level.getRandom().nextInt(2400)); // 2-4 minutes
		for (ServerPlayer player : level.players()) {
			if (inDappledForest(level, player.blockPosition())) {
				player.sendOverlayMessage(Component.literal("The wind is picking up...").withStyle(ChatFormatting.GOLD));
				level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BREEZE_IDLE_AIR, SoundSource.WEATHER, 1.0F, 0.5F);
			}
		}
	}

	public static void stopWind(ServerLevel level) {
		if (windTicks.getOrDefault(level.dimension(), 0) <= 0) return;
		windTicks.put(level.dimension(), 0);
		for (ServerPlayer player : level.players()) {
			if (inDappledForest(level, player.blockPosition())) {
				player.sendOverlayMessage(Component.literal("The wind dies down.").withStyle(ChatFormatting.GRAY));
			}
		}
	}

	private static boolean inDappledForest(ServerLevel level, BlockPos pos) {
		return level.getBiome(pos).is(Biomes.DAPPLED_FOREST);
	}

	private static void tickWorld(ServerLevel level) {
		if (level.dimension() != Level.OVERWORLD) return;

		int ticks = windTicks.getOrDefault(level.dimension(), 0);
		if (ticks <= 0) {
			if (level.getRandom().nextFloat() < WIND_CHANCE_PER_TICK) startWind(level);
			return;
		}
		if (ticks == 1) {
			stopWind(level);
			return;
		}
		windTicks.put(level.dimension(), ticks - 1);

		// Every 5 seconds, top up the tornadoes around each player in a Dappled Forest
		if (level.getGameTime() % 100 != 0) return;
		for (ServerPlayer player : level.players()) {
			if (player.isSpectator() || !inDappledForest(level, player.blockPosition())) continue;
			int nearby = level.getEntities(LEAF_TORNADO, player.getBoundingBox().inflate(48.0), e -> true).size();
			if (nearby < MAX_NEARBY) trySpawnNear(level, player);
		}
	}

	private static void trySpawnNear(ServerLevel level, ServerPlayer player) {
		var random = level.getRandom();
		for (int attempt = 0; attempt < 8; attempt++) {
			float angle = random.nextFloat() * Mth.TWO_PI;
			float dist = 14 + random.nextFloat() * 18; // 14-32 blocks away
			int x = Mth.floor(player.getX() + Mth.cos(angle) * dist);
			int z = Mth.floor(player.getZ() + Mth.sin(angle) * dist);
			int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
			BlockPos pos = new BlockPos(x, y, z);
			if (!inDappledForest(level, pos) || !level.getBlockState(pos.below()).isSolid()) continue;
			LeafTornado tornado = LEAF_TORNADO.create(level, EntitySpawnReason.EVENT);
			if (tornado == null) return;
			tornado.setPos(x + 0.5, y, z + 0.5);
			level.addFreshEntity(tornado);
			return;
		}
	}
}
