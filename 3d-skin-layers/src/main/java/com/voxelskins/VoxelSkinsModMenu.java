package com.voxelskins;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** Adds the "Configure" button in Mod Menu. Only loaded when Mod Menu is installed. */
public class VoxelSkinsModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return VoxelSkinsScreen::new;
	}
}
