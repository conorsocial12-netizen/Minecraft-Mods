package com.vivid.skins;

/** Added onto AvatarRenderState by a mixin: how Vivid draws this player's skin this frame. */
public interface SkinsState {
	/** Which parts are drawn as 3D pixels (SkinPart bits). */
	int vivid$getMask();

	void vivid$setMask(int mask);

	/** Thickness of the 3D pixels for each part (percent of normal), indexed by SkinPart.ordinal(). */
	int[] vivid$getThickness();

	/** For "on top of armour": how far (pixels) armour on each part sticks out; 0 = draw normally. */
	float[] vivid$getOverArmor();

	/** Draw the pixel-edge / body-rim outlines on this player. */
	boolean vivid$getOutline();

	void vivid$setOutline(boolean outline);
}
