package com.vivid.mixin;

import com.vivid.VividConfig;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Stronger, warmer torch and lantern light. */
@Mixin(LightmapRenderStateExtractor.class)
public abstract class LightmapExtractorMixin {
	@Inject(method = "extract", at = @At("TAIL"))
	private void vivid$light(LightmapRenderState state, float partialTicks, CallbackInfo ci) {
		if (!state.needsUpdate) return;
		VividConfig c = VividConfig.get();
		if (!c.enabled || !c.lightEnabled) return;

		state.blockFactor *= (float) c.torchGlow;

		float warm = (float) c.warmLight;
		Vector3fc tint = state.blockLightTint;
		if (tint != null && warm > 0f) {
			state.blockLightTint = new Vector3f(
					vivid$clamp(tint.x() * (1f + 0.06f * warm)),
					vivid$clamp(tint.y() * (1f - 0.08f * warm)),
					vivid$clamp(tint.z() * (1f - 0.28f * warm)));
		}
	}

	@Unique
	private static float vivid$clamp(float v) {
		return Math.max(0f, Math.min(1.5f, v));
	}
}
