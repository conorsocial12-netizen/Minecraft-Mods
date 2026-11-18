package com.voxelskins;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds the 3D meshes for a skin once and remembers them. Downloaded skins are read
 * straight from the in-memory texture; built-in skins (Steve, Alex, ...) from the resource pack.
 * Only used on the render thread.
 */
public final class SkinMeshCache {
	private static final Logger LOGGER = LoggerFactory.getLogger("3D Skin Layers");
	private static final int MAX_ENTRIES = 200;
	private static final long RETRY_MS = 3000;

	/** Meshes for one skin, indexed by SkinPart.ordinal(). */
	public record SkinMeshes(PartMesh[] parts) {
		public PartMesh get(SkinPart part) {
			return parts[part.ordinal()];
		}
	}

	private record Key(Identifier texture, boolean slim) {}

	private static final Map<Key, SkinMeshes> CACHE = new LinkedHashMap<>(64, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<Key, SkinMeshes> eldest) {
			return size() > MAX_ENTRIES;
		}
	};
	private static final Map<Key, Long> FAILED = new LinkedHashMap<>();

	private SkinMeshCache() {}

	/** Returns the meshes, or null if the skin can't be read (yet). */
	public static SkinMeshes get(Identifier texture, boolean slim) {
		Key key = new Key(texture, slim);
		SkinMeshes cached = CACHE.get(key);
		if (cached != null) return cached;

		Long failedAt = FAILED.get(key);
		long now = System.currentTimeMillis();
		if (failedAt != null && now - failedAt < RETRY_MS) return null;

		boolean[][] opaque = readOpaque(texture);
		if (opaque == null) {
			if (FAILED.size() > MAX_ENTRIES) FAILED.clear();
			FAILED.put(key, now);
			return null;
		}
		FAILED.remove(key);
		PartMesh[] parts = new PartMesh[SkinPart.values().length];
		for (SkinPart part : SkinPart.values()) {
			parts[part.ordinal()] = MeshBuilder.build(part, slim, opaque);
		}
		SkinMeshes meshes = new SkinMeshes(parts);
		CACHE.put(key, meshes);
		return meshes;
	}

	public static void clear() {
		CACHE.clear();
		FAILED.clear();
	}

	/** Which of the 64x64 skin pixels are visible. HD skins are scaled down (a block counts if any pixel in it shows). */
	private static boolean[][] readOpaque(Identifier texture) {
		try {
			AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(texture);
			if (tex instanceof DynamicTexture dyn) {
				NativeImage img = dyn.getPixels();
				if (img != null && !img.isClosed()) return toOpaque(img);
			}
			try (InputStream in = Minecraft.getInstance().getResourceManager().open(texture);
				 NativeImage img = NativeImage.read(in)) {
				return toOpaque(img);
			}
		} catch (Exception e) {
			LOGGER.debug("Couldn't read skin {}: {}", texture, e.toString());
			return null;
		}
	}

	private static boolean[][] toOpaque(NativeImage img) {
		int width = img.getWidth(), height = img.getHeight();
		if (width < 64 || width % 64 != 0 || height != width) return null; // not a modern 64x64 (or HD) skin
		int s = width / 64;
		boolean[][] opaque = new boolean[64][64];
		for (int x = 0; x < 64; x++) {
			for (int y = 0; y < 64; y++) {
				boolean any = false;
				for (int dx = 0; dx < s && !any; dx++)
					for (int dy = 0; dy < s && !any; dy++)
						any = (img.getPixel(x * s + dx, y * s + dy) >>> 24) != 0;
				opaque[x][y] = any;
			}
		}
		return opaque;
	}
}
