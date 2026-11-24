package com.vivid.mixin;

import com.vivid.VividConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Light haze at the edge of the world, richer fog colour, and clear tropical water. */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
	@Inject(method = "setupFog", at = @At("RETURN"))
	private void vivid$fog(Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount,
			ClientLevel level, CallbackInfoReturnable<FogData> cir) {
		VividConfig c = VividConfig.get();
		if (!c.enabled) return;
		FogData fog = cir.getReturnValue();
		if (fog == null) return;

		FogType type = camera.getFluidInCamera();
		Vector4f col = fog.color;

		if (type == FogType.NONE || type == FogType.ATMOSPHERIC) {
			if (!c.fogEnabled) return;
			// Haze: starts at hazeStart of the render distance and fades smoothly to the edge
			float end = fog.renderDistanceEnd;
			fog.renderDistanceStart = Math.min(fog.renderDistanceStart, end * (float) c.hazeStart);
			// Biome fog (swamps, rain...) pushed further away
			float mult = (float) c.biomeFogDistance;
			if (fog.environmentalEnd < Float.MAX_VALUE / 4f) {
				fog.environmentalStart *= mult;
				fog.environmentalEnd *= mult;
			}
			// Richer colour
			saturate(col, (float) c.fogSaturation);
		} else if (type == FogType.WATER) {
			if (!c.waterEnabled) return;
			fog.environmentalEnd *= (float) c.waterClarity;
			fog.environmentalStart *= (float) c.waterClarity;
			fog.skyEnd = fog.environmentalEnd;
			fog.cloudEnd = fog.environmentalEnd;
			int w = VividConfig.parseHex(c.waterColor, 0x1ED6E6);
			float wr = ((w >> 16) & 0xFF) / 255f, wg = ((w >> 8) & 0xFF) / 255f, wb = (w & 0xFF) / 255f;
			// Keep it dark in deep/night water: scale the target by how bright vanilla water fog is
			float bright = Math.max(col.x, Math.max(col.y, col.z));
			float scale = Math.max(0.2f, Math.min(1f, bright / 0.45f));
			float t = 0.7f;
			col.set(col.x + (wr * scale - col.x) * t, col.y + (wg * scale - col.y) * t, col.z + (wb * scale - col.z) * t, col.w);
		}
	}

	@org.spongepowered.asm.mixin.Unique
	private static void saturate(Vector4f col, float s) {
		float luma = col.x * 0.2126f + col.y * 0.7152f + col.z * 0.0722f;
		col.set(clamp(luma + (col.x - luma) * s), clamp(luma + (col.y - luma) * s), clamp(luma + (col.z - luma) * s), col.w);
	}

	@org.spongepowered.asm.mixin.Unique
	private static float clamp(float v) {
		return Math.max(0f, Math.min(1f, v));
	}
}
