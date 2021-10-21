package gregor0410.seedqueue.mixin;

import gregor0410.seedqueue.SeedQueue;
import gregor0410.seedqueue.WorldInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @Shadow @Final public MoreOptionsDialog moreOptionsDialog;

    @Inject(method="Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;createLevel()V",at=@At("HEAD"))
    private void onCreateLevel(CallbackInfo ci){
        if(SeedQueue.preGenerator.getSize()>0){
            WorldInfo worldInfo = SeedQueue.preGenerator.getHead();
            if(worldInfo.generatorOptions.getSeed()==this.moreOptionsDialog.getGeneratorOptions(false).getSeed()){
                //Seed hasn't been changed so we can connect to the IntegratedServer
                MinecraftClient mc = MinecraftClient.getInstance();
                ((MinecraftClientAccess)mc).invokeDisconnect();
                ((MinecraftClientAccess)mc).setServer(MinecraftServer.startServer((serverThread)->{
                    ((MinecraftServerAccess)worldInfo.server).setServerThread(serverThread);
                    return worldInfo.server;
                }));
                ((MinecraftClientAccess)mc).setIsIntegratedServerRunning(true);
                SocketAddress socketAddress = worldInfo.server.getNetworkIo().bindLocal();
                ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
                clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, mc, (Screen)null, (text) -> {
                }));
                clientConnection.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
                clientConnection.send(new LoginHelloC2SPacket(mc.getSession().getProfile()));
                ((MinecraftClientAccess) mc).setConnection(clientConnection);
            }
        }
    }
}
