package com.vivid.mixin;

import com.vivid.VividConfig;
import com.vivid.VividState;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Deeper blue sky, bigger orange sunsets and brighter stars. */
@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void vivid$sky(ClientLevel level, float partialTicks, Camera camera, SkyRenderState state, CallbackInfo ci) {
		// Remember the vanilla values for clouds / night glow, even if sky tweaks are off
		VividState.night = Math.min(1f, state.starBrightness / 0.5f);
		Vector4fc sunset = state.sunriseAndSunsetColor;
		if (sunset != null) {
			VividState.sunsetR = sunset.x();
			VividState.sunsetG = sunset.y();
			VividState.sunsetB = sunset.z();
			VividState.sunsetA = sunset.w();
		}

		VividConfig c = VividConfig.get();
		if (!c.enabled || !c.skyEnabled) return;

		// ---- sky colour: push away from grey ----
		Vector3fc sky = state.skyColor;
		if (sky != null) {
			float luma = sky.x() * 0.2126f + sky.y() * 0.7152f + sky.z() * 0.0722f;
			float s = (float) c.skySaturation;
			state.skyColor = new Vector3f(
					clamp(luma + (sky.x() - luma) * s),
					clamp(luma + (sky.y() - luma) * s),
					clamp(luma + (sky.z() - luma) * s));
		}

		// ---- sunrise / sunset: more orange, stronger ----
		if (sunset != null && sunset.w() > 0f) {
			float strength = (float) c.sunsetStrength;
			float orange = Math.min(1f, (strength - 1f) * 0.5f);
			float r = sunset.x();
			float g = sunset.y() * (1f - 0.25f * Math.max(0f, orange));
			float b = sunset.z() * (1f - 0.5f * Math.max(0f, orange));
			state.sunriseAndSunsetColor = new Vector4f(clamp(r), clamp(g), clamp(b), clamp(sunset.w() * strength));
		}

		// ---- stars ----
		state.starBrightness = clamp(state.starBrightness * (float) c.starBrightness);
	}

	@org.spongepowered.asm.mixin.Unique
	private static float clamp(float v) {
		return Math.max(0f, Math.min(1f, v));
	}
}
