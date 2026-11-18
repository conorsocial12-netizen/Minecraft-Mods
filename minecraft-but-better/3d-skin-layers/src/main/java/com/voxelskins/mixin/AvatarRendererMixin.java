package com.voxelskins.mixin;

import com.voxelskins.VoxelSkinsLogic;
import com.voxelskins.VoxelSkinsState;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** After the game collects a player's render info, work out which parts get 3D pixels. */
@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
			at = @At("TAIL"))
	private void voxelskins$decide(Avatar entity, AvatarRenderState state, float partialTick, CallbackInfo ci) {
		int mask;
		try {
			mask = VoxelSkinsLogic.computeMask(state);
		} catch (RuntimeException e) {
			mask = 0; // never break player rendering
		}
		((VoxelSkinsState) state).voxelskins$setMask(mask);
	}
}
