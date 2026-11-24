# Vivid

Makes Minecraft look completely better, and every part of it is adjustable. Client-side only, works on any server. Needs Fabric API (Mod Menu optional).

**Open the settings:** press **V**, type `/vivid`, or use Mod Menu. Changes show live behind the menu.
`/vivid toggle` turns it on/off, `/vivid reset` goes back to the Vibrant preset.

## Presets
Vibrant (default) · Cinematic · Realistic · Dreamy · Vanilla+ — tweak any slider and it becomes your own "Custom" look.

## What it changes (settings tabs)
- **Colour** – saturation, vibrance, contrast, brightness, vignette, bloom (glow), bloom threshold, sharpen
- **Sky & Fog** – deeper blue sky, bigger orange sunsets, brighter stars, clouds tinted by sunsets, light haze at the edge of your render distance, biome fog distance, fog colour, bright weather (rain stays bright)
- **Water & Light** – tropical clear blue water (pick any colour), underwater clarity, underwater tint & wobble, stronger torch light, warm light, night glow
- **Effects** – red hurt flash / low-health pulse, Nether and End colour moods
- **Particles** – mob hit particles (feathers from chickens, gunpowder from creepers, bones from skeletons, rotten flesh from zombies, string from spiders, wool in the sheep's colour, ink from squid, portal sparkles from endermen, and more), bigger death bursts, fancy critical hits (spinning ring + sparkles), impact sparks where your hit lands, amount sliders, and an "only my hits" option
- **3D Skins** – hat/hair, jacket, sleeves and pants become real 3D pixels. Works with armour: by default the pixels get thinner so they stay 3D *inside* the armour (peeking through gaps, never poking through). You can switch to "on top of armour" or "flat under armour". Separate toggles for each part, other players / yourself, your own thickness and the 3D distance. `/vivid reloadskins` rebuilds skins.
- **Armour** – *Minimal armour* (on by default): players' armour becomes a crown, shoulder pads and boots so skins show, and everything it doesn't cover stays fully 3D. Works with every armour material and resource pack. Sliders for crown height and shoulder pad size, and toggles to keep the chestplate body or the leggings. Trims are hidden while it's on.
- **Outline** – a light edge on every 3D skin pixel (bevelled look) and a light rim along the edges of the head, body, arms and legs, with width, brightness, colour and glow-in-the-dark settings
- **HUD** – smooth sliding hotbar selector, accent colour

Settings are saved in `config/vivid.json` (you can also edit it while the game runs).

If the separate 3d-skin-layers mod is installed too, Vivid lets it handle the 3D layers so nothing doubles up (the outline still works).

Note: water block colour changes need the chunks rebuilt — the menu does this for you when you close it (or press F3+A).
