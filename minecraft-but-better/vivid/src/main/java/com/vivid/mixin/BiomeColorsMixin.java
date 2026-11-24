package com.vivid.mixin;

import com.vivid.VividConfig;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Blends the water block colour towards your chosen water colour (tropical blue by default).
 * Water already on screen updates when chunks are rebuilt (F3+A, or the "Apply" button in the menu).
 */
@Mixin(BiomeColors.class)
public abstract class BiomeColorsMixin {
	@Inject(method = "getAverageWaterColor", at = @At("RETURN"), cancellable = true)
	private static void vivid$water(BlockAndTintGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		VividConfig c = VividConfig.get();
		if (!c.enabled || !c.waterEnabled || c.waterSurfaceBlend <= 0) return;
		int base = cir.getReturnValueI();
		int target = VividConfig.parseHex(c.waterColor, 0x1ED6E6);
		float t = (float) Math.min(1.0, c.waterSurfaceBlend);
		int r = mix((base >> 16) & 0xFF, (target >> 16) & 0xFF, t);
		int g = mix((base >> 8) & 0xFF, (target >> 8) & 0xFF, t);
		int b = mix(base & 0xFF, target & 0xFF, t);
		cir.setReturnValue((base & 0xFF000000) | (r << 16) | (g << 8) | b);
	}

	@org.spongepowered.asm.mixin.Unique
	private static int mix(int a, int b, float t) {
		return Math.round(a + (b - a) * t);
	}
}
