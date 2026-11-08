package com.conor.dappledforest.squirrel.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * A small red squirrel with a big bushy tail curling up over its back.
 * Texture is 64x32. Model space: y points down, the ground is at y = 24, the head faces -z.
 */
public class SquirrelModel extends EntityModel<SquirrelRenderState> {
	final ModelPart body;
	final ModelPart head;
	private final ModelPart tail;
	private final ModelPart tailTip;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;

	public SquirrelModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.head = root.getChild("head");
		this.tail = root.getChild("tail");
		this.tailTip = this.tail.getChild("tail_tip");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		root.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -3.5F, 4, 4, 7),
				PartPose.offset(0.0F, 20.0F, 0.5F));

		root.addOrReplaceChild("head",
				CubeListBuilder.create()
						.texOffs(24, 0).addBox(-2.0F, -2.5F, -3.5F, 4, 4, 4)    // skull
						.texOffs(44, 0).addBox(-1.0F, -0.5F, -4.5F, 2, 2, 1)    // muzzle
						.texOffs(40, 0).addBox(-2.0F, -4.5F, -1.5F, 1, 2, 1)    // left ear
						.texOffs(40, 0).addBox(1.0F, -4.5F, -1.5F, 1, 2, 1),    // right ear
				PartPose.offset(0.0F, 18.0F, -3.0F));

		PartDefinition tail = root.addOrReplaceChild("tail",
				CubeListBuilder.create().texOffs(0, 12).addBox(-2.5F, -9.0F, -1.0F, 5, 9, 4),
				PartPose.offsetAndRotation(0.0F, 21.0F, 4.0F, -0.25F, 0.0F, 0.0F));
		tail.addOrReplaceChild("tail_tip",
				CubeListBuilder.create().texOffs(18, 12).addBox(-2.5F, -3.0F, -3.0F, 5, 3, 4),
				PartPose.offsetAndRotation(0.0F, -9.0F, 0.0F, -0.7F, 0.0F, 0.0F));

		CubeListBuilder frontLeg = CubeListBuilder.create().texOffs(48, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
		CubeListBuilder hindLeg = CubeListBuilder.create().texOffs(52, 0).addBox(-0.5F, 0.0F, -1.0F, 1, 2, 2);
		root.addOrReplaceChild("right_front_leg", frontLeg, PartPose.offset(-1.4F, 22.0F, -2.0F));
		root.addOrReplaceChild("left_front_leg", frontLeg, PartPose.offset(1.4F, 22.0F, -2.0F));
		root.addOrReplaceChild("right_hind_leg", hindLeg, PartPose.offset(-1.5F, 22.0F, 2.5F));
		root.addOrReplaceChild("left_hind_leg", hindLeg, PartPose.offset(1.5F, 22.0F, 2.5F));

		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public void setupAnim(SquirrelRenderState state) {
		super.setupAnim(state); // resets every part to its default pose

		this.head.yRot = Mth.clamp(state.yRot, -50.0F, 50.0F) * Mth.DEG_TO_RAD;
		this.head.xRot = state.xRot * Mth.DEG_TO_RAD;

		// Quick scurrying: front pair and hind pair hop together, like a real squirrel
		float pos = state.walkAnimationPos;
		float amount = Math.min(state.walkAnimationSpeed, 1.0F);
		float hop = Mth.cos(pos * 1.2F) * 1.3F * amount;
		this.rightFrontLeg.xRot = hop;
		this.leftFrontLeg.xRot = hop;
		this.rightHindLeg.xRot = -hop;
		this.leftHindLeg.xRot = -hop;

		// Tail: idle flick, and it streams out behind when running
		float flick = Mth.sin(state.ageInTicks * 0.15F) * 0.08F;
		this.tail.xRot += flick - amount * 0.6F;
		this.tailTip.xRot += Mth.sin(state.ageInTicks * 0.15F + 1.0F) * 0.12F - amount * 0.3F;
		this.tail.zRot = Mth.sin(state.ageInTicks * 0.07F) * 0.06F;

		if (state.isSitting || state.isPerched) {
			// Sit up on the hind legs with the front paws tucked in
			this.body.xRot = -0.6F;
			this.body.y += 0.5F;
			this.head.y -= 1.5F;
			this.head.z += 1.0F;
			this.rightFrontLeg.y -= 1.5F;
			this.leftFrontLeg.y -= 1.5F;
			this.rightFrontLeg.z += 0.8F;
			this.leftFrontLeg.z += 0.8F;
			this.rightFrontLeg.xRot = -0.9F;
			this.leftFrontLeg.xRot = -0.9F;
			this.tail.xRot = -0.15F;
		} else if (state.isClimbing) {
			// Stretched out, claws forward
			this.rightFrontLeg.xRot = -0.8F + hop * 0.5F;
			this.leftFrontLeg.xRot = -0.8F - hop * 0.5F;
		}
	}
}
