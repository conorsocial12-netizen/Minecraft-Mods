package com.vivid.mixin;

import com.vivid.skins.SkinPart;
import com.vivid.skins.SkinsState;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Hides the flat vanilla layer for every part that Vivid draws in 3D instead. */
@Mixin(PlayerModel.class)
public class PlayerModelMixin {
	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
	private void vivid$hideFlat(AvatarRenderState state, CallbackInfo ci) {
		int mask = ((SkinsState) state).vivid$getMask();
		if (mask == 0) return;
		PlayerModel model = (PlayerModel) (Object) this;
		if ((mask & SkinPart.HAT.bit()) != 0) model.hat.visible = false;
		if ((mask & SkinPart.JACKET.bit()) != 0) model.jacket.visible = false;
		if ((mask & SkinPart.RIGHT_SLEEVE.bit()) != 0) model.rightSleeve.visible = false;
		if ((mask & SkinPart.LEFT_SLEEVE.bit()) != 0) model.leftSleeve.visible = false;
		if ((mask & SkinPart.RIGHT_PANTS.bit()) != 0) model.rightPants.visible = false;
		if ((mask & SkinPart.LEFT_PANTS.bit()) != 0) model.leftPants.visible = false;
	}
}
