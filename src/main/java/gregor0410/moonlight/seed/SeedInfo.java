package gregor0410.moonlight.seed;

import gregor0410.moonlight.Moonlight;
import gregor0410.moonlight.mixin.ChunkGeneratorAccessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeedInfo {
    public SeedInfoEnum stage;
    public long seed;
    private List<ChunkPos> strongholdLocs;
    private ChunkGenerator chunkGenerator;
    private BlockPos spawnLocation;
    private int strongholdIndex;

    public SeedInfo(){
        Moonlight.log(Level.INFO,"New SeedInfo");
        stage = SeedInfoEnum.FINDING_SPAWN_LOC;
        seed = (new Random()).nextLong();
        strongholdLocs = new ArrayList<ChunkPos>();
        spawnLocation = null;
    }

    public void addStronghold(ChunkPos chunkPos){
        this.strongholdLocs.add(chunkPos);
        if(chunkGenerator!=null){
            updateField_24749();
        }
    }
    private void updateField_24749(){
        ((ChunkGeneratorAccessor)chunkGenerator).getField_24749().addAll(strongholdLocs.subList(strongholdIndex,strongholdLocs.size()-1));
        strongholdIndex = strongholdLocs.size()-1;
    }
    public List<ChunkPos> getStrongholds(){
        return this.strongholdLocs;
    }
    public void setSpawnLocation(BlockPos pos){
        this.spawnLocation = pos;
    }
    public ChunkGenerator getChunkGenerator() {
        return chunkGenerator;
    }
    public void setChunkGenerator(ChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
        ((ChunkGeneratorAccessor)chunkGenerator).getField_24749().clear();
        updateField_24749();
    }
}
