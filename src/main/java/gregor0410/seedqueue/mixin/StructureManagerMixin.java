package gregor0410.seedqueue.mixin;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;

@Mixin(StructureManager.class)
public class StructureManagerMixin {
    @Shadow public Map<Identifier, Structure> structures;

    @Inject(method = "<init>",at=@At("TAIL"))
    private void onInit(CallbackInfo ci){
        this.structures = Collections.synchronizedMap(this.structures);
    }
}
