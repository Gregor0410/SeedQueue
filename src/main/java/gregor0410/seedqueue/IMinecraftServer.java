package gregor0410.seedqueue;

import java.io.IOException;

public interface IMinecraftServer {
    WorldInfo newPreGen() throws IOException;
    void startPreGen(WorldInfo worldInfo);
}
