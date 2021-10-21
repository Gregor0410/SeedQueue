package gregor0410.seedqueue.mixin;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.net.Proxy;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccess {
    @Accessor("netProxy")
    Proxy getNetProxy();
    @Accessor("sessionService")
    MinecraftSessionService getSessionService();
    @Accessor("server")
    void setServer(IntegratedServer server);
    @Accessor("isIntegratedServerRunning")
    void setIsIntegratedServerRunning(boolean bl);
    @Accessor("connection")
    void setConnection(ClientConnection connection);
    @Invoker("disconnect")
    void invokeDisconnect();
}
