package com.vivid.mixin;

import com.vivid.skins.SkinPart;
import com.vivid.skins.SkinsState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/** Remembers how Vivid draws this player's skin this frame. */
@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements SkinsState {
	@Unique private int vivid$mask;
	@Unique private boolean vivid$outline;
	@Unique private final int[] vivid$thickness = new int[SkinPart.values().length];
	@Unique private final float[] vivid$overArmor = new float[SkinPart.values().length];

	@Override
	public int vivid$getMask() {
		return vivid$mask;
	}

	@Override
	public void vivid$setMask(int mask) {
		vivid$mask = mask;
	}

	@Override
	public int[] vivid$getThickness() {
		return vivid$thickness;
	}

	@Override
	public float[] vivid$getOverArmor() {
		return vivid$overArmor;
	}

	@Override
	public boolean vivid$getOutline() {
		return vivid$outline;
	}

	@Override
	public void vivid$setOutline(boolean outline) {
		vivid$outline = outline;
	}
}
