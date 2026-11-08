package com.conor.dappledforest.arrow.client;

import com.conor.dappledforest.arrow.PoplarArrow;
import com.conor.dappledforest.arrow.PoplarArrowMod;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

public class PoplarArrowRenderer extends ArrowRenderer<PoplarArrow, ArrowRenderState> {
	private static final Identifier TEXTURE =
			PoplarArrowMod.id("textures/entity/projectiles/poplar_arrow.png");

	public PoplarArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ArrowRenderState createRenderState() {
		return new ArrowRenderState();
	}

	@Override
	protected Identifier getTextureLocation(ArrowRenderState state) {
		return TEXTURE;
	}
}
