package com.conor.dappledforest.fox.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Horse-sized fox. Texture is 128x64 (assets/dappledfox/textures/entity/dappled_fox.png).
 * Model space: y points down, the ground is at y = 24, the head faces -z.
 */
public class DappledFoxModel extends EntityModel<DappledFoxRenderState> {
	static final float NECK_TILT = 0.6F;
	static final float TAIL_TILT = 0.95F;

	private final ModelPart body;
	private final ModelPart saddle;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart tail;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;

	public DappledFoxModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.saddle = this.body.getChild("saddle");
		this.neck = root.getChild("neck");
		this.head = this.neck.getChild("head");
		this.tail = root.getChild("tail");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		PartDefinition body = root.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -7.0F, -11.0F, 9, 9, 22),
				PartPose.offset(0.0F, 11.0F, 2.0F));

		// Saddle: hidden unless the fox wears one.
		body.addOrReplaceChild("saddle",
				CubeListBuilder.create()
						.texOffs(36, 32).addBox(-4.0F, -8.0F, -4.0F, 8, 1, 9)    // seat
						.texOffs(72, 32).addBox(-5.0F, -7.0F, -1.0F, 1, 6, 3)    // left flap
						.texOffs(72, 32).addBox(4.0F, -7.0F, -1.0F, 1, 6, 3)     // right flap
						.texOffs(82, 32).addBox(-1.0F, -10.0F, -4.0F, 2, 2, 1),  // pommel
				PartPose.ZERO);

		PartDefinition neck = root.addOrReplaceChild("neck",
				CubeListBuilder.create().texOffs(64, 0).addBox(-2.5F, -8.0F, -2.5F, 5, 9, 5),
				PartPose.offsetAndRotation(0.0F, 6.0F, -8.0F, NECK_TILT, 0.0F, 0.0F));

		neck.addOrReplaceChild("head",
				CubeListBuilder.create()
						.texOffs(86, 0).addBox(-3.5F, -4.0F, -4.0F, 7, 6, 7)     // skull
						.texOffs(64, 16).addBox(-1.5F, -1.0F, -8.0F, 3, 3, 4)    // snout
						.texOffs(80, 16).addBox(-3.5F, -7.0F, -1.0F, 2, 3, 1)    // left ear
						.texOffs(80, 16).addBox(1.5F, -7.0F, -1.0F, 2, 3, 1),    // right ear
				PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -NECK_TILT, 0.0F, 0.0F));

		root.addOrReplaceChild("tail",
				CubeListBuilder.create().texOffs(14, 32).addBox(-2.5F, 0.0F, -2.5F, 5, 13, 5),
				PartPose.offsetAndRotation(0.0F, 6.0F, 12.5F, TAIL_TILT, 0.0F, 0.0F));

		CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 32).addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3);
		root.addOrReplaceChild("right_front_leg", leg, PartPose.offset(-2.6F, 12.0F, -6.0F));
		root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(2.6F, 12.0F, -6.0F));
		root.addOrReplaceChild("right_hind_leg", leg, PartPose.offset(-2.6F, 12.0F, 10.0F));
		root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(2.6F, 12.0F, 10.0F));

		return LayerDefinition.create(mesh, 128, 64);
	}

	@Override
	public void setupAnim(DappledFoxRenderState state) {
		super.setupAnim(state); // resets every part to its default pose

		// Look around
		float yaw = Mth.clamp(state.yRot, -60.0F, 60.0F) * Mth.DEG_TO_RAD;
		float pitch = state.xRot * Mth.DEG_TO_RAD;
		this.neck.yRot += yaw * 0.6F;
		this.head.yRot += yaw * 0.4F;
		this.head.xRot += pitch * 0.6F;

		// Trot: diagonal leg pairs swing together
		float pos = state.walkAnimationPos;
		float amount = Math.min(state.walkAnimationSpeed, 1.0F);
		float swing = Mth.cos(pos * 0.6662F) * 1.1F * amount;
		this.rightFrontLeg.xRot = swing;
		this.leftHindLeg.xRot = swing;
		this.leftFrontLeg.xRot = -swing;
		this.rightHindLeg.xRot = -swing;

		// Bushy tail sways idly and bounces when running
		this.tail.yRot = Mth.sin(state.ageInTicks * 0.1F) * 0.15F;
		this.tail.xRot += Mth.cos(pos * 0.6662F) * 0.25F * amount - amount * 0.35F;

		// Bite: neck lunges forward and down, head snaps
		float bite = state.biteAmount;
		if (bite > 0.0F) {
			this.neck.xRot += bite * 0.7F;
			this.neck.z -= bite * 2.0F;
			this.head.xRot -= bite * 0.35F;
		}

		this.saddle.visible = state.saddled;
	}
}
