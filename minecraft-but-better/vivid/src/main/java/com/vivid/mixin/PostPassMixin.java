package com.vivid.mixin;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.vivid.VividConfig;
import com.vivid.VividState;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Vanilla post-effect uniforms are fixed numbers from the JSON file. For Vivid's own pass we swap
 * the "VividConfig" uniform buffer for a ring of mappable buffers and rewrite it every frame
 * from the current settings, so every slider updates live.
 */
@Mixin(PostPass.class)
public abstract class PostPassMixin {
	@Unique private static final String VIVID_BLOCK = "VividConfig";

	@Shadow @Final private String name;
	@Shadow @Final private Map<String, GpuBuffer> customUniforms;

	@Unique private MappableRingBuffer vivid$ring;

	@Inject(method = "addToFrame", at = @At("HEAD"))
	private void vivid$writeUniforms(FrameGraphBuilder frame, Map<Identifier, ResourceHandle<RenderTarget>> targets,
			GpuBufferSlice shaderOrthoMatrix, CallbackInfo ci) {
		if (!this.name.startsWith("vivid:")) return;
		GpuBuffer original = this.customUniforms.get(VIVID_BLOCK);
		if (original == null) return;

		if (this.vivid$ring == null) {
			int size = (int) original.size();
			this.vivid$ring = new MappableRingBuffer(() -> "Vivid config", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE, size);
			original.close();
		} else {
			this.vivid$ring.rotate();
		}

		GpuBuffer buffer = this.vivid$ring.currentBuffer();
		try (GpuBufferSlice.MappedView view = buffer.map(false, true)) {
			vivid$fill(Std140Builder.intoBuffer(view.data()));
		}
		this.customUniforms.put(VIVID_BLOCK, buffer);
	}

	@Inject(method = "close", at = @At("HEAD"))
	private void vivid$close(CallbackInfo ci) {
		if (this.vivid$ring != null) {
			this.customUniforms.remove(VIVID_BLOCK);
			this.vivid$ring.close();
			this.vivid$ring = null;
		}
	}

	@Unique
	private static void vivid$fill(Std140Builder b) {
		VividConfig c = VividConfig.get();
		boolean grade = c.gradingEnabled;

		float night = c.lightEnabled ? VividState.night * (float) c.nightGlow : 0f;
		float rainLift = 1f + VividState.rain * (float) c.weatherBrightness * 0.18f;

		float saturation = grade ? (float) c.saturation : 1f;
		float vibrance = grade ? (float) c.vibrance : 0f;
		float contrast = grade ? (float) c.contrast : 1f;
		float brightness = (grade ? (float) c.brightness : 1f) * rainLift;
		float vignette = grade ? (float) c.vignette : 0f;
		float bloom = (grade ? (float) c.bloom : 0f) + night * 0.6f;
		float threshold = (float) Math.max(0.3, c.bloomThreshold - night * 0.15);
		float sharpen = grade ? (float) c.sharpen : 0f;

		float wobble = c.waterEnabled ? VividState.underwater * (float) c.underwaterWobble : 0f;
		float waterTint = c.waterEnabled ? VividState.underwater * (float) c.underwaterTint : 0f;
		int water = VividConfig.parseHex(c.waterColor, 0x1ED6E6);

		b.putVec4(saturation, vibrance, contrast, brightness);
		b.putVec4(vignette, bloom, sharpen, threshold);
		b.putVec4(VividState.hurt * (float) c.hurtVignette, wobble, VividState.time, 0f);
		b.putVec4(VividState.moodR, VividState.moodG, VividState.moodB, VividState.moodA);
		b.putVec4(((water >> 16) & 0xFF) / 255f, ((water >> 8) & 0xFF) / 255f, (water & 0xFF) / 255f, waterTint);
	}
}
