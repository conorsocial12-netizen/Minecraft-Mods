package com.voxelskins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;

/** Decides, once per player per frame, which parts are drawn in 3D (and whether they sit on armor). */
public final class VoxelSkinsLogic {
	/** Vanilla armor sticks out this far from the body: helmet/chestplate/boots, and leggings. */
	private static final float OUTER_ARMOR = 1.0f, INNER_ARMOR = 0.5f;
	/** A tiny extra gap so the pixels never flicker against the armor surface. */
	private static final float GAP = 0.05f;

	private VoxelSkinsLogic() {}

	public static int computeMask(AvatarRenderState s) {
		VoxelSkinsState vs = (VoxelSkinsState) s;
		float[] armor = vs.voxelskins$getArmor();
		Arrays.fill(armor, 0f);
		vs.voxelskins$setThickness(100);

		VoxelSkinsConfig c = VoxelSkinsConfig.get();
		if (!c.enabled || s.isSpectator || s.isInvisible || s.skin == null) return 0;
		if (c.renderDistance > 0 && s.distanceToCameraSq > c.renderDistance * c.renderDistance) return 0;
		Minecraft mc = Minecraft.getInstance();
		boolean self = mc.player != null && mc.player.getId() == s.id;
		if (self && !c.self) return 0;
		int thickness = self ? c.selfThickness : 100;
		vs.voxelskins$setThickness(thickness);

		SkinMeshCache.SkinMeshes meshes = SkinMeshCache.get(s.skin.body().texturePath(), isSlim(s.skin), thickness);
		if (meshes == null) return 0;

		boolean helmet = !isEmpty(s.headEquipment);
		boolean chestplate = !isEmpty(s.chestEquipment) && !s.chestEquipment.is(Items.ELYTRA);
		boolean leggings = !isEmpty(s.legsEquipment);

		// how far out the armor on each part reaches (0 = none)
		float head = helmet ? OUTER_ARMOR : 0;
		float body = chestplate ? OUTER_ARMOR : leggings ? INNER_ARMOR : 0;
		float arms = chestplate ? OUTER_ARMOR : 0;
		float legs = leggings ? INNER_ARMOR : 0; // boots alone only cover the feet; pants stay on the leg above them

		int mask = 0;
		mask |= part(meshes, armor, SkinPart.HAT, c.hat && s.showHat, head, c.overArmor);
		mask |= part(meshes, armor, SkinPart.JACKET, c.jacket && s.showJacket, body, c.overArmor);
		mask |= part(meshes, armor, SkinPart.RIGHT_SLEEVE, c.sleeves && s.showRightSleeve, arms, c.overArmor);
		mask |= part(meshes, armor, SkinPart.LEFT_SLEEVE, c.sleeves && s.showLeftSleeve, arms, c.overArmor);
		mask |= part(meshes, armor, SkinPart.RIGHT_PANTS, c.pants && s.showRightPants, legs, c.overArmor);
		mask |= part(meshes, armor, SkinPart.LEFT_PANTS, c.pants && s.showLeftPants, legs, c.overArmor);
		return mask;
	}

	public static boolean isSlim(PlayerSkin skin) {
		return skin.model() == PlayerModelType.SLIM;
	}

	private static int part(SkinMeshCache.SkinMeshes meshes, float[] armor, SkinPart part, boolean wanted,
							float armorDepth, boolean overArmor) {
		if (!wanted || meshes.get(part).isEmpty()) return 0; // nothing to replace
		if (armorDepth > 0) {
			if (!overArmor) return 0; // old behaviour: flat layer under armor
			armor[part.ordinal()] = armorDepth + GAP;
		}
		return part.bit();
	}

	private static boolean isEmpty(ItemStack stack) {
		return stack == null || stack.isEmpty();
	}
}
