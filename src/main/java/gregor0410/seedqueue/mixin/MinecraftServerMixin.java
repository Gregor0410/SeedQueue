package gregor0410.seedqueue.mixin;


import gregor0410.seedqueue.IMinecraftServer;
import gregor0410.seedqueue.SeedQueue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
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


    @Inject(method="<init>",at=@At("TAIL"))
    private void onInit(CallbackInfo ci){
        this.worlds = new ConcurrentHashMap<>(this.worlds);
    }

    @Inject(method = "Lnet/minecraft/server/MinecraftServer;prepareStartRegion(Lnet/minecraft/server/WorldGenerationProgressListener;)V",at=@At("TAIL"))
    private void onPrepareStartRegion(CallbackInfo ci){
        SeedQueue.preGenerator.genNext((MinecraftServer) (Object)this);
    }
}
