# Moonlight
Mod that offsets stronghold generation to a separate thread to increase loading speed of minecraft worlds.

# About
When Minecraft creates a world before it starts generating chunks it first generates the locations of all 128 strongholds. Each stronghold must do an expensive locateBiome() call to 
find a valid biome to spawn in, which results in there being a sizeable pause at 0% on the world loading screen.

The mod is called Moonlight as it works well in combination with Tuinity/Starlight to speed up world loading drastically.
