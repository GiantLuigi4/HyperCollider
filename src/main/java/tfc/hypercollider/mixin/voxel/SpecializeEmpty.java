package tfc.hypercollider.mixin.voxel;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;
import tfc.hypercollider.util.voxel.EmptyShape;

@Mixin(Shapes.class)
public class SpecializeEmpty {
    @Shadow
    @Final
    @Mutable
    private static VoxelShape EMPTY;

    static {
        EMPTY = EmptyShape.create();
    }
}
