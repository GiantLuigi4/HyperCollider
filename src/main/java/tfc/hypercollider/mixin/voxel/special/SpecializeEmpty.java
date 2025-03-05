package tfc.hypercollider.mixin.voxel.special;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.hypercollider.util.voxel.EmptyShape;

@Mixin(value = Shapes.class, priority = 1001)
public class SpecializeEmpty {
    @Shadow
    @Final
    @Mutable
    private static VoxelShape EMPTY;

    static {
        EMPTY = EmptyShape.create();
    }
}
