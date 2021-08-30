package gregor0410.moonlight.mixin;

import gregor0410.moonlight.Moonlight;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GeneratorOptions.class)
public class GeneratorOptionsMixin {
    @Inject(method="Lnet/minecraft/world/gen/GeneratorOptions;getDefaultOptions()Lnet/minecraft/world/gen/GeneratorOptions;",at=@At("HEAD"), cancellable = true)
    private static void getDefaultOptions(CallbackInfoReturnable info){
            Moonlight.preGenerator.nextSeed();
            long l = Moonlight.preGenerator.current.seed;
            Moonlight.log(Level.INFO,String.format("Injected seed"));
            SurfaceChunkGenerator surfaceChunkGenerator = GeneratorOptions.createOverworldGenerator(l);
            Moonlight.preGenerator.current.setChunkGenerator(surfaceChunkGenerator);
            info.setReturnValue(new GeneratorOptions(l, true, false, GeneratorOptions.method_28608(DimensionType.method_28517(l), surfaceChunkGenerator)));
        }
    }
