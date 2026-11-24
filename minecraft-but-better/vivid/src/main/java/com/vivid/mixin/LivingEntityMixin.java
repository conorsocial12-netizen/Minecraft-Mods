package com.vivid.mixin;

import com.vivid.HitParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Mob-specific hit and death particles (client side only). */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "handleDamageEvent", at = @At("TAIL"))
	private void vivid$hurtParticles(DamageSource source, CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self.level().isClientSide()) HitParticles.onHurt(self, source);
	}

	@Inject(method = "handleEntityEvent", at = @At("HEAD"))
	private void vivid$deathParticles(byte id, CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (id == 3 && self.level().isClientSide()) HitParticles.onDeath(self);
	}
}
