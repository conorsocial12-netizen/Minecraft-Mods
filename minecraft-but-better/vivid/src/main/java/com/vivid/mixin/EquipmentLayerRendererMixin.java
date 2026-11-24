package com.vivid.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vivid.VividConfig;
import com.vivid.skins.MinimalArmor;
import com.vivid.skins.SkinsState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

/**
 * Minimal armour on players: draws the armour with a cut-down texture (crown, shoulder pads, boots)
 * and hides the leggings and trims, unless those are switched back on in the settings.
 */
@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {
	@Shadow @Final private EquipmentAssetManager equipmentAssets;

	@Inject(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
			at = @At("HEAD"), cancellable = true)
	private void vivid$minimal(EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> assetId, Model<?> model,
			Object state, ItemStack stack, PoseStack poseStack, SubmitNodeCollector collector, int light,
			Identifier playerTextureOverride, int outlineColor, int order, CallbackInfo ci) {
		if (!(state instanceof AvatarRenderState avatar) || !((SkinsState) avatar).vivid$getMinimalArmor()) return;
		VividConfig c = VividConfig.get();

		if (layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS) {
			if (!c.minimalLeggings) ci.cancel(); // leggings hidden; otherwise draw them normally
			return;
		}
		if (layerType != EquipmentClientInfo.LayerType.HUMANOID) return;

		ci.cancel();
		EquipmentClientInfo info = this.equipmentAssets.get(assetId);
		List<EquipmentClientInfo.Layer> layers = info.getLayers(layerType);
		if (layers.isEmpty()) return;

		int dyeColor = DyedItemColor.getOrDefault(stack, 0);
		boolean glint = stack.hasFoil();
		int nextOrder = order;
		for (EquipmentClientInfo.Layer layer : layers) {
			int color = vivid$color(layer, dyeColor);
			if (color == 0) continue;
			Identifier texture = layer.usePlayerTexture() && playerTextureOverride != null
					? playerTextureOverride : MinimalArmor.masked(layer.getTextureLocation(layerType));
			RenderType renderType = glint ? RenderTypes.armorCutoutNoCullGlint(texture) : RenderTypes.armorCutoutNoCull(texture);
			vivid$submit(collector, nextOrder++, model, avatar, poseStack, renderType, light, color, outlineColor);
			glint = false;
		}
		// Trims are skipped: they would cover the parts minimal armour hides.
	}

	@Unique
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void vivid$submit(SubmitNodeCollector collector, int order, Model model, AvatarRenderState state,
			PoseStack poseStack, RenderType renderType, int light, int color, int outlineColor) {
		collector.order(order).submitModel(model, state, poseStack, renderType, light, OverlayTexture.NO_OVERLAY, color,
				(UvMapping) null, outlineColor);
	}

	@Unique
	private static int vivid$color(EquipmentClientInfo.Layer layer, int dyeColor) {
		Optional<EquipmentClientInfo.Dyeable> dyeable = layer.dyeable();
		if (dyeable.isPresent()) {
			int undyed = dyeable.get().colorWhenUndyed().map(ARGB::opaque).orElse(0);
			return dyeColor != 0 ? dyeColor : undyed;
		}
		return -1;
	}
}
