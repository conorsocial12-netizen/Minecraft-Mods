package com.vivid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.vivid.skins.SkinMeshCache;
import com.vivid.skins.SkinsLogic;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * Vivid settings. Open with V, /vivid, or Mod Menu. Every change shows up live in the world
 * behind the menu; changing any slider switches the preset to "Custom".
 */
public class VividScreen extends Screen {
	public enum Page {
		PRESETS("Presets"), COLOUR("Colour"), SKY("Sky & Fog"), WATER("Water"), EFFECTS("Effects"),
		PARTICLES("Particles"), SKINS("3D Skins"), ARMOUR("Armour"), OUTLINE("Outline"), HUD("HUD");

		final String label;

		Page(String label) {
			this.label = label;
		}
	}

	private static final int ROW = 22;
	private static final int COL_W = 150;

	private final Screen parent;
	private final Page page;
	private boolean waterChanged = false;

	public VividScreen(Screen parent) {
		this(parent, Page.PRESETS);
	}

	public VividScreen(Screen parent, Page page) {
		super(Component.literal("Vivid"));
		this.parent = parent;
		this.page = page;
	}

	@Override
	protected void init() {
		VividConfig c = VividConfig.get();
		int center = this.width / 2;
		int left = center - COL_W - 5;
		int right = center + 5;

		addRenderableWidget(new StringWidget(center - 100, 6, 200, 12,
				Component.literal("Vivid  -  " + page.label), this.font));

		// ---- page tabs ----
		Page[] pages = Page.values();
		int perRow = (pages.length + 1) / 2;
		int tabW = Math.min(76, (this.width - 20) / perRow - 2);
		for (int i = 0; i < pages.length; i++) {
			Page p = pages[i];
			int row = i / perRow, col = i % perRow;
			int inRow = row == 0 ? perRow : pages.length - perRow;
			int rowX = center - (inRow * (tabW + 2)) / 2;
			Button tab = Button.builder(Component.literal(p.label), b -> open(p))
					.bounds(rowX + col * (tabW + 2), 20 + row * 21, tabW, 20).build();
			tab.active = p != page;
			addRenderableWidget(tab);
		}

		int top = 70;
		int[] y = {top, top};

		switch (page) {
			case PRESETS -> {
				addRenderableWidget(toggle(left, y[0], COL_W, "Vivid", () -> c.enabled, v -> c.enabled = v,
						"Turn the whole mod on or off to compare with vanilla"));
				addRenderableWidget(new StringWidget(right, y[1] + 6, COL_W, 12,
						Component.literal("Current: " + c.preset), this.font));
				y[0] += ROW + 6; y[1] += ROW + 6;
				for (int i = 0; i < Presets.NAMES.size(); i++) {
					String name = Presets.NAMES.get(i);
					int x = i % 2 == 0 ? left : right;
					int col = i % 2;
					Button b = Button.builder(Component.literal(name.equals(c.preset) ? "* " + name + " *" : name), btn -> {
						Presets.apply(name, c);
						this.waterChanged = true;
						VividConfig.save();
						open(Page.PRESETS);
					}).bounds(x, y[col], COL_W, 20).tooltip(Tooltip.create(Component.literal(Presets.describe(name)))).build();
					addRenderableWidget(b);
					y[col] += ROW;
				}
				int ty = Math.max(y[0], y[1]) + 8;
				addRenderableWidget(new StringWidget(center - 160, ty, 320, 12,
						Component.literal(Presets.describe(c.preset)).withColor(0xAAAAAA), this.font));
				addRenderableWidget(new StringWidget(center - 160, ty + 14, 320, 12,
						Component.literal("Tweak anything on the other tabs to make it your own.").withColor(0x777777), this.font));
			}
			case COLOUR -> {
				add(y, 0, left, right, toggle(0, 0, COL_W, "Colour grading", () -> c.gradingEnabled, v -> c.gradingEnabled = v,
						"Screen-wide colour effects below"));
				add(y, 1, left, right, slider("Saturation", 0, 2, () -> c.saturation, v -> c.saturation = v, "%.2f",
						"How colourful everything is. 1 = vanilla"));
				add(y, 0, left, right, slider("Vibrance", 0, 1, () -> c.vibrance, v -> c.vibrance = v, "%.2f",
						"Boosts dull colours more than bright ones"));
				add(y, 1, left, right, slider("Contrast", 0.5, 1.5, () -> c.contrast, v -> c.contrast = v, "%.2f",
						"Difference between darks and lights. 1 = vanilla"));
				add(y, 0, left, right, slider("Brightness", 0.7, 1.3, () -> c.brightness, v -> c.brightness = v, "%.2f",
						"Overall exposure. 1 = vanilla"));
				add(y, 1, left, right, slider("Vignette", 0, 1, () -> c.vignette, v -> c.vignette = v, "%.2f",
						"Darker screen edges"));
				add(y, 0, left, right, slider("Bloom", 0, 1.5, () -> c.bloom, v -> c.bloom = v, "%.2f",
						"Soft glow around bright things"));
				add(y, 1, left, right, slider("Bloom threshold", 0.3, 0.95, () -> c.bloomThreshold, v -> c.bloomThreshold = v, "%.2f",
						"How bright something must be to glow. Lower = more glow"));
				add(y, 0, left, right, slider("Sharpen", 0, 1, () -> c.sharpen, v -> c.sharpen = v, "%.2f",
						"Crisper textures and edges"));
			}
			case SKY -> {
				add(y, 0, left, right, toggle(0, 0, COL_W, "Sky tweaks", () -> c.skyEnabled, v -> c.skyEnabled = v,
						"Sky colour, sunsets, stars and cloud colours"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Fog tweaks", () -> c.fogEnabled, v -> c.fogEnabled = v,
						"Distance haze and fog colour"));
				add(y, 0, left, right, slider("Sky colour", 0.5, 2, () -> c.skySaturation, v -> c.skySaturation = v, "%.2f",
						"Higher = deeper, richer blue sky"));
				add(y, 1, left, right, slider("Haze starts at", 0.2, 1, () -> c.hazeStart, v -> c.hazeStart = v, "%.0f%%", 100,
						"How far out the haze begins, as % of your render distance. 100% = almost no fog"));
				add(y, 0, left, right, slider("Sunsets", 0, 2.5, () -> c.sunsetStrength, v -> c.sunsetStrength = v, "%.2f",
						"Strength and orange-ness of sunrises and sunsets"));
				add(y, 1, left, right, slider("Biome fog distance", 0.5, 3, () -> c.biomeFogDistance, v -> c.biomeFogDistance = v, "x%.1f",
						"Pushes thick fog (rain, swamps...) further away"));
				add(y, 0, left, right, slider("Stars", 0.5, 3, () -> c.starBrightness, v -> c.starBrightness = v, "x%.1f",
						"Star brightness at night"));
				add(y, 1, left, right, slider("Fog colour", 0.5, 2, () -> c.fogSaturation, v -> c.fogSaturation = v, "%.2f",
						"How colourful the haze is"));
				add(y, 0, left, right, slider("Cloud colours", 0, 1, () -> c.cloudTint, v -> c.cloudTint = v, "%.2f",
						"Clouds glow with sunset colours"));
				add(y, 1, left, right, slider("Bright weather", 0, 1, () -> c.weatherBrightness, v -> c.weatherBrightness = v, "%.2f",
						"Keeps rain and storms from getting gloomy. 0 = vanilla"));
			}
			case WATER -> {
				int[] waterRgb = rgb(c.waterColor, 0x1ED6E6);
				add(y, 0, left, right, toggle(0, 0, COL_W, "Water tweaks", () -> c.waterEnabled, v -> { c.waterEnabled = v; waterChanged = true; },
						"Water colour, clarity and underwater effects"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Light tweaks", () -> c.lightEnabled, v -> c.lightEnabled = v,
						"Torch strength, warm light and night glow"));
				add(y, 0, left, right, slider("Water clarity", 0.5, 5, () -> c.waterClarity, v -> c.waterClarity = v, "x%.1f",
						"How far you can see underwater"));
				add(y, 1, left, right, slider("Torch glow", 0.8, 1.6, () -> c.torchGlow, v -> c.torchGlow = v, "x%.2f",
						"Strength of torches, lanterns and other block light"));
				add(y, 0, left, right, slider("Water surface colour", 0, 1, () -> c.waterSurfaceBlend, v -> { c.waterSurfaceBlend = v; waterChanged = true; }, "%.0f%%", 100,
						"How much water blocks take your water colour"));
				add(y, 1, left, right, slider("Warm light", 0, 1, () -> c.warmLight, v -> c.warmLight = v, "%.2f",
						"Makes torchlight more orange/golden"));
				add(y, 0, left, right, slider("Underwater tint", 0, 1, () -> c.underwaterTint, v -> c.underwaterTint = v, "%.2f",
						"Colours the screen when your head is underwater"));
				add(y, 1, left, right, slider("Night glow", 0, 1, () -> c.nightGlow, v -> c.nightGlow = v, "%.2f",
						"Extra bloom at night so lights really glow"));
				add(y, 0, left, right, slider("Underwater wobble", 0, 1, () -> c.underwaterWobble, v -> c.underwaterWobble = v, "%.2f",
						"Gentle wave distortion underwater"));
				y[1] = Math.max(y[0], y[1]);
				y[0] = y[1];
				add(y, 0, left, right, colourSlider("Water red", () -> waterRgb[0], v -> { waterRgb[0] = v; c.waterColor = hex(waterRgb); waterChanged = true; markCustom(); }));
				add(y, 1, left, right, colourSlider("Water green", () -> waterRgb[1], v -> { waterRgb[1] = v; c.waterColor = hex(waterRgb); waterChanged = true; markCustom(); }));
				add(y, 0, left, right, colourSlider("Water blue", () -> waterRgb[2], v -> { waterRgb[2] = v; c.waterColor = hex(waterRgb); waterChanged = true; markCustom(); }));
				addRenderableWidget(new StringWidget(right, y[1] + 6, COL_W, 12,
						Component.literal("███ water colour").withColor(VividConfig.parseHex(c.waterColor, 0x1ED6E6)), this.font));
			}
			case EFFECTS -> {
				add(y, 0, left, right, slider("Hurt flash", 0, 1, () -> c.hurtVignette, v -> c.hurtVignette = v, "%.2f",
						"Red screen edges when you take damage or are low on health"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Dimension moods", () -> c.dimensionMoods, v -> c.dimensionMoods = v,
						"The Nether and the End get their own colour grading"));
				add(y, 0, left, right, slider("Nether mood", 0, 1, () -> c.netherMood, v -> c.netherMood = v, "%.2f",
						"Warm, fiery tint in the Nether"));
				add(y, 1, left, right, slider("End mood", 0, 1, () -> c.endMood, v -> c.endMood = v, "%.2f",
						"Purple, otherworldly tint in the End"));
			}
			case PARTICLES -> {
				add(y, 0, left, right, toggle(0, 0, COL_W, "Combat particles", () -> c.particlesEnabled, v -> c.particlesEnabled = v,
						"All of Vivid's combat particles on/off"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Mob hit particles", () -> c.hitParticles, v -> c.hitParticles = v,
						"Bits fly off mobs when hit: feathers from chickens, gunpowder from creepers, bones from skeletons, wool from sheep..."));
				add(y, 0, left, right, plainSlider("Hit particle amount", 0, 4, () -> c.hitAmount, v -> c.hitAmount = v, "x%.1f",
						"How many bits fly off"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Only my hits", () -> c.ownHitsOnly, v -> c.ownHitsOnly = v,
						"ON = only when you hit something. OFF = any damage (other players, arrows, lava...)"));
				add(y, 0, left, right, toggle(0, 0, COL_W, "Death burst", () -> c.deathBurst, v -> c.deathBurst = v,
						"A bigger burst of the mob's particles when it dies"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Fancy crits", () -> c.fancyCrits, v -> c.fancyCrits = v,
						"Critical hits get a spinning ring and sparkles"));
				add(y, 0, left, right, plainSlider("Crit & spark amount", 0, 4, () -> c.critAmount, v -> c.critAmount = v, "x%.1f",
						"How big crit and impact effects are"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Impact sparks", () -> c.impactSparks, v -> c.impactSparks = v,
						"Sparks where your hit lands"));
				add(y, 0, left, right, toggle(0, 0, COL_W, "Puff for other mobs", () -> c.genericPuff, v -> c.genericPuff = v,
						"Mobs without their own particles (like players) give a small puff"));
			}
			case SKINS -> {
				add(y, 0, left, right, toggle(0, 0, COL_W, "3D skin layers", () -> c.skins3d, v -> c.skins3d = v,
						"Hat/hair, jacket, sleeves and pants become real 3D pixels"));
				add(y, 1, left, right, armourModeButton(c));
				add(y, 0, left, right, toggle(0, 0, COL_W, "Hat / hair", () -> c.skinHat, v -> c.skinHat = v, "3D hat layer"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Jacket", () -> c.skinJacket, v -> c.skinJacket = v, "3D jacket layer"));
				add(y, 0, left, right, toggle(0, 0, COL_W, "Sleeves", () -> c.skinSleeves, v -> c.skinSleeves = v, "3D sleeve layers"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Pants", () -> c.skinPants, v -> c.skinPants = v, "3D pants layers"));
				add(y, 0, left, right, toggle(0, 0, COL_W, "Other players", () -> c.skinsOnOthers, v -> c.skinsOnOthers = v,
						"3D skins and outline on everyone else"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Me (F5 / inventory)", () -> c.skinsOnSelf, v -> c.skinsOnSelf = v,
						"3D skins and outline on your own skin"));
				add(y, 0, left, right, plainSlider("My thickness", 10, 100, () -> c.selfThickness, v -> c.selfThickness = v, "%.0f%%",
						"How thick your own 3D pixels are. Thinner looks less chunky up close"));
				add(y, 1, left, right, plainSlider("3D distance", 0, 64, () -> c.skinDistance, v -> c.skinDistance = v, "%.0f blocks",
						"Further than this, the normal flat layer is used (0 = always 3D)"));
				if (VividClient.STANDALONE_SKIN_LAYERS) {
					addRenderableWidget(new StringWidget(center - 160, Math.max(y[0], y[1]) + 4, 320, 12,
							Component.literal("The separate 3D Skin Layers mod is installed, so it does the 3D layers.").withColor(0xFFAA55), this.font));
				}
			}
			case ARMOUR -> {
				add(y, 0, left, right, toggle(0, 0, COL_W, "Minimal armour", () -> c.minimalArmor, v -> c.minimalArmor = v,
						"Players' armour becomes a crown, shoulder pads and boots so skins show. Everything it doesn't cover stays 3D"));
				add(y, 1, left, right, armourModeButton(c));
				add(y, 0, left, right, rowsSlider("Crown height", 0, 8, () -> c.helmetRows, v -> c.helmetRows = v,
						"How far the helmet comes down the head (pixels)"));
				add(y, 1, left, right, rowsSlider("Shoulder pad size", 0, 12, () -> c.shoulderRows, v -> c.shoulderRows = v,
						"How far the shoulder pads come down the arms (pixels)"));
				add(y, 0, left, right, toggle(0, 0, COL_W, "Chestplate body", () -> c.minimalChestBody, v -> c.minimalChestBody = v,
						"Keep the chest part of the chestplate"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Leggings", () -> c.minimalLeggings, v -> c.minimalLeggings = v,
						"Keep the leggings"));
				addRenderableWidget(new StringWidget(center - 160, Math.max(y[0], y[1]) + 4, 320, 12,
						Component.literal("Armour trims are hidden while minimal armour is on.").withColor(0x999999), this.font));
				addRenderableWidget(new StringWidget(center - 160, Math.max(y[0], y[1]) + 18, 320, 12,
						Component.literal("\"Armour\" decides how 3D skins sit under any armour that's left.").withColor(0x999999), this.font));
			}
			case OUTLINE -> {
				int[] rim = rgb(c.rimColor, 0xFFFFFF);
				add(y, 0, left, right, toggle(0, 0, COL_W, "Pixel edges", () -> c.pixelEdges, v -> c.pixelEdges = v,
						"A light edge on every 3D skin pixel, for a bevelled look"));
				add(y, 1, left, right, toggle(0, 0, COL_W, "Body rim", () -> c.bodyRim, v -> c.bodyRim = v,
						"A light rim along the edges of the head, body, arms and legs"));
				add(y, 0, left, right, plainSlider("Edge width", 0.05, 0.5, () -> c.pixelEdgeWidth, v -> c.pixelEdgeWidth = v, "%.2f px",
						"How wide the pixel edges are"));
				add(y, 1, left, right, plainSlider("Rim width", 0.05, 1, () -> c.bodyRimWidth, v -> c.bodyRimWidth = v, "%.2f px",
						"How wide the body rim is"));
				add(y, 0, left, right, plainSlider("Edge brightness", 0, 1, () -> c.pixelEdgeStrength, v -> c.pixelEdgeStrength = v, "%.0f%%", 100,
						"How light the pixel edges are"));
				add(y, 1, left, right, plainSlider("Rim brightness", 0, 1, () -> c.bodyRimStrength, v -> c.bodyRimStrength = v, "%.0f%%", 100,
						"How light the body rim is"));
				add(y, 0, left, right, toggle(0, 0, COL_W, "Glow in the dark", () -> c.rimGlow, v -> c.rimGlow = v,
						"Outline stays bright even in dark places"));
				add(y, 1, left, right, colourSlider("Outline red", () -> rim[0], v -> { rim[0] = v; c.rimColor = hex(rim); }));
				add(y, 0, left, right, colourSlider("Outline green", () -> rim[1], v -> { rim[1] = v; c.rimColor = hex(rim); }));
				add(y, 1, left, right, colourSlider("Outline blue", () -> rim[2], v -> { rim[2] = v; c.rimColor = hex(rim); }));
				addRenderableWidget(new StringWidget(left, y[0] + 6, COL_W, 12,
						Component.literal("\u2588\u2588\u2588 outline colour").withColor(VividConfig.parseHex(c.rimColor, 0xFFFFFF)), this.font));
			}
			case HUD -> {
				int[] accent = rgb(c.accentColor, 0x4FE0FF);
				add(y, 0, left, right, toggle(0, 0, COL_W, "Smooth hotbar", () -> c.smoothHotbar, v -> c.smoothHotbar = v,
						"The selection box slides between slots"));
				add(y, 1, left, right, plainSlider("Hotbar speed", 2, 60, () -> c.hotbarSpeed, v -> c.hotbarSpeed = v, "%.0f",
						"How fast the selection box slides"));
				add(y, 0, left, right, toggle(0, 0, COL_W, "Accent on hotbar", () -> c.accentHotbar, v -> c.accentHotbar = v,
						"Tint the hotbar selection with your accent colour"));
				add(y, 1, left, right, colourSlider("Accent red", () -> accent[0], v -> { accent[0] = v; c.accentColor = hex(accent); }));
				add(y, 0, left, right, colourSlider("Accent green", () -> accent[1], v -> { accent[1] = v; c.accentColor = hex(accent); }));
				add(y, 1, left, right, colourSlider("Accent blue", () -> accent[2], v -> { accent[2] = v; c.accentColor = hex(accent); }));
				addRenderableWidget(new StringWidget(left, y[0] + 6, COL_W, 12,
						Component.literal("███ accent colour").withColor(VividConfig.parseHex(c.accentColor, 0x4FE0FF)), this.font));
			}
		}

		// ---- bottom buttons ----
		int by = Math.max(this.height - 28, Math.max(y[0], y[1]) + 6);
		addRenderableWidget(Button.builder(Component.literal("Reset to " + (Presets.NAMES.contains(c.preset) ? c.preset : "Vibrant")), b -> {
			String p = Presets.NAMES.contains(c.preset) ? c.preset : "Vibrant";
			Presets.apply(p, c);
			waterChanged = true;
			VividConfig.save();
			open(page);
		}).bounds(left, by, COL_W, 20).tooltip(Tooltip.create(Component.literal("Undo your tweaks and go back to this preset"))).build());
		addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose()).bounds(right, by, COL_W, 20).build());
	}

	// ---------------------------------------------------------------- helpers

	private void open(Page p) {
		applyPendingChanges();
		VividConfig.save();
		Minecraft.getInstance().gui.setScreen(new VividScreen(parent, p));
	}

	/** Water block colours are baked into chunks, so rebuild them if the water colour changed. */
	private void applyPendingChanges() {
		if (waterChanged) {
			waterChanged = false;
			Minecraft mc = Minecraft.getInstance();
			if (mc.level != null && mc.levelExtractor != null) mc.levelExtractor.allChanged();
		}
	}

	@Override
	public void onClose() {
		applyPendingChanges();
		VividConfig.save();
		Minecraft.getInstance().gui.setScreen(parent);
	}

	@Override
	public boolean isPauseScreen() {
		return false; // keep the world moving so you can see changes live
	}

	private void add(int[] y, int col, int left, int right, AbstractWidget w) {
		w.setX(col == 0 ? left : right);
		w.setY(y[col]);
		addRenderableWidget(w);
		y[col] += ROW;
	}

	private static Button armourModeButton(VividConfig c) {
		return Button.builder(armourLabel(c), b -> {
			SkinsLogic.ArmorMode[] all = SkinsLogic.ArmorMode.values();
			c.armorMode = all[(SkinsLogic.ArmorMode.parse(c.armorMode).ordinal() + 1) % all.length].name();
			b.setMessage(armourLabel(c));
		}).bounds(0, 0, COL_W, 20).tooltip(Tooltip.create(Component.literal(
				"Under armour: pixels get thinner so they stay 3D inside armour and peek through gaps. "
						+ "On top: your skin covers the armour. Flat: armoured parts use the normal flat layer."))).build();
	}

	/** Whole-pixel slider (rounds as you drag). */
	private static Slider rowsSlider(String label, double min, double max, DoubleSupplier get, DoubleConsumer set, String tip) {
		return plainSlider(label, min, max, get, v -> set.accept(Math.round(v)), "%.0f px", tip);
	}

	private static Component armourLabel(VividConfig c) {
		return Component.literal("Armour: " + SkinsLogic.ArmorMode.parse(c.armorMode).label);
	}

	private static void markCustom() {
		VividConfig.get().preset = "Custom";
	}

	private static int[] rgb(String hex, int fallback) {
		int v = VividConfig.parseHex(hex, fallback);
		return new int[]{(v >> 16) & 0xFF, (v >> 8) & 0xFF, v & 0xFF};
	}

	private static String hex(int[] rgb) {
		return VividConfig.toHex((rgb[0] << 16) | (rgb[1] << 8) | rgb[2]);
	}

	private static Button toggle(int x, int y, int w, String label, BooleanSupplier get, Consumer<Boolean> set, String tip) {
		return Button.builder(toggleLabel(label, get.getAsBoolean()), b -> {
			set.accept(!get.getAsBoolean());
			b.setMessage(toggleLabel(label, get.getAsBoolean()));
		}).bounds(x, y, w, 20).tooltip(Tooltip.create(Component.literal(tip))).build();
	}

	private static Component toggleLabel(String label, boolean on) {
		return Component.literal(label + ": " + (on ? "ON" : "OFF"));
	}

	private static Slider slider(String label, double min, double max, DoubleSupplier get, DoubleConsumer set, String fmt, String tip) {
		return slider(label, min, max, get, set, fmt, 1, tip);
	}

	private static Slider slider(String label, double min, double max, DoubleSupplier get, DoubleConsumer set, String fmt,
			double displayScale, String tip) {
		Slider s = new Slider(label, min, max, get.getAsDouble(), fmt, displayScale, v -> {
			set.accept(v);
			markCustom();
		});
		s.setTooltip(Tooltip.create(Component.literal(tip)));
		return s;
	}

	/** Slider for settings that presets don't touch (doesn't switch the preset to Custom). */
	private static Slider plainSlider(String label, double min, double max, DoubleSupplier get, DoubleConsumer set, String fmt, String tip) {
		return plainSlider(label, min, max, get, set, fmt, 1, tip);
	}

	private static Slider plainSlider(String label, double min, double max, DoubleSupplier get, DoubleConsumer set, String fmt,
			double displayScale, String tip) {
		Slider s = new Slider(label, min, max, get.getAsDouble(), fmt, displayScale, set);
		s.setTooltip(Tooltip.create(Component.literal(tip)));
		return s;
	}

	private static Slider colourSlider(String label, IntSupplier get, IntConsumer set) {
		return new Slider(label, 0, 255, get.getAsInt(), "%.0f", 1, v -> set.accept((int) Math.round(v)));
	}

	private static class Slider extends AbstractSliderButton {
		private final String label;
		private final double min;
		private final double max;
		private final String fmt;
		private final double displayScale;
		private final DoubleConsumer setter;

		Slider(String label, double min, double max, double current, String fmt, double displayScale, DoubleConsumer setter) {
			super(0, 0, COL_W, 20, Component.empty(), clamp01((current - min) / (max - min)));
			this.label = label;
			this.min = min;
			this.max = max;
			this.fmt = fmt;
			this.displayScale = displayScale;
			this.setter = setter;
			updateMessage();
		}

		private static double clamp01(double v) {
			return Math.max(0, Math.min(1, v));
		}

		private double real() {
			return min + this.value * (max - min);
		}

		@Override
		protected void updateMessage() {
			if (label == null) return; // called from the super constructor
			setMessage(Component.literal(label + ": " + String.format(fmt, real() * displayScale)));
		}

		@Override
		protected void applyValue() {
			setter.accept(real());
		}
	}
}
