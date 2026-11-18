# 3D Skin Layers (Fabric, Minecraft 26.3, client-side)

Turns the outer layer of player skins - hat/hair, jacket, sleeves and pants - into real
3D pixels that stick out from the body, instead of the flat vanilla panels.
Works on any server and with any skin (classic and slim arms, HD skins too).
Needs Fabric API; Mod Menu is optional.

## Settings
- `/skinlayers` opens the menu (also in Mod Menu -> 3D Skin Layers -> Configure).
- `/skinlayers toggle` turns it on/off, `/skinlayers reset` restores defaults,
  `/skinlayers reload` rebuilds all skins (e.g. after changing a resource pack).
- Menu: on/off, hat, jacket, sleeves, pants, yourself in F5, my thickness, show over armor, 3D distance.

Settings are saved in `config/voxelskins.json`.

## Notes
- Where armor is worn, the armor covers the layer like in vanilla. "Show over armor" puts the 3D pixels on top of the armor instead, but on skins with a full outer layer that mostly hides the armor, so it's off by default.
- Your own skin uses thinner 3D pixels (50% by default, "My thickness" slider) so it doesn't look chunky up close in F5 or the inventory. Other players stay at full thickness.
- Past the 3D distance (16 blocks by default) the normal flat layer is drawn; it looks the same from far away and is cheaper.
- Your first-person arm still uses the flat sleeve.
- Don't install it together with tr7zw's "3D Skin Layers" mod - they do the same job.
