package com.conor.dappledforest.tornado.client;

import com.conor.dappledforest.tornado.TornadoMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class TornadoClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(TornadoMod.LEAF_TORNADO, LeafTornadoRenderer::new);
	}
}
