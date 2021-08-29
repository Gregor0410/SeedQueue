package gregor0410.moonlight.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Accessor
    public List<ChunkPos> getField_24749();
}
