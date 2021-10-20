package gregor0410.seedqueue;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.storage.LevelStorage;

public class WorldInfo {
    public long seed;
    public BlockPos spawnPos;
    public LevelStorage.Session session;
    public ServerWorld world;
    public WorldInfo(long seed,BlockPos spawnPos,LevelStorage.Session session,ServerWorld world){
        this.seed = seed;
        this.spawnPos = spawnPos;
        this.session = session;
        this.world = world;
    }
}
