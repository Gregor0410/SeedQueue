package gregor0410.moonlight.pregen;

import com.google.common.collect.Lists;
import gregor0410.moonlight.seed.SeedInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.chunk.StructuresConfig;

import java.util.List;
import java.util.Random;

public class StrongholdPreGen implements Runnable{
    private Random random;
    private static final List<Biome> STRONGHOLD_BIOMES = Lists.<Biome>newArrayList(Biomes.PLAINS,Biomes.DESERT,Biomes.MOUNTAINS,Biomes.FOREST,Biomes.TAIGA,Biomes.SNOWY_TUNDRA,Biomes.SNOWY_MOUNTAINS,Biomes.MUSHROOM_FIELDS,Biomes.MUSHROOM_FIELD_SHORE,Biomes.DESERT_HILLS,Biomes.WOODED_HILLS,Biomes.TAIGA_HILLS,Biomes.MOUNTAIN_EDGE,Biomes.JUNGLE,Biomes.JUNGLE_HILLS,Biomes.JUNGLE_EDGE,Biomes.STONE_SHORE,Biomes.BIRCH_FOREST,Biomes.BIRCH_FOREST_HILLS,Biomes.DARK_FOREST,Biomes.SNOWY_TAIGA,Biomes.GIANT_TREE_TAIGA,Biomes.GIANT_TREE_TAIGA_HILLS,Biomes.WOODED_MOUNTAINS,Biomes.BADLANDS_PLATEAU,Biomes.SUNFLOWER_PLAINS,Biomes.DESERT_LAKES,Biomes.GRAVELLY_MOUNTAINS,Biomes.FLOWER_FOREST,Biomes.TAIGA_MOUNTAINS,Biomes.ICE_SPIKES,Biomes.MODIFIED_JUNGLE,Biomes.MODIFIED_JUNGLE_EDGE,Biomes.TALL_BIRCH_FOREST,Biomes.TALL_BIRCH_HILLS,Biomes.DARK_FOREST_HILLS,Biomes.SNOWY_TAIGA_MOUNTAINS,Biomes.GIANT_SPRUCE_TAIGA_HILLS,Biomes.MODIFIED_GRAVELLY_MOUNTAINS,Biomes.SHATTERED_SAVANNA,Biomes.SHATTERED_SAVANNA_PLATEAU,Biomes.ERODED_BADLANDS,Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU,Biomes.MODIFIED_BADLANDS_PLATEAU);
    private int i;
    private int j;
    private int k;
    private double d;
    private int l;
    private int m;
    private int n;
    private VanillaLayeredBiomeSource biomeSource;
    private SeedInfo seedInfo;

    public StrongholdPreGen(SeedInfo seedInfo){
        this.seedInfo = seedInfo;
        n = 0;
    }

    @Override
    public void run() {
        while(!generateStronghold(this.seedInfo.seed));
    }
    private boolean generateStronghold(long seed){
        if(n==0){
            //initialise algorithm
            this.random = new Random(seed);
            this.i = StructuresConfig.DEFAULT_STRONGHOLD.getDistance();
            this.j = StructuresConfig.DEFAULT_STRONGHOLD.getCount();
            this.k = StructuresConfig.DEFAULT_STRONGHOLD.getSpread();
            this.d = this.random.nextDouble() * Math.PI * 2.0;
            this.l = 0;
            this.m = 0;
            this.biomeSource = new VanillaLayeredBiomeSource(seed,false,false);
        }
        if(n<j){
            double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
            int o = (int)Math.round(Math.cos(d) * e);
            int p = (int)Math.round(Math.sin(d) * e);
            BlockPos blockPos = this.biomeSource.locateBiome((o << 4) + 8, 0, (p << 4) + 8, 112, STRONGHOLD_BIOMES, random);
            if (blockPos != null) {
                o = blockPos.getX() >> 4;
                p = blockPos.getZ() >> 4;
            }
            //update the seed info instance
            seedInfo.addStronghold(new ChunkPos(o, p));
            d += Math.PI * 2 / (double)k;
            ++l;
            if (l == k) {
                ++m;
                l = 0;
                k += 2 * k / (m + 1);
                k = Math.min(k, j - n);
                d += random.nextDouble() * Math.PI * 2.0;
            }
            n++;
            return false;
        }else{
            return true;
        }
    }
}
