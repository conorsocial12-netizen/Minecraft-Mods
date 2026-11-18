package com.voxelskins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Settings menu. Open with /skinlayers or from Mod Menu. */
public class VoxelSkinsScreen extends Screen {
	private final Screen parent;

	public VoxelSkinsScreen(Screen parent) {
		super(Component.literal("3D Skin Layers"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		VoxelSkinsConfig c = VoxelSkinsConfig.get();
		int colW = 150;
		int gap = 10;
		int left = this.width / 2 - colW - gap / 2;
		int right = this.width / 2 + gap / 2;
		int top = Math.max(28, this.height / 2 - 80);
		int row = 24;

		addRenderableWidget(new StringWidget(this.width / 2 - 100, top - 18, 200, 12,
				Component.literal("3D Skin Layers"), Minecraft.getInstance().font));

		int y = top;
		addRenderableWidget(toggle(left, y, colW, "3D layers", () -> c.enabled, v -> c.enabled = v)); y += row;
		addRenderableWidget(toggle(left, y, colW, "Hat", () -> c.hat, v -> c.hat = v)); y += row;
		addRenderableWidget(toggle(left, y, colW, "Jacket", () -> c.jacket, v -> c.jacket = v)); y += row;
		addRenderableWidget(toggle(left, y, colW, "Sleeves", () -> c.sleeves, v -> c.sleeves = v)); y += row;
		addRenderableWidget(toggle(left, y, colW, "Pants", () -> c.pants, v -> c.pants = v));

		y = top;
		addRenderableWidget(toggle(right, y, colW, "Me (F5 view)", () -> c.self, v -> c.self = v)); y += row;
		addRenderableWidget(new ThicknessSlider(right, y, colW, c)); y += row;
		addRenderableWidget(toggle(right, y, colW, "Show over armor", () -> c.overArmor, v -> c.overArmor = v)); y += row;
		addRenderableWidget(new DistanceSlider(right, y, colW, c));

		int by = top + row * 5 + 6;
		addRenderableWidget(Button.builder(Component.literal("Reset to defaults"), b -> {
			VoxelSkinsConfig.reset();
			Minecraft.getInstance().gui.setScreen(new VoxelSkinsScreen(parent));
		}).bounds(left, by, colW, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
				.bounds(right, by, colW, 20).build());
	}

	private static Button toggle(int x, int y, int w, String label, BooleanSupplier get, Consumer<Boolean> set) {
		return Button.builder(toggleLabel(label, get.getAsBoolean()), b -> {
			set.accept(!get.getAsBoolean());
			b.setMessage(toggleLabel(label, get.getAsBoolean()));
		}).bounds(x, y, w, 20).build();
	}

	private static Component toggleLabel(String label, boolean on) {
		return Component.literal(label + ": " + (on ? "ON" : "OFF"));
	}

	@Override
	public void onClose() {
		VoxelSkinsConfig.save();
		Minecraft.getInstance().gui.setScreen(parent);
	}

	private static class DistanceSlider extends AbstractSliderButton {
		private static final double MAX = 64;
		private final VoxelSkinsConfig config;

		DistanceSlider(int x, int y, int w, VoxelSkinsConfig config) {
			super(x, y, w, 20, Component.empty(), Math.min(1.0, config.renderDistance / MAX));
			this.config = config;
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			if (config == null) return; // called from super constructor
			long blocks = Math.round(this.value * MAX);
			setMessage(Component.literal("3D distance: " + (blocks == 0 ? "Unlimited" : blocks + " blocks")));
		}

		@Override
		protected void applyValue() {
			config.renderDistance = Math.round(this.value * MAX);
		}
	}

	/** How thick your own 3D pixels are (10% - 100%). */
	private static class ThicknessSlider extends AbstractSliderButton {
		private final VoxelSkinsConfig config;

		ThicknessSlider(int x, int y, int w, VoxelSkinsConfig config) {
			super(x, y, w, 20, Component.empty(), (config.selfThickness - 10) / 90.0);
			this.config = config;
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			if (config == null) return; // called from super constructor
			setMessage(Component.literal("My thickness: " + percent() + "%"));
		}

		@Override
		protected void applyValue() {
			config.selfThickness = percent();
		}

		private int percent() {
			return (int) (Math.round((10 + this.value * 90) / 5.0) * 5);
		}
	}
}
