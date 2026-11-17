package com.skinglow.mixin;

import com.skinglow.GlowLogic;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Supplies our color as the outline color instead of the vanilla team color. */
@Mixin(Entity.class)
public class EntityMixin {
	@Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
	private void skinglow$color(CallbackInfoReturnable<Integer> cir) {
		Entity self = (Entity) (Object) this;
		if (GlowLogic.shouldGlow(self) && GlowLogic.overridesTeamColor(self)) {
			cir.setReturnValue(GlowLogic.color(self));
		}
	}
}
