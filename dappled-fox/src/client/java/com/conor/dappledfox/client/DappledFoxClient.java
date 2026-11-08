package com.conor.dappledfox.client;

import com.conor.dappledfox.DappledFoxMod;
import com.conor.dappledfox.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DappledFoxClient implements ClientModInitializer {
	public static final ModelLayerLocation DAPPLED_FOX_LAYER =
			new ModelLayerLocation(DappledFoxMod.id("dappled_fox"), "main");

	@Override
	public void onInitializeClient() {
		ModelLayerRegistry.registerModelLayer(DAPPLED_FOX_LAYER, DappledFoxModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.DAPPLED_FOX, DappledFoxRenderer::new);
	}
}
