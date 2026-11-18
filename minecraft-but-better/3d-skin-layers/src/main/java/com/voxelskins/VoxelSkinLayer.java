package com.voxelskins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/** Draws the 3D pixels, following the head/body/arms/legs as they move. */
public class VoxelSkinLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
	public VoxelSkinLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, AvatarRenderState state, float yRot, float xRot) {
		int mask = ((VoxelSkinsState) state).voxelskins$getMask();
		if (mask == 0 || state.skin == null) return;

		Identifier texture = state.skin.body().texturePath();
		VoxelSkinsState vs = (VoxelSkinsState) state;
		boolean slim = VoxelSkinsLogic.isSlim(state.skin);
		SkinMeshCache.SkinMeshes meshes = SkinMeshCache.get(texture, slim, vs.voxelskins$getThickness());
		float[] armor = vs.voxelskins$getArmor();
		if (meshes == null) return;

		RenderType renderType = RenderTypes.entityTranslucent(texture);
		int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
		PlayerModel model = getParentModel();

		for (SkinPart part : SkinPart.values()) {
			if ((mask & part.bit()) == 0) continue;
			PartMesh mesh = meshes.get(part);
			ModelPart bone = switch (part) {
				case HAT -> model.head;
				case JACKET -> model.body;
				case RIGHT_SLEEVE -> model.rightArm;
				case LEFT_SLEEVE -> model.leftArm;
				case RIGHT_PANTS -> model.rightLeg;
				case LEFT_PANTS -> model.leftLeg;
			};
			poseStack.pushPose();
			model.root().translateAndRotate(poseStack);
			bone.translateAndRotate(poseStack);
			if (armor[part.ordinal()] > 0) onTopOfArmor(poseStack, part, slim, armor[part.ordinal()]);
			collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> mesh.emit(pose, buffer, light, overlay));
			poseStack.popPose();
		}
	}

	/**
	 * Stretch the layer so it sits on the outside of the armor instead of inside it.
	 * The body box is mapped onto the armor box (the same box grown by `depth` on every side;
	 * armor arms are always 4 wide, even on slim skins).
	 */
	private static void onTopOfArmor(PoseStack poseStack, SkinPart part, boolean slim, float depth) {
		float w = part.width(slim), ax = part.x - depth, aw = part.w + 2 * depth;
		float sx = aw / w, sy = (part.h + 2 * depth) / part.h, sz = (part.d + 2 * depth) / part.d;
		poseStack.translate(ax / 16f, (part.y - depth) / 16f, (part.z - depth) / 16f);
		poseStack.scale(sx, sy, sz);
		poseStack.translate(-part.minX(slim) / 16f, -part.y / 16f, -part.z / 16f);
	}
}
