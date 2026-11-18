package com.voxelskins.test;

import com.voxelskins.VoxelSkinsConfig;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.CameraType;

public class ScreenshotTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext sp = context.worldBuilder().create()) {
			sp.getClientLevel().waitForChunksRender();
			context.runOnClient(mc -> {
				mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
				mc.options.hideGui = true;
			});
			context.waitTicks(40);
			context.takeScreenshot("voxel_on");
			context.runOnClient(mc -> mc.player.setYRot(mc.player.getYRot() + 135f));
			context.waitTicks(10);
			context.takeScreenshot("voxel_on_side");
			context.runOnClient(mc -> mc.player.setYRot(mc.player.getYRot() - 135f));
			context.runOnClient(mc -> VoxelSkinsConfig.get().enabled = false);
			context.waitTicks(10);
			context.takeScreenshot("voxel_off");
		}
	}
}
