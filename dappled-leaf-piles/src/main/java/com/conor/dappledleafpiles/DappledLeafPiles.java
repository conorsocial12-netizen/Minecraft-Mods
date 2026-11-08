package com.conor.dappledleafpiles;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DappledLeafPiles implements ModInitializer {
	public static final String MOD_ID = "dappledleafpiles";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModBlocks.init();
		ModWorldgen.init();
		LOGGER.info("Autumn leaves are piling up in the Dappled Forest");
	}
}
