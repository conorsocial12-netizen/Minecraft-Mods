package com.skinglow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/** Interactive settings menu. Open with /skinglow or from Mod Menu. */
public class SkinGlowScreen extends Screen {
	private final Screen parent;

	public SkinGlowScreen(Screen parent) {
		super(Component.literal("Skin Glow"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		SkinGlowConfig c = SkinGlowConfig.get();
		int colW = 150;
		int gap = 10;
		int left = this.width / 2 - colW - gap / 2;
		int right = this.width / 2 + gap / 2;
		int top = Math.max(28, this.height / 2 - 100);
		int row = 24;

		addRenderableWidget(new StringWidget(this.width / 2 - 100, top - 18, 200, 12,
				Component.literal("Skin Glow"), Minecraft.getInstance().font));

		// ----- left column: who gets the outline + pulse -----
		int y = top;
		addRenderableWidget(toggle(left, y, colW, "Outline", () -> c.enabled, v -> c.enabled = v)); y += row;
		addRenderableWidget(toggle(left, y, colW, "Other players", () -> c.players, v -> c.players = v)); y += row;
		addRenderableWidget(toggle(left, y, colW, "Me (F5 view)", () -> c.self, v -> c.self = v)); y += row;
		addRenderableWidget(toggle(left, y, colW, "Hostile mobs", () -> c.hostileMobs, v -> c.hostileMobs = v)); y += row;
		addRenderableWidget(toggle(left, y, colW, "Passive mobs", () -> c.passiveMobs, v -> c.passiveMobs = v)); y += row;
		addRenderableWidget(toggle(left, y, colW, "Pulse", () -> c.pulse, v -> c.pulse = v)); y += row;
		addRenderableWidget(new Slider(left, y, colW, "Pulse speed", 0.1, 4.0, c.pulseSpeed, "%.1f", v -> c.pulseSpeed = v));

		// ----- right column: look -----
		y = top;
		addRenderableWidget(Button.builder(modeLabel(c), b -> {
			SkinGlowConfig.ColorMode[] all = SkinGlowConfig.ColorMode.values();
			c.colorMode = all[(c.colorMode.ordinal() + 1) % all.length];
			b.setMessage(modeLabel(c));
		}).bounds(right, y, colW, 20).build()); y += row;

		addRenderableWidget(new Slider(right, y, colW, "Intensity", 0.05, 1.0, c.brightness, "%.2f", v -> c.brightness = v)); y += row;

		int[] rgb = {(GlowLogic.parseHex(c.color, 0xFFFFFF) >> 16) & 0xFF,
				(GlowLogic.parseHex(c.color, 0xFFFFFF) >> 8) & 0xFF,
				GlowLogic.parseHex(c.color, 0xFFFFFF) & 0xFF};
		addRenderableWidget(new Slider(right, y, colW, "Red", 0, 255, rgb[0], "%.0f", v -> { rgb[0] = (int) Math.round(v); writeColor(c, rgb); })); y += row;
		addRenderableWidget(new Slider(right, y, colW, "Green", 0, 255, rgb[1], "%.0f", v -> { rgb[1] = (int) Math.round(v); writeColor(c, rgb); })); y += row;
		addRenderableWidget(new Slider(right, y, colW, "Blue", 0, 255, rgb[2], "%.0f", v -> { rgb[2] = (int) Math.round(v); writeColor(c, rgb); })); y += row;

		addRenderableWidget(new Slider(right, y, colW, "Max distance", 0, 128, c.maxDistance, "%.0f", v -> c.maxDistance = v)); y += row;

		// ----- bottom -----
		int by = top + row * 7 + 6;
		addRenderableWidget(Button.builder(Component.literal("Reset to defaults"), b -> {
			SkinGlowConfig.reset();
			Minecraft.getInstance().gui.setScreen(new SkinGlowScreen(parent));
		}).bounds(left, by, colW, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
				.bounds(right, by, colW, 20).build());
	}

	private static Component modeLabel(SkinGlowConfig c) {
		return Component.literal("Color mode: " + c.colorMode.name());
	}

	private static void writeColor(SkinGlowConfig c, int[] rgb) {
		c.color = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
	}

	private static Button toggle(int x, int y, int w, String label, BooleanSupplier get, Consumer<Boolean> set) {
		Button[] self = new Button[1];
		self[0] = Button.builder(toggleLabel(label, get.getAsBoolean()), b -> {
			set.accept(!get.getAsBoolean());
			b.setMessage(toggleLabel(label, get.getAsBoolean()));
		}).bounds(x, y, w, 20).build();
		return self[0];
	}

	private static Component toggleLabel(String label, boolean on) {
		return Component.literal(label + ": " + (on ? "ON" : "OFF"));
	}

	@Override
	public void onClose() {
		SkinGlowConfig.save();
		Minecraft.getInstance().gui.setScreen(parent);
	}

	private static class Slider extends AbstractSliderButton {
		private final String label;
		private final double min;
		private final double max;
		private final String fmt;
		private final DoubleConsumer setter;

		Slider(int x, int y, int w, String label, double min, double max, double current, String fmt, DoubleConsumer setter) {
			super(x, y, w, 20, Component.empty(), (current - min) / (max - min));
			this.label = label;
			this.min = min;
			this.max = max;
			this.fmt = fmt;
			this.setter = setter;
			updateMessage();
		}

		private double real() {
			return min + this.value * (max - min);
		}

		@Override
		protected void updateMessage() {
			if (label == null) return; // called from super constructor
			String shown = String.format(fmt, real());
			if (label.equals("Max distance") && real() < 0.5) shown = "Unlimited";
			setMessage(Component.literal(label + ": " + shown));
		}

		@Override
		protected void applyValue() {
			setter.accept(real());
		}
	}
}
