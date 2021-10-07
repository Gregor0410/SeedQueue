package gregor0410.seedqueue;

import net.fabricmc.api.ModInitializer;

public class SeedQueue implements ModInitializer {
    public static PreGenerator preGenerator;
    @Override
    public void onInitialize() {
        preGenerator = new PreGenerator(5);
    }
}
