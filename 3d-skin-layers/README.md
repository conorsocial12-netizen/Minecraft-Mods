# 3D Skin Layers (Fabric, Minecraft 26.3, client-side)

Turns the outer layer of player skins - hat/hair, jacket, sleeves and pants - into real
3D pixels that stick out from the body, instead of the flat vanilla panels.
Works on any server and with any skin (classic and slim arms, HD skins too).
Needs Fabric API; Mod Menu is optional.

## Settings
- `/skinlayers` opens the menu (also in Mod Menu -> 3D Skin Layers -> Configure).
- `/skinlayers toggle` turns it on/off, `/skinlayers reset` restores defaults,
  `/skinlayers reload` rebuilds all skins (e.g. after changing a resource pack).
- Menu: on/off, hat, jacket, sleeves, pants, yourself in F5, flat under armor, 3D distance.

Settings are saved in `config/voxelskins.json`.

## Notes
- Where armor is worn the flat layer is used, so pixels don't poke through it (can be turned off).
- Past the 3D distance (16 blocks by default) the normal flat layer is drawn; it looks the same from far away and is cheaper.
- Your first-person arm still uses the flat sleeve.
- Don't install it together with tr7zw's "3D Skin Layers" mod - they do the same job.
