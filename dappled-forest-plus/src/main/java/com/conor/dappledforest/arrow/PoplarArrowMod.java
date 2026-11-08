package com.conor.dappledforest.arrow;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class PoplarArrowMod implements ModInitializer {
	public static final String MOD_ID = "dappledforest";

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModEffects.init();
		ModEntities.init();
		ModItems.init();
	}
}
