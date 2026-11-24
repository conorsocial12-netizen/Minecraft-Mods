package com.vivid.mixin;

import com.vivid.VividConfig;
import com.vivid.VividState;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Clouds pick up the sunrise/sunset colour, and get a cool blue tint at night. */
@Mixin(CloudRenderer.class)
public abstract class CloudRendererMixin {
	@ModifyVariable(method = "prepare(ILnet/minecraft/client/CloudStatus;FILnet/minecraft/world/phys/Vec3;JF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private int vivid$tintClouds(int color) {
		VividConfig c = VividConfig.get();
		if (!c.enabled || !c.skyEnabled || c.cloudTint <= 0) return color;

		float r = ARGB.red(color) / 255f;
		float g = ARGB.green(color) / 255f;
		float b = ARGB.blue(color) / 255f;

		// Sunset: blend towards the (boosted) sunset colour, keeping the cloud's brightness
		float t = (float) Math.min(1.0, VividState.sunsetA * c.cloudTint * 0.8);
		if (t > 0f) {
			float lum = Math.max(r, Math.max(g, b));
			r += (VividState.sunsetR * lum * 1.15f - r) * t;
			g += (VividState.sunsetG * lum * 1.0f - g) * t;
			b += (VividState.sunsetB * lum * 0.9f - b) * t;
		}

		// Night: slight blue
		float n = VividState.night * (float) c.cloudTint * 0.25f;
		r *= 1f - n * 0.3f;
		g *= 1f - n * 0.15f;

		return ARGB.color(ARGB.alpha(color), to255(r), to255(g), to255(b));
	}

	@org.spongepowered.asm.mixin.Unique
	private static int to255(float v) {
		return Math.max(0, Math.min(255, Math.round(v * 255f)));
	}
}
