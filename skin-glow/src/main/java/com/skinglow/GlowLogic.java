package com.skinglow;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import java.awt.Color;

public final class GlowLogic {
	private GlowLogic() {
	}

	private enum Kind { SELF, PLAYER, HOSTILE, PASSIVE, OTHER }

	private static Kind kindOf(Entity e, Minecraft mc) {
		if (e == mc.player) return Kind.SELF;
		if (e instanceof Player) return Kind.PLAYER;
		if (e instanceof Enemy) return Kind.HOSTILE;
		if (e instanceof LivingEntity) return Kind.PASSIVE;
		return Kind.OTHER;
	}

	/** Should this entity get the outline? Only ever true for entities in the client's world. */
	public static boolean shouldGlow(Entity e) {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null || mc.player == null || mc.level == null || e == null) return false;
		if (e.level() != mc.level) return false; // ignore integrated-server copies

		SkinGlowConfig c = SkinGlowConfig.get();
		if (!c.enabled) return false;
		if (e.isInvisible() && !c.includeInvisible) return false;

		boolean ok = switch (kindOf(e, mc)) {
			case SELF -> c.self;
			case PLAYER -> c.players;
			case HOSTILE -> c.hostileMobs;
			case PASSIVE -> c.passiveMobs;
			case OTHER -> c.otherEntities;
		};
		if (!ok) return false;

		if (c.maxDistance > 0 && e != mc.player) {
			if (e.distanceToSqr(mc.player) > c.maxDistance * c.maxDistance) return false;
		}
		return true;
	}

	/** True when our color should replace the vanilla team color for this entity. */
	public static boolean overridesTeamColor(Entity e) {
		SkinGlowConfig c = SkinGlowConfig.get();
		return !(c.respectTeamColors && e.getTeam() != null);
	}

	/** Final 0xRRGGBB outline color for this entity right now. */
	public static int color(Entity e) {
		Minecraft mc = Minecraft.getInstance();
		SkinGlowConfig c = SkinGlowConfig.get();
		double t = System.currentTimeMillis() / 1000.0;

		String perKind = switch (kindOf(e, mc)) {
			case SELF -> c.selfColor;
			case PLAYER -> c.playerColor;
			case HOSTILE -> c.hostileColor;
			case PASSIVE -> c.passiveColor;
			case OTHER -> "";
		};

		int rgb;
		if (perKind != null && !perKind.isBlank()) {
			rgb = parseHex(perKind, 0xFFFFFF);
		} else {
			rgb = switch (c.colorMode) {
				case SOLID -> parseHex(c.color, 0xFFFFFF);
				case RAINBOW -> {
					double offset = c.rainbowOffsetPerEntity ? (e.getId() * 0.137) : 0.0;
					float hue = (float) (((t * c.rainbowSpeed) + offset) % 1.0);
					if (hue < 0) hue += 1f;
					yield Color.HSBtoRGB(hue, (float) c.rainbowSaturation, 1f) & 0xFFFFFF;
				}
				case GRADIENT -> {
					// smooth back-and-forth between color and color2
					double phase = 0.5 - 0.5 * Math.cos(t * c.gradientSpeed * 2 * Math.PI);
					yield lerp(parseHex(c.color, 0xFFFFFF), parseHex(c.color2, 0xFF00E5), phase);
				}
			};
		}

		double b = c.brightness;
		if (c.pulse) {
			double wave = 0.5 - 0.5 * Math.cos(t * c.pulseSpeed * 2 * Math.PI); // 0..1
			b *= c.pulseMinBrightness + (1.0 - c.pulseMinBrightness) * wave;
		}
		rgb = scale(rgb, b);

		if (c.swapRedBlue) {
			rgb = ((rgb & 0xFF) << 16) | (rgb & 0x00FF00) | ((rgb >> 16) & 0xFF);
		}
		return rgb;
	}

	static int parseHex(String s, int fallback) {
		try {
			String h = s.trim();
			if (h.startsWith("#")) h = h.substring(1);
			if (h.startsWith("0x") || h.startsWith("0X")) h = h.substring(2);
			if (h.length() == 3) {
				h = "" + h.charAt(0) + h.charAt(0) + h.charAt(1) + h.charAt(1) + h.charAt(2) + h.charAt(2);
			}
			return Integer.parseInt(h, 16) & 0xFFFFFF;
		} catch (Exception ex) {
			return fallback;
		}
	}

	private static int lerp(int a, int b, double t) {
		int r = (int) Math.round(((a >> 16) & 0xFF) * (1 - t) + ((b >> 16) & 0xFF) * t);
		int g = (int) Math.round(((a >> 8) & 0xFF) * (1 - t) + ((b >> 8) & 0xFF) * t);
		int bl = (int) Math.round((a & 0xFF) * (1 - t) + (b & 0xFF) * t);
		return (r << 16) | (g << 8) | bl;
	}

	private static int scale(int rgb, double f) {
		int r = (int) Math.round(((rgb >> 16) & 0xFF) * f);
		int g = (int) Math.round(((rgb >> 8) & 0xFF) * f);
		int b = (int) Math.round((rgb & 0xFF) * f);
		return (r << 16) | (g << 8) | b;
	}
}
