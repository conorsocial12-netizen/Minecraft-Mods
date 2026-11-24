package com.vivid.skins;

/**
 * The six outer-layer parts of a player skin. Coordinates are the vanilla
 * PlayerModel boxes (in pixels, relative to the part they hang off), and
 * u/v is where the overlay is drawn on the 64x64 skin texture.
 */
public enum SkinPart {
	//        u   v   x   y   z   w   h  d  thick  skipTop skipBottom
	HAT(      32,  0, -4, -8, -4,  8,  8, 8, 1.00f, false, false),
	JACKET(   16, 32, -4,  0, -2,  8, 12, 4, 0.90f, true,  true),
	RIGHT_SLEEVE(40, 32, -3, -2, -2, 4, 12, 4, 0.90f, false, false),
	LEFT_SLEEVE( 48, 48, -1, -2, -2, 4, 12, 4, 0.90f, false, false),
	RIGHT_PANTS(  0, 32, -2,  0, -2, 4, 12, 4, 0.90f, true,  true),
	LEFT_PANTS(   0, 48, -2,  0, -2, 4, 12, 4, 0.90f, true,  true);

	public final int u, v, x, y, z, w, h, d;
	/** How far (in pixels) the 3D pixels stick out from the body. */
	public final float thickness;
	/** Skip the top / bottom faces (they would poke into the neighbouring body part or the ground). */
	public final boolean skipTop, skipBottom;

	SkinPart(int u, int v, int x, int y, int z, int w, int h, int d, float thickness, boolean skipTop, boolean skipBottom) {
		this.u = u; this.v = v; this.x = x; this.y = y; this.z = z; this.w = w; this.h = h; this.d = d;
		this.thickness = thickness; this.skipTop = skipTop; this.skipBottom = skipBottom;
	}

	public int bit() {
		return 1 << ordinal();
	}

	/** Slim (Alex-style) arms are 3 pixels wide instead of 4. */
	public int width(boolean slim) {
		return slim && (this == RIGHT_SLEEVE || this == LEFT_SLEEVE) ? 3 : w;
	}

	public int minX(boolean slim) {
		return slim && this == RIGHT_SLEEVE ? -2 : x;
	}
}
