package com.vivid.skins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vivid.VividConfig;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Draws Vivid's 3D skin pixels and the light outlines (pixel edges + body rim),
 * following the head/body/arms/legs as they move.
 */
public class VividSkinLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
	private static final Identifier WHITE = Identifier.fromNamespaceAndPath("vivid", "textures/misc/white.png");
	private static final int FULL_BRIGHT = 0xF000F0;
	private static final Map<Integer, PartMesh> RIM_CACHE = new HashMap<>();

	public VividSkinLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, AvatarRenderState state, float yRot, float xRot) {
		SkinsState st = (SkinsState) state;
		int mask = st.vivid$getMask();
		boolean outline = st.vivid$getOutline();
		if ((mask == 0 && !outline) || state.skin == null) return;

		VividConfig c = VividConfig.get();
		Identifier texture = state.skin.body().texturePath();
		boolean slim = SkinsLogic.isSlim(state.skin);
		int edgeW = SkinsLogic.edgeWidthHundredths(c);
		int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
		PlayerModel model = getParentModel();

		RenderType skinType = RenderTypes.entityTranslucent(texture);
		RenderType whiteType = RenderTypes.entityTranslucent(WHITE);
		int rgb = VividConfig.parseHex(c.rimColor, 0xFFFFFF);
		int edgeColor = argb(c.pixelEdgeStrength, rgb);
		int rimColor = argb(c.bodyRimStrength, rgb);
		int outlineLight = c.rimGlow ? FULL_BRIGHT : light;

		for (SkinPart part : SkinPart.values()) {
			boolean is3d = (mask & part.bit()) != 0;
			boolean rim = outline && c.bodyRim && boneVisible(model, part);
			if (!is3d && !rim) continue;
			ModelPart bone = bone(model, part);

			poseStack.pushPose();
			model.root().translateAndRotate(poseStack);
			bone.translateAndRotate(poseStack);

			if (rim) {
				PartMesh rimMesh = rimMesh(part, slim, (int) Math.round(c.bodyRimWidth * 100));
				collector.submitCustomGeometry(poseStack, whiteType, (pose, buffer) -> rimMesh.emit(pose, buffer, outlineLight, overlay, rimColor));
			}

			if (is3d) {
				int i = part.ordinal();
				SkinMeshCache.SkinMeshes meshes = SkinMeshCache.get(texture, slim, st.vivid$getThickness()[i], edgeW);
				if (meshes != null) {
					float over = st.vivid$getOverArmor()[i];
					if (over > 0) onTopOfArmor(poseStack, part, slim, over);
					PartMesh mesh = meshes.get(part);
					collector.submitCustomGeometry(poseStack, skinType, (pose, buffer) -> mesh.emit(pose, buffer, light, overlay));
					PartMesh edges = meshes.edges(part);
					if (outline && c.pixelEdges && !edges.isEmpty()) {
						collector.submitCustomGeometry(poseStack, whiteType, (pose, buffer) -> edges.emit(pose, buffer, outlineLight, overlay, edgeColor));
					}
				}
			}
			poseStack.popPose();
		}
	}

	private static int argb(double strength, int rgb) {
		int a = (int) Math.round(Math.max(0, Math.min(1, strength)) * 255);
		return (a << 24) | (rgb & 0xFFFFFF);
	}

	private static ModelPart bone(PlayerModel model, SkinPart part) {
		return switch (part) {
			case HAT -> model.head;
			case JACKET -> model.body;
			case RIGHT_SLEEVE -> model.rightArm;
			case LEFT_SLEEVE -> model.leftArm;
			case RIGHT_PANTS -> model.rightLeg;
			case LEFT_PANTS -> model.leftLeg;
		};
	}

	private static boolean boneVisible(PlayerModel model, SkinPart part) {
		return bone(model, part).visible;
	}

	/** Rim strips round the base box of one body part (same box as the vanilla model). */
	private static PartMesh rimMesh(SkinPart part, boolean slim, int widthHundredths) {
		int key = (widthHundredths << 4) | (part.ordinal() << 1) | (slim ? 1 : 0);
		return RIM_CACHE.computeIfAbsent(key, k -> MeshBuilder.boxRim(part.minX(slim), part.y, part.z,
				part.width(slim), part.h, part.d, widthHundredths / 100f));
	}

	/**
	 * Stretch the layer so it sits on the outside of the armour instead of inside it.
	 * The body box is mapped onto the armour box (the same box grown by `depth` on every side;
	 * armour arms are always 4 wide, even on slim skins).
	 */
	private static void onTopOfArmor(PoseStack poseStack, SkinPart part, boolean slim, float depth) {
		float w = part.width(slim), ax = part.x - depth, aw = part.w + 2 * depth;
		float sx = aw / w, sy = (part.h + 2 * depth) / part.h, sz = (part.d + 2 * depth) / part.d;
		poseStack.translate(ax / 16f, (part.y - depth) / 16f, (part.z - depth) / 16f);
		poseStack.scale(sx, sy, sz);
		poseStack.translate(-part.minX(slim) / 16f, -part.y / 16f, -part.z / 16f);
	}
}
