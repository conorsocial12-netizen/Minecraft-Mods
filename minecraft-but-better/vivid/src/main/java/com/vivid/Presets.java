package com.vivid;

import java.util.List;

/** Ready-made looks. Applying one changes the visual settings but keeps your HUD settings. */
public final class Presets {
	public static final List<String> NAMES = List.of("Vibrant", "Cinematic", "Realistic", "Dreamy", "Vanilla+");

	public static String describe(String name) {
		return switch (name) {
			case "Vibrant" -> "Bright, punchy colours, orange sunsets, tropical water";
			case "Cinematic" -> "Deeper shadows, warm film-like grading, strong vignette";
			case "Realistic" -> "Natural colours, more atmospheric haze, soft light";
			case "Dreamy" -> "Soft pastel tones, lots of glow, gentle fog";
			case "Vanilla+" -> "Looks like normal Minecraft, just a bit cleaner";
			default -> "Your own settings";
		};
	}

	public static void apply(String name, VividConfig c) {
		VividConfig t = new VividConfig(); // starts as Vibrant
		switch (name) {
			case "Cinematic" -> {
				t.saturation = 1.1; t.vibrance = 0.2; t.contrast = 1.22; t.brightness = 0.97;
				t.vignette = 0.5; t.bloom = 0.45; t.bloomThreshold = 0.65; t.sharpen = 0.2;
				t.skySaturation = 1.2; t.sunsetStrength = 2.2; t.starBrightness = 1.6; t.cloudTint = 0.85;
				t.hazeStart = 0.6; t.biomeFogDistance = 1.0; t.fogSaturation = 1.1; t.weatherBrightness = 0.25;
				t.waterColor = "#1C7FB8"; t.waterSurfaceBlend = 0.45; t.waterClarity = 1.6;
				t.underwaterTint = 0.35; t.underwaterWobble = 0.4;
				t.torchGlow = 1.2; t.warmLight = 0.6; t.nightGlow = 0.7;
				t.netherMood = 0.45; t.endMood = 0.4;
			}
			case "Realistic" -> {
				t.saturation = 1.05; t.vibrance = 0.15; t.contrast = 1.05; t.brightness = 1.0;
				t.vignette = 0.15; t.bloom = 0.2; t.bloomThreshold = 0.8; t.sharpen = 0.3;
				t.skySaturation = 1.1; t.sunsetStrength = 1.3; t.starBrightness = 1.5; t.cloudTint = 0.5;
				t.hazeStart = 0.45; t.biomeFogDistance = 0.9; t.fogSaturation = 1.0; t.weatherBrightness = 0.3;
				t.waterColor = "#2A8CA8"; t.waterSurfaceBlend = 0.35; t.waterClarity = 1.5;
				t.underwaterTint = 0.3; t.underwaterWobble = 0.3;
				t.torchGlow = 1.1; t.warmLight = 0.45; t.nightGlow = 0.35;
				t.netherMood = 0.25; t.endMood = 0.2;
			}
			case "Dreamy" -> {
				t.saturation = 0.95; t.vibrance = 0.4; t.contrast = 0.9; t.brightness = 1.1;
				t.vignette = 0.12; t.bloom = 0.9; t.bloomThreshold = 0.5; t.sharpen = 0.0;
				t.skySaturation = 1.15; t.sunsetStrength = 1.9; t.starBrightness = 2.5; t.cloudTint = 1.0;
				t.hazeStart = 0.5; t.biomeFogDistance = 1.0; t.fogSaturation = 1.3; t.weatherBrightness = 0.8;
				t.waterColor = "#7FE3F0"; t.waterSurfaceBlend = 0.55; t.waterClarity = 2.0;
				t.underwaterTint = 0.3; t.underwaterWobble = 0.7;
				t.torchGlow = 1.2; t.warmLight = 0.3; t.nightGlow = 0.9;
				t.netherMood = 0.3; t.endMood = 0.4;
			}
			case "Vanilla+" -> {
				t.saturation = 1.08; t.vibrance = 0.12; t.contrast = 1.03; t.brightness = 1.0;
				t.vignette = 0.0; t.bloom = 0.1; t.bloomThreshold = 0.85; t.sharpen = 0.2;
				t.skySaturation = 1.1; t.sunsetStrength = 1.15; t.starBrightness = 1.3; t.cloudTint = 0.25;
				t.hazeStart = 0.85; t.biomeFogDistance = 1.2; t.fogSaturation = 1.05; t.weatherBrightness = 0.4;
				t.waterColor = "#3F76E4"; t.waterSurfaceBlend = 0.0; t.waterClarity = 1.3;
				t.underwaterTint = 0.1; t.underwaterWobble = 0.2;
				t.torchGlow = 1.05; t.warmLight = 0.15; t.nightGlow = 0.2;
				t.netherMood = 0.1; t.endMood = 0.1;
			}
			default -> { } // Vibrant
		}
		copyVisuals(t, c);
		c.preset = name;
	}

	private static void copyVisuals(VividConfig from, VividConfig to) {
		to.saturation = from.saturation; to.vibrance = from.vibrance; to.contrast = from.contrast;
		to.brightness = from.brightness; to.vignette = from.vignette; to.bloom = from.bloom;
		to.bloomThreshold = from.bloomThreshold; to.sharpen = from.sharpen;
		to.skySaturation = from.skySaturation; to.sunsetStrength = from.sunsetStrength;
		to.starBrightness = from.starBrightness; to.cloudTint = from.cloudTint;
		to.hazeStart = from.hazeStart; to.biomeFogDistance = from.biomeFogDistance;
		to.fogSaturation = from.fogSaturation; to.weatherBrightness = from.weatherBrightness;
		to.waterColor = from.waterColor; to.waterSurfaceBlend = from.waterSurfaceBlend;
		to.waterClarity = from.waterClarity; to.underwaterTint = from.underwaterTint;
		to.underwaterWobble = from.underwaterWobble;
		to.torchGlow = from.torchGlow; to.warmLight = from.warmLight; to.nightGlow = from.nightGlow;
		to.netherMood = from.netherMood; to.endMood = from.endMood;
	}

	private Presets() {}
}
