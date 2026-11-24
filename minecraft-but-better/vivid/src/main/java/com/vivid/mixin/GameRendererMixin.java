package com.vivid.mixin;

import com.vivid.VividClient;
import com.vivid.VividConfig;
import com.vivid.VividState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/** Adds Vivid's colour-grading post effect to the list of effects the game applies each frame. */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow @Final private Minecraft minecraft;
	@Shadow @Final private List<Identifier> requestedPostEffects;

	@Inject(method = "update", at = @At("TAIL"))
	private void vivid$requestEffect(DeltaTracker deltaTracker, CallbackInfo ci) {
		VividState.update(this.minecraft);
		if (VividConfig.get().enabled) {
			this.requestedPostEffects.add(VividClient.GRADE_EFFECT);
		}
	}
}
