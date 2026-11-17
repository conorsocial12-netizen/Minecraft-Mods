package com.skinglow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Config stored in config/skinglow.json. The file is re-read automatically
 * (checked once per second) whenever it changes, so you can edit it while the
 * game is running and see the result immediately.
 */
public class SkinGlowConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("skinglow.json");

	private static SkinGlowConfig instance = new SkinGlowConfig();
	private static long lastModified = -1;
	private static long lastCheckMs = 0;

	public enum ColorMode { SOLID, RAINBOW, GRADIENT }

	// ---- master ----
	public boolean enabled = true;

	// ---- who gets an outline ----
	public boolean players = true;          // other players
	public boolean self = true;             // you (visible in F5 / third person)
	public boolean hostileMobs = false;
	public boolean passiveMobs = false;
	public boolean otherEntities = false;   // items, armor stands, boats, ...
	public boolean includeInvisible = false;
	public double maxDistance = 0;          // blocks, 0 = unlimited

	// ---- color ----
	public ColorMode colorMode = ColorMode.SOLID;
	public String color = "#FFFFFF";        // SOLID color / GRADIENT start
	public String color2 = "#FF00E5";       // GRADIENT end
	public double gradientSpeed = 0.5;      // cycles per second
	public double rainbowSpeed = 0.25;      // cycles per second
	public double rainbowSaturation = 1.0;  // 0..1
	public boolean rainbowOffsetPerEntity = true; // each entity a different hue
	public double brightness = 0.6;         // 0..1 intensity; lower = more subtle

	// ---- optional per-category color (empty = use main color settings) ----
	public String playerColor = "";
	public String selfColor = "";
	public String hostileColor = "";
	public String passiveColor = "";

	// ---- pulse ----
	public boolean pulse = false;
	public double pulseSpeed = 1.0;         // pulses per second
	public double pulseMinBrightness = 0.35; // dimmest point, 0..1

	// ---- misc ----
	public boolean respectTeamColors = false; // keep vanilla team colors where a team exists
	public boolean swapRedBlue = false;       // flip if red and blue look swapped in game

	public static SkinGlowConfig get() {
		long now = System.currentTimeMillis();
		if (now - lastCheckMs > 1000) {
			lastCheckMs = now;
			try {
				if (Files.exists(PATH)) {
					long mod = Files.getLastModifiedTime(PATH).toMillis();
					if (mod != lastModified) load();
				}
			} catch (IOException ignored) {
			}
		}
		return instance;
	}

	public static void load() {
		try {
			if (Files.exists(PATH)) {
				SkinGlowConfig loaded = GSON.fromJson(Files.readString(PATH), SkinGlowConfig.class);
				if (loaded != null) instance = loaded;
				lastModified = Files.getLastModifiedTime(PATH).toMillis();
			} else {
				instance = new SkinGlowConfig();
				save();
			}
			instance.sanitize();
		} catch (Exception e) {
			// Bad JSON: keep the last good config, don't crash the game.
			System.err.println("[skinglow] Could not read config, keeping previous settings: " + e);
			try {
				lastModified = Files.getLastModifiedTime(PATH).toMillis();
			} catch (IOException ignored) {
			}
		}
	}

	public static void reset() {
		instance = new SkinGlowConfig();
		save();
	}

	public static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			Files.writeString(PATH, GSON.toJson(instance));
			lastModified = Files.getLastModifiedTime(PATH).toMillis();
		} catch (IOException e) {
			System.err.println("[skinglow] Could not save config: " + e);
		}
	}

	private void sanitize() {
		if (colorMode == null) colorMode = ColorMode.SOLID;
		brightness = clamp(brightness, 0, 1);
		rainbowSaturation = clamp(rainbowSaturation, 0, 1);
		pulseMinBrightness = clamp(pulseMinBrightness, 0, 1);
		if (maxDistance < 0) maxDistance = 0;
	}

	static double clamp(double v, double lo, double hi) {
		return Math.max(lo, Math.min(hi, v));
	}
}
