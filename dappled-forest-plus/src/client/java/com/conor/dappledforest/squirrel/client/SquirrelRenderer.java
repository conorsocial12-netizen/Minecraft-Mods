package com.conor.dappledforest.squirrel.client;

import com.conor.dappledforest.DappledForestPlus;
import com.conor.dappledforest.squirrel.AcornSquirrel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class SquirrelRenderer extends MobRenderer<AcornSquirrel, SquirrelRenderState, SquirrelModel> {
	private static final Identifier TEXTURE = DappledForestPlus.id("textures/entity/acorn_squirrel.png");
	private final ItemModelResolver items;

	public SquirrelRenderer(EntityRendererProvider.Context context) {
		super(context, new SquirrelModel(context.bakeLayer(SquirrelClient.SQUIRREL_LAYER)), 0.25F);
		this.items = context.getItemModelResolver();
		this.addLayer(new SquirrelHeldItemLayer(this));
	}

	@Override
	public SquirrelRenderState createRenderState() {
		return new SquirrelRenderState();
	}

	@Override
	public void extractRenderState(AcornSquirrel squirrel, SquirrelRenderState state, float partialTick) {
		super.extractRenderState(squirrel, state, partialTick);
		HoldingEntityRenderState.extractHoldingEntityRenderState(squirrel, state, this.items);
		state.isSitting = squirrel.isInSittingPose() || squirrel.isOrderedToSit();
		state.isPerched = squirrel.getVehicle() instanceof Player;
		state.isClimbing = squirrel.isClimbing();
	}

	@Override
	protected void scale(SquirrelRenderState state, PoseStack poseStack) {
		if (state.isBaby) {
			poseStack.scale(0.6F, 0.6F, 0.6F);
		}
	}

	@Override
	public Identifier getTextureLocation(SquirrelRenderState state) {
		return TEXTURE;
	}
}
