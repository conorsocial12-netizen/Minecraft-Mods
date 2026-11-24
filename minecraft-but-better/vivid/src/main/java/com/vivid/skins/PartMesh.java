package com.vivid.skins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Pre-built quads. Layout per quad: 4 vertices of (x, y, z, u, v) in block units / normalized UVs,
 * followed by the face normal (nx, ny, nz).
 */
public final class PartMesh {
	public static final int FLOATS_PER_QUAD = 4 * 5 + 3;
	public static final PartMesh EMPTY = new PartMesh(new float[0], 0);

	private final float[] data;
	private final int quads;

	public PartMesh(float[] data, int quads) {
		this.data = data;
		this.quads = quads;
	}

	public boolean isEmpty() {
		return quads == 0;
	}

	public void emit(PoseStack.Pose pose, VertexConsumer out, int light, int overlay) {
		emit(pose, out, light, overlay, -1);
	}

	public void emit(PoseStack.Pose pose, VertexConsumer out, int light, int overlay, int argb) {
		float[] q = data;
		for (int i = 0, o = 0; i < quads; i++, o += FLOATS_PER_QUAD) {
			float nx = q[o + 20], ny = q[o + 21], nz = q[o + 22];
			for (int k = 0; k < 4; k++) {
				int p = o + k * 5;
				out.addVertex(pose, q[p], q[p + 1], q[p + 2])
						.setColor(argb)
						.setUv(q[p + 3], q[p + 4])
						.setOverlay(overlay)
						.setLight(light)
						.setNormal(pose, nx, ny, nz);
			}
		}
	}
}
