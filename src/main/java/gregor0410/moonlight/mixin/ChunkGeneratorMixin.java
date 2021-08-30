package gregor0410.moonlight.mixin;

import gregor0410.moonlight.Moonlight;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Shadow @Final private List<ChunkPos> field_24749;

    @Inject(at = @At("HEAD"),method ="method_28509", cancellable = true)
    private void genStrongholds(CallbackInfo ci){
        if(this.equals(Moonlight.preGenerator.current.getChunkGenerator())){
            ci.cancel();
        }
    }
}
