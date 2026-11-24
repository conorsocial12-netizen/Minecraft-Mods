package com.vivid.skins;

import com.vivid.VividClient;
import com.vivid.VividConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;

/** Decides, once per player per frame, which parts are 3D, how thick, and how they handle armour. */
public final class SkinsLogic {
	public enum ArmorMode {
		/** 3D pixels get thinner so they fit inside the armour: still 3D, never poke through. */
		UNDER("Under armour (3D)"),
		/** 3D pixels sit on the outside of the armour (covers the armour with your skin). */
		OVER("On top of armour"),
		/** Armoured parts use the flat vanilla layer. */
		FLAT("Flat under armour");

		public final String label;

		ArmorMode(String label) {
			this.label = label;
		}

		public static ArmorMode parse(String s) {
			try {
				return valueOf(s);
			} catch (Exception e) {
				return UNDER;
			}
		}
	}

	/** Vanilla armour sticks out this far from the body (pixels): helmet/chestplate/boots, and leggings. */
	private static final float OUTER_ARMOR = 1.0f, INNER_ARMOR = 0.5f;
	/** Gap kept between 3D pixels and armour so they never flicker against each other. */
	private static final float GAP = 0.15f;

	private SkinsLogic() {}

	public static boolean isSlim(PlayerSkin skin) {
		return skin.model() == PlayerModelType.SLIM;
	}

	public static int edgeWidthHundredths(VividConfig c) {
		return c.pixelEdges ? (int) Math.round(c.pixelEdgeWidth * 100) : 0;
	}

	public static void compute(Avatar entity, AvatarRenderState s) {
		SkinsState st = (SkinsState) s;
		st.vivid$setMask(0);
		st.vivid$setOutline(false);
		Arrays.fill(st.vivid$getOverArmor(), 0f);
		Arrays.fill(st.vivid$getThickness(), 100);

		VividConfig c = VividConfig.get();
		if (!c.enabled || s.isSpectator || s.isInvisible || s.skin == null) return;
		if (!(entity instanceof Player)) return; // real players only (not mannequins)
		if (c.skinDistance > 0 && s.distanceToCameraSq > c.skinDistance * c.skinDistance) return;
		Minecraft mc = Minecraft.getInstance();
		boolean self = mc.player != null && mc.player.getId() == s.id;
		if (self ? !c.skinsOnSelf : !c.skinsOnOthers) return;

		st.vivid$setOutline(c.pixelEdges || c.bodyRim);
		if (!c.skins3d || VividClient.STANDALONE_SKIN_LAYERS) return;

		int base = self ? (int) Math.round(c.selfThickness) : 100;
		boolean slim = isSlim(s.skin);
		SkinMeshCache.SkinMeshes meshes = SkinMeshCache.get(s.skin.body().texturePath(), slim, base, edgeWidthHundredths(c));
		if (meshes == null) return;

		boolean helmet = !isEmpty(s.headEquipment);
		boolean chestplate = !isEmpty(s.chestEquipment) && !s.chestEquipment.is(Items.ELYTRA);
		boolean leggings = !isEmpty(s.legsEquipment);

		// how far out the armour on each part reaches (0 = none)
		float head = helmet ? OUTER_ARMOR : 0;
		float body = chestplate ? OUTER_ARMOR : leggings ? INNER_ARMOR : 0;
		float arms = chestplate ? OUTER_ARMOR : 0;
		float legs = leggings ? INNER_ARMOR : 0; // boots only cover the feet; their armour box is further out anyway

		ArmorMode mode = ArmorMode.parse(c.armorMode);
		int mask = 0;
		mask |= part(st, meshes, SkinPart.HAT, c.skinHat && s.showHat, head, mode, base);
		mask |= part(st, meshes, SkinPart.JACKET, c.skinJacket && s.showJacket, body, mode, base);
		mask |= part(st, meshes, SkinPart.RIGHT_SLEEVE, c.skinSleeves && s.showRightSleeve, arms, mode, base);
		mask |= part(st, meshes, SkinPart.LEFT_SLEEVE, c.skinSleeves && s.showLeftSleeve, arms, mode, base);
		mask |= part(st, meshes, SkinPart.RIGHT_PANTS, c.skinPants && s.showRightPants, legs, mode, base);
		mask |= part(st, meshes, SkinPart.LEFT_PANTS, c.skinPants && s.showLeftPants, legs, mode, base);
		st.vivid$setMask(mask);
	}

	private static int part(SkinsState st, SkinMeshCache.SkinMeshes meshes, SkinPart part, boolean wanted,
							float armorDepth, ArmorMode mode, int basePercent) {
		if (!wanted || meshes.get(part).isEmpty()) return 0; // nothing to replace
		int i = part.ordinal();
		st.vivid$getThickness()[i] = basePercent;
		if (armorDepth > 0) {
			switch (mode) {
				case FLAT -> {
					return 0;
				}
				case OVER -> st.vivid$getOverArmor()[i] = armorDepth + 0.05f;
				case UNDER -> {
					// squash the pixels so their outside stays just inside the armour
					int fit = (int) Math.floor((armorDepth - GAP) / part.thickness * 100f / 5f) * 5;
					st.vivid$getThickness()[i] = Math.max(10, Math.min(basePercent, fit));
				}
			}
		}
		return part.bit();
	}

	private static boolean isEmpty(ItemStack stack) {
		return stack == null || stack.isEmpty();
	}
}
