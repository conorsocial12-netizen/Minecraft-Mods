package com.voxelskins;

/** Added onto AvatarRenderState by a mixin: which skin parts get 3D pixels this frame. */
public interface VoxelSkinsState {
	int voxelskins$getMask();

	void voxelskins$setMask(int mask);
}
