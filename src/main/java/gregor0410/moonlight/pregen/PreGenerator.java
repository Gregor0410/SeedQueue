package gregor0410.moonlight.pregen;

import gregor0410.moonlight.Moonlight;
import gregor0410.moonlight.seed.SeedInfo;
import org.apache.logging.log4j.Level;

import java.util.LinkedList;
import java.util.Queue;

public class PreGenerator implements Runnable {
    private Queue<SeedInfo> seedQueue;
    public SeedInfo current;
    public Thread thread;
    private int maxQueueSize;

    public PreGenerator(int max){
        seedQueue = new LinkedList<SeedInfo>();
        maxQueueSize = max;
        thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        while(true) {
            if (seedQueue.size() < maxQueueSize) {
                SeedInfo seedInfo = new SeedInfo();
                seedQueue.add(seedInfo);
                Moonlight.log(Level.INFO,String.format("Queue size %d",seedQueue.size()));
                Thread strongholdGenThread = new Thread(new StrongholdPreGen(seedInfo));
                strongholdGenThread.start();
            }else{
                Thread.yield(); //yield the thread to prevent the evil java runtime from thinking it's not responding
            }
        }
    }
    public void nextSeed(){
        while (seedQueue.size()<1); //wait for next seed if queue is empty
        current = seedQueue.remove();
    }
}
