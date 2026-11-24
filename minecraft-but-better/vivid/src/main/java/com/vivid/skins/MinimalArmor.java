package com.vivid.skins;

import com.mojang.blaze3d.platform.NativeImage;
import com.vivid.VividConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * "Minimal armour": makes a cut-down copy of each armour texture with only a crown on the helmet,
 * shoulder pads on the chestplate and the boots left (optionally the chestplate body / leggings).
 * Works for every armour material (and resource packs), because it masks whatever texture the game uses.
 *
 * The humanoid armour texture is 64x32 (or an HD multiple) laid out like an old player skin:
 * head box at (0,0) + helmet outer at (32,0), body at (16,16), arm at (40,16), leg/boots at (0,16).
 */
public final class MinimalArmor {
	private static final Logger LOGGER = LoggerFactory.getLogger("Vivid minimal armour");

	private record Entry(String settings, Identifier masked) {}

	private static final Map<Identifier, Entry> CACHE = new HashMap<>();

	private MinimalArmor() {}

	/** Which mask settings a texture was made with; changing them rebuilds the textures. */
	private static String settingsKey(VividConfig c) {
		return c.helmetRows + "/" + c.shoulderRows + "/" + c.minimalChestBody;
	}

	/** Returns the masked version of an armour texture (or the original if it can't be read). */
	public static Identifier masked(Identifier original) {
		VividConfig c = VividConfig.get();
		String key = settingsKey(c);
		Entry e = CACHE.get(original);
		if (e != null && e.settings.equals(key)) return e.masked;

		Identifier id = Identifier.fromNamespaceAndPath("vivid", "minimal/" + original.getNamespace() + "/" + original.getPath());
		try (InputStream in = Minecraft.getInstance().getResourceManager().open(original)) {
			NativeImage img = NativeImage.read(in);
			applyMask(img, c);
			Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> "Vivid minimal " + original, img));
		} catch (Exception ex) {
			LOGGER.debug("Couldn't make minimal armour for {}: {}", original, ex.toString());
			CACHE.put(original, new Entry(key, original));
			return original;
		}
		CACHE.put(original, new Entry(key, id));
		return id;
	}

	/** Forget all masked textures (e.g. after a resource pack change). */
	public static void clear() {
		CACHE.clear();
	}

	private static void applyMask(NativeImage img, VividConfig c) {
		int width = img.getWidth(), height = img.getHeight();
		float s = width / 64f;
		if (s <= 0 || height * 2 != width) return; // not a humanoid armour layout: leave it alone
		int helmetRows = (int) Math.round(c.helmetRows);
		int shoulderRows = (int) Math.round(c.shoulderRows);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				float u = x / s, v = y / s; // position in 64x32 units
				if (!keep(u, v, helmetRows, shoulderRows, c.minimalChestBody)) img.setPixel(x, y, 0);
			}
		}
	}

	private static boolean keep(float u, float v, int helmetRows, int shoulderRows, boolean chestBody) {
		// ---- head (0..32) and helmet outer layer (32..64), rows 0..16 ----
		if (v < 16) {
			float lu = u % 32;                       // same layout for both
			if (v < 8) return lu >= 8 && lu < 16;    // top of the helmet (not the bottom face)
			return v < 8 + helmetRows;               // the top rows of the four sides = crown band
		}
		// ---- legs / boots (0..16) ----
		if (u < 16) return true;
		// ---- body (16..40) ----
		if (u < 40) return chestBody;
		// ---- arm (40..56) ----
		if (u < 56) {
			if (v < 20) return u >= 44 && u < 48;     // top of the shoulder (not the bottom face)
			return v < 20 + shoulderRows;            // shoulder pad
		}
		return false;
	}
}
