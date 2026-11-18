package com.voxelskins;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.network.chat.Component;

public class VoxelSkinsClient implements ClientModInitializer {
	private static boolean openMenuNextTick = false;

	@Override
	public void onInitializeClient() {
		VoxelSkinsConfig.load();

		// Add the 3D layer to every player-shaped renderer (players and mannequins).
		LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, renderer, helper) -> {
			if (renderer instanceof AvatarRenderer<?> avatarRenderer) {
				helper.register(new VoxelSkinLayer(avatarRenderer));
			}
		});

		// The chat screen closes right after a command runs, so open the menu on the next tick.
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (openMenuNextTick) {
				openMenuNextTick = false;
				client.gui.setScreen(new VoxelSkinsScreen(null));
			}
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(ClientCommands.literal("skinlayers")
						.executes(ctx -> {
							openMenuNextTick = true;
							return 1;
						})
						.then(ClientCommands.literal("toggle").executes(ctx -> {
							VoxelSkinsConfig c = VoxelSkinsConfig.get();
							c.enabled = !c.enabled;
							VoxelSkinsConfig.save();
							ctx.getSource().sendFeedback(Component.literal("3D Skin Layers " + (c.enabled ? "ON" : "OFF")));
							return 1;
						}))
						.then(ClientCommands.literal("reload").executes(ctx -> {
							SkinMeshCache.clear();
							ctx.getSource().sendFeedback(Component.literal("3D Skin Layers: skins rebuilt"));
							return 1;
						}))
						.then(ClientCommands.literal("reset").executes(ctx -> {
							VoxelSkinsConfig.reset();
							ctx.getSource().sendFeedback(Component.literal("3D Skin Layers reset to defaults"));
							return 1;
						}))));
	}
}
