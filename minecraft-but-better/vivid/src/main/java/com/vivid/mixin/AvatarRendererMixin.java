package com.vivid.mixin;

import com.vivid.skins.SkinsLogic;
import com.vivid.skins.SkinsState;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** After the game collects a player's render info, work out how Vivid draws their skin. */
@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
			at = @At("TAIL"))
	private void vivid$decide(Avatar entity, AvatarRenderState state, float partialTick, CallbackInfo ci) {
		try {
			SkinsLogic.compute(entity, state);
		} catch (RuntimeException e) {
			((SkinsState) state).vivid$setMask(0); // never break player rendering
			((SkinsState) state).vivid$setOutline(false);
			((SkinsState) state).vivid$setMinimalArmor(false);
			((SkinsState) state).vivid$setHatCover(-1);
		}
	}
}
