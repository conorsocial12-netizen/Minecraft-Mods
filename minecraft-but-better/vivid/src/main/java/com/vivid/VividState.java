package com.vivid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;

/** Values that change every frame (hurt flash, underwater, time of day...), fed to the shader. */
public final class VividState {
	private static final long START = System.nanoTime();
	private static long lastNanos = System.nanoTime();

	public static float time;
	public static float hurt;
	public static float underwater;
	public static float rain;

	// Mood tint (dimension colour grading)
	public static float moodR = 1, moodG = 1, moodB = 1, moodA = 0;

	// Written by SkyRendererMixin, read by clouds and the night glow
	public static float night;              // 0 = day, 1 = full night
	public static float sunsetR = 1, sunsetG = 0.6f, sunsetB = 0.3f, sunsetA = 0;

	public static void update(Minecraft mc) {
		long now = System.nanoTime();
		float dt = Math.min(0.1f, (now - lastNanos) / 1.0e9f);
		lastNanos = now;
		time = ((now - START) / 1.0e9f) % 3600f;

		VividConfig c = VividConfig.get();
		LocalPlayer player = mc.player;
		ClientLevel level = mc.level;

		// ---- hurt flash + low-health pulse ----
		float targetHurt = 0;
		if (player != null && !player.isCreative() && !player.isSpectator()) {
			if (player.hurtTime > 0) targetHurt = player.hurtTime / 10f;
			if (player.getHealth() <= 6f && player.getHealth() > 0f) {
				float pulse = 0.35f + 0.2f * (float) Math.sin(time * 4.0);
				targetHurt = Math.max(targetHurt, pulse * (1f - player.getHealth() / 7f));
			}
		}
		hurt += (targetHurt - hurt) * Math.min(1f, dt * 12f);

		// ---- underwater ----
		boolean inWater = mc.gameRenderer != null
				&& mc.gameRenderer.mainCamera().getFluidInCamera() == FogType.WATER;
		underwater += ((inWater ? 1f : 0f) - underwater) * Math.min(1f, dt * 6f);

		// ---- weather & dimension mood ----
		float tr = 1, tg = 1, tb = 1, ta = 0;
		rain = 0;
		if (level != null) {
			rain = level.getRainLevel(1f);
			if (c.dimensionMoods) {
				if (level.dimension() == Level.NETHER) {
					tr = 1.18f; tg = 0.86f; tb = 0.72f; ta = (float) c.netherMood;
				} else if (level.dimension() == Level.END) {
					tr = 0.96f; tg = 0.8f; tb = 1.22f; ta = (float) c.endMood;
				}
			}
		}
		float k = Math.min(1f, dt * 3f);
		moodR += (tr - moodR) * k;
		moodG += (tg - moodG) * k;
		moodB += (tb - moodB) * k;
		moodA += (ta - moodA) * k;
	}

	private VividState() {}
}
