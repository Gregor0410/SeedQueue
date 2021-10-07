package gregor0410.seedqueue.mixin;


import com.google.common.collect.ImmutableList;
import gregor0410.seedqueue.IMinecraftServer;
import gregor0410.seedqueue.PreGenerator;
import gregor0410.seedqueue.SeedQueue;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements IMinecraftServer {
    @Shadow @Final private Executor workerExecutor;

    @Shadow @Final protected LevelStorage.Session session;

    @Shadow @Final protected SaveProperties saveProperties;

    @Shadow @Final protected RegistryTracker.Modifiable dimensionTracker;

    @Mutable
    @Shadow private Map<RegistryKey<World>, ServerWorld> worlds;

    @Shadow public abstract boolean runTask();

    @Shadow protected abstract void method_16208();

    @Shadow protected abstract void executeTask(ServerTask serverTask);

    @Shadow public abstract int getTicks();

    @Shadow protected abstract boolean shouldKeepTicking();

    @Shadow public abstract boolean save(boolean bl, boolean bl2, boolean bl3);

    @Shadow public abstract SaveProperties getSaveProperties();

    private ChunkPos spawnPos;

    @Inject(method="<init>",at=@At("TAIL"))
    private void onInit(CallbackInfo ci){
        this.worlds = new ConcurrentHashMap<>(this.worlds);
    }

    @Inject(method = "Lnet/minecraft/server/MinecraftServer;createWorlds(Lnet/minecraft/server/WorldGenerationProgressListener;)V",at=@At("TAIL"))
    private void onCreateWorlds(CallbackInfo ci){
        try {
            this.newPreGen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "Lnet/minecraft/server/MinecraftServer;prepareStartRegion(Lnet/minecraft/server/WorldGenerationProgressListener;)V",at=@At("TAIL"))
    private void onPrepareStartRegion(CallbackInfo ci){
        this.startPreGen();
    }
    public void startPreGen(){
        ServerWorld serverWorld = this.worlds.get(PreGenerator.PRE_GEN);
        serverWorld.getChunkManager().getLightingProvider().setTaskBatchSize(500);
//        ChunkPos spawnPos = new ChunkPos(0,0);
        PreGenerator.worldGenerationProgressListener.start(this.spawnPos);
        serverWorld.getChunkManager().addTicket(ChunkTicketType.PLAYER,spawnPos,11, spawnPos);
        new Thread(()->{
            while (serverWorld.getChunkManager().getTotalChunksLoadedCount() < 441);
            ((MinecraftServer)(Object)this).submit(()-> {
                this.worlds.remove(PreGenerator.PRE_GEN);
                try {
                    serverWorld.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PreGenerator.worldGenerationProgressListener.stop();
                SeedQueue.preGenerator.next(serverWorld,(MinecraftServer)(Object) this);
            });
        }).start();
    }
    private static BlockPos getSpawnPos(ServerWorld serverWorld) {
        BlockPos spawnPos;
        ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
        BiomeSource biomeSource = chunkGenerator.getBiomeSource();
        List<Biome> list = biomeSource.getSpawnBiomes();
        Random random = new Random(serverWorld.getSeed());
        BlockPos blockPos = biomeSource.locateBiome(0, serverWorld.getSeaLevel(), 0, 256, list, random);
        ChunkPos chunkPos = blockPos == null ? new ChunkPos(0, 0) : new ChunkPos(blockPos);

        boolean bl4 = false;
        Iterator var12 = BlockTags.VALID_SPAWN.values().iterator();

        while (var12.hasNext()) {
            Block block = (Block) var12.next();
            if (biomeSource.getTopMaterials().contains(block.getDefaultState())) {
                bl4 = true;
                break;
            }
        }

        spawnPos = chunkPos.getCenterBlockPos().add(8, chunkGenerator.getSpawnHeight(), 8);
        int i = 0;
        int j = 0;
        int k = 0;
        int l = -1;

        for (int n = 0; n < 1024; ++n) {
            if (i > -16 && i <= 16 && j > -16 && j <= 16) {
                BlockPos blockPos2 = SpawnLocating.findServerSpawnPoint(serverWorld, new ChunkPos(chunkPos.x + i, chunkPos.z + j), bl4);
                if (blockPos2 != null) {
                    spawnPos = blockPos2;
                    break;
                }
            }

            if (i == j || i < 0 && i == -j || i > 0 && i == 1 - j) {
                int o = k;
                k = -l;
                l = o;
            }

            i += k;
            j += l;
        }

        return spawnPos;
    }

    @Override
    public void newPreGen() throws IOException {
        GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
        boolean bl = generatorOptions.isDebugWorld();
        long l = (new Random()).nextLong();
        long m = BiomeAccess.hashSeed(l);
        ChunkGenerator chunkGenerator = GeneratorOptions.createOverworldGenerator(l);
        RegistryKey<World> registryKey = RegistryKey.of(Registry.DIMENSION,new Identifier(String.format("%d", Instant.now().toEpochMilli())));
//        SimpleRegistry<DimensionOptions> dimensionsRegistry = this.getSaveProperties().getGeneratorOptions().getDimensionMap();
//        DimensionOptions dimensionOptions = new DimensionOptions(DimensionType::getOverworldDimensionType,chunkGenerator);
//        dimensionsRegistry.add(RegistryKey.of(Registry.DIMENSION_OPTIONS, registryKey.getValue()), dimensionOptions);
        List<Spawner> list = ImmutableList.of(new PhantomSpawner(), new PillagerSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(this.saveProperties.getMainWorldProperties()));
        //TODO : make new properties
        ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
        RegistryKey<DimensionType> registryKey2 = this.dimensionTracker.getDimensionTypeRegistry().getKey(DimensionType.getOverworldDimensionType()).orElseThrow(() -> {
            return new IllegalStateException("Unregistered dimension type");
        });
        WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(11);
        worldGenerationProgressTracker.start();
        LevelStorage.Session session = MinecraftClient.getInstance().getLevelStorage().createSession(String.format("New World %d", Instant.now().toEpochMilli()));
        PreGenerator.worldGenerationProgressListener = worldGenerationProgressTracker;
        ServerWorld serverWorld = new ServerWorld((MinecraftServer)(Object) this, this.workerExecutor, session, serverWorldProperties, registryKey, registryKey2, DimensionType.getOverworldDimensionType(), worldGenerationProgressTracker, chunkGenerator, bl, m, list, true);
        this.spawnPos = new ChunkPos(getSpawnPos(serverWorld));
        PreGenerator.PRE_GEN = registryKey;
        this.worlds.put(PreGenerator.PRE_GEN,serverWorld);
    }
}
