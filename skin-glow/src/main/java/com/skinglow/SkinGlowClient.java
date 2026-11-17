package com.skinglow;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;

public class SkinGlowClient implements ClientModInitializer {
	private static boolean openMenuNextTick = false;

	@Override
	public void onInitializeClient() {
		SkinGlowConfig.load();

		// The chat screen closes right after a command runs, so open the menu on the next tick.
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (openMenuNextTick) {
				openMenuNextTick = false;
				client.setScreen(new SkinGlowScreen(null));
			}
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(ClientCommands.literal("skinglow")
						.executes(ctx -> {
							openMenuNextTick = true;
							return 1;
						})
						.then(ClientCommands.literal("toggle").executes(ctx -> {
							SkinGlowConfig c = SkinGlowConfig.get();
							c.enabled = !c.enabled;
							SkinGlowConfig.save();
							ctx.getSource().sendFeedback(Component.literal("Skin Glow " + (c.enabled ? "ON" : "OFF")));
							return 1;
						}))
						.then(ClientCommands.literal("reset").executes(ctx -> {
							SkinGlowConfig.reset();
							ctx.getSource().sendFeedback(Component.literal("Skin Glow reset to defaults"));
							return 1;
						}))));
	}
}
