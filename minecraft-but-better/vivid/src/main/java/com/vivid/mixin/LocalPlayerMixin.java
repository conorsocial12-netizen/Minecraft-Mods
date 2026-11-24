package com.vivid.mixin;

import com.vivid.HitParticles;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fancier critical-hit particles on top of the vanilla ones. */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
	@Inject(method = "crit", at = @At("TAIL"))
	private void vivid$crit(Entity entity, CallbackInfo ci) {
		HitParticles.onCrit(entity, false);
	}

	@Inject(method = "magicCrit", at = @At("TAIL"))
	private void vivid$magicCrit(Entity entity, CallbackInfo ci) {
		HitParticles.onCrit(entity, true);
	}
}
