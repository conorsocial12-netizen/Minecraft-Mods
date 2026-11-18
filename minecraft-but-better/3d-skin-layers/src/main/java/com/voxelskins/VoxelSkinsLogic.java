package com.voxelskins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Decides, once per player per frame, which parts are drawn in 3D. */
public final class VoxelSkinsLogic {
	private VoxelSkinsLogic() {}

	public static int computeMask(AvatarRenderState s) {
		VoxelSkinsConfig c = VoxelSkinsConfig.get();
		if (!c.enabled || s.isSpectator || s.isInvisible || s.skin == null) return 0;
		if (c.renderDistance > 0 && s.distanceToCameraSq > c.renderDistance * c.renderDistance) return 0;
		if (!c.self) {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player != null && mc.player.getId() == s.id) return 0;
		}

		SkinMeshCache.SkinMeshes meshes = SkinMeshCache.get(s.skin.body().texturePath(), isSlim(s.skin));
		if (meshes == null) return 0;

		boolean head = !c.flatUnderArmor || isEmpty(s.headEquipment);
		boolean chest = !c.flatUnderArmor || isEmpty(s.chestEquipment) || s.chestEquipment.is(Items.ELYTRA);
		boolean legs = !c.flatUnderArmor || isEmpty(s.legsEquipment);

		int mask = 0;
		mask |= bit(meshes, SkinPart.HAT, c.hat && head && s.showHat);
		mask |= bit(meshes, SkinPart.JACKET, c.jacket && chest && s.showJacket);
		mask |= bit(meshes, SkinPart.RIGHT_SLEEVE, c.sleeves && chest && s.showRightSleeve);
		mask |= bit(meshes, SkinPart.LEFT_SLEEVE, c.sleeves && chest && s.showLeftSleeve);
		mask |= bit(meshes, SkinPart.RIGHT_PANTS, c.pants && legs && s.showRightPants);
		mask |= bit(meshes, SkinPart.LEFT_PANTS, c.pants && legs && s.showLeftPants);
		return mask;
	}

	public static boolean isSlim(PlayerSkin skin) {
		return skin.model() == PlayerModelType.SLIM;
	}

	private static int bit(SkinMeshCache.SkinMeshes meshes, SkinPart part, boolean wanted) {
		// a part with no overlay pixels at all keeps the (invisible) vanilla layer; nothing to replace
		return wanted && !meshes.get(part).isEmpty() ? part.bit() : 0;
	}

	private static boolean isEmpty(ItemStack stack) {
		return stack == null || stack.isEmpty();
	}
}
