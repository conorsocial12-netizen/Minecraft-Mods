package com.voxelskins.mixin;

import com.voxelskins.VoxelSkinsState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/** Remembers which parts are 3D for this player this frame. */
@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements VoxelSkinsState {
	@Unique
	private int voxelskins$mask;

	@Override
	public int voxelskins$getMask() {
		return voxelskins$mask;
	}

	@Override
	public void voxelskins$setMask(int mask) {
		voxelskins$mask = mask;
	}
}
