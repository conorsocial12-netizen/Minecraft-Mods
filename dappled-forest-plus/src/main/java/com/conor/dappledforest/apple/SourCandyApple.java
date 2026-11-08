package com.conor.dappledforest.apple;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourCandyApple implements ModInitializer {
	public static final String MOD_ID = "dappledforest";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.init();
		ModLoot.init();
		LOGGER.info("Sour Candy Apples are hiding in the Dappled Forest...");
	}
}
