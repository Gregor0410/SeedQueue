package gregor0410.seedqueue.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccess {
    @Invoker("setupSpawn")
    public static void invokeSetupSpawn(ServerWorld serverWorld, ServerWorldProperties serverWorldProperties, boolean bl, boolean bl2, boolean bl3){
        throw new AssertionError();
    }
    @Accessor("session")
    LevelStorage.Session getSession();
}
