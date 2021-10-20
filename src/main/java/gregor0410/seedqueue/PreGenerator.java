package gregor0410.seedqueue;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import gregor0410.seedqueue.mixin.MinecraftClientAccess;
import gregor0410.seedqueue.mixin.MinecraftServerAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelStorage;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class PreGenerator {
    private List<IntegratedServer> queue = Lists.newArrayList();
    private int max;
    public static RegistryKey<World> PRE_GEN = RegistryKey.of(Registry.DIMENSION, new Identifier("pre_gen"));
    public static WorldGenerationProgressListener worldGenerationProgressListener;
    public PreGenerator(int max){
        this.max = max;
    }
    public void next(MinecraftServer server){
        if(queue.size()<max) {
            MinecraftClient mc = MinecraftClient.getInstance();
            AtomicReference<IntegratedServer> atomicReference = new AtomicReference<>();
            Thread serverThread = new Thread(()->{
                IntegratedServer server2 = atomicReference.get();
                server2.setupServer();
                server2.save(false,false,false);
                server2.shutdown();
            },"pregen server thread");
            RegistryTracker.Modifiable registryTracker = RegistryTracker.create();
            LevelStorage.Session session = null;
            try {
                session = mc.getLevelStorage().createSession(String.format("New World %d", Instant.now().toEpochMilli()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            MinecraftClient.IntegratedResourceManager integratedResourceManager2 = null;
            try {
                integratedResourceManager2 = mc.method_29604(registryTracker, MinecraftClient::method_29598, MinecraftClient::createSaveProperties, false, ((MinecraftServerAccess)server).getSession());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            SaveProperties saveProperties = integratedResourceManager2.getSaveProperties();
            session.method_27425(registryTracker, saveProperties);
            integratedResourceManager2.getServerResourceManager().loadRegistryTags();
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(((MinecraftClientAccess)mc).getNetProxy(), UUID.randomUUID().toString());
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(mc.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            IntegratedServer integratedServer = new IntegratedServer(serverThread, mc, registryTracker, session, integratedResourceManager2.getResourcePackManager(), integratedResourceManager2.getServerResourceManager(), saveProperties, ((MinecraftClientAccess)mc).getSessionService(), gameProfileRepository, userCache, (i) -> {
                WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(i);
                worldGenerationProgressTracker.start();
                return worldGenerationProgressTracker;
            });
            atomicReference.set(integratedServer);
            serverThread.start();
            queue.add(integratedServer);
        }
    }
}
