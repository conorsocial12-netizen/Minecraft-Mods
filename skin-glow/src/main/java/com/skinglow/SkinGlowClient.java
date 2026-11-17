package com.skinglow;

import net.fabricmc.api.ClientModInitializer;

public class SkinGlowClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SkinGlowConfig.load();
	}
}
