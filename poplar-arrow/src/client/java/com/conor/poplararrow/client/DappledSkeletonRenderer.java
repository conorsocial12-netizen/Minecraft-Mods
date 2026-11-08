package com.conor.poplararrow.client;

import com.conor.poplararrow.PoplarArrowMod;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.layers.SkeletonClothingLayer;
import net.minecraft.resources.Identifier;

/**
 * Draws the normal vanilla skeleton, then a second, slightly bigger skeleton shell on top
 * (the same shape the Stray uses for its clothes) textured with red/orange/yellow poplar leaves.
 */
public class DappledSkeletonRenderer extends SkeletonRenderer {
	private static final Identifier LEAVES =
			PoplarArrowMod.id("textures/entity/dappled_skeleton_leaves.png");

	public DappledSkeletonRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.addLayer(new SkeletonClothingLayer<>(this, context.getModelSet(), ModelLayers.STRAY_OUTER_LAYER, LEAVES));
	}
}
