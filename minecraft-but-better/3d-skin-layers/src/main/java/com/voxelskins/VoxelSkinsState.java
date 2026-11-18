package com.voxelskins;

/** Added onto AvatarRenderState by a mixin: which skin parts get 3D pixels this frame, and how. */
public interface VoxelSkinsState {
	int voxelskins$getMask();

	void voxelskins$setMask(int mask);

	/** Thickness of this player's 3D pixels, percent of normal. */
	int voxelskins$getThickness();

	void voxelskins$setThickness(int percent);

	/** How far (pixels) armor on each part sticks out, indexed by SkinPart.ordinal(); 0 = no armor. */
	float[] voxelskins$getArmor();
}
