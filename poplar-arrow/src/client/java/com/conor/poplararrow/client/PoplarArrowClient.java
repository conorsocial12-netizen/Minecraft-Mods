package com.conor.poplararrow.client;

import com.conor.poplararrow.ModEntities;
import com.conor.poplararrow.PoplarArrowMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

public class PoplarArrowClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.POPLAR_ARROW, PoplarArrowRenderer::new);

		// Drawn with the pumpkin/powder-snow overlays, so the hotbar & chat stay readable on top
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.MISC_OVERLAYS,
				PoplarArrowMod.id("leaf_overlay"),
				LeafOverlay::render);
	}
}
