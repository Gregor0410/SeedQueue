package gregor0410.seedqueue.mixin;

import com.google.common.collect.Lists;
import net.minecraft.util.collection.WeightedList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(WeightedList.class)
public class WeightedListMixin<U> {
    @Mutable
    @Shadow @Final protected List<WeightedList.Entry<U>> entries;

    @Redirect(method ="Lnet/minecraft/util/collection/WeightedList;<init>(Ljava/util/List;)V" ,at=@At(value="FIELD",target = "Lnet/minecraft/util/collection/WeightedList;entries:Ljava/util/List;",opcode = Opcodes.PUTFIELD))
    private void onSetEntries(WeightedList<U> weightedList, List<WeightedList.Entry<U>> list){
        this.entries = Lists.newCopyOnWriteArrayList(list);
    }
}
