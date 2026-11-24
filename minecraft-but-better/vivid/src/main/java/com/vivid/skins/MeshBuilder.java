package com.vivid.skins;

import java.util.Arrays;

/**
 * Turns the overlay pixels of one skin part into a shell of little cubes ("voxels"), plus thin
 * highlight strips along the visible edges of those cubes (the "pixel edges" outline).
 *
 * The part is described by a grid one cell bigger than the body box on every side. The inner cells
 * are the body itself; every visible overlay pixel becomes one cell of the outer shell, placed
 * directly above the spot it is painted on. Edge cells between two filled faces are filled too, so
 * hair and hoods don't look notched. Only faces that can actually be seen are kept.
 */
public final class MeshBuilder {
	private static final int EMPTY = 0, BODY = 1, VOXEL = 2;
	// directions, matching vanilla ModelPart.Cube: DOWN = -Y (the top of the head, Y points down in models)
	private static final int DOWN = 0, UP = 1, WEST = 2, NORTH = 3, EAST = 4, SOUTH = 5;
	private static final int[][] STEP = {{0, -1, 0}, {0, 1, 0}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}, {0, 0, 1}};
	/** Pull UVs a hair inside the pixel so neighbouring pixels never bleed in. */
	private static final float UV_INSET = 0.02f;
	/** Highlight strips float this far (pixels) above the surface so they never flicker. */
	private static final float LIFT = 0.025f;
	private static final float[] NO_UV = new float[4];

	public record Built(PartMesh voxels, PartMesh edges) {}

	private MeshBuilder() {}

	/**
	 * @param opaque         [x][y] in 64x64 skin-pixel space: true if that pixel is (at least partly) visible
	 * @param thicknessScale 1 = normal depth, smaller = flatter pixels
	 * @param edgeWidth      width of the edge highlight strips, in skin pixels (0 = none)
	 */
	public static Built build(SkinPart part, boolean slim, boolean[][] opaque, float thicknessScale, float edgeWidth) {
		return build(part, slim, opaque, thicknessScale, edgeWidth, -1);
	}

	/**
	 * @param coverRows if >= 0, the top face and this many rows at the top of each side are left out
	 *                  (they're under the minimal helmet's crown, which would otherwise cut through them)
	 */
	public static Built build(SkinPart part, boolean slim, boolean[][] opaque, float thicknessScale, float edgeWidth, int coverRows) {
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
				if (!part.skipTop && coverRows < 0)
					put(cell, rect, opaque, u1 + i, v0 + k, idx(1 + i, 0, zc, gy, gz), false);
				if (!part.skipBottom)
					put(cell, rect, opaque, u2 + i, v0 + k, idx(1 + i, h + 1, zc, gy, gz), true);
			}
		}
		for (int j = 0; j < h; j++) {
			if (j < coverRows) continue;
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

		QuadBuffer voxels = new QuadBuffer();
		QuadBuffer edges = new QuadBuffer();

		for (int x = 0; x < gx; x++) {
			for (int y = 0; y < gy; y++) {
				for (int z = 0; z < gz; z++) {
					int c = idx(x, y, z, gy, gz);
					if (cell[c] != VOXEL) continue;
					float[] box = {bx[x], bx[x + 1], by[y], by[y + 1], bz[z], bz[z + 1]};
					for (int dir = 0; dir < 6; dir++) {
						if (!exposed(cell, x, y, z, dir, gx, gy, gz)) continue; // hidden face
						writeQuad(voxels.next(), voxels.offset(), dir, box, rect, c * 4);
					}
					if (edgeWidth > 0) addEdgeStrips(edges, cell, x, y, z, gx, gy, gz, box, edgeWidth);
				}
			}
		}
		return new Built(voxels.toMesh(), edges.toMesh());
	}

	/**
	 * Highlight strips round the outer edges of a plain box (the base skin of one body part):
	 * the "body rim" outline. Coordinates are in pixels, like the vanilla model boxes.
	 */
	public static PartMesh boxRim(float x0, float y0, float z0, float w, float h, float d, float width) {
		QuadBuffer out = new QuadBuffer();
		float[] box = {x0, x0 + w, y0, y0 + h, z0, z0 + d};
		for (int face = 0; face < 6; face++) {
			int faceAxis = axis(face);
			for (int side = 0; side < 6; side++) {
				if (axis(side) == faceAxis) continue;
				strip(out, box, face, side, width);
			}
		}
		return out.toMesh();
	}

	// ------------------------------------------------------------------------------------------

	private static boolean exposed(int[] cell, int x, int y, int z, int dir, int gx, int gy, int gz) {
		int nx = x + STEP[dir][0], ny = y + STEP[dir][1], nz = z + STEP[dir][2];
		boolean inside = nx >= 0 && ny >= 0 && nz >= 0 && nx < gx && ny < gy && nz < gz;
		return !inside || cell[idx(nx, ny, nz, gy, gz)] == EMPTY;
	}

	/** For every outward-facing side of this voxel, put strips along the edges that border empty space. */
	private static void addEdgeStrips(QuadBuffer out, int[] cell, int x, int y, int z, int gx, int gy, int gz,
									  float[] box, float width) {
		for (int face = 0; face < 6; face++) {
			if (!isOuter(face, x, y, z, gx, gy, gz)) continue;
			int faceAxis = axis(face);
			for (int side = 0; side < 6; side++) {
				if (axis(side) == faceAxis) continue;
				if (!exposed(cell, x, y, z, side, gx, gy, gz)) continue; // continues into a neighbour: no edge here
				strip(out, box, face, side, width);
			}
		}
	}

	/** Is this face of the cell on the outside of the shell (pointing away from the body)? */
	private static boolean isOuter(int dir, int x, int y, int z, int gx, int gy, int gz) {
		return switch (dir) {
			case DOWN -> y == 0;
			case UP -> y == gy - 1;
			case WEST -> x == 0;
			case EAST -> x == gx - 1;
			case NORTH -> z == 0;
			default -> z == gz - 1;
		};
	}

	private static int axis(int dir) {
		return switch (dir) {
			case WEST, EAST -> 0;
			case DOWN, UP -> 1;
			default -> 2;
		};
	}

	private static boolean positive(int dir) {
		return dir == UP || dir == EAST || dir == SOUTH;
	}

	/** One thin strip on face `face` of the box, hugging the edge it shares with face `side`. */
	private static void strip(QuadBuffer out, float[] box, int face, int side, float width) {
		float[] b = box.clone();
		// lift the strip just above the face
		int fa = axis(face) * 2;
		if (positive(face)) {
			b[fa] = b[fa + 1];
			b[fa + 1] += LIFT;
		} else {
			b[fa + 1] = b[fa];
			b[fa] -= LIFT;
		}
		// squeeze the strip against the side edge
		int sa = axis(side) * 2;
		float extent = b[sa + 1] - b[sa];
		float wdt = Math.min(width, extent * 0.5f);
		if (positive(side)) b[sa] = b[sa + 1] - wdt;
		else b[sa + 1] = b[sa] + wdt;
		writeQuad(out.next(), out.offset(), face, b, NO_UV, 0);
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
		Arrays.fill(src, -1);
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

	/** box = {x0, x1, y0, y1, z0, z1} in pixels. */
	private static void writeQuad(float[] out, int o, int dir, float[] box, float[] rect, int r) {
		float x0 = box[0], x1 = box[1], y0 = box[2], y1 = box[3], z0 = box[4], z1 = box[5];
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

	/** Growable array of quads. */
	private static final class QuadBuffer {
		private float[] data = new float[64 * PartMesh.FLOATS_PER_QUAD];
		private int quads = 0;
		private int offset = 0;

		/** Reserves room for one more quad; write it at offset(). */
		float[] next() {
			offset = quads * PartMesh.FLOATS_PER_QUAD;
			if (offset + PartMesh.FLOATS_PER_QUAD > data.length) data = Arrays.copyOf(data, data.length * 2);
			quads++;
			return data;
		}

		int offset() {
			return offset;
		}

		PartMesh toMesh() {
			return quads == 0 ? PartMesh.EMPTY : new PartMesh(Arrays.copyOf(data, quads * PartMesh.FLOATS_PER_QUAD), quads);
		}
	}
}
