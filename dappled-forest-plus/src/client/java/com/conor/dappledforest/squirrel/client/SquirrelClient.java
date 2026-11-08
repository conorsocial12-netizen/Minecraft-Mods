package com.conor.dappledforest.squirrel.client;

import com.conor.dappledforest.DappledForestPlus;
import com.conor.dappledforest.squirrel.SquirrelMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class SquirrelClient implements ClientModInitializer {
	public static final ModelLayerLocation SQUIRREL_LAYER =
			new ModelLayerLocation(DappledForestPlus.id("acorn_squirrel"), "main");

	@Override
	public void onInitializeClient() {
		ModelLayerRegistry.registerModelLayer(SQUIRREL_LAYER, SquirrelModel::createBodyLayer);
		EntityRendererRegistry.register(SquirrelMod.ACORN_SQUIRREL, SquirrelRenderer::new);
	}
}
