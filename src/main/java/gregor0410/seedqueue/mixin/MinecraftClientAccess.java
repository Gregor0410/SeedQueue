package gregor0410.seedqueue.mixin;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.Proxy;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccess {
    @Accessor("netProxy")
    Proxy getNetProxy();
    @Accessor("sessionService")
    MinecraftSessionService getSessionService();
}
