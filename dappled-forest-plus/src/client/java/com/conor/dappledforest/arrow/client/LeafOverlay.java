package com.conor.dappledforest.arrow.client;

import com.conor.dappledforest.arrow.ModEffects;
import com.conor.dappledforest.arrow.PoplarArrowMod;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

/**
 * Draws a pile of red / orange / yellow poplar leaves around the edges of the screen while the
 * player has the Leaf Blind effect. More hits = more layers = leaves creep further into the middle.
 * The leaves sway gently and fade out over the last 1.5 seconds of the effect.
 */
public final class LeafOverlay {
	private static final Identifier[] LEAF_TEXTURES = {
			PoplarArrowMod.id("textures/gui/overlay/leaf_0.png"),
			PoplarArrowMod.id("textures/gui/overlay/leaf_1.png"),
			PoplarArrowMod.id("textures/gui/overlay/leaf_2.png"),
	};
	private static final int LEAF_TEX_SIZE = 64;

	// Poplar leaf tints: red, orange, yellow (+ a couple of in-between shades)
	private static final int[] TINTS = {0xC8352A, 0xE0782A, 0xF2B632, 0xD95A27, 0xB8452E};

	private static final int BASE_LEAVES = 110;
	private static final int LEAVES_PER_LAYER = 70;
	private static final float FADE_TICKS = 30f;

	public static void render(GuiGraphicsExtractor graphics, DeltaTracker delta) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) return;

		MobEffectInstance effect = player.getEffect(ModEffects.LEAF_BLIND);
		if (effect == null) return;

		float partial = delta.getGameTimeDeltaPartialTick(false);
		float time = (player.tickCount + partial) / 20f;
		int layer = Mth.clamp(effect.getAmplifier(), 0, 2);
		float fade = Mth.clamp((effect.getDuration() - partial) / FADE_TICKS, 0f, 1f);
		if (fade <= 0f) return;

		int width = graphics.guiWidth();
		int height = graphics.guiHeight();
		int minSide = Math.min(width, height);

		// How far in from the edge leaves can reach: ~24% of the screen at 1 layer, ~46% at 3.
		float reach = minSide * (0.24f + 0.11f * layer);
		int count = BASE_LEAVES + LEAVES_PER_LAYER * layer;

		// Fixed seed so leaves don't jump around between frames
		RandomSource rand = RandomSource.create(0x9091A2L + layer);

		for (int i = 0; i < count; i++) {
			// Pick an edge, then a depth biased strongly towards the edge (squared)
			int side = rand.nextInt(4);
			float along = rand.nextFloat();
			float depth = rand.nextFloat();
			depth = depth * depth * reach;

			float x, y;
			switch (side) {
				case 0 -> { x = along * width; y = depth; }               // top
				case 1 -> { x = along * width; y = height - depth; }      // bottom
				case 2 -> { x = depth; y = along * height; }              // left
				default -> { x = width - depth; y = along * height; }     // right
			}

			// Leaves at the very edge are bigger; ones reaching inward are smaller
			float edgeness = 1f - depth / reach;
			int size = (int) (minSide * (0.09f + 0.11f * edgeness) * (0.75f + rand.nextFloat() * 0.5f));

			float baseAngle = rand.nextFloat() * Mth.TWO_PI;
			float swaySpeed = 0.8f + rand.nextFloat() * 0.8f;
			float swayPhase = rand.nextFloat() * Mth.TWO_PI;
			float angle = baseAngle + Mth.sin(time * swaySpeed + swayPhase) * 0.12f;
			float driftX = Mth.sin(time * swaySpeed * 0.7f + swayPhase) * 1.5f;

			Identifier tex = LEAF_TEXTURES[rand.nextInt(LEAF_TEXTURES.length)];
			int tint = TINTS[rand.nextInt(TINTS.length)];
			// Edge leaves are nearly opaque, inner ones slightly see-through
			int alpha = (int) (255 * fade * (0.80f + 0.20f * edgeness));
			int color = ARGB.color(alpha, tint);

			graphics.pose().pushMatrix();
			graphics.pose().translate(x + driftX, y);
			graphics.pose().rotate(angle);
			graphics.blit(RenderPipelines.GUI_TEXTURED, tex,
					-size / 2, -size / 2,
					0f, 0f,
					size, size,
					size, size,
					color);
			graphics.pose().popMatrix();
		}
	}

	private LeafOverlay() {}
}
