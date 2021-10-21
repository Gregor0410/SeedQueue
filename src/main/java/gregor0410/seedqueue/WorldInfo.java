package gregor0410.seedqueue;

import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;

import java.util.Optional;

public class WorldInfo {
    public GeneratorOptions generatorOptions;
    public boolean isInMemory;
    public IntegratedServer server;
    public LevelStorage.Session session;
    public WorldInfo(GeneratorOptions generatorOptions, Optional<IntegratedServer> server,LevelStorage.Session session){
        this.generatorOptions = generatorOptions;
        this.isInMemory = server.isPresent();
        this.server = server.get();
        this.session = session;
    }
}
