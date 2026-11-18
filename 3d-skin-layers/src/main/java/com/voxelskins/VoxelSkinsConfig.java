package com.voxelskins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Settings, saved in config/voxelskins.json. */
public class VoxelSkinsConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("voxelskins.json");
	private static VoxelSkinsConfig instance = new VoxelSkinsConfig();

	public boolean enabled = true;
	public boolean hat = true;
	public boolean jacket = true;
	public boolean sleeves = true;
	public boolean pants = true;
	/** Show 3D layers on yourself (F5 / inventory). */
	public boolean self = true;
	/** Go back to the flat layer where armor is worn, so pixels don't poke through it. */
	public boolean flatUnderArmor = true;
	/** Beyond this many blocks the normal flat layer is used (it looks the same from far away). 0 = always 3D. */
	public double renderDistance = 16;

	public static VoxelSkinsConfig get() {
		return instance;
	}

	public static void load() {
		try {
			if (Files.exists(PATH)) {
				VoxelSkinsConfig c = GSON.fromJson(Files.readString(PATH), VoxelSkinsConfig.class);
				if (c != null) instance = c;
			}
		} catch (Exception e) {
			instance = new VoxelSkinsConfig();
		}
		save();
	}

	public static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			Files.writeString(PATH, GSON.toJson(instance));
		} catch (Exception ignored) {
		}
	}

	public static void reset() {
		instance = new VoxelSkinsConfig();
		save();
	}
}
