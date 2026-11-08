package com.conor.poplararrow;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class PoplarArrowMod implements ModInitializer {
	public static final String MOD_ID = "poplararrow";

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
