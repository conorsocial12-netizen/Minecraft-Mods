package com.voxelskins;

/**
 * Turns the overlay pixels of one skin part into a shell of little cubes ("voxels").
 *
 * The part is described by a grid one cell bigger than the body box on every side.
 * The inner cells are the body itself; every visible overlay pixel becomes one
 * cell of the outer shell, placed directly above the spot it is painted on. Edge
 * cells between two filled faces are filled too, so hair and hoods don't look notched.
 * Only faces that can actually be seen are kept.
 */
public final class MeshBuilder {
	private static final int EMPTY = 0, BODY = 1, VOXEL = 2;
	// directions, matching vanilla ModelPart.Cube: DOWN = -Y (the top of the head, Y points down in models)
	private static final int DOWN = 0, UP = 1, WEST = 2, NORTH = 3, EAST = 4, SOUTH = 5;
	private static final int[][] STEP = {{0, -1, 0}, {0, 1, 0}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}, {0, 0, 1}};
	/** Pull UVs a hair inside the pixel so neighbouring pixels never bleed in. */
	private static final float UV_INSET = 0.02f;

	private MeshBuilder() {}

	/**
	 * @param opaque [x][y] in 64x64 skin-pixel space: true if that pixel is (at least partly) visible
	 * @param thicknessScale 1 = normal depth, smaller = flatter pixels
	 */
	public static PartMesh build(SkinPart part, boolean slim, boolean[][] opaque, float thicknessScale) {
		int w = part.width(slim), h = part.h, d = part.d;
		int gx = w + 2, gy = h + 2, gz = d + 2;
		int[] cell = new int[gx * gy * gz];
		float[] rect = new float[gx * gy * gz * 4]; // Ua, Va, Ub, Vb in skin pixels

		for (int x = 1; x <= w; x++)
			for (int y = 1; y <= h; y++)
				for (int z = 1; z <= d; z++)
					cell[idx(x, y, z, gy, gz)] = BODY;

		// box UV layout (same maths as vanilla ModelPart.Cube)
		int u0 = part.u, u1 = u0 + d, u2 = u1 + w, u4 = u2 + d;
		int v0 = part.v, v1 = v0 + d;

		for (int i = 0; i < w; i++) {
			for (int k = 0; k < d; k++) {
				int zc = 1 + (d - 1 - k);
				if (!part.skipTop)
					put(cell, rect, opaque, u1 + i, v0 + k, idx(1 + i, 0, zc, gy, gz), false);
				if (!part.skipBottom)
					put(cell, rect, opaque, u2 + i, v0 + k, idx(1 + i, h + 1, zc, gy, gz), true);
			}
		}
		for (int j = 0; j < h; j++) {
			int yc = 1 + j;
			for (int i = 0; i < d; i++) {
				put(cell, rect, opaque, u0 + i, v1 + j, idx(0, yc, 1 + (d - 1 - i), gy, gz), false); // west (right side)
				put(cell, rect, opaque, u2 + i, v1 + j, idx(w + 1, yc, 1 + i, gy, gz), false);      // east (left side)
			}
			for (int i = 0; i < w; i++) {
				put(cell, rect, opaque, u1 + i, v1 + j, idx(1 + i, yc, 0, gy, gz), false);          // north (front)
				put(cell, rect, opaque, u4 + i, v1 + j, idx(1 + (w - 1 - i), yc, d + 1, gy, gz), false); // south (back)
			}
		}

		fillEdges(cell, rect, gx, gy, gz);

		// grid cell boundaries -> model coordinates (pixels); the shell is `thickness` deep
		float t = part.thickness * thicknessScale;
		float[] bx = bounds(part.minX(slim), w, t);
		float[] by = bounds(part.y, h, t);
		float[] bz = bounds(part.z, d, t);

		int maxQuads = 0;
		for (int c : cell) if (c == VOXEL) maxQuads += 6;
		float[] out = new float[maxQuads * PartMesh.FLOATS_PER_QUAD];
		int quads = 0;

		for (int x = 0; x < gx; x++) {
			for (int y = 0; y < gy; y++) {
				for (int z = 0; z < gz; z++) {
					int c = idx(x, y, z, gy, gz);
					if (cell[c] != VOXEL) continue;
					for (int dir = 0; dir < 6; dir++) {
						int nx = x + STEP[dir][0], ny = y + STEP[dir][1], nz = z + STEP[dir][2];
						boolean inside = nx >= 0 && ny >= 0 && nz >= 0 && nx < gx && ny < gy && nz < gz;
						if (inside && cell[idx(nx, ny, nz, gy, gz)] != EMPTY) continue; // hidden face
						writeQuad(out, quads++ * PartMesh.FLOATS_PER_QUAD, dir,
								bx[x], bx[x + 1], by[y], by[y + 1], bz[z], bz[z + 1], rect, c * 4);
					}
				}
			}
		}
		return new PartMesh(out, quads);
	}

	private static int idx(int x, int y, int z, int gy, int gz) {
		return (x * gy + y) * gz + z;
	}

	private static void put(int[] cell, float[] rect, boolean[][] opaque, int px, int py, int c, boolean flipV) {
		if (px < 0 || py < 0 || px >= 64 || py >= 64 || !opaque[px][py]) return;
		cell[c] = VOXEL;
		rect[c * 4] = px;
		rect[c * 4 + 2] = px + 1;
		rect[c * 4 + 1] = flipV ? py + 1 : py;
		rect[c * 4 + 3] = flipV ? py : py + 1;
	}

	/** Fill the cells along the box edges when both neighbouring faces have a pixel there. */
	private static void fillEdges(int[] cell, float[] rect, int gx, int gy, int gz) {
		int[] src = new int[cell.length];
		java.util.Arrays.fill(src, -1);
		for (int x = 0; x < gx; x++) {
			for (int y = 0; y < gy; y++) {
				for (int z = 0; z < gz; z++) {
					boolean ox = x == 0 || x == gx - 1, oy = y == 0 || y == gy - 1, oz = z == 0 || z == gz - 1;
					int outside = (ox ? 1 : 0) + (oy ? 1 : 0) + (oz ? 1 : 0);
					if (outside != 2) continue;
					int ix = x == 0 ? 1 : gx - 2, iy = y == 0 ? 1 : gy - 2, iz = z == 0 ? 1 : gz - 2;
					int a, b; // a = preferred neighbour (a side face, so the side pattern continues around the edge)
					if (oy && oz) { a = idx(x, iy, z, gy, gz); b = idx(x, y, iz, gy, gz); }
					else if (oy && ox) { a = idx(x, iy, z, gy, gz); b = idx(ix, y, z, gy, gz); }
					else { a = idx(ix, y, z, gy, gz); b = idx(x, y, iz, gy, gz); } // ox && oz: prefer front/back
					if (cell[a] == VOXEL && cell[b] == VOXEL) src[idx(x, y, z, gy, gz)] = a;
				}
			}
		}
		for (int c = 0; c < cell.length; c++) {
			if (src[c] < 0) continue;
			cell[c] = VOXEL;
			System.arraycopy(rect, src[c] * 4, rect, c * 4, 4);
		}
	}

	private static float[] bounds(int min, int size, float t) {
		float[] b = new float[size + 3];
		b[0] = min - t;
		for (int k = 1; k <= size + 1; k++) b[k] = min + k - 1;
		b[size + 2] = min + size + t;
		return b;
	}

	private static void writeQuad(float[] out, int o, int dir, float x0, float x1, float y0, float y1, float z0, float z1,
								  float[] rect, int r) {
		// the eight corners, named like vanilla's ModelPart.Cube
		float[][] v = {
				{x0, y0, z0}, {x1, y0, z0}, {x1, y1, z0}, {x0, y1, z0},
				{x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}, {x0, y1, z1}};
		int[] order = switch (dir) {
			case DOWN -> new int[]{5, 4, 0, 1};
			case UP -> new int[]{2, 3, 7, 6};
			case WEST -> new int[]{0, 4, 7, 3};
			case NORTH -> new int[]{1, 0, 3, 2};
			case EAST -> new int[]{5, 1, 2, 6};
			default -> new int[]{4, 5, 6, 7};
		};
		float ua = inset(rect[r], rect[r + 2]), ub = inset(rect[r + 2], rect[r]);
		float va = inset(rect[r + 1], rect[r + 3]), vb = inset(rect[r + 3], rect[r + 1]);
		float[][] uv = {{ub, va}, {ua, va}, {ua, vb}, {ub, vb}};
		for (int k = 0; k < 4; k++) {
			float[] p = v[order[k]];
			int q = o + k * 5;
			out[q] = p[0] / 16f;
			out[q + 1] = p[1] / 16f;
			out[q + 2] = p[2] / 16f;
			out[q + 3] = uv[k][0] / 64f;
			out[q + 4] = uv[k][1] / 64f;
		}
		out[o + 20] = STEP[dir][0];
		out[o + 21] = STEP[dir][1];
		out[o + 22] = STEP[dir][2];
	}

	/** Move `from` slightly towards `to`. */
	private static float inset(float from, float to) {
		return from + Math.signum(to - from) * UV_INSET;
	}
}
