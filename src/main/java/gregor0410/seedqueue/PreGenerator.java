package gregor0410.seedqueue;

import com.google.common.collect.Lists;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.List;

public class PreGenerator {
    private List<ServerWorld> queue = Lists.newArrayList();
    private int max;
    public static RegistryKey<World> PRE_GEN = RegistryKey.of(Registry.DIMENSION, new Identifier("pre_gen"));
    public static WorldGenerationProgressListener worldGenerationProgressListener;
    public PreGenerator(int max){
        this.max = max;
    }
    public void next(ServerWorld serverWorld, MinecraftServer server){
        queue.add(serverWorld);
        if(queue.size()<max) {
            try {
                ((IMinecraftServer) server).newPreGen();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ((IMinecraftServer) server).startPreGen();
        }
    }
}
