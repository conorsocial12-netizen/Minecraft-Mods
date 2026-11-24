package com.vivid;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class VividClient implements ClientModInitializer {
	public static final String MOD_ID = "vivid";
	public static final Identifier GRADE_EFFECT = Identifier.fromNamespaceAndPath(MOD_ID, "grade");

	private static KeyMapping openSettingsKey;
	private static boolean openMenuNextTick = false;

	@Override
	public void onInitializeClient() {
		VividConfig.load();

		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "vivid"));
		openSettingsKey = KeyMappingHelper.registerKeyMapping(
				new KeyMapping("key.vivid.settings", InputConstants.KEY_V, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// The chat screen closes right after a command runs, so open the menu on the next tick.
			if (openMenuNextTick) {
				openMenuNextTick = false;
				client.gui.setScreen(new VividScreen(null));
			}
			while (openSettingsKey.consumeClick()) {
				client.gui.setScreen(new VividScreen(null));
			}
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(ClientCommands.literal("vivid")
						.executes(ctx -> {
							openMenuNextTick = true;
							return 1;
						})
						.then(ClientCommands.literal("toggle").executes(ctx -> {
							VividConfig c = VividConfig.get();
							c.enabled = !c.enabled;
							VividConfig.save();
							ctx.getSource().sendFeedback(Component.literal("Vivid " + (c.enabled ? "ON" : "OFF")));
							return 1;
						}))
						.then(ClientCommands.literal("reset").executes(ctx -> {
							VividConfig.reset();
							ctx.getSource().sendFeedback(Component.literal("Vivid reset to the Vibrant preset"));
							return 1;
						}))));
	}
}
