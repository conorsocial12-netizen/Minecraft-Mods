package com.conor.dappledforest.squirrel.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Draws whatever the squirrel is fetching in its mouth (same approach as the vanilla fox). */
public class SquirrelHeldItemLayer extends RenderLayer<SquirrelRenderState, SquirrelModel> {
	public SquirrelHeldItemLayer(RenderLayerParent<SquirrelRenderState, SquirrelModel> parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, SquirrelRenderState state,
			float yRot, float xRot) {
		if (state.heldItem.isEmpty()) return;
		var head = this.getParentModel().head;
		poseStack.pushPose();
		poseStack.translate(head.x / 16.0F, head.y / 16.0F, head.z / 16.0F);
		poseStack.rotate(Axis.YP, head.yRot);
		poseStack.rotate(Axis.XP, head.xRot);
		poseStack.translate(0.0F, 0.08F, -0.3F);  // in front of the muzzle
		poseStack.rotateDegrees(Axis.XP, 90.0F);
		poseStack.scale(0.5F, 0.5F, 0.5F);
		state.heldItem.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();
	}
}
