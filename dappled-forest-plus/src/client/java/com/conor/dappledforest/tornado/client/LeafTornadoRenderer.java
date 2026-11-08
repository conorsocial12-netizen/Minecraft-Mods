package com.conor.dappledforest.tornado.client;

import com.conor.dappledforest.leafpiles.ModBlocks;
import com.conor.dappledforest.tornado.LeafTornado;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Draws the tornado as a funnel of tumbling leaf clumps (the red / orange / yellow Leaf Pile models)
 * spinning around the centre: narrow and fast at the bottom, wide and slower at the top.
 * The entity also spawns poplar leaf particles around it for extra swirl.
 */
public class LeafTornadoRenderer extends EntityRenderer<LeafTornado, LeafTornadoRenderer.State> {
	private static final int RINGS = 14;
	private static final int PER_RING = 6;

	private final ItemModelResolver items;

	public LeafTornadoRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.items = context.getItemModelResolver();
	}

	public static class State extends EntityRenderState {
		public final ItemStackRenderState[] leaves = {
				new ItemStackRenderState(), new ItemStackRenderState(), new ItemStackRenderState()};
		public float strength;
		public int seed;
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(LeafTornado tornado, State state, float partialTick) {
		super.extractRenderState(tornado, state, partialTick);
		for (int i = 0; i < 3; i++) {
			this.items.updateForNonLiving(state.leaves[i], new ItemStack(ModBlocks.ALL.get(i)), ItemDisplayContext.GROUND, tornado);
		}
		state.strength = tornado.getStrength(partialTick);
		state.seed = tornado.getId();
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		super.submit(state, poseStack, collector, camera);
		if (state.strength <= 0.01F) return;
		float t = state.ageInTicks;

		for (int ring = 0; ring < RINGS; ring++) {
			float frac = ring / (float) (RINGS - 1);                 // 0 at the bottom, 1 at the top
			float height = frac * LeafTornado.HEIGHT * state.strength;
			float radius = (0.35F + frac * frac * 2.2F) * (0.4F + 0.6F * state.strength);
			float speed = 0.35F - frac * 0.18F;                      // faster near the ground
			float wobbleX = Mth.sin(t * 0.05F + frac * 3.0F) * 0.35F * frac;
			float wobbleZ = Mth.cos(t * 0.04F + frac * 2.0F) * 0.35F * frac;

			for (int k = 0; k < PER_RING; k++) {
				int index = ring * PER_RING + k;
				float angle = t * speed + k * (Mth.TWO_PI / PER_RING) + ring * 0.7F + (state.seed % 7);
				float jitter = Mth.sin(index * 12.9898F) * 0.25F;
				float x = Mth.cos(angle) * (radius + jitter) + wobbleX;
				float z = Mth.sin(angle) * (radius + jitter) + wobbleZ;
				float y = height + Mth.sin(t * 0.3F + index) * 0.15F;

				poseStack.pushPose();
				poseStack.translate(x, y, z);
				poseStack.rotate(Axis.YP, -angle);
				poseStack.rotate(Axis.XP, t * 0.25F + index);
				poseStack.rotate(Axis.ZP, t * 0.17F + index * 0.5F);
				float size = (0.55F + 0.25F * frac) * state.strength;
				poseStack.scale(size, size, size);
				state.leaves[index % 3].submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
				poseStack.popPose();
			}
		}
	}
}
