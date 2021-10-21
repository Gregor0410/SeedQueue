package gregor0410.seedqueue.mixin;

import gregor0410.seedqueue.SeedQueue;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GeneratorOptions.class)
public class GeneratorOptionsMixin {
    @Inject(method = "Lnet/minecraft/world/gen/GeneratorOptions;getDefaultOptions()Lnet/minecraft/world/gen/GeneratorOptions;", at=@At("HEAD"), cancellable = true)
    private static void onGetDefaultGeneratorOptions(CallbackInfoReturnable cir){
        if(SeedQueue.preGenerator.getSize()>0){
            cir.setReturnValue(SeedQueue.preGenerator.getHead().generatorOptions);
        }
    }
}
