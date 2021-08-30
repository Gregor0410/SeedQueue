package gregor0410.moonlight;

import com.google.common.collect.Lists;
import gregor0410.moonlight.mixin.ChunkGeneratorAccessor;
import gregor0410.moonlight.pregen.PreGenerator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Moonlight implements ModInitializer {
    public static final String MOD_NAME = "Moonlight";
    private static final List<Biome> STRONGHOLD_BIOMES = Lists.<Biome>newArrayList(Biomes.PLAINS,Biomes.DESERT,Biomes.MOUNTAINS,Biomes.FOREST,Biomes.TAIGA,Biomes.SNOWY_TUNDRA,Biomes.SNOWY_MOUNTAINS,Biomes.MUSHROOM_FIELDS,Biomes.MUSHROOM_FIELD_SHORE,Biomes.DESERT_HILLS,Biomes.WOODED_HILLS,Biomes.TAIGA_HILLS,Biomes.MOUNTAIN_EDGE,Biomes.JUNGLE,Biomes.JUNGLE_HILLS,Biomes.JUNGLE_EDGE,Biomes.STONE_SHORE,Biomes.BIRCH_FOREST,Biomes.BIRCH_FOREST_HILLS,Biomes.DARK_FOREST,Biomes.SNOWY_TAIGA,Biomes.GIANT_TREE_TAIGA,Biomes.GIANT_TREE_TAIGA_HILLS,Biomes.WOODED_MOUNTAINS,Biomes.BADLANDS_PLATEAU,Biomes.SUNFLOWER_PLAINS,Biomes.DESERT_LAKES,Biomes.GRAVELLY_MOUNTAINS,Biomes.FLOWER_FOREST,Biomes.TAIGA_MOUNTAINS,Biomes.ICE_SPIKES,Biomes.MODIFIED_JUNGLE,Biomes.MODIFIED_JUNGLE_EDGE,Biomes.TALL_BIRCH_FOREST,Biomes.TALL_BIRCH_HILLS,Biomes.DARK_FOREST_HILLS,Biomes.SNOWY_TAIGA_MOUNTAINS,Biomes.GIANT_SPRUCE_TAIGA_HILLS,Biomes.MODIFIED_GRAVELLY_MOUNTAINS,Biomes.SHATTERED_SAVANNA,Biomes.SHATTERED_SAVANNA_PLATEAU,Biomes.ERODED_BADLANDS,Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU,Biomes.MODIFIED_BADLANDS_PLATEAU);
    public static Logger LOGGER = LogManager.getLogger();
    public static PreGenerator preGenerator;

    @Override
    public void onInitialize(){
        log(Level.INFO,"Initialising");
        preGenerator = new PreGenerator(5);
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
}
