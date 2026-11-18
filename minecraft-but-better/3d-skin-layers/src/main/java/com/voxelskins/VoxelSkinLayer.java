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
		SkinMeshCache.SkinMeshes meshes = SkinMeshCache.get(texture, VoxelSkinsLogic.isSlim(state.skin));
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
			collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> mesh.emit(pose, buffer, light, overlay));
			poseStack.popPose();
		}
	}
}
