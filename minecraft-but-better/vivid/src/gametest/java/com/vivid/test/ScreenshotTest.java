package com.vivid.test;

import com.vivid.VividConfig;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.minecraft.client.CameraType;

/** Takes Vivid ON vs OFF screenshots of a few scenes (only used on the screenshot branch). */
public class ScreenshotTest implements FabricClientGameTest {
	private int n = 0;

	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext sp = context.worldBuilder()
				.setUseConsistentSettings(false)
				.adjustSettings(s -> s.setSeed("vivid"))
				.create()) {
			TestServerContext server = sp.getServer();
			server.runCommand("gamerule advance_time false");
			server.runCommand("gamerule advance_weather false");
			server.runCommand("weather clear");
			server.runCommand("difficulty peaceful");
			server.runCommand("time set 3000");
			context.runOnClient(mc -> mc.gui.hud.toggle()); // hide the HUD for the world shots
			sp.getConnection().waitForChunksRender();
			context.waitTicks(60);

			// ---- landscape ----
			look(context, 0, 15);
			pair(context, "day_landscape");
			look(context, 180, 15);
			pair(context, "day_landscape_2");

			// ---- sunset ----
			server.runCommand("time set 12400");
			look(context, 90, -3);
			context.waitTicks(30);
			pair(context, "sunset");

			// ---- night sky ----
			server.runCommand("time set 18000");
			look(context, 0, -45);
			context.waitTicks(30);
			pair(context, "night_stars");

			// ---- player: no armour, then iron armour (third person, facing the camera) ----
			server.runCommand("time set 3000");
			context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
			look(context, 0, 0);
			context.waitTicks(30);
			pair(context, "player_3d_skin");
			server.runCommand("item replace entity @a armor.head with iron_helmet");
			server.runCommand("item replace entity @a armor.chest with iron_chestplate");
			server.runCommand("item replace entity @a armor.legs with iron_leggings");
			server.runCommand("item replace entity @a armor.feet with iron_boots");
			context.waitTicks(30);
			pair(context, "player_iron_armour");
			server.runCommand("item replace entity @a armor.head with diamond_helmet");
			server.runCommand("item replace entity @a armor.chest with diamond_chestplate");
			server.runCommand("item replace entity @a armor.legs with diamond_leggings");
			server.runCommand("item replace entity @a armor.feet with diamond_boots");
			look(context, 150, 0);
			context.waitTicks(30);
			pair(context, "player_diamond_armour_side");

			// ---- HUD (hotbar accent) ----
			context.runOnClient(mc -> {
				mc.options.setCameraType(CameraType.FIRST_PERSON);
				mc.gui.hud.toggle();
			});
			look(context, 0, 15);
			context.waitTicks(30);
			pair(context, "hud_hotbar");
			context.runOnClient(mc -> mc.gui.hud.toggle());

			// ---- underwater ----
			server.runCommand("execute as @a at @s run fill ~-10 ~-3 ~-10 ~10 ~4 ~10 water");
			context.waitTicks(80);
			look(context, 0, 10);
			context.waitTicks(40);
			pair(context, "underwater");
		}
	}

	private static void look(ClientGameTestContext context, float yaw, float pitch) {
		context.runOnClient(mc -> {
			mc.player.setYRot(yaw);
			mc.player.setXRot(pitch);
			mc.player.yRotO = yaw;
			mc.player.xRotO = pitch;
		});
		context.waitTicks(10);
	}

	/** Same view with Vivid on, then off. */
	private void pair(ClientGameTestContext context, String name) {
		setEnabled(context, true);
		shot(context, name + "_vivid_on");
		setEnabled(context, false);
		shot(context, name + "_vanilla");
		setEnabled(context, true);
	}

	private static void setEnabled(ClientGameTestContext context, boolean on) {
		context.runOnClient(mc -> {
			VividConfig.get().enabled = on;
			if (mc.levelExtractor != null) mc.levelExtractor.allChanged(); // water colour is baked into chunks
		});
		context.waitTicks(60);
	}

	private void shot(ClientGameTestContext context, String name) {
		context.takeScreenshot(TestScreenshotOptions.of(String.format("%02d_%s", n++, name)).disableCounterPrefix().withSize(1280, 720));
	}
}
