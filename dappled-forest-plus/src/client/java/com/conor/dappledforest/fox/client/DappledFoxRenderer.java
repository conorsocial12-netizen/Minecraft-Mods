package com.conor.dappledforest.fox.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.conor.dappledforest.fox.DappledFoxMod;
import com.conor.dappledforest.fox.entity.DappledFoxEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;

public class DappledFoxRenderer extends MobRenderer<DappledFoxEntity, DappledFoxRenderState, DappledFoxModel> {
	private static final Identifier[] TEXTURES = {
			DappledFoxMod.id("textures/entity/dappled_fox.png"),         // orange
			DappledFoxMod.id("textures/entity/dappled_fox_red.png"),     // red
			DappledFoxMod.id("textures/entity/dappled_fox_yellow.png"),  // yellow
	};

	public DappledFoxRenderer(EntityRendererProvider.Context context) {
		super(context, new DappledFoxModel(context.bakeLayer(DappledFoxClient.DAPPLED_FOX_LAYER)), 0.75F);
	}

	@Override
	public DappledFoxRenderState createRenderState() {
		return new DappledFoxRenderState();
	}

	@Override
	public void extractRenderState(DappledFoxEntity fox, DappledFoxRenderState state, float partialTick) {
		super.extractRenderState(fox, state, partialTick);
		state.saddled = !fox.getItemBySlot(EquipmentSlot.SADDLE).isEmpty();
		state.biteAmount = fox.getBiteAmount(partialTick);
		state.variant = fox.getVariant();
	}

	@Override
	protected void scale(DappledFoxRenderState state, PoseStack poseStack) {
		if (state.isBaby) {
			poseStack.scale(0.55F, 0.55F, 0.55F); // kits are a bit bigger than a normal fox
		}
	}

	@Override
	public Identifier getTextureLocation(DappledFoxRenderState state) {
		return TEXTURES[Math.floorMod(state.variant, TEXTURES.length)];
	}
}
