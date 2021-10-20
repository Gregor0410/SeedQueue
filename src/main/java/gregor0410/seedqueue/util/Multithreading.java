package gregor0410.seedqueue.util;

import net.minecraft.server.MinecraftServer;

import java.util.concurrent.atomic.AtomicBoolean;

public class Multithreading {
    public static void awaitServerTask(MinecraftServer server, Runnable task){
        AtomicBoolean done = new AtomicBoolean(false);
        server.submit(()->{
            task.run();
            done.set(true);
        });
        while(!done.get()){
            Thread.yield();
        }
    }
}
