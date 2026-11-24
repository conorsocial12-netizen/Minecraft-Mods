package com.vivid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * All Vivid settings, saved in config/vivid.json. Defaults are the "Vibrant" preset.
 * The file is re-read automatically (checked once a second) when it changes on disk.
 */
public class VividConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("vivid.json");

	private static VividConfig instance = new VividConfig();
	private static long lastModified = -1;
	private static long lastCheckMs = 0;

	// ---- master ----
	public boolean enabled = true;
	public String preset = "Vibrant";

	// ---- colour grading (screen-wide) ----
	public boolean gradingEnabled = true;
	public double saturation = 1.25;
	public double vibrance = 0.35;
	public double contrast = 1.08;
	public double brightness = 1.04;
	public double vignette = 0.22;
	public double bloom = 0.35;
	public double bloomThreshold = 0.72;
	public double sharpen = 0.25;

	// ---- sky ----
	public boolean skyEnabled = true;
	public double skySaturation = 1.35;   // deeper blue day sky
	public double sunsetStrength = 1.6;   // bigger, more orange sunsets
	public double starBrightness = 2.0;   // multiplier
	public double cloudTint = 0.6;        // clouds pick up sunset colours

	// ---- fog & atmosphere ----
	public boolean fogEnabled = true;
	public double hazeStart = 0.72;       // fraction of render distance where haze begins
	public double biomeFogDistance = 1.4; // pushes biome fog (swamps etc.) further away
	public double fogSaturation = 1.2;
	public double weatherBrightness = 0.6; // 0 = vanilla gloomy rain, 1 = rain stays bright

	// ---- water ----
	public boolean waterEnabled = true;
	public String waterColor = "#1ED6E6";  // tropical clear blue
	public double waterSurfaceBlend = 0.6; // how much water blocks take this colour
	public double waterClarity = 2.2;      // see further underwater
	public double underwaterTint = 0.25;
	public double underwaterWobble = 0.5;

	// ---- lighting ----
	public boolean lightEnabled = true;
	public double torchGlow = 1.15;   // block light strength
	public double warmLight = 0.35;   // warmer torch/lantern colour
	public double nightGlow = 0.5;    // extra bloom at night so lights glow

	// ---- dynamic effects ----
	public double hurtVignette = 0.7;
	public boolean dimensionMoods = true;
	public double netherMood = 0.3;
	public double endMood = 0.3;

	// ---- combat particles ----
	public boolean particlesEnabled = true;
	public boolean hitParticles = true;    // feathers from chickens, gunpowder from creepers...
	public double hitAmount = 1.0;
	public boolean ownHitsOnly = false;    // only when you did the damage
	public boolean deathBurst = true;      // big burst when a mob dies
	public boolean fancyCrits = true;      // ring + sparkles on critical hits
	public double critAmount = 1.0;
	public boolean impactSparks = true;    // sparks where your hit lands
	public boolean genericPuff = false;    // puff of smoke for mobs without their own particles

	// ---- HUD ----
	public boolean smoothHotbar = true;
	public double hotbarSpeed = 18;
	public boolean accentHotbar = true;
	public String accentColor = "#4FE0FF";

	public static VividConfig get() {
		long now = System.currentTimeMillis();
		if (now - lastCheckMs > 1000) {
			lastCheckMs = now;
			try {
				if (Files.exists(PATH) && Files.getLastModifiedTime(PATH).toMillis() != lastModified) load();
			} catch (IOException ignored) {
			}
		}
		return instance;
	}

	public static void load() {
		try {
			if (Files.exists(PATH)) {
				VividConfig loaded = GSON.fromJson(Files.readString(PATH), VividConfig.class);
				if (loaded != null) instance = loaded;
				lastModified = Files.getLastModifiedTime(PATH).toMillis();
			} else {
				instance = new VividConfig();
				save();
			}
			instance.sanitize();
		} catch (Exception e) {
			System.err.println("[vivid] Could not read config, keeping previous settings: " + e);
			try {
				lastModified = Files.getLastModifiedTime(PATH).toMillis();
			} catch (IOException ignored) {
			}
		}
	}

	public static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			Files.writeString(PATH, GSON.toJson(instance));
			lastModified = Files.getLastModifiedTime(PATH).toMillis();
		} catch (IOException e) {
			System.err.println("[vivid] Could not save config: " + e);
		}
	}

	public static void reset() {
		instance = new VividConfig();
		save();
	}

	private void sanitize() {
		if (preset == null) preset = "Custom";
		if (waterColor == null) waterColor = "#1ED6E6";
		if (accentColor == null) accentColor = "#4FE0FF";
		saturation = clamp(saturation, 0, 2);
		vibrance = clamp(vibrance, 0, 1);
		contrast = clamp(contrast, 0.5, 1.5);
		brightness = clamp(brightness, 0.6, 1.4);
		vignette = clamp(vignette, 0, 1);
		bloom = clamp(bloom, 0, 1.5);
		bloomThreshold = clamp(bloomThreshold, 0.3, 0.95);
		sharpen = clamp(sharpen, 0, 1);
		hazeStart = clamp(hazeStart, 0.2, 1);
		waterClarity = clamp(waterClarity, 0.5, 5);
		hotbarSpeed = clamp(hotbarSpeed, 2, 60);
		hitAmount = clamp(hitAmount, 0, 4);
		critAmount = clamp(critAmount, 0, 4);
	}

	public static double clamp(double v, double lo, double hi) {
		return Math.max(lo, Math.min(hi, v));
	}

	/** Parses "#RRGGBB" into 0xRRGGBB, or returns the fallback. */
	public static int parseHex(String s, int fallback) {
		try {
			String h = s.trim();
			if (h.startsWith("#")) h = h.substring(1);
			return Integer.parseInt(h, 16) & 0xFFFFFF;
		} catch (Exception e) {
			return fallback;
		}
	}

	public static String toHex(int rgb) {
		return String.format("#%06X", rgb & 0xFFFFFF);
	}
}
