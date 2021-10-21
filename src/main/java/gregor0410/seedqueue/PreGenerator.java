package gregor0410.seedqueue;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gregor0410.seedqueue.mixin.MinecraftClientAccess;
import gregor0410.seedqueue.mixin.MinecraftServerAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class PreGenerator {
    private List<WorldInfo> queue = Lists.newArrayList();
    private int max;
    public static RegistryKey<World> PRE_GEN = RegistryKey.of(Registry.DIMENSION, new Identifier("pre_gen"));
    public static WorldGenerationProgressListener worldGenerationProgressListener;
    public PreGenerator(int max){
        this.max = max;
    }
    public void genNext(MinecraftServer server){
        if(queue.size()<max) {
            MinecraftClient mc = MinecraftClient.getInstance();
            AtomicReference<IntegratedServer> atomicReference = new AtomicReference<>();
            Thread serverThread = new Thread("pregen server thread"){
                public void run() {
                    IntegratedServer server2 = atomicReference.get();
                    server2.setupServer();
                    server2.save(false, false, false);
                }
            };
            RegistryTracker.Modifiable registryTracker = RegistryTracker.create();
            GeneratorOptions generatorOptions = GeneratorOptions.getDefaultOptions();
            String levelName = String.format("New World %d", Instant.now().toEpochMilli());
            LevelInfo levelInfo = new LevelInfo(levelName.trim(), server.getDefaultGameMode(), server.isHardcore(),server.getSaveProperties().getDifficulty(),false,server.getGameRules().copy(), DataPackSettings.SAFE_MODE);
            LevelStorage.Session session2 = null;
            try {
                session2 = mc.getLevelStorage().createSession(levelName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            MinecraftClient.IntegratedResourceManager integratedResourceManager2 = null;
            try {
                integratedResourceManager2 = mc.method_29604(registryTracker, (x)->levelInfo.method_29558(),
                (session, modifiable2, resourceManager, dataPackSettings) -> {
                    RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, resourceManager, registryTracker);
                    DataResult<SimpleRegistry<DimensionOptions>> dataResult = registryOps.loadToRegistry(generatorOptions.getDimensionMap(), Registry.DIMENSION_OPTIONS, DimensionOptions.CODEC);
                    SimpleRegistry<DimensionOptions> simpleRegistry = (SimpleRegistry<DimensionOptions>)dataResult.result().orElse(generatorOptions.getDimensionMap());
                    return new LevelProperties(levelInfo, generatorOptions.method_29573(simpleRegistry), dataResult.lifecycle());
                },
            false, session2);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            SaveProperties saveProperties = integratedResourceManager2.getSaveProperties();
            session2.method_27425(registryTracker, saveProperties);
            integratedResourceManager2.getServerResourceManager().loadRegistryTags();
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(((MinecraftClientAccess)mc).getNetProxy(), UUID.randomUUID().toString());
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(mc.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            IntegratedServer integratedServer = new IntegratedServer(serverThread, mc, registryTracker, session2, integratedResourceManager2.getResourcePackManager(), integratedResourceManager2.getServerResourceManager(), saveProperties, ((MinecraftClientAccess)mc).getSessionService(), gameProfileRepository, userCache, (i) -> {
                WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(i);
                worldGenerationProgressTracker.start();
                return worldGenerationProgressTracker;
            });
            atomicReference.set(integratedServer);
            serverThread.start();
            queue.add(new WorldInfo(generatorOptions, Optional.of(integratedServer),session2));
        }
    }
    public WorldInfo getHead(){
        return queue.get(0);
    }
    public WorldInfo removeHead(){
        return queue.remove(0);
    }
    public int getSize(){
        return this.queue.size();
    }
}
