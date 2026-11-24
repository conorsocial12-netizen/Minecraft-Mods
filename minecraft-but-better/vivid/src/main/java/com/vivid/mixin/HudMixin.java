package com.vivid.mixin;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.vivid.VividConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** The hotbar selector glides between slots and can be tinted with your accent colour. */
@Mixin(Hud.class)
public abstract class HudMixin {
	@Unique private static float vivid$animX = Float.NaN;
	@Unique private static long vivid$lastNanos = 0L;

	// 2nd blitSprite(pipeline, sprite, x, y, w, h) call in extractItemHotbar = the selection box
	@Redirect(method = "extractItemHotbar", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
			ordinal = 1))
	private void vivid$selection(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier sprite,
			int x, int y, int width, int height) {
		VividConfig c = VividConfig.get();
		boolean active = c.enabled;

		long now = System.nanoTime();
		float dt = vivid$lastNanos == 0L ? 0f : Math.min(0.1f, (now - vivid$lastNanos) / 1.0e9f);
		vivid$lastNanos = now;

		float drawX = x;
		if (active && c.smoothHotbar) {
			if (Float.isNaN(vivid$animX) || Math.abs(vivid$animX - x) > 400f) vivid$animX = x;
			float k = 1f - (float) Math.exp(-c.hotbarSpeed * dt);
			vivid$animX += (x - vivid$animX) * k;
			if (Math.abs(vivid$animX - x) < 0.05f) vivid$animX = x;
			drawX = vivid$animX;
		} else {
			vivid$animX = x;
		}

		int color = 0xFFFFFFFF;
		if (active && c.accentHotbar) {
			color = 0xFF000000 | VividConfig.parseHex(c.accentColor, 0x4FE0FF);
		}

		graphics.pose().pushMatrix();
		graphics.pose().translate(drawX - x, 0f);
		graphics.blitSprite(pipeline, sprite, x, y, width, height, color);
		graphics.pose().popMatrix();
	}
}
